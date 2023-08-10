package net.minecraft.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class SoulFireBlock extends AbstractFireBlock
{
    public SoulFireBlock(AbstractBlock.Properties properties)
    {
        super(properties, 2.0F);
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder
     * immediately returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return this.isValidPosition(stateIn, worldIn, currentPos) ? this.getDefaultState() : Blocks.AIR.getDefaultState();
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return shouldLightSoulFire(worldIn.getBlockState(pos.down()).getBlock());
    }

    public static boolean shouldLightSoulFire(Block block)
    {
        return block.isIn(BlockTags.SOUL_FIRE_BASE_BLOCKS);
    }

    protected boolean canBurn(BlockState state)
    {
        return true;
    }
}
