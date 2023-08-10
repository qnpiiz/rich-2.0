package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class KelpBlock extends AbstractBodyPlantBlock implements ILiquidContainer
{
    protected KelpBlock(AbstractBlock.Properties properties)
    {
        super(properties, Direction.UP, VoxelShapes.fullCube(), true);
    }

    protected AbstractTopPlantBlock getTopPlantBlock()
    {
        return (AbstractTopPlantBlock)Blocks.KELP;
    }

    public FluidState getFluidState(BlockState state)
    {
        return Fluids.WATER.getStillFluidState(false);
    }

    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
    {
        return false;
    }

    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        return false;
    }
}
