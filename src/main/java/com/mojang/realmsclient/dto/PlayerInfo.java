package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import net.minecraft.realms.IPersistentSerializable;

public class PlayerInfo extends ValueObject implements IPersistentSerializable
{
    @SerializedName("name")
    private String field_230573_a_;
    @SerializedName("uuid")
    private String field_230574_b_;
    @SerializedName("operator")
    private boolean field_230575_c_;
    @SerializedName("accepted")
    private boolean field_230576_d_;
    @SerializedName("online")
    private boolean field_230577_e_;

    public String func_230757_a_()
    {
        return this.field_230573_a_;
    }

    public void func_230758_a_(String p_230758_1_)
    {
        this.field_230573_a_ = p_230758_1_;
    }

    public String func_230760_b_()
    {
        return this.field_230574_b_;
    }

    public void func_230761_b_(String p_230761_1_)
    {
        this.field_230574_b_ = p_230761_1_;
    }

    public boolean func_230763_c_()
    {
        return this.field_230575_c_;
    }

    public void func_230759_a_(boolean p_230759_1_)
    {
        this.field_230575_c_ = p_230759_1_;
    }

    public boolean func_230765_d_()
    {
        return this.field_230576_d_;
    }

    public void func_230762_b_(boolean p_230762_1_)
    {
        this.field_230576_d_ = p_230762_1_;
    }

    public boolean func_230766_e_()
    {
        return this.field_230577_e_;
    }

    public void func_230764_c_(boolean p_230764_1_)
    {
        this.field_230577_e_ = p_230764_1_;
    }
}
