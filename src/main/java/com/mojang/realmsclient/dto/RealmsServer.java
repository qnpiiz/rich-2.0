package com.mojang.realmsclient.dto;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServer extends ValueObject
{
    private static final Logger field_230600_s_ = LogManager.getLogger();
    public long field_230582_a_;
    public String field_230583_b_;
    public String field_230584_c_;
    public String field_230585_d_;
    public RealmsServer.Status field_230586_e_;
    public String field_230587_f_;
    public String field_230588_g_;
    public List<PlayerInfo> field_230589_h_;
    public Map<Integer, RealmsWorldOptions> field_230590_i_;
    public boolean field_230591_j_;
    public boolean field_230592_k_;
    public int field_230593_l_;
    public RealmsServer.ServerType field_230594_m_;
    public int field_230595_n_;
    public String field_230596_o_;
    public int field_230597_p_;
    public String field_230598_q_;
    public RealmsServerPing field_230599_r_ = new RealmsServerPing();

    public String func_230768_a_()
    {
        return this.field_230585_d_;
    }

    public String func_230775_b_()
    {
        return this.field_230584_c_;
    }

    public String func_230778_c_()
    {
        return this.field_230596_o_;
    }

    public void func_230773_a_(String p_230773_1_)
    {
        this.field_230584_c_ = p_230773_1_;
    }

    public void func_230777_b_(String p_230777_1_)
    {
        this.field_230585_d_ = p_230777_1_;
    }

    public void func_230772_a_(RealmsServerPlayerList p_230772_1_)
    {
        List<String> list = Lists.newArrayList();
        int i = 0;

        for (String s : p_230772_1_.field_230610_b_)
        {
            if (!s.equals(Minecraft.getInstance().getSession().getPlayerID()))
            {
                String s1 = "";

                try
                {
                    s1 = RealmsUtil.func_225193_a(s);
                }
                catch (Exception exception)
                {
                    field_230600_s_.error("Could not get name for " + s, (Throwable)exception);
                    continue;
                }

                list.add(s1);
                ++i;
            }
        }

        this.field_230599_r_.field_230607_a_ = String.valueOf(i);
        this.field_230599_r_.field_230608_b_ = Joiner.on('\n').join(list);
    }

    public static RealmsServer func_230770_a_(JsonObject p_230770_0_)
    {
        RealmsServer realmsserver = new RealmsServer();

        try
        {
            realmsserver.field_230582_a_ = JsonUtils.func_225169_a("id", p_230770_0_, -1L);
            realmsserver.field_230583_b_ = JsonUtils.func_225171_a("remoteSubscriptionId", p_230770_0_, (String)null);
            realmsserver.field_230584_c_ = JsonUtils.func_225171_a("name", p_230770_0_, (String)null);
            realmsserver.field_230585_d_ = JsonUtils.func_225171_a("motd", p_230770_0_, (String)null);
            realmsserver.field_230586_e_ = func_230780_d_(JsonUtils.func_225171_a("state", p_230770_0_, RealmsServer.Status.CLOSED.name()));
            realmsserver.field_230587_f_ = JsonUtils.func_225171_a("owner", p_230770_0_, (String)null);

            if (p_230770_0_.get("players") != null && p_230770_0_.get("players").isJsonArray())
            {
                realmsserver.field_230589_h_ = func_230769_a_(p_230770_0_.get("players").getAsJsonArray());
                func_230771_a_(realmsserver);
            }
            else
            {
                realmsserver.field_230589_h_ = Lists.newArrayList();
            }

            realmsserver.field_230593_l_ = JsonUtils.func_225172_a("daysLeft", p_230770_0_, 0);
            realmsserver.field_230591_j_ = JsonUtils.func_225170_a("expired", p_230770_0_, false);
            realmsserver.field_230592_k_ = JsonUtils.func_225170_a("expiredTrial", p_230770_0_, false);
            realmsserver.field_230594_m_ = func_230781_e_(JsonUtils.func_225171_a("worldType", p_230770_0_, RealmsServer.ServerType.NORMAL.name()));
            realmsserver.field_230588_g_ = JsonUtils.func_225171_a("ownerUUID", p_230770_0_, "");

            if (p_230770_0_.get("slots") != null && p_230770_0_.get("slots").isJsonArray())
            {
                realmsserver.field_230590_i_ = func_230776_b_(p_230770_0_.get("slots").getAsJsonArray());
            }
            else
            {
                realmsserver.field_230590_i_ = func_237697_e_();
            }

            realmsserver.field_230596_o_ = JsonUtils.func_225171_a("minigameName", p_230770_0_, (String)null);
            realmsserver.field_230595_n_ = JsonUtils.func_225172_a("activeSlot", p_230770_0_, -1);
            realmsserver.field_230597_p_ = JsonUtils.func_225172_a("minigameId", p_230770_0_, -1);
            realmsserver.field_230598_q_ = JsonUtils.func_225171_a("minigameImage", p_230770_0_, (String)null);
        }
        catch (Exception exception)
        {
            field_230600_s_.error("Could not parse McoServer: " + exception.getMessage());
        }

        return realmsserver;
    }

    private static void func_230771_a_(RealmsServer p_230771_0_)
    {
        p_230771_0_.field_230589_h_.sort((p_229951_0_, p_229951_1_) ->
        {
            return ComparisonChain.start().compareFalseFirst(p_229951_1_.func_230765_d_(), p_229951_0_.func_230765_d_()).compare(p_229951_0_.func_230757_a_().toLowerCase(Locale.ROOT), p_229951_1_.func_230757_a_().toLowerCase(Locale.ROOT)).result();
        });
    }

    private static List<PlayerInfo> func_230769_a_(JsonArray p_230769_0_)
    {
        List<PlayerInfo> list = Lists.newArrayList();

        for (JsonElement jsonelement : p_230769_0_)
        {
            try
            {
                JsonObject jsonobject = jsonelement.getAsJsonObject();
                PlayerInfo playerinfo = new PlayerInfo();
                playerinfo.func_230758_a_(JsonUtils.func_225171_a("name", jsonobject, (String)null));
                playerinfo.func_230761_b_(JsonUtils.func_225171_a("uuid", jsonobject, (String)null));
                playerinfo.func_230759_a_(JsonUtils.func_225170_a("operator", jsonobject, false));
                playerinfo.func_230762_b_(JsonUtils.func_225170_a("accepted", jsonobject, false));
                playerinfo.func_230764_c_(JsonUtils.func_225170_a("online", jsonobject, false));
                list.add(playerinfo);
            }
            catch (Exception exception)
            {
            }
        }

        return list;
    }

    private static Map<Integer, RealmsWorldOptions> func_230776_b_(JsonArray p_230776_0_)
    {
        Map<Integer, RealmsWorldOptions> map = Maps.newHashMap();

        for (JsonElement jsonelement : p_230776_0_)
        {
            try
            {
                JsonObject jsonobject = jsonelement.getAsJsonObject();
                JsonParser jsonparser = new JsonParser();
                JsonElement jsonelement1 = jsonparser.parse(jsonobject.get("options").getAsString());
                RealmsWorldOptions realmsworldoptions;

                if (jsonelement1 == null)
                {
                    realmsworldoptions = RealmsWorldOptions.func_237700_a_();
                }
                else
                {
                    realmsworldoptions = RealmsWorldOptions.func_230788_a_(jsonelement1.getAsJsonObject());
                }

                int i = JsonUtils.func_225172_a("slotId", jsonobject, -1);
                map.put(i, realmsworldoptions);
            }
            catch (Exception exception)
            {
            }
        }

        for (int j = 1; j <= 3; ++j)
        {
            if (!map.containsKey(j))
            {
                map.put(j, RealmsWorldOptions.func_237701_b_());
            }
        }

        return map;
    }

    private static Map<Integer, RealmsWorldOptions> func_237697_e_()
    {
        Map<Integer, RealmsWorldOptions> map = Maps.newHashMap();
        map.put(1, RealmsWorldOptions.func_237701_b_());
        map.put(2, RealmsWorldOptions.func_237701_b_());
        map.put(3, RealmsWorldOptions.func_237701_b_());
        return map;
    }

    public static RealmsServer func_230779_c_(String p_230779_0_)
    {
        try
        {
            return func_230770_a_((new JsonParser()).parse(p_230779_0_).getAsJsonObject());
        }
        catch (Exception exception)
        {
            field_230600_s_.error("Could not parse McoServer: " + exception.getMessage());
            return new RealmsServer();
        }
    }

    private static RealmsServer.Status func_230780_d_(String p_230780_0_)
    {
        try
        {
            return RealmsServer.Status.valueOf(p_230780_0_);
        }
        catch (Exception exception)
        {
            return RealmsServer.Status.CLOSED;
        }
    }

    private static RealmsServer.ServerType func_230781_e_(String p_230781_0_)
    {
        try
        {
            return RealmsServer.ServerType.valueOf(p_230781_0_);
        }
        catch (Exception exception)
        {
            return RealmsServer.ServerType.NORMAL;
        }
    }

    public int hashCode()
    {
        return Objects.hash(this.field_230582_a_, this.field_230584_c_, this.field_230585_d_, this.field_230586_e_, this.field_230587_f_, this.field_230591_j_);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (p_equals_1_ == null)
        {
            return false;
        }
        else if (p_equals_1_ == this)
        {
            return true;
        }
        else if (p_equals_1_.getClass() != this.getClass())
        {
            return false;
        }
        else
        {
            RealmsServer realmsserver = (RealmsServer)p_equals_1_;
            return (new EqualsBuilder()).append(this.field_230582_a_, realmsserver.field_230582_a_).append((Object)this.field_230584_c_, (Object)realmsserver.field_230584_c_).append((Object)this.field_230585_d_, (Object)realmsserver.field_230585_d_).append((Object)this.field_230586_e_, (Object)realmsserver.field_230586_e_).append((Object)this.field_230587_f_, (Object)realmsserver.field_230587_f_).append(this.field_230591_j_, realmsserver.field_230591_j_).append((Object)this.field_230594_m_, (Object)this.field_230594_m_).isEquals();
        }
    }

    public RealmsServer clone()
    {
        RealmsServer realmsserver = new RealmsServer();
        realmsserver.field_230582_a_ = this.field_230582_a_;
        realmsserver.field_230583_b_ = this.field_230583_b_;
        realmsserver.field_230584_c_ = this.field_230584_c_;
        realmsserver.field_230585_d_ = this.field_230585_d_;
        realmsserver.field_230586_e_ = this.field_230586_e_;
        realmsserver.field_230587_f_ = this.field_230587_f_;
        realmsserver.field_230589_h_ = this.field_230589_h_;
        realmsserver.field_230590_i_ = this.func_230774_a_(this.field_230590_i_);
        realmsserver.field_230591_j_ = this.field_230591_j_;
        realmsserver.field_230592_k_ = this.field_230592_k_;
        realmsserver.field_230593_l_ = this.field_230593_l_;
        realmsserver.field_230599_r_ = new RealmsServerPing();
        realmsserver.field_230599_r_.field_230607_a_ = this.field_230599_r_.field_230607_a_;
        realmsserver.field_230599_r_.field_230608_b_ = this.field_230599_r_.field_230608_b_;
        realmsserver.field_230594_m_ = this.field_230594_m_;
        realmsserver.field_230588_g_ = this.field_230588_g_;
        realmsserver.field_230596_o_ = this.field_230596_o_;
        realmsserver.field_230595_n_ = this.field_230595_n_;
        realmsserver.field_230597_p_ = this.field_230597_p_;
        realmsserver.field_230598_q_ = this.field_230598_q_;
        return realmsserver;
    }

    public Map<Integer, RealmsWorldOptions> func_230774_a_(Map<Integer, RealmsWorldOptions> p_230774_1_)
    {
        Map<Integer, RealmsWorldOptions> map = Maps.newHashMap();

        for (Entry<Integer, RealmsWorldOptions> entry : p_230774_1_.entrySet())
        {
            map.put(entry.getKey(), entry.getValue().clone());
        }

        return map;
    }

    public String func_237696_a_(int p_237696_1_)
    {
        return this.field_230584_c_ + " (" + this.field_230590_i_.get(p_237696_1_).func_230787_a_(p_237696_1_) + ")";
    }

    public ServerData func_244783_d(String p_244783_1_)
    {
        return new ServerData(this.field_230584_c_, p_244783_1_, false);
    }

    public static class ServerComparator implements Comparator<RealmsServer>
    {
        private final String field_223701_a;

        public ServerComparator(String p_i51687_1_)
        {
            this.field_223701_a = p_i51687_1_;
        }

        public int compare(RealmsServer p_compare_1_, RealmsServer p_compare_2_)
        {
            return ComparisonChain.start().compareTrueFirst(p_compare_1_.field_230586_e_ == RealmsServer.Status.UNINITIALIZED, p_compare_2_.field_230586_e_ == RealmsServer.Status.UNINITIALIZED).compareTrueFirst(p_compare_1_.field_230592_k_, p_compare_2_.field_230592_k_).compareTrueFirst(p_compare_1_.field_230587_f_.equals(this.field_223701_a), p_compare_2_.field_230587_f_.equals(this.field_223701_a)).compareFalseFirst(p_compare_1_.field_230591_j_, p_compare_2_.field_230591_j_).compareTrueFirst(p_compare_1_.field_230586_e_ == RealmsServer.Status.OPEN, p_compare_2_.field_230586_e_ == RealmsServer.Status.OPEN).compare(p_compare_1_.field_230582_a_, p_compare_2_.field_230582_a_).result();
        }
    }

    public static enum ServerType
    {
        NORMAL,
        MINIGAME,
        ADVENTUREMAP,
        EXPERIENCE,
        INSPIRATION;
    }

    public static enum Status
    {
        CLOSED,
        OPEN,
        UNINITIALIZED;
    }
}
