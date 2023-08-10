package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public class VineBlock extends Block
{
    public static final BooleanProperty UP = SixWayBlock.UP;
    public static final BooleanProperty NORTH = SixWayBlock.NORTH;
    public static final BooleanProperty EAST = SixWayBlock.EAST;
    public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    public static final BooleanProperty WEST = SixWayBlock.WEST;
    public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((facingProperty) ->
    {
        return facingProperty.getKey() != Direction.DOWN;
    }).collect(Util.toMapCollector());
    private static final VoxelShape UP_AABB = Block.makeCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.makeCuboidShape(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private final Map<BlockState, VoxelShape> stateToShapeMap;

    public VineBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(UP, Boolean.valueOf(false)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)));
        this.stateToShapeMap = ImmutableMap.copyOf(this.stateContainer.getValidStates().stream().collect(Collectors.toMap(Function.identity(), VineBlock::getShapeForState)));
    }

    private static VoxelShape getShapeForState(BlockState state)
    {
        VoxelShape voxelshape = VoxelShapes.empty();

        if (state.get(UP))
        {
            voxelshape = UP_AABB;
        }

        if (state.get(NORTH))
        {
            voxelshape = VoxelShapes.or(voxelshape, SOUTH_AABB);
        }

        if (state.get(SOUTH))
        {
            voxelshape = VoxelShapes.or(voxelshape, NORTH_AABB);
        }

        if (state.get(EAST))
        {
            voxelshape = VoxelShapes.or(voxelshape, WEST_AABB);
        }

        if (state.get(WEST))
        {
            voxelshape = VoxelShapes.or(voxelshape, EAST_AABB);
        }

        return voxelshape;
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.stateToShapeMap.get(state);
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return this.getBlocksAttachedTo(this.func_196545_h(state, worldIn, pos));
    }

    private boolean getBlocksAttachedTo(BlockState state)
    {
        return this.countBlocksVineIsAttachedTo(state) > 0;
    }

    private int countBlocksVineIsAttachedTo(BlockState state)
    {
        int i = 0;

        for (BooleanProperty booleanproperty : FACING_TO_PROPERTY_MAP.values())
        {
            if (state.get(booleanproperty))
            {
                ++i;
            }
        }

        return i;
    }

    private boolean hasAttachment(IBlockReader blockReader, BlockPos pos, Direction direction)
    {
        if (direction == Direction.DOWN)
        {
            return false;
        }
        else
        {
            BlockPos blockpos = pos.offset(direction);

            if (canAttachTo(blockReader, blockpos, direction))
            {
                return true;
            }
            else if (direction.getAxis() == Direction.Axis.Y)
            {
                return false;
            }
            else
            {
                BooleanProperty booleanproperty = FACING_TO_PROPERTY_MAP.get(direction);
                BlockState blockstate = blockReader.getBlockState(pos.up());
                return blockstate.isIn(this) && blockstate.get(booleanproperty);
            }
        }
    }

    public static boolean canAttachTo(IBlockReader blockReader, BlockPos worldIn, Direction neighborPos)
    {
        BlockState blockstate = blockReader.getBlockState(worldIn);
        return Block.doesSideFillSquare(blockstate.getCollisionShape(blockReader, worldIn), neighborPos.getOpposite());
    }

    private BlockState func_196545_h(BlockState state, IBlockReader blockReader, BlockPos pos)
    {
        BlockPos blockpos = pos.up();

        if (state.get(UP))
        {
            state = state.with(UP, Boolean.valueOf(canAttachTo(blockReader, blockpos, Direction.DOWN)));
        }

        BlockState blockstate = null;

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BooleanProperty booleanproperty = getPropertyFor(direction);

            if (state.get(booleanproperty))
            {
                boolean flag = this.hasAttachment(blockReader, pos, direction);

                if (!flag)
                {
                    if (blockstate == null)
                    {
                        blockstate = blockReader.getBlockState(blockpos);
                    }

                    flag = blockstate.isIn(this) && blockstate.get(booleanproperty);
                }

                state = state.with(booleanproperty, Boolean.valueOf(flag));
            }
        }

        return state;
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder
     * immediately returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN)
        {
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        else
        {
            BlockState blockstate = this.func_196545_h(stateIn, worldIn, currentPos);
            return !this.getBlocksAttachedTo(blockstate) ? Blocks.AIR.getDefaultState() : blockstate;
        }
    }

    /**
     * Performs a random tick on a block.
     */
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (worldIn.rand.nextInt(4) == 0)
        {
            Direction direction = Direction.getRandomDirection(random);
            BlockPos blockpos = pos.up();

            if (direction.getAxis().isHorizontal() && !state.get(getPropertyFor(direction)))
            {
                if (this.hasVineBelow(worldIn, pos))
                {
                    BlockPos blockpos4 = pos.offset(direction);
                    BlockState blockstate4 = worldIn.getBlockState(blockpos4);

                    if (blockstate4.isAir())
                    {
                        Direction direction3 = direction.rotateY();
                        Direction direction4 = direction.rotateYCCW();
                        boolean flag = state.get(getPropertyFor(direction3));
                        boolean flag1 = state.get(getPropertyFor(direction4));
                        BlockPos blockpos2 = blockpos4.offset(direction3);
                        BlockPos blockpos3 = blockpos4.offset(direction4);

                        if (flag && canAttachTo(worldIn, blockpos2, direction3))
                        {
                            worldIn.setBlockState(blockpos4, this.getDefaultState().with(getPropertyFor(direction3), Boolean.valueOf(true)), 2);
                        }
                        else if (flag1 && canAttachTo(worldIn, blockpos3, direction4))
                        {
                            worldIn.setBlockState(blockpos4, this.getDefaultState().with(getPropertyFor(direction4), Boolean.valueOf(true)), 2);
                        }
                        else
                        {
                            Direction direction1 = direction.getOpposite();

                            if (flag && worldIn.isAirBlock(blockpos2) && canAttachTo(worldIn, pos.offset(direction3), direction1))
                            {
                                worldIn.setBlockState(blockpos2, this.getDefaultState().with(getPropertyFor(direction1), Boolean.valueOf(true)), 2);
                            }
                            else if (flag1 && worldIn.isAirBlock(blockpos3) && canAttachTo(worldIn, pos.offset(direction4), direction1))
                            {
                                worldIn.setBlockState(blockpos3, this.getDefaultState().with(getPropertyFor(direction1), Boolean.valueOf(true)), 2);
                            }
                            else if ((double)worldIn.rand.nextFloat() < 0.05D && canAttachTo(worldIn, blockpos4.up(), Direction.UP))
                            {
                                worldIn.setBlockState(blockpos4, this.getDefaultState().with(UP, Boolean.valueOf(true)), 2);
                            }
                        }
                    }
                    else if (canAttachTo(worldIn, blockpos4, direction))
                    {
                        worldIn.setBlockState(pos, state.with(getPropertyFor(direction), Boolean.valueOf(true)), 2);
                    }
                }
            }
            else
            {
                if (direction == Direction.UP && pos.getY() < 255)
                {
                    if (this.hasAttachment(worldIn, pos, direction))
                    {
                        worldIn.setBlockState(pos, state.with(UP, Boolean.valueOf(true)), 2);
                        return;
                    }

                    if (worldIn.isAirBlock(blockpos))
                    {
                        if (!this.hasVineBelow(worldIn, pos))
                        {
                            return;
                        }

                        BlockState blockstate3 = state;

                        for (Direction direction2 : Direction.Plane.HORIZONTAL)
                        {
                            if (random.nextBoolean() || !canAttachTo(worldIn, blockpos.offset(direction2), Direction.UP))
                            {
                                blockstate3 = blockstate3.with(getPropertyFor(direction2), Boolean.valueOf(false));
                            }
                        }

                        if (this.isFacingCardinal(blockstate3))
                        {
                            worldIn.setBlockState(blockpos, blockstate3, 2);
                        }

                        return;
                    }
                }

                if (pos.getY() > 0)
                {
                    BlockPos blockpos1 = pos.down();
                    BlockState blockstate = worldIn.getBlockState(blockpos1);

                    if (blockstate.isAir() || blockstate.isIn(this))
                    {
                        BlockState blockstate1 = blockstate.isAir() ? this.getDefaultState() : blockstate;
                        BlockState blockstate2 = this.func_196544_a(state, blockstate1, random);

                        if (blockstate1 != blockstate2 && this.isFacingCardinal(blockstate2))
                        {
                            worldIn.setBlockState(blockpos1, blockstate2, 2);
                        }
                    }
                }
            }
        }
    }

    private BlockState func_196544_a(BlockState state, BlockState state2, Random rand)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            if (rand.nextBoolean())
            {
                BooleanProperty booleanproperty = getPropertyFor(direction);

                if (state.get(booleanproperty))
                {
                    state2 = state2.with(booleanproperty, Boolean.valueOf(true));
                }
            }
        }

        return state2;
    }

    private boolean isFacingCardinal(BlockState state)
    {
        return state.get(NORTH) || state.get(EAST) || state.get(SOUTH) || state.get(WEST);
    }

    private boolean hasVineBelow(IBlockReader blockReader, BlockPos pos)
    {
        int i = 4;
        Iterable<BlockPos> iterable = BlockPos.getAllInBoxMutable(pos.getX() - 4, pos.getY() - 1, pos.getZ() - 4, pos.getX() + 4, pos.getY() + 1, pos.getZ() + 4);
        int j = 5;

        for (BlockPos blockpos : iterable)
        {
            if (blockReader.getBlockState(blockpos).isIn(this))
            {
                --j;

                if (j <= 0)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isReplaceable(BlockState state, BlockItemUseContext useContext)
    {
        BlockState blockstate = useContext.getWorld().getBlockState(useContext.getPos());

        if (blockstate.isIn(this))
        {
            return this.countBlocksVineIsAttachedTo(blockstate) < FACING_TO_PROPERTY_MAP.size();
        }
        else
        {
            return super.isReplaceable(state, useContext);
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos());
        boolean flag = blockstate.isIn(this);
        BlockState blockstate1 = flag ? blockstate : this.getDefaultState();

        for (Direction direction : context.getNearestLookingDirections())
        {
            if (direction != Direction.DOWN)
            {
                BooleanProperty booleanproperty = getPropertyFor(direction);
                boolean flag1 = flag && blockstate.get(booleanproperty);

                if (!flag1 && this.hasAttachment(context.getWorld(), context.getPos(), direction))
                {
                    return blockstate1.with(booleanproperty, Boolean.valueOf(true));
                }
            }
        }

        return flag ? blockstate1 : null;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(UP, NORTH, EAST, SOUTH, WEST);
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
                return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));

            case COUNTERCLOCKWISE_90:
                return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));

            case CLOCKWISE_90:
                return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));

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
                return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));

            case FRONT_BACK:
                return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));

            default:
                return super.mirror(state, mirrorIn);
        }
    }

    public static BooleanProperty getPropertyFor(Direction side)
    {
        return FACING_TO_PROPERTY_MAP.get(side);
    }
}
