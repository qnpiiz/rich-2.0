package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseDependant implements IPlacementConfig
{
    public static final Codec<NoiseDependant> field_236550_a_ = RecordCodecBuilder.create((p_236552_0_) ->
    {
        return p_236552_0_.group(Codec.DOUBLE.fieldOf("noise_level").forGetter((p_236554_0_) -> {
            return p_236554_0_.noiseLevel;
        }), Codec.INT.fieldOf("below_noise").forGetter((p_236553_0_) -> {
            return p_236553_0_.belowNoise;
        }), Codec.INT.fieldOf("above_noise").forGetter((p_236551_0_) -> {
            return p_236551_0_.aboveNoise;
        })).apply(p_236552_0_, NoiseDependant::new);
    });
    public final double noiseLevel;
    public final int belowNoise;
    public final int aboveNoise;

    public NoiseDependant(double noiseLevel, int belowNoise, int aboveNoise)
    {
        this.noiseLevel = noiseLevel;
        this.belowNoise = belowNoise;
        this.aboveNoise = aboveNoise;
    }
}
