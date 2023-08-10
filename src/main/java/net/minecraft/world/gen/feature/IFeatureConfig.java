package net.minecraft.world.gen.feature;

import java.util.stream.Stream;

public interface IFeatureConfig
{
    NoFeatureConfig NO_FEATURE_CONFIG = NoFeatureConfig.field_236559_b_;

default Stream<ConfiguredFeature<?, ?>> func_241856_an_()
    {
        return Stream.empty();
    }
}
