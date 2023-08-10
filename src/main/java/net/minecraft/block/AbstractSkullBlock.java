package net.minecraft.block;

import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public abstract class AbstractSkullBlock extends ContainerBlock implements IArmorVanishable
{
    private final SkullBlock.ISkullType skullType;

    public AbstractSkullBlock(SkullBlock.ISkullType iSkullType, AbstractBlock.Properties properties)
    {
        super(properties);
        this.skullType = iSkullType;
    }

    public TileEntity createNewTileEntity(IBlockReader worldIn)
    {
        return new SkullTileEntity();
    }

    public SkullBlock.ISkullType getSkullType()
    {
        return this.skullType;
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    }
}
