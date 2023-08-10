package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class FeatureSizeType<P extends AbstractFeatureSizeType>
{
    public static final FeatureSizeType<TwoLayerFeature> TWO_LAYERS_FEATURE_SIZE = register("two_layers_feature_size", TwoLayerFeature.field_236728_c_);
    public static final FeatureSizeType<ThreeLayerFeature> THREE_LAYERS_FEATURE_SIZE = register("three_layers_feature_size", ThreeLayerFeature.field_236716_c_);
    private final Codec<P> codec;

    private static <P extends AbstractFeatureSizeType> FeatureSizeType<P> register(String name, Codec<P> codec)
    {
        return Registry.register(Registry.FEATURE_SIZE_TYPE, name, new FeatureSizeType<>(codec));
    }

    private FeatureSizeType(Codec<P> codec)
    {
        this.codec = codec;
    }

    public Codec<P> getCodec()
    {
        return this.codec;
    }
}
