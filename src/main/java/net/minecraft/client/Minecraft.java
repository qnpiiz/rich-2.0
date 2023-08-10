package net.minecraft.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Queues;
import com.google.gson.JsonElement;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.OfflineSocialInteractions;
import com.mojang.authlib.minecraft.SocialInteractionsService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.PlatformDescriptors;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.LoadingGui;
import net.minecraft.client.gui.ResourceLoadProgressGui;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.fonts.FontResourceManager;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmBackupScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.DatapackFailureScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.EditWorldScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MemoryErrorScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.client.gui.screen.WinGameScreen;
import net.minecraft.client.gui.screen.WorkingScreen;
import net.minecraft.client.gui.screen.WorldLoadProgressScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.social.FilterManager;
import net.minecraft.client.gui.social.SocialInteractionsScreen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.login.ClientLoginNetHandler;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GPUWarning;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IWindowEventListener;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.PaintingSpriteUploader;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DownloadingPackFinder;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.LegacyResourcePackWrapper;
import net.minecraft.client.resources.LegacyResourcePackWrapperV4;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.util.IMutableSearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.client.util.SearchTreeReloadable;
import net.minecraft.client.util.Splashes;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.Commands;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.profiler.DataPoint;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.profiler.IProfileResult;
import net.minecraft.profiler.IProfiler;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.LongTickDetector;
import net.minecraft.profiler.Snooper;
import net.minecraft.profiler.TimeTracker;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.ServerPackFinder;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Timer;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenSettingsExport;
import net.minecraft.util.registry.WorldSettingsImport;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.KeybindTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.listener.ChainedChunkStatusListener;
import net.minecraft.world.chunk.listener.TrackingChunkStatusListener;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.ServerWorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fun.rich.Rich;
import fun.rich.event.EventManager;
import fun.rich.event.events.impl.input.EventMiddleMouse;
import fun.rich.font.FontUtil;
import fun.rich.font.MCFontRenderer;

public class Minecraft extends RecursiveEventLoop<Runnable> implements ISnooperInfo, IWindowEventListener
{
    private static Minecraft instance;
    private static final Logger LOGGER = LogManager.getLogger();
    public static final boolean IS_RUNNING_ON_MAC = Util.getOSType() == Util.OS.OSX;
    public static final ResourceLocation DEFAULT_FONT_RENDERER_NAME = new ResourceLocation("default");
    public static final ResourceLocation UNIFORM_FONT_RENDERER_NAME = new ResourceLocation("uniform");
    public static final ResourceLocation standardGalacticFontRenderer = new ResourceLocation("alt");
    private static final CompletableFuture<Unit> RESOURCE_RELOAD_INIT_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
    private static final ITextComponent field_244596_I = new TranslationTextComponent("multiplayer.socialInteractions.not_available");
    private final File fileResourcepacks;

    /** The player's GameProfile properties */
    private final PropertyMap profileProperties;
    private final TextureManager textureManager;
    private final DataFixer dataFixer;
    private final VirtualScreen virtualScreen;
    private final MainWindow mainWindow;
    public final Timer timer = new Timer(20.0F, 0L);
    private final Snooper snooper = new Snooper("client", this, Util.milliTime());
    private final RenderTypeBuffers renderTypeBuffers;
    public final WorldRenderer worldRenderer;
    private final EntityRendererManager renderManager;
    private final ItemRenderer itemRenderer;
    private final FirstPersonRenderer firstPersonRenderer;
    public final ParticleManager particles;
    private final SearchTreeManager searchTreeManager = new SearchTreeManager();
    private final Session session;


    public final FontRenderer fontRenderer;
    public MCFontRenderer sfui16;
    public MCFontRenderer sfui18;
    public MCFontRenderer neverlose500_13;
    public MCFontRenderer neverlose500_14;
    public MCFontRenderer tahoma;
    public MCFontRenderer tahoma16;
    public MCFontRenderer neverlose500_15;
    public MCFontRenderer neverlose900_20;
    public MCFontRenderer neverlose900_19;
    public MCFontRenderer neverlose900_18;
    public MCFontRenderer neverlose900_17;
    public MCFontRenderer neverlose900_16;
    public MCFontRenderer neverlose900_15;
    public MCFontRenderer notification;
    public MCFontRenderer rubik_30;
    public MCFontRenderer rubik_18;
    public MCFontRenderer rubik_17;
    public MCFontRenderer rubik_16;
    public MCFontRenderer rubik_15;
    public MCFontRenderer rubik_14;
    public MCFontRenderer rubik_13;
    public MCFontRenderer neverlose900_14;
    public MCFontRenderer neverlose900_30;
    public MCFontRenderer neverlose900_13;
    public MCFontRenderer neverlose500_16;
    public MCFontRenderer neverlose500_17;
    public MCFontRenderer neverlose500_19;
    public MCFontRenderer neverlose500_20;
    public MCFontRenderer neverlose500_18;


    public final @Getter GameRenderer gameRenderer;
    public final DebugRenderer debugRenderer;
    private final AtomicReference<TrackingChunkStatusListener> refChunkStatusListener = new AtomicReference<>();
    public final IngameGui ingameGUI;
    public final GameSettings gameSettings;
    private final CreativeSettings creativeSettings;
    public final MouseHelper mouseHelper;
    public final KeyboardListener keyboardListener;
    public final File gameDir;
    private final String launchedVersion;
    private final String versionType;
    private final Proxy proxy;
    private final SaveFormat saveFormat;

    /** The FrameTimer's instance */
    public final FrameTimer frameTimer = new FrameTimer();
    private final boolean jvm64bit;
    private final boolean isDemo;
    private final boolean enableMultiplayer;
    private final boolean enableChat;
    private final IReloadableResourceManager resourceManager;
    private final DownloadingPackFinder packFinder;
    private final ResourcePackList resourcePackRepository;
    private final LanguageManager languageManager;
    private final BlockColors blockColors;
    private final ItemColors itemColors;
    private final Framebuffer framebuffer;
    private final SoundHandler soundHandler;
    private final MusicTicker musicTicker;
    private final FontResourceManager fontResourceMananger;
    private final Splashes splashes;
    private final GPUWarning warningGPU;
    private final MinecraftSessionService sessionService;
    private final SocialInteractionsService field_244734_au;
    private final SkinManager skinManager;
    private final ModelManager modelManager;

    /**
     * The BlockRenderDispatcher instance that will be used based off gamesettings
     */
    private final BlockRendererDispatcher blockRenderDispatcher;
    private final PaintingSpriteUploader paintingSprites;
    private final PotionSpriteUploader potionSprites;
    private final ToastGui toastGui;
    private final MinecraftGame game = new MinecraftGame(this);
    private final Tutorial tutorial;
    private final FilterManager field_244597_aC;
    public static byte[] memoryReserve = new byte[10485760];
    @Nullable
    public PlayerController playerController;
    @Nullable
    public ClientWorld world;
    @Nullable
    public ClientPlayerEntity player;
    @Nullable
    private IntegratedServer integratedServer;
    @Nullable
    private ServerData currentServerData;
    @Nullable
    private NetworkManager networkManager;
    private boolean integratedServerIsRunning;
    @Nullable
    public Entity renderViewEntity;
    @Nullable
    public Entity pointedEntity;
    @Nullable
    public RayTraceResult objectMouseOver;
    private int rightClickDelayTimer;
    protected int leftClickCounter;
    private boolean isGamePaused;
    private float renderPartialTicksPaused;

    /** Time in nanoseconds of when the class is loaded */
    private long startNanoTime = Util.nanoTime();
    private long debugUpdateTime;
    private int fpsCounter;
    public boolean skipRenderWorld;
    @Nullable
    public Screen currentScreen;
    @Nullable
    public LoadingGui loadingGui;

    /** True if the player is connected to a realms server */
    private boolean connectedToRealms;
    private Thread thread;
    private volatile boolean running = true;
    @Nullable
    private CrashReport crashReporter;
    private static @Getter int debugFPS;
    public String debug = "";
    public boolean debugWireframe;
    public boolean debugChunkPath;
    public boolean renderChunksMany = true;
    private boolean isWindowFocused;
    private final Queue<Runnable> queueChunkTracking = Queues.newConcurrentLinkedQueue();
    @Nullable
    private CompletableFuture<Void> futureRefreshResources;
    @Nullable
    private TutorialToast field_244598_aV;
    private IProfiler profiler = EmptyProfiler.INSTANCE;
    private int gameTime;
    private final TimeTracker gameTimeTracker = new TimeTracker(Util.nanoTimeSupplier, () ->
    {
        return this.gameTime;
    });
    @Nullable
    private IProfileResult profilerResult;
    private String debugProfilerName = "root";

    public Minecraft(GameConfiguration gameConfig)
    {
        super("Client");
        instance = this;
        this.gameDir = gameConfig.folderInfo.gameDir;
        File file1 = gameConfig.folderInfo.assetsDir;
        this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
        this.launchedVersion = gameConfig.gameInfo.version;
        this.versionType = gameConfig.gameInfo.versionType;
        this.profileProperties = gameConfig.userInfo.profileProperties;
        this.packFinder = new DownloadingPackFinder(new File(this.gameDir, "server-resource-packs"), gameConfig.folderInfo.getAssetsIndex());
        this.resourcePackRepository = new ResourcePackList(Minecraft::makePackInfo, this.packFinder, new FolderPackFinder(this.fileResourcepacks, IPackNameDecorator.PLAIN));
        this.proxy = gameConfig.userInfo.proxy;
        YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(this.proxy);
        this.sessionService = yggdrasilauthenticationservice.createMinecraftSessionService();
        this.field_244734_au = this.func_244735_a(yggdrasilauthenticationservice, gameConfig);
        this.session = gameConfig.userInfo.session;
        LOGGER.info("Setting user: {}", (Object)this.session.getUsername());
        LOGGER.debug("(Session ID is {})", (Object)this.session.getSessionID());
        this.isDemo = gameConfig.gameInfo.isDemo;
        this.enableMultiplayer = !gameConfig.gameInfo.disableMultiplayer;
        this.enableChat = !gameConfig.gameInfo.disableChat;
        this.jvm64bit = isJvm64bit();
        this.integratedServer = null;
        String s;
        int i;

        if (this.isMultiplayerEnabled() && gameConfig.serverInfo.serverName != null)
        {
            s = gameConfig.serverInfo.serverName;
            i = gameConfig.serverInfo.serverPort;
        }
        else
        {
            s = null;
            i = 0;
        }

        KeybindTextComponent.func_240696_a_(KeyBinding::getDisplayString);
        this.dataFixer = DataFixesManager.getDataFixer();
        this.toastGui = new ToastGui(this);
        this.tutorial = new Tutorial(this);
        this.thread = Thread.currentThread();
        this.gameSettings = new GameSettings(this, this.gameDir);
        this.creativeSettings = new CreativeSettings(this.gameDir, this.dataFixer);
        LOGGER.info("Backend library: {}", (Object)RenderSystem.getBackendDescription());
        ScreenSize screensize;

        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0)
        {
            screensize = new ScreenSize(this.gameSettings.overrideWidth, this.gameSettings.overrideHeight, gameConfig.displayInfo.fullscreenWidth, gameConfig.displayInfo.fullscreenHeight, gameConfig.displayInfo.fullscreen);
        }
        else
        {
            screensize = gameConfig.displayInfo;
        }

        Util.nanoTimeSupplier = RenderSystem.initBackendSystem();
        this.virtualScreen = new VirtualScreen(this);
        this.mainWindow = this.virtualScreen.create(screensize, this.gameSettings.fullscreenResolution, this.getWindowTitle());
        this.setGameFocused(true);

