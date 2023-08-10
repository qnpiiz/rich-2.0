package net.minecraft.block;

import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.Property;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class AbstractRailBlock extends Block
{
    protected static final VoxelShape FLAT_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    protected static final VoxelShape ASCENDING_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    private final boolean disableCorners;

    public static boolean isRail(World worldIn, BlockPos pos)
    {
        return isRail(worldIn.getBlockState(pos));
    }

    public static boolean isRail(BlockState state)
    {
        return state.isIn(BlockTags.RAILS) && state.getBlock() instanceof AbstractRailBlock;
    }

    protected AbstractRailBlock(boolean isDisableCorner, AbstractBlock.Properties builder)
    {
        super(builder);
        this.disableCorners = isDisableCorner;
    }

    public boolean areCornersDisabled()
    {
        return this.disableCorners;
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        RailShape railshape = state.isIn(this) ? state.get(this.getShapeProperty()) : null;
        return railshape != null && railshape.isAscending() ? ASCENDING_AABB : FLAT_AABB;
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return hasSolidSideOnTop(worldIn, pos.down());
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (!oldState.isIn(state.getBlock()))
        {
            this.updateRailState(state, worldIn, pos, isMoving);
        }
    }

    protected BlockState updateRailState(BlockState state, World world, BlockPos pos, boolean isMoving)
    {
        state = this.getUpdatedState(world, pos, state, true);

        if (this.disableCorners)
        {
            state.neighborChanged(world, pos, this, pos, isMoving);
        }

        return state;
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (!worldIn.isRemote && worldIn.getBlockState(pos).isIn(this))
        {
            RailShape railshape = state.get(this.getShapeProperty());

            if (isValidRailDirection(pos, worldIn, railshape))
            {
                spawnDrops(state, worldIn, pos);
                worldIn.removeBlock(pos, isMoving);
            }
            else
            {
                this.updateState(state, worldIn, pos, blockIn);
            }
        }
    }

    private static boolean isValidRailDirection(BlockPos pos, World world, RailShape railShape)
    {
        if (!hasSolidSideOnTop(world, pos.down()))
        {
            return true;
        }
        else
        {
            switch (railShape)
            {
                case ASCENDING_EAST:
                    return !hasSolidSideOnTop(world, pos.east());

                case ASCENDING_WEST:
                    return !hasSolidSideOnTop(world, pos.west());

                case ASCENDING_NORTH:
                    return !hasSolidSideOnTop(world, pos.north());

                case ASCENDING_SOUTH:
                    return !hasSolidSideOnTop(world, pos.south());

                default:
                    return false;
            }
        }
    }

    protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn)
    {
    }

    protected BlockState getUpdatedState(World worldIn, BlockPos pos, BlockState state, boolean placing)
    {
        if (worldIn.isRemote)
        {
            return state;
        }
        else
        {
            RailShape railshape = state.get(this.getShapeProperty());
            return (new RailState(worldIn, pos, state)).placeRail(worldIn.isBlockPowered(pos), placing, railshape).getNewState();
        }
    }

    /**
     * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
     */
    public PushReaction getPushReaction(BlockState state)
    {
        return PushReaction.NORMAL;
    }

    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!isMoving)
        {
            super.onReplaced(state, worldIn, pos, newState, isMoving);

            if (state.get(this.getShapeProperty()).isAscending())
            {
                worldIn.notifyNeighborsOfStateChange(pos.up(), this);
            }

            if (this.disableCorners)
            {
                worldIn.notifyNeighborsOfStateChange(pos, this);
                worldIn.notifyNeighborsOfStateChange(pos.down(), this);
            }
        }
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState blockstate = super.getDefaultState();
        Direction direction = context.getPlacementHorizontalFacing();
        boolean flag = direction == Direction.EAST || direction == Direction.WEST;
        return blockstate.with(this.getShapeProperty(), flag ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
    }

    public abstract Property<RailShape> getShapeProperty();
}
