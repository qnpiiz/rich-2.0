package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class WallBlock extends Block implements IWaterLoggable
{
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final EnumProperty<WallHeight> WALL_HEIGHT_EAST = BlockStateProperties.WALL_HEIGHT_EAST;
    public static final EnumProperty<WallHeight> WALL_HEIGHT_NORTH = BlockStateProperties.WALL_HEIGHT_NORTH;
    public static final EnumProperty<WallHeight> WALL_HEIGHT_SOUTH = BlockStateProperties.WALL_HEIGHT_SOUTH;
    public static final EnumProperty<WallHeight> WALL_HEIGHT_WEST = BlockStateProperties.WALL_HEIGHT_WEST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final Map<BlockState, VoxelShape> stateToShapeMap;
    private final Map<BlockState, VoxelShape> stateToCollisionShapeMap;
    private static final VoxelShape CENTER_POLE_SHAPE = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape WALL_CONNECTION_NORTH_SIDE_SHAPE = Block.makeCuboidShape(7.0D, 0.0D, 0.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape WALL_CONNECTION_SOUTH_SIDE_SHAPE = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 16.0D);
    private static final VoxelShape WALL_CONNECTION_WEST_SIDE_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape WALL_CONNECTION_EAST_SIDE_SHAPE = Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D);

    public WallBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(UP, Boolean.valueOf(true)).with(WALL_HEIGHT_NORTH, WallHeight.NONE).with(WALL_HEIGHT_EAST, WallHeight.NONE).with(WALL_HEIGHT_SOUTH, WallHeight.NONE).with(WALL_HEIGHT_WEST, WallHeight.NONE).with(WATERLOGGED, Boolean.valueOf(false)));
        this.stateToShapeMap = this.makeShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F);
        this.stateToCollisionShapeMap = this.makeShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F);
    }

    private static VoxelShape getHeightAlteredShape(VoxelShape baseShape, WallHeight height, VoxelShape lowShape, VoxelShape tallShape)
    {
        if (height == WallHeight.TALL)
        {
            return VoxelShapes.or(baseShape, tallShape);
        }
        else
        {
            return height == WallHeight.LOW ? VoxelShapes.or(baseShape, lowShape) : baseShape;
        }
    }

    private Map<BlockState, VoxelShape> makeShapes(float p_235624_1_, float p_235624_2_, float p_235624_3_, float p_235624_4_, float p_235624_5_, float p_235624_6_)
    {
        float f = 8.0F - p_235624_1_;
        float f1 = 8.0F + p_235624_1_;
        float f2 = 8.0F - p_235624_2_;
        float f3 = 8.0F + p_235624_2_;
        VoxelShape voxelshape = Block.makeCuboidShape((double)f, 0.0D, (double)f, (double)f1, (double)p_235624_3_, (double)f1);
        VoxelShape voxelshape1 = Block.makeCuboidShape((double)f2, (double)p_235624_4_, 0.0D, (double)f3, (double)p_235624_5_, (double)f3);
        VoxelShape voxelshape2 = Block.makeCuboidShape((double)f2, (double)p_235624_4_, (double)f2, (double)f3, (double)p_235624_5_, 16.0D);
        VoxelShape voxelshape3 = Block.makeCuboidShape(0.0D, (double)p_235624_4_, (double)f2, (double)f3, (double)p_235624_5_, (double)f3);
        VoxelShape voxelshape4 = Block.makeCuboidShape((double)f2, (double)p_235624_4_, (double)f2, 16.0D, (double)p_235624_5_, (double)f3);
        VoxelShape voxelshape5 = Block.makeCuboidShape((double)f2, (double)p_235624_4_, 0.0D, (double)f3, (double)p_235624_6_, (double)f3);
        VoxelShape voxelshape6 = Block.makeCuboidShape((double)f2, (double)p_235624_4_, (double)f2, (double)f3, (double)p_235624_6_, 16.0D);
        VoxelShape voxelshape7 = Block.makeCuboidShape(0.0D, (double)p_235624_4_, (double)f2, (double)f3, (double)p_235624_6_, (double)f3);
        VoxelShape voxelshape8 = Block.makeCuboidShape((double)f2, (double)p_235624_4_, (double)f2, 16.0D, (double)p_235624_6_, (double)f3);
        Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (Boolean obool : UP.getAllowedValues())
        {
            for (WallHeight wallheight : WALL_HEIGHT_EAST.getAllowedValues())
            {
                for (WallHeight wallheight1 : WALL_HEIGHT_NORTH.getAllowedValues())
                {
                    for (WallHeight wallheight2 : WALL_HEIGHT_WEST.getAllowedValues())
                    {
                        for (WallHeight wallheight3 : WALL_HEIGHT_SOUTH.getAllowedValues())
                        {
                            VoxelShape voxelshape9 = VoxelShapes.empty();
                            voxelshape9 = getHeightAlteredShape(voxelshape9, wallheight, voxelshape4, voxelshape8);
                            voxelshape9 = getHeightAlteredShape(voxelshape9, wallheight2, voxelshape3, voxelshape7);
                            voxelshape9 = getHeightAlteredShape(voxelshape9, wallheight1, voxelshape1, voxelshape5);
                            voxelshape9 = getHeightAlteredShape(voxelshape9, wallheight3, voxelshape2, voxelshape6);

                            if (obool)
                            {
                                voxelshape9 = VoxelShapes.or(voxelshape9, voxelshape);
                            }

                            BlockState blockstate = this.getDefaultState().with(UP, obool).with(WALL_HEIGHT_EAST, wallheight).with(WALL_HEIGHT_WEST, wallheight2).with(WALL_HEIGHT_NORTH, wallheight1).with(WALL_HEIGHT_SOUTH, wallheight3);
                            builder.put(blockstate.with(WATERLOGGED, Boolean.valueOf(false)), voxelshape9);
                            builder.put(blockstate.with(WATERLOGGED, Boolean.valueOf(true)), voxelshape9);
                        }
                    }
                }
            }
        }

        return builder.build();
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.stateToShapeMap.get(state);
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.stateToCollisionShapeMap.get(state);
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    }

    private boolean shouldConnect(BlockState state, boolean sideSolid, Direction direction)
    {
        Block block = state.getBlock();
        boolean flag = block instanceof FenceGateBlock && FenceGateBlock.isParallel(state, direction);
        return state.isIn(BlockTags.WALLS) || !cannotAttach(block) && sideSolid || block instanceof PaneBlock || flag;
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        IWorldReader iworldreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        BlockPos blockpos1 = blockpos.north();
        BlockPos blockpos2 = blockpos.east();
        BlockPos blockpos3 = blockpos.south();
        BlockPos blockpos4 = blockpos.west();
        BlockPos blockpos5 = blockpos.up();
        BlockState blockstate = iworldreader.getBlockState(blockpos1);
        BlockState blockstate1 = iworldreader.getBlockState(blockpos2);
        BlockState blockstate2 = iworldreader.getBlockState(blockpos3);
        BlockState blockstate3 = iworldreader.getBlockState(blockpos4);
        BlockState blockstate4 = iworldreader.getBlockState(blockpos5);
        boolean flag = this.shouldConnect(blockstate, blockstate.isSolidSide(iworldreader, blockpos1, Direction.SOUTH), Direction.SOUTH);
        boolean flag1 = this.shouldConnect(blockstate1, blockstate1.isSolidSide(iworldreader, blockpos2, Direction.WEST), Direction.WEST);
        boolean flag2 = this.shouldConnect(blockstate2, blockstate2.isSolidSide(iworldreader, blockpos3, Direction.NORTH), Direction.NORTH);
        boolean flag3 = this.shouldConnect(blockstate3, blockstate3.isSolidSide(iworldreader, blockpos4, Direction.EAST), Direction.EAST);
        BlockState blockstate5 = this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(fluidstate.getFluid() == Fluids.WATER));
        return this.func_235626_a_(iworldreader, blockstate5, blockpos5, blockstate4, flag, flag1, flag2, flag3);
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

        if (facing == Direction.DOWN)
        {
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        else
        {
            return facing == Direction.UP ? this.func_235625_a_(worldIn, stateIn, facingPos, facingState) : this.func_235627_a_(worldIn, currentPos, stateIn, facingPos, facingState, facing);
        }
    }

    private static boolean hasHeightForProperty(BlockState state, Property<WallHeight> heightProperty)
    {
        return state.get(heightProperty) != WallHeight.NONE;
    }

    private static boolean compareShapes(VoxelShape shape1, VoxelShape shape2)
    {
        return !VoxelShapes.compare(shape2, shape1, IBooleanFunction.ONLY_FIRST);
    }

    private BlockState func_235625_a_(IWorldReader reader, BlockState state1, BlockPos pos, BlockState state2)
    {
        boolean flag = hasHeightForProperty(state1, WALL_HEIGHT_NORTH);
        boolean flag1 = hasHeightForProperty(state1, WALL_HEIGHT_EAST);
        boolean flag2 = hasHeightForProperty(state1, WALL_HEIGHT_SOUTH);
        boolean flag3 = hasHeightForProperty(state1, WALL_HEIGHT_WEST);
        return this.func_235626_a_(reader, state1, pos, state2, flag, flag1, flag2, flag3);
    }

    private BlockState func_235627_a_(IWorldReader reader, BlockPos p_235627_2_, BlockState p_235627_3_, BlockPos p_235627_4_, BlockState p_235627_5_, Direction directionIn)
    {
        Direction direction = directionIn.getOpposite();
        boolean flag = directionIn == Direction.NORTH ? this.shouldConnect(p_235627_5_, p_235627_5_.isSolidSide(reader, p_235627_4_, direction), direction) : hasHeightForProperty(p_235627_3_, WALL_HEIGHT_NORTH);
        boolean flag1 = directionIn == Direction.EAST ? this.shouldConnect(p_235627_5_, p_235627_5_.isSolidSide(reader, p_235627_4_, direction), direction) : hasHeightForProperty(p_235627_3_, WALL_HEIGHT_EAST);
        boolean flag2 = directionIn == Direction.SOUTH ? this.shouldConnect(p_235627_5_, p_235627_5_.isSolidSide(reader, p_235627_4_, direction), direction) : hasHeightForProperty(p_235627_3_, WALL_HEIGHT_SOUTH);
        boolean flag3 = directionIn == Direction.WEST ? this.shouldConnect(p_235627_5_, p_235627_5_.isSolidSide(reader, p_235627_4_, direction), direction) : hasHeightForProperty(p_235627_3_, WALL_HEIGHT_WEST);
        BlockPos blockpos = p_235627_2_.up();
        BlockState blockstate = reader.getBlockState(blockpos);
        return this.func_235626_a_(reader, p_235627_3_, blockpos, blockstate, flag, flag1, flag2, flag3);
    }

    private BlockState func_235626_a_(IWorldReader reader, BlockState state, BlockPos pos, BlockState collisionState, boolean connectedSouth, boolean connectedWest, boolean connectedNorth, boolean connectedEast)
    {
        VoxelShape voxelshape = collisionState.getCollisionShape(reader, pos).project(Direction.DOWN);
        BlockState blockstate = this.func_235630_a_(state, connectedSouth, connectedWest, connectedNorth, connectedEast, voxelshape);
        return blockstate.with(UP, Boolean.valueOf(this.func_235628_a_(blockstate, collisionState, voxelshape)));
    }

    private boolean func_235628_a_(BlockState p_235628_1_, BlockState p_235628_2_, VoxelShape shape)
    {
        boolean flag = p_235628_2_.getBlock() instanceof WallBlock && p_235628_2_.get(UP);

        if (flag)
        {
            return true;
        }
        else
        {
            WallHeight wallheight = p_235628_1_.get(WALL_HEIGHT_NORTH);
            WallHeight wallheight1 = p_235628_1_.get(WALL_HEIGHT_SOUTH);
            WallHeight wallheight2 = p_235628_1_.get(WALL_HEIGHT_EAST);
            WallHeight wallheight3 = p_235628_1_.get(WALL_HEIGHT_WEST);
            boolean flag1 = wallheight1 == WallHeight.NONE;
            boolean flag2 = wallheight3 == WallHeight.NONE;
            boolean flag3 = wallheight2 == WallHeight.NONE;
            boolean flag4 = wallheight == WallHeight.NONE;
            boolean flag5 = flag4 && flag1 && flag2 && flag3 || flag4 != flag1 || flag2 != flag3;

            if (flag5)
            {
                return true;
            }
            else
            {
                boolean flag6 = wallheight == WallHeight.TALL && wallheight1 == WallHeight.TALL || wallheight2 == WallHeight.TALL && wallheight3 == WallHeight.TALL;

                if (flag6)
                {
                    return false;
                }
                else
                {
                    return p_235628_2_.getBlock().isIn(BlockTags.WALL_POST_OVERRIDE) || compareShapes(shape, CENTER_POLE_SHAPE);
                }
            }
        }
    }

    private BlockState func_235630_a_(BlockState state, boolean connectedSouth, boolean connectedWest, boolean connectedNorth, boolean connectedEast, VoxelShape shape)
    {
        return state.with(WALL_HEIGHT_NORTH, this.func_235633_a_(connectedSouth, shape, WALL_CONNECTION_NORTH_SIDE_SHAPE)).with(WALL_HEIGHT_EAST, this.func_235633_a_(connectedWest, shape, WALL_CONNECTION_EAST_SIDE_SHAPE)).with(WALL_HEIGHT_SOUTH, this.func_235633_a_(connectedNorth, shape, WALL_CONNECTION_SOUTH_SIDE_SHAPE)).with(WALL_HEIGHT_WEST, this.func_235633_a_(connectedEast, shape, WALL_CONNECTION_WEST_SIDE_SHAPE));
    }

    private WallHeight func_235633_a_(boolean p_235633_1_, VoxelShape p_235633_2_, VoxelShape p_235633_3_)
    {
        if (p_235633_1_)
        {
            return compareShapes(p_235633_2_, p_235633_3_) ? WallHeight.TALL : WallHeight.LOW;
        }
        else
        {
            return WallHeight.NONE;
        }
    }

    public FluidState getFluidState(BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return !state.get(WATERLOGGED);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(UP, WALL_HEIGHT_NORTH, WALL_HEIGHT_EAST, WALL_HEIGHT_WEST, WALL_HEIGHT_SOUTH, WATERLOGGED);
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public BlockState rotate(BlockState state, Rotation rot)
    {
        switch (rot)
        {
            case CLOCKWISE_180:
                return state.with(WALL_HEIGHT_NORTH, state.get(WALL_HEIGHT_SOUTH)).with(WALL_HEIGHT_EAST, state.get(WALL_HEIGHT_WEST)).with(WALL_HEIGHT_SOUTH, state.get(WALL_HEIGHT_NORTH)).with(WALL_HEIGHT_WEST, state.get(WALL_HEIGHT_EAST));

            case COUNTERCLOCKWISE_90:
                return state.with(WALL_HEIGHT_NORTH, state.get(WALL_HEIGHT_EAST)).with(WALL_HEIGHT_EAST, state.get(WALL_HEIGHT_SOUTH)).with(WALL_HEIGHT_SOUTH, state.get(WALL_HEIGHT_WEST)).with(WALL_HEIGHT_WEST, state.get(WALL_HEIGHT_NORTH));

            case CLOCKWISE_90:
                return state.with(WALL_HEIGHT_NORTH, state.get(WALL_HEIGHT_WEST)).with(WALL_HEIGHT_EAST, state.get(WALL_HEIGHT_NORTH)).with(WALL_HEIGHT_SOUTH, state.get(WALL_HEIGHT_EAST)).with(WALL_HEIGHT_WEST, state.get(WALL_HEIGHT_SOUTH));

            default:
                return state;
        }
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                return state.with(WALL_HEIGHT_NORTH, state.get(WALL_HEIGHT_SOUTH)).with(WALL_HEIGHT_SOUTH, state.get(WALL_HEIGHT_NORTH));

            case FRONT_BACK:
                return state.with(WALL_HEIGHT_EAST, state.get(WALL_HEIGHT_WEST)).with(WALL_HEIGHT_WEST, state.get(WALL_HEIGHT_EAST));

            default:
                return super.mirror(state, mirrorIn);
        }
    }
}
