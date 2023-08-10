package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class WalkToTargetTask extends Task<MobEntity>
{
    private int field_242302_b;
    @Nullable
    private Path field_220488_a;
    @Nullable
    private BlockPos field_220489_b;
    private float field_220490_c;

    public WalkToTargetTask()
    {
        this(150, 250);
    }

    public WalkToTargetTask(int p_i241908_1_, int p_i241908_2_)
    {
        super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleStatus.REGISTERED, MemoryModuleType.PATH, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_PRESENT), p_i241908_1_, p_i241908_2_);
    }

    protected boolean shouldExecute(ServerWorld worldIn, MobEntity owner)
    {
        if (this.field_242302_b > 0)
        {
            --this.field_242302_b;
            return false;
        }
        else
        {
            Brain<?> brain = owner.getBrain();
            WalkTarget walktarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();
            boolean flag = this.hasReachedTarget(owner, walktarget);

            if (!flag && this.func_220487_a(owner, walktarget, worldIn.getGameTime()))
            {
                this.field_220489_b = walktarget.getTarget().getBlockPos();
                return true;
            }
            else
            {
                brain.removeMemory(MemoryModuleType.WALK_TARGET);

                if (flag)
                {
                    brain.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
                }

                return false;
            }
        }
    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn)
    {
        if (this.field_220488_a != null && this.field_220489_b != null)
        {
            Optional<WalkTarget> optional = entityIn.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
            PathNavigator pathnavigator = entityIn.getNavigator();
            return !pathnavigator.noPath() && optional.isPresent() && !this.hasReachedTarget(entityIn, optional.get());
        }
        else
        {
            return false;
        }
    }

    protected void resetTask(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn)
    {
        if (entityIn.getBrain().hasMemory(MemoryModuleType.WALK_TARGET) && !this.hasReachedTarget(entityIn, entityIn.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get()) && entityIn.getNavigator().func_244428_t())
        {
            this.field_242302_b = worldIn.getRandom().nextInt(40);
        }

        entityIn.getNavigator().clearPath();
        entityIn.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
        entityIn.getBrain().removeMemory(MemoryModuleType.PATH);
        this.field_220488_a = null;
    }

    protected void startExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn)
    {
        entityIn.getBrain().setMemory(MemoryModuleType.PATH, this.field_220488_a);
        entityIn.getNavigator().setPath(this.field_220488_a, (double)this.field_220490_c);
    }

    protected void updateTask(ServerWorld worldIn, MobEntity owner, long gameTime)
    {
        Path path = owner.getNavigator().getPath();
        Brain<?> brain = owner.getBrain();

        if (this.field_220488_a != path)
        {
            this.field_220488_a = path;
            brain.setMemory(MemoryModuleType.PATH, path);
        }

        if (path != null && this.field_220489_b != null)
        {
            WalkTarget walktarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();

            if (walktarget.getTarget().getBlockPos().distanceSq(this.field_220489_b) > 4.0D && this.func_220487_a(owner, walktarget, worldIn.getGameTime()))
            {
                this.field_220489_b = walktarget.getTarget().getBlockPos();
                this.startExecuting(worldIn, owner, gameTime);
            }
        }
    }

    private boolean func_220487_a(MobEntity p_220487_1_, WalkTarget p_220487_2_, long p_220487_3_)
    {
        BlockPos blockpos = p_220487_2_.getTarget().getBlockPos();
        this.field_220488_a = p_220487_1_.getNavigator().getPathToPos(blockpos, 0);
        this.field_220490_c = p_220487_2_.getSpeed();
        Brain<?> brain = p_220487_1_.getBrain();

        if (this.hasReachedTarget(p_220487_1_, p_220487_2_))
        {
            brain.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        }
        else
        {
            boolean flag = this.field_220488_a != null && this.field_220488_a.reachesTarget();

            if (flag)
            {
                brain.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            }
            else if (!brain.hasMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE))
            {
                brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, p_220487_3_);
            }

            if (this.field_220488_a != null)
            {
                return true;
            }

            Vector3d vector3d = RandomPositionGenerator.findRandomTargetBlockTowards((CreatureEntity)p_220487_1_, 10, 7, Vector3d.copyCenteredHorizontally(blockpos));

            if (vector3d != null)
            {
                this.field_220488_a = p_220487_1_.getNavigator().getPathToPos(vector3d.x, vector3d.y, vector3d.z, 0);
                return this.field_220488_a != null;
            }
        }

        return false;
    }

    private boolean hasReachedTarget(MobEntity p_220486_1_, WalkTarget p_220486_2_)
    {
        return p_220486_2_.getTarget().getBlockPos().manhattanDistance(p_220486_1_.getPosition()) <= p_220486_2_.getDistance();
    }
}
