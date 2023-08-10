package net.minecraft.block;

import java.util.Map;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class HugeMushroomBlock extends Block
{
    public static final BooleanProperty NORTH = SixWayBlock.NORTH;
    public static final BooleanProperty EAST = SixWayBlock.EAST;
    public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    public static final BooleanProperty WEST = SixWayBlock.WEST;
    public static final BooleanProperty UP = SixWayBlock.UP;
    public static final BooleanProperty DOWN = SixWayBlock.DOWN;
    private static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP;

    public HugeMushroomBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(true)).with(EAST, Boolean.valueOf(true)).with(SOUTH, Boolean.valueOf(true)).with(WEST, Boolean.valueOf(true)).with(UP, Boolean.valueOf(true)).with(DOWN, Boolean.valueOf(true)));
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        IBlockReader iblockreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        return this.getDefaultState().with(DOWN, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.down()).getBlock())).with(UP, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.up()).getBlock())).with(NORTH, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.north()).getBlock())).with(EAST, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.east()).getBlock())).with(SOUTH, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.south()).getBlock())).with(WEST, Boolean.valueOf(this != iblockreader.getBlockState(blockpos.west()).getBlock()));
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder
     * immediately returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return facingState.isIn(this) ? stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(false)) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.with(FACING_TO_PROPERTY_MAP.get(rot.rotate(Direction.NORTH)), state.get(NORTH)).with(FACING_TO_PROPERTY_MAP.get(rot.rotate(Direction.SOUTH)), state.get(SOUTH)).with(FACING_TO_PROPERTY_MAP.get(rot.rotate(Direction.EAST)), state.get(EAST)).with(FACING_TO_PROPERTY_MAP.get(rot.rotate(Direction.WEST)), state.get(WEST)).with(FACING_TO_PROPERTY_MAP.get(rot.rotate(Direction.UP)), state.get(UP)).with(FACING_TO_PROPERTY_MAP.get(rot.rotate(Direction.DOWN)), state.get(DOWN));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        return state.with(FACING_TO_PROPERTY_MAP.get(mirrorIn.mirror(Direction.NORTH)), state.get(NORTH)).with(FACING_TO_PROPERTY_MAP.get(mirrorIn.mirror(Direction.SOUTH)), state.get(SOUTH)).with(FACING_TO_PROPERTY_MAP.get(mirrorIn.mirror(Direction.EAST)), state.get(EAST)).with(FACING_TO_PROPERTY_MAP.get(mirrorIn.mirror(Direction.WEST)), state.get(WEST)).with(FACING_TO_PROPERTY_MAP.get(mirrorIn.mirror(Direction.UP)), state.get(UP)).with(FACING_TO_PROPERTY_MAP.get(mirrorIn.mirror(Direction.DOWN)), state.get(DOWN));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }
}
