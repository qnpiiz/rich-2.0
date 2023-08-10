package net.minecraft.entity.ai.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.GroundPathHelper;

public class RestrictSunGoal extends Goal
{
    private final CreatureEntity entity;

    public RestrictSunGoal(CreatureEntity creature)
    {
        this.entity = creature;
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute()
    {
        return this.entity.world.isDaytime() && this.entity.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty() && GroundPathHelper.isGroundNavigator(this.entity);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        ((GroundPathNavigator)this.entity.getNavigator()).setAvoidSun(true);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        if (GroundPathHelper.isGroundNavigator(this.entity))
        {
            ((GroundPathNavigator)this.entity.getNavigator()).setAvoidSun(false);
        }
    }
}
