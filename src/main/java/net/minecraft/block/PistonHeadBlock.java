package net.minecraft.block;

import java.util.Arrays;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class PistonHeadBlock extends DirectionalBlock
{
    public static final EnumProperty<PistonType> TYPE = BlockStateProperties.PISTON_TYPE;
    public static final BooleanProperty SHORT = BlockStateProperties.SHORT;
    protected static final VoxelShape PISTON_EXTENSION_EAST_AABB = Block.makeCuboidShape(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape PISTON_EXTENSION_WEST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
    protected static final VoxelShape PISTON_EXTENSION_SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape PISTON_EXTENSION_NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
    protected static final VoxelShape PISTON_EXTENSION_UP_AABB = Block.makeCuboidShape(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape PISTON_EXTENSION_DOWN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
    protected static final VoxelShape UP_ARM_AABB = Block.makeCuboidShape(6.0D, -4.0D, 6.0D, 10.0D, 12.0D, 10.0D);
    protected static final VoxelShape DOWN_ARM_AABB = Block.makeCuboidShape(6.0D, 4.0D, 6.0D, 10.0D, 20.0D, 10.0D);
    protected static final VoxelShape SOUTH_ARM_AABB = Block.makeCuboidShape(6.0D, 6.0D, -4.0D, 10.0D, 10.0D, 12.0D);
    protected static final VoxelShape NORTH_ARM_AABB = Block.makeCuboidShape(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 20.0D);
    protected static final VoxelShape EAST_ARM_AABB = Block.makeCuboidShape(-4.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    protected static final VoxelShape WEST_ARM_AABB = Block.makeCuboidShape(4.0D, 6.0D, 6.0D, 20.0D, 10.0D, 10.0D);
    protected static final VoxelShape SHORT_UP_ARM_AABB = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
    protected static final VoxelShape SHORT_DOWN_ARM_AABB = Block.makeCuboidShape(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    protected static final VoxelShape SHORT_SOUTH_ARM_AABB = Block.makeCuboidShape(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 12.0D);
    protected static final VoxelShape SHORT_NORTH_ARM_AABB = Block.makeCuboidShape(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 16.0D);
    protected static final VoxelShape SHORT_EAST_ARM_AABB = Block.makeCuboidShape(0.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    protected static final VoxelShape SHORT_WEST_ARM_AABB = Block.makeCuboidShape(4.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    private static final VoxelShape[] EXTENDED_SHAPES = getShapesForExtension(true);
    private static final VoxelShape[] UNEXTENDED_SHAPES = getShapesForExtension(false);

    private static VoxelShape[] getShapesForExtension(boolean extended)
    {
        return Arrays.stream(Direction.values()).map((direction) ->
        {
            return getShapeForDirection(direction, extended);
        }).toArray((id) ->
        {
            return new VoxelShape[id];
        });
    }

    private static VoxelShape getShapeForDirection(Direction direction, boolean shortArm)
    {
        switch (direction)
        {
            case DOWN:
            default:
                return VoxelShapes.or(PISTON_EXTENSION_DOWN_AABB, shortArm ? SHORT_DOWN_ARM_AABB : DOWN_ARM_AABB);

            case UP:
                return VoxelShapes.or(PISTON_EXTENSION_UP_AABB, shortArm ? SHORT_UP_ARM_AABB : UP_ARM_AABB);

            case NORTH:
                return VoxelShapes.or(PISTON_EXTENSION_NORTH_AABB, shortArm ? SHORT_NORTH_ARM_AABB : NORTH_ARM_AABB);

            case SOUTH:
                return VoxelShapes.or(PISTON_EXTENSION_SOUTH_AABB, shortArm ? SHORT_SOUTH_ARM_AABB : SOUTH_ARM_AABB);

            case WEST:
                return VoxelShapes.or(PISTON_EXTENSION_WEST_AABB, shortArm ? SHORT_WEST_ARM_AABB : WEST_ARM_AABB);

            case EAST:
                return VoxelShapes.or(PISTON_EXTENSION_EAST_AABB, shortArm ? SHORT_EAST_ARM_AABB : EAST_ARM_AABB);
        }
    }

    public PistonHeadBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(TYPE, PistonType.DEFAULT).with(SHORT, Boolean.valueOf(false)));
    }

    public boolean isTransparent(BlockState state)
    {
        return true;
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return (state.get(SHORT) ? EXTENDED_SHAPES : UNEXTENDED_SHAPES)[state.get(FACING).ordinal()];
    }

    private boolean isExtended(BlockState baseState, BlockState extendedState)
    {
        Block block = baseState.get(TYPE) == PistonType.DEFAULT ? Blocks.PISTON : Blocks.STICKY_PISTON;
        return extendedState.isIn(block) && extendedState.get(PistonBlock.EXTENDED) && extendedState.get(FACING) == baseState.get(FACING);
    }

    /**
     * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually
     * collect this block
     */
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (!worldIn.isRemote && player.abilities.isCreativeMode)
        {
            BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());

            if (this.isExtended(state, worldIn.getBlockState(blockpos)))
            {
                worldIn.destroyBlock(blockpos, false);
            }
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.isIn(newState.getBlock()))
        {
            super.onReplaced(state, worldIn, pos, newState, isMoving);
            BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());

            if (this.isExtended(state, worldIn.getBlockState(blockpos)))
            {
                worldIn.destroyBlock(blockpos, true);
            }
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
        return facing.getOpposite() == stateIn.get(FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState blockstate = worldIn.getBlockState(pos.offset(state.get(FACING).getOpposite()));
        return this.isExtended(state, blockstate) || blockstate.isIn(Blocks.MOVING_PISTON) && blockstate.get(FACING) == state.get(FACING);
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (state.isValidPosition(worldIn, pos))
        {
            BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());
            worldIn.getBlockState(blockpos).neighborChanged(worldIn, blockpos, blockIn, fromPos, false);
        }
    }

    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
        return new ItemStack(state.get(TYPE) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, TYPE, SHORT);
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    }
}
