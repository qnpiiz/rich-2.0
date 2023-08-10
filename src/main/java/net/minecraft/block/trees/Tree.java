package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;

public abstract class Tree
{
    @Nullable
    protected abstract ConfiguredFeature < BaseTreeFeatureConfig, ? > getTreeFeature(Random randomIn, boolean largeHive);

    public boolean attemptGrowTree(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random rand)
    {
        ConfiguredFeature < BaseTreeFeatureConfig, ? > configuredfeature = this.getTreeFeature(rand, this.hasNearbyFlora(world, pos));

        if (configuredfeature == null)
        {
            return false;
        }
        else
        {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);
            configuredfeature.config.forcePlacement();

            if (configuredfeature.func_242765_a(world, chunkGenerator, rand, pos))
            {
                return true;
            }
            else
            {
                world.setBlockState(pos, state, 4);
                return false;
            }
        }
    }

    private boolean hasNearbyFlora(IWorld world, BlockPos pos)
    {
        for (BlockPos blockpos : BlockPos.Mutable.getAllInBoxMutable(pos.down().north(2).west(2), pos.up().south(2).east(2)))
        {
            if (world.getBlockState(blockpos).isIn(BlockTags.FLOWERS))
            {
                return true;
            }
        }

        return false;
    }
}
