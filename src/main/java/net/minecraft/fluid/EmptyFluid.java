package net.minecraft.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class EmptyFluid extends Fluid
{
    public Item getFilledBucket()
    {
        return Items.AIR;
    }

    public boolean canDisplace(FluidState fluidState, IBlockReader blockReader, BlockPos pos, Fluid fluid, Direction direction)
    {
        return true;
    }

    public Vector3d getFlow(IBlockReader blockReader, BlockPos pos, FluidState fluidState)
    {
        return Vector3d.ZERO;
    }

    public int getTickRate(IWorldReader p_205569_1_)
    {
        return 0;
    }

    protected boolean isEmpty()
    {
        return true;
    }

    protected float getExplosionResistance()
    {
        return 0.0F;
    }

    public float getActualHeight(FluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_)
    {
        return 0.0F;
    }

    public float getHeight(FluidState p_223407_1_)
    {
        return 0.0F;
    }

    protected BlockState getBlockState(FluidState state)
    {
        return Blocks.AIR.getDefaultState();
    }

    public boolean isSource(FluidState state)
    {
        return false;
    }

    public int getLevel(FluidState state)
    {
        return 0;
    }

    public VoxelShape func_215664_b(FluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_)
    {
        return VoxelShapes.empty();
    }
}
