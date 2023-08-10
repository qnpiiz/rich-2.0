package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public class MoveThroughVillageAtNightGoal extends Goal
{
    private final CreatureEntity entity;
    private final int field_220757_b;
    @Nullable
    private BlockPos field_220758_c;

    public MoveThroughVillageAtNightGoal(CreatureEntity entity, int p_i50321_2_)
    {
        this.entity = entity;
        this.field_220757_b = p_i50321_2_;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute()
    {
        if (this.entity.isBeingRidden())
        {
            return false;
        }
        else if (this.entity.world.isDaytime())
        {
            return false;
        }
        else if (this.entity.getRNG().nextInt(this.field_220757_b) != 0)
        {
            return false;
        }
        else
        {
            ServerWorld serverworld = (ServerWorld)this.entity.world;
            BlockPos blockpos = this.entity.getPosition();

            if (!serverworld.func_241119_a_(blockpos, 6))
            {
                return false;
            }
            else
            {
                Vector3d vector3d = RandomPositionGenerator.func_221024_a(this.entity, 15, 7, (p_220755_1_) ->
                {
                    return (double)(-serverworld.sectionsToVillage(SectionPos.from(p_220755_1_)));
                });
                this.field_220758_c = vector3d == null ? null : new BlockPos(vector3d);
                return this.field_220758_c != null;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return this.field_220758_c != null && !this.entity.getNavigator().noPath() && this.entity.getNavigator().getTargetPos().equals(this.field_220758_c);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick()
    {
        if (this.field_220758_c != null)
        {
            PathNavigator pathnavigator = this.entity.getNavigator();

            if (pathnavigator.noPath() && !this.field_220758_c.withinDistance(this.entity.getPositionVec(), 10.0D))
            {
                Vector3d vector3d = Vector3d.copyCenteredHorizontally(this.field_220758_c);
                Vector3d vector3d1 = this.entity.getPositionVec();
                Vector3d vector3d2 = vector3d1.subtract(vector3d);
                vector3d = vector3d2.scale(0.4D).add(vector3d);
                Vector3d vector3d3 = vector3d.subtract(vector3d1).normalize().scale(10.0D).add(vector3d1);
                BlockPos blockpos = new BlockPos(vector3d3);
                blockpos = this.entity.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos);

                if (!pathnavigator.tryMoveToXYZ((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D))
                {
                    this.func_220754_g();
                }
            }
        }
    }

    private void func_220754_g()
    {
        Random random = this.entity.getRNG();
        BlockPos blockpos = this.entity.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, this.entity.getPosition().add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
        this.entity.getNavigator().tryMoveToXYZ((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D);
    }
}
