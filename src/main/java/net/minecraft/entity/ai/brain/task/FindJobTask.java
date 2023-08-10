package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class FindJobTask extends Task<VillagerEntity>
{
    private final float field_234017_b_;

    public FindJobTask(float p_i231545_1_)
    {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.MOBS, MemoryModuleStatus.VALUE_PRESENT));
        this.field_234017_b_ = p_i231545_1_;
    }

    protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner)
    {
        if (owner.isChild())
        {
            return false;
        }
        else
        {
            return owner.getVillagerData().getProfession() == VillagerProfession.NONE;
        }
    }

    protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn)
    {
        BlockPos blockpos = entityIn.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos();
        Optional<PointOfInterestType> optional = worldIn.getPointOfInterestManager().getType(blockpos);

        if (optional.isPresent())
        {
            BrainUtil.getNearbyVillagers(entityIn, (p_234021_3_) ->
            {
                return this.func_234018_a_(optional.get(), p_234021_3_, blockpos);
            }).findFirst().ifPresent((p_234023_4_) ->
            {
                this.func_234022_a_(worldIn, entityIn, p_234023_4_, blockpos, p_234023_4_.getBrain().getMemory(MemoryModuleType.JOB_SITE).isPresent());
            });
        }
    }

    private boolean func_234018_a_(PointOfInterestType p_234018_1_, VillagerEntity p_234018_2_, BlockPos p_234018_3_)
    {
        boolean flag = p_234018_2_.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();

        if (flag)
        {
            return false;
        }
        else
        {
            Optional<GlobalPos> optional = p_234018_2_.getBrain().getMemory(MemoryModuleType.JOB_SITE);
            VillagerProfession villagerprofession = p_234018_2_.getVillagerData().getProfession();

            if (p_234018_2_.getVillagerData().getProfession() != VillagerProfession.NONE && villagerprofession.getPointOfInterest().getPredicate().test(p_234018_1_))
            {
                return !optional.isPresent() ? this.func_234020_a_(p_234018_2_, p_234018_3_, p_234018_1_) : optional.get().getPos().equals(p_234018_3_);
            }
            else
            {
                return false;
            }
        }
    }

    private void func_234022_a_(ServerWorld p_234022_1_, VillagerEntity p_234022_2_, VillagerEntity p_234022_3_, BlockPos p_234022_4_, boolean p_234022_5_)
    {
        this.func_234019_a_(p_234022_2_);

        if (!p_234022_5_)
        {
            BrainUtil.setTargetPosition(p_234022_3_, p_234022_4_, this.field_234017_b_, 1);
            p_234022_3_.getBrain().setMemory(MemoryModuleType.POTENTIAL_JOB_SITE, GlobalPos.getPosition(p_234022_1_.getDimensionKey(), p_234022_4_));
            DebugPacketSender.func_218801_c(p_234022_1_, p_234022_4_);
        }
    }

    private boolean func_234020_a_(VillagerEntity p_234020_1_, BlockPos p_234020_2_, PointOfInterestType p_234020_3_)
    {
        Path path = p_234020_1_.getNavigator().getPathToPos(p_234020_2_, p_234020_3_.getValidRange());
        return path != null && path.reachesTarget();
    }

    private void func_234019_a_(VillagerEntity p_234019_1_)
    {
        p_234019_1_.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
        p_234019_1_.getBrain().removeMemory(MemoryModuleType.LOOK_TARGET);
        p_234019_1_.getBrain().removeMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}
