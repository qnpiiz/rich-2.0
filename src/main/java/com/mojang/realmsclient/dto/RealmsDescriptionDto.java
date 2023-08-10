package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import net.minecraft.realms.IPersistentSerializable;

public class RealmsDescriptionDto extends ValueObject implements IPersistentSerializable
{
    @SerializedName("name")
    public String field_230578_a_;
    @SerializedName("description")
    public String field_230579_b_;

    public RealmsDescriptionDto(String p_i51655_1_, String p_i51655_2_)
    {
        this.field_230578_a_ = p_i51655_1_;
        this.field_230579_b_ = p_i51655_2_;
    }
}
