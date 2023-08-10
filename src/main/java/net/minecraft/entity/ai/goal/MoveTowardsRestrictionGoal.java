package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.vector.Vector3d;

public class MoveTowardsRestrictionGoal extends Goal
{
    private final CreatureEntity creature;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    private final double movementSpeed;

    public MoveTowardsRestrictionGoal(CreatureEntity creatureIn, double speedIn)
    {
        this.creature = creatureIn;
        this.movementSpeed = speedIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute()
    {
        if (this.creature.isWithinHomeDistanceCurrentPosition())
        {
            return false;
        }
        else
        {
            Vector3d vector3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.creature, 16, 7, Vector3d.copyCenteredHorizontally(this.creature.getHomePosition()));

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
        return !this.creature.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.creature.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.movementSpeed);
    }
}