        try
        {
            InputStream inputstream = this.getPackFinder().getVanillaPack().getResourceStream(ResourcePackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_16x16.png"));
            InputStream inputstream1 = this.getPackFinder().getVanillaPack().getResourceStream(ResourcePackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_32x32.png"));
            this.mainWindow.setWindowIcon(inputstream, inputstream1);
        }
        catch (IOException ioexception)
        {
            LOGGER.error("Couldn't set icon", (Throwable)ioexception);
        }

        this.mainWindow.setFramerateLimit(this.gameSettings.framerateLimit);
        this.mouseHelper = new MouseHelper(this);
        this.mouseHelper.registerCallbacks(this.mainWindow.getHandle());
        this.keyboardListener = new KeyboardListener(this);
        this.keyboardListener.setupCallbacks(this.mainWindow.getHandle());
        RenderSystem.initRenderer(this.gameSettings.glDebugVerbosity, false);
        this.framebuffer = new Framebuffer(this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight(), true, IS_RUNNING_ON_MAC);
        this.framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.resourceManager = new SimpleReloadableResourceManager(ResourcePackType.CLIENT_RESOURCES);
        this.resourcePackRepository.reloadPacksFromFinders();
        this.gameSettings.fillResourcePackList(this.resourcePackRepository);
        this.languageManager = new LanguageManager(this.gameSettings.language);
        this.resourceManager.addReloadListener(this.languageManager);
        this.textureManager = new TextureManager(this.resourceManager);
        this.resourceManager.addReloadListener(this.textureManager);
        this.skinManager = new SkinManager(this.textureManager, new File(file1, "skins"), this.sessionService);
        this.saveFormat = new SaveFormat(this.gameDir.toPath().resolve("saves"), this.gameDir.toPath().resolve("backups"), this.dataFixer);
        this.soundHandler = new SoundHandler(this.resourceManager, this.gameSettings);
        this.resourceManager.addReloadListener(this.soundHandler);
        this.splashes = new Splashes(this.session);
        this.resourceManager.addReloadListener(this.splashes);
        this.musicTicker = new MusicTicker(this);
        this.fontResourceMananger = new FontResourceManager(this.textureManager);


        this.fontRenderer = this.fontResourceMananger.func_238548_a_();
        this.sfui18 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/sf-ui.ttf"), 18.0f, 0), true, true);
        this.sfui16 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/sf-ui.ttf"), 16.0f, 0), true, true);
        this.neverlose500_13 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose500.ttf"), 13.0f, 0), true, true);
        this.neverlose500_14 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose500.ttf"), 14.0f, 0), true, true);
        this.neverlose500_15 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose500.ttf"), 15.0f, 0), true, true);
        this.neverlose500_16 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose500.ttf"), 16.0f, 0), true, true);
        this.neverlose500_17 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose500.ttf"), 17.0f, 0), true, true);
        this.neverlose500_18 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose500.ttf"), 18.0f, 0), true, true);
        this.neverlose500_19 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose500.ttf"), 19.0f, 0), true, true);
        this.neverlose500_20 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose500.ttf"), 20.0f, 0), true, true);
        this.neverlose900_13 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose900.ttf"), 13.0f, 0), true, true);
        this.neverlose900_14 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose900.ttf"), 14.0f, 0), true, true);
        this.neverlose900_15 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose900.ttf"), 15.0f, 0), true, true);
        this.neverlose900_16 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose900.ttf"), 16.0f, 0), true, true);
        this.neverlose900_17 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose900.ttf"), 17.0f, 0), true, true);
        this.neverlose900_18 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose900.ttf"), 18.0f, 0), true, true);
        this.neverlose900_19 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose900.ttf"), 19.0f, 0), true, true);
        this.neverlose900_20 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose900.ttf"), 20.0f, 0), true, true);
        this.neverlose900_30 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/neverlose900.ttf"), 50, 0), true, true);
        this.rubik_30 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/rubik.ttf"), 30.0f, 0), true, true);
        this.rubik_18 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/rubik.ttf"), 18.0f, 0), true, true);
        this.rubik_17 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/rubik.ttf"), 17.0f, 0), true, true);
        this.rubik_16 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/rubik.ttf"), 16.0f, 0), true, true);
        this.rubik_15 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/rubik.ttf"), 15.0f, 0), true, true);
        this.rubik_14 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/rubik.ttf"), 14.0f, 0), true, true);
        this.rubik_13 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/rubik.ttf"), 13.0f, 0), true, true);
        this.notification = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/notification.ttf"), 40.0f, 0), true, true);
        this.tahoma = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/tahoma.ttf"), 15.0f, 0), true, true);
        this.tahoma16 = new MCFontRenderer(FontUtil.getFontFromTTF(new ResourceLocation("font/tahoma.ttf"), 16.0f, 0), true, true);


        this.resourceManager.addReloadListener(this.fontResourceMananger.getReloadListener());
        this.forceUnicodeFont(this.getForceUnicodeFont());
        this.resourceManager.addReloadListener(new GrassColorReloadListener());
        this.resourceManager.addReloadListener(new FoliageColorReloadListener());
        this.mainWindow.setRenderPhase("Startup");
        RenderSystem.setupDefaultState(0, 0, this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight());
        this.mainWindow.setRenderPhase("Post startup");
        this.blockColors = BlockColors.init();
        this.itemColors = ItemColors.init(this.blockColors);
        this.modelManager = new ModelManager(this.textureManager, this.blockColors, this.gameSettings.mipmapLevels);
        this.resourceManager.addReloadListener(this.modelManager);
        this.itemRenderer = new ItemRenderer(this.textureManager, this.modelManager, this.itemColors);
        this.renderManager = new EntityRendererManager(this.textureManager, this.itemRenderer, this.resourceManager, this.fontRenderer, this.gameSettings);
        this.firstPersonRenderer = new FirstPersonRenderer(this);
        this.resourceManager.addReloadListener(this.itemRenderer);
        this.renderTypeBuffers = new RenderTypeBuffers();
        this.gameRenderer = new GameRenderer(this, this.resourceManager, this.renderTypeBuffers);
        this.resourceManager.addReloadListener(this.gameRenderer);
        this.field_244597_aC = new FilterManager(this, this.field_244734_au);
        this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.blockColors);
        this.resourceManager.addReloadListener(this.blockRenderDispatcher);
        this.worldRenderer = new WorldRenderer(this, this.renderTypeBuffers);
        this.resourceManager.addReloadListener(this.worldRenderer);
        this.populateSearchTreeManager();
        this.resourceManager.addReloadListener(this.searchTreeManager);
        this.particles = new ParticleManager(this.world, this.textureManager);
        this.resourceManager.addReloadListener(this.particles);
        this.paintingSprites = new PaintingSpriteUploader(this.textureManager);
        this.resourceManager.addReloadListener(this.paintingSprites);
        this.potionSprites = new PotionSpriteUploader(this.textureManager);
        this.resourceManager.addReloadListener(this.potionSprites);
        this.warningGPU = new GPUWarning();
        this.resourceManager.addReloadListener(this.warningGPU);
        this.ingameGUI = new IngameGui(this);
        this.debugRenderer = new DebugRenderer(this);
        RenderSystem.setErrorCallback(this::disableVSyncAfterGlError);

        if (this.gameSettings.fullscreen && !this.mainWindow.isFullscreen())
        {
            this.mainWindow.toggleFullscreen();
            this.gameSettings.fullscreen = this.mainWindow.isFullscreen();
        }

        this.mainWindow.setVsync(this.gameSettings.vsync);
        this.mainWindow.setRawMouseInput(this.gameSettings.rawMouseInput);
        this.mainWindow.setLogOnGlError();
        this.updateWindowSize();

        Rich.instance.init();

        if (s != null)
        {
            this.displayGuiScreen(new ConnectingScreen(new MainMenuScreen(), this, s, i));
        }
        else
        {
            this.displayGuiScreen(new MainMenuScreen(true));
        }

        ResourceLoadProgressGui.loadLogoTexture(this);
        List<IResourcePack> list = this.resourcePackRepository.func_232623_f_();
        this.setLoadingGui(new ResourceLoadProgressGui(this, this.resourceManager.reloadResources(Util.getServerExecutor(), this, RESOURCE_RELOAD_INIT_TASK, list), (throwable) ->
        {
            Util.acceptOrElse(throwable, this::restoreResourcePacks, () -> {
                if (SharedConstants.developmentMode)
                {
                    this.checkMissingData();
                }
            });
        }, false));
    }

    public void setDefaultMinecraftTitle()
    {
        this.mainWindow.setWindowTitle(this.getWindowTitle());
    }

    private String getWindowTitle()
    {
        StringBuilder stringbuilder = new StringBuilder("Minecraft");

        if (this.isModdedClient())
        {
            stringbuilder.append("*");
        }

        stringbuilder.append(" ");
        stringbuilder.append(SharedConstants.getVersion().getName());
        ClientPlayNetHandler clientplaynethandler = this.getConnection();

        if (clientplaynethandler != null && clientplaynethandler.getNetworkManager().isChannelOpen())
        {
            stringbuilder.append(" - ");

            if (this.integratedServer != null && !this.integratedServer.getPublic())
            {
                stringbuilder.append(I18n.format("title.singleplayer"));
            }
            else if (this.isConnectedToRealms())
            {
                stringbuilder.append(I18n.format("title.multiplayer.realms"));
            }
            else if (this.integratedServer == null && (this.currentServerData == null || !this.currentServerData.isOnLAN()))
            {
                stringbuilder.append(I18n.format("title.multiplayer.other"));
            }
            else
            {
                stringbuilder.append(I18n.format("title.multiplayer.lan"));
            }
        }

        return stringbuilder.toString();
    }

    private SocialInteractionsService func_244735_a(YggdrasilAuthenticationService p_244735_1_, GameConfiguration p_244735_2_)
    {
        try
        {
            return p_244735_1_.createSocialInteractionsService(p_244735_2_.userInfo.session.getToken());
        }
        catch (AuthenticationException authenticationexception)
        {
            LOGGER.error("Failed to verify authentication", (Throwable)authenticationexception);
            return new OfflineSocialInteractions();
        }
    }

    public boolean isModdedClient()
    {
        return !"vanilla".equals(ClientBrandRetriever.getClientModName()) || Minecraft.class.getSigners() == null;
    }

    private void restoreResourcePacks(Throwable throwableIn)
    {
        if (this.resourcePackRepository.func_232621_d_().size() > 1)
        {
            ITextComponent itextcomponent;

            if (throwableIn instanceof SimpleReloadableResourceManager.FailedPackException)
            {
                itextcomponent = new StringTextComponent(((SimpleReloadableResourceManager.FailedPackException)throwableIn).getPack().getName());
            }
            else
            {
                itextcomponent = null;
            }

            this.throwResourcePackLoadError(throwableIn, itextcomponent);
        }
        else
        {
            Util.toRuntimeException(throwableIn);
        }
    }

    public void throwResourcePackLoadError(Throwable throwable, @Nullable ITextComponent errorMessage)
    {
        LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", throwable);
        this.resourcePackRepository.setEnabledPacks(Collections.emptyList());
        this.gameSettings.resourcePacks.clear();
        this.gameSettings.incompatibleResourcePacks.clear();
        this.gameSettings.saveOptions();
        this.reloadResources().thenRun(() ->
        {
            ToastGui toastgui = this.getToastGui();
            SystemToast.addOrUpdate(toastgui, SystemToast.Type.PACK_LOAD_FAILURE, new TranslationTextComponent("resourcePack.load_fail"), errorMessage);
        });
    }

