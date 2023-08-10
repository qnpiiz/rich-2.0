package net.minecraft.block;

import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

public class WeepingVinesBlock extends AbstractBodyPlantBlock
{
    public static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public WeepingVinesBlock(AbstractBlock.Properties properties)
    {
        super(properties, Direction.DOWN, SHAPE, false);
    }

    protected AbstractTopPlantBlock getTopPlantBlock()
    {
        return (AbstractTopPlantBlock)Blocks.WEEPING_VINES;
    }
}
