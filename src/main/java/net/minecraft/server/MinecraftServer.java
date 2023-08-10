package net.minecraft.server;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.SpawnLocationHelper;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.loot.LootTableManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.profiler.IProfileResult;
import net.minecraft.profiler.IProfiler;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.LongTickDetector;
import net.minecraft.profiler.Snooper;
import net.minecraft.profiler.TimeTracker;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.management.OpEntry;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.WhiteList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.test.TestCollection;
import net.minecraft.util.CryptException;
import net.minecraft.util.CryptManager;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.filter.IChatFilter;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.spawner.WanderingTraderSpawner;
import net.minecraft.world.storage.CommandStorage;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraft.world.storage.PlayerData;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSavedDataCallableSave;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer extends RecursiveEventLoop<TickDelayedTask> implements ISnooperInfo, ICommandSource, AutoCloseable
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final File USER_CACHE_FILE = new File("usercache.json");
    public static final WorldSettings DEMO_WORLD_SETTINGS = new WorldSettings("Demo World", GameType.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), DatapackCodec.VANILLA_CODEC);
    protected final SaveFormat.LevelSave anvilConverterForAnvilFile;
    protected final PlayerData playerDataManager;
    private final Snooper snooper = new Snooper("server", this, Util.milliTime());
    private final List<Runnable> tickables = Lists.newArrayList();
    private final TimeTracker timeTracker = new TimeTracker(Util.nanoTimeSupplier, this::getTickCounter);
    private IProfiler profiler = EmptyProfiler.INSTANCE;
    private final NetworkSystem networkSystem;
    private final IChunkStatusListenerFactory chunkStatusListenerFactory;
    private final ServerStatusResponse statusResponse = new ServerStatusResponse();
    private final Random random = new Random();
    private final DataFixer dataFixer;
    private String hostname;
    private int serverPort = -1;
    protected final DynamicRegistries.Impl field_240767_f_;
    private final Map<RegistryKey<World>, ServerWorld> worlds = Maps.newLinkedHashMap();
    private PlayerList playerList;
    private volatile boolean serverRunning = true;
    private boolean serverStopped;
    private int tickCounter;
    protected final Proxy serverProxy;
    private boolean onlineMode;
    private boolean preventProxyConnections;
    private boolean pvpEnabled;
    private boolean allowFlight;
    @Nullable
    private String motd;
    private int buildLimit;
    private int maxPlayerIdleMinutes;
    public final long[] tickTimeArray = new long[100];
    @Nullable
    private KeyPair serverKeyPair;
    @Nullable
    private String serverOwner;
    private boolean isDemo;

    /** The texture pack for the server */
    private String resourcePackUrl = "";
    private String resourcePackHash = "";
    private volatile boolean serverIsRunning;
    private long timeOfLastWarning;
    private boolean startProfiling;
    private boolean isGamemodeForced;
    private final MinecraftSessionService sessionService;
    private final GameProfileRepository profileRepo;
    private final PlayerProfileCache profileCache;
    private long nanoTimeSinceStatusRefresh;
    private final Thread serverThread;
    private long serverTime = Util.milliTime();
    private long runTasksUntil;
    private boolean isRunningScheduledTasks;
    private boolean worldIconSet;
    private final ResourcePackList resourcePacks;
    private final ServerScoreboard scoreboard = new ServerScoreboard(this);
    @Nullable
    private CommandStorage field_229733_al_;
    private final CustomServerBossInfoManager customBossEvents = new CustomServerBossInfoManager();
    private final FunctionManager functionManager;
    private final FrameTimer frameTimer = new FrameTimer();
    private boolean whitelistEnabled;
    private float tickTime;
    private final Executor backgroundExecutor;
    @Nullable
    private String serverId;
    private DataPackRegistries resourceManager;
    private final TemplateManager field_240765_ak_;
    protected final IServerConfiguration field_240768_i_;

    public static <S extends MinecraftServer> S func_240784_a_(Function<Thread, S> p_240784_0_)
    {
        AtomicReference<S> atomicreference = new AtomicReference<>();
        Thread thread = new Thread(() ->
        {
            atomicreference.get().func_240802_v_();
        }, "Server thread");
        thread.setUncaughtExceptionHandler((p_240779_0_, p_240779_1_) ->
        {
            LOGGER.error(p_240779_1_);
        });
        S s = p_240784_0_.apply(thread);
        atomicreference.set(s);
        thread.start();
        return s;
    }

    public MinecraftServer(Thread p_i232576_1_, DynamicRegistries.Impl p_i232576_2_, SaveFormat.LevelSave p_i232576_3_, IServerConfiguration p_i232576_4_, ResourcePackList p_i232576_5_, Proxy p_i232576_6_, DataFixer p_i232576_7_, DataPackRegistries p_i232576_8_, MinecraftSessionService p_i232576_9_, GameProfileRepository p_i232576_10_, PlayerProfileCache p_i232576_11_, IChunkStatusListenerFactory p_i232576_12_)
    {
        super("Server");
        this.field_240767_f_ = p_i232576_2_;
        this.field_240768_i_ = p_i232576_4_;
        this.serverProxy = p_i232576_6_;
        this.resourcePacks = p_i232576_5_;
        this.resourceManager = p_i232576_8_;
        this.sessionService = p_i232576_9_;
        this.profileRepo = p_i232576_10_;
        this.profileCache = p_i232576_11_;
        this.networkSystem = new NetworkSystem(this);
        this.chunkStatusListenerFactory = p_i232576_12_;
        this.anvilConverterForAnvilFile = p_i232576_3_;
        this.playerDataManager = p_i232576_3_.getPlayerDataManager();
        this.dataFixer = p_i232576_7_;
        this.functionManager = new FunctionManager(this, p_i232576_8_.getFunctionReloader());
        this.field_240765_ak_ = new TemplateManager(p_i232576_8_.getResourceManager(), p_i232576_3_, p_i232576_7_);
        this.serverThread = p_i232576_1_;
        this.backgroundExecutor = Util.getServerExecutor();
    }

    private void func_213204_a(DimensionSavedDataManager p_213204_1_)
    {
        ScoreboardSaveData scoreboardsavedata = p_213204_1_.getOrCreate(ScoreboardSaveData::new, "scoreboard");
        scoreboardsavedata.setScoreboard(this.getScoreboard());
        this.getScoreboard().addDirtyRunnable(new WorldSavedDataCallableSave(scoreboardsavedata));
    }

    /**
     * Initialises the server and starts it.
     */
    protected abstract boolean init() throws IOException;

    public static void func_240777_a_(SaveFormat.LevelSave p_240777_0_)
    {
        if (p_240777_0_.isSaveFormatOutdated())
        {
            LOGGER.info("Converting map!");
            p_240777_0_.convertRegions(new IProgressUpdate()
            {
                private long startTime = Util.milliTime();
                public void displaySavingString(ITextComponent component)
                {
                }
                public void resetProgressAndMessage(ITextComponent component)
                {
                }
                public void setLoadingProgress(int progress)
                {
                    if (Util.milliTime() - this.startTime >= 1000L)
                    {
                        this.startTime = Util.milliTime();
                        MinecraftServer.LOGGER.info("Converting... {}%", (int)progress);
                    }
                }
                public void setDoneWorking()
                {
                }
                public void displayLoadingString(ITextComponent component)
                {
                }
            });
        }
    }

    protected void func_240800_l__()
    {
        this.setResourcePackFromWorld();
        this.field_240768_i_.addServerBranding(this.getServerModName(), this.func_230045_q_().isPresent());
        IChunkStatusListener ichunkstatuslistener = this.chunkStatusListenerFactory.create(11);
        this.func_240787_a_(ichunkstatuslistener);
        this.func_230543_p_();
        this.loadInitialChunks(ichunkstatuslistener);
    }

    protected void func_230543_p_()
    {
    }

    protected void func_240787_a_(IChunkStatusListener p_240787_1_)
    {
        IServerWorldInfo iserverworldinfo = this.field_240768_i_.getServerWorldInfo();
        DimensionGeneratorSettings dimensiongeneratorsettings = this.field_240768_i_.getDimensionGeneratorSettings();
        boolean flag = dimensiongeneratorsettings.func_236227_h_();
        long i = dimensiongeneratorsettings.getSeed();
        long j = BiomeManager.getHashedSeed(i);
        List<ISpecialSpawner> list = ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new VillageSiege(), new WanderingTraderSpawner(iserverworldinfo));
        SimpleRegistry<Dimension> simpleregistry = dimensiongeneratorsettings.func_236224_e_();
        Dimension dimension = simpleregistry.getValueForKey(Dimension.OVERWORLD);
        ChunkGenerator chunkgenerator;
        DimensionType dimensiontype;

        if (dimension == null)
        {
            dimensiontype = this.field_240767_f_.func_230520_a_().getOrThrow(DimensionType.OVERWORLD);
            chunkgenerator = DimensionGeneratorSettings.func_242750_a(this.field_240767_f_.getRegistry(Registry.BIOME_KEY), this.field_240767_f_.getRegistry(Registry.NOISE_SETTINGS_KEY), (new Random()).nextLong());
        }
        else
        {
            dimensiontype = dimension.getDimensionType();
            chunkgenerator = dimension.getChunkGenerator();
        }

        ServerWorld serverworld = new ServerWorld(this, this.backgroundExecutor, this.anvilConverterForAnvilFile, iserverworldinfo, World.OVERWORLD, dimensiontype, p_240787_1_, chunkgenerator, flag, j, list, true);
        this.worlds.put(World.OVERWORLD, serverworld);
        DimensionSavedDataManager dimensionsaveddatamanager = serverworld.getSavedData();
        this.func_213204_a(dimensionsaveddatamanager);
        this.field_229733_al_ = new CommandStorage(dimensionsaveddatamanager);
        WorldBorder worldborder = serverworld.getWorldBorder();
        worldborder.deserialize(iserverworldinfo.getWorldBorderSerializer());

        if (!iserverworldinfo.isInitialized())
        {
            try
            {
                func_240786_a_(serverworld, iserverworldinfo, dimensiongeneratorsettings.hasBonusChest(), flag, true);
                iserverworldinfo.setInitialized(true);

                if (flag)
                {
                    this.func_240778_a_(this.field_240768_i_);
                }
            }
            catch (Throwable throwable1)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Exception initializing level");

                try
                {
                    serverworld.fillCrashReport(crashreport);
                }
                catch (Throwable throwable)
                {
                }

                throw new ReportedException(crashreport);
            }

            iserverworldinfo.setInitialized(true);
        }

        this.getPlayerList().func_212504_a(serverworld);

        if (this.field_240768_i_.getCustomBossEventData() != null)
        {
            this.getCustomBossEvents().read(this.field_240768_i_.getCustomBossEventData());
        }

        for (Entry<RegistryKey<Dimension>, Dimension> entry : simpleregistry.getEntries())
        {
            RegistryKey<Dimension> registrykey = entry.getKey();

            if (registrykey != Dimension.OVERWORLD)
            {
                RegistryKey<World> registrykey1 = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, registrykey.getLocation());
                DimensionType dimensiontype1 = entry.getValue().getDimensionType();
                ChunkGenerator chunkgenerator1 = entry.getValue().getChunkGenerator();
                DerivedWorldInfo derivedworldinfo = new DerivedWorldInfo(this.field_240768_i_, iserverworldinfo);
                ServerWorld serverworld1 = new ServerWorld(this, this.backgroundExecutor, this.anvilConverterForAnvilFile, derivedworldinfo, registrykey1, dimensiontype1, p_240787_1_, chunkgenerator1, flag, j, ImmutableList.of(), false);
                worldborder.addListener(new IBorderListener.Impl(serverworld1.getWorldBorder()));
                this.worlds.put(registrykey1, serverworld1);
            }
        }
    }

    private static void func_240786_a_(ServerWorld p_240786_0_, IServerWorldInfo p_240786_1_, boolean hasBonusChest, boolean p_240786_3_, boolean p_240786_4_)
    {
        ChunkGenerator chunkgenerator = p_240786_0_.getChunkProvider().getChunkGenerator();

        if (!p_240786_4_)
        {
            p_240786_1_.setSpawn(BlockPos.ZERO.up(chunkgenerator.getGroundHeight()), 0.0F);
        }
        else if (p_240786_3_)
        {
            p_240786_1_.setSpawn(BlockPos.ZERO.up(), 0.0F);
        }
        else
        {
            BiomeProvider biomeprovider = chunkgenerator.getBiomeProvider();
            Random random = new Random(p_240786_0_.getSeed());
            BlockPos blockpos = biomeprovider.findBiomePosition(0, p_240786_0_.getSeaLevel(), 0, 256, (p_244265_0_) ->
            {
                return p_244265_0_.getMobSpawnInfo().isValidSpawnBiomeForPlayer();
            }, random);
            ChunkPos chunkpos = blockpos == null ? new ChunkPos(0, 0) : new ChunkPos(blockpos);

            if (blockpos == null)
            {
                LOGGER.warn("Unable to find spawn biome");
            }

            boolean flag = false;

            for (Block block : BlockTags.VALID_SPAWN.getAllElements())
            {
                if (biomeprovider.getSurfaceBlocks().contains(block.getDefaultState()))
                {
                    flag = true;
                    break;
                }
            }

            p_240786_1_.setSpawn(chunkpos.asBlockPos().add(8, chunkgenerator.getGroundHeight(), 8), 0.0F);
            int i1 = 0;
            int j1 = 0;
            int i = 0;
            int j = -1;
            int k = 32;

            for (int l = 0; l < 1024; ++l)
            {
                if (i1 > -16 && i1 <= 16 && j1 > -16 && j1 <= 16)
                {
                    BlockPos blockpos1 = SpawnLocationHelper.func_241094_a_(p_240786_0_, new ChunkPos(chunkpos.x + i1, chunkpos.z + j1), flag);

                    if (blockpos1 != null)
                    {
                        p_240786_1_.setSpawn(blockpos1, 0.0F);
                        break;
                    }
                }

                if (i1 == j1 || i1 < 0 && i1 == -j1 || i1 > 0 && i1 == 1 - j1)
                {
                    int k1 = i;
                    i = -j;
                    j = k1;
                }

                i1 += i;
                j1 += j;
            }

            if (hasBonusChest)
            {
                ConfiguredFeature <? , ? > configuredfeature = Features.BONUS_CHEST;
                configuredfeature.func_242765_a(p_240786_0_, chunkgenerator, p_240786_0_.rand, new BlockPos(p_240786_1_.getSpawnX(), p_240786_1_.getSpawnY(), p_240786_1_.getSpawnZ()));
            }
        }
    }

    private void func_240778_a_(IServerConfiguration p_240778_1_)
    {
        p_240778_1_.setDifficulty(Difficulty.PEACEFUL);
        p_240778_1_.setDifficultyLocked(true);
        IServerWorldInfo iserverworldinfo = p_240778_1_.getServerWorldInfo();
        iserverworldinfo.setRaining(false);
        iserverworldinfo.setThundering(false);
        iserverworldinfo.setClearWeatherTime(1000000000);
        iserverworldinfo.setDayTime(6000L);
        iserverworldinfo.setGameType(GameType.SPECTATOR);
    }

    /**
     * Loads the spawn chunks and any forced chunks
     */
    private void loadInitialChunks(IChunkStatusListener p_213186_1_)
    {
        ServerWorld serverworld = this.func_241755_D_();
        LOGGER.info("Preparing start region for dimension {}", (Object)serverworld.getDimensionKey().getLocation());
        BlockPos blockpos = serverworld.getSpawnPoint();
        p_213186_1_.start(new ChunkPos(blockpos));
        ServerChunkProvider serverchunkprovider = serverworld.getChunkProvider();
        serverchunkprovider.getLightManager().func_215598_a(500);
        this.serverTime = Util.milliTime();
        serverchunkprovider.registerTicket(TicketType.START, new ChunkPos(blockpos), 11, Unit.INSTANCE);

        while (serverchunkprovider.getLoadedChunksCount() != 441)
        {
            this.serverTime = Util.milliTime() + 10L;
            this.runScheduledTasks();
        }

        this.serverTime = Util.milliTime() + 10L;
        this.runScheduledTasks();

        for (ServerWorld serverworld1 : this.worlds.values())
        {
            ForcedChunksSaveData forcedchunkssavedata = serverworld1.getSavedData().get(ForcedChunksSaveData::new, "chunks");

            if (forcedchunkssavedata != null)
            {
                LongIterator longiterator = forcedchunkssavedata.getChunks().iterator();

                while (longiterator.hasNext())
                {
                    long i = longiterator.nextLong();
                    ChunkPos chunkpos = new ChunkPos(i);
                    serverworld1.getChunkProvider().forceChunk(chunkpos, true);
                }
            }
        }

        this.serverTime = Util.milliTime() + 10L;
        this.runScheduledTasks();
        p_213186_1_.stop();
        serverchunkprovider.getLightManager().func_215598_a(5);
        this.func_240794_aZ_();
    }

    protected void setResourcePackFromWorld()
    {
        File file1 = this.anvilConverterForAnvilFile.resolveFilePath(FolderName.RESOURCES_ZIP).toFile();

        if (file1.isFile())
        {
            String s = this.anvilConverterForAnvilFile.getSaveName();

            try
            {
                this.setResourcePack("level://" + URLEncoder.encode(s, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
            }
            catch (UnsupportedEncodingException unsupportedencodingexception)
            {
                LOGGER.warn("Something went wrong url encoding {}", (Object)s);
            }
        }
    }

    public GameType getGameType()
    {
        return this.field_240768_i_.getGameType();
    }

    /**
     * Defaults to false.
     */
    public boolean isHardcore()
    {
        return this.field_240768_i_.isHardcore();
    }

    public abstract int getOpPermissionLevel();

    public abstract int getFunctionLevel();

    public abstract boolean allowLoggingRcon();

    public boolean save(boolean suppressLog, boolean flush, boolean forced)
    {
        boolean flag = false;

        for (ServerWorld serverworld : this.getWorlds())
        {
            if (!suppressLog)
            {
                LOGGER.info("Saving chunks for level '{}'/{}", serverworld, serverworld.getDimensionKey().getLocation());
            }

            serverworld.save((IProgressUpdate)null, flush, serverworld.disableLevelSaving && !forced);
            flag = true;
        }

        ServerWorld serverworld1 = this.func_241755_D_();
        IServerWorldInfo iserverworldinfo = this.field_240768_i_.getServerWorldInfo();
        iserverworldinfo.setWorldBorderSerializer(serverworld1.getWorldBorder().getSerializer());
        this.field_240768_i_.setCustomBossEventData(this.getCustomBossEvents().write());
        this.anvilConverterForAnvilFile.saveLevel(this.field_240767_f_, this.field_240768_i_, this.getPlayerList().getHostPlayerData());
        return flag;
    }

    public void close()
    {
        this.stopServer();
    }

    /**
     * Saves all necessary data as preparation for stopping the server.
     */
    protected void stopServer()
    {
        LOGGER.info("Stopping server");

        if (this.getNetworkSystem() != null)
        {
            this.getNetworkSystem().terminateEndpoints();
        }

        if (this.playerList != null)
        {
            LOGGER.info("Saving players");
            this.playerList.saveAllPlayerData();
            this.playerList.removeAllPlayers();
        }

        LOGGER.info("Saving worlds");

        for (ServerWorld serverworld : this.getWorlds())
        {
            if (serverworld != null)
            {
                serverworld.disableLevelSaving = false;
            }
        }

        this.save(false, true, false);

        for (ServerWorld serverworld1 : this.getWorlds())
        {
            if (serverworld1 != null)
            {
                try
                {
                    serverworld1.close();
                }
                catch (IOException ioexception1)
                {
                    LOGGER.error("Exception closing the level", (Throwable)ioexception1);
                }
            }
        }

        if (this.snooper.isSnooperRunning())
        {
            this.snooper.stop();
        }

        this.resourceManager.close();

        try
        {
            this.anvilConverterForAnvilFile.close();
        }
        catch (IOException ioexception)
        {
            LOGGER.error("Failed to unlock level {}", this.anvilConverterForAnvilFile.getSaveName(), ioexception);
        }
    }

    /**
     * "getHostname" is already taken, but both return the hostname.
     */
    public String getServerHostname()
    {
        return this.hostname;
    }

    public void setHostname(String host)
    {
        this.hostname = host;
    }

    public boolean isServerRunning()
    {
        return this.serverRunning;
    }

    /**
     * Sets the serverRunning variable to false, in order to get the server to shut down.
     */
    public void initiateShutdown(boolean waitForServer)
    {
        this.serverRunning = false;

        if (waitForServer)
        {
            try
            {
                this.serverThread.join();
            }
            catch (InterruptedException interruptedexception)
            {
                LOGGER.error("Error while shutting down", (Throwable)interruptedexception);
            }
        }
    }

    protected void func_240802_v_()
    {
        try
        {
            if (this.init())
            {
                this.serverTime = Util.milliTime();
                this.statusResponse.setServerDescription(new StringTextComponent(this.motd));
                this.statusResponse.setVersion(new ServerStatusResponse.Version(SharedConstants.getVersion().getName(), SharedConstants.getVersion().getProtocolVersion()));
                this.applyServerIconToResponse(this.statusResponse);

                while (this.serverRunning)
                {
                    long i = Util.milliTime() - this.serverTime;

                    if (i > 2000L && this.serverTime - this.timeOfLastWarning >= 15000L)
                    {
                        long j = i / 50L;
                        LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", i, j);
                        this.serverTime += j * 50L;
                        this.timeOfLastWarning = this.serverTime;
                    }

                    this.serverTime += 50L;
                    LongTickDetector longtickdetector = LongTickDetector.func_233524_a_("Server");
                    this.func_240773_a_(longtickdetector);
                    this.profiler.startTick();
                    this.profiler.startSection("tick");
                    this.tick(this::isAheadOfTime);
                    this.profiler.endStartSection("nextTickWait");
                    this.isRunningScheduledTasks = true;
                    this.runTasksUntil = Math.max(Util.milliTime() + 50L, this.serverTime);
                    this.runScheduledTasks();
                    this.profiler.endSection();
                    this.profiler.endTick();
                    this.func_240795_b_(longtickdetector);
                    this.serverIsRunning = true;
                }
            }
            else
            {
                this.finalTick((CrashReport)null);
            }
        }
        catch (Throwable throwable1)
        {
            LOGGER.error("Encountered an unexpected exception", throwable1);
            CrashReport crashreport;

            if (throwable1 instanceof ReportedException)
            {
                crashreport = this.addServerInfoToCrashReport(((ReportedException)throwable1).getCrashReport());
            }
            else
            {
                crashreport = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", throwable1));
            }

            File file1 = new File(new File(this.getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

            if (crashreport.saveToFile(file1))
            {
                LOGGER.error("This crash report has been saved to: {}", (Object)file1.getAbsolutePath());
            }
            else
            {
                LOGGER.error("We were unable to save this crash report to disk.");
            }

            this.finalTick(crashreport);
        }
        finally
        {
            try
            {
                this.serverStopped = true;
                this.stopServer();
            }
            catch (Throwable throwable)
            {
                LOGGER.error("Exception stopping the server", throwable);
            }
            finally
            {
                this.systemExitNow();
            }
        }
    }

    private boolean isAheadOfTime()
    {
        return this.isTaskRunning() || Util.milliTime() < (this.isRunningScheduledTasks ? this.runTasksUntil : this.serverTime);
    }

    /**
     * Runs all pending tasks and waits for more tasks until serverTime is reached.
     */
    protected void runScheduledTasks()
    {
        this.drainTasks();
        this.driveUntil(() ->
        {
            return !this.isAheadOfTime();
        });
    }

    protected TickDelayedTask wrapTask(Runnable runnable)
    {
        return new TickDelayedTask(this.tickCounter, runnable);
    }

    protected boolean canRun(TickDelayedTask runnable)
    {
        return runnable.getScheduledTime() + 3 < this.tickCounter || this.isAheadOfTime();
    }

    public boolean driveOne()
    {
        boolean flag = this.driveOneInternal();
        this.isRunningScheduledTasks = flag;
        return flag;
    }

    private boolean driveOneInternal()
    {
        if (super.driveOne())
        {
            return true;
        }
        else
        {
            if (this.isAheadOfTime())
            {
                for (ServerWorld serverworld : this.getWorlds())
                {
                    if (serverworld.getChunkProvider().driveOneTask())
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    protected void run(TickDelayedTask taskIn)
    {
        this.getProfiler().func_230035_c_("runTask");
        super.run(taskIn);
    }

    private void applyServerIconToResponse(ServerStatusResponse response)
    {
        File file1 = this.getFile("server-icon.png");

        if (!file1.exists())
        {
            file1 = this.anvilConverterForAnvilFile.getIconFile();
        }

        if (file1.isFile())
        {
            ByteBuf bytebuf = Unpooled.buffer();

            try
            {
                BufferedImage bufferedimage = ImageIO.read(file1);
                Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
                Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
                ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
                ByteBuffer bytebuffer = Base64.getEncoder().encode(bytebuf.nioBuffer());
                response.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(bytebuffer));
            }
            catch (Exception exception)
            {
                LOGGER.error("Couldn't load server icon", (Throwable)exception);
            }
            finally
            {
                bytebuf.release();
            }
        }
    }

    public boolean isWorldIconSet()
    {
        this.worldIconSet = this.worldIconSet || this.getWorldIconFile().isFile();
        return this.worldIconSet;
    }

    public File getWorldIconFile()
    {
        return this.anvilConverterForAnvilFile.getIconFile();
    }

    public File getDataDirectory()
    {
        return new File(".");
    }

    /**
     * Called on exit from the main run() loop.
     */
    protected void finalTick(CrashReport report)
    {
    }

    /**
     * Directly calls System.exit(0), instantly killing the program.
     */
    protected void systemExitNow()
    {
    }

    /**
     * Main function called by run() every loop.
     */
    protected void tick(BooleanSupplier hasTimeLeft)
    {
        long i = Util.nanoTime();
        ++this.tickCounter;
        this.updateTimeLightAndEntities(hasTimeLeft);

        if (i - this.nanoTimeSinceStatusRefresh >= 5000000000L)
        {
            this.nanoTimeSinceStatusRefresh = i;
            this.statusResponse.setPlayers(new ServerStatusResponse.Players(this.getMaxPlayers(), this.getCurrentPlayerCount()));
            GameProfile[] agameprofile = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
            int j = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - agameprofile.length);

            for (int k = 0; k < agameprofile.length; ++k)
            {
                agameprofile[k] = this.playerList.getPlayers().get(j + k).getGameProfile();
            }

            Collections.shuffle(Arrays.asList(agameprofile));
            this.statusResponse.getPlayers().setPlayers(agameprofile);
        }

        if (this.tickCounter % 6000 == 0)
        {
            LOGGER.debug("Autosave started");
            this.profiler.startSection("save");
            this.playerList.saveAllPlayerData();
            this.save(true, false, false);
            this.profiler.endSection();
            LOGGER.debug("Autosave finished");
        }

        this.profiler.startSection("snooper");

        if (!this.snooper.isSnooperRunning() && this.tickCounter > 100)
        {
            this.snooper.start();
        }

        if (this.tickCounter % 6000 == 0)
        {
            this.snooper.addMemoryStatsToSnooper();
        }

        this.profiler.endSection();
        this.profiler.startSection("tallying");
        long l = this.tickTimeArray[this.tickCounter % 100] = Util.nanoTime() - i;
        this.tickTime = this.tickTime * 0.8F + (float)l / 1000000.0F * 0.19999999F;
        long i1 = Util.nanoTime();
        this.frameTimer.addFrame(i1 - i);
        this.profiler.endSection();
    }

    protected void updateTimeLightAndEntities(BooleanSupplier hasTimeLeft)
    {
        this.profiler.startSection("commandFunctions");
        this.getFunctionManager().tick();
        this.profiler.endStartSection("levels");

        for (ServerWorld serverworld : this.getWorlds())
        {
            this.profiler.startSection(() ->
            {
                return serverworld + " " + serverworld.getDimensionKey().getLocation();
            });

            if (this.tickCounter % 20 == 0)
            {
                this.profiler.startSection("timeSync");
                this.playerList.func_232642_a_(new SUpdateTimePacket(serverworld.getGameTime(), serverworld.getDayTime(), serverworld.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), serverworld.getDimensionKey());
                this.profiler.endSection();
            }

            this.profiler.startSection("tick");

            try
            {
                serverworld.tick(hasTimeLeft);
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception ticking world");
                serverworld.fillCrashReport(crashreport);
                throw new ReportedException(crashreport);
            }

            this.profiler.endSection();
            this.profiler.endSection();
        }

        this.profiler.endStartSection("connection");
        this.getNetworkSystem().tick();
        this.profiler.endStartSection("players");
        this.playerList.tick();

        if (SharedConstants.developmentMode)
        {
            TestCollection.field_229570_a_.func_229574_b_();
        }

        this.profiler.endStartSection("server gui refresh");

        for (int i = 0; i < this.tickables.size(); ++i)
        {
            this.tickables.get(i).run();
        }

        this.profiler.endSection();
    }

    public boolean getAllowNether()
    {
        return true;
    }

    public void registerTickable(Runnable tickable)
    {
        this.tickables.add(tickable);
    }

    protected void setServerId(String serverIdIn)
    {
        this.serverId = serverIdIn;
    }

    public boolean isThreadAlive()
    {
        return !this.serverThread.isAlive();
    }

    /**
     * Returns a File object from the specified string.
     */
    public File getFile(String fileName)
    {
        return new File(this.getDataDirectory(), fileName);
    }

    public final ServerWorld func_241755_D_()
    {
        return this.worlds.get(World.OVERWORLD);
    }

    @Nullable

    /**
     * Gets the worldServer by the given dimension.
     */
    public ServerWorld getWorld(RegistryKey<World> dimension)
    {
        return this.worlds.get(dimension);
    }

    public Set<RegistryKey<World>> func_240770_D_()
    {
        return this.worlds.keySet();
    }

    public Iterable<ServerWorld> getWorlds()
    {
        return this.worlds.values();
    }

    /**
     * Returns the server's Minecraft version as string.
     */
    public String getMinecraftVersion()
    {
        return SharedConstants.getVersion().getName();
    }

    /**
     * Returns the number of players currently on the server.
     */
    public int getCurrentPlayerCount()
    {
        return this.playerList.getCurrentPlayerCount();
    }

    /**
     * Returns the maximum number of players allowed on the server.
     */
    public int getMaxPlayers()
    {
        return this.playerList.getMaxPlayers();
    }

    /**
     * Returns an array of the usernames of all the connected players.
     */
    public String[] getOnlinePlayerNames()
    {
        return this.playerList.getOnlinePlayerNames();
    }

    public String getServerModName()
    {
        return "vanilla";
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport addServerInfoToCrashReport(CrashReport report)
    {
        if (this.playerList != null)
        {
            report.getCategory().addDetail("Player Count", () ->
            {
                return this.playerList.getCurrentPlayerCount() + " / " + this.playerList.getMaxPlayers() + "; " + this.playerList.getPlayers();
            });
        }

        report.getCategory().addDetail("Data Packs", () ->
        {
            StringBuilder stringbuilder = new StringBuilder();

            for (ResourcePackInfo resourcepackinfo : this.resourcePacks.getEnabledPacks())
            {
                if (stringbuilder.length() > 0)
                {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(resourcepackinfo.getName());

                if (!resourcepackinfo.getCompatibility().isCompatible())
                {
                    stringbuilder.append(" (incompatible)");
                }
            }

            return stringbuilder.toString();
        });

        if (this.serverId != null)
        {
            report.getCategory().addDetail("Server Id", () ->
            {
                return this.serverId;
            });
        }

        return report;
    }

    public abstract Optional<String> func_230045_q_();

    /**
     * Send a chat message to the CommandSender
     */
    public void sendMessage(ITextComponent component, UUID senderUUID)
    {
        LOGGER.info(component.getString());
    }

    /**
     * Gets KeyPair instanced in MinecraftServer.
     */
    public KeyPair getKeyPair()
    {
        return this.serverKeyPair;
    }

    /**
     * Gets serverPort.
     */
    public int getServerPort()
    {
        return this.serverPort;
    }

    public void setServerPort(int port)
    {
        this.serverPort = port;
    }

    /**
     * Returns the username of the server owner (for integrated servers)
     */
    public String getServerOwner()
    {
        return this.serverOwner;
    }

    /**
     * Sets the username of the owner of this server (in the case of an integrated server)
     */
    public void setServerOwner(String owner)
    {
        this.serverOwner = owner;
    }

    public boolean isSinglePlayer()
    {
        return this.serverOwner != null;
    }

    protected void func_244801_P()
    {
        LOGGER.info("Generating keypair");

        try
        {
            this.serverKeyPair = CryptManager.generateKeyPair();
        }
        catch (CryptException cryptexception)
        {
            throw new IllegalStateException("Failed to generate key pair", cryptexception);
        }
    }

    public void setDifficultyForAllWorlds(Difficulty difficulty, boolean p_147139_2_)
    {
        if (p_147139_2_ || !this.field_240768_i_.isDifficultyLocked())
        {
            this.field_240768_i_.setDifficulty(this.field_240768_i_.isHardcore() ? Difficulty.HARD : difficulty);
            this.func_240794_aZ_();
            this.getPlayerList().getPlayers().forEach(this::sendDifficultyToPlayer);
        }
    }

    public int func_230512_b_(int p_230512_1_)
    {
        return p_230512_1_;
    }

    private void func_240794_aZ_()
    {
        for (ServerWorld serverworld : this.getWorlds())
        {
            serverworld.setAllowedSpawnTypes(this.func_230536_N_(), this.func_230537_U_());
        }
    }

    public void setDifficultyLocked(boolean locked)
    {
        this.field_240768_i_.setDifficultyLocked(locked);
        this.getPlayerList().getPlayers().forEach(this::sendDifficultyToPlayer);
    }

    private void sendDifficultyToPlayer(ServerPlayerEntity playerIn)
    {
        IWorldInfo iworldinfo = playerIn.getServerWorld().getWorldInfo();
        playerIn.connection.sendPacket(new SServerDifficultyPacket(iworldinfo.getDifficulty(), iworldinfo.isDifficultyLocked()));
    }

    protected boolean func_230536_N_()
    {
        return this.field_240768_i_.getDifficulty() != Difficulty.PEACEFUL;
    }

    /**
     * Gets whether this is a demo or not.
     */
    public boolean isDemo()
    {
        return this.isDemo;
    }

    /**
     * Sets whether this is a demo or not.
     */
    public void setDemo(boolean demo)
    {
        this.isDemo = demo;
    }

    public String getResourcePackUrl()
    {
        return this.resourcePackUrl;
    }

    public String getResourcePackHash()
    {
        return this.resourcePackHash;
    }

    public void setResourcePack(String url, String hash)
    {
        this.resourcePackUrl = url;
        this.resourcePackHash = hash;
    }

    public void fillSnooper(Snooper snooper)
    {
        snooper.addClientStat("whitelist_enabled", false);
        snooper.addClientStat("whitelist_count", 0);

        if (this.playerList != null)
        {
            snooper.addClientStat("players_current", this.getCurrentPlayerCount());
            snooper.addClientStat("players_max", this.getMaxPlayers());
            snooper.addClientStat("players_seen", this.playerDataManager.getSeenPlayerUUIDs().length);
        }

        snooper.addClientStat("uses_auth", this.onlineMode);
        snooper.addClientStat("gui_state", this.getGuiEnabled() ? "enabled" : "disabled");
        snooper.addClientStat("run_time", (Util.milliTime() - snooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
        snooper.addClientStat("avg_tick_ms", (int)(MathHelper.average(this.tickTimeArray) * 1.0E-6D));
        int i = 0;

        for (ServerWorld serverworld : this.getWorlds())
        {
            if (serverworld != null)
            {
                snooper.addClientStat("world[" + i + "][dimension]", serverworld.getDimensionKey().getLocation());
                snooper.addClientStat("world[" + i + "][mode]", this.field_240768_i_.getGameType());
                snooper.addClientStat("world[" + i + "][difficulty]", serverworld.getDifficulty());
                snooper.addClientStat("world[" + i + "][hardcore]", this.field_240768_i_.isHardcore());
                snooper.addClientStat("world[" + i + "][height]", this.buildLimit);
                snooper.addClientStat("world[" + i + "][chunks_loaded]", serverworld.getChunkProvider().getLoadedChunkCount());
                ++i;
            }
        }

        snooper.addClientStat("worlds", i);
    }

    public abstract boolean isDedicatedServer();

    public abstract int func_241871_k();

    public boolean isServerInOnlineMode()
    {
        return this.onlineMode;
    }

    public void setOnlineMode(boolean online)
    {
        this.onlineMode = online;
    }

    public boolean getPreventProxyConnections()
    {
        return this.preventProxyConnections;
    }

    public void setPreventProxyConnections(boolean p_190517_1_)
    {
        this.preventProxyConnections = p_190517_1_;
    }

    public boolean func_230537_U_()
    {
        return true;
    }

    public boolean func_230538_V_()
    {
        return true;
    }

    /**
     * Get if native transport should be used. Native transport means linux server performance improvements and
     * optimized packet sending/receiving on linux
     */
    public abstract boolean shouldUseNativeTransport();

    public boolean isPVPEnabled()
    {
        return this.pvpEnabled;
    }

    public void setAllowPvp(boolean allowPvp)
    {
        this.pvpEnabled = allowPvp;
    }

    public boolean isFlightAllowed()
    {
        return this.allowFlight;
    }

    public void setAllowFlight(boolean allow)
    {
        this.allowFlight = allow;
    }

    /**
     * Return whether command blocks are enabled.
     */
    public abstract boolean isCommandBlockEnabled();

    public String getMOTD()
    {
        return this.motd;
    }

    public void setMOTD(String motdIn)
    {
        this.motd = motdIn;
    }

    public int getBuildLimit()
    {
        return this.buildLimit;
    }

    public void setBuildLimit(int maxBuildHeight)
    {
        this.buildLimit = maxBuildHeight;
    }

    public boolean isServerStopped()
    {
        return this.serverStopped;
    }

    public PlayerList getPlayerList()
    {
        return this.playerList;
    }

    public void setPlayerList(PlayerList list)
    {
        this.playerList = list;
    }

    /**
     * Returns true if this integrated server is open to LAN
     */
    public abstract boolean getPublic();

    /**
     * Sets the game type for all worlds.
     */
    public void setGameType(GameType gameMode)
    {
        this.field_240768_i_.setGameType(gameMode);
    }

    @Nullable
    public NetworkSystem getNetworkSystem()
    {
        return this.networkSystem;
    }

    public boolean serverIsInRunLoop()
    {
        return this.serverIsRunning;
    }

    public boolean getGuiEnabled()
    {
        return false;
    }

    public abstract boolean shareToLAN(GameType gameMode, boolean cheats, int port);

    public int getTickCounter()
    {
        return this.tickCounter;
    }

    public Snooper getSnooper()
    {
        return this.snooper;
    }

    /**
     * Return the spawn protection area's size.
     */
    public int getSpawnProtectionSize()
    {
        return 16;
    }

    public boolean isBlockProtected(ServerWorld worldIn, BlockPos pos, PlayerEntity playerIn)
    {
        return false;
    }

    /**
     * Set the forceGamemode field (whether joining players will be put in their old gamemode or the default one)
     */
    public void setForceGamemode(boolean force)
    {
        this.isGamemodeForced = force;
    }

    /**
     * Get the forceGamemode field (whether joining players will be put in their old gamemode or the default one)
     */
    public boolean getForceGamemode()
    {
        return this.isGamemodeForced;
    }

    public boolean func_230541_aj_()
    {
        return true;
    }

    public int getMaxPlayerIdleMinutes()
    {
        return this.maxPlayerIdleMinutes;
    }

    public void setPlayerIdleTimeout(int idleTimeout)
    {
        this.maxPlayerIdleMinutes = idleTimeout;
    }

    public MinecraftSessionService getMinecraftSessionService()
    {
        return this.sessionService;
    }

    public GameProfileRepository getGameProfileRepository()
    {
        return this.profileRepo;
    }

    public PlayerProfileCache getPlayerProfileCache()
    {
        return this.profileCache;
    }

    public ServerStatusResponse getServerStatusResponse()
    {
        return this.statusResponse;
    }

    public void refreshStatusNextTick()
    {
        this.nanoTimeSinceStatusRefresh = 0L;
    }

    public int getMaxWorldSize()
    {
        return 29999984;
    }

    public boolean shouldDeferTasks()
    {
        return super.shouldDeferTasks() && !this.isServerStopped();
    }

    public Thread getExecutionThread()
    {
        return this.serverThread;
    }

    /**
     * The compression treshold. If the packet is larger than the specified amount of bytes, it will be compressed
     */
    public int getNetworkCompressionThreshold()
    {
        return 256;
    }

    public long getServerTime()
    {
        return this.serverTime;
    }

    public DataFixer getDataFixer()
    {
        return this.dataFixer;
    }

    public int getSpawnRadius(@Nullable ServerWorld worldIn)
    {
        return worldIn != null ? worldIn.getGameRules().getInt(GameRules.SPAWN_RADIUS) : 10;
    }

    public AdvancementManager getAdvancementManager()
    {
        return this.resourceManager.getAdvancementManager();
    }

    public FunctionManager getFunctionManager()
    {
        return this.functionManager;
    }

    public CompletableFuture<Void> func_240780_a_(Collection<String> p_240780_1_)
    {
        CompletableFuture<Void> completablefuture = CompletableFuture.supplyAsync(() ->
        {
            return p_240780_1_.stream().map(this.resourcePacks::getPackInfo).filter(Objects::nonNull).map(ResourcePackInfo::getResourcePack).collect(ImmutableList.toImmutableList());
        }, this).thenCompose((p_240775_1_) ->
        {
            return DataPackRegistries.func_240961_a_(p_240775_1_, this.isDedicatedServer() ? Commands.EnvironmentType.DEDICATED : Commands.EnvironmentType.INTEGRATED, this.getFunctionLevel(), this.backgroundExecutor, this);
        }).thenAcceptAsync((p_240782_2_) ->
        {
            this.resourceManager.close();
            this.resourceManager = p_240782_2_;
            this.resourcePacks.setEnabledPacks(p_240780_1_);
            this.field_240768_i_.setDatapackCodec(func_240771_a_(this.resourcePacks));
            p_240782_2_.updateTags();
            this.getPlayerList().saveAllPlayerData();
            this.getPlayerList().reloadResources();
            this.functionManager.setFunctionReloader(this.resourceManager.getFunctionReloader());
            this.field_240765_ak_.onResourceManagerReload(this.resourceManager.getResourceManager());
        }, this);

        if (this.isOnExecutionThread())
        {
            this.driveUntil(completablefuture::isDone);
        }

        return completablefuture;
    }

    public static DatapackCodec func_240772_a_(ResourcePackList p_240772_0_, DatapackCodec p_240772_1_, boolean p_240772_2_)
    {
        p_240772_0_.reloadPacksFromFinders();

        if (p_240772_2_)
        {
            p_240772_0_.setEnabledPacks(Collections.singleton("vanilla"));
            return new DatapackCodec(ImmutableList.of("vanilla"), ImmutableList.of());
        }
        else
        {
            Set<String> set = Sets.newLinkedHashSet();

            for (String s : p_240772_1_.getEnabled())
            {
                if (p_240772_0_.func_232617_b_(s))
                {
                    set.add(s);
                }
                else
                {
                    LOGGER.warn("Missing data pack {}", (Object)s);
                }
            }

            for (ResourcePackInfo resourcepackinfo : p_240772_0_.getAllPacks())
            {
                String s1 = resourcepackinfo.getName();

                if (!p_240772_1_.getDisabled().contains(s1) && !set.contains(s1))
                {
                    LOGGER.info("Found new data pack {}, loading it automatically", (Object)s1);
                    set.add(s1);
                }
            }

            if (set.isEmpty())
            {
                LOGGER.info("No datapacks selected, forcing vanilla");
                set.add("vanilla");
            }

            p_240772_0_.setEnabledPacks(set);
            return func_240771_a_(p_240772_0_);
        }
    }

    private static DatapackCodec func_240771_a_(ResourcePackList p_240771_0_)
    {
        Collection<String> collection = p_240771_0_.func_232621_d_();
        List<String> list = ImmutableList.copyOf(collection);
        List<String> list1 = p_240771_0_.func_232616_b_().stream().filter((p_240781_1_) ->
        {
            return !collection.contains(p_240781_1_);
        }).collect(ImmutableList.toImmutableList());
        return new DatapackCodec(list, list1);
    }

    public void kickPlayersNotWhitelisted(CommandSource commandSourceIn)
    {
        if (this.isWhitelistEnabled())
        {
            PlayerList playerlist = commandSourceIn.getServer().getPlayerList();
            WhiteList whitelist = playerlist.getWhitelistedPlayers();

            for (ServerPlayerEntity serverplayerentity : Lists.newArrayList(playerlist.getPlayers()))
            {
                if (!whitelist.isWhitelisted(serverplayerentity.getGameProfile()))
                {
                    serverplayerentity.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.not_whitelisted"));
                }
            }
        }
    }

    public ResourcePackList getResourcePacks()
    {
        return this.resourcePacks;
    }

    public Commands getCommandManager()
    {
        return this.resourceManager.getCommandManager();
    }

    public CommandSource getCommandSource()
    {
        ServerWorld serverworld = this.func_241755_D_();
        return new CommandSource(this, serverworld == null ? Vector3d.ZERO : Vector3d.copy(serverworld.getSpawnPoint()), Vector2f.ZERO, serverworld, 4, "Server", new StringTextComponent("Server"), this, (Entity)null);
    }

    public boolean shouldReceiveFeedback()
    {
        return true;
    }

    public boolean shouldReceiveErrors()
    {
        return true;
    }

    public RecipeManager getRecipeManager()
    {
        return this.resourceManager.getRecipeManager();
    }

    public ITagCollectionSupplier func_244266_aF()
    {
        return this.resourceManager.func_244358_d();
    }

    public ServerScoreboard getScoreboard()
    {
        return this.scoreboard;
    }

    public CommandStorage func_229735_aN_()
    {
        if (this.field_229733_al_ == null)
        {
            throw new NullPointerException("Called before server init");
        }
        else
        {
            return this.field_229733_al_;
        }
    }

    public LootTableManager getLootTableManager()
    {
        return this.resourceManager.getLootTableManager();
    }

    public LootPredicateManager func_229736_aP_()
    {
        return this.resourceManager.getLootPredicateManager();
    }

    public GameRules getGameRules()
    {
        return this.func_241755_D_().getGameRules();
    }

    public CustomServerBossInfoManager getCustomBossEvents()
    {
        return this.customBossEvents;
    }

    public boolean isWhitelistEnabled()
    {
        return this.whitelistEnabled;
    }

    public void setWhitelistEnabled(boolean whitelistEnabledIn)
    {
        this.whitelistEnabled = whitelistEnabledIn;
    }

    public float getTickTime()
    {
        return this.tickTime;
    }

    public int getPermissionLevel(GameProfile profile)
    {
        if (this.getPlayerList().canSendCommands(profile))
        {
            OpEntry opentry = this.getPlayerList().getOppedPlayers().getEntry(profile);

            if (opentry != null)
            {
                return opentry.getPermissionLevel();
            }
            else if (this.isServerOwner(profile))
            {
                return 4;
            }
            else if (this.isSinglePlayer())
            {
                return this.getPlayerList().commandsAllowedForAll() ? 4 : 0;
            }
            else
            {
                return this.getOpPermissionLevel();
            }
        }
        else
        {
            return 0;
        }
    }

    public FrameTimer getFrameTimer()
    {
        return this.frameTimer;
    }

    public IProfiler getProfiler()
    {
        return this.profiler;
    }

    public abstract boolean isServerOwner(GameProfile profileIn);

    public void dumpDebugInfo(Path p_223711_1_) throws IOException
    {
        Path path = p_223711_1_.resolve("levels");

        for (Entry<RegistryKey<World>, ServerWorld> entry : this.worlds.entrySet())
        {
            ResourceLocation resourcelocation = entry.getKey().getLocation();
            Path path1 = path.resolve(resourcelocation.getNamespace()).resolve(resourcelocation.getPath());
            Files.createDirectories(path1);
            entry.getValue().writeDebugInfo(path1);
        }

        this.dumpGameRules(p_223711_1_.resolve("gamerules.txt"));
        this.dumpClasspath(p_223711_1_.resolve("classpath.txt"));
        this.dumpDummyCrashReport(p_223711_1_.resolve("example_crash.txt"));
        this.dumpStats(p_223711_1_.resolve("stats.txt"));
        this.dumpThreads(p_223711_1_.resolve("threads.txt"));
    }

    private void dumpStats(Path p_223710_1_) throws IOException
    {
        try (Writer writer = Files.newBufferedWriter(p_223710_1_))
        {
            writer.write(String.format("pending_tasks: %d\n", this.getQueueSize()));
            writer.write(String.format("average_tick_time: %f\n", this.getTickTime()));
            writer.write(String.format("tick_times: %s\n", Arrays.toString(this.tickTimeArray)));
            writer.write(String.format("queue: %s\n", Util.getServerExecutor()));
        }
    }

    private void dumpDummyCrashReport(Path p_223709_1_) throws IOException
    {
        CrashReport crashreport = new CrashReport("Server dump", new Exception("dummy"));
        this.addServerInfoToCrashReport(crashreport);

        try (Writer writer = Files.newBufferedWriter(p_223709_1_))
        {
            writer.write(crashreport.getCompleteReport());
        }
    }

    private void dumpGameRules(Path p_223708_1_) throws IOException
    {
        try (Writer writer = Files.newBufferedWriter(p_223708_1_))
        {
            final List<String> list = Lists.newArrayList();
            final GameRules gamerules = this.getGameRules();
            GameRules.visitAll(new GameRules.IRuleEntryVisitor()
            {
                public <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> key, GameRules.RuleType<T> type)
                {
                    list.add(String.format("%s=%s\n", key.getName(), gamerules.<T>get(key).toString()));
                }
            });

            for (String s : list)
            {
                writer.write(s);
            }
        }
    }

    private void dumpClasspath(Path p_223706_1_) throws IOException
    {
        try (Writer writer = Files.newBufferedWriter(p_223706_1_))
        {
            String s = System.getProperty("java.class.path");
            String s1 = System.getProperty("path.separator");

            for (String s2 : Splitter.on(s1).split(s))
            {
                writer.write(s2);
                writer.write("\n");
            }
        }
    }

    private void dumpThreads(Path p_223712_1_) throws IOException
    {
        ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
        Arrays.sort(athreadinfo, Comparator.comparing(ThreadInfo::getThreadName));

        try (Writer writer = Files.newBufferedWriter(p_223712_1_))
        {
            for (ThreadInfo threadinfo : athreadinfo)
            {
                writer.write(threadinfo.toString());
                writer.write(10);
            }
        }
    }

    private void func_240773_a_(@Nullable LongTickDetector p_240773_1_)
    {
        if (this.startProfiling)
        {
            this.startProfiling = false;
            this.timeTracker.func_233507_c_();
        }

        this.profiler = LongTickDetector.func_233523_a_(this.timeTracker.func_233508_d_(), p_240773_1_);
    }

    private void func_240795_b_(@Nullable LongTickDetector p_240795_1_)
    {
        if (p_240795_1_ != null)
        {
            p_240795_1_.func_233525_b_();
        }

        this.profiler = this.timeTracker.func_233508_d_();
    }

    public boolean func_240789_aP_()
    {
        return this.timeTracker.func_233505_a_();
    }

    public void func_240790_aQ_()
    {
        this.startProfiling = true;
    }

    public IProfileResult func_240791_aR_()
    {
        IProfileResult iprofileresult = this.timeTracker.func_233509_e_();
        this.timeTracker.func_233506_b_();
        return iprofileresult;
    }

    public Path func_240776_a_(FolderName p_240776_1_)
    {
        return this.anvilConverterForAnvilFile.resolveFilePath(p_240776_1_);
    }

    public boolean func_230540_aS_()
    {
        return true;
    }

    public TemplateManager func_240792_aT_()
    {
        return this.field_240765_ak_;
    }

    public IServerConfiguration func_240793_aU_()
    {
        return this.field_240768_i_;
    }

    public DynamicRegistries func_244267_aX()
    {
        return this.field_240767_f_;
    }

    @Nullable
    public IChatFilter func_244435_a(ServerPlayerEntity p_244435_1_)
    {
        return null;
    }
}
