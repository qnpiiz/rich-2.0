package net.minecraft.inventory;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface ISidedInventoryProvider
{
    ISidedInventory createInventory(BlockState state, IWorld world, BlockPos pos);
}
