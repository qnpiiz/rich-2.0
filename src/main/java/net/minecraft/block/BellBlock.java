package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BellAttachment;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BellBlock extends ContainerBlock
{
    public static final DirectionProperty HORIZONTAL_FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final EnumProperty<BellAttachment> ATTACHMENT = BlockStateProperties.BELL_ATTACHMENT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final VoxelShape FLOOR_NORTH_SOUTH_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
    private static final VoxelShape FLOOR_EAST_WEST_SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
    private static final VoxelShape BELL_CUP_SHAPE = Block.makeCuboidShape(5.0D, 6.0D, 5.0D, 11.0D, 13.0D, 11.0D);
    private static final VoxelShape BELL_RIM_SHAPE = Block.makeCuboidShape(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D);
    private static final VoxelShape BASE_WALL_SHAPE = VoxelShapes.or(BELL_RIM_SHAPE, BELL_CUP_SHAPE);
    private static final VoxelShape DOUBLE_WALL_NORTH_SOUTH_SHAPE = VoxelShapes.or(BASE_WALL_SHAPE, Block.makeCuboidShape(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 16.0D));
    private static final VoxelShape DOUBLE_WALL_EAST_WEST_SHAPE = VoxelShapes.or(BASE_WALL_SHAPE, Block.makeCuboidShape(0.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
    private static final VoxelShape WEST_FACING_WALL_SHAPE = VoxelShapes.or(BASE_WALL_SHAPE, Block.makeCuboidShape(0.0D, 13.0D, 7.0D, 13.0D, 15.0D, 9.0D));
    private static final VoxelShape EAST_FACING_WALL_SHAPE = VoxelShapes.or(BASE_WALL_SHAPE, Block.makeCuboidShape(3.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
    private static final VoxelShape NORTH_FACING_WALL_SHAPE = VoxelShapes.or(BASE_WALL_SHAPE, Block.makeCuboidShape(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 13.0D));
    private static final VoxelShape SOUTH_FACING_WALL_SHAPE = VoxelShapes.or(BASE_WALL_SHAPE, Block.makeCuboidShape(7.0D, 13.0D, 3.0D, 9.0D, 15.0D, 16.0D));
    private static final VoxelShape CEILING_SHAPE = VoxelShapes.or(BASE_WALL_SHAPE, Block.makeCuboidShape(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D));

    public BellBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(ATTACHMENT, BellAttachment.FLOOR).with(POWERED, Boolean.valueOf(false)));
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        boolean flag = worldIn.isBlockPowered(pos);

        if (flag != state.get(POWERED))
        {
            if (flag)
            {
                this.ring(worldIn, pos, (Direction)null);
            }

            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag)), 3);
        }
    }

    public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile)
    {
        Entity entity = projectile.func_234616_v_();
        PlayerEntity playerentity = entity instanceof PlayerEntity ? (PlayerEntity)entity : null;
        this.attemptRing(worldIn, state, hit, playerentity, true);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        return this.attemptRing(worldIn, state, hit, player, true) ? ActionResultType.func_233537_a_(worldIn.isRemote) : ActionResultType.PASS;
    }

    public boolean attemptRing(World world, BlockState state, BlockRayTraceResult result, @Nullable PlayerEntity player, boolean canRingBell)
    {
        Direction direction = result.getFace();
        BlockPos blockpos = result.getPos();
        boolean flag = !canRingBell || this.canRingFrom(state, direction, result.getHitVec().y - (double)blockpos.getY());

        if (flag)
        {
            boolean flag1 = this.ring(world, blockpos, direction);

            if (flag1 && player != null)
            {
                player.addStat(Stats.BELL_RING);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns true if the bell can be rung from the given side and vertical position. For example, bells attached to
     * their northern neighbor cannot be rung from the south face, since it can't swing north-south.
     */
    private boolean canRingFrom(BlockState pos, Direction directionIn, double distanceY)
    {
        if (directionIn.getAxis() != Direction.Axis.Y && !(distanceY > (double)0.8124F))
        {
            Direction direction = pos.get(HORIZONTAL_FACING);
            BellAttachment bellattachment = pos.get(ATTACHMENT);

            switch (bellattachment)
            {
                case FLOOR:
                    return direction.getAxis() == directionIn.getAxis();

                case SINGLE_WALL:
                case DOUBLE_WALL:
                    return direction.getAxis() != directionIn.getAxis();

                case CEILING:
                    return true;

                default:
                    return false;
            }
        }
        else
        {
            return false;
        }
    }

    public boolean ring(World world, BlockPos pos, @Nullable Direction direction)
    {
        TileEntity tileentity = world.getTileEntity(pos);

        if (!world.isRemote && tileentity instanceof BellTileEntity)
        {
            if (direction == null)
            {
                direction = world.getBlockState(pos).get(HORIZONTAL_FACING);
            }

            ((BellTileEntity)tileentity).ring(direction);
            world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0F, 1.0F);
            return true;
        }
        else
        {
            return false;
        }
    }

    private VoxelShape getShape(BlockState state)
    {
        Direction direction = state.get(HORIZONTAL_FACING);
        BellAttachment bellattachment = state.get(ATTACHMENT);

        if (bellattachment == BellAttachment.FLOOR)
        {
            return direction != Direction.NORTH && direction != Direction.SOUTH ? FLOOR_EAST_WEST_SHAPE : FLOOR_NORTH_SOUTH_SHAPE;
        }
        else if (bellattachment == BellAttachment.CEILING)
        {
            return CEILING_SHAPE;
        }
        else if (bellattachment == BellAttachment.DOUBLE_WALL)
        {
            return direction != Direction.NORTH && direction != Direction.SOUTH ? DOUBLE_WALL_EAST_WEST_SHAPE : DOUBLE_WALL_NORTH_SOUTH_SHAPE;
        }
        else if (direction == Direction.NORTH)
        {
            return NORTH_FACING_WALL_SHAPE;
        }
        else if (direction == Direction.SOUTH)
        {
            return SOUTH_FACING_WALL_SHAPE;
        }
        else
        {
            return direction == Direction.EAST ? EAST_FACING_WALL_SHAPE : WEST_FACING_WALL_SHAPE;
        }
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.getShape(state);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.getShape(state);
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
     */
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Direction direction = context.getFace();
        BlockPos blockpos = context.getPos();
        World world = context.getWorld();
        Direction.Axis direction$axis = direction.getAxis();

        if (direction$axis == Direction.Axis.Y)
        {
            BlockState blockstate = this.getDefaultState().with(ATTACHMENT, direction == Direction.DOWN ? BellAttachment.CEILING : BellAttachment.FLOOR).with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing());

            if (blockstate.isValidPosition(context.getWorld(), blockpos))
            {
                return blockstate;
            }
        }
        else
        {
            boolean flag = direction$axis == Direction.Axis.X && world.getBlockState(blockpos.west()).isSolidSide(world, blockpos.west(), Direction.EAST) && world.getBlockState(blockpos.east()).isSolidSide(world, blockpos.east(), Direction.WEST) || direction$axis == Direction.Axis.Z && world.getBlockState(blockpos.north()).isSolidSide(world, blockpos.north(), Direction.SOUTH) && world.getBlockState(blockpos.south()).isSolidSide(world, blockpos.south(), Direction.NORTH);
            BlockState blockstate1 = this.getDefaultState().with(HORIZONTAL_FACING, direction.getOpposite()).with(ATTACHMENT, flag ? BellAttachment.DOUBLE_WALL : BellAttachment.SINGLE_WALL);

            if (blockstate1.isValidPosition(context.getWorld(), context.getPos()))
            {
                return blockstate1;
            }

            boolean flag1 = world.getBlockState(blockpos.down()).isSolidSide(world, blockpos.down(), Direction.UP);
            blockstate1 = blockstate1.with(ATTACHMENT, flag1 ? BellAttachment.FLOOR : BellAttachment.CEILING);

            if (blockstate1.isValidPosition(context.getWorld(), context.getPos()))
            {
                return blockstate1;
            }
        }

        return null;
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder
     * immediately returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        BellAttachment bellattachment = stateIn.get(ATTACHMENT);
        Direction direction = getDirectionFromState(stateIn).getOpposite();

        if (direction == facing && !stateIn.isValidPosition(worldIn, currentPos) && bellattachment != BellAttachment.DOUBLE_WALL)
        {
            return Blocks.AIR.getDefaultState();
        }
        else
        {
            if (facing.getAxis() == stateIn.get(HORIZONTAL_FACING).getAxis())
            {
                if (bellattachment == BellAttachment.DOUBLE_WALL && !facingState.isSolidSide(worldIn, facingPos, facing))
                {
                    return stateIn.with(ATTACHMENT, BellAttachment.SINGLE_WALL).with(HORIZONTAL_FACING, facing.getOpposite());
                }

                if (bellattachment == BellAttachment.SINGLE_WALL && direction.getOpposite() == facing && facingState.isSolidSide(worldIn, facingPos, stateIn.get(HORIZONTAL_FACING)))
                {
                    return stateIn.with(ATTACHMENT, BellAttachment.DOUBLE_WALL);
                }
            }

            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        Direction direction = getDirectionFromState(state).getOpposite();
        return direction == Direction.UP ? Block.hasEnoughSolidSide(worldIn, pos.up(), Direction.DOWN) : HorizontalFaceBlock.isSideSolidForDirection(worldIn, pos, direction);
    }

    private static Direction getDirectionFromState(BlockState state)
    {
        switch ((BellAttachment)state.get(ATTACHMENT))
        {
            case FLOOR:
                return Direction.UP;

            case CEILING:
                return Direction.DOWN;

            default:
                return state.get(HORIZONTAL_FACING).getOpposite();
        }
    }

    /**
     * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
     */
    public PushReaction getPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING, ATTACHMENT, POWERED);
    }

    @Nullable
    public TileEntity createNewTileEntity(IBlockReader worldIn)
    {
        return new BellTileEntity();
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    }
}
