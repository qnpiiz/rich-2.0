package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Objects;
import net.minecraft.client.resources.I18n;

public class RealmsWorldOptions extends ValueObject
{
    public Boolean field_230614_a_;
    public Boolean field_230615_b_;
    public Boolean field_230616_c_;
    public Boolean field_230617_d_;
    public Integer field_230618_e_;
    public Boolean field_230619_f_;
    public Boolean field_230620_g_;
    public Integer field_230621_h_;
    public Integer field_230622_i_;
    public String field_230623_j_;
    public long field_230624_k_;
    public String field_230625_l_;
    public boolean field_230626_m_;
    public boolean field_230627_n_;
    private static final String field_237699_o_ = null;

    public RealmsWorldOptions(Boolean atlasTexturesIn, Boolean p_i51651_2_, Boolean p_i51651_3_, Boolean p_i51651_4_, Integer p_i51651_5_, Boolean p_i51651_6_, Integer p_i51651_7_, Integer p_i51651_8_, Boolean p_i51651_9_, String p_i51651_10_)
    {
        this.field_230614_a_ = atlasTexturesIn;
        this.field_230615_b_ = p_i51651_2_;
        this.field_230616_c_ = p_i51651_3_;
        this.field_230617_d_ = p_i51651_4_;
        this.field_230618_e_ = p_i51651_5_;
        this.field_230619_f_ = p_i51651_6_;
        this.field_230621_h_ = p_i51651_7_;
        this.field_230622_i_ = p_i51651_8_;
        this.field_230620_g_ = p_i51651_9_;
        this.field_230623_j_ = p_i51651_10_;
    }

    public static RealmsWorldOptions func_237700_a_()
    {
        return new RealmsWorldOptions(true, true, true, true, 0, false, 2, 0, false, "");
    }

    public static RealmsWorldOptions func_237701_b_()
    {
        RealmsWorldOptions realmsworldoptions = func_237700_a_();
        realmsworldoptions.func_230789_a_(true);
        return realmsworldoptions;
    }

    public void func_230789_a_(boolean p_230789_1_)
    {
        this.field_230627_n_ = p_230789_1_;
    }

    public static RealmsWorldOptions func_230788_a_(JsonObject p_230788_0_)
    {
        RealmsWorldOptions realmsworldoptions = new RealmsWorldOptions(JsonUtils.func_225170_a("pvp", p_230788_0_, true), JsonUtils.func_225170_a("spawnAnimals", p_230788_0_, true), JsonUtils.func_225170_a("spawnMonsters", p_230788_0_, true), JsonUtils.func_225170_a("spawnNPCs", p_230788_0_, true), JsonUtils.func_225172_a("spawnProtection", p_230788_0_, 0), JsonUtils.func_225170_a("commandBlocks", p_230788_0_, false), JsonUtils.func_225172_a("difficulty", p_230788_0_, 2), JsonUtils.func_225172_a("gameMode", p_230788_0_, 0), JsonUtils.func_225170_a("forceGameMode", p_230788_0_, false), JsonUtils.func_225171_a("slotName", p_230788_0_, ""));
        realmsworldoptions.field_230624_k_ = JsonUtils.func_225169_a("worldTemplateId", p_230788_0_, -1L);
        realmsworldoptions.field_230625_l_ = JsonUtils.func_225171_a("worldTemplateImage", p_230788_0_, field_237699_o_);
        realmsworldoptions.field_230626_m_ = JsonUtils.func_225170_a("adventureMap", p_230788_0_, false);
        return realmsworldoptions;
    }

    public String func_230787_a_(int p_230787_1_)
    {
        if (this.field_230623_j_ != null && !this.field_230623_j_.isEmpty())
        {
            return this.field_230623_j_;
        }
        else
        {
            return this.field_230627_n_ ? I18n.format("mco.configure.world.slot.empty") : this.func_230790_b_(p_230787_1_);
        }
    }

    public String func_230790_b_(int p_230790_1_)
    {
        return I18n.format("mco.configure.world.slot", p_230790_1_);
    }

    public String func_230791_c_()
    {
        JsonObject jsonobject = new JsonObject();

        if (!this.field_230614_a_)
        {
            jsonobject.addProperty("pvp", this.field_230614_a_);
        }

        if (!this.field_230615_b_)
        {
            jsonobject.addProperty("spawnAnimals", this.field_230615_b_);
        }

        if (!this.field_230616_c_)
        {
            jsonobject.addProperty("spawnMonsters", this.field_230616_c_);
        }

        if (!this.field_230617_d_)
        {
            jsonobject.addProperty("spawnNPCs", this.field_230617_d_);
        }

        if (this.field_230618_e_ != 0)
        {
            jsonobject.addProperty("spawnProtection", this.field_230618_e_);
        }

        if (this.field_230619_f_)
        {
            jsonobject.addProperty("commandBlocks", this.field_230619_f_);
        }

        if (this.field_230621_h_ != 2)
        {
            jsonobject.addProperty("difficulty", this.field_230621_h_);
        }

        if (this.field_230622_i_ != 0)
        {
            jsonobject.addProperty("gameMode", this.field_230622_i_);
        }

        if (this.field_230620_g_)
        {
            jsonobject.addProperty("forceGameMode", this.field_230620_g_);
        }

        if (!Objects.equals(this.field_230623_j_, ""))
        {
            jsonobject.addProperty("slotName", this.field_230623_j_);
        }

        return jsonobject.toString();
    }

    public RealmsWorldOptions clone()
    {
        return new RealmsWorldOptions(this.field_230614_a_, this.field_230615_b_, this.field_230616_c_, this.field_230617_d_, this.field_230618_e_, this.field_230619_f_, this.field_230621_h_, this.field_230622_i_, this.field_230620_g_, this.field_230623_j_);
    }
}
