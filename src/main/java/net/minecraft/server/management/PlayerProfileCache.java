package net.minecraft.server.management;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerProfileCache
{
    private static final Logger field_242114_a = LogManager.getLogger();
    private static boolean onlineMode;
    private final Map<String, PlayerProfileCache.ProfileEntry> usernameToProfileEntryMap = Maps.newConcurrentMap();
    private final Map<UUID, PlayerProfileCache.ProfileEntry> uuidToProfileEntryMap = Maps.newConcurrentMap();
    private final GameProfileRepository profileRepo;
    private final Gson gson = (new GsonBuilder()).create();
    private final File usercacheFile;
    private final AtomicLong field_242115_h = new AtomicLong();

    public PlayerProfileCache(GameProfileRepository profileRepoIn, File usercacheFileIn)
    {
        this.profileRepo = profileRepoIn;
        this.usercacheFile = usercacheFileIn;
        Lists.reverse(this.func_242116_a()).forEach(this::func_242118_a);
    }

    private void func_242118_a(PlayerProfileCache.ProfileEntry p_242118_1_)
    {
        GameProfile gameprofile = p_242118_1_.getGameProfile();
        p_242118_1_.func_242126_a(this.func_242123_d());
        String s = gameprofile.getName();

        if (s != null)
        {
            this.usernameToProfileEntryMap.put(s.toLowerCase(Locale.ROOT), p_242118_1_);
        }

        UUID uuid = gameprofile.getId();

        if (uuid != null)
        {
            this.uuidToProfileEntryMap.put(uuid, p_242118_1_);
        }
    }

    @Nullable
    private static GameProfile lookupProfile(GameProfileRepository profileRepoIn, String name)
    {
        final AtomicReference<GameProfile> atomicreference = new AtomicReference<>();
        ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback()
        {
            public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_)
            {
                atomicreference.set(p_onProfileLookupSucceeded_1_);
            }
            public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_)
            {
                atomicreference.set((GameProfile)null);
            }
        };
        profileRepoIn.findProfilesByNames(new String[] {name}, Agent.MINECRAFT, profilelookupcallback);
        GameProfile gameprofile = atomicreference.get();

        if (!isOnlineMode() && gameprofile == null)
        {
            UUID uuid = PlayerEntity.getUUID(new GameProfile((UUID)null, name));
            gameprofile = new GameProfile(uuid, name);
        }

        return gameprofile;
    }

    public static void setOnlineMode(boolean onlineModeIn)
    {
        onlineMode = onlineModeIn;
    }

    private static boolean isOnlineMode()
    {
        return onlineMode;
    }

    /**
     * Add an entry to this cache
     */
    public void addEntry(GameProfile gameProfile)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(2, 1);
        Date date = calendar.getTime();
        PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = new PlayerProfileCache.ProfileEntry(gameProfile, date);
        this.func_242118_a(playerprofilecache$profileentry);
        this.save();
    }

    private long func_242123_d()
    {
        return this.field_242115_h.incrementAndGet();
    }

    @Nullable

    /**
     * Get a player's GameProfile given their username. Mojang's server's will be contacted if the entry is not cached
     * locally.
     */
    public GameProfile getGameProfileForUsername(String username)
    {
        String s = username.toLowerCase(Locale.ROOT);
        PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.usernameToProfileEntryMap.get(s);
        boolean flag = false;

        if (playerprofilecache$profileentry != null && (new Date()).getTime() >= playerprofilecache$profileentry.expirationDate.getTime())
        {
            this.uuidToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getId());
            this.usernameToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getName().toLowerCase(Locale.ROOT));
            flag = true;
            playerprofilecache$profileentry = null;
        }

        GameProfile gameprofile;

        if (playerprofilecache$profileentry != null)
        {
            playerprofilecache$profileentry.func_242126_a(this.func_242123_d());
            gameprofile = playerprofilecache$profileentry.getGameProfile();
        }
        else
        {
            gameprofile = lookupProfile(this.profileRepo, s);

            if (gameprofile != null)
            {
                this.addEntry(gameprofile);
                flag = false;
            }
        }

        if (flag)
        {
            this.save();
        }

        return gameprofile;
    }

    @Nullable

    /**
     * Get a player's {@link GameProfile} given their UUID
     */
    public GameProfile getProfileByUUID(UUID uuid)
    {
        PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.uuidToProfileEntryMap.get(uuid);

        if (playerprofilecache$profileentry == null)
        {
            return null;
        }
        else
        {
            playerprofilecache$profileentry.func_242126_a(this.func_242123_d());
            return playerprofilecache$profileentry.getGameProfile();
        }
    }

    private static DateFormat func_242124_e()
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    }

    public List<PlayerProfileCache.ProfileEntry> func_242116_a()
    {
        List<PlayerProfileCache.ProfileEntry> list = Lists.newArrayList();

        try (Reader reader = Files.newReader(this.usercacheFile, StandardCharsets.UTF_8))
        {
            JsonArray jsonarray = this.gson.fromJson(reader, JsonArray.class);

            if (jsonarray == null)
            {
                return list;
            }

            DateFormat dateformat = func_242124_e();
            jsonarray.forEach((p_242122_2_) ->
            {
                PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = func_242121_a(p_242122_2_, dateformat);

                if (playerprofilecache$profileentry != null)
                {
                    list.add(playerprofilecache$profileentry);
                }
            });
        }
        catch (FileNotFoundException filenotfoundexception)
        {
        }
        catch (JsonParseException | IOException ioexception)
        {
            field_242114_a.warn("Failed to load profile cache {}", this.usercacheFile, ioexception);
        }

        return list;
    }

    /**
     * Save the cached profiles to disk
     */
    public void save()
    {
        JsonArray jsonarray = new JsonArray();
        DateFormat dateformat = func_242124_e();
        this.func_242117_a(1000).forEach((p_242120_2_) ->
        {
            jsonarray.add(func_242119_a(p_242120_2_, dateformat));
        });
        String s = this.gson.toJson((JsonElement)jsonarray);

        try (Writer writer = Files.newWriter(this.usercacheFile, StandardCharsets.UTF_8))
        {
            writer.write(s);
        }
        catch (IOException ioexception)
        {
        }
    }

    private Stream<PlayerProfileCache.ProfileEntry> func_242117_a(int p_242117_1_)
    {
        return ImmutableList.copyOf(this.uuidToProfileEntryMap.values()).stream().sorted(Comparator.comparing(PlayerProfileCache.ProfileEntry::func_242128_c).reversed()).limit((long)p_242117_1_);
    }

    private static JsonElement func_242119_a(PlayerProfileCache.ProfileEntry p_242119_0_, DateFormat p_242119_1_)
    {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("name", p_242119_0_.getGameProfile().getName());
        UUID uuid = p_242119_0_.getGameProfile().getId();
        jsonobject.addProperty("uuid", uuid == null ? "" : uuid.toString());
        jsonobject.addProperty("expiresOn", p_242119_1_.format(p_242119_0_.getExpirationDate()));
        return jsonobject;
    }

    @Nullable
    private static PlayerProfileCache.ProfileEntry func_242121_a(JsonElement p_242121_0_, DateFormat p_242121_1_)
    {
        if (p_242121_0_.isJsonObject())
        {
            JsonObject jsonobject = p_242121_0_.getAsJsonObject();
            JsonElement jsonelement = jsonobject.get("name");
            JsonElement jsonelement1 = jsonobject.get("uuid");
            JsonElement jsonelement2 = jsonobject.get("expiresOn");

            if (jsonelement != null && jsonelement1 != null)
            {
                String s = jsonelement1.getAsString();
                String s1 = jsonelement.getAsString();
                Date date = null;

                if (jsonelement2 != null)
                {
                    try
                    {
                        date = p_242121_1_.parse(jsonelement2.getAsString());
                    }
                    catch (ParseException parseexception)
                    {
                    }
                }

                if (s1 != null && s != null && date != null)
                {
                    UUID uuid;

                    try
                    {
                        uuid = UUID.fromString(s);
                    }
                    catch (Throwable throwable)
                    {
                        return null;
                    }

                    return new PlayerProfileCache.ProfileEntry(new GameProfile(uuid, s1), date);
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    static class ProfileEntry
    {
        private final GameProfile gameProfile;
        private final Date expirationDate;
        private volatile long field_242125_c;

        private ProfileEntry(GameProfile p_i241888_1_, Date p_i241888_2_)
        {
            this.gameProfile = p_i241888_1_;
            this.expirationDate = p_i241888_2_;
        }

        public GameProfile getGameProfile()
        {
            return this.gameProfile;
        }

        public Date getExpirationDate()
        {
            return this.expirationDate;
        }

        public void func_242126_a(long p_242126_1_)
        {
            this.field_242125_c = p_242126_1_;
        }

        public long func_242128_c()
        {
            return this.field_242125_c;
        }
    }
}
