package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class SleepAtHomeTask extends Task<LivingEntity>
{
    private long field_220552_a;

    public SleepAtHomeTask()
    {
        super(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.LAST_WOKEN, MemoryModuleStatus.REGISTERED));
    }

    protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner)
    {
        if (owner.isPassenger())
        {
            return false;
        }
        else
        {
            Brain<?> brain = owner.getBrain();
            GlobalPos globalpos = brain.getMemory(MemoryModuleType.HOME).get();

            if (worldIn.getDimensionKey() != globalpos.getDimension())
            {
                return false;
            }
            else
            {
                Optional<Long> optional = brain.getMemory(MemoryModuleType.LAST_WOKEN);

                if (optional.isPresent())
                {
                    long i = worldIn.getGameTime() - optional.get();

                    if (i > 0L && i < 100L)
                    {
                        return false;
                    }
                }

                BlockState blockstate = worldIn.getBlockState(globalpos.getPos());
                return globalpos.getPos().withinDistance(owner.getPositionVec(), 2.0D) && blockstate.getBlock().isIn(BlockTags.BEDS) && !blockstate.get(BedBlock.OCCUPIED);
            }
        }
    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn)
    {
        Optional<GlobalPos> optional = entityIn.getBrain().getMemory(MemoryModuleType.HOME);

        if (!optional.isPresent())
        {
            return false;
        }
        else
        {
            BlockPos blockpos = optional.get().getPos();
            return entityIn.getBrain().hasActivity(Activity.REST) && entityIn.getPosY() > (double)blockpos.getY() + 0.4D && blockpos.withinDistance(entityIn.getPositionVec(), 1.14D);
        }
    }

    protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn)
    {
        if (gameTimeIn > this.field_220552_a)
        {
            InteractWithDoorTask.func_242294_a(worldIn, entityIn, (PathPoint)null, (PathPoint)null);
            entityIn.startSleeping(entityIn.getBrain().getMemory(MemoryModuleType.HOME).get().getPos());
        }
    }

    protected boolean isTimedOut(long gameTime)
    {
        return false;
    }

    protected void resetTask(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn)
    {
        if (entityIn.isSleeping())
        {
            entityIn.wakeUp();
            this.field_220552_a = gameTimeIn + 40L;
        }
    }
}
