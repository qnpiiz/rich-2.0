package net.minecraft.block;

import java.util.Random;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.server.ServerWorld;

public abstract class SpreadableSnowyDirtBlock extends SnowyDirtBlock
{
    protected SpreadableSnowyDirtBlock(AbstractBlock.Properties builder)
    {
        super(builder);
    }

    private static boolean isSnowyConditions(BlockState state, IWorldReader worldReader, BlockPos pos)
    {
        BlockPos blockpos = pos.up();
        BlockState blockstate = worldReader.getBlockState(blockpos);

        if (blockstate.isIn(Blocks.SNOW) && blockstate.get(SnowBlock.LAYERS) == 1)
        {
            return true;
        }
        else if (blockstate.getFluidState().getLevel() == 8)
        {
            return false;
        }
        else
        {
            int i = LightEngine.func_215613_a(worldReader, state, pos, blockstate, blockpos, Direction.UP, blockstate.getOpacity(worldReader, blockpos));
            return i < worldReader.getMaxLightLevel();
        }
    }

    private static boolean isSnowyAndNotUnderwater(BlockState state, IWorldReader worldReader, BlockPos pos)
    {
        BlockPos blockpos = pos.up();
        return isSnowyConditions(state, worldReader, pos) && !worldReader.getFluidState(blockpos).isTagged(FluidTags.WATER);
    }

    /**
     * Performs a random tick on a block.
     */
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (!isSnowyConditions(state, worldIn, pos))
        {
            worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
        }
        else
        {
            if (worldIn.getLight(pos.up()) >= 9)
            {
                BlockState blockstate = this.getDefaultState();

                for (int i = 0; i < 4; ++i)
                {
                    BlockPos blockpos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

                    if (worldIn.getBlockState(blockpos).isIn(Blocks.DIRT) && isSnowyAndNotUnderwater(blockstate, worldIn, blockpos))
                    {
                        worldIn.setBlockState(blockpos, blockstate.with(SNOWY, Boolean.valueOf(worldIn.getBlockState(blockpos.up()).isIn(Blocks.SNOW))));
                    }
                }
            }
        }
    }
}
