package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class RedstoneWireBlock extends Block
{
    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.REDSTONE_NORTH;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.REDSTONE_EAST;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.REDSTONE_SOUTH;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.REDSTONE_WEST;
    public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
    public static final Map<Direction, EnumProperty<RedstoneSide>> FACING_PROPERTY_MAP = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));
    private static final VoxelShape BASE_SHAPE = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);
    private static final Map<Direction, VoxelShape> SIDE_TO_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Direction.SOUTH, Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Direction.EAST, Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Direction.WEST, Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D)));
    private static final Map<Direction, VoxelShape> SIDE_TO_ASCENDING_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, VoxelShapes.or(SIDE_TO_SHAPE.get(Direction.NORTH), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 16.0D, 1.0D)), Direction.SOUTH, VoxelShapes.or(SIDE_TO_SHAPE.get(Direction.SOUTH), Block.makeCuboidShape(3.0D, 0.0D, 15.0D, 13.0D, 16.0D, 16.0D)), Direction.EAST, VoxelShapes.or(SIDE_TO_SHAPE.get(Direction.EAST), Block.makeCuboidShape(15.0D, 0.0D, 3.0D, 16.0D, 16.0D, 13.0D)), Direction.WEST, VoxelShapes.or(SIDE_TO_SHAPE.get(Direction.WEST), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 1.0D, 16.0D, 13.0D))));
    private final Map<BlockState, VoxelShape> stateToShapeMap = Maps.newHashMap();
    private static final Vector3f[] powerRGB = new Vector3f[16];
    private final BlockState sideBaseState;
    private boolean canProvidePower = true;

    public RedstoneWireBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, RedstoneSide.NONE).with(EAST, RedstoneSide.NONE).with(SOUTH, RedstoneSide.NONE).with(WEST, RedstoneSide.NONE).with(POWER, Integer.valueOf(0)));
        this.sideBaseState = this.getDefaultState().with(NORTH, RedstoneSide.SIDE).with(EAST, RedstoneSide.SIDE).with(SOUTH, RedstoneSide.SIDE).with(WEST, RedstoneSide.SIDE);

        for (BlockState blockstate : this.getStateContainer().getValidStates())
        {
            if (blockstate.get(POWER) == 0)
            {
                this.stateToShapeMap.put(blockstate, this.getShapeForState(blockstate));
            }
        }
    }

    private VoxelShape getShapeForState(BlockState state)
    {
        VoxelShape voxelshape = BASE_SHAPE;

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            RedstoneSide redstoneside = state.get(FACING_PROPERTY_MAP.get(direction));

            if (redstoneside == RedstoneSide.SIDE)
            {
                voxelshape = VoxelShapes.or(voxelshape, SIDE_TO_SHAPE.get(direction));
            }
            else if (redstoneside == RedstoneSide.UP)
            {
                voxelshape = VoxelShapes.or(voxelshape, SIDE_TO_ASCENDING_SHAPE.get(direction));
            }
        }

        return voxelshape;
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.stateToShapeMap.get(state.with(POWER, Integer.valueOf(0)));
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getUpdatedState(context.getWorld(), this.sideBaseState, context.getPos());
    }

    private BlockState getUpdatedState(IBlockReader reader, BlockState state, BlockPos pos)
    {
        boolean flag = areAllSidesInvalid(state);
        state = this.recalculateFacingState(reader, this.getDefaultState().with(POWER, state.get(POWER)), pos);

        if (flag && areAllSidesInvalid(state))
        {
            return state;
        }
        else
        {
            boolean flag1 = state.get(NORTH).func_235921_b_();
            boolean flag2 = state.get(SOUTH).func_235921_b_();
            boolean flag3 = state.get(EAST).func_235921_b_();
            boolean flag4 = state.get(WEST).func_235921_b_();
            boolean flag5 = !flag1 && !flag2;
            boolean flag6 = !flag3 && !flag4;

            if (!flag4 && flag5)
            {
                state = state.with(WEST, RedstoneSide.SIDE);
            }

            if (!flag3 && flag5)
            {
                state = state.with(EAST, RedstoneSide.SIDE);
            }

            if (!flag1 && flag6)
            {
                state = state.with(NORTH, RedstoneSide.SIDE);
            }

            if (!flag2 && flag6)
            {
                state = state.with(SOUTH, RedstoneSide.SIDE);
            }

            return state;
        }
    }

    private BlockState recalculateFacingState(IBlockReader reader, BlockState state, BlockPos pos)
    {
        boolean flag = !reader.getBlockState(pos.up()).isNormalCube(reader, pos);

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            if (!state.get(FACING_PROPERTY_MAP.get(direction)).func_235921_b_())
            {
                RedstoneSide redstoneside = this.recalculateSide(reader, pos, direction, flag);
                state = state.with(FACING_PROPERTY_MAP.get(direction), redstoneside);
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
            return stateIn;
        }
        else if (facing == Direction.UP)
        {
            return this.getUpdatedState(worldIn, stateIn, currentPos);
        }
        else
        {
            RedstoneSide redstoneside = this.getSide(worldIn, currentPos, facing);
            return redstoneside.func_235921_b_() == stateIn.get(FACING_PROPERTY_MAP.get(facing)).func_235921_b_() && !areAllSidesValid(stateIn) ? stateIn.with(FACING_PROPERTY_MAP.get(facing), redstoneside) : this.getUpdatedState(worldIn, this.sideBaseState.with(POWER, stateIn.get(POWER)).with(FACING_PROPERTY_MAP.get(facing), redstoneside), currentPos);
        }
    }

    private static boolean areAllSidesValid(BlockState state)
    {
        return state.get(NORTH).func_235921_b_() && state.get(SOUTH).func_235921_b_() && state.get(EAST).func_235921_b_() && state.get(WEST).func_235921_b_();
    }

    private static boolean areAllSidesInvalid(BlockState state)
    {
        return !state.get(NORTH).func_235921_b_() && !state.get(SOUTH).func_235921_b_() && !state.get(EAST).func_235921_b_() && !state.get(WEST).func_235921_b_();
    }

    /**
     * performs updates on diagonal neighbors of the target position and passes in the flags. The flags can be
     * referenced from the docs for {@link IWorldWriter#setBlockState(IBlockState, BlockPos, int)}.
     */
    public void updateDiagonalNeighbors(BlockState state, IWorld worldIn, BlockPos pos, int flags, int recursionLeft)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            RedstoneSide redstoneside = state.get(FACING_PROPERTY_MAP.get(direction));

            if (redstoneside != RedstoneSide.NONE && !worldIn.getBlockState(blockpos$mutable.setAndMove(pos, direction)).isIn(this))
            {
                blockpos$mutable.move(Direction.DOWN);
                BlockState blockstate = worldIn.getBlockState(blockpos$mutable);

                if (!blockstate.isIn(Blocks.OBSERVER))
                {
                    BlockPos blockpos = blockpos$mutable.offset(direction.getOpposite());
                    BlockState blockstate1 = blockstate.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos), worldIn, blockpos$mutable, blockpos);
                    replaceBlockState(blockstate, blockstate1, worldIn, blockpos$mutable, flags, recursionLeft);
                }

                blockpos$mutable.setAndMove(pos, direction).move(Direction.UP);
                BlockState blockstate3 = worldIn.getBlockState(blockpos$mutable);

                if (!blockstate3.isIn(Blocks.OBSERVER))
                {
                    BlockPos blockpos1 = blockpos$mutable.offset(direction.getOpposite());
                    BlockState blockstate2 = blockstate3.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos1), worldIn, blockpos$mutable, blockpos1);
                    replaceBlockState(blockstate3, blockstate2, worldIn, blockpos$mutable, flags, recursionLeft);
                }
            }
        }
    }

    private RedstoneSide getSide(IBlockReader worldIn, BlockPos pos, Direction face)
    {
        return this.recalculateSide(worldIn, pos, face, !worldIn.getBlockState(pos.up()).isNormalCube(worldIn, pos));
    }

    private RedstoneSide recalculateSide(IBlockReader reader, BlockPos pos, Direction direction, boolean nonNormalCubeAbove)
    {
        BlockPos blockpos = pos.offset(direction);
        BlockState blockstate = reader.getBlockState(blockpos);

        if (nonNormalCubeAbove)
        {
            boolean flag = this.canPlaceOnTopOf(reader, blockpos, blockstate);

            if (flag && canConnectUpwardsTo(reader.getBlockState(blockpos.up())))
            {
                if (blockstate.isSolidSide(reader, blockpos, direction.getOpposite()))
                {
                    return RedstoneSide.UP;
                }

                return RedstoneSide.SIDE;
            }
        }

        return !canConnectTo(blockstate, direction) && (blockstate.isNormalCube(reader, blockpos) || !canConnectUpwardsTo(reader.getBlockState(blockpos.down()))) ? RedstoneSide.NONE : RedstoneSide.SIDE;
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return this.canPlaceOnTopOf(worldIn, blockpos, blockstate);
    }

    private boolean canPlaceOnTopOf(IBlockReader reader, BlockPos pos, BlockState state)
    {
        return state.isSolidSide(reader, pos, Direction.UP) || state.isIn(Blocks.HOPPER);
    }

    private void updatePower(World world, BlockPos pos, BlockState state)
    {
        int i = this.getStrongestSignal(world, pos);

        if (state.get(POWER) != i)
        {
            if (world.getBlockState(pos) == state)
            {
                world.setBlockState(pos, state.with(POWER, Integer.valueOf(i)), 2);
            }

            Set<BlockPos> set = Sets.newHashSet();
            set.add(pos);

            for (Direction direction : Direction.values())
            {
                set.add(pos.offset(direction));
            }

            for (BlockPos blockpos : set)
            {
                world.notifyNeighborsOfStateChange(blockpos, this);
            }
        }
    }

    private int getStrongestSignal(World world, BlockPos pos)
    {
        this.canProvidePower = false;
        int i = world.getRedstonePowerFromNeighbors(pos);
        this.canProvidePower = true;
        int j = 0;

        if (i < 15)
        {
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                BlockPos blockpos = pos.offset(direction);
                BlockState blockstate = world.getBlockState(blockpos);
                j = Math.max(j, this.getPower(blockstate));
                BlockPos blockpos1 = pos.up();

                if (blockstate.isNormalCube(world, blockpos) && !world.getBlockState(blockpos1).isNormalCube(world, blockpos1))
                {
                    j = Math.max(j, this.getPower(world.getBlockState(blockpos.up())));
                }
                else if (!blockstate.isNormalCube(world, blockpos))
                {
                    j = Math.max(j, this.getPower(world.getBlockState(blockpos.down())));
                }
            }
        }

        return Math.max(i, j - 1);
    }

    private int getPower(BlockState state)
    {
        return state.isIn(this) ? state.get(POWER) : 0;
    }

    /**
     * Calls World.notifyNeighborsOfStateChange() for all neighboring blocks, but only if the given block is a redstone
     * wire.
     */
    private void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos)
    {
        if (worldIn.getBlockState(pos).isIn(this))
        {
            worldIn.notifyNeighborsOfStateChange(pos, this);

            for (Direction direction : Direction.values())
            {
                worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }
        }
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (!oldState.isIn(state.getBlock()) && !worldIn.isRemote)
        {
            this.updatePower(worldIn, pos, state);

            for (Direction direction : Direction.Plane.VERTICAL)
            {
                worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }

            this.updateNeighboursStateChange(worldIn, pos);
        }
    }

    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!isMoving && !state.isIn(newState.getBlock()))
        {
            super.onReplaced(state, worldIn, pos, newState, isMoving);

            if (!worldIn.isRemote)
            {
                for (Direction direction : Direction.values())
                {
                    worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
                }

                this.updatePower(worldIn, pos, state);
                this.updateNeighboursStateChange(worldIn, pos);
            }
        }
    }

    private void updateNeighboursStateChange(World world, BlockPos pos)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            this.notifyWireNeighborsOfStateChange(world, pos.offset(direction));
        }

        for (Direction direction1 : Direction.Plane.HORIZONTAL)
        {
            BlockPos blockpos = pos.offset(direction1);

            if (world.getBlockState(blockpos).isNormalCube(world, blockpos))
            {
                this.notifyWireNeighborsOfStateChange(world, blockpos.up());
            }
            else
            {
                this.notifyWireNeighborsOfStateChange(world, blockpos.down());
            }
        }
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (!worldIn.isRemote)
        {
            if (state.isValidPosition(worldIn, pos))
            {
                this.updatePower(worldIn, pos, state);
            }
            else
            {
                spawnDrops(state, worldIn, pos);
                worldIn.removeBlock(pos, false);
            }
        }
    }

    /**
     * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return !this.canProvidePower ? 0 : blockState.getWeakPower(blockAccess, pos, side);
    }

    /**
     * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        if (this.canProvidePower && side != Direction.DOWN)
        {
            int i = blockState.get(POWER);

            if (i == 0)
            {
                return 0;
            }
            else
            {
                return side != Direction.UP && !this.getUpdatedState(blockAccess, blockState, pos).get(FACING_PROPERTY_MAP.get(side.getOpposite())).func_235921_b_() ? 0 : i;
            }
        }
        else
        {
            return 0;
        }
    }

    protected static boolean canConnectUpwardsTo(BlockState state)
    {
        return canConnectTo(state, (Direction)null);
    }

    protected static boolean canConnectTo(BlockState blockState, @Nullable Direction side)
    {
        if (blockState.isIn(Blocks.REDSTONE_WIRE))
        {
            return true;
        }
        else if (blockState.isIn(Blocks.REPEATER))
        {
            Direction direction = blockState.get(RepeaterBlock.HORIZONTAL_FACING);
            return direction == side || direction.getOpposite() == side;
        }
        else if (blockState.isIn(Blocks.OBSERVER))
        {
            return side == blockState.get(ObserverBlock.FACING);
        }
        else
        {
            return blockState.canProvidePower() && side != null;
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
     */
    public boolean canProvidePower(BlockState state)
    {
        return this.canProvidePower;
    }

    public static int getRGBByPower(int power)
    {
        Vector3f vector3f = powerRGB[power];
        return MathHelper.rgb(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    private void spawnPoweredParticle(World world, Random rand, BlockPos pos, Vector3f rgbVector, Direction directionFrom, Direction directionTo, float minChance, float maxChance)
    {
        float f = maxChance - minChance;

        if (!(rand.nextFloat() >= 0.2F * f))
        {
            float f1 = 0.4375F;
            float f2 = minChance + f * rand.nextFloat();
            double d0 = 0.5D + (double)(0.4375F * (float)directionFrom.getXOffset()) + (double)(f2 * (float)directionTo.getXOffset());
            double d1 = 0.5D + (double)(0.4375F * (float)directionFrom.getYOffset()) + (double)(f2 * (float)directionTo.getYOffset());
            double d2 = 0.5D + (double)(0.4375F * (float)directionFrom.getZOffset()) + (double)(f2 * (float)directionTo.getZOffset());
            world.addParticle(new RedstoneParticleData(rgbVector.getX(), rgbVector.getY(), rgbVector.getZ(), 1.0F), (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        int i = stateIn.get(POWER);

        if (i != 0)
        {
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                RedstoneSide redstoneside = stateIn.get(FACING_PROPERTY_MAP.get(direction));

                switch (redstoneside)
                {
                    case UP:
                        this.spawnPoweredParticle(worldIn, rand, pos, powerRGB[i], direction, Direction.UP, -0.5F, 0.5F);

                    case SIDE:
                        this.spawnPoweredParticle(worldIn, rand, pos, powerRGB[i], Direction.DOWN, direction, 0.0F, 0.5F);
                        break;

                    case NONE:
                    default:
                        this.spawnPoweredParticle(worldIn, rand, pos, powerRGB[i], Direction.DOWN, direction, 0.0F, 0.3F);
                }
            }
        }
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

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH, EAST, SOUTH, WEST, POWER);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (!player.abilities.allowEdit)
        {
            return ActionResultType.PASS;
        }
        else
        {
            if (areAllSidesValid(state) || areAllSidesInvalid(state))
            {
                BlockState blockstate = areAllSidesValid(state) ? this.getDefaultState() : this.sideBaseState;
                blockstate = blockstate.with(POWER, state.get(POWER));
                blockstate = this.getUpdatedState(worldIn, blockstate, pos);

                if (blockstate != state)
                {
                    worldIn.setBlockState(pos, blockstate, 3);
                    this.updateChangedConnections(worldIn, pos, state, blockstate);
                    return ActionResultType.SUCCESS;
                }
            }

            return ActionResultType.PASS;
        }
    }

    private void updateChangedConnections(World world, BlockPos pos, BlockState prevState, BlockState newState)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos blockpos = pos.offset(direction);

            if (prevState.get(FACING_PROPERTY_MAP.get(direction)).func_235921_b_() != newState.get(FACING_PROPERTY_MAP.get(direction)).func_235921_b_() && world.getBlockState(blockpos).isNormalCube(world, blockpos))
            {
                world.notifyNeighborsOfStateExcept(blockpos, newState.getBlock(), direction.getOpposite());
            }
        }
    }

    static
    {
        for (int i = 0; i <= 15; ++i)
        {
            float f = (float)i / 15.0F;
            float f1 = f * 0.6F + (f > 0.0F ? 0.4F : 0.3F);
            float f2 = MathHelper.clamp(f * f * 0.7F - 0.5F, 0.0F, 1.0F);
            float f3 = MathHelper.clamp(f * f * 0.6F - 0.7F, 0.0F, 1.0F);
            powerRGB[i] = new Vector3f(f1, f2, f3);
        }
    }
}
