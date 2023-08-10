package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractButtonBlock extends HorizontalFaceBlock
{
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    protected static final VoxelShape AABB_CEILING_X_OFF = Block.makeCuboidShape(6.0D, 14.0D, 5.0D, 10.0D, 16.0D, 11.0D);
    protected static final VoxelShape AABB_CEILING_Z_OFF = Block.makeCuboidShape(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
    protected static final VoxelShape AABB_FLOOR_X_OFF = Block.makeCuboidShape(6.0D, 0.0D, 5.0D, 10.0D, 2.0D, 11.0D);
    protected static final VoxelShape AABB_FLOOR_Z_OFF = Block.makeCuboidShape(5.0D, 0.0D, 6.0D, 11.0D, 2.0D, 10.0D);
    protected static final VoxelShape AABB_NORTH_OFF = Block.makeCuboidShape(5.0D, 6.0D, 14.0D, 11.0D, 10.0D, 16.0D);
    protected static final VoxelShape AABB_SOUTH_OFF = Block.makeCuboidShape(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 2.0D);
    protected static final VoxelShape AABB_WEST_OFF = Block.makeCuboidShape(14.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
    protected static final VoxelShape AABB_EAST_OFF = Block.makeCuboidShape(0.0D, 6.0D, 5.0D, 2.0D, 10.0D, 11.0D);
    protected static final VoxelShape AABB_CEILING_X_ON = Block.makeCuboidShape(6.0D, 15.0D, 5.0D, 10.0D, 16.0D, 11.0D);
    protected static final VoxelShape AABB_CEILING_Z_ON = Block.makeCuboidShape(5.0D, 15.0D, 6.0D, 11.0D, 16.0D, 10.0D);
    protected static final VoxelShape AABB_FLOOR_X_ON = Block.makeCuboidShape(6.0D, 0.0D, 5.0D, 10.0D, 1.0D, 11.0D);
    protected static final VoxelShape AABB_FLOOR_Z_ON = Block.makeCuboidShape(5.0D, 0.0D, 6.0D, 11.0D, 1.0D, 10.0D);
    protected static final VoxelShape AABB_NORTH_ON = Block.makeCuboidShape(5.0D, 6.0D, 15.0D, 11.0D, 10.0D, 16.0D);
    protected static final VoxelShape AABB_SOUTH_ON = Block.makeCuboidShape(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 1.0D);
    protected static final VoxelShape AABB_WEST_ON = Block.makeCuboidShape(15.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
    protected static final VoxelShape AABB_EAST_ON = Block.makeCuboidShape(0.0D, 6.0D, 5.0D, 1.0D, 10.0D, 11.0D);
    private final boolean wooden;

    protected AbstractButtonBlock(boolean isWooden, AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(FACE, AttachFace.WALL));
        this.wooden = isWooden;
    }

    private int getActiveDuration()
    {
        return this.wooden ? 30 : 20;
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        Direction direction = state.get(HORIZONTAL_FACING);
        boolean flag = state.get(POWERED);

        switch ((AttachFace)state.get(FACE))
        {
            case FLOOR:
                if (direction.getAxis() == Direction.Axis.X)
                {
                    return flag ? AABB_FLOOR_X_ON : AABB_FLOOR_X_OFF;
                }

                return flag ? AABB_FLOOR_Z_ON : AABB_FLOOR_Z_OFF;

            case WALL:
                switch (direction)
                {
                    case EAST:
                        return flag ? AABB_EAST_ON : AABB_EAST_OFF;

                    case WEST:
                        return flag ? AABB_WEST_ON : AABB_WEST_OFF;

                    case SOUTH:
                        return flag ? AABB_SOUTH_ON : AABB_SOUTH_OFF;

                    case NORTH:
                    default:
                        return flag ? AABB_NORTH_ON : AABB_NORTH_OFF;
                }

            case CEILING:
            default:
                if (direction.getAxis() == Direction.Axis.X)
                {
                    return flag ? AABB_CEILING_X_ON : AABB_CEILING_X_OFF;
                }
                else
                {
                    return flag ? AABB_CEILING_Z_ON : AABB_CEILING_Z_OFF;
                }
        }
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (state.get(POWERED))
        {
            return ActionResultType.CONSUME;
        }
        else
        {
            this.powerBlock(state, worldIn, pos);
            this.playSound(player, worldIn, pos, true);
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
    }

    public void powerBlock(BlockState state, World world, BlockPos pos)
    {
        world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(true)), 3);
        this.updateNeighbors(state, world, pos);
        world.getPendingBlockTicks().scheduleTick(pos, this, this.getActiveDuration());
    }

    protected void playSound(@Nullable PlayerEntity playerIn, IWorld worldIn, BlockPos pos, boolean hitByArrow)
    {
        worldIn.playSound(hitByArrow ? playerIn : null, pos, this.getSoundEvent(hitByArrow), SoundCategory.BLOCKS, 0.3F, hitByArrow ? 0.6F : 0.5F);
    }

    protected abstract SoundEvent getSoundEvent(boolean isOn);

    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!isMoving && !state.isIn(newState.getBlock()))
        {
            if (state.get(POWERED))
            {
                this.updateNeighbors(state, worldIn, pos);
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
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
        return blockState.get(POWERED) && getFacing(blockState) == side ? 15 : 0;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
     */
    public boolean canProvidePower(BlockState state)
    {
        return true;
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (state.get(POWERED))
        {
            if (this.wooden)
            {
                this.checkPressed(state, worldIn, pos);
            }
            else
            {
                worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(false)), 3);
                this.updateNeighbors(state, worldIn, pos);
                this.playSound((PlayerEntity)null, worldIn, pos, false);
            }
        }
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (!worldIn.isRemote && this.wooden && !state.get(POWERED))
        {
            this.checkPressed(state, worldIn, pos);
        }
    }

    private void checkPressed(BlockState state, World worldIn, BlockPos pos)
    {
        List <? extends Entity > list = worldIn.getEntitiesWithinAABB(AbstractArrowEntity.class, state.getShape(worldIn, pos).getBoundingBox().offset(pos));
        boolean flag = !list.isEmpty();
        boolean flag1 = state.get(POWERED);

        if (flag != flag1)
        {
            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag)), 3);
            this.updateNeighbors(state, worldIn, pos);
            this.playSound((PlayerEntity)null, worldIn, pos, flag);
        }

        if (flag)
        {
            worldIn.getPendingBlockTicks().scheduleTick(new BlockPos(pos), this, this.getActiveDuration());
        }
    }

    private void updateNeighbors(BlockState state, World worldIn, BlockPos pos)
    {
        worldIn.notifyNeighborsOfStateChange(pos, this);
        worldIn.notifyNeighborsOfStateChange(pos.offset(getFacing(state).getOpposite()), this);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING, POWERED, FACE);
    }
}
