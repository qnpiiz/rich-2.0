package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ColumnConfig implements IFeatureConfig
{
    public static final Codec<ColumnConfig> CODEC = RecordCodecBuilder.create((p_242793_0_) ->
    {
        return p_242793_0_.group(FeatureSpread.func_242254_a(0, 2, 1).fieldOf("reach").forGetter((p_242796_0_) -> {
            return p_242796_0_.field_242790_b;
        }), FeatureSpread.func_242254_a(1, 5, 5).fieldOf("height").forGetter((p_242792_0_) -> {
            return p_242792_0_.field_242791_c;
        })).apply(p_242793_0_, ColumnConfig::new);
    });
    private final FeatureSpread field_242790_b;
    private final FeatureSpread field_242791_c;

    public ColumnConfig(FeatureSpread p_i241981_1_, FeatureSpread p_i241981_2_)
    {
        this.field_242790_b = p_i241981_1_;
        this.field_242791_c = p_i241981_2_;
    }

    public FeatureSpread func_242794_am_()
    {
        return this.field_242790_b;
    }

    public FeatureSpread func_242795_b()
    {
        return this.field_242791_c;
    }
}
