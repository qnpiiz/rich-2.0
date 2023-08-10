package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;

public class NoFeatureConfig implements IFeatureConfig
{
    public static final Codec<NoFeatureConfig> field_236558_a_;
    public static final NoFeatureConfig field_236559_b_ = new NoFeatureConfig();

    static
    {
        field_236558_a_ = Codec.unit(() ->
        {
            return field_236559_b_;
        });
    }
}
