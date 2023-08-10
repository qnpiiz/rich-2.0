package net.minecraft.block;

import java.util.Random;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

public class TwistingVinesTopBlock extends AbstractTopPlantBlock
{
    public static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 15.0D, 12.0D);

    public TwistingVinesTopBlock(AbstractBlock.Properties properties)
    {
        super(properties, Direction.UP, SHAPE, false, 0.1D);
    }

    /**
     * Used to determine how much to grow the plant when using bonemeal. Kelp always returns 1, where as the nether
     * vines return a random value at least 1.
     */
    protected int getGrowthAmount(Random rand)
    {
        return PlantBlockHelper.getGrowthAmount(rand);
    }

    protected Block getBodyPlantBlock()
    {
        return Blocks.TWISTING_VINES_PLANT;
    }

    protected boolean canGrowIn(BlockState state)
    {
        return PlantBlockHelper.isAir(state);
    }
}
