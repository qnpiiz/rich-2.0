package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class MineshaftConfig implements IFeatureConfig
{
    public static final Codec<MineshaftConfig> field_236541_a_ = RecordCodecBuilder.create((p_236543_0_) ->
    {
        return p_236543_0_.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((p_236544_0_) -> {
            return p_236544_0_.probability;
        }), MineshaftStructure.Type.field_236324_c_.fieldOf("type").forGetter((p_236542_0_) -> {
            return p_236542_0_.type;
        })).apply(p_236543_0_, MineshaftConfig::new);
    });
    public final float probability;
    public final MineshaftStructure.Type type;

    public MineshaftConfig(float p_i241988_1_, MineshaftStructure.Type p_i241988_2_)
    {
        this.probability = p_i241988_1_;
        this.type = p_i241988_2_;
    }
}
