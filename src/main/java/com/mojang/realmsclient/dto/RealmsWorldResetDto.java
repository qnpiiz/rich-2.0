package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import net.minecraft.realms.IPersistentSerializable;

public class RealmsWorldResetDto extends ValueObject implements IPersistentSerializable
{
    @SerializedName("seed")
    private final String field_230628_a_;
    @SerializedName("worldTemplateId")
    private final long field_230629_b_;
    @SerializedName("levelType")
    private final int field_230630_c_;
    @SerializedName("generateStructures")
    private final boolean field_230631_d_;

    public RealmsWorldResetDto(String p_i51640_1_, long p_i51640_2_, int p_i51640_4_, boolean p_i51640_5_)
    {
        this.field_230628_a_ = p_i51640_1_;
        this.field_230629_b_ = p_i51640_2_;
        this.field_230630_c_ = p_i51640_4_;
        this.field_230631_d_ = p_i51640_5_;
    }
}
