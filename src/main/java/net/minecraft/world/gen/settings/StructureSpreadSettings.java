package net.minecraft.world.gen.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StructureSpreadSettings
{
    public static final Codec<StructureSpreadSettings> field_236656_a_ = RecordCodecBuilder.create((p_236661_0_) ->
    {
        return p_236661_0_.group(Codec.intRange(0, 1023).fieldOf("distance").forGetter(StructureSpreadSettings::func_236660_a_), Codec.intRange(0, 1023).fieldOf("spread").forGetter(StructureSpreadSettings::func_236662_b_), Codec.intRange(1, 4095).fieldOf("count").forGetter(StructureSpreadSettings::func_236663_c_)).apply(p_236661_0_, StructureSpreadSettings::new);
    });
    private final int field_236657_b_;
    private final int field_236658_c_;
    private final int field_236659_d_;

    public StructureSpreadSettings(int p_i232018_1_, int p_i232018_2_, int p_i232018_3_)
    {
        this.field_236657_b_ = p_i232018_1_;
        this.field_236658_c_ = p_i232018_2_;
        this.field_236659_d_ = p_i232018_3_;
    }

    public int func_236660_a_()
    {
        return this.field_236657_b_;
    }

    public int func_236662_b_()
    {
        return this.field_236658_c_;
    }

    public int func_236663_c_()
    {
        return this.field_236659_d_;
    }
}
