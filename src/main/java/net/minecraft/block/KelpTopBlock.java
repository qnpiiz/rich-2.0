package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class KelpTopBlock extends AbstractTopPlantBlock implements ILiquidContainer
{
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);

    protected KelpTopBlock(AbstractBlock.Properties builder)
    {
        super(builder, Direction.UP, SHAPE, true, 0.14D);
    }

    protected boolean canGrowIn(BlockState state)
    {
        return state.isIn(Blocks.WATER);
    }

    protected Block getBodyPlantBlock()
    {
        return Blocks.KELP_PLANT;
    }

    protected boolean canGrowOn(Block block)
    {
        return block != Blocks.MAGMA_BLOCK;
    }

    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
    {
        return false;
    }

    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        return false;
    }

    /**
     * Used to determine how much to grow the plant when using bonemeal. Kelp always returns 1, where as the nether
     * vines return a random value at least 1.
     */
    protected int getGrowthAmount(Random rand)
    {
        return 1;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        return fluidstate.isTagged(FluidTags.WATER) && fluidstate.getLevel() == 8 ? super.getStateForPlacement(context) : null;
    }

    public FluidState getFluidState(BlockState state)
    {
        return Fluids.WATER.getStillFluidState(false);
    }
}
