package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.vector.Vector3d;

public class LeapAtTargetGoal extends Goal
{
    private final MobEntity leaper;
    private LivingEntity leapTarget;
    private final float leapMotionY;

    public LeapAtTargetGoal(MobEntity leapingEntity, float leapMotionYIn)
    {
        this.leaper = leapingEntity;
        this.leapMotionY = leapMotionYIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute()
    {
        if (this.leaper.isBeingRidden())
        {
            return false;
        }
        else
        {
            this.leapTarget = this.leaper.getAttackTarget();

            if (this.leapTarget == null)
            {
                return false;
            }
            else
            {
                double d0 = this.leaper.getDistanceSq(this.leapTarget);

                if (!(d0 < 4.0D) && !(d0 > 16.0D))
                {
                    if (!this.leaper.isOnGround())
                    {
                        return false;
                    }
                    else
                    {
                        return this.leaper.getRNG().nextInt(5) == 0;
                    }
                }
                else
                {
                    return false;
                }
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return !this.leaper.isOnGround();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        Vector3d vector3d = this.leaper.getMotion();
        Vector3d vector3d1 = new Vector3d(this.leapTarget.getPosX() - this.leaper.getPosX(), 0.0D, this.leapTarget.getPosZ() - this.leaper.getPosZ());

        if (vector3d1.lengthSquared() > 1.0E-7D)
        {
            vector3d1 = vector3d1.normalize().scale(0.4D).add(vector3d.scale(0.2D));
        }

        this.leaper.setMotion(vector3d1.x, (double)this.leapMotionY, vector3d1.z);
    }
}
