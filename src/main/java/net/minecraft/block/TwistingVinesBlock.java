package net.minecraft.block;

import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

public class TwistingVinesBlock extends AbstractBodyPlantBlock
{
    public static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public TwistingVinesBlock(AbstractBlock.Properties properties)
    {
        super(properties, Direction.UP, SHAPE, false);
    }

    protected AbstractTopPlantBlock getTopPlantBlock()
    {
        return (AbstractTopPlantBlock)Blocks.TWISTING_VINES;
    }
}
