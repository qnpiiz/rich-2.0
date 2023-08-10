package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.vector.Vector3d;

public class MoveTowardsTargetGoal extends Goal
{
    private final CreatureEntity creature;
    private LivingEntity targetEntity;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    private final double speed;
    private final float maxTargetDistance;

    public MoveTowardsTargetGoal(CreatureEntity creature, double speedIn, float targetMaxDistance)
    {
        this.creature = creature;
        this.speed = speedIn;
        this.maxTargetDistance = targetMaxDistance;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute()
    {
        this.targetEntity = this.creature.getAttackTarget();

        if (this.targetEntity == null)
        {
            return false;
        }
        else if (this.targetEntity.getDistanceSq(this.creature) > (double)(this.maxTargetDistance * this.maxTargetDistance))
        {
            return false;
        }
        else
        {
            Vector3d vector3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.creature, 16, 7, this.targetEntity.getPositionVec());

            if (vector3d == null)
            {
                return false;
            }
            else
            {
                this.movePosX = vector3d.x;
                this.movePosY = vector3d.y;
                this.movePosZ = vector3d.z;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return !this.creature.getNavigator().noPath() && this.targetEntity.isAlive() && this.targetEntity.getDistanceSq(this.creature) < (double)(this.maxTargetDistance * this.maxTargetDistance);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.targetEntity = null;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.creature.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
    }
}
