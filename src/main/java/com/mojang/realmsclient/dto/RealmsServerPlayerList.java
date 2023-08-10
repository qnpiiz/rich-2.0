package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerPlayerList extends ValueObject
{
    private static final Logger field_230611_c_ = LogManager.getLogger();
    private static final JsonParser field_237698_d_ = new JsonParser();
    public long field_230609_a_;
    public List<String> field_230610_b_;

    public static RealmsServerPlayerList func_230785_a_(JsonObject p_230785_0_)
    {
        RealmsServerPlayerList realmsserverplayerlist = new RealmsServerPlayerList();

        try
        {
            realmsserverplayerlist.field_230609_a_ = JsonUtils.func_225169_a("serverId", p_230785_0_, -1L);
            String s = JsonUtils.func_225171_a("playerList", p_230785_0_, (String)null);

            if (s != null)
            {
                JsonElement jsonelement = field_237698_d_.parse(s);

                if (jsonelement.isJsonArray())
                {
                    realmsserverplayerlist.field_230610_b_ = func_230784_a_(jsonelement.getAsJsonArray());
                }
                else
                {
                    realmsserverplayerlist.field_230610_b_ = Lists.newArrayList();
                }
            }
            else
            {
                realmsserverplayerlist.field_230610_b_ = Lists.newArrayList();
            }
        }
        catch (Exception exception)
        {
            field_230611_c_.error("Could not parse RealmsServerPlayerList: " + exception.getMessage());
        }

        return realmsserverplayerlist;
    }

    private static List<String> func_230784_a_(JsonArray p_230784_0_)
    {
        List<String> list = Lists.newArrayList();

        for (JsonElement jsonelement : p_230784_0_)
        {
            try
            {
                list.add(jsonelement.getAsString());
            }
            catch (Exception exception)
            {
            }
        }

        return list;
    }
}
