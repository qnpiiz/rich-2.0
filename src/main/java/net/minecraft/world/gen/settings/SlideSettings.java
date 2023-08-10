package net.minecraft.world.gen.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SlideSettings
{
    public static final Codec<SlideSettings> field_236182_a_ = RecordCodecBuilder.create((p_236187_0_) ->
    {
        return p_236187_0_.group(Codec.INT.fieldOf("target").forGetter(SlideSettings::func_236186_a_), Codec.intRange(0, 256).fieldOf("size").forGetter(SlideSettings::func_236188_b_), Codec.INT.fieldOf("offset").forGetter(SlideSettings::func_236189_c_)).apply(p_236187_0_, SlideSettings::new);
    });
    private final int field_236183_b_;
    private final int field_236184_c_;
    private final int field_236185_d_;

    public SlideSettings(int p_i231911_1_, int p_i231911_2_, int p_i231911_3_)
    {
        this.field_236183_b_ = p_i231911_1_;
        this.field_236184_c_ = p_i231911_2_;
        this.field_236185_d_ = p_i231911_3_;
    }

    public int func_236186_a_()
    {
        return this.field_236183_b_;
    }

    public int func_236188_b_()
    {
        return this.field_236184_c_;
    }

    public int func_236189_c_()
    {
        return this.field_236185_d_;
    }
}
