package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class SnowyDirtBlock extends Block
{
    public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

    protected SnowyDirtBlock(AbstractBlock.Properties builder)
    {
        super(builder);
        this.setDefaultState(this.stateContainer.getBaseState().with(SNOWY, Boolean.valueOf(false)));
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder
     * immediately returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return facing != Direction.UP ? super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos) : stateIn.with(SNOWY, Boolean.valueOf(facingState.isIn(Blocks.SNOW_BLOCK) || facingState.isIn(Blocks.SNOW)));
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos().up());
        return this.getDefaultState().with(SNOWY, Boolean.valueOf(blockstate.isIn(Blocks.SNOW_BLOCK) || blockstate.isIn(Blocks.SNOW)));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(SNOWY);
    }
}
