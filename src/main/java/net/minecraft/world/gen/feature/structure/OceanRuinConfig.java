package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class OceanRuinConfig implements IFeatureConfig
{
    public static final Codec<OceanRuinConfig> field_236561_a_ = RecordCodecBuilder.create((p_236563_0_) ->
    {
        return p_236563_0_.group(OceanRuinStructure.Type.field_236998_c_.fieldOf("biome_temp").forGetter((p_236565_0_) -> {
            return p_236565_0_.field_204031_a;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("large_probability").forGetter((p_236564_0_) -> {
            return p_236564_0_.largeProbability;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("cluster_probability").forGetter((p_236562_0_) -> {
            return p_236562_0_.clusterProbability;
        })).apply(p_236563_0_, OceanRuinConfig::new);
    });
    public final OceanRuinStructure.Type field_204031_a;
    public final float largeProbability;
    public final float clusterProbability;

    public OceanRuinConfig(OceanRuinStructure.Type p_i48866_1_, float largeProbability, float clusterProbability)
    {
        this.field_204031_a = p_i48866_1_;
        this.largeProbability = largeProbability;
        this.clusterProbability = clusterProbability;
    }
}
