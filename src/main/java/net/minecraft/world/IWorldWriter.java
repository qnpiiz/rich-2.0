package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface IWorldWriter
{
    boolean setBlockState(BlockPos pos, BlockState state, int flags, int recursionLeft);

default boolean setBlockState(BlockPos pos, BlockState newState, int flags)
    {
        return this.setBlockState(pos, newState, flags, 512);
    }

    boolean removeBlock(BlockPos pos, boolean isMoving);

default boolean destroyBlock(BlockPos pos, boolean dropBlock)
    {
        return this.destroyBlock(pos, dropBlock, (Entity)null);
    }

default boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity)
    {
        return this.destroyBlock(pos, dropBlock, entity, 512);
    }

    boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity, int recursionLeft);

default boolean addEntity(Entity entityIn)
    {
        return false;
    }
}
