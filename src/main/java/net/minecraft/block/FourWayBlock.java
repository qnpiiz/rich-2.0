package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class FourWayBlock extends Block implements IWaterLoggable
{
    public static final BooleanProperty NORTH = SixWayBlock.NORTH;
    public static final BooleanProperty EAST = SixWayBlock.EAST;
    public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    public static final BooleanProperty WEST = SixWayBlock.WEST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((facingProperty) ->
    {
        return facingProperty.getKey().getAxis().isHorizontal();
    }).collect(Util.toMapCollector());
    protected final VoxelShape[] collisionShapes;
    protected final VoxelShape[] shapes;
    private final Object2IntMap<BlockState> statePaletteMap = new Object2IntOpenHashMap<>();

    protected FourWayBlock(float nodeWidth, float extensionWidth, float nodeHeight, float extensionHeight, float collisionY, AbstractBlock.Properties properties)
    {
        super(properties);
        this.collisionShapes = this.makeShapes(nodeWidth, extensionWidth, collisionY, 0.0F, collisionY);
        this.shapes = this.makeShapes(nodeWidth, extensionWidth, nodeHeight, 0.0F, extensionHeight);

        for (BlockState blockstate : this.stateContainer.getValidStates())
        {
            this.getIndex(blockstate);
        }
    }

    protected VoxelShape[] makeShapes(float nodeWidth, float extensionWidth, float nodeHeight, float extensionBottom, float extensionHeight)
    {
        float f = 8.0F - nodeWidth;
        float f1 = 8.0F + nodeWidth;
        float f2 = 8.0F - extensionWidth;
        float f3 = 8.0F + extensionWidth;
        VoxelShape voxelshape = Block.makeCuboidShape((double)f, 0.0D, (double)f, (double)f1, (double)nodeHeight, (double)f1);
        VoxelShape voxelshape1 = Block.makeCuboidShape((double)f2, (double)extensionBottom, 0.0D, (double)f3, (double)extensionHeight, (double)f3);
        VoxelShape voxelshape2 = Block.makeCuboidShape((double)f2, (double)extensionBottom, (double)f2, (double)f3, (double)extensionHeight, 16.0D);
        VoxelShape voxelshape3 = Block.makeCuboidShape(0.0D, (double)extensionBottom, (double)f2, (double)f3, (double)extensionHeight, (double)f3);
        VoxelShape voxelshape4 = Block.makeCuboidShape((double)f2, (double)extensionBottom, (double)f2, 16.0D, (double)extensionHeight, (double)f3);
        VoxelShape voxelshape5 = VoxelShapes.or(voxelshape1, voxelshape4);
        VoxelShape voxelshape6 = VoxelShapes.or(voxelshape2, voxelshape3);
        VoxelShape[] avoxelshape = new VoxelShape[] {VoxelShapes.empty(), voxelshape2, voxelshape3, voxelshape6, voxelshape1, VoxelShapes.or(voxelshape2, voxelshape1), VoxelShapes.or(voxelshape3, voxelshape1), VoxelShapes.or(voxelshape6, voxelshape1), voxelshape4, VoxelShapes.or(voxelshape2, voxelshape4), VoxelShapes.or(voxelshape3, voxelshape4), VoxelShapes.or(voxelshape6, voxelshape4), voxelshape5, VoxelShapes.or(voxelshape2, voxelshape5), VoxelShapes.or(voxelshape3, voxelshape5), VoxelShapes.or(voxelshape6, voxelshape5)};

        for (int i = 0; i < 16; ++i)
        {
            avoxelshape[i] = VoxelShapes.or(voxelshape, avoxelshape[i]);
        }

        return avoxelshape;
    }

    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return !state.get(WATERLOGGED);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.shapes[this.getIndex(state)];
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.collisionShapes[this.getIndex(state)];
    }

    private static int getMask(Direction facing)
    {
        return 1 << facing.getHorizontalIndex();
    }

    protected int getIndex(BlockState state)
    {
        return this.statePaletteMap.computeIntIfAbsent(state, (stateIn) ->
        {
            int i = 0;

            if (stateIn.get(NORTH))
            {
                i |= getMask(Direction.NORTH);
            }

            if (stateIn.get(EAST))
            {
                i |= getMask(Direction.EAST);
            }

            if (stateIn.get(SOUTH))
            {
                i |= getMask(Direction.SOUTH);
            }

            if (stateIn.get(WEST))
            {
                i |= getMask(Direction.WEST);
            }

            return i;
        });
    }

    public FluidState getFluidState(BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
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
}
