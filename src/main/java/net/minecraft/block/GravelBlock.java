package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class GravelBlock extends FallingBlock
{
    public GravelBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    public int getDustColor(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return -8356741;
    }
}
