package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class GatherPOITask extends Task<CreatureEntity>
{
    private final PointOfInterestType field_220604_a;
    private final MemoryModuleType<GlobalPos> field_220605_b;
    private final boolean field_220606_c;
    private final Optional<Byte> field_242290_e;
    private long field_220607_d;
    private final Long2ObjectMap<GatherPOITask.RetryMarker> field_223013_e = new Long2ObjectOpenHashMap<>();

    public GatherPOITask(PointOfInterestType p_i241906_1_, MemoryModuleType<GlobalPos> p_i241906_2_, MemoryModuleType<GlobalPos> p_i241906_3_, boolean p_i241906_4_, Optional<Byte> p_i241906_5_)
    {
        super(func_233841_a_(p_i241906_2_, p_i241906_3_));
        this.field_220604_a = p_i241906_1_;
        this.field_220605_b = p_i241906_3_;
        this.field_220606_c = p_i241906_4_;
        this.field_242290_e = p_i241906_5_;
    }

    public GatherPOITask(PointOfInterestType p_i241907_1_, MemoryModuleType<GlobalPos> p_i241907_2_, boolean p_i241907_3_, Optional<Byte> p_i241907_4_)
    {
        this(p_i241907_1_, p_i241907_2_, p_i241907_2_, p_i241907_3_, p_i241907_4_);
    }

    private static ImmutableMap < MemoryModuleType<?>, MemoryModuleStatus > func_233841_a_(MemoryModuleType<GlobalPos> p_233841_0_, MemoryModuleType<GlobalPos> p_233841_1_)
    {
        Builder < MemoryModuleType<?>, MemoryModuleStatus > builder = ImmutableMap.builder();
        builder.put(p_233841_0_, MemoryModuleStatus.VALUE_ABSENT);

        if (p_233841_1_ != p_233841_0_)
        {
            builder.put(p_233841_1_, MemoryModuleStatus.VALUE_ABSENT);
        }

        return builder.build();
    }

    protected boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner)
    {
        if (this.field_220606_c && owner.isChild())
        {
            return false;
        }
        else if (this.field_220607_d == 0L)
        {
            this.field_220607_d = owner.world.getGameTime() + (long)worldIn.rand.nextInt(20);
            return false;
        }
        else
        {
            return worldIn.getGameTime() >= this.field_220607_d;
        }
    }

    protected void startExecuting(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn)
    {
        this.field_220607_d = gameTimeIn + 20L + (long)worldIn.getRandom().nextInt(20);
        PointOfInterestManager pointofinterestmanager = worldIn.getPointOfInterestManager();
        this.field_223013_e.long2ObjectEntrySet().removeIf((p_241362_2_) ->
        {
            return !p_241362_2_.getValue().func_241371_b_(gameTimeIn);
        });
        Predicate<BlockPos> predicate = (p_220603_3_) ->
        {
            GatherPOITask.RetryMarker gatherpoitask$retrymarker = this.field_223013_e.get(p_220603_3_.toLong());

            if (gatherpoitask$retrymarker == null)
            {
                return true;
            }
            else if (!gatherpoitask$retrymarker.func_241372_c_(gameTimeIn))
            {
                return false;
            }
            else {
                gatherpoitask$retrymarker.func_241370_a_(gameTimeIn);
                return true;
            }
        };
        Set<BlockPos> set = pointofinterestmanager.func_242324_b(this.field_220604_a.getPredicate(), predicate, entityIn.getPosition(), 48, PointOfInterestManager.Status.HAS_SPACE).limit(5L).collect(Collectors.toSet());
        Path path = entityIn.getNavigator().pathfind(set, this.field_220604_a.getValidRange());

        if (path != null && path.reachesTarget())
        {
            BlockPos blockpos1 = path.getTarget();
            pointofinterestmanager.getType(blockpos1).ifPresent((p_225441_5_) ->
            {
                pointofinterestmanager.take(this.field_220604_a.getPredicate(), (p_225442_1_) -> {
                    return p_225442_1_.equals(blockpos1);
                }, blockpos1, 1);
                entityIn.getBrain().setMemory(this.field_220605_b, GlobalPos.getPosition(worldIn.getDimensionKey(), blockpos1));
                this.field_242290_e.ifPresent((p_242291_2_) -> {
                    worldIn.setEntityState(entityIn, p_242291_2_);
                });
                this.field_223013_e.clear();
                DebugPacketSender.func_218801_c(worldIn, blockpos1);
            });
        }
        else
        {
            for (BlockPos blockpos : set)
            {
                this.field_223013_e.computeIfAbsent(blockpos.toLong(), (p_241363_3_) ->
                {
                    return new GatherPOITask.RetryMarker(entityIn.world.rand, gameTimeIn);
                });
            }
        }
    }

    static class RetryMarker
    {
        private final Random field_241366_a_;
        private long field_241367_b_;
        private long field_241368_c_;
        private int field_241369_d_;

        RetryMarker(Random p_i241233_1_, long p_i241233_2_)
        {
            this.field_241366_a_ = p_i241233_1_;
            this.func_241370_a_(p_i241233_2_);
        }

        public void func_241370_a_(long p_241370_1_)
        {
            this.field_241367_b_ = p_241370_1_;
            int i = this.field_241369_d_ + this.field_241366_a_.nextInt(40) + 40;
            this.field_241369_d_ = Math.min(i, 400);
            this.field_241368_c_ = p_241370_1_ + (long)this.field_241369_d_;
        }

        public boolean func_241371_b_(long p_241371_1_)
        {
            return p_241371_1_ - this.field_241367_b_ < 400L;
        }

        public boolean func_241372_c_(long p_241372_1_)
        {
            return p_241372_1_ >= this.field_241368_c_;
        }

        public String toString()
        {
            return "RetryMarker{, previousAttemptAt=" + this.field_241367_b_ + ", nextScheduledAttemptAt=" + this.field_241368_c_ + ", currentDelay=" + this.field_241369_d_ + '}';
        }
    }
}
