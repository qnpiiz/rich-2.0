package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ScaffoldingBlock extends Block implements IWaterLoggable
{
    private static final VoxelShape TOP_SLAB_SHAPE;
    private static final VoxelShape FULL_SHAPE;
    private static final VoxelShape BOTTOM_SLAB_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    private static final VoxelShape field_220124_g = VoxelShapes.fullCube().withOffset(0.0D, -1.0D, 0.0D);
    public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE_0_7;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

    protected ScaffoldingBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(DISTANCE, Integer.valueOf(7)).with(WATERLOGGED, Boolean.valueOf(false)).with(BOTTOM, Boolean.valueOf(false)));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(DISTANCE, WATERLOGGED, BOTTOM);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if (!context.hasItem(state.getBlock().asItem()))
        {
            return state.get(BOTTOM) ? FULL_SHAPE : TOP_SLAB_SHAPE;
        }
        else
        {
            return VoxelShapes.fullCube();
        }
    }

    public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return VoxelShapes.fullCube();
    }

    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext)
    {
        return useContext.getItem().getItem() == this.asItem();
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos blockpos = context.getPos();
        World world = context.getWorld();
        int i = getDistance(world, blockpos);
        return this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(world.getFluidState(blockpos).getFluid() == Fluids.WATER)).with(DISTANCE, Integer.valueOf(i)).with(BOTTOM, Boolean.valueOf(this.hasScaffoldingBelow(world, blockpos, i)));
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (!worldIn.isRemote)
        {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
        }
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder
     * immediately returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (stateIn.get(WATERLOGGED))
        {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        if (!worldIn.isRemote())
        {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
        }

        return stateIn;
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        int i = getDistance(worldIn, pos);
        BlockState blockstate = state.with(DISTANCE, Integer.valueOf(i)).with(BOTTOM, Boolean.valueOf(this.hasScaffoldingBelow(worldIn, pos, i)));

        if (blockstate.get(DISTANCE) == 7)
        {
            if (state.get(DISTANCE) == 7)
            {
                worldIn.addEntity(new FallingBlockEntity(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, blockstate.with(WATERLOGGED, Boolean.valueOf(false))));
            }
            else
            {
                worldIn.destroyBlock(pos, true);
            }
        }
        else if (state != blockstate)
        {
            worldIn.setBlockState(pos, blockstate, 3);
        }
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return getDistance(worldIn, pos) < 7;
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if (context.func_216378_a(VoxelShapes.fullCube(), pos, true) && !context.getPosY())
        {
            return TOP_SLAB_SHAPE;
        }
        else
        {
            return state.get(DISTANCE) != 0 && state.get(BOTTOM) && context.func_216378_a(field_220124_g, pos, true) ? BOTTOM_SLAB_SHAPE : VoxelShapes.empty();
        }
    }

    public FluidState getFluidState(BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    private boolean hasScaffoldingBelow(IBlockReader blockReader, BlockPos pos, int distance)
    {
        return distance > 0 && !blockReader.getBlockState(pos.down()).isIn(this);
    }

    public static int getDistance(IBlockReader blockReader, BlockPos pos)
    {
        BlockPos.Mutable blockpos$mutable = pos.toMutable().move(Direction.DOWN);
        BlockState blockstate = blockReader.getBlockState(blockpos$mutable);
        int i = 7;

        if (blockstate.isIn(Blocks.SCAFFOLDING))
        {
            i = blockstate.get(DISTANCE);
        }
        else if (blockstate.isSolidSide(blockReader, blockpos$mutable, Direction.UP))
        {
            return 0;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockState blockstate1 = blockReader.getBlockState(blockpos$mutable.setAndMove(pos, direction));

            if (blockstate1.isIn(Blocks.SCAFFOLDING))
            {
                i = Math.min(i, blockstate1.get(DISTANCE) + 1);

                if (i == 1)
                {
                    break;
                }
            }
        }

        return i;
    }

    static
    {
        VoxelShape voxelshape = Block.makeCuboidShape(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        VoxelShape voxelshape1 = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D);
        VoxelShape voxelshape2 = Block.makeCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
        VoxelShape voxelshape3 = Block.makeCuboidShape(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0D);
        VoxelShape voxelshape4 = Block.makeCuboidShape(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
        TOP_SLAB_SHAPE = VoxelShapes.or(voxelshape, voxelshape1, voxelshape2, voxelshape3, voxelshape4);
        VoxelShape voxelshape5 = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 2.0D, 16.0D);
        VoxelShape voxelshape6 = Block.makeCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
        VoxelShape voxelshape7 = Block.makeCuboidShape(0.0D, 0.0D, 14.0D, 16.0D, 2.0D, 16.0D);
        VoxelShape voxelshape8 = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 2.0D);
        FULL_SHAPE = VoxelShapes.or(ScaffoldingBlock.BOTTOM_SLAB_SHAPE, TOP_SLAB_SHAPE, voxelshape6, voxelshape5, voxelshape8, voxelshape7);
    }
}
