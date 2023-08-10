package net.minecraft.block;

import java.util.Random;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.NetherVegetationFeature;
import net.minecraft.world.gen.feature.TwistingVineFeature;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.server.ServerWorld;

public class NyliumBlock extends Block implements IGrowable
{
    protected NyliumBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    private static boolean isDarkEnough(BlockState state, IWorldReader reader, BlockPos pos)
    {
        BlockPos blockpos = pos.up();
        BlockState blockstate = reader.getBlockState(blockpos);
        int i = LightEngine.func_215613_a(reader, state, pos, blockstate, blockpos, Direction.UP, blockstate.getOpacity(reader, blockpos));
        return i < reader.getMaxLightLevel();
    }

    /**
     * Performs a random tick on a block.
     */
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (!isDarkEnough(state, worldIn, pos))
        {
            worldIn.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
        }
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return worldIn.getBlockState(pos.up()).isAir();
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state)
    {
        return true;
    }

    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state)
    {
        BlockState blockstate = worldIn.getBlockState(pos);
        BlockPos blockpos = pos.up();

        if (blockstate.isIn(Blocks.CRIMSON_NYLIUM))
        {
            NetherVegetationFeature.func_236325_a_(worldIn, rand, blockpos, Features.Configs.CRIMSON_FOREST_VEGETATION_CONFIG, 3, 1);
        }
        else if (blockstate.isIn(Blocks.WARPED_NYLIUM))
        {
            NetherVegetationFeature.func_236325_a_(worldIn, rand, blockpos, Features.Configs.WARPED_FOREST_VEGETATION_CONFIG, 3, 1);
            NetherVegetationFeature.func_236325_a_(worldIn, rand, blockpos, Features.Configs.NETHER_SPROUTS_CONFIG, 3, 1);

            if (rand.nextInt(8) == 0)
            {
                TwistingVineFeature.func_236423_a_(worldIn, rand, blockpos, 3, 1, 2);
            }
        }
    }
}
