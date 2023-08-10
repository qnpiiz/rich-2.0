package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.util.math.BlockPos;

public abstract class InteractDoorGoal extends Goal
{
    protected MobEntity entity;
    protected BlockPos doorPosition = BlockPos.ZERO;
    protected boolean doorInteract;
    private boolean hasStoppedDoorInteraction;
    private float entityPositionX;
    private float entityPositionZ;

    public InteractDoorGoal(MobEntity entityIn)
    {
        this.entity = entityIn;

        if (!GroundPathHelper.isGroundNavigator(entityIn))
        {
            throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
        }
    }

    protected boolean canDestroy()
    {
        if (!this.doorInteract)
        {
            return false;
        }
        else
        {
            BlockState blockstate = this.entity.world.getBlockState(this.doorPosition);

            if (!(blockstate.getBlock() instanceof DoorBlock))
            {
                this.doorInteract = false;
                return false;
            }
            else
            {
                return blockstate.get(DoorBlock.OPEN);
            }
        }
    }

    protected void toggleDoor(boolean open)
    {
        if (this.doorInteract)
        {
            BlockState blockstate = this.entity.world.getBlockState(this.doorPosition);

            if (blockstate.getBlock() instanceof DoorBlock)
            {
                ((DoorBlock)blockstate.getBlock()).openDoor(this.entity.world, blockstate, this.doorPosition, open);
            }
        }
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute()
    {
        if (!GroundPathHelper.isGroundNavigator(this.entity))
        {
            return false;
        }
        else if (!this.entity.collidedHorizontally)
        {
            return false;
        }
        else
        {
            GroundPathNavigator groundpathnavigator = (GroundPathNavigator)this.entity.getNavigator();
            Path path = groundpathnavigator.getPath();

            if (path != null && !path.isFinished() && groundpathnavigator.getEnterDoors())
            {
                for (int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i)
                {
                    PathPoint pathpoint = path.getPathPointFromIndex(i);
                    this.doorPosition = new BlockPos(pathpoint.x, pathpoint.y + 1, pathpoint.z);

                    if (!(this.entity.getDistanceSq((double)this.doorPosition.getX(), this.entity.getPosY(), (double)this.doorPosition.getZ()) > 2.25D))
                    {
                        this.doorInteract = DoorBlock.isWooden(this.entity.world, this.doorPosition);

                        if (this.doorInteract)
                        {
                            return true;
                        }
                    }
                }

                this.doorPosition = this.entity.getPosition().up();
                this.doorInteract = DoorBlock.isWooden(this.entity.world, this.doorPosition);
                return this.doorInteract;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return !this.hasStoppedDoorInteraction;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.hasStoppedDoorInteraction = false;
        this.entityPositionX = (float)((double)this.doorPosition.getX() + 0.5D - this.entity.getPosX());
        this.entityPositionZ = (float)((double)this.doorPosition.getZ() + 0.5D - this.entity.getPosZ());
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick()
    {
        float f = (float)((double)this.doorPosition.getX() + 0.5D - this.entity.getPosX());
        float f1 = (float)((double)this.doorPosition.getZ() + 0.5D - this.entity.getPosZ());
        float f2 = this.entityPositionX * f + this.entityPositionZ * f1;

        if (f2 < 0.0F)
        {
            this.hasStoppedDoorInteraction = true;
        }
    }
}
