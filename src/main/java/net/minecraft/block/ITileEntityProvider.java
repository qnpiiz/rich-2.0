package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public interface ITileEntityProvider
{
    @Nullable
    TileEntity createNewTileEntity(IBlockReader worldIn);
}
