package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class FindWalkTargetAfterRaidVictoryTask extends FindWalkTargetTask
{
    public FindWalkTargetAfterRaidVictoryTask(float speed)
    {
        super(speed);
    }

    protected boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner)
    {
        Raid raid = worldIn.findRaid(owner.getPosition());
        return raid != null && raid.isVictory() && super.shouldExecute(worldIn, owner);
    }
}