    public void run()
    {
        this.thread = Thread.currentThread();

        try
        {
            boolean flag = false;

            while (this.running)
            {
                if (this.crashReporter != null)
                {
                    displayCrashReport(this.crashReporter);
                    return;
                }

                try
                {
                    LongTickDetector longtickdetector = LongTickDetector.func_233524_a_("Renderer");
                    boolean flag1 = this.isDebugMode();
                    this.tick(flag1, longtickdetector);
                    this.profiler.startTick();
                    this.runGameLoop(!flag);
                    this.profiler.endTick();
                    this.func_238210_b_(flag1, longtickdetector);
                }
                catch (OutOfMemoryError outofmemoryerror)
                {
                    if (flag)
                    {
                        throw outofmemoryerror;
                    }

                    this.freeMemory();
                    this.displayGuiScreen(new MemoryErrorScreen());
                    System.gc();
                    LOGGER.fatal("Out of memory", (Throwable)outofmemoryerror);
                    flag = true;
                }
            }
        }
        catch (ReportedException reportedexception)
        {
            this.addGraphicsAndWorldToCrashReport(reportedexception.getCrashReport());
            this.freeMemory();
            LOGGER.fatal("Reported exception thrown!", (Throwable)reportedexception);
            displayCrashReport(reportedexception.getCrashReport());
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable));
            LOGGER.fatal("Unreported exception thrown!", throwable);
            this.freeMemory();
            displayCrashReport(crashreport);
        }
    }

    void forceUnicodeFont(boolean forced)
    {
        this.fontResourceMananger.func_238551_a_(forced ? ImmutableMap.of(DEFAULT_FONT_RENDERER_NAME, UNIFORM_FONT_RENDERER_NAME) : ImmutableMap.of());
    }

    /**
     * Fills {@link #searchTreeManager} with the current item and recipe registry contents.
     */
    private void populateSearchTreeManager()
    {
        SearchTree<ItemStack> searchtree = new SearchTree<>((stack) ->
        {
            return stack.getTooltip((PlayerEntity)null, ITooltipFlag.TooltipFlags.NORMAL).stream().map((textComponent) -> {
                return TextFormatting.getTextWithoutFormattingCodes(textComponent.getString()).trim();
            }).filter((name) -> {
                return !name.isEmpty();
            });
        }, (stack) ->
        {
            return Stream.of(Registry.ITEM.getKey(stack.getItem()));
        });
        SearchTreeReloadable<ItemStack> searchtreereloadable = new SearchTreeReloadable<>((stack) ->
        {
            return ItemTags.getCollection().getOwningTags(stack.getItem()).stream();
        });
        NonNullList<ItemStack> nonnulllist = NonNullList.create();

        for (Item item : Registry.ITEM)
        {
            item.fillItemGroup(ItemGroup.SEARCH, nonnulllist);
        }

        nonnulllist.forEach((stack) ->
        {
            searchtree.func_217872_a(stack);
            searchtreereloadable.func_217872_a(stack);
        });
        SearchTree<RecipeList> searchtree1 = new SearchTree<>((recipeList) ->
        {
            return recipeList.getRecipes().stream().flatMap((recipe) -> {
                return recipe.getRecipeOutput().getTooltip((PlayerEntity)null, ITooltipFlag.TooltipFlags.NORMAL).stream();
            }).map((textComponent) -> {
                return TextFormatting.getTextWithoutFormattingCodes(textComponent.getString()).trim();
            }).filter((name) -> {
                return !name.isEmpty();
            });
        }, (recipeList) ->
        {
            return recipeList.getRecipes().stream().map((recipe) -> {
                return Registry.ITEM.getKey(recipe.getRecipeOutput().getItem());
            });
        });
        this.searchTreeManager.add(SearchTreeManager.ITEMS, searchtree);
        this.searchTreeManager.add(SearchTreeManager.TAGS, searchtreereloadable);
        this.searchTreeManager.add(SearchTreeManager.RECIPES, searchtree1);
    }

    private void disableVSyncAfterGlError(int error, long description)
    {
        this.gameSettings.vsync = false;
        this.gameSettings.saveOptions();
    }

    private static boolean isJvm64bit()
    {
        String[] astring = new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

        for (String s : astring)
        {
            String s1 = System.getProperty(s);

            if (s1 != null && s1.contains("64"))
            {
                return true;
            }
        }

        return false;
    }

    public Framebuffer getFramebuffer()
    {
        return this.framebuffer;
    }

    /**
     * Gets the version that Minecraft was launched under (the name of a version JSON). Specified via the
     * <code>--version</code> flag.
     */
    public String getVersion()
    {
        return this.launchedVersion;
    }

    /**
     * Gets the type of version that Minecraft was launched under (as specified in the version JSON). Specified via the
     * <code>--versionType</code> flag.
     */
    public String getVersionType()
    {
        return this.versionType;
    }

    public void crashed(CrashReport crash)
    {
        this.crashReporter = crash;
    }

    /**
     * Wrapper around displayCrashReportInternal
     */
    public static void displayCrashReport(CrashReport report)
    {
        File file1 = new File(getInstance().gameDir, "crash-reports");
        File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
        Bootstrap.printToSYSOUT(report.getCompleteReport());

        if (report.getFile() != null)
        {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + report.getFile());
            System.exit(-1);
        }
        else if (report.saveToFile(file2))
        {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            System.exit(-1);
        }
        else
        {
            Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }

    public boolean getForceUnicodeFont()
    {
        return this.gameSettings.forceUnicodeFont;
    }

    public CompletableFuture<Void> reloadResources()
    {
        if (this.futureRefreshResources != null)
        {
            return this.futureRefreshResources;
        }
        else
        {
            CompletableFuture<Void> completablefuture = new CompletableFuture<>();

            if (this.loadingGui instanceof ResourceLoadProgressGui)
            {
                this.futureRefreshResources = completablefuture;
                return completablefuture;
            }
            else
            {
                this.resourcePackRepository.reloadPacksFromFinders();
                List<IResourcePack> list = this.resourcePackRepository.func_232623_f_();
                this.setLoadingGui(new ResourceLoadProgressGui(this, this.resourceManager.reloadResources(Util.getServerExecutor(), this, RESOURCE_RELOAD_INIT_TASK, list), (throwable) ->
                {
                    Util.acceptOrElse(throwable, this::restoreResourcePacks, () -> {
                        this.worldRenderer.loadRenderers();
                        completablefuture.complete((Void)null);
                    });
                }, true));
                return completablefuture;
            }
        }
    }

    private void checkMissingData()
    {
        boolean flag = false;
        BlockModelShapes blockmodelshapes = this.getBlockRendererDispatcher().getBlockModelShapes();
        IBakedModel ibakedmodel = blockmodelshapes.getModelManager().getMissingModel();

        for (Block block : Registry.BLOCK)
        {
            for (BlockState blockstate : block.getStateContainer().getValidStates())
            {
                if (blockstate.getRenderType() == BlockRenderType.MODEL)
                {
                    IBakedModel ibakedmodel1 = blockmodelshapes.getModel(blockstate);

                    if (ibakedmodel1 == ibakedmodel)
                    {
                        LOGGER.debug("Missing model for: {}", (Object)blockstate);
                        flag = true;
                    }
                }
            }
        }

        TextureAtlasSprite textureatlassprite1 = ibakedmodel.getParticleTexture();

        for (Block block1 : Registry.BLOCK)
        {
            for (BlockState blockstate1 : block1.getStateContainer().getValidStates())
            {
                TextureAtlasSprite textureatlassprite = blockmodelshapes.getTexture(blockstate1);

                if (!blockstate1.isAir() && textureatlassprite == textureatlassprite1)
                {
                    LOGGER.debug("Missing particle icon for: {}", (Object)blockstate1);
                    flag = true;
                }
            }
        }

        NonNullList<ItemStack> nonnulllist = NonNullList.create();

        for (Item item : Registry.ITEM)
        {
            nonnulllist.clear();
            item.fillItemGroup(ItemGroup.SEARCH, nonnulllist);

            for (ItemStack itemstack : nonnulllist)
            {
                String s = itemstack.getTranslationKey();
                String s1 = (new TranslationTextComponent(s)).getString();

                if (s1.toLowerCase(Locale.ROOT).equals(item.getTranslationKey()))
                {
                    LOGGER.debug("Missing translation for: {} {} {}", itemstack, s, itemstack.getItem());
                }
            }
        }

        flag = flag | ScreenManager.isMissingScreen();

        if (flag)
        {
            throw new IllegalStateException("Your game data is foobar, fix the errors above!");
        }
    }

    /**
     * Returns the save loader that is currently being used
     */
    public SaveFormat getSaveLoader()
    {
        return this.saveFormat;
    }

    private void openChatScreen(String defaultText)
    {
        if (!this.isIntegratedServerRunning() && !this.isChatEnabled())
        {
            if (this.player != null)
            {
                this.player.sendMessage((new TranslationTextComponent("chat.cannotSend")).mergeStyle(TextFormatting.RED), Util.DUMMY_UUID);
            }
        }
        else
        {
            this.displayGuiScreen(new ChatScreen(defaultText));
        }
    }

    public void displayGuiScreen(@Nullable Screen guiScreenIn)
    {
        if (this.currentScreen != null)
        {
            this.currentScreen.onClose();
        }

        if (guiScreenIn == null && this.world == null)
        {
            guiScreenIn = new MainMenuScreen();
        }
        else if (guiScreenIn == null && this.player.getShouldBeDead())
        {
            if (this.player.isShowDeathScreen())
            {
                guiScreenIn = new DeathScreen((ITextComponent)null, this.world.getWorldInfo().isHardcore());
            }
            else
            {
                this.player.respawnPlayer();
            }
        }

        if (guiScreenIn instanceof MainMenuScreen || guiScreenIn instanceof MultiplayerScreen)
        {
            this.gameSettings.showDebugInfo = false;
            this.ingameGUI.getChatGUI().clearChatMessages(true);
        }

        this.currentScreen = guiScreenIn;

        if (guiScreenIn != null)
        {
            this.mouseHelper.ungrabMouse();
            KeyBinding.unPressAllKeys();
            guiScreenIn.init(this, this.mainWindow.getScaledWidth(), this.mainWindow.getScaledHeight());
            this.skipRenderWorld = false;
            NarratorChatListener.INSTANCE.say(guiScreenIn.getNarrationMessage());
        }
        else
        {
            this.soundHandler.resume();
            this.mouseHelper.grabMouse();
        }

        this.setDefaultMinecraftTitle();
    }

    public void setLoadingGui(@Nullable LoadingGui loadingGuiIn)
    {
        this.loadingGui = loadingGuiIn;
    }

    /**
     * Shuts down the minecraft applet by stopping the resource downloads, and clearing up GL stuff; called when the
     * application (or web page) is exited.
     */
    public void shutdownMinecraftApplet()
    {
        try
        {
            LOGGER.info("Stopping!");

            try
            {
                NarratorChatListener.INSTANCE.close();
            }
            catch (Throwable throwable1)
            {
            }

            try
            {
                if (this.world != null)
                {
                    this.world.sendQuittingDisconnectingPacket();
                }

                this.unloadWorld();
            }
            catch (Throwable throwable)
            {
            }

            if (this.currentScreen != null)
            {
                this.currentScreen.onClose();
            }

            this.close();
        }
        finally
        {
            Util.nanoTimeSupplier = System::nanoTime;

            if (this.crashReporter == null)
            {
                System.exit(0);
            }
        }
    }

    public void close()
    {
        try
        {
            this.modelManager.close();
            this.fontResourceMananger.close();
            this.gameRenderer.close();
            this.worldRenderer.close();
            this.soundHandler.unloadSounds();
            this.resourcePackRepository.close();
            this.particles.close();
            this.potionSprites.close();
            this.paintingSprites.close();
            this.textureManager.close();
            this.resourceManager.close();
            Util.shutdown();
        }
        catch (Throwable throwable)
        {
            LOGGER.error("Shutdown failure!", throwable);
            throw throwable;
        }
        finally
        {
            this.virtualScreen.close();
            this.mainWindow.close();
        }
    }

    private void runGameLoop(boolean renderWorldIn)
    {
        this.mainWindow.setRenderPhase("Pre render");
        long i = Util.nanoTime();

        if (this.mainWindow.shouldClose())
        {
            this.shutdown();
        }

        if (this.futureRefreshResources != null && !(this.loadingGui instanceof ResourceLoadProgressGui))
        {
            CompletableFuture<Void> completablefuture = this.futureRefreshResources;
            this.futureRefreshResources = null;
            this.reloadResources().thenRun(() ->
            {
                completablefuture.complete((Void)null);
            });
        }

        Runnable runnable;

        while ((runnable = this.queueChunkTracking.poll()) != null)
        {
            runnable.run();
        }

        if (renderWorldIn)
        {
            int j = this.timer.getPartialTicks(Util.milliTime());
            this.profiler.startSection("scheduledExecutables");
            this.drainTasks();
            this.profiler.endSection();
            this.profiler.startSection("tick");

            for (int k = 0; k < Math.min(10, j); ++k)
            {
                this.profiler.func_230035_c_("clientTick");
                this.runTick();
            }

            this.profiler.endSection();
        }

        this.mouseHelper.updatePlayerLook();
        this.mainWindow.setRenderPhase("Render");
        this.profiler.startSection("sound");
        this.soundHandler.updateListener(this.gameRenderer.getActiveRenderInfo());
        this.profiler.endSection();
        this.profiler.startSection("render");
        RenderSystem.pushMatrix();
        RenderSystem.clear(16640, IS_RUNNING_ON_MAC);
        this.framebuffer.bindFramebuffer(true);
        FogRenderer.resetFog();
        this.profiler.startSection("display");
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        this.profiler.endSection();

        if (!this.skipRenderWorld)
        {
            this.profiler.endStartSection("gameRenderer");
            this.gameRenderer.updateCameraAndRender(this.isGamePaused ? this.renderPartialTicksPaused : this.timer.renderPartialTicks, i, renderWorldIn);
            this.profiler.endStartSection("toasts");
            this.toastGui.func_238541_a_(new MatrixStack());
            this.profiler.endSection();
        }

        if (this.profilerResult != null)
        {
            this.profiler.startSection("fpsPie");
            this.func_238183_a_(new MatrixStack(), this.profilerResult);
            this.profiler.endSection();
        }

        this.profiler.startSection("blit");
        this.framebuffer.unbindFramebuffer();
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        this.framebuffer.framebufferRender(this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight());
        RenderSystem.popMatrix();
        this.profiler.endStartSection("updateDisplay");
        this.mainWindow.flipFrame();
        int i1 = this.getFramerateLimit();

        if ((double)i1 < AbstractOption.FRAMERATE_LIMIT.getMaxValue())
        {
            RenderSystem.limitDisplayFPS(i1);
        }

        this.profiler.endStartSection("yield");
        Thread.yield();
        this.profiler.endSection();
        this.mainWindow.setRenderPhase("Post render");
        ++this.fpsCounter;
        boolean flag = this.isSingleplayer() && (this.currentScreen != null && this.currentScreen.isPauseScreen() || this.loadingGui != null && this.loadingGui.isPauseScreen()) && !this.integratedServer.getPublic();

        if (this.isGamePaused != flag)
        {
            if (this.isGamePaused)
            {
                this.renderPartialTicksPaused = this.timer.renderPartialTicks;
            }
            else
            {
                this.timer.renderPartialTicks = this.renderPartialTicksPaused;
            }

            this.isGamePaused = flag;
        }

        long l = Util.nanoTime();
        this.frameTimer.addFrame(l - this.startNanoTime);
        this.startNanoTime = l;
        this.profiler.startSection("fpsUpdate");

        while (Util.milliTime() >= this.debugUpdateTime + 1000L)
        {
            debugFPS = this.fpsCounter;
            this.debug = String.format("%d fps T: %s%s%s%s B: %d", debugFPS, (double)this.gameSettings.framerateLimit == AbstractOption.FRAMERATE_LIMIT.getMaxValue() ? "inf" : this.gameSettings.framerateLimit, this.gameSettings.vsync ? " vsync" : "", this.gameSettings.graphicFanciness.toString(), this.gameSettings.cloudOption == CloudOption.OFF ? "" : (this.gameSettings.cloudOption == CloudOption.FAST ? " fast-clouds" : " fancy-clouds"), this.gameSettings.biomeBlendRadius);
            this.debugUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.snooper.addMemoryStatsToSnooper();

            if (!this.snooper.isSnooperRunning())
            {
                this.snooper.start();
            }
        }

        this.profiler.endSection();
    }

    private boolean isDebugMode()
    {
        return this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI;
    }

    private void tick(boolean isDebug, @Nullable LongTickDetector detector)
    {
        if (isDebug)
        {
            if (!this.gameTimeTracker.func_233505_a_())
            {
                this.gameTime = 0;
                this.gameTimeTracker.func_233507_c_();
            }

            ++this.gameTime;
        }
        else
        {
            this.gameTimeTracker.func_233506_b_();
        }

        this.profiler = LongTickDetector.func_233523_a_(this.gameTimeTracker.func_233508_d_(), detector);
    }

    private void func_238210_b_(boolean isDebug, @Nullable LongTickDetector detector)
    {
        if (detector != null)
        {
            detector.func_233525_b_();
        }

        if (isDebug)
        {
            this.profilerResult = this.gameTimeTracker.func_233509_e_();
        }
        else
        {
            this.profilerResult = null;
        }

        this.profiler = this.gameTimeTracker.func_233508_d_();
    }

    public void updateWindowSize()
    {
        int i = this.mainWindow.calcGuiScale(this.gameSettings.guiScale, this.getForceUnicodeFont());
        this.mainWindow.setGuiScale((double)i);

        if (this.currentScreen != null)
        {
            this.currentScreen.resize(this, this.mainWindow.getScaledWidth(), this.mainWindow.getScaledHeight());
        }

        Framebuffer framebuffer = this.getFramebuffer();
        framebuffer.resize(this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight(), IS_RUNNING_ON_MAC);
        this.gameRenderer.updateShaderGroupSize(this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight());
        this.mouseHelper.setIgnoreFirstMove();
    }

    public void ignoreFirstMove()
    {
        this.mouseHelper.ignoreFirstMove();
    }

    private int getFramerateLimit()
    {
        return this.world != null || this.currentScreen == null && this.loadingGui == null ? this.mainWindow.getLimitFramerate() : 60;
    }

    /**
     * Attempts to free as much memory as possible, including leaving the world and running the garbage collector.
     */
    public void freeMemory()
    {
        try
        {
            memoryReserve = new byte[0];
            this.worldRenderer.deleteAllDisplayLists();
        }
        catch (Throwable throwable1)
        {
        }

        try
        {
            System.gc();

            if (this.integratedServerIsRunning && this.integratedServer != null)
            {
                this.integratedServer.initiateShutdown(true);
            }

            this.unloadWorld(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
        }
        catch (Throwable throwable)
        {
        }

        System.gc();
    }

    /**
     * Update debugProfilerName in response to number keys in debug screen
     */
    void updateDebugProfilerName(int keyCount)
    {
        if (this.profilerResult != null)
        {
            List<DataPoint> list = this.profilerResult.getDataPoints(this.debugProfilerName);

            if (!list.isEmpty())
            {
                DataPoint datapoint = list.remove(0);

                if (keyCount == 0)
                {
                    if (!datapoint.name.isEmpty())
                    {
                        int i = this.debugProfilerName.lastIndexOf(30);

                        if (i >= 0)
                        {
                            this.debugProfilerName = this.debugProfilerName.substring(0, i);
                        }
                    }
                }
                else
                {
                    --keyCount;

                    if (keyCount < list.size() && !"unspecified".equals((list.get(keyCount)).name))
                    {
                        if (!this.debugProfilerName.isEmpty())
                        {
                            this.debugProfilerName = this.debugProfilerName + '\u001e';
                        }

                        this.debugProfilerName = this.debugProfilerName + (list.get(keyCount)).name;
                    }
                }
            }
        }
    }

    private void func_238183_a_(MatrixStack matrixStack, IProfileResult profilerResult)
    {
        List<DataPoint> list = profilerResult.getDataPoints(this.debugProfilerName);
        DataPoint datapoint = list.remove(0);
        RenderSystem.clear(256, IS_RUNNING_ON_MAC);
        RenderSystem.matrixMode(5889);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0D, (double)this.mainWindow.getFramebufferWidth(), (double)this.mainWindow.getFramebufferHeight(), 0.0D, 1000.0D, 3000.0D);
        RenderSystem.matrixMode(5888);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
        RenderSystem.lineWidth(1.0F);
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        int i = 160;
        int j = this.mainWindow.getFramebufferWidth() - 160 - 10;
        int k = this.mainWindow.getFramebufferHeight() - 320;
        RenderSystem.enableBlend();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)((float)j - 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
        bufferbuilder.pos((double)((float)j - 176.0F), (double)(k + 320), 0.0D).color(200, 0, 0, 0).endVertex();
        bufferbuilder.pos((double)((float)j + 176.0F), (double)(k + 320), 0.0D).color(200, 0, 0, 0).endVertex();
        bufferbuilder.pos((double)((float)j + 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
        tessellator.draw();
        RenderSystem.disableBlend();
        double d0 = 0.0D;

        for (DataPoint datapoint1 : list)
        {
            int l = MathHelper.floor(datapoint1.relTime / 4.0D) + 1;
            bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
            int i1 = datapoint1.getTextColor();
            int j1 = i1 >> 16 & 255;
            int k1 = i1 >> 8 & 255;
            int l1 = i1 & 255;
            bufferbuilder.pos((double)j, (double)k, 0.0D).color(j1, k1, l1, 255).endVertex();

            for (int i2 = l; i2 >= 0; --i2)
            {
                float f = (float)((d0 + datapoint1.relTime * (double)i2 / (double)l) * (double)((float)Math.PI * 2F) / 100.0D);
                float f1 = MathHelper.sin(f) * 160.0F;
                float f2 = MathHelper.cos(f) * 160.0F * 0.5F;
                bufferbuilder.pos((double)((float)j + f1), (double)((float)k - f2), 0.0D).color(j1, k1, l1, 255).endVertex();
            }

            tessellator.draw();
            bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int l2 = l; l2 >= 0; --l2)
            {
                float f3 = (float)((d0 + datapoint1.relTime * (double)l2 / (double)l) * (double)((float)Math.PI * 2F) / 100.0D);
                float f4 = MathHelper.sin(f3) * 160.0F;
                float f5 = MathHelper.cos(f3) * 160.0F * 0.5F;

                if (!(f5 > 0.0F))
                {
                    bufferbuilder.pos((double)((float)j + f4), (double)((float)k - f5), 0.0D).color(j1 >> 1, k1 >> 1, l1 >> 1, 255).endVertex();
                    bufferbuilder.pos((double)((float)j + f4), (double)((float)k - f5 + 10.0F), 0.0D).color(j1 >> 1, k1 >> 1, l1 >> 1, 255).endVertex();
                }
            }

            tessellator.draw();
            d0 += datapoint1.relTime;
        }

        DecimalFormat decimalformat = new DecimalFormat("##0.00");
        decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        RenderSystem.enableTexture();
        String s = IProfileResult.decodePath(datapoint.name);
        String s1 = "";

        if (!"unspecified".equals(s))
        {
            s1 = s1 + "[0] ";
        }

        if (s.isEmpty())
        {
            s1 = s1 + "ROOT ";
        }
        else
        {
            s1 = s1 + s + ' ';
        }

        int k2 = 16777215;
        this.fontRenderer.drawStringWithShadow(matrixStack, s1, (float)(j - 160), (float)(k - 80 - 16), 16777215);
        s1 = decimalformat.format(datapoint.rootRelTime) + "%";
        this.fontRenderer.drawStringWithShadow(matrixStack, s1, (float)(j + 160 - this.fontRenderer.getStringWidth(s1)), (float)(k - 80 - 16), 16777215);

        for (int j2 = 0; j2 < list.size(); ++j2)
        {
            DataPoint datapoint2 = list.get(j2);
            StringBuilder stringbuilder = new StringBuilder();

            if ("unspecified".equals(datapoint2.name))
            {
                stringbuilder.append("[?] ");
            }
            else
            {
                stringbuilder.append("[").append(j2 + 1).append("] ");
            }

            String s2 = stringbuilder.append(datapoint2.name).toString();
            this.fontRenderer.drawStringWithShadow(matrixStack, s2, (float)(j - 160), (float)(k + 80 + j2 * 8 + 20), datapoint2.getTextColor());
            s2 = decimalformat.format(datapoint2.relTime) + "%";
            this.fontRenderer.drawStringWithShadow(matrixStack, s2, (float)(j + 160 - 50 - this.fontRenderer.getStringWidth(s2)), (float)(k + 80 + j2 * 8 + 20), datapoint2.getTextColor());
            s2 = decimalformat.format(datapoint2.rootRelTime) + "%";
            this.fontRenderer.drawStringWithShadow(matrixStack, s2, (float)(j + 160 - this.fontRenderer.getStringWidth(s2)), (float)(k + 80 + j2 * 8 + 20), datapoint2.getTextColor());
        }
    }

    /**
     * Called when the window is closing. Sets 'running' to false which allows the game loop to exit cleanly.
     */
    public void shutdown()
    {
        this.running = false;
    }

    public boolean isRunning()
    {
        return this.running;
    }

    /**
     * Displays the ingame menu
     */
    public void displayInGameMenu(boolean pauseOnly)
    {
        if (this.currentScreen == null)
        {
            boolean flag = this.isSingleplayer() && !this.integratedServer.getPublic();

            if (flag)
            {
                this.displayGuiScreen(new IngameMenuScreen(!pauseOnly));
                this.soundHandler.pause();
            }
            else
            {
                this.displayGuiScreen(new IngameMenuScreen(true));
            }
        }
    }

    private void sendClickBlockToController(boolean leftClick)
    {
        if (!leftClick)
        {
            this.leftClickCounter = 0;
        }

        if (this.leftClickCounter <= 0 && !this.player.isHandActive())
        {
            if (leftClick && this.objectMouseOver != null && this.objectMouseOver.getType() == RayTraceResult.Type.BLOCK)
            {
                BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)this.objectMouseOver;
                BlockPos blockpos = blockraytraceresult.getPos();

                if (!this.world.getBlockState(blockpos).isAir())
                {
                    Direction direction = blockraytraceresult.getFace();

                    if (this.playerController.onPlayerDamageBlock(blockpos, direction))
                    {
                        this.particles.addBlockHitEffects(blockpos, direction);
                        this.player.swingArm(Hand.MAIN_HAND);
                    }
                }
            }
            else
            {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    private void clickMouse()
    {
        if (this.leftClickCounter <= 0)
        {
            if (this.objectMouseOver == null)
            {
                LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");

                if (this.playerController.isNotCreative())
                {
                    this.leftClickCounter = 10;
                }
            }
            else if (!this.player.isRowingBoat())
            {
                switch (this.objectMouseOver.getType())
                {
                    case ENTITY:
                        this.playerController.attackEntity(this.player, ((EntityRayTraceResult)this.objectMouseOver).getEntity());
                        break;

                    case BLOCK:
                        BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)this.objectMouseOver;
                        BlockPos blockpos = blockraytraceresult.getPos();

                        if (!this.world.getBlockState(blockpos).isAir())
                        {
                            this.playerController.clickBlock(blockpos, blockraytraceresult.getFace());
                            break;
                        }

                    case MISS:
                        if (this.playerController.isNotCreative())
                        {
                            this.leftClickCounter = 10;
                        }

                        this.player.resetCooldown();
                }

                this.player.swingArm(Hand.MAIN_HAND);
            }
        }
    }

    /**
     * Called when user clicked he's mouse right button (place)
     */
    private void rightClickMouse()
    {
        if (!this.playerController.getIsHittingBlock())
        {
            this.rightClickDelayTimer = 4;

            if (!this.player.isRowingBoat())
            {
                if (this.objectMouseOver == null)
                {
                    LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
                }

                for (Hand hand : Hand.values())
                {
                    ItemStack itemstack = this.player.getHeldItem(hand);

                    if (this.objectMouseOver != null)
                    {
                        switch (this.objectMouseOver.getType())
                        {
                            case ENTITY:
                                EntityRayTraceResult entityraytraceresult = (EntityRayTraceResult)this.objectMouseOver;
                                Entity entity = entityraytraceresult.getEntity();
                                ActionResultType actionresulttype = this.playerController.interactWithEntity(this.player, entity, entityraytraceresult, hand);

                                if (!actionresulttype.isSuccessOrConsume())
                                {
                                    actionresulttype = this.playerController.interactWithEntity(this.player, entity, hand);
                                }

                                if (actionresulttype.isSuccessOrConsume())
                                {
                                    if (actionresulttype.isSuccess())
                                    {
                                        this.player.swingArm(hand);
                                    }

                                    return;
                                }

                                break;

                            case BLOCK:
                                BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)this.objectMouseOver;
                                int i = itemstack.getCount();
                                ActionResultType actionresulttype1 = this.playerController.func_217292_a(this.player, this.world, hand, blockraytraceresult);

                                if (actionresulttype1.isSuccessOrConsume())
                                {
                                    if (actionresulttype1.isSuccess())
                                    {
                                        this.player.swingArm(hand);

                                        if (!itemstack.isEmpty() && (itemstack.getCount() != i || this.playerController.isInCreativeMode()))
                                        {
                                            this.gameRenderer.itemRenderer.resetEquippedProgress(hand);
                                        }
                                    }

                                    return;
                                }

                                if (actionresulttype1 == ActionResultType.FAIL)
                                {
                                    return;
                                }
                        }
                    }

                    if (!itemstack.isEmpty())
                    {
                        ActionResultType actionresulttype2 = this.playerController.processRightClick(this.player, this.world, hand);

                        if (actionresulttype2.isSuccessOrConsume())
                        {
                            if (actionresulttype2.isSuccess())
                            {
                                this.player.swingArm(hand);
                            }

                            this.gameRenderer.itemRenderer.resetEquippedProgress(hand);
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Return the musicTicker's instance
     */
    public MusicTicker getMusicTicker()
    {
        return this.musicTicker;
    }

    /**
     * Runs the current tick.
     */
    public void runTick()
    {
        if (this.rightClickDelayTimer > 0)
        {
            --this.rightClickDelayTimer;
        }

        this.profiler.startSection("gui");

        if (!this.isGamePaused)
        {
            this.ingameGUI.tick();
        }

        this.profiler.endSection();
        this.gameRenderer.getMouseOver(1.0F);
        this.tutorial.onMouseHover(this.world, this.objectMouseOver);
        this.profiler.startSection("gameMode");

        if (!this.isGamePaused && this.world != null)
        {
            this.playerController.tick();
        }

        this.profiler.endStartSection("textures");

        if (this.world != null)
        {
            this.textureManager.tick();
        }

        if (this.currentScreen == null && this.player != null)
        {
            if (this.player.getShouldBeDead() && !(this.currentScreen instanceof DeathScreen))
            {
                this.displayGuiScreen((Screen)null);
            }
            else if (this.player.isSleeping() && this.world != null)
            {
                this.displayGuiScreen(new SleepInMultiplayerScreen());
            }
        }
        else if (this.currentScreen != null && this.currentScreen instanceof SleepInMultiplayerScreen && !this.player.isSleeping())
        {
            this.displayGuiScreen((Screen)null);
        }

        if (this.currentScreen != null)
        {
            this.leftClickCounter = 10000;
        }

        if (this.currentScreen != null)
        {
            Screen.wrapScreenError(() ->
            {
                this.currentScreen.tick();
            }, "Ticking screen", this.currentScreen.getClass().getCanonicalName());
        }

        if (!this.gameSettings.showDebugInfo)
        {
            this.ingameGUI.reset();
        }

        if (this.loadingGui == null && (this.currentScreen == null || this.currentScreen.passEvents))
        {
            this.profiler.endStartSection("Keybindings");
            this.processKeyBinds();

            if (this.leftClickCounter > 0)
            {
                --this.leftClickCounter;
            }
        }

        if (this.world != null)
        {
            this.profiler.endStartSection("gameRenderer");

            if (!this.isGamePaused)
            {
                this.gameRenderer.tick();
            }

            this.profiler.endStartSection("levelRenderer");

            if (!this.isGamePaused)
            {
                this.worldRenderer.tick();
            }

            this.profiler.endStartSection("level");

            if (!this.isGamePaused)
            {
                if (this.world.getTimeLightningFlash() > 0)
                {
                    this.world.setTimeLightningFlash(this.world.getTimeLightningFlash() - 1);
                }

                this.world.tickEntities();
            }
        }
        else if (this.gameRenderer.getShaderGroup() != null)
        {
            this.gameRenderer.stopUseShader();
        }

        if (!this.isGamePaused)
        {
            this.musicTicker.tick();
        }

        this.soundHandler.tick(this.isGamePaused);

        if (this.world != null)
        {
            if (!this.isGamePaused)
            {
                if (!this.gameSettings.field_244601_E && this.func_244600_aM())
                {
                    ITextComponent itextcomponent = new TranslationTextComponent("tutorial.socialInteractions.title");
                    ITextComponent itextcomponent1 = new TranslationTextComponent("tutorial.socialInteractions.description", Tutorial.createKeybindComponent("socialInteractions"));
                    this.field_244598_aV = new TutorialToast(TutorialToast.Icons.SOCIAL_INTERACTIONS, itextcomponent, itextcomponent1, true);
                    this.tutorial.func_244698_a(this.field_244598_aV, 160);
                    this.gameSettings.field_244601_E = true;
                    this.gameSettings.saveOptions();
                }

                this.tutorial.tick();

                try
                {
                    this.world.tick(() ->
                    {
                        return true;
                    });
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception in world tick");

                    if (this.world == null)
                    {
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected level");
                        crashreportcategory.addDetail("Problem", "Level is null!");
                    }
                    else
                    {
                        this.world.fillCrashReport(crashreport);
                    }

                    throw new ReportedException(crashreport);
                }
            }

            this.profiler.endStartSection("animateTick");

            if (!this.isGamePaused && this.world != null)
            {
                this.world.animateTick(MathHelper.floor(this.player.getPosX()), MathHelper.floor(this.player.getPosY()), MathHelper.floor(this.player.getPosZ()));
            }

            this.profiler.endStartSection("particles");

            if (!this.isGamePaused)
            {
                this.particles.tick();
            }
        }
        else if (this.networkManager != null)
        {
            this.profiler.endStartSection("pendingConnection");
            this.networkManager.tick();
        }

        this.profiler.endStartSection("keyboard");
        this.keyboardListener.tick();
        this.profiler.endSection();
    }

    private boolean func_244600_aM()
    {
        return !this.integratedServerIsRunning || this.integratedServer != null && this.integratedServer.getPublic();
    }

    private void processKeyBinds()
    {
        for (; this.gameSettings.keyBindTogglePerspective.isPressed(); this.worldRenderer.setDisplayListEntitiesDirty())
        {
            PointOfView pointofview = this.gameSettings.getPointOfView();
            this.gameSettings.setPointOfView(this.gameSettings.getPointOfView().func_243194_c());

            if (pointofview.func_243192_a() != this.gameSettings.getPointOfView().func_243192_a())
            {
                this.gameRenderer.loadEntityShader(this.gameSettings.getPointOfView().func_243192_a() ? this.getRenderViewEntity() : null);
            }
        }

        while (this.gameSettings.keyBindSmoothCamera.isPressed())
        {
            this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
        }

        for (int i = 0; i < 9; ++i)
        {
            boolean flag = this.gameSettings.keyBindSaveToolbar.isKeyDown();
            boolean flag1 = this.gameSettings.keyBindLoadToolbar.isKeyDown();

            if (this.gameSettings.keyBindsHotbar[i].isPressed())
            {
                if (this.player.isSpectator())
                {
                    this.ingameGUI.getSpectatorGui().onHotbarSelected(i);
                }
                else if (!this.player.isCreative() || this.currentScreen != null || !flag1 && !flag)
                {
                    this.player.inventory.currentItem = i;
                }
                else
                {
                    CreativeScreen.handleHotbarSnapshots(this, i, flag1, flag);
                }
            }
        }

        while (this.gameSettings.field_244602_au.isPressed())
        {
            if (!this.func_244600_aM())
            {
                this.player.sendStatusMessage(field_244596_I, true);
                NarratorChatListener.INSTANCE.say(field_244596_I.getString());
            }
            else
            {
                if (this.field_244598_aV != null)
                {
                    this.tutorial.func_244697_a(this.field_244598_aV);
                    this.field_244598_aV = null;
                }

                this.displayGuiScreen(new SocialInteractionsScreen());
            }
        }

        while (this.gameSettings.keyBindInventory.isPressed())
        {
            if (this.playerController.isRidingHorse())
            {
                this.player.sendHorseInventory();
            }
            else
            {
                this.tutorial.openInventory();
                this.displayGuiScreen(new InventoryScreen(this.player));
            }
        }

        while (this.gameSettings.keyBindAdvancements.isPressed())
        {
            this.displayGuiScreen(new AdvancementsScreen(this.player.connection.getAdvancementManager()));
        }

        while (this.gameSettings.keyBindSwapHands.isPressed())
        {
            if (!this.player.isSpectator())
            {
                this.getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
            }
        }

        while (this.gameSettings.keyBindDrop.isPressed())
        {
            if (!this.player.isSpectator() && this.player.drop(Screen.hasControlDown()))
            {
                this.player.swingArm(Hand.MAIN_HAND);
            }
        }

        boolean flag2 = this.gameSettings.chatVisibility != ChatVisibility.HIDDEN;

        if (flag2)
        {
            while (this.gameSettings.keyBindChat.isPressed())
            {
                this.openChatScreen("");
            }

            if (this.currentScreen == null && this.loadingGui == null && this.gameSettings.keyBindCommand.isPressed())
            {
                this.openChatScreen("/");
            }
        }

        if (this.player.isHandActive())
        {
            if (!this.gameSettings.keyBindUseItem.isKeyDown())
            {
                this.playerController.onStoppedUsingItem(this.player);
            }

            while (this.gameSettings.keyBindAttack.isPressed())
            {
            }

            while (this.gameSettings.keyBindUseItem.isPressed())
            {
            }

            while (this.gameSettings.keyBindPickBlock.isPressed())
            {
            }
        }
        else
        {
            while (this.gameSettings.keyBindAttack.isPressed())
            {
                this.clickMouse();
            }

            while (this.gameSettings.keyBindUseItem.isPressed())
            {
                this.rightClickMouse();
            }

            while (this.gameSettings.keyBindPickBlock.isPressed())
            {
                this.middleClickMouse();
            }
        }

        if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.player.isHandActive())
        {
            this.rightClickMouse();
        }

        this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.mouseHelper.isMouseGrabbed());
    }

    public static DatapackCodec loadDataPackCodec(SaveFormat.LevelSave worldStorage)
    {
        MinecraftServer.func_240777_a_(worldStorage);
        DatapackCodec datapackcodec = worldStorage.readDatapackCodec();

        if (datapackcodec == null)
        {
            throw new IllegalStateException("Failed to load data pack config");
        }
        else
        {
            return datapackcodec;
        }
    }

    public static IServerConfiguration loadWorld(SaveFormat.LevelSave worldStorage, DynamicRegistries.Impl dynamicRegistries, IResourceManager resourceManager, DatapackCodec datapackCodec)
    {
        WorldSettingsImport<INBT> worldsettingsimport = WorldSettingsImport.create(NBTDynamicOps.INSTANCE, resourceManager, dynamicRegistries);
        IServerConfiguration iserverconfiguration = worldStorage.readServerConfiguration(worldsettingsimport, datapackCodec);

        if (iserverconfiguration == null)
        {
            throw new IllegalStateException("Failed to load world");
        }
        else
        {
            return iserverconfiguration;
        }
    }

    public void loadWorld(String worldName)
    {
        this.loadWorld(worldName, DynamicRegistries.func_239770_b_(), Minecraft::loadDataPackCodec, Minecraft::loadWorld, false, Minecraft.WorldSelectionType.BACKUP);
    }

    public void createWorld(String worldName, WorldSettings worldSettings, DynamicRegistries.Impl dynamicRegistriesIn, DimensionGeneratorSettings dimensionGeneratorSettings)
    {
        this.loadWorld(worldName, dynamicRegistriesIn, (worldStorage) ->
        {
            return worldSettings.getDatapackCodec();
        }, (worldStorage, dynamicRegistries, resourceManager, datapackCodec) ->
        {
            WorldGenSettingsExport<JsonElement> worldgensettingsexport = WorldGenSettingsExport.create(JsonOps.INSTANCE, dynamicRegistriesIn);
            WorldSettingsImport<JsonElement> worldsettingsimport = WorldSettingsImport.create(JsonOps.INSTANCE, resourceManager, dynamicRegistriesIn);
            DataResult<DimensionGeneratorSettings> dataresult = DimensionGeneratorSettings.field_236201_a_.encodeStart(worldgensettingsexport, dimensionGeneratorSettings).setLifecycle(Lifecycle.stable()).flatMap((p_243209_1_) -> {
                return DimensionGeneratorSettings.field_236201_a_.parse(worldsettingsimport, p_243209_1_);
            });
            DimensionGeneratorSettings dimensiongeneratorsettings = dataresult.resultOrPartial(Util.func_240982_a_("Error reading worldgen settings after loading data packs: ", LOGGER::error)).orElse(dimensionGeneratorSettings);
            return new ServerWorldInfo(worldSettings, dimensiongeneratorsettings, dataresult.lifecycle());
        }, false, Minecraft.WorldSelectionType.CREATE);
    }

    private void loadWorld(String worldName, DynamicRegistries.Impl dynamicRegistries, Function<SaveFormat.LevelSave, DatapackCodec> levelSaveToDatapackFunction, Function4<SaveFormat.LevelSave, DynamicRegistries.Impl, IResourceManager, DatapackCodec, IServerConfiguration> quadFunction, boolean vanillaOnly, Minecraft.WorldSelectionType selectionType)
    {
        SaveFormat.LevelSave saveformat$levelsave;

        try
        {
            saveformat$levelsave = this.saveFormat.getLevelSave(worldName);
        }
        catch (IOException ioexception2)
        {
            LOGGER.warn("Failed to read level {} data", worldName, ioexception2);
            SystemToast.func_238535_a_(this, worldName);
            this.displayGuiScreen((Screen)null);
            return;
        }

        Minecraft.PackManager minecraft$packmanager;

        try
        {
            minecraft$packmanager = this.reloadDatapacks(dynamicRegistries, levelSaveToDatapackFunction, quadFunction, vanillaOnly, saveformat$levelsave);
        }
        catch (Exception exception)
        {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load", (Throwable)exception);
            this.displayGuiScreen(new DatapackFailureScreen(() ->
            {
                this.loadWorld(worldName, dynamicRegistries, levelSaveToDatapackFunction, quadFunction, true, selectionType);
            }));

            try
            {
                saveformat$levelsave.close();
            }
            catch (IOException ioexception)
            {
                LOGGER.warn("Failed to unlock access to level {}", worldName, ioexception);
            }

            return;
        }

        IServerConfiguration iserverconfiguration = minecraft$packmanager.getServerConfiguration();
        boolean flag = iserverconfiguration.getDimensionGeneratorSettings().func_236229_j_();
        boolean flag1 = iserverconfiguration.getLifecycle() != Lifecycle.stable();

        if (selectionType == Minecraft.WorldSelectionType.NONE || !flag && !flag1)
        {
            this.unloadWorld();
            this.refChunkStatusListener.set((TrackingChunkStatusListener)null);

            try
            {
                saveformat$levelsave.saveLevel(dynamicRegistries, iserverconfiguration);
                minecraft$packmanager.getDataPackRegistries().updateTags();
                YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(this.proxy);
                MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
                GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
                PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.gameDir, MinecraftServer.USER_CACHE_FILE.getName()));
                SkullTileEntity.setProfileCache(playerprofilecache);
                SkullTileEntity.setSessionService(minecraftsessionservice);
                PlayerProfileCache.setOnlineMode(false);
                this.integratedServer = MinecraftServer.func_240784_a_((thread) ->
                {
                    return new IntegratedServer(thread, this, dynamicRegistries, saveformat$levelsave, minecraft$packmanager.getResourcePacks(), minecraft$packmanager.getDataPackRegistries(), iserverconfiguration, minecraftsessionservice, gameprofilerepository, playerprofilecache, (radius) -> {
                        TrackingChunkStatusListener trackingchunkstatuslistener = new TrackingChunkStatusListener(radius + 0);
                        trackingchunkstatuslistener.startTracking();
                        this.refChunkStatusListener.set(trackingchunkstatuslistener);
                        return new ChainedChunkStatusListener(trackingchunkstatuslistener, this.queueChunkTracking::add);
                    });
                });
                this.integratedServerIsRunning = true;
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
                crashreportcategory.addDetail("Level ID", worldName);
                crashreportcategory.addDetail("Level Name", iserverconfiguration.getWorldName());
                throw new ReportedException(crashreport);
            }

            while (this.refChunkStatusListener.get() == null)
            {
                Thread.yield();
            }

            WorldLoadProgressScreen worldloadprogressscreen = new WorldLoadProgressScreen(this.refChunkStatusListener.get());
            this.displayGuiScreen(worldloadprogressscreen);
            this.profiler.startSection("waitForServer");

            while (!this.integratedServer.serverIsInRunLoop())
            {
                worldloadprogressscreen.tick();
                this.runGameLoop(false);

                try
                {
                    Thread.sleep(16L);
                }
                catch (InterruptedException interruptedexception)
                {
                }

                if (this.crashReporter != null)
                {
                    displayCrashReport(this.crashReporter);
                    return;
                }
            }

            this.profiler.endSection();
            SocketAddress socketaddress = this.integratedServer.getNetworkSystem().addLocalEndpoint();
            NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
            networkmanager.setNetHandler(new ClientLoginNetHandler(networkmanager, this, (Screen)null, (statusMessage) ->
            {
            }));
            networkmanager.sendPacket(new CHandshakePacket(socketaddress.toString(), 0, ProtocolType.LOGIN));
            networkmanager.sendPacket(new CLoginStartPacket(this.getSession().getProfile()));
            this.networkManager = networkmanager;
        }
        else
        {
            this.deleteWorld(selectionType, worldName, flag, () ->
            {
                this.loadWorld(worldName, dynamicRegistries, levelSaveToDatapackFunction, quadFunction, vanillaOnly, Minecraft.WorldSelectionType.NONE);
            });
            minecraft$packmanager.close();

            try
            {
                saveformat$levelsave.close();
            }
            catch (IOException ioexception1)
            {
                LOGGER.warn("Failed to unlock access to level {}", worldName, ioexception1);
            }
        }
    }

    private void deleteWorld(Minecraft.WorldSelectionType selectionType, String worldName, boolean customized, Runnable runnable)
    {
        if (selectionType == Minecraft.WorldSelectionType.BACKUP)
        {
            ITextComponent itextcomponent;
            ITextComponent itextcomponent1;

            if (customized)
            {
                itextcomponent = new TranslationTextComponent("selectWorld.backupQuestion.customized");
                itextcomponent1 = new TranslationTextComponent("selectWorld.backupWarning.customized");
            }
            else
            {
                itextcomponent = new TranslationTextComponent("selectWorld.backupQuestion.experimental");
                itextcomponent1 = new TranslationTextComponent("selectWorld.backupWarning.experimental");
            }

            this.displayGuiScreen(new ConfirmBackupScreen((Screen)null, (editMode, checkedBox) ->
            {
                if (editMode)
                {
                    EditWorldScreen.func_241651_a_(this.saveFormat, worldName);
                }

                runnable.run();
            }, itextcomponent, itextcomponent1, false));
        }
        else
        {
            this.displayGuiScreen(new ConfirmScreen((confirm) ->
            {
                if (confirm)
                {
                    runnable.run();
                }
                else {
                    this.displayGuiScreen((Screen)null);

                    try (SaveFormat.LevelSave saveformat$levelsave = this.saveFormat.getLevelSave(worldName))
                    {
                        saveformat$levelsave.deleteSave();
                    }
                    catch (IOException ioexception)
                    {
                        SystemToast.func_238538_b_(this, worldName);
                        LOGGER.error("Failed to delete world {}", worldName, ioexception);
                    }
                }
            }, new TranslationTextComponent("selectWorld.backupQuestion.experimental"), new TranslationTextComponent("selectWorld.backupWarning.experimental"), DialogTexts.GUI_PROCEED, DialogTexts.GUI_CANCEL));
        }
    }

    public Minecraft.PackManager reloadDatapacks(DynamicRegistries.Impl dynamicRegistries, Function<SaveFormat.LevelSave, DatapackCodec> worldStorageToDatapackFunction, Function4<SaveFormat.LevelSave, DynamicRegistries.Impl, IResourceManager, DatapackCodec, IServerConfiguration> quadFunction, boolean vanillaOnly, SaveFormat.LevelSave worldStorage) throws InterruptedException, ExecutionException
    {
        DatapackCodec datapackcodec = worldStorageToDatapackFunction.apply(worldStorage);
        ResourcePackList resourcepacklist = new ResourcePackList(new ServerPackFinder(), new FolderPackFinder(worldStorage.resolveFilePath(FolderName.DATAPACKS).toFile(), IPackNameDecorator.WORLD));

        try
        {
            DatapackCodec datapackcodec1 = MinecraftServer.func_240772_a_(resourcepacklist, datapackcodec, vanillaOnly);
            CompletableFuture<DataPackRegistries> completablefuture = DataPackRegistries.func_240961_a_(resourcepacklist.func_232623_f_(), Commands.EnvironmentType.INTEGRATED, 2, Util.getServerExecutor(), this);
            this.driveUntil(completablefuture::isDone);
            DataPackRegistries datapackregistries = completablefuture.get();
            IServerConfiguration iserverconfiguration = quadFunction.apply(worldStorage, dynamicRegistries, datapackregistries.getResourceManager(), datapackcodec1);
            return new Minecraft.PackManager(resourcepacklist, datapackregistries, iserverconfiguration);
        }
        catch (ExecutionException | InterruptedException interruptedexception)
        {
            resourcepacklist.close();
            throw interruptedexception;
        }
    }

    /**
     * unloads the current world first
     */
    public void loadWorld(ClientWorld worldClientIn)
    {
        WorkingScreen workingscreen = new WorkingScreen();
        workingscreen.displaySavingString(new TranslationTextComponent("connect.joining"));
        this.updateScreenTick(workingscreen);
        this.world = worldClientIn;
        this.updateWorldRenderer(worldClientIn);

        if (!this.integratedServerIsRunning)
        {
            AuthenticationService authenticationservice = new YggdrasilAuthenticationService(this.proxy);
            MinecraftSessionService minecraftsessionservice = authenticationservice.createMinecraftSessionService();
            GameProfileRepository gameprofilerepository = authenticationservice.createProfileRepository();
            PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.gameDir, MinecraftServer.USER_CACHE_FILE.getName()));
            SkullTileEntity.setProfileCache(playerprofilecache);
            SkullTileEntity.setSessionService(minecraftsessionservice);
            PlayerProfileCache.setOnlineMode(false);
        }
    }

    public void unloadWorld()
    {
        this.unloadWorld(new WorkingScreen());
    }

    public void unloadWorld(Screen screenIn)
    {
        ClientPlayNetHandler clientplaynethandler = this.getConnection();

        if (clientplaynethandler != null)
        {
            this.dropTasks();
            clientplaynethandler.cleanup();
        }

        IntegratedServer integratedserver = this.integratedServer;
        this.integratedServer = null;
        this.gameRenderer.resetData();
        this.playerController = null;
        NarratorChatListener.INSTANCE.clear();
        this.updateScreenTick(screenIn);

        if (this.world != null)
        {
            if (integratedserver != null)
            {
                this.profiler.startSection("waitForServer");

                while (!integratedserver.isThreadAlive())
                {
                    this.runGameLoop(false);
                }

                this.profiler.endSection();
            }

            this.packFinder.clearResourcePack();
            this.ingameGUI.resetPlayersOverlayFooterHeader();
            this.currentServerData = null;
            this.integratedServerIsRunning = false;
            this.game.leaveGameSession();
        }

        this.world = null;
        this.updateWorldRenderer((ClientWorld)null);
        this.player = null;
    }

    private void updateScreenTick(Screen screenIn)
    {
        this.profiler.startSection("forcedTick");
        this.soundHandler.stop();
        this.renderViewEntity = null;
        this.networkManager = null;
        this.displayGuiScreen(screenIn);
        this.runGameLoop(false);
        this.profiler.endSection();
    }

    public void forcedScreenTick(Screen screen)
    {
        this.profiler.startSection("forcedTick");
        this.displayGuiScreen(screen);
        this.runGameLoop(false);
        this.profiler.endSection();
    }

    private void updateWorldRenderer(@Nullable ClientWorld worldIn)
    {
        this.worldRenderer.setWorldAndLoadRenderers(worldIn);
        this.particles.clearEffects(worldIn);
        TileEntityRendererDispatcher.instance.setWorld(worldIn);
        this.setDefaultMinecraftTitle();
    }

    public boolean isMultiplayerEnabled()
    {
        return true;
    }

    public boolean cannotSendChatMessages(UUID playerUUID)
    {
        if (this.isChatEnabled())
        {
            return this.field_244597_aC.func_244756_c(playerUUID);
        }
        else
        {
            return (this.player == null || !playerUUID.equals(this.player.getUniqueID())) && !playerUUID.equals(Util.DUMMY_UUID);
        }
    }

    public boolean isChatEnabled()
    {
        return true;
    }

    /**
     * Gets whether this is a demo or not.
     */
    public final boolean isDemo()
    {
        return this.isDemo;
    }

    @Nullable
    public ClientPlayNetHandler getConnection()
    {
        return this.player == null ? null : this.player.connection;
    }

    public static boolean isGuiEnabled()
    {
        return !instance.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled()
    {
        return instance.gameSettings.graphicFanciness.func_238162_a_() >= GraphicsFanciness.FANCY.func_238162_a_();
    }

    public static boolean isFabulousGraphicsEnabled()
    {
        return instance.gameSettings.graphicFanciness.func_238162_a_() >= GraphicsFanciness.FABULOUS.func_238162_a_();
    }

    /**
     * Returns if ambient occlusion is enabled
     */
    public static boolean isAmbientOcclusionEnabled()
    {
        return instance.gameSettings.ambientOcclusionStatus != AmbientOcclusionStatus.OFF;
    }

    /**
     * Called when user clicked he's mouse middle button (pick block)
     */
    private void middleClickMouse()
    {
        EventManager.call(new EventMiddleMouse());

        if (this.objectMouseOver != null && this.objectMouseOver.getType() != RayTraceResult.Type.MISS)
        {
            boolean flag = this.player.abilities.isCreativeMode;
            TileEntity tileentity = null;
            RayTraceResult.Type raytraceresult$type = this.objectMouseOver.getType();
            ItemStack itemstack;

            if (raytraceresult$type == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = ((BlockRayTraceResult)this.objectMouseOver).getPos();
                BlockState blockstate = this.world.getBlockState(blockpos);
                Block block = blockstate.getBlock();

                if (blockstate.isAir())
                {
                    return;
                }

                itemstack = block.getItem(this.world, blockpos, blockstate);

                if (itemstack.isEmpty())
                {
                    return;
                }

                if (flag && Screen.hasControlDown() && block.isTileEntityProvider())
                {
                    tileentity = this.world.getTileEntity(blockpos);
                }
            }
            else
            {
                if (raytraceresult$type != RayTraceResult.Type.ENTITY || !flag)
                {
                    return;
                }

                Entity entity = ((EntityRayTraceResult)this.objectMouseOver).getEntity();

                if (entity instanceof PaintingEntity)
                {
                    itemstack = new ItemStack(Items.PAINTING);
                }
                else if (entity instanceof LeashKnotEntity)
                {
                    itemstack = new ItemStack(Items.LEAD);
                }
                else if (entity instanceof ItemFrameEntity)
                {
                    ItemFrameEntity itemframeentity = (ItemFrameEntity)entity;
                    ItemStack itemstack1 = itemframeentity.getDisplayedItem();

                    if (itemstack1.isEmpty())
                    {
                        itemstack = new ItemStack(Items.ITEM_FRAME);
                    }
                    else
                    {
                        itemstack = itemstack1.copy();
                    }
                }
                else if (entity instanceof AbstractMinecartEntity)
                {
                    AbstractMinecartEntity abstractminecartentity = (AbstractMinecartEntity)entity;
                    Item item;

                    switch (abstractminecartentity.getMinecartType())
                    {
                        case FURNACE:
                            item = Items.FURNACE_MINECART;
                            break;

                        case CHEST:
                            item = Items.CHEST_MINECART;
                            break;

                        case TNT:
                            item = Items.TNT_MINECART;
                            break;

                        case HOPPER:
                            item = Items.HOPPER_MINECART;
                            break;

                        case COMMAND_BLOCK:
                            item = Items.COMMAND_BLOCK_MINECART;
                            break;

                        default:
                            item = Items.MINECART;
                    }

                    itemstack = new ItemStack(item);
                }
                else if (entity instanceof BoatEntity)
                {
                    itemstack = new ItemStack(((BoatEntity)entity).getItemBoat());
                }
                else if (entity instanceof ArmorStandEntity)
                {
                    itemstack = new ItemStack(Items.ARMOR_STAND);
                }
                else if (entity instanceof EnderCrystalEntity)
                {
                    itemstack = new ItemStack(Items.END_CRYSTAL);
                }
                else
                {
                    SpawnEggItem spawneggitem = SpawnEggItem.getEgg(entity.getType());

                    if (spawneggitem == null)
                    {
                        return;
                    }

                    itemstack = new ItemStack(spawneggitem);
                }
            }

            if (itemstack.isEmpty())
            {
                String s = "";

                if (raytraceresult$type == RayTraceResult.Type.BLOCK)
                {
                    s = Registry.BLOCK.getKey(this.world.getBlockState(((BlockRayTraceResult)this.objectMouseOver).getPos()).getBlock()).toString();
                }
                else if (raytraceresult$type == RayTraceResult.Type.ENTITY)
                {
                    s = Registry.ENTITY_TYPE.getKey(((EntityRayTraceResult)this.objectMouseOver).getEntity().getType()).toString();
                }

                LOGGER.warn("Picking on: [{}] {} gave null item", raytraceresult$type, s);
            }
            else
            {
                PlayerInventory playerinventory = this.player.inventory;

                if (tileentity != null)
                {
                    this.storeTEInStack(itemstack, tileentity);
                }

                int i = playerinventory.getSlotFor(itemstack);

                if (flag)
                {
                    playerinventory.setPickedItemStack(itemstack);
                    this.playerController.sendSlotPacket(this.player.getHeldItem(Hand.MAIN_HAND), 36 + playerinventory.currentItem);
                }
                else if (i != -1)
                {
                    if (PlayerInventory.isHotbar(i))
                    {
                        playerinventory.currentItem = i;
                    }
                    else
                    {
                        this.playerController.pickItem(i);
                    }
                }
            }
        }
    }

    private ItemStack storeTEInStack(ItemStack stack, TileEntity te)
    {
        CompoundNBT compoundnbt = te.write(new CompoundNBT());

        if (stack.getItem() instanceof SkullItem && compoundnbt.contains("SkullOwner"))
        {
            CompoundNBT compoundnbt2 = compoundnbt.getCompound("SkullOwner");
            stack.getOrCreateTag().put("SkullOwner", compoundnbt2);
            return stack;
        }
        else
        {
            stack.setTagInfo("BlockEntityTag", compoundnbt);
            CompoundNBT compoundnbt1 = new CompoundNBT();
            ListNBT listnbt = new ListNBT();
            listnbt.add(StringNBT.valueOf("\"(+NBT)\""));
            compoundnbt1.put("Lore", listnbt);
            stack.setTagInfo("display", compoundnbt1);
            return stack;
        }
    }

    /**
     * adds core server Info (GL version , Texture pack, isModded, type), and the worldInfo to the crash report
     */
    public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash)
    {
        fillCrashReport(this.languageManager, this.launchedVersion, this.gameSettings, theCrash);

        if (this.world != null)
        {
            this.world.fillCrashReport(theCrash);
        }

        return theCrash;
    }

    public static void fillCrashReport(@Nullable LanguageManager languageManagerIn, String versionIn, @Nullable GameSettings settingsIn, CrashReport crashReportIn)
    {
        CrashReportCategory crashreportcategory = crashReportIn.getCategory();
        crashreportcategory.addDetail("Launched Version", () ->
        {
            return versionIn;
        });
        crashreportcategory.addDetail("Backend library", RenderSystem::getBackendDescription);
        crashreportcategory.addDetail("Backend API", RenderSystem::getApiDescription);
        crashreportcategory.addDetail("GL Caps", RenderSystem::getCapsString);
        crashreportcategory.addDetail("Using VBOs", () ->
        {
            return "Yes";
        });
        crashreportcategory.addDetail("Is Modded", () ->
        {
            String s1 = ClientBrandRetriever.getClientModName();

            if (!"vanilla".equals(s1))
            {
                return "Definitely; Client brand changed to '" + s1 + "'";
            }
            else {
                return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
            }
        });
        crashreportcategory.addDetail("Type", "Client (map_client.txt)");

        if (settingsIn != null)
        {
            if (instance != null)
            {
                String s = instance.getGPUWarning().func_243499_m();

                if (s != null)
                {
                    crashreportcategory.addDetail("GPU Warnings", s);
                }
            }

            crashreportcategory.addDetail("Graphics mode", settingsIn.graphicFanciness);
            crashreportcategory.addDetail("Resource Packs", () ->
            {
                StringBuilder stringbuilder = new StringBuilder();

                for (String s1 : settingsIn.resourcePacks)
                {
                    if (stringbuilder.length() > 0)
                    {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(s1);

                    if (settingsIn.incompatibleResourcePacks.contains(s1))
                    {
                        stringbuilder.append(" (incompatible)");
                    }
                }

                return stringbuilder.toString();
            });
        }

        if (languageManagerIn != null)
        {
            crashreportcategory.addDetail("Current Language", () ->
            {
                return languageManagerIn.getCurrentLanguage().toString();
            });
        }

        crashreportcategory.addDetail("CPU", PlatformDescriptors::getCpuInfo);
    }

    /**
     * Return the singleton Minecraft instance for the game
     */
    public static Minecraft getInstance()
    {
        return instance;
    }

    public CompletableFuture<Void> scheduleResourcesRefresh()
    {
        return this.supplyAsync(this::reloadResources).thenCompose((voidIn) ->
        {
            return voidIn;
        });
    }

    public void fillSnooper(Snooper snooper)
    {
        snooper.addClientStat("fps", debugFPS);
        snooper.addClientStat("vsync_enabled", this.gameSettings.vsync);
        snooper.addClientStat("display_frequency", this.mainWindow.getRefreshRate());
        snooper.addClientStat("display_type", this.mainWindow.isFullscreen() ? "fullscreen" : "windowed");
        snooper.addClientStat("run_time", (Util.milliTime() - snooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
        snooper.addClientStat("current_action", this.getCurrentAction());
        snooper.addClientStat("language", this.gameSettings.language == null ? "en_us" : this.gameSettings.language);
        String s = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
        snooper.addClientStat("endianness", s);
        snooper.addClientStat("subtitles", this.gameSettings.showSubtitles);
        snooper.addClientStat("touch", this.gameSettings.touchscreen ? "touch" : "mouse");
        int i = 0;

        for (ResourcePackInfo resourcepackinfo : this.resourcePackRepository.getEnabledPacks())
        {
            if (!resourcepackinfo.isAlwaysEnabled() && !resourcepackinfo.isOrderLocked())
            {
                snooper.addClientStat("resource_pack[" + i++ + "]", resourcepackinfo.getName());
            }
        }

        snooper.addClientStat("resource_packs", i);

        if (this.integratedServer != null)
        {
            snooper.addClientStat("snooper_partner", this.integratedServer.getSnooper().getUniqueID());
        }
    }

    /**
     * Return the current action's name
     */
    private String getCurrentAction()
    {
        if (this.integratedServer != null)
        {
            return this.integratedServer.getPublic() ? "hosting_lan" : "singleplayer";
        }
        else if (this.currentServerData != null)
        {
            return this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer";
        }
        else
        {
            return "out_of_game";
        }
    }

    /**
     * Set the current ServerData instance.
     */
    public void setServerData(@Nullable ServerData serverDataIn)
    {
        this.currentServerData = serverDataIn;
    }

    @Nullable
    public ServerData getCurrentServerData()
    {
        return this.currentServerData;
    }

    public boolean isIntegratedServerRunning()
    {
        return this.integratedServerIsRunning;
    }

    /**
     * Returns true if there is only one player playing, and the current server is the integrated one.
     */
    public boolean isSingleplayer()
    {
        return this.integratedServerIsRunning && this.integratedServer != null;
    }

    @Nullable

    /**
     * Returns the currently running integrated server
     */
    public IntegratedServer getIntegratedServer()
    {
        return this.integratedServer;
    }

    /**
     * Returns the PlayerUsageSnooper instance.
     */
    public Snooper getSnooper()
    {
        return this.snooper;
    }

    public Session getSession()
    {
        return this.session;
    }

    /**
     * Return the player's GameProfile properties
     */
    public PropertyMap getProfileProperties()
    {
        if (this.profileProperties.isEmpty())
        {
            GameProfile gameprofile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
            this.profileProperties.putAll(gameprofile.getProperties());
        }

        return this.profileProperties;
    }

    public Proxy getProxy()
    {
        return this.proxy;
    }

    public TextureManager getTextureManager()
    {
        return this.textureManager;
    }

    public IResourceManager getResourceManager()
    {
        return this.resourceManager;
    }

    public ResourcePackList getResourcePackList()
    {
        return this.resourcePackRepository;
    }

    public DownloadingPackFinder getPackFinder()
    {
        return this.packFinder;
    }

    public File getFileResourcePacks()
    {
        return this.fileResourcepacks;
    }

    public LanguageManager getLanguageManager()
    {
        return this.languageManager;
    }

    public Function<ResourceLocation, TextureAtlasSprite> getAtlasSpriteGetter(ResourceLocation locationIn)
    {
        return this.modelManager.getAtlasTexture(locationIn)::getSprite;
    }

    public boolean isJava64bit()
    {
        return this.jvm64bit;
    }

    public boolean isGamePaused()
    {
        return this.isGamePaused;
    }

    public GPUWarning getGPUWarning()
    {
        return this.warningGPU;
    }

    public SoundHandler getSoundHandler()
    {
        return this.soundHandler;
    }

    public BackgroundMusicSelector getBackgroundMusicSelector()
    {
        if (this.currentScreen instanceof WinGameScreen)
        {
            return BackgroundMusicTracks.CREDITS_MUSIC;
        }
        else if (this.player != null)
        {
            if (this.player.world.getDimensionKey() == World.THE_END)
            {
                return this.ingameGUI.getBossOverlay().shouldPlayEndBossMusic() ? BackgroundMusicTracks.DRAGON_FIGHT_MUSIC : BackgroundMusicTracks.END_MUSIC;
            }
            else
            {
                Biome.Category biome$category = this.player.world.getBiome(this.player.getPosition()).getCategory();

                if (!this.musicTicker.isBackgroundMusicPlaying(BackgroundMusicTracks.UNDER_WATER_MUSIC) && (!this.player.canSwim() || biome$category != Biome.Category.OCEAN && biome$category != Biome.Category.RIVER))
                {
                    return this.player.world.getDimensionKey() != World.THE_NETHER && this.player.abilities.isCreativeMode && this.player.abilities.allowFlying ? BackgroundMusicTracks.CREATIVE_MODE_MUSIC : this.world.getBiomeManager().getBiomeAtPosition(this.player.getPosition()).getBackgroundMusic().orElse(BackgroundMusicTracks.WORLD_MUSIC);
                }
                else
                {
                    return BackgroundMusicTracks.UNDER_WATER_MUSIC;
                }
            }
        }
        else
        {
            return BackgroundMusicTracks.MAIN_MENU_MUSIC;
        }
    }

    public MinecraftSessionService getSessionService()
    {
        return this.sessionService;
    }

    public SkinManager getSkinManager()
    {
        return this.skinManager;
    }

    @Nullable
    public Entity getRenderViewEntity()
    {
        return this.renderViewEntity;
    }

    public void setRenderViewEntity(Entity viewingEntity)
    {
        this.renderViewEntity = viewingEntity;
        this.gameRenderer.loadEntityShader(viewingEntity);
    }

    public boolean isEntityGlowing(Entity entity)
    {
        return entity.isGlowing() || this.player != null && this.player.isSpectator() && this.gameSettings.keyBindSpectatorOutlines.isKeyDown() && entity.getType() == EntityType.PLAYER;
    }

    protected Thread getExecutionThread()
    {
        return this.thread;
    }

    protected Runnable wrapTask(Runnable runnable)
    {
        return runnable;
    }

    protected boolean canRun(Runnable runnable)
    {
        return true;
    }

    public BlockRendererDispatcher getBlockRendererDispatcher()
    {
        return this.blockRenderDispatcher;
    }

    public EntityRendererManager getRenderManager()
    {
        return this.renderManager;
    }

    public ItemRenderer getItemRenderer()
    {
        return this.itemRenderer;
    }

    public FirstPersonRenderer getFirstPersonRenderer()
    {
        return this.firstPersonRenderer;
    }

    public <T> IMutableSearchTree<T> getSearchTree(SearchTreeManager.Key<T> key)
    {
        return this.searchTreeManager.get(key);
    }

    /**
     * Return the FrameTimer's instance
     */
    public FrameTimer getFrameTimer()
    {
        return this.frameTimer;
    }

    /**
     * Return true if the player is connected to a realms server
     */
    public boolean isConnectedToRealms()
    {
        return this.connectedToRealms;
    }

    /**
     * Set if the player is connected to a realms server
     */
    public void setConnectedToRealms(boolean isConnected)
    {
        this.connectedToRealms = isConnected;
    }

    public DataFixer getDataFixer()
    {
        return this.dataFixer;
    }

    public float getRenderPartialTicks()
    {
        return this.timer.renderPartialTicks;
    }

    public float getTickLength()
    {
        return this.timer.elapsedPartialTicks;
    }

    public BlockColors getBlockColors()
    {
        return this.blockColors;
    }

    /**
     * Whether to use reduced debug info
     */
    public boolean isReducedDebug()
    {
        return this.player != null && this.player.hasReducedDebug() || this.gameSettings.reducedDebugInfo;
    }

    public ToastGui getToastGui()
    {
        return this.toastGui;
    }

    public Tutorial getTutorial()
    {
        return this.tutorial;
    }

    public boolean isGameFocused()
    {
        return this.isWindowFocused;
    }

    public CreativeSettings getCreativeSettings()
    {
        return this.creativeSettings;
    }

    public ModelManager getModelManager()
    {
        return this.modelManager;
    }

    /**
     * Gets the sprite uploader used for paintings.
     */
    public PaintingSpriteUploader getPaintingSpriteUploader()
    {
        return this.paintingSprites;
    }

    /**
     * Gets the sprite uploader used for potions.
     */
    public PotionSpriteUploader getPotionSpriteUploader()
    {
        return this.potionSprites;
    }

    public void setGameFocused(boolean focused)
    {
        this.isWindowFocused = focused;
    }

    public IProfiler getProfiler()
    {
        return this.profiler;
    }

    public MinecraftGame getMinecraftGame()
    {
        return this.game;
    }

    public Splashes getSplashes()
    {
        return this.splashes;
    }

    @Nullable
    public LoadingGui getLoadingGui()
    {
        return this.loadingGui;
    }

    public FilterManager func_244599_aA()
    {
        return this.field_244597_aC;
    }

    public boolean isRenderOnThread()
    {
        return false;
    }

    public MainWindow getMainWindow()
    {
        return this.mainWindow;
    }

    public RenderTypeBuffers getRenderTypeBuffers()
    {
        return this.renderTypeBuffers;
    }

    private static ResourcePackInfo makePackInfo(String name, boolean isAlwaysEnabled, Supplier<IResourcePack> resourceSupplier, IResourcePack resourcePack, PackMetadataSection resourcePackMeta, ResourcePackInfo.Priority priority, IPackNameDecorator decorator)
    {
        int i = resourcePackMeta.getPackFormat();
        Supplier<IResourcePack> supplier = resourceSupplier;

        if (i <= 3)
        {
            supplier = wrapV3(resourceSupplier);
        }

        if (i <= 4)
        {
            supplier = wrapV4(supplier);
        }

        return new ResourcePackInfo(name, isAlwaysEnabled, supplier, resourcePack, resourcePackMeta, priority, decorator);
    }

    private static Supplier<IResourcePack> wrapV3(Supplier<IResourcePack> resourcePackSupplier)
    {
        return () ->
        {
            return new LegacyResourcePackWrapper(resourcePackSupplier.get(), LegacyResourcePackWrapper.NEW_TO_LEGACY_MAP);
        };
    }

    private static Supplier<IResourcePack> wrapV4(Supplier<IResourcePack> resourcePackSupplier)
    {
        return () ->
        {
            return new LegacyResourcePackWrapperV4(resourcePackSupplier.get());
        };
    }

    public void setMipmapLevels(int mipMapLevel)
    {
        this.modelManager.setMaxMipmapLevel(mipMapLevel);
    }

    public static final class PackManager implements AutoCloseable
    {
        private final ResourcePackList resourcePackList;
        private final DataPackRegistries datapackRegistries;
        private final IServerConfiguration serverConfiguration;

        private PackManager(ResourcePackList resourcePackList, DataPackRegistries datapackRegistries, IServerConfiguration serverConfiguration)
        {
            this.resourcePackList = resourcePackList;
            this.datapackRegistries = datapackRegistries;
            this.serverConfiguration = serverConfiguration;
        }

        public ResourcePackList getResourcePacks()
        {
            return this.resourcePackList;
        }

        public DataPackRegistries getDataPackRegistries()
        {
            return this.datapackRegistries;
        }

        public IServerConfiguration getServerConfiguration()
        {
            return this.serverConfiguration;
        }

        public void close()
        {
            this.resourcePackList.close();
            this.datapackRegistries.close();
        }
    }

    static enum WorldSelectionType
    {
        NONE,
        CREATE,
        BACKUP;
    }
}
