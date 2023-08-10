package net.minecraft.world.gen.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ScalingSettings
{
    private static final Codec<Double> field_236146_b_ = Codec.doubleRange(0.001D, 1000.0D);
    public static final Codec<ScalingSettings> field_236145_a_ = RecordCodecBuilder.create((p_236152_0_) ->
    {
        return p_236152_0_.group(field_236146_b_.fieldOf("xz_scale").forGetter(ScalingSettings::func_236151_a_), field_236146_b_.fieldOf("y_scale").forGetter(ScalingSettings::func_236153_b_), field_236146_b_.fieldOf("xz_factor").forGetter(ScalingSettings::func_236154_c_), field_236146_b_.fieldOf("y_factor").forGetter(ScalingSettings::func_236155_d_)).apply(p_236152_0_, ScalingSettings::new);
    });
    private final double field_236147_c_;
    private final double field_236148_d_;
    private final double field_236149_e_;
    private final double field_236150_f_;

    public ScalingSettings(double p_i231909_1_, double p_i231909_3_, double p_i231909_5_, double p_i231909_7_)
    {
        this.field_236147_c_ = p_i231909_1_;
        this.field_236148_d_ = p_i231909_3_;
        this.field_236149_e_ = p_i231909_5_;
        this.field_236150_f_ = p_i231909_7_;
    }

    public double func_236151_a_()
    {
        return this.field_236147_c_;
    }

    public double func_236153_b_()
    {
        return this.field_236148_d_;
    }

    public double func_236154_c_()
    {
        return this.field_236149_e_;
    }

    public double func_236155_d_()
    {
        return this.field_236150_f_;
    }
}
