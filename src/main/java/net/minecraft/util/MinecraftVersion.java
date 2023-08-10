package net.minecraft.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.bridge.game.GameVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftVersion implements GameVersion
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final GameVersion GAME_VERSION = new MinecraftVersion();
    private final String id;
    private final String name;
    private final boolean stable;
    private final int worldVersion;
    private final int protocolVersion;
    private final int packVersion;
    private final Date buildTime;
    private final String releaseTarget;

    private MinecraftVersion()
    {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.name = "1.16.5";
        this.stable = true;
        this.worldVersion = 2586;
        this.protocolVersion = SharedConstants.func_244709_b();
        this.packVersion = 6;
        this.buildTime = new Date();
        this.releaseTarget = "1.16.5";
    }

    private MinecraftVersion(JsonObject json)
    {
        this.id = JSONUtils.getString(json, "id");
        this.name = JSONUtils.getString(json, "name");
        this.releaseTarget = JSONUtils.getString(json, "release_target");
        this.stable = JSONUtils.getBoolean(json, "stable");
        this.worldVersion = JSONUtils.getInt(json, "world_version");
        this.protocolVersion = JSONUtils.getInt(json, "protocol_version");
        this.packVersion = JSONUtils.getInt(json, "pack_version");
        this.buildTime = Date.from(ZonedDateTime.parse(JSONUtils.getString(json, "build_time")).toInstant());
    }

    /**
     * Creates a new instance containing game version data from version.json (or fallback data if necessary).
     *  
     * For getting data, use {@link SharedConstants#getVersion} instead, as that is cached.
     */
    public static GameVersion load()
    {
        try (InputStream inputstream = MinecraftVersion.class.getResourceAsStream("/version.json"))
        {
            if (inputstream == null)
            {
                LOGGER.warn("Missing version information!");
                return GAME_VERSION;
            }
            else
            {
                MinecraftVersion minecraftversion;

                try (InputStreamReader inputstreamreader = new InputStreamReader(inputstream))
                {
                    minecraftversion = new MinecraftVersion(JSONUtils.fromJson(inputstreamreader));
                }

                return minecraftversion;
            }
        }
        catch (JsonParseException | IOException ioexception)
        {
            throw new IllegalStateException("Game version information is corrupt", ioexception);
        }
    }

    public String getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public String getReleaseTarget()
    {
        return this.releaseTarget;
    }

    public int getWorldVersion()
    {
        return this.worldVersion;
    }

    public int getProtocolVersion()
    {
        return this.protocolVersion;
    }

    public int getPackVersion()
    {
        return this.packVersion;
    }

    public Date getBuildTime()
    {
        return this.buildTime;
    }

    public boolean isStable()
    {
        return this.stable;
    }
}
