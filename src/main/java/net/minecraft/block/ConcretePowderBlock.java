package net.minecraft.block;

import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ConcretePowderBlock extends FallingBlock
{
    private final BlockState solidifiedState;

    public ConcretePowderBlock(Block solidified, AbstractBlock.Properties properties)
    {
        super(properties);
        this.solidifiedState = solidified.getDefaultState();
    }

    public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlock)
    {
        if (shouldSolidify(worldIn, pos, hitState))
        {
            worldIn.setBlockState(pos, this.solidifiedState, 3);
        }
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        IBlockReader iblockreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockState blockstate = iblockreader.getBlockState(blockpos);
        return shouldSolidify(iblockreader, blockpos, blockstate) ? this.solidifiedState : super.getStateForPlacement(context);
    }

    private static boolean shouldSolidify(IBlockReader reader, BlockPos pos, BlockState state)
    {
        return causesSolidify(state) || isTouchingLiquid(reader, pos);
    }

    private static boolean isTouchingLiquid(IBlockReader reader, BlockPos pos)
    {
        boolean flag = false;
        BlockPos.Mutable blockpos$mutable = pos.toMutable();

        for (Direction direction : Direction.values())
        {
            BlockState blockstate = reader.getBlockState(blockpos$mutable);

            if (direction != Direction.DOWN || causesSolidify(blockstate))
            {
                blockpos$mutable.setAndMove(pos, direction);
                blockstate = reader.getBlockState(blockpos$mutable);

                if (causesSolidify(blockstate) && !blockstate.isSolidSide(reader, pos, direction.getOpposite()))
                {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

    private static boolean causesSolidify(BlockState state)
    {
        return state.getFluidState().isTagged(FluidTags.WATER);
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder
     * immediately returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return isTouchingLiquid(worldIn, currentPos) ? this.solidifiedState : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public int getDustColor(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return state.getMaterialColor(reader, pos).colorValue;
    }
}
