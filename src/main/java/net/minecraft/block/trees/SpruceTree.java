package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;

public class SpruceTree extends BigTree
{
    @Nullable
    protected ConfiguredFeature < BaseTreeFeatureConfig, ? > getTreeFeature(Random randomIn, boolean largeHive)
    {
        return Features.SPRUCE;
    }

    @Nullable
    protected ConfiguredFeature < BaseTreeFeatureConfig, ? > getHugeTreeFeature(Random rand)
    {
        return rand.nextBoolean() ? Features.MEGA_SPRUCE : Features.MEGA_PINE;
    }
}
