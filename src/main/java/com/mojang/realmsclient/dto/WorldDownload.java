package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldDownload extends ValueObject
{
    private static final Logger field_230646_d_ = LogManager.getLogger();
    public String field_230643_a_;
    public String field_230644_b_;
    public String field_230645_c_;

    public static WorldDownload func_230802_a_(String p_230802_0_)
    {
        JsonParser jsonparser = new JsonParser();
        JsonObject jsonobject = jsonparser.parse(p_230802_0_).getAsJsonObject();
        WorldDownload worlddownload = new WorldDownload();

        try
        {
            worlddownload.field_230643_a_ = JsonUtils.func_225171_a("downloadLink", jsonobject, "");
            worlddownload.field_230644_b_ = JsonUtils.func_225171_a("resourcePackUrl", jsonobject, "");
            worlddownload.field_230645_c_ = JsonUtils.func_225171_a("resourcePackHash", jsonobject, "");
        }
        catch (Exception exception)
        {
            field_230646_d_.error("Could not parse WorldDownload: " + exception.getMessage());
        }

        return worlddownload;
    }
}
