package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;

public class BirchTree extends Tree
{
    @Nullable
    protected ConfiguredFeature < BaseTreeFeatureConfig, ? > getTreeFeature(Random randomIn, boolean largeHive)
    {
        return largeHive ? Features.BIRCH_BEES_005 : Features.BIRCH;
    }
}
