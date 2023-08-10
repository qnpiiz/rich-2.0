package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public class DummyTask extends Task<LivingEntity>
{
    public DummyTask(int durationMin, int durationMax)
    {
        super(ImmutableMap.of(), durationMin, durationMax);
    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn)
    {
        return true;
    }
}
