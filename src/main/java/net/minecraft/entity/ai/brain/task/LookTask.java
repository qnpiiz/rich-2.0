package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class LookTask extends Task<MobEntity>
{
    public LookTask(int durationMin, int durationMax)
    {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_PRESENT), durationMin, durationMax);
    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn)
    {
        return entityIn.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).filter((posWrapper) ->
        {
            return posWrapper.isVisibleTo(entityIn);
        }).isPresent();
    }

    protected void resetTask(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn)
    {
        entityIn.getBrain().removeMemory(MemoryModuleType.LOOK_TARGET);
    }

    protected void updateTask(ServerWorld worldIn, MobEntity owner, long gameTime)
    {
        owner.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).ifPresent((posWrapper) ->
        {
            owner.getLookController().setLookPosition(posWrapper.getPos());
        });
    }
}
