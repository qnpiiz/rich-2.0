package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;

public abstract class BigTree extends Tree
{
    public boolean attemptGrowTree(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random rand)
    {
        for (int i = 0; i >= -1; --i)
        {
            for (int j = 0; j >= -1; --j)
            {
                if (canBigTreeSpawnAt(state, world, pos, i, j))
                {
                    return this.growBigTree(world, chunkGenerator, pos, state, rand, i, j);
                }
            }
        }

        return super.attemptGrowTree(world, chunkGenerator, pos, state, rand);
    }

    @Nullable
    protected abstract ConfiguredFeature < BaseTreeFeatureConfig, ? > getHugeTreeFeature(Random rand);

    public boolean growBigTree(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random rand, int branchX, int branchY)
    {
        ConfiguredFeature < BaseTreeFeatureConfig, ? > configuredfeature = this.getHugeTreeFeature(rand);

        if (configuredfeature == null)
        {
            return false;
        }
        else
        {
            configuredfeature.config.forcePlacement();
            BlockState blockstate = Blocks.AIR.getDefaultState();
            world.setBlockState(pos.add(branchX, 0, branchY), blockstate, 4);
            world.setBlockState(pos.add(branchX + 1, 0, branchY), blockstate, 4);
            world.setBlockState(pos.add(branchX, 0, branchY + 1), blockstate, 4);
            world.setBlockState(pos.add(branchX + 1, 0, branchY + 1), blockstate, 4);

            if (configuredfeature.func_242765_a(world, chunkGenerator, rand, pos.add(branchX, 0, branchY)))
            {
                return true;
            }
            else
            {
                world.setBlockState(pos.add(branchX, 0, branchY), state, 4);
                world.setBlockState(pos.add(branchX + 1, 0, branchY), state, 4);
                world.setBlockState(pos.add(branchX, 0, branchY + 1), state, 4);
                world.setBlockState(pos.add(branchX + 1, 0, branchY + 1), state, 4);
                return false;
            }
        }
    }

    public static boolean canBigTreeSpawnAt(BlockState blockUnder, IBlockReader worldIn, BlockPos pos, int xOffset, int zOffset)
    {
        Block block = blockUnder.getBlock();
        return block == worldIn.getBlockState(pos.add(xOffset, 0, zOffset)).getBlock() && block == worldIn.getBlockState(pos.add(xOffset + 1, 0, zOffset)).getBlock() && block == worldIn.getBlockState(pos.add(xOffset, 0, zOffset + 1)).getBlock() && block == worldIn.getBlockState(pos.add(xOffset + 1, 0, zOffset + 1)).getBlock();
    }
}
