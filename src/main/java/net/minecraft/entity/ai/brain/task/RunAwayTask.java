package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class RunAwayTask<T> extends Task<CreatureEntity>
{
    private final MemoryModuleType<T> field_233957_b_;
    private final float field_233958_c_;
    private final int field_233959_d_;
    private final Function<T, Vector3d> field_233960_e_;

    public RunAwayTask(MemoryModuleType<T> p_i231533_1_, float p_i231533_2_, int p_i231533_3_, boolean p_i231533_4_, Function<T, Vector3d> p_i231533_5_)
    {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, p_i231533_4_ ? MemoryModuleStatus.REGISTERED : MemoryModuleStatus.VALUE_ABSENT, p_i231533_1_, MemoryModuleStatus.VALUE_PRESENT));
        this.field_233957_b_ = p_i231533_1_;
        this.field_233958_c_ = p_i231533_2_;
        this.field_233959_d_ = p_i231533_3_;
        this.field_233960_e_ = p_i231533_5_;
    }

    public static RunAwayTask<BlockPos> func_233963_a_(MemoryModuleType<BlockPos> p_233963_0_, float p_233963_1_, int p_233963_2_, boolean p_233963_3_)
    {
        return new RunAwayTask<>(p_233963_0_, p_233963_1_, p_233963_2_, p_233963_3_, Vector3d::copyCenteredHorizontally);
    }

    public static RunAwayTask <? extends Entity > func_233965_b_(MemoryModuleType <? extends Entity > p_233965_0_, float p_233965_1_, int p_233965_2_, boolean p_233965_3_)
    {
        return new RunAwayTask<>(p_233965_0_, p_233965_1_, p_233965_2_, p_233965_3_, Entity::getPositionVec);
    }

    protected boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner)
    {
        return this.func_233964_b_(owner) ? false : owner.getPositionVec().isWithinDistanceOf(this.func_233961_a_(owner), (double)this.field_233959_d_);
    }

    private Vector3d func_233961_a_(CreatureEntity p_233961_1_)
    {
        return this.field_233960_e_.apply(p_233961_1_.getBrain().getMemory(this.field_233957_b_).get());
    }

    private boolean func_233964_b_(CreatureEntity p_233964_1_)
    {
        if (!p_233964_1_.getBrain().hasMemory(MemoryModuleType.WALK_TARGET))
        {
            return false;
        }
        else
        {
            WalkTarget walktarget = p_233964_1_.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get();

            if (walktarget.getSpeed() != this.field_233958_c_)
            {
                return false;
            }
            else
            {
                Vector3d vector3d = walktarget.getTarget().getPos().subtract(p_233964_1_.getPositionVec());
                Vector3d vector3d1 = this.func_233961_a_(p_233964_1_).subtract(p_233964_1_.getPositionVec());
                return vector3d.dotProduct(vector3d1) < 0.0D;
            }
        }
    }

    protected void startExecuting(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn)
    {
        func_233962_a_(entityIn, this.func_233961_a_(entityIn), this.field_233958_c_);
    }

    private static void func_233962_a_(CreatureEntity p_233962_0_, Vector3d p_233962_1_, float p_233962_2_)
    {
        for (int i = 0; i < 10; ++i)
        {
            Vector3d vector3d = RandomPositionGenerator.func_223548_b(p_233962_0_, 16, 7, p_233962_1_);

            if (vector3d != null)
            {
                p_233962_0_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vector3d, p_233962_2_, 0));
                return;
            }
        }
    }
}
