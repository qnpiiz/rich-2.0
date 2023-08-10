package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Locale;
import net.minecraft.realms.IPersistentSerializable;

public class RegionPingResult extends ValueObject implements IPersistentSerializable
{
    @SerializedName("regionName")
    private final String field_230632_a_;
    @SerializedName("ping")
    private final int field_230633_b_;

    public RegionPingResult(String entityRendererIn, int mcIn)
    {
        this.field_230632_a_ = entityRendererIn;
        this.field_230633_b_ = mcIn;
    }

    public int func_230792_a_()
    {
        return this.field_230633_b_;
    }

    public String toString()
    {
        return String.format(Locale.ROOT, "%s --> %.2f ms", this.field_230632_a_, (float)this.field_230633_b_);
    }
}
