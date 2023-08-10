package com.mojang.realmsclient.client;

import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsDescriptionDto;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.RealmsWorldResetDto;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsHttpException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.realms.PersistenceSerializer;
import net.minecraft.util.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsClient
{
    public static RealmsClient.Environment field_224944_a = RealmsClient.Environment.PRODUCTION;
    private static boolean field_224945_b;
    private static final Logger field_224946_c = LogManager.getLogger();
    private final String field_224947_d;
    private final String field_224948_e;
    private final Minecraft field_244732_f;
    private static final PersistenceSerializer field_237691_f_ = new PersistenceSerializer();

    public static RealmsClient func_224911_a()
    {
        Minecraft minecraft = Minecraft.getInstance();
        String s = minecraft.getSession().getUsername();
        String s1 = minecraft.getSession().getSessionID();

        if (!field_224945_b)
        {
            field_224945_b = true;
            String s2 = System.getenv("realms.environment");

            if (s2 == null)
            {
                s2 = System.getProperty("realms.environment");
            }

            if (s2 != null)
            {
                if ("LOCAL".equals(s2))
                {
                    func_224941_d();
                }
                else if ("STAGE".equals(s2))
                {
                    func_224940_b();
                }
            }
        }

        return new RealmsClient(s1, s, minecraft);
    }

    public static void func_224940_b()
    {
        field_224944_a = RealmsClient.Environment.STAGE;
    }

    public static void func_224921_c()
    {
        field_224944_a = RealmsClient.Environment.PRODUCTION;
    }

    public static void func_224941_d()
    {
        field_224944_a = RealmsClient.Environment.LOCAL;
    }

    public RealmsClient(String p_i242128_1_, String p_i242128_2_, Minecraft p_i242128_3_)
    {
        this.field_224947_d = p_i242128_1_;
        this.field_224948_e = p_i242128_2_;
        this.field_244732_f = p_i242128_3_;
        RealmsClientConfig.func_224896_a(p_i242128_3_.getProxy());
    }

    public RealmsServerList func_224902_e() throws RealmsServiceException
    {
        String s = this.func_224926_c("worlds");
        String s1 = this.func_224938_a(Request.func_224953_a(s));
        return RealmsServerList.func_230783_a_(s1);
    }

    public RealmsServer func_224935_a(long p_224935_1_) throws RealmsServiceException
    {
        String s = this.func_224926_c("worlds" + "/$ID".replace("$ID", String.valueOf(p_224935_1_)));
        String s1 = this.func_224938_a(Request.func_224953_a(s));
        return RealmsServer.func_230779_c_(s1);
    }

    public RealmsServerPlayerLists func_224915_f() throws RealmsServiceException
    {
        String s = this.func_224926_c("activities/liveplayerlist");
        String s1 = this.func_224938_a(Request.func_224953_a(s));
        return RealmsServerPlayerLists.func_230786_a_(s1);
    }

    public RealmsServerAddress func_224904_b(long p_224904_1_) throws RealmsServiceException
    {
        String s = this.func_224926_c("worlds" + "/v1/$ID/join/pc".replace("$ID", "" + p_224904_1_));
        String s1 = this.func_224938_a(Request.func_224960_a(s, 5000, 30000));
        return RealmsServerAddress.func_230782_a_(s1);
    }

    public void func_224900_a(long p_224900_1_, String p_224900_3_, String p_224900_4_) throws RealmsServiceException
    {
        RealmsDescriptionDto realmsdescriptiondto = new RealmsDescriptionDto(p_224900_3_, p_224900_4_);
        String s = this.func_224926_c("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(p_224900_1_)));
        String s1 = field_237691_f_.func_237694_a_(realmsdescriptiondto);
        this.func_224938_a(Request.func_224959_a(s, s1, 5000, 10000));
    }

    public Boolean func_224918_g() throws RealmsServiceException
    {
        String s = this.func_224926_c("mco/available");
        String s1 = this.func_224938_a(Request.func_224953_a(s));
        return Boolean.valueOf(s1);
    }

    public Boolean func_224931_h() throws RealmsServiceException
    {
        String s = this.func_224926_c("mco/stageAvailable");
        String s1 = this.func_224938_a(Request.func_224953_a(s));
        return Boolean.valueOf(s1);
    }

    public RealmsClient.CompatibleVersionResponse func_224939_i() throws RealmsServiceException
    {
        String s = this.func_224926_c("mco/client/compatible");
        String s1 = this.func_224938_a(Request.func_224953_a(s));

        try
        {
            return RealmsClient.CompatibleVersionResponse.valueOf(s1);
        }
        catch (IllegalArgumentException illegalargumentexception)
        {
            throw new RealmsServiceException(500, "Could not check compatible version, got response: " + s1, -1, "");
        }
    }

    public void func_224908_a(long p_224908_1_, String p_224908_3_) throws RealmsServiceException
    {
        String s = this.func_224926_c("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(p_224908_1_)).replace("$UUID", p_224908_3_));
        this.func_224938_a(Request.func_224952_b(s));
    }

    public void func_224912_c(long p_224912_1_) throws RealmsServiceException
    {
        String s = this.func_224926_c("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224912_1_)));
        this.func_224938_a(Request.func_224952_b(s));
    }

    public RealmsServer func_224910_b(long p_224910_1_, String p_224910_3_) throws RealmsServiceException
    {
        PlayerInfo playerinfo = new PlayerInfo();
        playerinfo.func_230758_a_(p_224910_3_);
        String s = this.func_224926_c("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224910_1_)));
        String s1 = this.func_224938_a(Request.func_224951_b(s, field_237691_f_.func_237694_a_(playerinfo)));
        return RealmsServer.func_230779_c_(s1);
    }

    public BackupList func_224923_d(long p_224923_1_) throws RealmsServiceException
    {
        String s = this.func_224926_c("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(p_224923_1_)));
        String s1 = this.func_224938_a(Request.func_224953_a(s));
        return BackupList.func_230753_a_(s1);
    }

    public void func_224922_b(long p_224922_1_, String p_224922_3_, String p_224922_4_) throws RealmsServiceException
    {
        RealmsDescriptionDto realmsdescriptiondto = new RealmsDescriptionDto(p_224922_3_, p_224922_4_);
        String s = this.func_224926_c("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224922_1_)));
        this.func_224938_a(Request.func_224951_b(s, field_237691_f_.func_237694_a_(realmsdescriptiondto)));
    }

    public void func_224925_a(long p_224925_1_, int p_224925_3_, RealmsWorldOptions p_224925_4_) throws RealmsServiceException
    {
        String s = this.func_224926_c("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(p_224925_1_)).replace("$SLOT_ID", String.valueOf(p_224925_3_)));
        String s1 = p_224925_4_.func_230791_c_();
        this.func_224938_a(Request.func_224951_b(s, s1));
    }

    public boolean func_224927_a(long p_224927_1_, int p_224927_3_) throws RealmsServiceException
    {
        String s = this.func_224926_c("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(p_224927_1_)).replace("$SLOT_ID", String.valueOf(p_224927_3_)));
        String s1 = this.func_224938_a(Request.func_224965_c(s, ""));
        return Boolean.valueOf(s1);
    }

    public void func_224928_c(long p_224928_1_, String p_224928_3_) throws RealmsServiceException
    {
        String s = this.func_224907_b("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(p_224928_1_)), "backupId=" + p_224928_3_);
        this.func_224938_a(Request.func_224966_b(s, "", 40000, 600000));
    }

    public WorldTemplatePaginatedList func_224930_a(int p_224930_1_, int p_224930_2_, RealmsServer.ServerType p_224930_3_) throws RealmsServiceException
    {
        String s = this.func_224907_b("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", p_224930_3_.toString()), String.format("page=%d&pageSize=%d", p_224930_1_, p_224930_2_));
        String s1 = this.func_224938_a(Request.func_224953_a(s));
        return WorldTemplatePaginatedList.func_230804_a_(s1);
    }

    public Boolean func_224905_d(long p_224905_1_, String p_224905_3_) throws RealmsServiceException
    {
        String s = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", p_224905_3_).replace("$WORLD_ID", String.valueOf(p_224905_1_));
        String s1 = this.func_224926_c("worlds" + s);
        return Boolean.valueOf(this.func_224938_a(Request.func_224965_c(s1, "")));
    }

    public Ops func_224906_e(long p_224906_1_, String p_224906_3_) throws RealmsServiceException
    {
        String s = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(p_224906_1_)).replace("$PROFILE_UUID", p_224906_3_);
        String s1 = this.func_224926_c("ops" + s);
        return Ops.func_230754_a_(this.func_224938_a(Request.func_224951_b(s1, "")));
    }

    public Ops func_224929_f(long p_224929_1_, String p_224929_3_) throws RealmsServiceException
    {
        String s = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(p_224929_1_)).replace("$PROFILE_UUID", p_224929_3_);
        String s1 = this.func_224926_c("ops" + s);
        return Ops.func_230754_a_(this.func_224938_a(Request.func_224952_b(s1)));
    }

    public Boolean func_224942_e(long p_224942_1_) throws RealmsServiceException
    {
        String s = this.func_224926_c("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(p_224942_1_)));
        String s1 = this.func_224938_a(Request.func_224965_c(s, ""));
        return Boolean.valueOf(s1);
    }

    public Boolean func_224932_f(long p_224932_1_) throws RealmsServiceException
    {
        String s = this.func_224926_c("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(p_224932_1_)));
        String s1 = this.func_224938_a(Request.func_224965_c(s, ""));
        return Boolean.valueOf(s1);
    }

    public Boolean func_224943_a(long p_224943_1_, String p_224943_3_, Integer p_224943_4_, boolean p_224943_5_) throws RealmsServiceException
    {
        RealmsWorldResetDto realmsworldresetdto = new RealmsWorldResetDto(p_224943_3_, -1L, p_224943_4_, p_224943_5_);
        String s = this.func_224926_c("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(p_224943_1_)));
        String s1 = this.func_224938_a(Request.func_224959_a(s, field_237691_f_.func_237694_a_(realmsworldresetdto), 30000, 80000));
        return Boolean.valueOf(s1);
    }

    public Boolean func_224924_g(long p_224924_1_, String p_224924_3_) throws RealmsServiceException
    {
        RealmsWorldResetDto realmsworldresetdto = new RealmsWorldResetDto((String)null, Long.valueOf(p_224924_3_), -1, false);
        String s = this.func_224926_c("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(p_224924_1_)));
        String s1 = this.func_224938_a(Request.func_224959_a(s, field_237691_f_.func_237694_a_(realmsworldresetdto), 30000, 80000));
        return Boolean.valueOf(s1);
    }

    public Subscription func_224933_g(long p_224933_1_) throws RealmsServiceException
    {
        String s = this.func_224926_c("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224933_1_)));
        String s1 = this.func_224938_a(Request.func_224953_a(s));
        return Subscription.func_230793_a_(s1);
    }

    public int func_224909_j() throws RealmsServiceException
    {
        return this.func_224919_k().field_230569_a_.size();
    }

    public PendingInvitesList func_224919_k() throws RealmsServiceException
    {
        String s = this.func_224926_c("invites/pending");
        String s1 = this.func_224938_a(Request.func_224953_a(s));
        PendingInvitesList pendinginviteslist = PendingInvitesList.func_230756_a_(s1);
        pendinginviteslist.field_230569_a_.removeIf(this::func_244733_a);
        return pendinginviteslist;
    }

    private boolean func_244733_a(PendingInvite p_244733_1_)
    {
        try
        {
            UUID uuid = UUID.fromString(p_244733_1_.field_230566_d_);
            return this.field_244732_f.func_244599_aA().func_244757_e(uuid);
        }
        catch (IllegalArgumentException illegalargumentexception)
        {
            return false;
        }
    }

    public void func_224901_a(String p_224901_1_) throws RealmsServiceException
    {
        String s = this.func_224926_c("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", p_224901_1_));
        this.func_224938_a(Request.func_224965_c(s, ""));
    }

    public WorldDownload func_224917_b(long p_224917_1_, int p_224917_3_) throws RealmsServiceException
    {
        String s = this.func_224926_c("worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", String.valueOf(p_224917_1_)).replace("$SLOT_ID", String.valueOf(p_224917_3_)));
        String s1 = this.func_224938_a(Request.func_224953_a(s));
        return WorldDownload.func_230802_a_(s1);
    }

    @Nullable
    public UploadInfo func_224934_h(long p_224934_1_, @Nullable String p_224934_3_) throws RealmsServiceException
    {
        String s = this.func_224926_c("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(p_224934_1_)));
        return UploadInfo.func_230796_a_(this.func_224938_a(Request.func_224965_c(s, UploadInfo.func_243090_b(p_224934_3_))));
    }

    public void func_224913_b(String p_224913_1_) throws RealmsServiceException
    {
        String s = this.func_224926_c("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", p_224913_1_));
        this.func_224938_a(Request.func_224965_c(s, ""));
    }

    public void func_224937_l() throws RealmsServiceException
    {
        String s = this.func_224926_c("mco/tos/agreed");
        this.func_224938_a(Request.func_224951_b(s, ""));
    }

    public RealmsNews func_224920_m() throws RealmsServiceException
    {
        String s = this.func_224926_c("mco/v1/news");
        String s1 = this.func_224938_a(Request.func_224960_a(s, 5000, 10000));
        return RealmsNews.func_230767_a_(s1);
    }

    public void func_224903_a(PingResult p_224903_1_) throws RealmsServiceException
    {
        String s = this.func_224926_c("regions/ping/stat");
        this.func_224938_a(Request.func_224951_b(s, field_237691_f_.func_237694_a_(p_224903_1_)));
    }

    public Boolean func_224914_n() throws RealmsServiceException
    {
        String s = this.func_224926_c("trial");
        String s1 = this.func_224938_a(Request.func_224953_a(s));
        return Boolean.valueOf(s1);
    }

    public void func_224916_h(long p_224916_1_) throws RealmsServiceException
    {
        String s = this.func_224926_c("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224916_1_)));
        this.func_224938_a(Request.func_224952_b(s));
    }

    @Nullable
    private String func_224926_c(String p_224926_1_)
    {
        return this.func_224907_b(p_224926_1_, (String)null);
    }

    @Nullable
    private String func_224907_b(String p_224907_1_, @Nullable String p_224907_2_)
    {
        try
        {
            return (new URI(field_224944_a.field_224899_e, field_224944_a.field_224898_d, "/" + p_224907_1_, p_224907_2_, (String)null)).toASCIIString();
        }
        catch (URISyntaxException urisyntaxexception)
        {
            urisyntaxexception.printStackTrace();
            return null;
        }
    }

    private String func_224938_a(Request<?> p_224938_1_) throws RealmsServiceException
    {
        p_224938_1_.func_224962_a("sid", this.field_224947_d);
        p_224938_1_.func_224962_a("user", this.field_224948_e);
        p_224938_1_.func_224962_a("version", SharedConstants.getVersion().getName());

        try
        {
            int i = p_224938_1_.func_224958_b();

            if (i != 503 && i != 277)
            {
                String s = p_224938_1_.func_224963_c();

                if (i >= 200 && i < 300)
                {
                    return s;
                }
                else if (i == 401)
                {
                    String s1 = p_224938_1_.func_224956_c("WWW-Authenticate");
                    field_224946_c.info("Could not authorize you against Realms server: " + s1);
                    throw new RealmsServiceException(i, s1, -1, s1);
                }
                else if (s != null && s.length() != 0)
                {
                    RealmsError realmserror = RealmsError.func_241826_a_(s);
                    field_224946_c.error("Realms http code: " + i + " -  error code: " + realmserror.func_224974_b() + " -  message: " + realmserror.func_224973_a() + " - raw body: " + s);
                    throw new RealmsServiceException(i, s, realmserror);
                }
                else
                {
                    field_224946_c.error("Realms error code: " + i + " message: " + s);
                    throw new RealmsServiceException(i, s, i, "");
                }
            }
            else
            {
                int j = p_224938_1_.func_224957_a();
                throw new RetryCallException(j, i);
            }
        }
        catch (RealmsHttpException realmshttpexception)
        {
            throw new RealmsServiceException(500, "Could not connect to Realms: " + realmshttpexception.getMessage(), -1, "");
        }
    }

    public static enum CompatibleVersionResponse
    {
        COMPATIBLE,
        OUTDATED,
        OTHER;
    }

    public static enum Environment
    {
        PRODUCTION("pc.realms.minecraft.net", "https"),
        STAGE("pc-stage.realms.minecraft.net", "https"),
        LOCAL("localhost:8080", "http");

        public String field_224898_d;
        public String field_224899_e;

        private Environment(String p_i51584_3_, String p_i51584_4_)
        {
            this.field_224898_d = p_i51584_3_;
            this.field_224899_e = p_i51584_4_;
        }
    }
}
