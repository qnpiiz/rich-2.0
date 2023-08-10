package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class FollowBoatGoal extends Goal
{
    private int field_205143_a;
    private final CreatureEntity swimmer;
    private PlayerEntity player;
    private BoatGoals field_205146_d;

    public FollowBoatGoal(CreatureEntity swimmer)
    {
        this.swimmer = swimmer;
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute()
    {
        List<BoatEntity> list = this.swimmer.world.getEntitiesWithinAABB(BoatEntity.class, this.swimmer.getBoundingBox().grow(5.0D));
        boolean flag = false;

        for (BoatEntity boatentity : list)
        {
            Entity entity = boatentity.getControllingPassenger();

            if (entity instanceof PlayerEntity && (MathHelper.abs(((PlayerEntity)entity).moveStrafing) > 0.0F || MathHelper.abs(((PlayerEntity)entity).moveForward) > 0.0F))
            {
                flag = true;
                break;
            }
        }

        return this.player != null && (MathHelper.abs(this.player.moveStrafing) > 0.0F || MathHelper.abs(this.player.moveForward) > 0.0F) || flag;
    }

    public boolean isPreemptible()
    {
        return true;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return this.player != null && this.player.isPassenger() && (MathHelper.abs(this.player.moveStrafing) > 0.0F || MathHelper.abs(this.player.moveForward) > 0.0F);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        for (BoatEntity boatentity : this.swimmer.world.getEntitiesWithinAABB(BoatEntity.class, this.swimmer.getBoundingBox().grow(5.0D)))
        {
            if (boatentity.getControllingPassenger() != null && boatentity.getControllingPassenger() instanceof PlayerEntity)
            {
                this.player = (PlayerEntity)boatentity.getControllingPassenger();
                break;
            }
        }

        this.field_205143_a = 0;
        this.field_205146_d = BoatGoals.GO_TO_BOAT;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.player = null;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick()
    {
        boolean flag = MathHelper.abs(this.player.moveStrafing) > 0.0F || MathHelper.abs(this.player.moveForward) > 0.0F;
        float f = this.field_205146_d == BoatGoals.GO_IN_BOAT_DIRECTION ? (flag ? 0.01F : 0.0F) : 0.015F;
        this.swimmer.moveRelative(f, new Vector3d((double)this.swimmer.moveStrafing, (double)this.swimmer.moveVertical, (double)this.swimmer.moveForward));
        this.swimmer.move(MoverType.SELF, this.swimmer.getMotion());

        if (--this.field_205143_a <= 0)
        {
            this.field_205143_a = 10;

            if (this.field_205146_d == BoatGoals.GO_TO_BOAT)
            {
                BlockPos blockpos = this.player.getPosition().offset(this.player.getHorizontalFacing().getOpposite());
                blockpos = blockpos.add(0, -1, 0);
                this.swimmer.getNavigator().tryMoveToXYZ((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D);

                if (this.swimmer.getDistance(this.player) < 4.0F)
                {
                    this.field_205143_a = 0;
                    this.field_205146_d = BoatGoals.GO_IN_BOAT_DIRECTION;
                }
            }
            else if (this.field_205146_d == BoatGoals.GO_IN_BOAT_DIRECTION)
            {
                Direction direction = this.player.getAdjustedHorizontalFacing();
                BlockPos blockpos1 = this.player.getPosition().offset(direction, 10);
                this.swimmer.getNavigator().tryMoveToXYZ((double)blockpos1.getX(), (double)(blockpos1.getY() - 1), (double)blockpos1.getZ(), 1.0D);

                if (this.swimmer.getDistance(this.player) > 12.0F)
                {
                    this.field_205143_a = 0;
                    this.field_205146_d = BoatGoals.GO_TO_BOAT;
                }
            }
        }
    }
}
