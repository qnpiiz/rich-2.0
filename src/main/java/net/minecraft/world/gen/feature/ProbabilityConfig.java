package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.carver.ICarverConfig;

public class ProbabilityConfig implements ICarverConfig, IFeatureConfig
{
    public static final Codec<ProbabilityConfig> field_236576_b_ = RecordCodecBuilder.create((p_236578_0_) ->
    {
        return p_236578_0_.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((p_236577_0_) -> {
            return p_236577_0_.probability;
        })).apply(p_236578_0_, ProbabilityConfig::new);
    });
    public final float probability;

    public ProbabilityConfig(float probability)
    {
        this.probability = probability;
    }
}
