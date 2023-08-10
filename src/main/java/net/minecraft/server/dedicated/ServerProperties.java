package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;

public class ServerProperties extends PropertyManager<ServerProperties>
{
    public final boolean onlineMode = this.registerBool("online-mode", true);
    public final boolean preventProxyConnections = this.registerBool("prevent-proxy-connections", false);
    public final String serverIp = this.registerString("server-ip", "");
    public final boolean spawnAnimals = this.registerBool("spawn-animals", true);
    public final boolean spawnNPCs = this.registerBool("spawn-npcs", true);
    public final boolean allowPvp = this.registerBool("pvp", true);
    public final boolean allowFlight = this.registerBool("allow-flight", false);
    public final String resourcePack = this.registerString("resource-pack", "");
    public final String motd = this.registerString("motd", "A Minecraft Server");
    public final boolean forceGamemode = this.registerBool("force-gamemode", false);
    public final boolean enforceWhitelist = this.registerBool("enforce-whitelist", false);
    public final Difficulty difficulty = this.func_218983_a("difficulty", enumConverter(Difficulty::byId, Difficulty::byName), Difficulty::getTranslationKey, Difficulty.EASY);
    public final GameType gamemode = this.func_218983_a("gamemode", enumConverter(GameType::getByID, GameType::getByName), GameType::getName, GameType.SURVIVAL);
    public final String worldName = this.registerString("level-name", "world");
    public final int serverPort = this.registerInt("server-port", 25565);
    public final int maxBuildHeight = this.func_218962_a("max-build-height", (p_218987_0_) ->
    {
        return MathHelper.clamp((p_218987_0_ + 8) / 16 * 16, 64, 256);
    }, 256);
    public final Boolean announceAdvancements = this.func_218978_b("announce-player-achievements");
    public final boolean enableQuery = this.registerBool("enable-query", false);
    public final int queryPort = this.registerInt("query.port", 25565);
    public final boolean enableRcon = this.registerBool("enable-rcon", false);
    public final int rconPort = this.registerInt("rcon.port", 25575);
    public final String rconPassword = this.registerString("rcon.password", "");

    /** Deprecated. Use resourcePackSha1 instead. */
    public final String resourcePackHash = this.func_218980_a("resource-pack-hash");
    public final String resourcePackSha1 = this.registerString("resource-pack-sha1", "");
    public final boolean hardcore = this.registerBool("hardcore", false);
    public final boolean allowNether = this.registerBool("allow-nether", true);
    public final boolean spawnMonsters = this.registerBool("spawn-monsters", true);
    public final boolean field_218993_F;
    public final boolean useNativeTransport;
    public final boolean enableCommandBlock;
    public final int spawnProtection;
    public final int opPermissionLevel;
    public final int functionPermissionLevel;
    public final long maxTickTime;
    public final int rateLimit;
    public final int viewDistance;
    public final int maxPlayers;
    public final int networkCompressionThreshold;
    public final boolean broadcastRconToOps;
    public final boolean broadcastConsoleToOps;
    public final int maxWorldSize;
    public final boolean field_241078_O_;
    public final boolean field_241079_P_;
    public final boolean field_241080_Q_;
    public final int field_241081_R_;
    public final String field_244715_T;
    public final PropertyManager<ServerProperties>.Property<Integer> playerIdleTimeout;
    public final PropertyManager<ServerProperties>.Property<Boolean> whitelistEnabled;
    public final DimensionGeneratorSettings field_241082_U_;

    public ServerProperties(Properties p_i242099_1_, DynamicRegistries p_i242099_2_)
    {
        super(p_i242099_1_);

        if (this.registerBool("snooper-enabled", true))
        {
        }

        this.field_218993_F = false;
        this.useNativeTransport = this.registerBool("use-native-transport", true);
        this.enableCommandBlock = this.registerBool("enable-command-block", false);
        this.spawnProtection = this.registerInt("spawn-protection", 16);
        this.opPermissionLevel = this.registerInt("op-permission-level", 4);
        this.functionPermissionLevel = this.registerInt("function-permission-level", 2);
        this.maxTickTime = this.func_218967_a("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
        this.rateLimit = this.registerInt("rate-limit", 0);
        this.viewDistance = this.registerInt("view-distance", 10);
        this.maxPlayers = this.registerInt("max-players", 20);
        this.networkCompressionThreshold = this.registerInt("network-compression-threshold", 256);
        this.broadcastRconToOps = this.registerBool("broadcast-rcon-to-ops", true);
        this.broadcastConsoleToOps = this.registerBool("broadcast-console-to-ops", true);
        this.maxWorldSize = this.func_218962_a("max-world-size", (p_218986_0_) ->
        {
            return MathHelper.clamp(p_218986_0_, 1, 29999984);
        }, 29999984);
        this.field_241078_O_ = this.registerBool("sync-chunk-writes", true);
        this.field_241079_P_ = this.registerBool("enable-jmx-monitoring", false);
        this.field_241080_Q_ = this.registerBool("enable-status", true);
        this.field_241081_R_ = this.func_218962_a("entity-broadcast-range-percentage", (p_241083_0_) ->
        {
            return MathHelper.clamp(p_241083_0_, 10, 1000);
        }, 100);
        this.field_244715_T = this.registerString("text-filtering-config", "");
        this.playerIdleTimeout = this.func_218974_b("player-idle-timeout", 0);
        this.whitelistEnabled = this.func_218961_b("white-list", false);
        this.field_241082_U_ = DimensionGeneratorSettings.func_242753_a(p_i242099_2_, p_i242099_1_);
    }

    public static ServerProperties func_244380_a(DynamicRegistries registries, Path p_244380_1_)
    {
        return new ServerProperties(load(p_244380_1_), registries);
    }

    protected ServerProperties func_241881_b(DynamicRegistries p_241881_1_, Properties p_241881_2_)
    {
        return new ServerProperties(p_241881_2_, p_241881_1_);
    }
}
