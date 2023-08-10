package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsNews extends ValueObject
{
    private static final Logger field_230581_b_ = LogManager.getLogger();
    public String field_230580_a_;

    public static RealmsNews func_230767_a_(String p_230767_0_)
    {
        RealmsNews realmsnews = new RealmsNews();

        try
        {
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = jsonparser.parse(p_230767_0_).getAsJsonObject();
            realmsnews.field_230580_a_ = JsonUtils.func_225171_a("newsLink", jsonobject, (String)null);
        }
        catch (Exception exception)
        {
            field_230581_b_.error("Could not parse RealmsNews: " + exception.getMessage());
        }

        return realmsnews;
    }
}
