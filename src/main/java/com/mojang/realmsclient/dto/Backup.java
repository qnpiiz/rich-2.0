package com.mojang.realmsclient.dto;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Backup extends ValueObject
{
    private static final Logger field_230558_f_ = LogManager.getLogger();
    public String field_230553_a_;
    public Date field_230554_b_;
    public long field_230555_c_;
    private boolean field_230559_g_;
    public Map<String, String> field_230556_d_ = Maps.newHashMap();
    public Map<String, String> field_230557_e_ = Maps.newHashMap();

    public static Backup func_230750_a_(JsonElement p_230750_0_)
    {
        JsonObject jsonobject = p_230750_0_.getAsJsonObject();
        Backup backup = new Backup();

        try
        {
            backup.field_230553_a_ = JsonUtils.func_225171_a("backupId", jsonobject, "");
            backup.field_230554_b_ = JsonUtils.func_225173_a("lastModifiedDate", jsonobject);
            backup.field_230555_c_ = JsonUtils.func_225169_a("size", jsonobject, 0L);

            if (jsonobject.has("metadata"))
            {
                JsonObject jsonobject1 = jsonobject.getAsJsonObject("metadata");

                for (Entry<String, JsonElement> entry : jsonobject1.entrySet())
                {
                    if (!entry.getValue().isJsonNull())
                    {
                        backup.field_230556_d_.put(func_230751_a_(entry.getKey()), entry.getValue().getAsString());
                    }
                }
            }
        }
        catch (Exception exception)
        {
            field_230558_f_.error("Could not parse Backup: " + exception.getMessage());
        }

        return backup;
    }

    private static String func_230751_a_(String p_230751_0_)
    {
        String[] astring = p_230751_0_.split("_");
        StringBuilder stringbuilder = new StringBuilder();

        for (String s : astring)
        {
            if (s != null && s.length() >= 1)
            {
                if ("of".equals(s))
                {
                    stringbuilder.append(s).append(" ");
                }
                else
                {
                    char c0 = Character.toUpperCase(s.charAt(0));
                    stringbuilder.append(c0).append(s.substring(1)).append(" ");
                }
            }
        }

        return stringbuilder.toString();
    }

    public boolean func_230749_a_()
    {
        return this.field_230559_g_;
    }

    public void func_230752_a_(boolean p_230752_1_)
    {
        this.field_230559_g_ = p_230752_1_;
    }
}
