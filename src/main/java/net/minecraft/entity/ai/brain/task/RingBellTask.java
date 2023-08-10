package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class RingBellTask extends Task<LivingEntity>
{
    public RingBellTask()
    {
        super(ImmutableMap.of(MemoryModuleType.MEETING_POINT, MemoryModuleStatus.VALUE_PRESENT));
    }

    protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner)
    {
        return worldIn.rand.nextFloat() > 0.95F;
    }

    protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn)
    {
        Brain<?> brain = entityIn.getBrain();
        BlockPos blockpos = brain.getMemory(MemoryModuleType.MEETING_POINT).get().getPos();

        if (blockpos.withinDistance(entityIn.getPosition(), 3.0D))
        {
            BlockState blockstate = worldIn.getBlockState(blockpos);

            if (blockstate.isIn(Blocks.BELL))
            {
                BellBlock bellblock = (BellBlock)blockstate.getBlock();
                bellblock.ring(worldIn, blockpos, (Direction)null);
            }
        }
    }
}
