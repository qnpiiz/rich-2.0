package com.mojang.realmsclient.dto;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UploadInfo extends ValueObject
{
    private static final Logger field_230638_a_ = LogManager.getLogger();
    private static final Pattern field_243085_b = Pattern.compile("^[a-zA-Z][-a-zA-Z0-9+.]+:");
    private final boolean field_230639_b_;
    @Nullable
    private final String field_230640_c_;
    private final URI field_230641_d_;

    private UploadInfo(boolean p_i242046_1_, @Nullable String p_i242046_2_, URI p_i242046_3_)
    {
        this.field_230639_b_ = p_i242046_1_;
        this.field_230640_c_ = p_i242046_2_;
        this.field_230641_d_ = p_i242046_3_;
    }

    @Nullable
    public static UploadInfo func_230796_a_(String p_230796_0_)
    {
        try
        {
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = jsonparser.parse(p_230796_0_).getAsJsonObject();
            String s = JsonUtils.func_225171_a("uploadEndpoint", jsonobject, (String)null);

            if (s != null)
            {
                int i = JsonUtils.func_225172_a("port", jsonobject, -1);
                URI uri = func_243087_a(s, i);

                if (uri != null)
                {
                    boolean flag = JsonUtils.func_225170_a("worldClosed", jsonobject, false);
                    String s1 = JsonUtils.func_225171_a("token", jsonobject, (String)null);
                    return new UploadInfo(flag, s1, uri);
                }
            }
        }
        catch (Exception exception)
        {
            field_230638_a_.error("Could not parse UploadInfo: " + exception.getMessage());
        }

        return null;
    }

    @Nullable
    @VisibleForTesting
    public static URI func_243087_a(String p_243087_0_, int p_243087_1_)
    {
        Matcher matcher = field_243085_b.matcher(p_243087_0_);
        String s = func_243088_a(p_243087_0_, matcher);

        try
        {
            URI uri = new URI(s);
            int i = func_243086_a(p_243087_1_, uri.getPort());
            return i != uri.getPort() ? new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), i, uri.getPath(), uri.getQuery(), uri.getFragment()) : uri;
        }
        catch (URISyntaxException urisyntaxexception)
        {
            field_230638_a_.warn("Failed to parse URI {}", s, urisyntaxexception);
            return null;
        }
    }

    private static int func_243086_a(int p_243086_0_, int p_243086_1_)
    {
        if (p_243086_0_ != -1)
        {
            return p_243086_0_;
        }
        else
        {
            return p_243086_1_ != -1 ? p_243086_1_ : 8080;
        }
    }

    private static String func_243088_a(String p_243088_0_, Matcher p_243088_1_)
    {
        return p_243088_1_.find() ? p_243088_0_ : "http://" + p_243088_0_;
    }

    public static String func_243090_b(@Nullable String p_243090_0_)
    {
        JsonObject jsonobject = new JsonObject();

        if (p_243090_0_ != null)
        {
            jsonobject.addProperty("token", p_243090_0_);
        }

        return jsonobject.toString();
    }

    @Nullable
    public String func_230795_a_()
    {
        return this.field_230640_c_;
    }

    public URI func_243089_b()
    {
        return this.field_230641_d_;
    }

    public boolean func_230799_c_()
    {
        return this.field_230639_b_;
    }
}
