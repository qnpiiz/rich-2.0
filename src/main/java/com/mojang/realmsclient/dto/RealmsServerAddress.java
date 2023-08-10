package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerAddress extends ValueObject
{
    private static final Logger field_230604_d_ = LogManager.getLogger();
    public String field_230601_a_;
    public String field_230602_b_;
    public String field_230603_c_;

    public static RealmsServerAddress func_230782_a_(String p_230782_0_)
    {
        JsonParser jsonparser = new JsonParser();
        RealmsServerAddress realmsserveraddress = new RealmsServerAddress();

        try
        {
            JsonObject jsonobject = jsonparser.parse(p_230782_0_).getAsJsonObject();
            realmsserveraddress.field_230601_a_ = JsonUtils.func_225171_a("address", jsonobject, (String)null);
            realmsserveraddress.field_230602_b_ = JsonUtils.func_225171_a("resourcePackUrl", jsonobject, (String)null);
            realmsserveraddress.field_230603_c_ = JsonUtils.func_225171_a("resourcePackHash", jsonobject, (String)null);
        }
        catch (Exception exception)
        {
            field_230604_d_.error("Could not parse RealmsServerAddress: " + exception.getMessage());
        }

        return realmsserveraddress;
    }
}
