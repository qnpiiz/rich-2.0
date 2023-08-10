package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerPlayerLists extends ValueObject
{
    private static final Logger field_230613_b_ = LogManager.getLogger();
    public List<RealmsServerPlayerList> field_230612_a_;

    public static RealmsServerPlayerLists func_230786_a_(String p_230786_0_)
    {
        RealmsServerPlayerLists realmsserverplayerlists = new RealmsServerPlayerLists();
        realmsserverplayerlists.field_230612_a_ = Lists.newArrayList();

        try
        {
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = jsonparser.parse(p_230786_0_).getAsJsonObject();

            if (jsonobject.get("lists").isJsonArray())
            {
                JsonArray jsonarray = jsonobject.get("lists").getAsJsonArray();
                Iterator<JsonElement> iterator = jsonarray.iterator();

                while (iterator.hasNext())
                {
                    realmsserverplayerlists.field_230612_a_.add(RealmsServerPlayerList.func_230785_a_(iterator.next().getAsJsonObject()));
                }
            }
        }
        catch (Exception exception)
        {
            field_230613_b_.error("Could not parse RealmsServerPlayerLists: " + exception.getMessage());
        }

        return realmsserverplayerlists;
    }
}
