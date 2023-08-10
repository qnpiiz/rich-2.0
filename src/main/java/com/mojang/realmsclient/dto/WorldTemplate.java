package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldTemplate extends ValueObject
{
    private static final Logger field_230656_j_ = LogManager.getLogger();
    public String field_230647_a_ = "";
    public String field_230648_b_ = "";
    public String field_230649_c_ = "";
    public String field_230650_d_ = "";
    public String field_230651_e_ = "";
    @Nullable
    public String field_230652_f_;
    public String field_230653_g_ = "";
    public String field_230654_h_ = "";
    public WorldTemplate.Type field_230655_i_ = WorldTemplate.Type.WORLD_TEMPLATE;

    public static WorldTemplate func_230803_a_(JsonObject p_230803_0_)
    {
        WorldTemplate worldtemplate = new WorldTemplate();

        try
        {
            worldtemplate.field_230647_a_ = JsonUtils.func_225171_a("id", p_230803_0_, "");
            worldtemplate.field_230648_b_ = JsonUtils.func_225171_a("name", p_230803_0_, "");
            worldtemplate.field_230649_c_ = JsonUtils.func_225171_a("version", p_230803_0_, "");
            worldtemplate.field_230650_d_ = JsonUtils.func_225171_a("author", p_230803_0_, "");
            worldtemplate.field_230651_e_ = JsonUtils.func_225171_a("link", p_230803_0_, "");
            worldtemplate.field_230652_f_ = JsonUtils.func_225171_a("image", p_230803_0_, (String)null);
            worldtemplate.field_230653_g_ = JsonUtils.func_225171_a("trailer", p_230803_0_, "");
            worldtemplate.field_230654_h_ = JsonUtils.func_225171_a("recommendedPlayers", p_230803_0_, "");
            worldtemplate.field_230655_i_ = WorldTemplate.Type.valueOf(JsonUtils.func_225171_a("type", p_230803_0_, WorldTemplate.Type.WORLD_TEMPLATE.name()));
        }
        catch (Exception exception)
        {
            field_230656_j_.error("Could not parse WorldTemplate: " + exception.getMessage());
        }

        return worldtemplate;
    }

    public static enum Type
    {
        WORLD_TEMPLATE,
        MINIGAME,
        ADVENTUREMAP,
        EXPERIENCE,
        INSPIRATION;
    }
}
