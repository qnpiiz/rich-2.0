package net.minecraft.world.gen.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseSettings
{
    public static final Codec<NoiseSettings> field_236156_a_ = RecordCodecBuilder.create((p_236170_0_) ->
    {
        return p_236170_0_.group(Codec.intRange(0, 256).fieldOf("height").forGetter(NoiseSettings::func_236169_a_), ScalingSettings.field_236145_a_.fieldOf("sampling").forGetter(NoiseSettings::func_236171_b_), SlideSettings.field_236182_a_.fieldOf("top_slide").forGetter(NoiseSettings::func_236172_c_), SlideSettings.field_236182_a_.fieldOf("bottom_slide").forGetter(NoiseSettings::func_236173_d_), Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(NoiseSettings::func_236174_e_), Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(NoiseSettings::func_236175_f_), Codec.DOUBLE.fieldOf("density_factor").forGetter(NoiseSettings::func_236176_g_), Codec.DOUBLE.fieldOf("density_offset").forGetter(NoiseSettings::func_236177_h_), Codec.BOOL.fieldOf("simplex_surface_noise").forGetter(NoiseSettings::func_236178_i_), Codec.BOOL.optionalFieldOf("random_density_offset", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(NoiseSettings::func_236179_j_), Codec.BOOL.optionalFieldOf("island_noise_override", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(NoiseSettings::func_236180_k_), Codec.BOOL.optionalFieldOf("amplified", Boolean.valueOf(false), Lifecycle.experimental()).forGetter(NoiseSettings::func_236181_l_)).apply(p_236170_0_, NoiseSettings::new);
    });
    private final int field_236157_b_;
    private final ScalingSettings field_236158_c_;
    private final SlideSettings field_236159_d_;
    private final SlideSettings field_236160_e_;
    private final int field_236161_f_;
    private final int field_236162_g_;
    private final double field_236163_h_;
    private final double field_236164_i_;
    private final boolean field_236165_j_;
    private final boolean field_236166_k_;
    private final boolean field_236167_l_;
    private final boolean field_236168_m_;

    public NoiseSettings(int p_i231910_1_, ScalingSettings p_i231910_2_, SlideSettings p_i231910_3_, SlideSettings p_i231910_4_, int p_i231910_5_, int p_i231910_6_, double p_i231910_7_, double p_i231910_9_, boolean p_i231910_11_, boolean p_i231910_12_, boolean p_i231910_13_, boolean p_i231910_14_)
    {
        this.field_236157_b_ = p_i231910_1_;
        this.field_236158_c_ = p_i231910_2_;
        this.field_236159_d_ = p_i231910_3_;
        this.field_236160_e_ = p_i231910_4_;
        this.field_236161_f_ = p_i231910_5_;
        this.field_236162_g_ = p_i231910_6_;
        this.field_236163_h_ = p_i231910_7_;
        this.field_236164_i_ = p_i231910_9_;
        this.field_236165_j_ = p_i231910_11_;
        this.field_236166_k_ = p_i231910_12_;
        this.field_236167_l_ = p_i231910_13_;
        this.field_236168_m_ = p_i231910_14_;
    }

    public int func_236169_a_()
    {
        return this.field_236157_b_;
    }

    public ScalingSettings func_236171_b_()
    {
        return this.field_236158_c_;
    }

    public SlideSettings func_236172_c_()
    {
        return this.field_236159_d_;
    }

    public SlideSettings func_236173_d_()
    {
        return this.field_236160_e_;
    }

    public int func_236174_e_()
    {
        return this.field_236161_f_;
    }

    public int func_236175_f_()
    {
        return this.field_236162_g_;
    }

    public double func_236176_g_()
    {
        return this.field_236163_h_;
    }

    public double func_236177_h_()
    {
        return this.field_236164_i_;
    }

    @Deprecated
    public boolean func_236178_i_()
    {
        return this.field_236165_j_;
    }

    @Deprecated
    public boolean func_236179_j_()
    {
        return this.field_236166_k_;
    }

    @Deprecated
    public boolean func_236180_k_()
    {
        return this.field_236167_l_;
    }

    @Deprecated
    public boolean func_236181_l_()
    {
        return this.field_236168_m_;
    }
}
