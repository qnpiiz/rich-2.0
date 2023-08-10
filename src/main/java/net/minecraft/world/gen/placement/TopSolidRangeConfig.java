package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TopSolidRangeConfig implements IPlacementConfig
{
    public static final Codec<TopSolidRangeConfig> field_236985_a_ = RecordCodecBuilder.create((p_236986_0_) ->
    {
        return p_236986_0_.group(Codec.INT.fieldOf("bottom_offset").orElse(0).forGetter((p_236988_0_) -> {
            return p_236988_0_.field_242813_c;
        }), Codec.INT.fieldOf("top_offset").orElse(0).forGetter((p_236987_0_) -> {
            return p_236987_0_.field_242814_d;
        }), Codec.INT.fieldOf("maximum").orElse(0).forGetter((p_242816_0_) -> {
            return p_242816_0_.field_242815_e;
        })).apply(p_236986_0_, TopSolidRangeConfig::new);
    });
    public final int field_242813_c;
    public final int field_242814_d;
    public final int field_242815_e;

    public TopSolidRangeConfig(int p_i241992_1_, int p_i241992_2_, int p_i241992_3_)
    {
        this.field_242813_c = p_i241992_1_;
        this.field_242814_d = p_i241992_2_;
        this.field_242815_e = p_i241992_3_;
    }
}
