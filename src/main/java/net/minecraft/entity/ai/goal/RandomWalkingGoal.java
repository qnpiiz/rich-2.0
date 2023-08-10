package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.vector.Vector3d;

public class RandomWalkingGoal extends Goal
{
    protected final CreatureEntity creature;
    protected double x;
    protected double y;
    protected double z;
    protected final double speed;
    protected int executionChance;
    protected boolean mustUpdate;
    private boolean field_234053_h_;

    public RandomWalkingGoal(CreatureEntity creatureIn, double speedIn)
    {
        this(creatureIn, speedIn, 120);
    }

    public RandomWalkingGoal(CreatureEntity creatureIn, double speedIn, int chance)
    {
        this(creatureIn, speedIn, chance, true);
    }

    public RandomWalkingGoal(CreatureEntity creature, double speed, int chance, boolean p_i231550_5_)
    {
        this.creature = creature;
        this.speed = speed;
        this.executionChance = chance;
        this.field_234053_h_ = p_i231550_5_;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute()
    {
        if (this.creature.isBeingRidden())
        {
            return false;
        }
        else
        {
            if (!this.mustUpdate)
            {
                if (this.field_234053_h_ && this.creature.getIdleTime() >= 100)
                {
                    return false;
                }

                if (this.creature.getRNG().nextInt(this.executionChance) != 0)
                {
                    return false;
                }
            }

            Vector3d vector3d = this.getPosition();

            if (vector3d == null)
            {
                return false;
            }
            else
            {
                this.x = vector3d.x;
                this.y = vector3d.y;
                this.z = vector3d.z;
                this.mustUpdate = false;
                return true;
            }
        }
    }

    @Nullable
    protected Vector3d getPosition()
    {
        return RandomPositionGenerator.findRandomTarget(this.creature, 10, 7);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return !this.creature.getNavigator().noPath() && !this.creature.isBeingRidden();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.creature.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, this.speed);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.creature.getNavigator().clearPath();
        super.resetTask();
    }

    /**
     * Makes task to bypass chance
     */
    public void makeUpdate()
    {
        this.mustUpdate = true;
    }

    /**
     * Changes task random possibility for execution
     */
    public void setExecutionChance(int newchance)
    {
        this.executionChance = newchance;
    }
}
