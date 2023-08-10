package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class TallSeaGrassBlock extends DoublePlantBlock implements ILiquidContainer
{
    public static final EnumProperty<DoubleBlockHalf> HALF = DoublePlantBlock.HALF;
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public TallSeaGrassBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return state.isSolidSide(worldIn, pos, Direction.UP) && !state.isIn(Blocks.MAGMA_BLOCK);
    }

    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
        return new ItemStack(Blocks.SEAGRASS);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState blockstate = super.getStateForPlacement(context);

        if (blockstate != null)
        {
            FluidState fluidstate = context.getWorld().getFluidState(context.getPos().up());

            if (fluidstate.isTagged(FluidTags.WATER) && fluidstate.getLevel() == 8)
            {
                return blockstate;
            }
        }

        return null;
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        if (state.get(HALF) == DoubleBlockHalf.UPPER)
        {
            BlockState blockstate = worldIn.getBlockState(pos.down());
            return blockstate.isIn(this) && blockstate.get(HALF) == DoubleBlockHalf.LOWER;
        }
        else
        {
            FluidState fluidstate = worldIn.getFluidState(pos);
            return super.isValidPosition(state, worldIn, pos) && fluidstate.isTagged(FluidTags.WATER) && fluidstate.getLevel() == 8;
        }
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
