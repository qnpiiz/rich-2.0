package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.GrindstoneContainer;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class GrindstoneBlock extends HorizontalFaceBlock
{
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_LEG_1 = Block.makeCuboidShape(2.0D, 0.0D, 6.0D, 4.0D, 7.0D, 10.0D);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_LEG_2 = Block.makeCuboidShape(12.0D, 0.0D, 6.0D, 14.0D, 7.0D, 10.0D);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_HOLDER_1 = Block.makeCuboidShape(2.0D, 7.0D, 5.0D, 4.0D, 13.0D, 11.0D);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_HOLDER_2 = Block.makeCuboidShape(12.0D, 7.0D, 5.0D, 14.0D, 13.0D, 11.0D);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_FLOOR_NORTH_SOUTH_LEG_1, SHAPE_FLOOR_NORTH_SOUTH_HOLDER_1);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_FLOOR_NORTH_SOUTH_LEG_2, SHAPE_FLOOR_NORTH_SOUTH_HOLDER_2);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_COMBINED = VoxelShapes.or(SHAPE_FLOOR_NORTH_SOUTH_LEG_HOLDER_COMBINED_1, SHAPE_FLOOR_NORTH_SOUTH_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH = VoxelShapes.or(SHAPE_FLOOR_NORTH_SOUTH_COMBINED, Block.makeCuboidShape(4.0D, 4.0D, 2.0D, 12.0D, 16.0D, 14.0D));
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_LEG_1 = Block.makeCuboidShape(6.0D, 0.0D, 2.0D, 10.0D, 7.0D, 4.0D);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_LEG_2 = Block.makeCuboidShape(6.0D, 0.0D, 12.0D, 10.0D, 7.0D, 14.0D);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_HOLDER_1 = Block.makeCuboidShape(5.0D, 7.0D, 2.0D, 11.0D, 13.0D, 4.0D);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_HOLDER_2 = Block.makeCuboidShape(5.0D, 7.0D, 12.0D, 11.0D, 13.0D, 14.0D);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_FLOOR_EAST_WEST_LEG_1, SHAPE_FLOOR_EAST_WEST_HOLDER_1);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_FLOOR_EAST_WEST_LEG_2, SHAPE_FLOOR_EAST_WEST_HOLDER_2);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_COMBINED = VoxelShapes.or(SHAPE_FLOOR_EAST_WEST_LEG_HOLDER_COMBINED_1, SHAPE_FLOOR_EAST_WEST_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST = VoxelShapes.or(SHAPE_FLOOR_EAST_WEST_COMBINED, Block.makeCuboidShape(2.0D, 4.0D, 4.0D, 14.0D, 16.0D, 12.0D));
    public static final VoxelShape SHAPE_WALL_SOUTH_LEG_1 = Block.makeCuboidShape(2.0D, 6.0D, 0.0D, 4.0D, 10.0D, 7.0D);
    public static final VoxelShape SHAPE_WALL_SOUTH_LEG_2 = Block.makeCuboidShape(12.0D, 6.0D, 0.0D, 14.0D, 10.0D, 7.0D);
    public static final VoxelShape SHAPE_WALL_SOUTH_HOLDER_1 = Block.makeCuboidShape(2.0D, 5.0D, 7.0D, 4.0D, 11.0D, 13.0D);
    public static final VoxelShape SHAPE_WALL_SOUTH_HOLDER_2 = Block.makeCuboidShape(12.0D, 5.0D, 7.0D, 14.0D, 11.0D, 13.0D);
    public static final VoxelShape SHAPE_WALL_SOUTH_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_WALL_SOUTH_LEG_1, SHAPE_WALL_SOUTH_HOLDER_1);
    public static final VoxelShape SHAPE_WALL_SOUTH_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_WALL_SOUTH_LEG_2, SHAPE_WALL_SOUTH_HOLDER_2);
    public static final VoxelShape SHAPE_WALL_SOUTH_COMBINED = VoxelShapes.or(SHAPE_WALL_SOUTH_LEG_HOLDER_COMBINED_1, SHAPE_WALL_SOUTH_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_WALL_SOUTH = VoxelShapes.or(SHAPE_WALL_SOUTH_COMBINED, Block.makeCuboidShape(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 16.0D));
    public static final VoxelShape SHAPE_WALL_NORTH_LEG_1 = Block.makeCuboidShape(2.0D, 6.0D, 7.0D, 4.0D, 10.0D, 16.0D);
    public static final VoxelShape SHAPE_WALL_NORTH_LEG_2 = Block.makeCuboidShape(12.0D, 6.0D, 7.0D, 14.0D, 10.0D, 16.0D);
    public static final VoxelShape SHAPE_WALL_NORTH_HOLDER_1 = Block.makeCuboidShape(2.0D, 5.0D, 3.0D, 4.0D, 11.0D, 9.0D);
    public static final VoxelShape SHAPE_WALL_NORTH_HOLDER_2 = Block.makeCuboidShape(12.0D, 5.0D, 3.0D, 14.0D, 11.0D, 9.0D);
    public static final VoxelShape SHAPE_WALL_NORTH_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_WALL_NORTH_LEG_1, SHAPE_WALL_NORTH_HOLDER_1);
    public static final VoxelShape SHAPE_WALL_NORTH_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_WALL_NORTH_LEG_2, SHAPE_WALL_NORTH_HOLDER_2);
    public static final VoxelShape SHAPE_WALL_NORTH_COMBINED = VoxelShapes.or(SHAPE_WALL_NORTH_LEG_HOLDER_COMBINED_1, SHAPE_WALL_NORTH_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_WALL_NORTH = VoxelShapes.or(SHAPE_WALL_NORTH_COMBINED, Block.makeCuboidShape(4.0D, 2.0D, 0.0D, 12.0D, 14.0D, 12.0D));
    public static final VoxelShape SHAPE_WALL_WEST_LEG_1 = Block.makeCuboidShape(7.0D, 6.0D, 2.0D, 16.0D, 10.0D, 4.0D);
    public static final VoxelShape SHAPE_WALL_WEST_LEG_2 = Block.makeCuboidShape(7.0D, 6.0D, 12.0D, 16.0D, 10.0D, 14.0D);
    public static final VoxelShape SHAPE_WALL_WEST_HOLDER_1 = Block.makeCuboidShape(3.0D, 5.0D, 2.0D, 9.0D, 11.0D, 4.0D);
    public static final VoxelShape SHAPE_WALL_WEST_HOLDER_2 = Block.makeCuboidShape(3.0D, 5.0D, 12.0D, 9.0D, 11.0D, 14.0D);
    public static final VoxelShape SHAPE_WALL_WEST_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_WALL_WEST_LEG_1, SHAPE_WALL_WEST_HOLDER_1);
    public static final VoxelShape SHAPE_WALL_WEST_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_WALL_WEST_LEG_2, SHAPE_WALL_WEST_HOLDER_2);
    public static final VoxelShape SHAPE_WALL_WEST_COMBINED = VoxelShapes.or(SHAPE_WALL_WEST_LEG_HOLDER_COMBINED_1, SHAPE_WALL_WEST_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_WALL_WEST = VoxelShapes.or(SHAPE_WALL_WEST_COMBINED, Block.makeCuboidShape(0.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D));
    public static final VoxelShape SHAPE_WALL_EAST_LEG_1 = Block.makeCuboidShape(0.0D, 6.0D, 2.0D, 9.0D, 10.0D, 4.0D);
    public static final VoxelShape SHAPE_WALL_EAST_LEG_2 = Block.makeCuboidShape(0.0D, 6.0D, 12.0D, 9.0D, 10.0D, 14.0D);
    public static final VoxelShape SHAPE_WALL_EAST_HOLDER_1 = Block.makeCuboidShape(7.0D, 5.0D, 2.0D, 13.0D, 11.0D, 4.0D);
    public static final VoxelShape SHAPE_WALL_EAST_HOLDER_2 = Block.makeCuboidShape(7.0D, 5.0D, 12.0D, 13.0D, 11.0D, 14.0D);
    public static final VoxelShape SHAPE_WALL_EAST_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_WALL_EAST_LEG_1, SHAPE_WALL_EAST_HOLDER_1);
    public static final VoxelShape SHAPE_WALL_EAST_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_WALL_EAST_LEG_2, SHAPE_WALL_EAST_HOLDER_2);
    public static final VoxelShape SHAPE_WALL_EAST_COMBINED = VoxelShapes.or(SHAPE_WALL_EAST_LEG_HOLDER_COMBINED_1, SHAPE_WALL_EAST_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_WALL_EAST = VoxelShapes.or(SHAPE_WALL_EAST_COMBINED, Block.makeCuboidShape(4.0D, 2.0D, 4.0D, 16.0D, 14.0D, 12.0D));
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_LEG_1 = Block.makeCuboidShape(2.0D, 9.0D, 6.0D, 4.0D, 16.0D, 10.0D);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_LEG_2 = Block.makeCuboidShape(12.0D, 9.0D, 6.0D, 14.0D, 16.0D, 10.0D);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_HOLDER_1 = Block.makeCuboidShape(2.0D, 3.0D, 5.0D, 4.0D, 9.0D, 11.0D);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_HOLDER_2 = Block.makeCuboidShape(12.0D, 3.0D, 5.0D, 14.0D, 9.0D, 11.0D);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_CEILING_NORTH_OR_SOUTH_LEG_1, SHAPE_CEILING_NORTH_OR_SOUTH_HOLDER_1);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_CEILING_NORTH_OR_SOUTH_LEG_2, SHAPE_CEILING_NORTH_OR_SOUTH_HOLDER_2);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_COMBINED = VoxelShapes.or(SHAPE_CEILING_NORTH_OR_SOUTH_LEG_HOLDER_COMBINED_1, SHAPE_CEILING_NORTH_OR_SOUTH_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH = VoxelShapes.or(SHAPE_CEILING_NORTH_OR_SOUTH_COMBINED, Block.makeCuboidShape(4.0D, 0.0D, 2.0D, 12.0D, 12.0D, 14.0D));
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_LEG_1 = Block.makeCuboidShape(6.0D, 9.0D, 2.0D, 10.0D, 16.0D, 4.0D);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_LEG_2 = Block.makeCuboidShape(6.0D, 9.0D, 12.0D, 10.0D, 16.0D, 14.0D);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_HOLDER_1 = Block.makeCuboidShape(5.0D, 3.0D, 2.0D, 11.0D, 9.0D, 4.0D);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_HOLDER_2 = Block.makeCuboidShape(5.0D, 3.0D, 12.0D, 11.0D, 9.0D, 14.0D);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_CEILING_EAST_OR_WEST_LEG_1, SHAPE_CEILING_EAST_OR_WEST_HOLDER_1);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_CEILING_EAST_OR_WEST_LEG_2, SHAPE_CEILING_EAST_OR_WEST_HOLDER_2);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_COMBINED = VoxelShapes.or(SHAPE_CEILING_EAST_OR_WEST_LEG_HOLDER_COMBINED_1, SHAPE_CEILING_EAST_OR_WEST_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST = VoxelShapes.or(SHAPE_CEILING_EAST_OR_WEST_COMBINED, Block.makeCuboidShape(2.0D, 0.0D, 4.0D, 14.0D, 12.0D, 12.0D));
    private static final ITextComponent CONTAINER_NAME = new TranslationTextComponent("container.grindstone_title");

    protected GrindstoneBlock(AbstractBlock.Properties propertiesIn)
    {
        super(propertiesIn);
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(FACE, AttachFace.WALL));
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

    private VoxelShape getShapeFromState(BlockState state)
    {
        Direction direction = state.get(HORIZONTAL_FACING);

        switch ((AttachFace)state.get(FACE))
        {
            case FLOOR:
                if (direction != Direction.NORTH && direction != Direction.SOUTH)
                {
                    return SHAPE_FLOOR_EAST_WEST;
                }

                return SHAPE_FLOOR_NORTH_SOUTH;

            case WALL:
                if (direction == Direction.NORTH)
                {
                    return SHAPE_WALL_NORTH;
                }
                else if (direction == Direction.SOUTH)
                {
                    return SHAPE_WALL_SOUTH;
                }
                else
                {
                    if (direction == Direction.EAST)
                    {
                        return SHAPE_WALL_EAST;
                    }

                    return SHAPE_WALL_WEST;
                }

            case CEILING:
                if (direction != Direction.NORTH && direction != Direction.SOUTH)
                {
                    return SHAPE_CEILING_EAST_OR_WEST;
                }

                return SHAPE_CEILING_NORTH_OR_SOUTH;

            default:
                return SHAPE_FLOOR_EAST_WEST;
        }
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.getShapeFromState(state);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.getShapeFromState(state);
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return true;
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (worldIn.isRemote)
        {
            return ActionResultType.SUCCESS;
        }
        else
        {
            player.openContainer(state.getContainer(worldIn, pos));
            player.addStat(Stats.INTERACT_WITH_GRINDSTONE);
            return ActionResultType.CONSUME;
        }
    }

    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos)
    {
        return new SimpleNamedContainerProvider((id, inventory, player) ->
        {
            return new GrindstoneContainer(id, inventory, IWorldPosCallable.of(worldIn, pos));
        }, CONTAINER_NAME);
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING, FACE);
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    }
}
