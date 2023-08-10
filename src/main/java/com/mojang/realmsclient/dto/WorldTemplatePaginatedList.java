package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldTemplatePaginatedList extends ValueObject
{
    private static final Logger field_230661_e_ = LogManager.getLogger();
    public List<WorldTemplate> field_230657_a_;
    public int field_230658_b_;
    public int field_230659_c_;
    public int field_230660_d_;

    public WorldTemplatePaginatedList()
    {
    }

    public WorldTemplatePaginatedList(int p_i51733_1_)
    {
        this.field_230657_a_ = Collections.emptyList();
        this.field_230658_b_ = 0;
        this.field_230659_c_ = p_i51733_1_;
        this.field_230660_d_ = -1;
    }

    public static WorldTemplatePaginatedList func_230804_a_(String p_230804_0_)
    {
        WorldTemplatePaginatedList worldtemplatepaginatedlist = new WorldTemplatePaginatedList();
        worldtemplatepaginatedlist.field_230657_a_ = Lists.newArrayList();

        try
        {
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = jsonparser.parse(p_230804_0_).getAsJsonObject();

            if (jsonobject.get("templates").isJsonArray())
            {
                Iterator<JsonElement> iterator = jsonobject.get("templates").getAsJsonArray().iterator();

                while (iterator.hasNext())
                {
                    worldtemplatepaginatedlist.field_230657_a_.add(WorldTemplate.func_230803_a_(iterator.next().getAsJsonObject()));
                }
            }

            worldtemplatepaginatedlist.field_230658_b_ = JsonUtils.func_225172_a("page", jsonobject, 0);
            worldtemplatepaginatedlist.field_230659_c_ = JsonUtils.func_225172_a("size", jsonobject, 0);
            worldtemplatepaginatedlist.field_230660_d_ = JsonUtils.func_225172_a("total", jsonobject, 0);
        }
        catch (Exception exception)
        {
            field_230661_e_.error("Could not parse WorldTemplatePaginatedList: " + exception.getMessage());
        }

        return worldtemplatepaginatedlist;
    }
}
