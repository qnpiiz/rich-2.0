package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;

public class OakTree extends Tree
{
    @Nullable
    protected ConfiguredFeature < BaseTreeFeatureConfig, ? > getTreeFeature(Random randomIn, boolean largeHive)
    {
        if (randomIn.nextInt(10) == 0)
        {
            return largeHive ? Features.FANCY_OAK_BEES_005 : Features.FANCY_OAK;
        }
        else
        {
            return largeHive ? Features.OAK_BEES_005 : Features.OAK;
        }
    }
}
