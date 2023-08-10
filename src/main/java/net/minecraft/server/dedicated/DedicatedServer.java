package net.minecraft.server.dedicated;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.network.rcon.IServer;
import net.minecraft.network.rcon.MainThread;
import net.minecraft.network.rcon.QueryThread;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.profiler.Snooper;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerPropertiesProvider;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.DefaultWithNameUncaughtExceptionHandler;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.filter.ChatFilterClient;
import net.minecraft.util.text.filter.IChatFilter;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedServer extends MinecraftServer implements IServer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern RESOURCE_PACK_SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
    private final List<PendingCommand> pendingCommandList = Collections.synchronizedList(Lists.newArrayList());
    private QueryThread rconQueryThread;
    private final RConConsoleSource rconConsoleSource;
    private MainThread rconThread;
    private final ServerPropertiesProvider settings;
    @Nullable
    private MinecraftServerGui serverGui;
    @Nullable
    private final ChatFilterClient field_244714_r;

    public DedicatedServer(Thread p_i232601_1_, DynamicRegistries.Impl p_i232601_2_, SaveFormat.LevelSave p_i232601_3_, ResourcePackList p_i232601_4_, DataPackRegistries p_i232601_5_, IServerConfiguration p_i232601_6_, ServerPropertiesProvider p_i232601_7_, DataFixer p_i232601_8_, MinecraftSessionService p_i232601_9_, GameProfileRepository p_i232601_10_, PlayerProfileCache p_i232601_11_, IChunkStatusListenerFactory p_i232601_12_)
    {
        super(p_i232601_1_, p_i232601_2_, p_i232601_3_, p_i232601_6_, p_i232601_4_, Proxy.NO_PROXY, p_i232601_8_, p_i232601_5_, p_i232601_9_, p_i232601_10_, p_i232601_11_, p_i232601_12_);
        this.settings = p_i232601_7_;
        this.rconConsoleSource = new RConConsoleSource(this);
        this.field_244714_r = null;
    }

    /**
     * Initialises the server and starts it.
     */
    public boolean init() throws IOException
    {
        Thread thread = new Thread("Server console handler")
        {
            public void run()
            {
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                String s1;

                try
                {
                    while (!DedicatedServer.this.isServerStopped() && DedicatedServer.this.isServerRunning() && (s1 = bufferedreader.readLine()) != null)
                    {
                        DedicatedServer.this.handleConsoleInput(s1, DedicatedServer.this.getCommandSource());
                    }
                }
                catch (IOException ioexception1)
                {
                    DedicatedServer.LOGGER.error("Exception handling console input", (Throwable)ioexception1);
                }
            }
        };
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
        LOGGER.info("Starting minecraft server version " + SharedConstants.getVersion().getName());

        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L)
        {
            LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        LOGGER.info("Loading properties");
        ServerProperties serverproperties = this.settings.getProperties();

        if (this.isSinglePlayer())
        {
            this.setHostname("127.0.0.1");
        }
        else
        {
            this.setOnlineMode(serverproperties.onlineMode);
            this.setPreventProxyConnections(serverproperties.preventProxyConnections);
            this.setHostname(serverproperties.serverIp);
        }

        this.setAllowPvp(serverproperties.allowPvp);
        this.setAllowFlight(serverproperties.allowFlight);
        this.setResourcePack(serverproperties.resourcePack, this.loadResourcePackSHA());
        this.setMOTD(serverproperties.motd);
        this.setForceGamemode(serverproperties.forceGamemode);
        super.setPlayerIdleTimeout(serverproperties.playerIdleTimeout.get());
        this.setWhitelistEnabled(serverproperties.enforceWhitelist);
        this.field_240768_i_.setGameType(serverproperties.gamemode);
        LOGGER.info("Default game type: {}", (Object)serverproperties.gamemode);
        InetAddress inetaddress = null;

        if (!this.getServerHostname().isEmpty())
        {
            inetaddress = InetAddress.getByName(this.getServerHostname());
        }

        if (this.getServerPort() < 0)
        {
            this.setServerPort(serverproperties.serverPort);
        }

        this.func_244801_P();
        LOGGER.info("Starting Minecraft server on {}:{}", this.getServerHostname().isEmpty() ? "*" : this.getServerHostname(), this.getServerPort());

        try
        {
            this.getNetworkSystem().addEndpoint(inetaddress, this.getServerPort());
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("**** FAILED TO BIND TO PORT!");
            LOGGER.warn("The exception was: {}", (Object)ioexception.toString());
            LOGGER.warn("Perhaps a server is already running on that port?");
            return false;
        }

        if (!this.isServerInOnlineMode())
        {
            LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
            LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }

        if (this.convertFiles())
        {
            this.getPlayerProfileCache().save();
        }

        if (!PreYggdrasilConverter.func_219587_e(this))
        {
            return false;
        }
        else
        {
            this.setPlayerList(new DedicatedPlayerList(this, this.field_240767_f_, this.playerDataManager));
            long i = Util.nanoTime();
            this.setBuildLimit(serverproperties.maxBuildHeight);
            SkullTileEntity.setProfileCache(this.getPlayerProfileCache());
            SkullTileEntity.setSessionService(this.getMinecraftSessionService());
            PlayerProfileCache.setOnlineMode(this.isServerInOnlineMode());
            LOGGER.info("Preparing level \"{}\"", (Object)this.func_230542_k__());
            this.func_240800_l__();
            long j = Util.nanoTime() - i;
            String s = String.format(Locale.ROOT, "%.3fs", (double)j / 1.0E9D);
            LOGGER.info("Done ({})! For help, type \"help\"", (Object)s);

            if (serverproperties.announceAdvancements != null)
            {
                this.getGameRules().get(GameRules.ANNOUNCE_ADVANCEMENTS).set(serverproperties.announceAdvancements, this);
            }

            if (serverproperties.enableQuery)
            {
                LOGGER.info("Starting GS4 status listener");
                this.rconQueryThread = QueryThread.func_242129_a(this);
            }

            if (serverproperties.enableRcon)
            {
                LOGGER.info("Starting remote control listener");
                this.rconThread = MainThread.func_242130_a(this);
            }

            if (this.getMaxTickTime() > 0L)
            {
                Thread thread1 = new Thread(new ServerHangWatchdog(this));
                thread1.setUncaughtExceptionHandler(new DefaultWithNameUncaughtExceptionHandler(LOGGER));
                thread1.setName("Server Watchdog");
                thread1.setDaemon(true);
                thread1.start();
            }

            Items.AIR.fillItemGroup(ItemGroup.SEARCH, NonNullList.create());

            if (serverproperties.field_241079_P_)
            {
                ServerInfoMBean.func_233490_a_(this);
            }

            return true;
        }
    }

    public boolean func_230537_U_()
    {
        return this.getServerProperties().spawnAnimals && super.func_230537_U_();
    }

    public boolean func_230536_N_()
    {
        return this.settings.getProperties().spawnMonsters && super.func_230536_N_();
    }

    public boolean func_230538_V_()
    {
        return this.settings.getProperties().spawnNPCs && super.func_230538_V_();
    }

    public String loadResourcePackSHA()
    {
        ServerProperties serverproperties = this.settings.getProperties();
        String s;

        if (!serverproperties.resourcePackSha1.isEmpty())
        {
            s = serverproperties.resourcePackSha1;

            if (!Strings.isNullOrEmpty(serverproperties.resourcePackHash))
            {
                LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
        }
        else if (!Strings.isNullOrEmpty(serverproperties.resourcePackHash))
        {
            LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            s = serverproperties.resourcePackHash;
        }
        else
        {
            s = "";
        }

        if (!s.isEmpty() && !RESOURCE_PACK_SHA1_PATTERN.matcher(s).matches())
        {
            LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
        }

        if (!serverproperties.resourcePack.isEmpty() && s.isEmpty())
        {
            LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
        }

        return s;
    }

    public ServerProperties getServerProperties()
    {
        return this.settings.getProperties();
    }

    public void func_230543_p_()
    {
        this.setDifficultyForAllWorlds(this.getServerProperties().difficulty, true);
    }

    /**
     * Defaults to false.
     */
    public boolean isHardcore()
    {
        return this.getServerProperties().hardcore;
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport addServerInfoToCrashReport(CrashReport report)
    {
        report = super.addServerInfoToCrashReport(report);
        report.getCategory().addDetail("Is Modded", () ->
        {
            return this.func_230045_q_().orElse("Unknown (can't tell)");
        });
        report.getCategory().addDetail("Type", () ->
        {
            return "Dedicated Server (map_server.txt)";
        });
        return report;
    }

    public Optional<String> func_230045_q_()
    {
        String s = this.getServerModName();
        return !"vanilla".equals(s) ? Optional.of("Definitely; Server brand changed to '" + s + "'") : Optional.empty();
    }

    /**
     * Directly calls System.exit(0), instantly killing the program.
     */
    public void systemExitNow()
    {
        if (this.field_244714_r != null)
        {
            this.field_244714_r.close();
        }

        if (this.serverGui != null)
        {
            this.serverGui.func_219050_b();
        }

        if (this.rconThread != null)
        {
            this.rconThread.func_219591_b();
        }

        if (this.rconQueryThread != null)
        {
            this.rconQueryThread.func_219591_b();
        }
    }

    public void updateTimeLightAndEntities(BooleanSupplier hasTimeLeft)
    {
        super.updateTimeLightAndEntities(hasTimeLeft);
        this.executePendingCommands();
    }

    public boolean getAllowNether()
    {
        return this.getServerProperties().allowNether;
    }

    public void fillSnooper(Snooper snooper)
    {
        snooper.addClientStat("whitelist_enabled", this.getPlayerList().isWhiteListEnabled());
        snooper.addClientStat("whitelist_count", this.getPlayerList().getWhitelistedPlayerNames().length);
        super.fillSnooper(snooper);
    }

    public void handleConsoleInput(String p_195581_1_, CommandSource p_195581_2_)
    {
        this.pendingCommandList.add(new PendingCommand(p_195581_1_, p_195581_2_));
    }

    public void executePendingCommands()
    {
        while (!this.pendingCommandList.isEmpty())
        {
            PendingCommand pendingcommand = this.pendingCommandList.remove(0);
            this.getCommandManager().handleCommand(pendingcommand.sender, pendingcommand.command);
        }
    }

    public boolean isDedicatedServer()
    {
        return true;
    }

    public int func_241871_k()
    {
        return this.getServerProperties().rateLimit;
    }

    /**
     * Get if native transport should be used. Native transport means linux server performance improvements and
     * optimized packet sending/receiving on linux
     */
    public boolean shouldUseNativeTransport()
    {
        return this.getServerProperties().useNativeTransport;
    }

    public DedicatedPlayerList getPlayerList()
    {
        return (DedicatedPlayerList)super.getPlayerList();
    }

    /**
     * Returns true if this integrated server is open to LAN
     */
    public boolean getPublic()
    {
        return true;
    }

    /**
     * Returns the server's hostname.
     */
    public String getHostname()
    {
        return this.getServerHostname();
    }

    /**
     * Never used, but "getServerPort" is already taken.
     */
    public int getPort()
    {
        return this.getServerPort();
    }

    /**
     * Returns the server message of the day
     */
    public String getMotd()
    {
        return this.getMOTD();
    }

    public void setGuiEnabled()
    {
        if (this.serverGui == null)
        {
            this.serverGui = MinecraftServerGui.func_219048_a(this);
        }
    }

    public boolean getGuiEnabled()
    {
        return this.serverGui != null;
    }

    public boolean shareToLAN(GameType gameMode, boolean cheats, int port)
    {
        return false;
    }

    /**
     * Return whether command blocks are enabled.
     */
    public boolean isCommandBlockEnabled()
    {
        return this.getServerProperties().enableCommandBlock;
    }

    /**
     * Return the spawn protection area's size.
     */
    public int getSpawnProtectionSize()
    {
        return this.getServerProperties().spawnProtection;
    }

    public boolean isBlockProtected(ServerWorld worldIn, BlockPos pos, PlayerEntity playerIn)
    {
        if (worldIn.getDimensionKey() != World.OVERWORLD)
        {
            return false;
        }
        else if (this.getPlayerList().getOppedPlayers().isEmpty())
        {
            return false;
        }
        else if (this.getPlayerList().canSendCommands(playerIn.getGameProfile()))
        {
            return false;
        }
        else if (this.getSpawnProtectionSize() <= 0)
        {
            return false;
        }
        else
        {
            BlockPos blockpos = worldIn.getSpawnPoint();
            int i = MathHelper.abs(pos.getX() - blockpos.getX());
            int j = MathHelper.abs(pos.getZ() - blockpos.getZ());
            int k = Math.max(i, j);
            return k <= this.getSpawnProtectionSize();
        }
    }

    public boolean func_230541_aj_()
    {
        return this.getServerProperties().field_241080_Q_;
    }

    public int getOpPermissionLevel()
    {
        return this.getServerProperties().opPermissionLevel;
    }

    public int getFunctionLevel()
    {
        return this.getServerProperties().functionPermissionLevel;
    }

    public void setPlayerIdleTimeout(int idleTimeout)
    {
        super.setPlayerIdleTimeout(idleTimeout);
        this.settings.func_219033_a((p_213224_2_) ->
        {
            return p_213224_2_.playerIdleTimeout.func_244381_a(this.func_244267_aX(), idleTimeout);
        });
    }

    public boolean allowLoggingRcon()
    {
        return this.getServerProperties().broadcastRconToOps;
    }

    public boolean allowLogging()
    {
        return this.getServerProperties().broadcastConsoleToOps;
    }

    public int getMaxWorldSize()
    {
        return this.getServerProperties().maxWorldSize;
    }

    /**
     * The compression treshold. If the packet is larger than the specified amount of bytes, it will be compressed
     */
    public int getNetworkCompressionThreshold()
    {
        return this.getServerProperties().networkCompressionThreshold;
    }

    protected boolean convertFiles()
    {
        boolean flag = false;

        for (int i = 0; !flag && i <= 2; ++i)
        {
            if (i > 0)
            {
                LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            flag = PreYggdrasilConverter.convertUserBanlist(this);
        }

        boolean flag1 = false;

        for (int j = 0; !flag1 && j <= 2; ++j)
        {
            if (j > 0)
            {
                LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            flag1 = PreYggdrasilConverter.convertIpBanlist(this);
        }

        boolean flag2 = false;

        for (int k = 0; !flag2 && k <= 2; ++k)
        {
            if (k > 0)
            {
                LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            flag2 = PreYggdrasilConverter.convertOplist(this);
        }

        boolean flag3 = false;

        for (int l = 0; !flag3 && l <= 2; ++l)
        {
            if (l > 0)
            {
                LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            flag3 = PreYggdrasilConverter.convertWhitelist(this);
        }

        boolean flag4 = false;

        for (int i1 = 0; !flag4 && i1 <= 2; ++i1)
        {
            if (i1 > 0)
            {
                LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
                this.sleepFiveSeconds();
            }

            flag4 = PreYggdrasilConverter.convertSaveFiles(this);
        }

        return flag || flag1 || flag2 || flag3 || flag4;
    }

    private void sleepFiveSeconds()
    {
        try
        {
            Thread.sleep(5000L);
        }
        catch (InterruptedException interruptedexception)
        {
        }
    }

    public long getMaxTickTime()
    {
        return this.getServerProperties().maxTickTime;
    }

    /**
     * Used by RCon's Query in the form of "MajorServerMod 1.2.3: MyPlugin 1.3; AnotherPlugin 2.1; AndSoForth 1.0".
     */
    public String getPlugins()
    {
        return "";
    }

    /**
     * Handle a command received by an RCon instance
     */
    public String handleRConCommand(String command)
    {
        this.rconConsoleSource.resetLog();
        this.runImmediately(() ->
        {
            this.getCommandManager().handleCommand(this.rconConsoleSource.getCommandSource(), command);
        });
        return this.rconConsoleSource.getLogContents();
    }

    public void func_213223_o(boolean p_213223_1_)
    {
        this.settings.func_219033_a((p_213222_2_) ->
        {
            return p_213222_2_.whitelistEnabled.func_244381_a(this.func_244267_aX(), p_213223_1_);
        });
    }

    /**
     * Saves all necessary data as preparation for stopping the server.
     */
    public void stopServer()
    {
        super.stopServer();
        Util.shutdown();
    }

    public boolean isServerOwner(GameProfile profileIn)
    {
        return false;
    }

    public int func_230512_b_(int p_230512_1_)
    {
        return this.getServerProperties().field_241081_R_ * p_230512_1_ / 100;
    }

    public String func_230542_k__()
    {
        return this.anvilConverterForAnvilFile.getSaveName();
    }

    public boolean func_230540_aS_()
    {
        return this.settings.getProperties().field_241078_O_;
    }

    @Nullable
    public IChatFilter func_244435_a(ServerPlayerEntity p_244435_1_)
    {
        return this.field_244714_r != null ? this.field_244714_r.func_244566_a(p_244435_1_.getGameProfile()) : null;
    }
}
