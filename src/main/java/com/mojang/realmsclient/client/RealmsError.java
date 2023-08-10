package com.mojang.realmsclient.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsError
{
    private static final Logger field_224975_a = LogManager.getLogger();
    private final String field_224976_b;
    private final int field_224977_c;

    private RealmsError(String p_i241823_1_, int p_i241823_2_)
    {
        this.field_224976_b = p_i241823_1_;
        this.field_224977_c = p_i241823_2_;
    }

    public static RealmsError func_241826_a_(String p_241826_0_)
    {
        try
        {
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = jsonparser.parse(p_241826_0_).getAsJsonObject();
            String s = JsonUtils.func_225171_a("errorMsg", jsonobject, "");
            int i = JsonUtils.func_225172_a("errorCode", jsonobject, -1);
            return new RealmsError(s, i);
        }
        catch (Exception exception)
        {
            field_224975_a.error("Could not parse RealmsError: " + exception.getMessage());
            field_224975_a.error("The error was: " + p_241826_0_);
            return new RealmsError("Failed to parse response from server", -1);
        }
    }

    public String func_224973_a()
    {
        return this.field_224976_b;
    }

    public int func_224974_b()
    {
        return this.field_224977_c;
    }
}
