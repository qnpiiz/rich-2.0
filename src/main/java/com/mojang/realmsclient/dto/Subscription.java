package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Subscription extends ValueObject
{
    private static final Logger field_230637_d_ = LogManager.getLogger();
    public long field_230634_a_;
    public int field_230635_b_;
    public Subscription.Type field_230636_c_ = Subscription.Type.NORMAL;

    public static Subscription func_230793_a_(String p_230793_0_)
    {
        Subscription subscription = new Subscription();

        try
        {
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = jsonparser.parse(p_230793_0_).getAsJsonObject();
            subscription.field_230634_a_ = JsonUtils.func_225169_a("startDate", jsonobject, 0L);
            subscription.field_230635_b_ = JsonUtils.func_225172_a("daysLeft", jsonobject, 0);
            subscription.field_230636_c_ = func_230794_b_(JsonUtils.func_225171_a("subscriptionType", jsonobject, Subscription.Type.NORMAL.name()));
        }
        catch (Exception exception)
        {
            field_230637_d_.error("Could not parse Subscription: " + exception.getMessage());
        }

        return subscription;
    }

    private static Subscription.Type func_230794_b_(String p_230794_0_)
    {
        try
        {
            return Subscription.Type.valueOf(p_230794_0_);
        }
        catch (Exception exception)
        {
            return Subscription.Type.NORMAL;
        }
    }

    public static enum Type
    {
        NORMAL,
        RECURRING;
    }
}
