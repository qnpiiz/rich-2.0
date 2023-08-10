package com.mojang.realmsclient.dto;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Set;

public class Ops extends ValueObject
{
    public Set<String> field_230562_a_ = Sets.newHashSet();

    public static Ops func_230754_a_(String p_230754_0_)
    {
        Ops ops = new Ops();
        JsonParser jsonparser = new JsonParser();

        try
        {
            JsonElement jsonelement = jsonparser.parse(p_230754_0_);
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            JsonElement jsonelement1 = jsonobject.get("ops");

            if (jsonelement1.isJsonArray())
            {
                for (JsonElement jsonelement2 : jsonelement1.getAsJsonArray())
                {
                    ops.field_230562_a_.add(jsonelement2.getAsString());
                }
            }
        }
        catch (Exception exception)
        {
        }

        return ops;
    }
}
