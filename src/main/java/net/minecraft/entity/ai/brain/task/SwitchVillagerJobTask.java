package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class SwitchVillagerJobTask extends Task<VillagerEntity>
{
    final VillagerProfession field_233929_b_;

    public SwitchVillagerJobTask(VillagerProfession p_i231525_1_)
    {
        super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.MOBS, MemoryModuleStatus.VALUE_PRESENT));
        this.field_233929_b_ = p_i231525_1_;
    }

    protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn)
    {
        GlobalPos globalpos = entityIn.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
        worldIn.getPointOfInterestManager().getType(globalpos.getPos()).ifPresent((p_233933_3_) ->
        {
            BrainUtil.getNearbyVillagers(entityIn, (p_233935_3_) -> {
                return this.func_233934_a_(globalpos, p_233933_3_, p_233935_3_);
            }).reduce(entityIn, SwitchVillagerJobTask::func_233932_a_);
        });
    }

    private static VillagerEntity func_233932_a_(VillagerEntity p_233932_0_, VillagerEntity p_233932_1_)
    {
        VillagerEntity villagerentity;
        VillagerEntity villagerentity1;

        if (p_233932_0_.getXp() > p_233932_1_.getXp())
        {
            villagerentity = p_233932_0_;
            villagerentity1 = p_233932_1_;
        }
        else
        {
            villagerentity = p_233932_1_;
            villagerentity1 = p_233932_0_;
        }

        villagerentity1.getBrain().removeMemory(MemoryModuleType.JOB_SITE);
        return villagerentity;
    }

    private boolean func_233934_a_(GlobalPos p_233934_1_, PointOfInterestType p_233934_2_, VillagerEntity p_233934_3_)
    {
        return this.func_233931_a_(p_233934_3_) && p_233934_1_.equals(p_233934_3_.getBrain().getMemory(MemoryModuleType.JOB_SITE).get()) && this.func_233930_a_(p_233934_2_, p_233934_3_.getVillagerData().getProfession());
    }

    private boolean func_233930_a_(PointOfInterestType p_233930_1_, VillagerProfession p_233930_2_)
    {
        return p_233930_2_.getPointOfInterest().getPredicate().test(p_233930_1_);
    }

    private boolean func_233931_a_(VillagerEntity p_233931_1_)
    {
        return p_233931_1_.getBrain().getMemory(MemoryModuleType.JOB_SITE).isPresent();
    }
}
