package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TopSolidWithNoiseConfig implements IPlacementConfig
{
    public static final Codec<TopSolidWithNoiseConfig> field_236978_a_ = RecordCodecBuilder.create((p_236980_0_) ->
    {
        return p_236980_0_.group(Codec.INT.fieldOf("noise_to_count_ratio").forGetter((p_236984_0_) -> {
            return p_236984_0_.noiseToCountRatio;
        }), Codec.DOUBLE.fieldOf("noise_factor").forGetter((p_236983_0_) -> {
            return p_236983_0_.noiseFactor;
        }), Codec.DOUBLE.fieldOf("noise_offset").orElse(0.0D).forGetter((p_236982_0_) -> {
            return p_236982_0_.noiseOffset;
        })).apply(p_236980_0_, TopSolidWithNoiseConfig::new);
    });
    public final int noiseToCountRatio;
    public final double noiseFactor;
    public final double noiseOffset;

    public TopSolidWithNoiseConfig(int p_i242029_1_, double p_i242029_2_, double p_i242029_4_)
    {
        this.noiseToCountRatio = p_i242029_1_;
        this.noiseFactor = p_i242029_2_;
        this.noiseOffset = p_i242029_4_;
    }
}
