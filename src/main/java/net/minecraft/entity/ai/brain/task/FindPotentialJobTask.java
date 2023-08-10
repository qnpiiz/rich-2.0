package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.server.ServerWorld;

public class FindPotentialJobTask extends Task<VillagerEntity>
{
    final float speed;

    public FindPotentialJobTask(float speed)
    {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleStatus.VALUE_PRESENT), 1200);
        this.speed = speed;
    }

    protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner)
    {
        return owner.getBrain().getTemporaryActivity().map((activity) ->
        {
            return activity == Activity.IDLE || activity == Activity.WORK || activity == Activity.PLAY;
        }).orElse(true);
    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn)
    {
        return entityIn.getBrain().hasMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
    }

    protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime)
    {
        BrainUtil.setTargetPosition(owner, owner.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos(), this.speed, 1);
    }

    protected void resetTask(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn)
    {
        Optional<GlobalPos> optional = entityIn.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        optional.ifPresent((globalPos) ->
        {
            BlockPos blockpos = globalPos.getPos();
            ServerWorld serverworld = worldIn.getServer().getWorld(globalPos.getDimension());

            if (serverworld != null)
            {
                PointOfInterestManager pointofinterestmanager = serverworld.getPointOfInterestManager();

                if (pointofinterestmanager.exists(blockpos, (p_241377_0_) ->
            {
                return true;
            }))
                {
                    pointofinterestmanager.release(blockpos);
                }
                DebugPacketSender.func_218801_c(worldIn, blockpos);
            }
        });
        entityIn.getBrain().removeMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}
