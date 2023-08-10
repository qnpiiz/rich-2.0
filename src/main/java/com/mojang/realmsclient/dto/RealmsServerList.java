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

public class RealmsServerList extends ValueObject
{
    private static final Logger field_230606_b_ = LogManager.getLogger();
    public List<RealmsServer> field_230605_a_;

    public static RealmsServerList func_230783_a_(String p_230783_0_)
    {
        RealmsServerList realmsserverlist = new RealmsServerList();
        realmsserverlist.field_230605_a_ = Lists.newArrayList();

        try
        {
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = jsonparser.parse(p_230783_0_).getAsJsonObject();

            if (jsonobject.get("servers").isJsonArray())
            {
                JsonArray jsonarray = jsonobject.get("servers").getAsJsonArray();
                Iterator<JsonElement> iterator = jsonarray.iterator();

                while (iterator.hasNext())
                {
                    realmsserverlist.field_230605_a_.add(RealmsServer.func_230770_a_(iterator.next().getAsJsonObject()));
                }
            }
        }
        catch (Exception exception)
        {
            field_230606_b_.error("Could not parse McoServerList: " + exception.getMessage());
        }

        return realmsserverlist;
    }
}
