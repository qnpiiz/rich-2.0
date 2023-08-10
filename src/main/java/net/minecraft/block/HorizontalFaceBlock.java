package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class HorizontalFaceBlock extends HorizontalBlock
{
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.FACE;

    protected HorizontalFaceBlock(AbstractBlock.Properties builder)
    {
        super(builder);
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return isSideSolidForDirection(worldIn, pos, getFacing(state).getOpposite());
    }

    public static boolean isSideSolidForDirection(IWorldReader reader, BlockPos pos, Direction direction)
    {
        BlockPos blockpos = pos.offset(direction);
        return reader.getBlockState(blockpos).isSolidSide(reader, blockpos, direction.getOpposite());
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        for (Direction direction : context.getNearestLookingDirections())
        {
            BlockState blockstate;

            if (direction.getAxis() == Direction.Axis.Y)
            {
                blockstate = this.getDefaultState().with(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing());
            }
            else
            {
                blockstate = this.getDefaultState().with(FACE, AttachFace.WALL).with(HORIZONTAL_FACING, direction.getOpposite());
            }

            if (blockstate.isValidPosition(context.getWorld(), context.getPos()))
            {
                return blockstate;
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
        return getFacing(stateIn).getOpposite() == facing && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    protected static Direction getFacing(BlockState state)
    {
        switch ((AttachFace)state.get(FACE))
        {
            case CEILING:
                return Direction.DOWN;

            case FLOOR:
                return Direction.UP;

            default:
                return state.get(HORIZONTAL_FACING);
        }
    }
}
