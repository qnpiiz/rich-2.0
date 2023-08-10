package net.minecraft.block;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.CommandBlockMinecartEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DetectorRailBlock extends AbstractRailBlock
{
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public DetectorRailBlock(AbstractBlock.Properties properties)
    {
        super(true, properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, Boolean.valueOf(false)).with(SHAPE, RailShape.NORTH_SOUTH));
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
     */
    public boolean canProvidePower(BlockState state)
    {
        return true;
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (!worldIn.isRemote)
        {
            if (!state.get(POWERED))
            {
                this.updatePoweredState(worldIn, pos, state);
            }
        }
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (state.get(POWERED))
        {
            this.updatePoweredState(worldIn, pos, state);
        }
    }

    /**
     * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return blockState.get(POWERED) ? 15 : 0;
    }

    /**
     * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        if (!blockState.get(POWERED))
        {
            return 0;
        }
        else
        {
            return side == Direction.UP ? 15 : 0;
        }
    }

    private void updatePoweredState(World worldIn, BlockPos pos, BlockState state)
    {
        if (this.isValidPosition(state, worldIn, pos))
        {
            boolean flag = state.get(POWERED);
            boolean flag1 = false;
            List<AbstractMinecartEntity> list = this.findMinecarts(worldIn, pos, AbstractMinecartEntity.class, (Predicate<Entity>)null);

            if (!list.isEmpty())
            {
                flag1 = true;
            }

            if (flag1 && !flag)
            {
                BlockState blockstate = state.with(POWERED, Boolean.valueOf(true));
                worldIn.setBlockState(pos, blockstate, 3);
                this.updateConnectedRails(worldIn, pos, blockstate, true);
                worldIn.notifyNeighborsOfStateChange(pos, this);
                worldIn.notifyNeighborsOfStateChange(pos.down(), this);
                worldIn.markBlockRangeForRenderUpdate(pos, state, blockstate);
            }

            if (!flag1 && flag)
            {
                BlockState blockstate1 = state.with(POWERED, Boolean.valueOf(false));
                worldIn.setBlockState(pos, blockstate1, 3);
                this.updateConnectedRails(worldIn, pos, blockstate1, false);
                worldIn.notifyNeighborsOfStateChange(pos, this);
                worldIn.notifyNeighborsOfStateChange(pos.down(), this);
                worldIn.markBlockRangeForRenderUpdate(pos, state, blockstate1);
            }

            if (flag1)
            {
                worldIn.getPendingBlockTicks().scheduleTick(pos, this, 20);
            }

            worldIn.updateComparatorOutputLevel(pos, this);
        }
    }

    protected void updateConnectedRails(World worldIn, BlockPos pos, BlockState state, boolean powered)
    {
        RailState railstate = new RailState(worldIn, pos, state);

        for (BlockPos blockpos : railstate.getConnectedRails())
        {
            BlockState blockstate = worldIn.getBlockState(blockpos);
            blockstate.neighborChanged(worldIn, blockpos, blockstate.getBlock(), pos, false);
        }
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (!oldState.isIn(state.getBlock()))
        {
            this.updatePoweredState(worldIn, pos, this.updateRailState(state, worldIn, pos, isMoving));
        }
    }

    public Property<RailShape> getShapeProperty()
    {
        return SHAPE;
    }

    /**
     * @deprecated call via {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
     * is fine.
     */
    public boolean hasComparatorInputOverride(BlockState state)
    {
        return true;
    }

    /**
     * @deprecated call via {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
    {
        if (blockState.get(POWERED))
        {
            List<CommandBlockMinecartEntity> list = this.findMinecarts(worldIn, pos, CommandBlockMinecartEntity.class, (Predicate<Entity>)null);

            if (!list.isEmpty())
            {
                return list.get(0).getCommandBlockLogic().getSuccessCount();
            }

            List<AbstractMinecartEntity> list1 = this.findMinecarts(worldIn, pos, AbstractMinecartEntity.class, EntityPredicates.HAS_INVENTORY);

            if (!list1.isEmpty())
            {
                return Container.calcRedstoneFromInventory((IInventory)list1.get(0));
            }
        }

        return 0;
    }

    protected <T extends AbstractMinecartEntity> List<T> findMinecarts(World worldIn, BlockPos pos, Class<T> cartType, @Nullable Predicate<Entity> filter)
    {
        return worldIn.getEntitiesWithinAABB(cartType, this.getDectectionBox(pos), filter);
    }

    private AxisAlignedBB getDectectionBox(BlockPos pos)
    {
        double d0 = 0.2D;
        return new AxisAlignedBB((double)pos.getX() + 0.2D, (double)pos.getY(), (double)pos.getZ() + 0.2D, (double)(pos.getX() + 1) - 0.2D, (double)(pos.getY() + 1) - 0.2D, (double)(pos.getZ() + 1) - 0.2D);
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
                switch ((RailShape)state.get(SHAPE))
                {
                    case ASCENDING_EAST:
                        return state.with(SHAPE, RailShape.ASCENDING_WEST);

                    case ASCENDING_WEST:
                        return state.with(SHAPE, RailShape.ASCENDING_EAST);

                    case ASCENDING_NORTH:
                        return state.with(SHAPE, RailShape.ASCENDING_SOUTH);

                    case ASCENDING_SOUTH:
                        return state.with(SHAPE, RailShape.ASCENDING_NORTH);

                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);

                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);

                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);

                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);
                }

            case COUNTERCLOCKWISE_90:
                switch ((RailShape)state.get(SHAPE))
                {
                    case ASCENDING_EAST:
                        return state.with(SHAPE, RailShape.ASCENDING_NORTH);

                    case ASCENDING_WEST:
                        return state.with(SHAPE, RailShape.ASCENDING_SOUTH);

                    case ASCENDING_NORTH:
                        return state.with(SHAPE, RailShape.ASCENDING_WEST);

                    case ASCENDING_SOUTH:
                        return state.with(SHAPE, RailShape.ASCENDING_EAST);

                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);

                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);

                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);

                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);

                    case NORTH_SOUTH:
                        return state.with(SHAPE, RailShape.EAST_WEST);

                    case EAST_WEST:
                        return state.with(SHAPE, RailShape.NORTH_SOUTH);
                }

            case CLOCKWISE_90:
                switch ((RailShape)state.get(SHAPE))
                {
                    case ASCENDING_EAST:
                        return state.with(SHAPE, RailShape.ASCENDING_SOUTH);

                    case ASCENDING_WEST:
                        return state.with(SHAPE, RailShape.ASCENDING_NORTH);

                    case ASCENDING_NORTH:
                        return state.with(SHAPE, RailShape.ASCENDING_EAST);

                    case ASCENDING_SOUTH:
                        return state.with(SHAPE, RailShape.ASCENDING_WEST);

                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);

                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);

                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);

                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);

                    case NORTH_SOUTH:
                        return state.with(SHAPE, RailShape.EAST_WEST);

                    case EAST_WEST:
                        return state.with(SHAPE, RailShape.NORTH_SOUTH);
                }

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
        RailShape railshape = state.get(SHAPE);

        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                switch (railshape)
                {
                    case ASCENDING_NORTH:
                        return state.with(SHAPE, RailShape.ASCENDING_SOUTH);

                    case ASCENDING_SOUTH:
                        return state.with(SHAPE, RailShape.ASCENDING_NORTH);

                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);

                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);

                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);

                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);

                    default:
                        return super.mirror(state, mirrorIn);
                }

            case FRONT_BACK:
                switch (railshape)
                {
                    case ASCENDING_EAST:
                        return state.with(SHAPE, RailShape.ASCENDING_WEST);

                    case ASCENDING_WEST:
                        return state.with(SHAPE, RailShape.ASCENDING_EAST);

                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;

                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);

                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);

                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);

                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);
                }
        }

        return super.mirror(state, mirrorIn);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(SHAPE, POWERED);
    }
}
