package net.minecraft.block;

import java.util.function.Supplier;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractChestBlock<E extends TileEntity> extends ContainerBlock
{
    protected final Supplier < TileEntityType <? extends E >> tileEntityType;

    protected AbstractChestBlock(AbstractBlock.Properties builder, Supplier < TileEntityType <? extends E >> tileEntityTypeSupplier)
    {
        super(builder);
        this.tileEntityType = tileEntityTypeSupplier;
    }

    public abstract TileEntityMerger.ICallbackWrapper <? extends ChestTileEntity > combine(BlockState state, World world, BlockPos pos, boolean override);
}
