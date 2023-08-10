package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.NarratorStatus;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.client.settings.ToggleableKeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.HandSide;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Difficulty;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.CustomGuis;
import net.optifine.CustomSky;
import net.optifine.DynamicLights;
import net.optifine.Lang;
import net.optifine.NaturalTextures;
import net.optifine.RandomEntities;
import net.optifine.config.FloatOptions;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import net.optifine.util.FontUtils;
import net.optifine.util.KeyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameSettings
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final TypeToken<List<String>> TYPE_LIST_STRING = new TypeToken<List<String>>()
    {
    };
    private static final Splitter KEY_VALUE_SPLITTER = Splitter.on(':').limit(2);
    public double mouseSensitivity = 0.5D;
    public int renderDistanceChunks = -1;
    public float entityDistanceScaling = 1.0F;
    public int framerateLimit = 120;
    public CloudOption cloudOption = CloudOption.FANCY;
    public GraphicsFanciness graphicFanciness = GraphicsFanciness.FANCY;
    public AmbientOcclusionStatus ambientOcclusionStatus = AmbientOcclusionStatus.MAX;
    public List<String> resourcePacks = Lists.newArrayList();
    public List<String> incompatibleResourcePacks = Lists.newArrayList();
    public ChatVisibility chatVisibility = ChatVisibility.FULL;
    public double chatOpacity = 1.0D;
    public double chatLineSpacing = 0.0D;
    public double accessibilityTextBackgroundOpacity = 0.5D;
    @Nullable
    public String fullscreenResolution;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus = true;
    private final Set<PlayerModelPart> setModelParts = Sets.newHashSet(PlayerModelPart.values());
    public HandSide mainHand = HandSide.RIGHT;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips = true;
    public double chatScale = 1.0D;
    public double chatWidth = 1.0D;
    public double chatHeightUnfocused = (double)0.44366196F;
    public double chatHeightFocused = 1.0D;
    public double chatDelay = 0.0D;
    public int mipmapLevels = 4;
    private final Map<SoundCategory, Float> soundLevels = Maps.newEnumMap(SoundCategory.class);
    public boolean useNativeTransport = true;
    public AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.CROSSHAIR;
    public TutorialSteps tutorialStep = TutorialSteps.MOVEMENT;
    public boolean field_244601_E = false;
    public int biomeBlendRadius = 2;
    public double mouseWheelSensitivity = 1.0D;
    public boolean rawMouseInput = true;
    public int glDebugVerbosity = 1;
    public boolean autoJump = true;
    public boolean autoSuggestCommands = true;
    public boolean chatColor = true;
    public boolean chatLinks = true;
    public boolean chatLinksPrompt = true;
    public boolean vsync = true;
    public boolean entityShadows = true;
    public boolean forceUnicodeFont;
    public boolean invertMouse;
    public boolean discreteMouseScroll;
    public boolean realmsNotifications = true;
    public boolean reducedDebugInfo;
    public boolean snooper = true;
    public boolean showSubtitles;
    public boolean accessibilityTextBackground = true;
    public boolean touchscreen;
    public boolean fullscreen;
    public boolean viewBobbing = true;
    public boolean toggleCrouch;
    public boolean toggleSprint;
    public boolean skipMultiplayerWarning;
    public boolean field_244794_ae = true;
    public final KeyBinding keyBindForward = new KeyBinding("key.forward", 87, "key.categories.movement");
    public final KeyBinding keyBindLeft = new KeyBinding("key.left", 65, "key.categories.movement");
    public final KeyBinding keyBindBack = new KeyBinding("key.back", 83, "key.categories.movement");
    public final KeyBinding keyBindRight = new KeyBinding("key.right", 68, "key.categories.movement");
    public final KeyBinding keyBindJump = new KeyBinding("key.jump", 32, "key.categories.movement");
    public final KeyBinding keyBindSneak = new ToggleableKeyBinding("key.sneak", 340, "key.categories.movement", () ->
    {
        return this.toggleCrouch;
    });
    public final KeyBinding keyBindSprint = new ToggleableKeyBinding("key.sprint", 341, "key.categories.movement", () ->
    {
        return this.toggleSprint;
    });
    public final KeyBinding keyBindInventory = new KeyBinding("key.inventory", 69, "key.categories.inventory");
    public final KeyBinding keyBindSwapHands = new KeyBinding("key.swapOffhand", 70, "key.categories.inventory");
    public final KeyBinding keyBindDrop = new KeyBinding("key.drop", 81, "key.categories.inventory");
    public final KeyBinding keyBindUseItem = new KeyBinding("key.use", InputMappings.Type.MOUSE, 1, "key.categories.gameplay");
    public final KeyBinding keyBindAttack = new KeyBinding("key.attack", InputMappings.Type.MOUSE, 0, "key.categories.gameplay");
    public final KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", InputMappings.Type.MOUSE, 2, "key.categories.gameplay");
    public final KeyBinding keyBindChat = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
    public final KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
    public final KeyBinding keyBindCommand = new KeyBinding("key.command", 47, "key.categories.multiplayer");
    public final KeyBinding field_244602_au = new KeyBinding("key.socialInteractions", 80, "key.categories.multiplayer");
    public final KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 291, "key.categories.misc");
    public final KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
    public final KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", InputMappings.INPUT_INVALID.getKeyCode(), "key.categories.misc");
    public final KeyBinding keyBindFullscreen = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
    public final KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", InputMappings.INPUT_INVALID.getKeyCode(), "key.categories.misc");
    public final KeyBinding keyBindAdvancements = new KeyBinding("key.advancements", 76, "key.categories.misc");
    public final KeyBinding[] keyBindsHotbar = new KeyBinding[] {new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 57, "key.categories.inventory")};
    public final KeyBinding keyBindSaveToolbar = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
    public final KeyBinding keyBindLoadToolbar = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
    public KeyBinding[] keyBindings = ArrayUtils.addAll(new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.field_244602_au, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindFullscreen, this.keyBindSpectatorOutlines, this.keyBindSwapHands, this.keyBindSaveToolbar, this.keyBindLoadToolbar, this.keyBindAdvancements}, this.keyBindsHotbar);
    protected Minecraft mc;
    private final File optionsFile;
    public Difficulty difficulty = Difficulty.NORMAL;
    public boolean hideGUI;
    private PointOfView pointOfView = PointOfView.FIRST_PERSON;
    public boolean showDebugInfo;
    public boolean showDebugProfilerChart;
    public boolean showLagometer;
    public String lastServer = "";
    public boolean smoothCamera;
    public double fov = 70.0D;
    public float screenEffectScale = 1.0F;
    public float fovScaleEffect = 1.0F;
    public double gamma;
    public int guiScale;
    public ParticleStatus particles = ParticleStatus.ALL;
    public NarratorStatus narrator = NarratorStatus.OFF;
    public String language = "en_us";
    public boolean syncChunkWrites;
    public int ofFogType = 1;
    public float ofFogStart = 0.8F;
    public int ofMipmapType = 0;
    public boolean ofOcclusionFancy = false;
    public boolean ofSmoothFps = false;
    public boolean ofSmoothWorld = Config.isSingleProcessor();
    public boolean ofLazyChunkLoading = Config.isSingleProcessor();
    public boolean ofRenderRegions = false;
    public boolean ofSmartAnimations = false;
    public double ofAoLevel = 1.0D;
    public int ofAaLevel = 0;
    public int ofAfLevel = 1;
    public int ofClouds = 0;
    public double ofCloudsHeight = 0.0D;
    public int ofTrees = 0;
    public int ofRain = 0;
    public int ofDroppedItems = 0;
    public int ofBetterGrass = 3;
    public int ofAutoSaveTicks = 4000;
    public boolean ofLagometer = false;
    public boolean ofProfiler = false;
    public boolean ofShowFps = false;
    public boolean ofWeather = true;
    public boolean ofSky = true;
    public boolean ofStars = true;
    public boolean ofSunMoon = true;
    public int ofVignette = 0;
    public int ofChunkUpdates = 1;
    public boolean ofChunkUpdatesDynamic = false;
    public int ofTime = 0;
    public boolean ofBetterSnow = false;
    public boolean ofSwampColors = true;
    public boolean ofRandomEntities = true;
    public boolean ofCustomFonts = true;
    public boolean ofCustomColors = true;
    public boolean ofCustomSky = true;
    public boolean ofShowCapes = true;
    public int ofConnectedTextures = 2;
    public boolean ofCustomItems = true;
    public boolean ofNaturalTextures = false;
    public boolean ofEmissiveTextures = true;
    public boolean ofFastMath = false;
    public boolean ofFastRender = false;
    public int ofTranslucentBlocks = 0;
    public boolean ofDynamicFov = true;
    public boolean ofAlternateBlocks = true;
    public int ofDynamicLights = 3;
    public boolean ofCustomEntityModels = true;
    public boolean ofCustomGuis = true;
    public boolean ofShowGlErrors = true;
    public int ofScreenshotSize = 1;
    public int ofChatBackground = 0;
    public boolean ofChatShadow = true;
    public int ofAnimatedWater = 0;
    public int ofAnimatedLava = 0;
    public boolean ofAnimatedFire = true;
    public boolean ofAnimatedPortal = true;
    public boolean ofAnimatedRedstone = true;
    public boolean ofAnimatedExplosion = true;
    public boolean ofAnimatedFlame = true;
    public boolean ofAnimatedSmoke = true;
    public boolean ofVoidParticles = true;
    public boolean ofWaterParticles = true;
    public boolean ofRainSplash = true;
    public boolean ofPortalParticles = true;
    public boolean ofPotionParticles = true;
    public boolean ofFireworkParticles = true;
    public boolean ofDrippingWaterLava = true;
    public boolean ofAnimatedTerrain = true;
    public boolean ofAnimatedTextures = true;
    public static final int DEFAULT = 0;
    public static final int FAST = 1;
    public static final int FANCY = 2;
    public static final int OFF = 3;
    public static final int SMART = 4;
    public static final int COMPACT = 5;
    public static final int ANIM_ON = 0;
    public static final int ANIM_GENERATED = 1;
    public static final int ANIM_OFF = 2;
    public static final String DEFAULT_STR = "Default";
    public static final double CHAT_WIDTH_SCALE = 4.0571431D;
    private static final int[] OF_TREES_VALUES = new int[] {0, 1, 4, 2};
    private static final int[] OF_DYNAMIC_LIGHTS = new int[] {3, 1, 2};
    private static final String[] KEYS_DYNAMIC_LIGHTS = new String[] {"options.off", "options.graphics.fast", "options.graphics.fancy"};
    public KeyBinding ofKeyBindZoom;
    private File optionsFileOF;

    public GameSettings(Minecraft mcIn, File mcDataDir)
    {
        this.setForgeKeybindProperties();
        this.mc = mcIn;
        this.optionsFile = new File(mcDataDir, "options.txt");

        if (mcIn.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L)
        {
            AbstractOption.RENDER_DISTANCE.setMaxValue(32.0F);
            long i = 1000000L;

            if (Runtime.getRuntime().maxMemory() >= 1500L * i)
            {
                AbstractOption.RENDER_DISTANCE.setMaxValue(48.0F);
            }

            if (Runtime.getRuntime().maxMemory() >= 2500L * i)
            {
                AbstractOption.RENDER_DISTANCE.setMaxValue(64.0F);
            }
        }
        else
        {
            AbstractOption.RENDER_DISTANCE.setMaxValue(16.0F);
        }

        this.renderDistanceChunks = mcIn.isJava64bit() ? 12 : 8;
        this.syncChunkWrites = Util.getOSType() == Util.OS.WINDOWS;
        this.optionsFileOF = new File(mcDataDir, "optionsof.txt");
        this.framerateLimit = (int)AbstractOption.FRAMERATE_LIMIT.getMaxValue();
        this.ofKeyBindZoom = new KeyBinding("of.key.zoom", 67, "key.categories.misc");
        this.keyBindings = ArrayUtils.add(this.keyBindings, this.ofKeyBindZoom);
        KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[] {this.ofKeyBindZoom});
        this.renderDistanceChunks = 8;
        this.loadOptions();
        Config.initGameSettings(this);
    }

    public float getTextBackgroundOpacity(float opacity)
    {
        return this.accessibilityTextBackground ? opacity : (float)this.accessibilityTextBackgroundOpacity;
    }

    public int getTextBackgroundColor(float opacity)
    {
        return (int)(this.getTextBackgroundOpacity(opacity) * 255.0F) << 24 & -16777216;
    }

    public int getChatBackgroundColor(int chatColor)
    {
        return this.accessibilityTextBackground ? chatColor : (int)(this.accessibilityTextBackgroundOpacity * 255.0D) << 24 & -16777216;
    }

    public void setKeyBindingCode(KeyBinding keyBindingIn, InputMappings.Input inputIn)
    {
        keyBindingIn.bind(inputIn);
        this.saveOptions();
    }

    /**
     * Loads the options from the options file. It appears that this has replaced the previous 'loadOptions'
     */
    public void loadOptions()
    {
        try
        {
            if (!this.optionsFile.exists())
            {
                return;
            }

            this.soundLevels.clear();
            CompoundNBT compoundnbt = new CompoundNBT();

            try (BufferedReader bufferedreader = Files.newReader(this.optionsFile, Charsets.UTF_8))
            {
                bufferedreader.lines().forEach((p_lambda$loadOptions$2_1_) ->
                {
                    try {
                        Iterator<String> iterator = KEY_VALUE_SPLITTER.split(p_lambda$loadOptions$2_1_).iterator();
                        compoundnbt.putString(iterator.next(), iterator.next());
                    }
                    catch (Exception exception21)
                    {
                        LOGGER.warn("Skipping bad option: {}", (Object)p_lambda$loadOptions$2_1_);
                    }
                });
            }

            CompoundNBT compoundnbt1 = this.dataFix(compoundnbt);

            if (!compoundnbt1.contains("graphicsMode") && compoundnbt1.contains("fancyGraphics"))
            {
                if ("true".equals(compoundnbt1.getString("fancyGraphics")))
                {
                    this.graphicFanciness = GraphicsFanciness.FANCY;
                }
                else
                {
                    this.graphicFanciness = GraphicsFanciness.FAST;
                }
            }

            for (String s : compoundnbt1.keySet())
            {
                String s1 = compoundnbt1.getString(s);

                try
                {
                    if ("autoJump".equals(s))
                    {
                        AbstractOption.AUTO_JUMP.set(this, s1);
                    }

                    if ("autoSuggestions".equals(s))
                    {
                        AbstractOption.AUTO_SUGGEST_COMMANDS.set(this, s1);
                    }

                    if ("chatColors".equals(s))
                    {
                        AbstractOption.CHAT_COLOR.set(this, s1);
                    }

                    if ("chatLinks".equals(s))
                    {
                        AbstractOption.CHAT_LINKS.set(this, s1);
                    }

                    if ("chatLinksPrompt".equals(s))
                    {
                        AbstractOption.CHAT_LINKS_PROMPT.set(this, s1);
                    }

                    if ("enableVsync".equals(s))
                    {
                        AbstractOption.VSYNC.set(this, s1);

                        if (this.vsync)
                        {
                            this.framerateLimit = (int)AbstractOption.FRAMERATE_LIMIT.getMaxValue();
                        }

                        this.updateVSync();
                    }

                    if ("entityShadows".equals(s))
                    {
                        AbstractOption.ENTITY_SHADOWS.set(this, s1);
                    }

                    if ("forceUnicodeFont".equals(s))
                    {
                        AbstractOption.FORCE_UNICODE_FONT.set(this, s1);
                    }

                    if ("discrete_mouse_scroll".equals(s))
                    {
                        AbstractOption.DISCRETE_MOUSE_SCROLL.set(this, s1);
                    }

                    if ("invertYMouse".equals(s))
                    {
                        AbstractOption.INVERT_MOUSE.set(this, s1);
                    }

                    if ("realmsNotifications".equals(s))
                    {
                        AbstractOption.REALMS_NOTIFICATIONS.set(this, s1);
                    }

                    if ("reducedDebugInfo".equals(s))
                    {
                        AbstractOption.REDUCED_DEBUG_INFO.set(this, s1);
                    }

                    if ("showSubtitles".equals(s))
                    {
                        AbstractOption.SHOW_SUBTITLES.set(this, s1);
                    }

                    if ("snooperEnabled".equals(s))
                    {
                        AbstractOption.SNOOPER.set(this, s1);
                    }

                    if ("touchscreen".equals(s))
                    {
                        AbstractOption.TOUCHSCREEN.set(this, s1);
                    }

                    if ("fullscreen".equals(s))
                    {
                        AbstractOption.FULLSCREEN.set(this, s1);
                    }

                    if ("bobView".equals(s))
                    {
                        AbstractOption.VIEW_BOBBING.set(this, s1);
                    }

                    if ("toggleCrouch".equals(s))
                    {
                        this.toggleCrouch = "true".equals(s1);
                    }

                    if ("toggleSprint".equals(s))
                    {
                        this.toggleSprint = "true".equals(s1);
                    }

                    if ("mouseSensitivity".equals(s))
                    {
                        this.mouseSensitivity = (double)parseFloat(s1);
                    }

                    if ("fov".equals(s))
                    {
                        this.fov = (double)(parseFloat(s1) * 40.0F + 70.0F);
                    }

                    if ("screenEffectScale".equals(s))
                    {
                        this.screenEffectScale = parseFloat(s1);
                    }

                    if ("fovEffectScale".equals(s))
                    {
                        this.fovScaleEffect = parseFloat(s1);
                    }

                    if ("gamma".equals(s))
                    {
                        this.gamma = (double)parseFloat(s1);
                    }

                    if ("renderDistance".equals(s))
                    {
                        this.renderDistanceChunks = Integer.parseInt(s1);
                    }

                    if ("entityDistanceScaling".equals(s))
                    {
                        this.entityDistanceScaling = Float.parseFloat(s1);
                    }

                    if ("guiScale".equals(s))
                    {
                        this.guiScale = Integer.parseInt(s1);
                    }

                    if ("particles".equals(s))
                    {
                        this.particles = ParticleStatus.byId(Integer.parseInt(s1));
                    }

                    if ("maxFps".equals(s))
                    {
                        this.framerateLimit = Integer.parseInt(s1);

                        if (this.vsync)
                        {
                            this.framerateLimit = (int)AbstractOption.FRAMERATE_LIMIT.getMaxValue();
                        }

                        if (this.framerateLimit <= 0)
                        {
                            this.framerateLimit = (int)AbstractOption.FRAMERATE_LIMIT.getMaxValue();
                        }

                        if (this.mc.getMainWindow() != null)
                        {
                            this.mc.getMainWindow().setFramerateLimit(this.framerateLimit);
                        }
                    }

                    if ("difficulty".equals(s))
                    {
                        this.difficulty = Difficulty.byId(Integer.parseInt(s1));
                    }

                    if ("graphicsMode".equals(s))
                    {
                        this.graphicFanciness = GraphicsFanciness.func_238163_a_(Integer.parseInt(s1));
                        this.updateRenderClouds();
                    }

                    if ("tutorialStep".equals(s))
                    {
                        this.tutorialStep = TutorialSteps.byName(s1);
                    }

                    if ("ao".equals(s))
                    {
                        if ("true".equals(s1))
                        {
                            this.ambientOcclusionStatus = AmbientOcclusionStatus.MAX;
                        }
                        else if ("false".equals(s1))
                        {
                            this.ambientOcclusionStatus = AmbientOcclusionStatus.OFF;
                        }
                        else
                        {
                            this.ambientOcclusionStatus = AmbientOcclusionStatus.getValue(Integer.parseInt(s1));
                        }
                    }

                    if ("renderClouds".equals(s))
                    {
                        if ("true".equals(s1))
                        {
                            this.cloudOption = CloudOption.FANCY;
                        }
                        else if ("false".equals(s1))
                        {
                            this.cloudOption = CloudOption.OFF;
                        }
                        else if ("fast".equals(s1))
                        {
                            this.cloudOption = CloudOption.FAST;
                        }
                    }

                    if ("attackIndicator".equals(s))
                    {
                        this.attackIndicator = AttackIndicatorStatus.byId(Integer.parseInt(s1));
                    }

                    if ("resourcePacks".equals(s))
                    {
                        this.resourcePacks = JSONUtils.fromJSONUnlenient(GSON, s1, TYPE_LIST_STRING);

                        if (this.resourcePacks == null)
                        {
                            this.resourcePacks = Lists.newArrayList();
                        }
                    }

                    if ("incompatibleResourcePacks".equals(s))
                    {
                        this.incompatibleResourcePacks = JSONUtils.fromJSONUnlenient(GSON, s1, TYPE_LIST_STRING);

                        if (this.incompatibleResourcePacks == null)
                        {
                            this.incompatibleResourcePacks = Lists.newArrayList();
                        }
                    }

                    if ("lastServer".equals(s))
                    {
                        this.lastServer = s1;
                    }

                    if ("lang".equals(s))
                    {
                        this.language = s1;
                    }

                    if ("chatVisibility".equals(s))
                    {
                        this.chatVisibility = ChatVisibility.getValue(Integer.parseInt(s1));
                    }

                    if ("chatOpacity".equals(s))
                    {
                        this.chatOpacity = (double)parseFloat(s1);
                    }

                    if ("chatLineSpacing".equals(s))
                    {
                        this.chatLineSpacing = (double)parseFloat(s1);
                    }

                    if ("textBackgroundOpacity".equals(s))
                    {
                        this.accessibilityTextBackgroundOpacity = (double)parseFloat(s1);
                    }

                    if ("backgroundForChatOnly".equals(s))
                    {
                        this.accessibilityTextBackground = "true".equals(s1);
                    }

                    if ("fullscreenResolution".equals(s))
                    {
                        this.fullscreenResolution = s1;
                    }

                    if ("hideServerAddress".equals(s))
                    {
                        this.hideServerAddress = "true".equals(s1);
                    }

                    if ("advancedItemTooltips".equals(s))
                    {
                        this.advancedItemTooltips = "true".equals(s1);
                    }

                    if ("pauseOnLostFocus".equals(s))
                    {
                        this.pauseOnLostFocus = "true".equals(s1);
                    }

                    if ("overrideHeight".equals(s))
                    {
                        this.overrideHeight = Integer.parseInt(s1);
                    }

                    if ("overrideWidth".equals(s))
                    {
                        this.overrideWidth = Integer.parseInt(s1);
                    }

                    if ("heldItemTooltips".equals(s))
                    {
                        this.heldItemTooltips = "true".equals(s1);
                    }

                    if ("chatHeightFocused".equals(s))
                    {
                        this.chatHeightFocused = (double)parseFloat(s1);
                    }

                    if ("chatDelay".equals(s))
                    {
                        this.chatDelay = (double)parseFloat(s1);
                    }

                    if ("chatHeightUnfocused".equals(s))
                    {
                        this.chatHeightUnfocused = (double)parseFloat(s1);
                    }

                    if ("chatScale".equals(s))
                    {
                        this.chatScale = (double)parseFloat(s1);
                    }

                    if ("chatWidth".equals(s))
                    {
                        this.chatWidth = (double)parseFloat(s1);
                    }

                    if ("mipmapLevels".equals(s))
                    {
                        this.mipmapLevels = Integer.parseInt(s1);
                    }

                    if ("useNativeTransport".equals(s))
                    {
                        this.useNativeTransport = "true".equals(s1);
                    }

                    if ("mainHand".equals(s))
                    {
                        this.mainHand = "left".equals(s1) ? HandSide.LEFT : HandSide.RIGHT;
                    }

                    if ("narrator".equals(s))
                    {
                        this.narrator = NarratorStatus.byId(Integer.parseInt(s1));
                    }

                    if ("biomeBlendRadius".equals(s))
                    {
                        this.biomeBlendRadius = Integer.parseInt(s1);
                    }

                    if ("mouseWheelSensitivity".equals(s))
                    {
                        this.mouseWheelSensitivity = (double)parseFloat(s1);
                    }

                    if ("rawMouseInput".equals(s))
                    {
                        this.rawMouseInput = "true".equals(s1);
                    }

                    if ("glDebugVerbosity".equals(s))
                    {
                        this.glDebugVerbosity = Integer.parseInt(s1);
                    }

                    if ("skipMultiplayerWarning".equals(s))
                    {
                        this.skipMultiplayerWarning = "true".equals(s1);
                    }

                    if ("hideMatchedNames".equals(s))
                    {
                        this.field_244794_ae = "true".equals(s1);
                    }

                    if ("joinedFirstServer".equals(s))
                    {
                        this.field_244601_E = "true".equals(s1);
                    }

                    if ("syncChunkWrites".equals(s))
                    {
                        this.syncChunkWrites = "true".equals(s1);
                    }

                    for (KeyBinding keybinding : this.keyBindings)
                    {
                        if (s.equals("key_" + keybinding.getKeyDescription()))
                        {
                            if (Reflector.KeyModifier_valueFromString.exists())
                            {
                                if (s1.indexOf(58) != -1)
                                {
                                    String[] astring = s1.split(":");
                                    Object object = Reflector.call(Reflector.KeyModifier_valueFromString, astring[1]);
                                    Reflector.call(keybinding, Reflector.ForgeKeyBinding_setKeyModifierAndCode, object, InputMappings.getInputByName(astring[0]));
                                }
                                else
                                {
                                    Object object1 = Reflector.getFieldValue(Reflector.KeyModifier_NONE);
                                    Reflector.call(keybinding, Reflector.ForgeKeyBinding_setKeyModifierAndCode, object1, InputMappings.getInputByName(s1));
                                }
                            }
                            else
                            {
                                keybinding.bind(InputMappings.getInputByName(s1));
                            }
                        }
                    }

                    for (SoundCategory soundcategory : SoundCategory.values())
                    {
                        if (s.equals("soundCategory_" + soundcategory.getName()))
                        {
                            this.soundLevels.put(soundcategory, parseFloat(s1));
                        }
                    }

                    for (PlayerModelPart playermodelpart : PlayerModelPart.values())
                    {
                        if (s.equals("modelPart_" + playermodelpart.getPartName()))
                        {
                            this.setModelPartEnabled(playermodelpart, "true".equals(s1));
                        }
                    }
                }
                catch (Exception exception)
                {
                    LOGGER.warn("Skipping bad option: {}:{}", s, s1);
                    exception.printStackTrace();
                }
            }

            KeyBinding.resetKeyBindingArrayAndHash();
        }
        catch (Exception exception11)
        {
            LOGGER.error("Failed to load options", (Throwable)exception11);
        }

        this.loadOfOptions();
    }

    private CompoundNBT dataFix(CompoundNBT nbt)
    {
        int i = 0;

        try
        {
            i = Integer.parseInt(nbt.getString("version"));
        }
        catch (RuntimeException runtimeexception)
        {
        }

        return NBTUtil.update(this.mc.getDataFixer(), DefaultTypeReferences.OPTIONS, nbt, i);
    }

    /**
     * Parses a string into a float.
     */
    private static float parseFloat(String floatString)
    {
        if ("true".equals(floatString))
        {
            return 1.0F;
        }
        else
        {
            return "false".equals(floatString) ? 0.0F : Float.parseFloat(floatString);
        }
    }

    /**
     * Saves the options to the options file.
     */
    public void saveOptions()
    {
        if (!Reflector.ClientModLoader_isLoading.exists() || !Reflector.callBoolean(Reflector.ClientModLoader_isLoading))
        {
            try (PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8)))
            {
                printwriter.println("version:" + SharedConstants.getVersion().getWorldVersion());
                printwriter.println("autoJump:" + AbstractOption.AUTO_JUMP.get(this));
                printwriter.println("autoSuggestions:" + AbstractOption.AUTO_SUGGEST_COMMANDS.get(this));
                printwriter.println("chatColors:" + AbstractOption.CHAT_COLOR.get(this));
                printwriter.println("chatLinks:" + AbstractOption.CHAT_LINKS.get(this));
                printwriter.println("chatLinksPrompt:" + AbstractOption.CHAT_LINKS_PROMPT.get(this));
                printwriter.println("enableVsync:" + AbstractOption.VSYNC.get(this));
                printwriter.println("entityShadows:" + AbstractOption.ENTITY_SHADOWS.get(this));
                printwriter.println("forceUnicodeFont:" + AbstractOption.FORCE_UNICODE_FONT.get(this));
                printwriter.println("discrete_mouse_scroll:" + AbstractOption.DISCRETE_MOUSE_SCROLL.get(this));
                printwriter.println("invertYMouse:" + AbstractOption.INVERT_MOUSE.get(this));
                printwriter.println("realmsNotifications:" + AbstractOption.REALMS_NOTIFICATIONS.get(this));
                printwriter.println("reducedDebugInfo:" + AbstractOption.REDUCED_DEBUG_INFO.get(this));
                printwriter.println("snooperEnabled:" + AbstractOption.SNOOPER.get(this));
                printwriter.println("showSubtitles:" + AbstractOption.SHOW_SUBTITLES.get(this));
                printwriter.println("touchscreen:" + AbstractOption.TOUCHSCREEN.get(this));
                printwriter.println("fullscreen:" + AbstractOption.FULLSCREEN.get(this));
                printwriter.println("bobView:" + AbstractOption.VIEW_BOBBING.get(this));
                printwriter.println("toggleCrouch:" + this.toggleCrouch);
                printwriter.println("toggleSprint:" + this.toggleSprint);
                printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
                printwriter.println("fov:" + (this.fov - 70.0D) / 40.0D);
                printwriter.println("screenEffectScale:" + this.screenEffectScale);
                printwriter.println("fovEffectScale:" + this.fovScaleEffect);
                printwriter.println("gamma:" + this.gamma);
                printwriter.println("renderDistance:" + this.renderDistanceChunks);
                printwriter.println("entityDistanceScaling:" + this.entityDistanceScaling);
                printwriter.println("guiScale:" + this.guiScale);
                printwriter.println("particles:" + this.particles.getId());
                printwriter.println("maxFps:" + this.framerateLimit);
                printwriter.println("difficulty:" + this.difficulty.getId());
                printwriter.println("graphicsMode:" + this.graphicFanciness.func_238162_a_());
                printwriter.println("ao:" + this.ambientOcclusionStatus.getId());
                printwriter.println("biomeBlendRadius:" + this.biomeBlendRadius);

                switch (this.cloudOption)
                {
                    case FANCY:
                        printwriter.println("renderClouds:true");
                        break;

                    case FAST:
                        printwriter.println("renderClouds:fast");
                        break;

                    case OFF:
                        printwriter.println("renderClouds:false");
                }

                printwriter.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
                printwriter.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
                printwriter.println("lastServer:" + this.lastServer);
                printwriter.println("lang:" + this.language);
                printwriter.println("chatVisibility:" + this.chatVisibility.getId());
                printwriter.println("chatOpacity:" + this.chatOpacity);
                printwriter.println("chatLineSpacing:" + this.chatLineSpacing);
                printwriter.println("textBackgroundOpacity:" + this.accessibilityTextBackgroundOpacity);
                printwriter.println("backgroundForChatOnly:" + this.accessibilityTextBackground);

                if (this.mc.getMainWindow().getVideoMode().isPresent())
                {
                    printwriter.println("fullscreenResolution:" + this.mc.getMainWindow().getVideoMode().get().getSettingsString());
                }

                printwriter.println("hideServerAddress:" + this.hideServerAddress);
                printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
                printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
                printwriter.println("overrideWidth:" + this.overrideWidth);
                printwriter.println("overrideHeight:" + this.overrideHeight);
                printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
                printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
                printwriter.println("chatDelay: " + this.chatDelay);
                printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
                printwriter.println("chatScale:" + this.chatScale);
                printwriter.println("chatWidth:" + (float)this.chatWidth);
                printwriter.println("mipmapLevels:" + this.mipmapLevels);
                printwriter.println("useNativeTransport:" + this.useNativeTransport);
                printwriter.println("mainHand:" + (this.mainHand == HandSide.LEFT ? "left" : "right"));
                printwriter.println("attackIndicator:" + this.attackIndicator.getId());
                printwriter.println("narrator:" + this.narrator.getId());
                printwriter.println("tutorialStep:" + this.tutorialStep.getName());
                printwriter.println("mouseWheelSensitivity:" + this.mouseWheelSensitivity);
                printwriter.println("rawMouseInput:" + AbstractOption.RAW_MOUSE_INPUT.get(this));
                printwriter.println("glDebugVerbosity:" + this.glDebugVerbosity);
                printwriter.println("skipMultiplayerWarning:" + this.skipMultiplayerWarning);
                printwriter.println("hideMatchedNames:" + this.field_244794_ae);
                printwriter.println("joinedFirstServer:" + this.field_244601_E);
                printwriter.println("syncChunkWrites:" + this.syncChunkWrites);

                for (KeyBinding keybinding : this.keyBindings)
                {
                    if (Reflector.ForgeKeyBinding_getKeyModifier.exists())
                    {
                        String s = "key_" + keybinding.getKeyDescription() + ":" + keybinding.getTranslationKey();
                        Object object = Reflector.call(keybinding, Reflector.ForgeKeyBinding_getKeyModifier);
                        Object object1 = Reflector.getFieldValue(Reflector.KeyModifier_NONE);
                        printwriter.println(object != object1 ? s + ":" + object : s);
                    }
                    else
                    {
                        printwriter.println("key_" + keybinding.getKeyDescription() + ":" + keybinding.getTranslationKey());
                    }
                }

                for (SoundCategory soundcategory : SoundCategory.values())
                {
                    printwriter.println("soundCategory_" + soundcategory.getName() + ":" + this.getSoundLevel(soundcategory));
                }

                for (PlayerModelPart playermodelpart : PlayerModelPart.values())
                {
                    printwriter.println("modelPart_" + playermodelpart.getPartName() + ":" + this.setModelParts.contains(playermodelpart));
                }
            }
            catch (Exception exception1)
            {
                LOGGER.error("Failed to save options", (Throwable)exception1);
            }

            this.saveOfOptions();
            this.sendSettingsToServer();
        }
    }

    public float getSoundLevel(SoundCategory category)
    {
        return this.soundLevels.containsKey(category) ? this.soundLevels.get(category) : 1.0F;
    }

    public void setSoundLevel(SoundCategory category, float volume)
    {
        this.soundLevels.put(category, volume);
        this.mc.getSoundHandler().setSoundLevel(category, volume);
    }

    /**
     * Send a client info packet with settings information to the server
     */
    public void sendSettingsToServer()
    {
        if (this.mc.player != null)
        {
            int i = 0;

            for (PlayerModelPart playermodelpart : this.setModelParts)
            {
                i |= playermodelpart.getPartMask();
            }

            this.mc.player.connection.sendPacket(new CClientSettingsPacket(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColor, i, this.mainHand));
        }
    }

    public Set<PlayerModelPart> getModelParts()
    {
        return ImmutableSet.copyOf(this.setModelParts);
    }

    public void setModelPartEnabled(PlayerModelPart modelPart, boolean enable)
    {
        if (enable)
        {
            this.setModelParts.add(modelPart);
        }
        else
        {
            this.setModelParts.remove(modelPart);
        }

        this.sendSettingsToServer();
    }

    public void switchModelPartEnabled(PlayerModelPart modelPart)
    {
        if (this.getModelParts().contains(modelPart))
        {
            this.setModelParts.remove(modelPart);
        }
        else
        {
            this.setModelParts.add(modelPart);
        }

        this.sendSettingsToServer();
    }

    public CloudOption getCloudOption()
    {
        return this.renderDistanceChunks >= 4 ? this.cloudOption : CloudOption.OFF;
    }

    /**
     * Return true if the client connect to a server using the native transport system
     */
    public boolean isUsingNativeTransport()
    {
        return this.useNativeTransport;
    }

    public void setOptionFloatValueOF(AbstractOption p_setOptionFloatValueOF_1_, double p_setOptionFloatValueOF_2_)
    {
        if (p_setOptionFloatValueOF_1_ == AbstractOption.CLOUD_HEIGHT)
        {
            this.ofCloudsHeight = p_setOptionFloatValueOF_2_;
        }

        if (p_setOptionFloatValueOF_1_ == AbstractOption.AO_LEVEL)
        {
            this.ofAoLevel = p_setOptionFloatValueOF_2_;
            this.mc.worldRenderer.loadRenderers();
        }

        if (p_setOptionFloatValueOF_1_ == AbstractOption.AA_LEVEL)
        {
            int i = (int)p_setOptionFloatValueOF_2_;

            if (i > 0 && Config.isShaders())
            {
                Config.showGuiMessage(Lang.get("of.message.aa.shaders1"), Lang.get("of.message.aa.shaders2"));
                return;
            }

            if (i > 0 && Config.isGraphicsFabulous())
            {
                Config.showGuiMessage(Lang.get("of.message.aa.gf1"), Lang.get("of.message.aa.gf2"));
                return;
            }

            this.ofAaLevel = i;
            this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
        }

        if (p_setOptionFloatValueOF_1_ == AbstractOption.AF_LEVEL)
        {
            int j = (int)p_setOptionFloatValueOF_2_;
            this.ofAfLevel = j;
            this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
            this.mc.scheduleResourcesRefresh();
            Shaders.uninit();
        }

        if (p_setOptionFloatValueOF_1_ == AbstractOption.MIPMAP_TYPE)
        {
            int k = (int)p_setOptionFloatValueOF_2_;
            this.ofMipmapType = Config.limit(k, 0, 3);
            this.updateMipmaps();
        }
    }

    public double getOptionFloatValueOF(AbstractOption p_getOptionFloatValueOF_1_)
    {
        if (p_getOptionFloatValueOF_1_ == AbstractOption.CLOUD_HEIGHT)
        {
            return this.ofCloudsHeight;
        }
        else if (p_getOptionFloatValueOF_1_ == AbstractOption.AO_LEVEL)
        {
            return this.ofAoLevel;
        }
        else if (p_getOptionFloatValueOF_1_ == AbstractOption.AA_LEVEL)
        {
            return (double)this.ofAaLevel;
        }
        else if (p_getOptionFloatValueOF_1_ == AbstractOption.AF_LEVEL)
        {
            return (double)this.ofAfLevel;
        }
        else if (p_getOptionFloatValueOF_1_ == AbstractOption.MIPMAP_TYPE)
        {
            return (double)this.ofMipmapType;
        }
        else if (p_getOptionFloatValueOF_1_ == AbstractOption.FRAMERATE_LIMIT)
        {
            return (double)this.framerateLimit == AbstractOption.FRAMERATE_LIMIT.getMaxValue() && this.vsync ? 0.0D : (double)this.framerateLimit;
        }
        else
        {
            return (double)Float.MAX_VALUE;
        }
    }

    public void setOptionValueOF(AbstractOption p_setOptionValueOF_1_, int p_setOptionValueOF_2_)
    {
        if (p_setOptionValueOF_1_ == AbstractOption.FOG_FANCY)
        {
            switch (this.ofFogType)
            {
                case 1:
                    this.ofFogType = 2;

                    if (!Config.isFancyFogAvailable())
                    {
                        this.ofFogType = 3;
                    }

                    break;

                case 2:
                    this.ofFogType = 3;
                    break;

                case 3:
                    this.ofFogType = 1;
                    break;

                default:
                    this.ofFogType = 1;
            }
        }

        if (p_setOptionValueOF_1_ == AbstractOption.FOG_START)
        {
            this.ofFogStart += 0.2F;

            if (this.ofFogStart > 0.81F)
            {
                this.ofFogStart = 0.2F;
            }
        }

        if (p_setOptionValueOF_1_ == AbstractOption.SMOOTH_FPS)
        {
            this.ofSmoothFps = !this.ofSmoothFps;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.SMOOTH_WORLD)
        {
            this.ofSmoothWorld = !this.ofSmoothWorld;
            Config.updateThreadPriorities();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.CLOUDS)
        {
            ++this.ofClouds;

            if (this.ofClouds > 3)
            {
                this.ofClouds = 0;
            }

            this.updateRenderClouds();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.TREES)
        {
            this.ofTrees = nextValue(this.ofTrees, OF_TREES_VALUES);
            this.mc.worldRenderer.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.DROPPED_ITEMS)
        {
            ++this.ofDroppedItems;

            if (this.ofDroppedItems > 2)
            {
                this.ofDroppedItems = 0;
            }
        }

        if (p_setOptionValueOF_1_ == AbstractOption.RAIN)
        {
            ++this.ofRain;

            if (this.ofRain > 3)
            {
                this.ofRain = 0;
            }
        }

        if (p_setOptionValueOF_1_ == AbstractOption.ANIMATED_WATER)
        {
            ++this.ofAnimatedWater;

            if (this.ofAnimatedWater == 1)
            {
                ++this.ofAnimatedWater;
            }

            if (this.ofAnimatedWater > 2)
            {
                this.ofAnimatedWater = 0;
            }
        }

        if (p_setOptionValueOF_1_ == AbstractOption.ANIMATED_LAVA)
        {
            ++this.ofAnimatedLava;

            if (this.ofAnimatedLava == 1)
            {
                ++this.ofAnimatedLava;
            }

            if (this.ofAnimatedLava > 2)
            {
                this.ofAnimatedLava = 0;
            }
        }

        if (p_setOptionValueOF_1_ == AbstractOption.ANIMATED_FIRE)
        {
            this.ofAnimatedFire = !this.ofAnimatedFire;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.ANIMATED_PORTAL)
        {
            this.ofAnimatedPortal = !this.ofAnimatedPortal;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.ANIMATED_REDSTONE)
        {
            this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.ANIMATED_EXPLOSION)
        {
            this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.ANIMATED_FLAME)
        {
            this.ofAnimatedFlame = !this.ofAnimatedFlame;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.ANIMATED_SMOKE)
        {
            this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.VOID_PARTICLES)
        {
            this.ofVoidParticles = !this.ofVoidParticles;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.WATER_PARTICLES)
        {
            this.ofWaterParticles = !this.ofWaterParticles;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.PORTAL_PARTICLES)
        {
            this.ofPortalParticles = !this.ofPortalParticles;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.POTION_PARTICLES)
        {
            this.ofPotionParticles = !this.ofPotionParticles;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.FIREWORK_PARTICLES)
        {
            this.ofFireworkParticles = !this.ofFireworkParticles;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.DRIPPING_WATER_LAVA)
        {
            this.ofDrippingWaterLava = !this.ofDrippingWaterLava;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.ANIMATED_TERRAIN)
        {
            this.ofAnimatedTerrain = !this.ofAnimatedTerrain;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.ANIMATED_TEXTURES)
        {
            this.ofAnimatedTextures = !this.ofAnimatedTextures;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.RAIN_SPLASH)
        {
            this.ofRainSplash = !this.ofRainSplash;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.LAGOMETER)
        {
            this.ofLagometer = !this.ofLagometer;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.SHOW_FPS)
        {
            this.ofShowFps = !this.ofShowFps;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.AUTOSAVE_TICKS)
        {
            int i = 900;
            this.ofAutoSaveTicks = Math.max(this.ofAutoSaveTicks / i * i, i);
            this.ofAutoSaveTicks *= 2;

            if (this.ofAutoSaveTicks > 32 * i)
            {
                this.ofAutoSaveTicks = i;
            }
        }

        if (p_setOptionValueOF_1_ == AbstractOption.BETTER_GRASS)
        {
            ++this.ofBetterGrass;

            if (this.ofBetterGrass > 3)
            {
                this.ofBetterGrass = 1;
            }

            this.mc.worldRenderer.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.CONNECTED_TEXTURES)
        {
            ++this.ofConnectedTextures;

            if (this.ofConnectedTextures > 3)
            {
                this.ofConnectedTextures = 1;
            }

            if (this.ofConnectedTextures == 2)
            {
                this.mc.worldRenderer.loadRenderers();
            }
            else
            {
                this.mc.scheduleResourcesRefresh();
            }
        }

        if (p_setOptionValueOF_1_ == AbstractOption.WEATHER)
        {
            this.ofWeather = !this.ofWeather;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.SKY)
        {
            this.ofSky = !this.ofSky;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.STARS)
        {
            this.ofStars = !this.ofStars;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.SUN_MOON)
        {
            this.ofSunMoon = !this.ofSunMoon;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.VIGNETTE)
        {
            ++this.ofVignette;

            if (this.ofVignette > 2)
            {
                this.ofVignette = 0;
            }
        }

        if (p_setOptionValueOF_1_ == AbstractOption.CHUNK_UPDATES)
        {
            ++this.ofChunkUpdates;

            if (this.ofChunkUpdates > 5)
            {
                this.ofChunkUpdates = 1;
            }
        }

        if (p_setOptionValueOF_1_ == AbstractOption.CHUNK_UPDATES_DYNAMIC)
        {
            this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.TIME)
        {
            ++this.ofTime;

            if (this.ofTime > 2)
            {
                this.ofTime = 0;
            }
        }

        if (p_setOptionValueOF_1_ == AbstractOption.PROFILER)
        {
            this.ofProfiler = !this.ofProfiler;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.BETTER_SNOW)
        {
            this.ofBetterSnow = !this.ofBetterSnow;
            this.mc.worldRenderer.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.SWAMP_COLORS)
        {
            this.ofSwampColors = !this.ofSwampColors;
            CustomColors.updateUseDefaultGrassFoliageColors();
            this.mc.worldRenderer.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.RANDOM_ENTITIES)
        {
            this.ofRandomEntities = !this.ofRandomEntities;
            RandomEntities.update();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.CUSTOM_FONTS)
        {
            this.ofCustomFonts = !this.ofCustomFonts;
            FontUtils.reloadFonts();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.CUSTOM_COLORS)
        {
            this.ofCustomColors = !this.ofCustomColors;
            CustomColors.update();
            this.mc.worldRenderer.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.CUSTOM_ITEMS)
        {
            this.ofCustomItems = !this.ofCustomItems;
            this.mc.scheduleResourcesRefresh();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.CUSTOM_SKY)
        {
            this.ofCustomSky = !this.ofCustomSky;
            CustomSky.update();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.SHOW_CAPES)
        {
            this.ofShowCapes = !this.ofShowCapes;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.NATURAL_TEXTURES)
        {
            this.ofNaturalTextures = !this.ofNaturalTextures;
            NaturalTextures.update();
            this.mc.worldRenderer.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.EMISSIVE_TEXTURES)
        {
            this.ofEmissiveTextures = !this.ofEmissiveTextures;
            this.mc.scheduleResourcesRefresh();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.FAST_MATH)
        {
            this.ofFastMath = !this.ofFastMath;
            MathHelper.fastMath = this.ofFastMath;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.FAST_RENDER)
        {
            this.ofFastRender = !this.ofFastRender;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.TRANSLUCENT_BLOCKS)
        {
            if (this.ofTranslucentBlocks == 0)
            {
                this.ofTranslucentBlocks = 1;
            }
            else if (this.ofTranslucentBlocks == 1)
            {
                this.ofTranslucentBlocks = 2;
            }
            else if (this.ofTranslucentBlocks == 2)
            {
                this.ofTranslucentBlocks = 0;
            }
            else
            {
                this.ofTranslucentBlocks = 0;
            }

            this.mc.worldRenderer.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.LAZY_CHUNK_LOADING)
        {
            this.ofLazyChunkLoading = !this.ofLazyChunkLoading;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.RENDER_REGIONS)
        {
            this.ofRenderRegions = !this.ofRenderRegions;
            this.mc.worldRenderer.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.SMART_ANIMATIONS)
        {
            this.ofSmartAnimations = !this.ofSmartAnimations;
            this.mc.worldRenderer.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.DYNAMIC_FOV)
        {
            this.ofDynamicFov = !this.ofDynamicFov;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.ALTERNATE_BLOCKS)
        {
            this.ofAlternateBlocks = !this.ofAlternateBlocks;
            this.mc.worldRenderer.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.DYNAMIC_LIGHTS)
        {
            this.ofDynamicLights = nextValue(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
            DynamicLights.removeLights(this.mc.worldRenderer);
        }

        if (p_setOptionValueOF_1_ == AbstractOption.SCREENSHOT_SIZE)
        {
            ++this.ofScreenshotSize;

            if (this.ofScreenshotSize > 4)
            {
                this.ofScreenshotSize = 1;
            }
        }

        if (p_setOptionValueOF_1_ == AbstractOption.CUSTOM_ENTITY_MODELS)
        {
            this.ofCustomEntityModels = !this.ofCustomEntityModels;
            this.mc.scheduleResourcesRefresh();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.CUSTOM_GUIS)
        {
            this.ofCustomGuis = !this.ofCustomGuis;
            CustomGuis.update();
        }

        if (p_setOptionValueOF_1_ == AbstractOption.SHOW_GL_ERRORS)
        {
            this.ofShowGlErrors = !this.ofShowGlErrors;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.HELD_ITEM_TOOLTIPS)
        {
            this.heldItemTooltips = !this.heldItemTooltips;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.ADVANCED_TOOLTIPS)
        {
            this.advancedItemTooltips = !this.advancedItemTooltips;
        }

        if (p_setOptionValueOF_1_ == AbstractOption.CHAT_BACKGROUND)
        {
            if (this.ofChatBackground == 0)
            {
                this.ofChatBackground = 5;
            }
            else if (this.ofChatBackground == 5)
            {
                this.ofChatBackground = 3;
            }
            else
            {
                this.ofChatBackground = 0;
            }
        }

        if (p_setOptionValueOF_1_ == AbstractOption.CHAT_SHADOW)
        {
            this.ofChatShadow = !this.ofChatShadow;
        }
    }

    public ITextComponent getKeyComponentOF(AbstractOption p_getKeyComponentOF_1_)
    {
        String s = this.getKeyBindingOF(p_getKeyComponentOF_1_);
        ITextComponent itextcomponent = new StringTextComponent(s);
        return itextcomponent;
    }

    public String getKeyBindingOF(AbstractOption p_getKeyBindingOF_1_)
    {
        String s = I18n.format(p_getKeyBindingOF_1_.getResourceKey()) + ": ";

        if (s == null)
        {
            s = p_getKeyBindingOF_1_.getResourceKey();
        }

        if (p_getKeyBindingOF_1_ == AbstractOption.RENDER_DISTANCE)
        {
            int i1 = (int)AbstractOption.RENDER_DISTANCE.get(this);
            String s2 = I18n.format("of.options.renderDistance.tiny");
            int i = 2;

            if (i1 >= 4)
            {
                s2 = I18n.format("of.options.renderDistance.short");
                i = 4;
            }

            if (i1 >= 8)
            {
                s2 = I18n.format("of.options.renderDistance.normal");
                i = 8;
            }

            if (i1 >= 16)
            {
                s2 = I18n.format("of.options.renderDistance.far");
                i = 16;
            }

            if (i1 >= 32)
            {
                s2 = Lang.get("of.options.renderDistance.extreme");
                i = 32;
            }

            if (i1 >= 48)
            {
                s2 = Lang.get("of.options.renderDistance.insane");
                i = 48;
            }

            if (i1 >= 64)
            {
                s2 = Lang.get("of.options.renderDistance.ludicrous");
                i = 64;
            }

            int j = this.renderDistanceChunks - i;
            String s1 = s2;

            if (j > 0)
            {
                s1 = s2 + "+";
            }

            return s + i1 + " " + s1 + "";
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.FOG_FANCY)
        {
            switch (this.ofFogType)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                case 3:
                    return s + Lang.getOff();

                default:
                    return s + Lang.getOff();
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.FOG_START)
        {
            return s + this.ofFogStart;
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.MIPMAP_TYPE)
        {
            return FloatOptions.getText(p_getKeyBindingOF_1_, (double)this.ofMipmapType);
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.SMOOTH_FPS)
        {
            return this.ofSmoothFps ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.SMOOTH_WORLD)
        {
            return this.ofSmoothWorld ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.CLOUDS)
        {
            switch (this.ofClouds)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                case 3:
                    return s + Lang.getOff();

                default:
                    return s + Lang.getDefault();
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.TREES)
        {
            switch (this.ofTrees)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                case 3:
                default:
                    return s + Lang.getDefault();

                case 4:
                    return s + Lang.get("of.general.smart");
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.DROPPED_ITEMS)
        {
            switch (this.ofDroppedItems)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                default:
                    return s + Lang.getDefault();
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.RAIN)
        {
            switch (this.ofRain)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                case 3:
                    return s + Lang.getOff();

                default:
                    return s + Lang.getDefault();
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.ANIMATED_WATER)
        {
            switch (this.ofAnimatedWater)
            {
                case 1:
                    return s + Lang.get("of.options.animation.dynamic");

                case 2:
                    return s + Lang.getOff();

                default:
                    return s + Lang.getOn();
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.ANIMATED_LAVA)
        {
            switch (this.ofAnimatedLava)
            {
                case 1:
                    return s + Lang.get("of.options.animation.dynamic");

                case 2:
                    return s + Lang.getOff();

                default:
                    return s + Lang.getOn();
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.ANIMATED_FIRE)
        {
            return this.ofAnimatedFire ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.ANIMATED_PORTAL)
        {
            return this.ofAnimatedPortal ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.ANIMATED_REDSTONE)
        {
            return this.ofAnimatedRedstone ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.ANIMATED_EXPLOSION)
        {
            return this.ofAnimatedExplosion ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.ANIMATED_FLAME)
        {
            return this.ofAnimatedFlame ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.ANIMATED_SMOKE)
        {
            return this.ofAnimatedSmoke ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.VOID_PARTICLES)
        {
            return this.ofVoidParticles ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.WATER_PARTICLES)
        {
            return this.ofWaterParticles ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.PORTAL_PARTICLES)
        {
            return this.ofPortalParticles ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.POTION_PARTICLES)
        {
            return this.ofPotionParticles ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.FIREWORK_PARTICLES)
        {
            return this.ofFireworkParticles ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.DRIPPING_WATER_LAVA)
        {
            return this.ofDrippingWaterLava ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.ANIMATED_TERRAIN)
        {
            return this.ofAnimatedTerrain ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.ANIMATED_TEXTURES)
        {
            return this.ofAnimatedTextures ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.RAIN_SPLASH)
        {
            return this.ofRainSplash ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.LAGOMETER)
        {
            return this.ofLagometer ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.SHOW_FPS)
        {
            return this.ofShowFps ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.AUTOSAVE_TICKS)
        {
            int l = 900;

            if (this.ofAutoSaveTicks <= l)
            {
                return s + Lang.get("of.options.save.45s");
            }
            else if (this.ofAutoSaveTicks <= 2 * l)
            {
                return s + Lang.get("of.options.save.90s");
            }
            else if (this.ofAutoSaveTicks <= 4 * l)
            {
                return s + Lang.get("of.options.save.3min");
            }
            else if (this.ofAutoSaveTicks <= 8 * l)
            {
                return s + Lang.get("of.options.save.6min");
            }
            else
            {
                return this.ofAutoSaveTicks <= 16 * l ? s + Lang.get("of.options.save.12min") : s + Lang.get("of.options.save.24min");
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.BETTER_GRASS)
        {
            switch (this.ofBetterGrass)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                default:
                    return s + Lang.getOff();
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.CONNECTED_TEXTURES)
        {
            switch (this.ofConnectedTextures)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                default:
                    return s + Lang.getOff();
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.WEATHER)
        {
            return this.ofWeather ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.SKY)
        {
            return this.ofSky ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.STARS)
        {
            return this.ofStars ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.SUN_MOON)
        {
            return this.ofSunMoon ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.VIGNETTE)
        {
            switch (this.ofVignette)
            {
                case 1:
                    return s + Lang.getFast();

                case 2:
                    return s + Lang.getFancy();

                default:
                    return s + Lang.getDefault();
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.CHUNK_UPDATES)
        {
            return s + this.ofChunkUpdates;
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.CHUNK_UPDATES_DYNAMIC)
        {
            return this.ofChunkUpdatesDynamic ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.TIME)
        {
            if (this.ofTime == 1)
            {
                return s + Lang.get("of.options.time.dayOnly");
            }
            else
            {
                return this.ofTime == 2 ? s + Lang.get("of.options.time.nightOnly") : s + Lang.getDefault();
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.AA_LEVEL)
        {
            return FloatOptions.getText(p_getKeyBindingOF_1_, (double)this.ofAaLevel);
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.AF_LEVEL)
        {
            return FloatOptions.getText(p_getKeyBindingOF_1_, (double)this.ofAfLevel);
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.PROFILER)
        {
            return this.ofProfiler ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.BETTER_SNOW)
        {
            return this.ofBetterSnow ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.SWAMP_COLORS)
        {
            return this.ofSwampColors ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.RANDOM_ENTITIES)
        {
            return this.ofRandomEntities ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.CUSTOM_FONTS)
        {
            return this.ofCustomFonts ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.CUSTOM_COLORS)
        {
            return this.ofCustomColors ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.CUSTOM_SKY)
        {
            return this.ofCustomSky ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.SHOW_CAPES)
        {
            return this.ofShowCapes ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.CUSTOM_ITEMS)
        {
            return this.ofCustomItems ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.NATURAL_TEXTURES)
        {
            return this.ofNaturalTextures ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.EMISSIVE_TEXTURES)
        {
            return this.ofEmissiveTextures ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.FAST_MATH)
        {
            return this.ofFastMath ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.FAST_RENDER)
        {
            return this.ofFastRender ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.TRANSLUCENT_BLOCKS)
        {
            if (this.ofTranslucentBlocks == 1)
            {
                return s + Lang.getFast();
            }
            else
            {
                return this.ofTranslucentBlocks == 2 ? s + Lang.getFancy() : s + Lang.getDefault();
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.LAZY_CHUNK_LOADING)
        {
            return this.ofLazyChunkLoading ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.RENDER_REGIONS)
        {
            return this.ofRenderRegions ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.SMART_ANIMATIONS)
        {
            return this.ofSmartAnimations ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.DYNAMIC_FOV)
        {
            return this.ofDynamicFov ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.ALTERNATE_BLOCKS)
        {
            return this.ofAlternateBlocks ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.DYNAMIC_LIGHTS)
        {
            int k = indexOf(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
            return s + getTranslation(KEYS_DYNAMIC_LIGHTS, k);
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.SCREENSHOT_SIZE)
        {
            return this.ofScreenshotSize <= 1 ? s + Lang.getDefault() : s + this.ofScreenshotSize + "x";
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.CUSTOM_ENTITY_MODELS)
        {
            return this.ofCustomEntityModels ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.CUSTOM_GUIS)
        {
            return this.ofCustomGuis ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.SHOW_GL_ERRORS)
        {
            return this.ofShowGlErrors ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.HELD_ITEM_TOOLTIPS)
        {
            return this.heldItemTooltips ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.ADVANCED_TOOLTIPS)
        {
            return this.advancedItemTooltips ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.FRAMERATE_LIMIT)
        {
            double d1 = AbstractOption.FRAMERATE_LIMIT.get(this);

            if (d1 == 0.0D)
            {
                return s + Lang.get("of.options.framerateLimit.vsync");
            }
            else
            {
                return d1 == AbstractOption.FRAMERATE_LIMIT.getMaxValue() ? s + I18n.format("options.framerateLimit.max") : s + (int)d1 + " fps";
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.CHAT_BACKGROUND)
        {
            if (this.ofChatBackground == 3)
            {
                return s + Lang.getOff();
            }
            else
            {
                return this.ofChatBackground == 5 ? s + Lang.get("of.general.compact") : s + Lang.getDefault();
            }
        }
        else if (p_getKeyBindingOF_1_ == AbstractOption.CHAT_SHADOW)
        {
            return this.ofChatShadow ? s + Lang.getOn() : s + Lang.getOff();
        }
        else if (p_getKeyBindingOF_1_ instanceof SliderPercentageOption)
        {
            SliderPercentageOption sliderpercentageoption = (SliderPercentageOption)p_getKeyBindingOF_1_;
            double d0 = sliderpercentageoption.get(this);
            return d0 == 0.0D ? s + I18n.format("options.off") : s + (int)(d0 * 100.0D) + "%";
        }
        else
        {
            return null;
        }
    }

    public void loadOfOptions()
    {
        try
        {
            File file1 = this.optionsFileOF;

            if (!file1.exists())
            {
                file1 = this.optionsFile;
            }

            if (!file1.exists())
            {
                return;
            }

            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new FileInputStream(file1), StandardCharsets.UTF_8));
            String s = "";

            while ((s = bufferedreader.readLine()) != null)
            {
                try
                {
                    String[] astring = s.split(":");

                    if (astring[0].equals("ofRenderDistanceChunks") && astring.length >= 2)
                    {
                        this.renderDistanceChunks = Integer.valueOf(astring[1]);
                        this.renderDistanceChunks = Config.limit(this.renderDistanceChunks, 2, 1024);
                    }

                    if (astring[0].equals("ofFogType") && astring.length >= 2)
                    {
                        this.ofFogType = Integer.valueOf(astring[1]);
                        this.ofFogType = Config.limit(this.ofFogType, 1, 3);
                    }

                    if (astring[0].equals("ofFogStart") && astring.length >= 2)
                    {
                        this.ofFogStart = Float.valueOf(astring[1]);

                        if (this.ofFogStart < 0.2F)
                        {
                            this.ofFogStart = 0.2F;
                        }

                        if (this.ofFogStart > 0.81F)
                        {
                            this.ofFogStart = 0.8F;
                        }
                    }

                    if (astring[0].equals("ofMipmapType") && astring.length >= 2)
                    {
                        this.ofMipmapType = Integer.valueOf(astring[1]);
                        this.ofMipmapType = Config.limit(this.ofMipmapType, 0, 3);
                    }

                    if (astring[0].equals("ofOcclusionFancy") && astring.length >= 2)
                    {
                        this.ofOcclusionFancy = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofSmoothFps") && astring.length >= 2)
                    {
                        this.ofSmoothFps = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofSmoothWorld") && astring.length >= 2)
                    {
                        this.ofSmoothWorld = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofAoLevel") && astring.length >= 2)
                    {
                        this.ofAoLevel = (double)Float.valueOf(astring[1]).floatValue();
                        this.ofAoLevel = Config.limit(this.ofAoLevel, 0.0D, 1.0D);
                    }

                    if (astring[0].equals("ofClouds") && astring.length >= 2)
                    {
                        this.ofClouds = Integer.valueOf(astring[1]);
                        this.ofClouds = Config.limit(this.ofClouds, 0, 3);
                        this.updateRenderClouds();
                    }

                    if (astring[0].equals("ofCloudsHeight") && astring.length >= 2)
                    {
                        this.ofCloudsHeight = (double)Float.valueOf(astring[1]).floatValue();
                        this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0D, 1.0D);
                    }

                    if (astring[0].equals("ofTrees") && astring.length >= 2)
                    {
                        this.ofTrees = Integer.valueOf(astring[1]);
                        this.ofTrees = limit(this.ofTrees, OF_TREES_VALUES);
                    }

                    if (astring[0].equals("ofDroppedItems") && astring.length >= 2)
                    {
                        this.ofDroppedItems = Integer.valueOf(astring[1]);
                        this.ofDroppedItems = Config.limit(this.ofDroppedItems, 0, 2);
                    }

                    if (astring[0].equals("ofRain") && astring.length >= 2)
                    {
                        this.ofRain = Integer.valueOf(astring[1]);
                        this.ofRain = Config.limit(this.ofRain, 0, 3);
                    }

                    if (astring[0].equals("ofAnimatedWater") && astring.length >= 2)
                    {
                        this.ofAnimatedWater = Integer.valueOf(astring[1]);
                        this.ofAnimatedWater = Config.limit(this.ofAnimatedWater, 0, 2);
                    }

                    if (astring[0].equals("ofAnimatedLava") && astring.length >= 2)
                    {
                        this.ofAnimatedLava = Integer.valueOf(astring[1]);
                        this.ofAnimatedLava = Config.limit(this.ofAnimatedLava, 0, 2);
                    }

                    if (astring[0].equals("ofAnimatedFire") && astring.length >= 2)
                    {
                        this.ofAnimatedFire = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofAnimatedPortal") && astring.length >= 2)
                    {
                        this.ofAnimatedPortal = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofAnimatedRedstone") && astring.length >= 2)
                    {
                        this.ofAnimatedRedstone = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofAnimatedExplosion") && astring.length >= 2)
                    {
                        this.ofAnimatedExplosion = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofAnimatedFlame") && astring.length >= 2)
                    {
                        this.ofAnimatedFlame = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofAnimatedSmoke") && astring.length >= 2)
                    {
                        this.ofAnimatedSmoke = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofVoidParticles") && astring.length >= 2)
                    {
                        this.ofVoidParticles = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofWaterParticles") && astring.length >= 2)
                    {
                        this.ofWaterParticles = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofPortalParticles") && astring.length >= 2)
                    {
                        this.ofPortalParticles = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofPotionParticles") && astring.length >= 2)
                    {
                        this.ofPotionParticles = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofFireworkParticles") && astring.length >= 2)
                    {
                        this.ofFireworkParticles = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofDrippingWaterLava") && astring.length >= 2)
                    {
                        this.ofDrippingWaterLava = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofAnimatedTerrain") && astring.length >= 2)
                    {
                        this.ofAnimatedTerrain = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofAnimatedTextures") && astring.length >= 2)
                    {
                        this.ofAnimatedTextures = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofRainSplash") && astring.length >= 2)
                    {
                        this.ofRainSplash = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofLagometer") && astring.length >= 2)
                    {
                        this.ofLagometer = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofShowFps") && astring.length >= 2)
                    {
                        this.ofShowFps = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofAutoSaveTicks") && astring.length >= 2)
                    {
                        this.ofAutoSaveTicks = Integer.valueOf(astring[1]);
                        this.ofAutoSaveTicks = Config.limit(this.ofAutoSaveTicks, 40, 40000);
                    }

                    if (astring[0].equals("ofBetterGrass") && astring.length >= 2)
                    {
                        this.ofBetterGrass = Integer.valueOf(astring[1]);
                        this.ofBetterGrass = Config.limit(this.ofBetterGrass, 1, 3);
                    }

                    if (astring[0].equals("ofConnectedTextures") && astring.length >= 2)
                    {
                        this.ofConnectedTextures = Integer.valueOf(astring[1]);
                        this.ofConnectedTextures = Config.limit(this.ofConnectedTextures, 1, 3);
                    }

                    if (astring[0].equals("ofWeather") && astring.length >= 2)
                    {
                        this.ofWeather = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofSky") && astring.length >= 2)
                    {
                        this.ofSky = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofStars") && astring.length >= 2)
                    {
                        this.ofStars = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofSunMoon") && astring.length >= 2)
                    {
                        this.ofSunMoon = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofVignette") && astring.length >= 2)
                    {
                        this.ofVignette = Integer.valueOf(astring[1]);
                        this.ofVignette = Config.limit(this.ofVignette, 0, 2);
                    }

                    if (astring[0].equals("ofChunkUpdates") && astring.length >= 2)
                    {
                        this.ofChunkUpdates = Integer.valueOf(astring[1]);
                        this.ofChunkUpdates = Config.limit(this.ofChunkUpdates, 1, 5);
                    }

                    if (astring[0].equals("ofChunkUpdatesDynamic") && astring.length >= 2)
                    {
                        this.ofChunkUpdatesDynamic = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofTime") && astring.length >= 2)
                    {
                        this.ofTime = Integer.valueOf(astring[1]);
                        this.ofTime = Config.limit(this.ofTime, 0, 2);
                    }

                    if (astring[0].equals("ofAaLevel") && astring.length >= 2)
                    {
                        this.ofAaLevel = Integer.valueOf(astring[1]);
                        this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
                    }

                    if (astring[0].equals("ofAfLevel") && astring.length >= 2)
                    {
                        this.ofAfLevel = Integer.valueOf(astring[1]);
                        this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
                    }

                    if (astring[0].equals("ofProfiler") && astring.length >= 2)
                    {
                        this.ofProfiler = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofBetterSnow") && astring.length >= 2)
                    {
                        this.ofBetterSnow = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofSwampColors") && astring.length >= 2)
                    {
                        this.ofSwampColors = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofRandomEntities") && astring.length >= 2)
                    {
                        this.ofRandomEntities = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofCustomFonts") && astring.length >= 2)
                    {
                        this.ofCustomFonts = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofCustomColors") && astring.length >= 2)
                    {
                        this.ofCustomColors = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofCustomItems") && astring.length >= 2)
                    {
                        this.ofCustomItems = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofCustomSky") && astring.length >= 2)
                    {
                        this.ofCustomSky = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofShowCapes") && astring.length >= 2)
                    {
                        this.ofShowCapes = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofNaturalTextures") && astring.length >= 2)
                    {
                        this.ofNaturalTextures = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofEmissiveTextures") && astring.length >= 2)
                    {
                        this.ofEmissiveTextures = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofLazyChunkLoading") && astring.length >= 2)
                    {
                        this.ofLazyChunkLoading = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofRenderRegions") && astring.length >= 2)
                    {
                        this.ofRenderRegions = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofSmartAnimations") && astring.length >= 2)
                    {
                        this.ofSmartAnimations = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofDynamicFov") && astring.length >= 2)
                    {
                        this.ofDynamicFov = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofAlternateBlocks") && astring.length >= 2)
                    {
                        this.ofAlternateBlocks = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofDynamicLights") && astring.length >= 2)
                    {
                        this.ofDynamicLights = Integer.valueOf(astring[1]);
                        this.ofDynamicLights = limit(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
                    }

                    if (astring[0].equals("ofScreenshotSize") && astring.length >= 2)
                    {
                        this.ofScreenshotSize = Integer.valueOf(astring[1]);
                        this.ofScreenshotSize = Config.limit(this.ofScreenshotSize, 1, 4);
                    }

                    if (astring[0].equals("ofCustomEntityModels") && astring.length >= 2)
                    {
                        this.ofCustomEntityModels = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofCustomGuis") && astring.length >= 2)
                    {
                        this.ofCustomGuis = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofShowGlErrors") && astring.length >= 2)
                    {
                        this.ofShowGlErrors = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofFastMath") && astring.length >= 2)
                    {
                        this.ofFastMath = Boolean.valueOf(astring[1]);
                        MathHelper.fastMath = this.ofFastMath;
                    }

                    if (astring[0].equals("ofFastRender") && astring.length >= 2)
                    {
                        this.ofFastRender = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofTranslucentBlocks") && astring.length >= 2)
                    {
                        this.ofTranslucentBlocks = Integer.valueOf(astring[1]);
                        this.ofTranslucentBlocks = Config.limit(this.ofTranslucentBlocks, 0, 2);
                    }

                    if (astring[0].equals("ofChatBackground") && astring.length >= 2)
                    {
                        this.ofChatBackground = Integer.valueOf(astring[1]);
                    }

                    if (astring[0].equals("ofChatShadow") && astring.length >= 2)
                    {
                        this.ofChatShadow = Boolean.valueOf(astring[1]);
                    }

                    if (astring[0].equals("key_" + this.ofKeyBindZoom.getKeyDescription()))
                    {
                        this.ofKeyBindZoom.bind(InputMappings.getInputByName(astring[1]));
                    }
                }
                catch (Exception exception1)
                {
                    Config.dbg("Skipping bad option: " + s);
                    exception1.printStackTrace();
                }
            }

            KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[] {this.ofKeyBindZoom});
            KeyBinding.resetKeyBindingArrayAndHash();
            bufferedreader.close();
        }
        catch (Exception exception11)
        {
            Config.warn("Failed to load options");
            exception11.printStackTrace();
        }
    }

    public void saveOfOptions()
    {
        try
        {
            PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFileOF), StandardCharsets.UTF_8));
            printwriter.println("ofFogType:" + this.ofFogType);
            printwriter.println("ofFogStart:" + this.ofFogStart);
            printwriter.println("ofMipmapType:" + this.ofMipmapType);
            printwriter.println("ofOcclusionFancy:" + this.ofOcclusionFancy);
            printwriter.println("ofSmoothFps:" + this.ofSmoothFps);
            printwriter.println("ofSmoothWorld:" + this.ofSmoothWorld);
            printwriter.println("ofAoLevel:" + this.ofAoLevel);
            printwriter.println("ofClouds:" + this.ofClouds);
            printwriter.println("ofCloudsHeight:" + this.ofCloudsHeight);
            printwriter.println("ofTrees:" + this.ofTrees);
            printwriter.println("ofDroppedItems:" + this.ofDroppedItems);
            printwriter.println("ofRain:" + this.ofRain);
            printwriter.println("ofAnimatedWater:" + this.ofAnimatedWater);
            printwriter.println("ofAnimatedLava:" + this.ofAnimatedLava);
            printwriter.println("ofAnimatedFire:" + this.ofAnimatedFire);
            printwriter.println("ofAnimatedPortal:" + this.ofAnimatedPortal);
            printwriter.println("ofAnimatedRedstone:" + this.ofAnimatedRedstone);
            printwriter.println("ofAnimatedExplosion:" + this.ofAnimatedExplosion);
            printwriter.println("ofAnimatedFlame:" + this.ofAnimatedFlame);
            printwriter.println("ofAnimatedSmoke:" + this.ofAnimatedSmoke);
            printwriter.println("ofVoidParticles:" + this.ofVoidParticles);
            printwriter.println("ofWaterParticles:" + this.ofWaterParticles);
            printwriter.println("ofPortalParticles:" + this.ofPortalParticles);
            printwriter.println("ofPotionParticles:" + this.ofPotionParticles);
            printwriter.println("ofFireworkParticles:" + this.ofFireworkParticles);
            printwriter.println("ofDrippingWaterLava:" + this.ofDrippingWaterLava);
            printwriter.println("ofAnimatedTerrain:" + this.ofAnimatedTerrain);
            printwriter.println("ofAnimatedTextures:" + this.ofAnimatedTextures);
            printwriter.println("ofRainSplash:" + this.ofRainSplash);
            printwriter.println("ofLagometer:" + this.ofLagometer);
            printwriter.println("ofShowFps:" + this.ofShowFps);
            printwriter.println("ofAutoSaveTicks:" + this.ofAutoSaveTicks);
            printwriter.println("ofBetterGrass:" + this.ofBetterGrass);
            printwriter.println("ofConnectedTextures:" + this.ofConnectedTextures);
            printwriter.println("ofWeather:" + this.ofWeather);
            printwriter.println("ofSky:" + this.ofSky);
            printwriter.println("ofStars:" + this.ofStars);
            printwriter.println("ofSunMoon:" + this.ofSunMoon);
            printwriter.println("ofVignette:" + this.ofVignette);
            printwriter.println("ofChunkUpdates:" + this.ofChunkUpdates);
            printwriter.println("ofChunkUpdatesDynamic:" + this.ofChunkUpdatesDynamic);
            printwriter.println("ofTime:" + this.ofTime);
            printwriter.println("ofAaLevel:" + this.ofAaLevel);
            printwriter.println("ofAfLevel:" + this.ofAfLevel);
            printwriter.println("ofProfiler:" + this.ofProfiler);
            printwriter.println("ofBetterSnow:" + this.ofBetterSnow);
            printwriter.println("ofSwampColors:" + this.ofSwampColors);
            printwriter.println("ofRandomEntities:" + this.ofRandomEntities);
            printwriter.println("ofCustomFonts:" + this.ofCustomFonts);
            printwriter.println("ofCustomColors:" + this.ofCustomColors);
            printwriter.println("ofCustomItems:" + this.ofCustomItems);
            printwriter.println("ofCustomSky:" + this.ofCustomSky);
            printwriter.println("ofShowCapes:" + this.ofShowCapes);
            printwriter.println("ofNaturalTextures:" + this.ofNaturalTextures);
            printwriter.println("ofEmissiveTextures:" + this.ofEmissiveTextures);
            printwriter.println("ofLazyChunkLoading:" + this.ofLazyChunkLoading);
            printwriter.println("ofRenderRegions:" + this.ofRenderRegions);
            printwriter.println("ofSmartAnimations:" + this.ofSmartAnimations);
            printwriter.println("ofDynamicFov:" + this.ofDynamicFov);
            printwriter.println("ofAlternateBlocks:" + this.ofAlternateBlocks);
            printwriter.println("ofDynamicLights:" + this.ofDynamicLights);
            printwriter.println("ofScreenshotSize:" + this.ofScreenshotSize);
            printwriter.println("ofCustomEntityModels:" + this.ofCustomEntityModels);
            printwriter.println("ofCustomGuis:" + this.ofCustomGuis);
            printwriter.println("ofShowGlErrors:" + this.ofShowGlErrors);
            printwriter.println("ofFastMath:" + this.ofFastMath);
            printwriter.println("ofFastRender:" + this.ofFastRender);
            printwriter.println("ofTranslucentBlocks:" + this.ofTranslucentBlocks);
            printwriter.println("ofChatBackground:" + this.ofChatBackground);
            printwriter.println("ofChatShadow:" + this.ofChatShadow);
            printwriter.println("key_" + this.ofKeyBindZoom.getKeyDescription() + ":" + this.ofKeyBindZoom.getTranslationKey());
            printwriter.close();
        }
        catch (Exception exception1)
        {
            Config.warn("Failed to save options");
            exception1.printStackTrace();
        }
    }

    public void updateRenderClouds()
    {
        switch (this.ofClouds)
        {
            case 1:
                this.cloudOption = CloudOption.FAST;
                break;

            case 2:
                this.cloudOption = CloudOption.FANCY;
                break;

            case 3:
                this.cloudOption = CloudOption.OFF;
                break;

            default:
                if (this.graphicFanciness != GraphicsFanciness.FAST)
                {
                    this.cloudOption = CloudOption.FANCY;
                }
                else
                {
                    this.cloudOption = CloudOption.FAST;
                }
        }

        if (this.graphicFanciness == GraphicsFanciness.FABULOUS)
        {
            WorldRenderer worldrenderer = Minecraft.getInstance().worldRenderer;

            if (worldrenderer != null)
            {
                Framebuffer framebuffer = worldrenderer.func_239232_u_();

                if (framebuffer != null)
                {
                    framebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
                }
            }
        }
    }

    public void resetSettings()
    {
        this.renderDistanceChunks = 8;
        this.entityDistanceScaling = 1.0F;
        this.viewBobbing = true;
        this.framerateLimit = (int)AbstractOption.FRAMERATE_LIMIT.getMaxValue();
        this.vsync = false;
        this.updateVSync();
        this.mipmapLevels = 4;
        this.graphicFanciness = GraphicsFanciness.FANCY;
        this.ambientOcclusionStatus = AmbientOcclusionStatus.MAX;
        this.cloudOption = CloudOption.FANCY;
        this.fov = 70.0D;
        this.gamma = 0.0D;
        this.guiScale = 0;
        this.particles = ParticleStatus.ALL;
        this.heldItemTooltips = true;
        this.forceUnicodeFont = false;
        this.ofFogType = 1;
        this.ofFogStart = 0.8F;
        this.ofMipmapType = 0;
        this.ofOcclusionFancy = false;
        this.ofSmartAnimations = false;
        this.ofSmoothFps = false;
        Config.updateAvailableProcessors();
        this.ofSmoothWorld = Config.isSingleProcessor();
        this.ofLazyChunkLoading = false;
        this.ofRenderRegions = false;
        this.ofFastMath = false;
        this.ofFastRender = false;
        this.ofTranslucentBlocks = 0;
        this.ofDynamicFov = true;
        this.ofAlternateBlocks = true;
        this.ofDynamicLights = 3;
        this.ofScreenshotSize = 1;
        this.ofCustomEntityModels = true;
        this.ofCustomGuis = true;
        this.ofShowGlErrors = true;
        this.ofChatBackground = 0;
        this.ofChatShadow = true;
        this.ofAoLevel = 1.0D;
        this.ofAaLevel = 0;
        this.ofAfLevel = 1;
        this.ofClouds = 0;
        this.ofCloudsHeight = 0.0D;
        this.ofTrees = 0;
        this.ofRain = 0;
        this.ofBetterGrass = 3;
        this.ofAutoSaveTicks = 4000;
        this.ofLagometer = false;
        this.ofShowFps = false;
        this.ofProfiler = false;
        this.ofWeather = true;
        this.ofSky = true;
        this.ofStars = true;
        this.ofSunMoon = true;
        this.ofVignette = 0;
        this.ofChunkUpdates = 1;
        this.ofChunkUpdatesDynamic = false;
        this.ofTime = 0;
        this.ofBetterSnow = false;
        this.ofSwampColors = true;
        this.ofRandomEntities = true;
        this.biomeBlendRadius = 2;
        this.ofCustomFonts = true;
        this.ofCustomColors = true;
        this.ofCustomItems = true;
        this.ofCustomSky = true;
        this.ofShowCapes = true;
        this.ofConnectedTextures = 2;
        this.ofNaturalTextures = false;
        this.ofEmissiveTextures = true;
        this.ofAnimatedWater = 0;
        this.ofAnimatedLava = 0;
        this.ofAnimatedFire = true;
        this.ofAnimatedPortal = true;
        this.ofAnimatedRedstone = true;
        this.ofAnimatedExplosion = true;
        this.ofAnimatedFlame = true;
        this.ofAnimatedSmoke = true;
        this.ofVoidParticles = true;
        this.ofWaterParticles = true;
        this.ofRainSplash = true;
        this.ofPortalParticles = true;
        this.ofPotionParticles = true;
        this.ofFireworkParticles = true;
        this.ofDrippingWaterLava = true;
        this.ofAnimatedTerrain = true;
        this.ofAnimatedTextures = true;
        Shaders.setShaderPack("OFF");
        Shaders.configAntialiasingLevel = 0;
        Shaders.uninit();
        Shaders.storeConfig();
        this.mc.scheduleResourcesRefresh();
        this.saveOptions();
    }

    public void updateVSync()
    {
        if (this.mc.getMainWindow() != null)
        {
            this.mc.getMainWindow().setVsync(this.vsync);
        }
    }

    public void updateMipmaps()
    {
        this.mc.setMipmapLevels(this.mipmapLevels);
        this.mc.scheduleResourcesRefresh();
    }

    public void setAllAnimations(boolean p_setAllAnimations_1_)
    {
        int i = p_setAllAnimations_1_ ? 0 : 2;
        this.ofAnimatedWater = i;
        this.ofAnimatedLava = i;
        this.ofAnimatedFire = p_setAllAnimations_1_;
        this.ofAnimatedPortal = p_setAllAnimations_1_;
        this.ofAnimatedRedstone = p_setAllAnimations_1_;
        this.ofAnimatedExplosion = p_setAllAnimations_1_;
        this.ofAnimatedFlame = p_setAllAnimations_1_;
        this.ofAnimatedSmoke = p_setAllAnimations_1_;
        this.ofVoidParticles = p_setAllAnimations_1_;
        this.ofWaterParticles = p_setAllAnimations_1_;
        this.ofRainSplash = p_setAllAnimations_1_;
        this.ofPortalParticles = p_setAllAnimations_1_;
        this.ofPotionParticles = p_setAllAnimations_1_;
        this.ofFireworkParticles = p_setAllAnimations_1_;
        this.particles = p_setAllAnimations_1_ ? ParticleStatus.ALL : ParticleStatus.MINIMAL;
        this.ofDrippingWaterLava = p_setAllAnimations_1_;
        this.ofAnimatedTerrain = p_setAllAnimations_1_;
        this.ofAnimatedTextures = p_setAllAnimations_1_;
    }

    private static int nextValue(int p_nextValue_0_, int[] p_nextValue_1_)
    {
        int i = indexOf(p_nextValue_0_, p_nextValue_1_);

        if (i < 0)
        {
            return p_nextValue_1_[0];
        }
        else
        {
            ++i;

            if (i >= p_nextValue_1_.length)
            {
                i = 0;
            }

            return p_nextValue_1_[i];
        }
    }

    private static int limit(int p_limit_0_, int[] p_limit_1_)
    {
        int i = indexOf(p_limit_0_, p_limit_1_);
        return i < 0 ? p_limit_1_[0] : p_limit_0_;
    }

    private static int indexOf(int p_indexOf_0_, int[] p_indexOf_1_)
    {
        for (int i = 0; i < p_indexOf_1_.length; ++i)
        {
            if (p_indexOf_1_[i] == p_indexOf_0_)
            {
                return i;
            }
        }

        return -1;
    }

    private static String getTranslation(String[] p_getTranslation_0_, int p_getTranslation_1_)
    {
        if (p_getTranslation_1_ < 0 || p_getTranslation_1_ >= p_getTranslation_0_.length)
        {
            p_getTranslation_1_ = 0;
        }

        return I18n.format(p_getTranslation_0_[p_getTranslation_1_]);
    }

    private void setForgeKeybindProperties()
    {
        if (Reflector.KeyConflictContext_IN_GAME.exists())
        {
            if (Reflector.ForgeKeyBinding_setKeyConflictContext.exists())
            {
                Object object = Reflector.getFieldValue(Reflector.KeyConflictContext_IN_GAME);
                Reflector.call(this.keyBindForward, Reflector.ForgeKeyBinding_setKeyConflictContext, object);
                Reflector.call(this.keyBindLeft, Reflector.ForgeKeyBinding_setKeyConflictContext, object);
                Reflector.call(this.keyBindBack, Reflector.ForgeKeyBinding_setKeyConflictContext, object);
                Reflector.call(this.keyBindRight, Reflector.ForgeKeyBinding_setKeyConflictContext, object);
                Reflector.call(this.keyBindJump, Reflector.ForgeKeyBinding_setKeyConflictContext, object);
                Reflector.call(this.keyBindSneak, Reflector.ForgeKeyBinding_setKeyConflictContext, object);
                Reflector.call(this.keyBindSprint, Reflector.ForgeKeyBinding_setKeyConflictContext, object);
                Reflector.call(this.keyBindAttack, Reflector.ForgeKeyBinding_setKeyConflictContext, object);
                Reflector.call(this.keyBindChat, Reflector.ForgeKeyBinding_setKeyConflictContext, object);
                Reflector.call(this.keyBindPlayerList, Reflector.ForgeKeyBinding_setKeyConflictContext, object);
                Reflector.call(this.keyBindCommand, Reflector.ForgeKeyBinding_setKeyConflictContext, object);
                Reflector.call(this.keyBindTogglePerspective, Reflector.ForgeKeyBinding_setKeyConflictContext, object);
                Reflector.call(this.keyBindSmoothCamera, Reflector.ForgeKeyBinding_setKeyConflictContext, object);
            }
        }
    }

    public void fillResourcePackList(ResourcePackList resourcePackListIn)
    {
        Set<String> set = Sets.newLinkedHashSet();
        Iterator<String> iterator = this.resourcePacks.iterator();

        while (iterator.hasNext())
        {
            String s = iterator.next();
            ResourcePackInfo resourcepackinfo = resourcePackListIn.getPackInfo(s);

            if (resourcepackinfo == null && !s.startsWith("file/"))
            {
                resourcepackinfo = resourcePackListIn.getPackInfo("file/" + s);
            }

            if (resourcepackinfo == null)
            {
                LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", (Object)s);
                iterator.remove();
            }
            else if (!resourcepackinfo.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(s))
            {
                LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", (Object)s);
                iterator.remove();
            }
            else if (resourcepackinfo.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(s))
            {
                LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", (Object)s);
                this.incompatibleResourcePacks.remove(s);
            }
            else
            {
                set.add(resourcepackinfo.getName());
            }
        }

        resourcePackListIn.setEnabledPacks(set);
    }

    public PointOfView getPointOfView()
    {
        return this.pointOfView;
    }

    public void setPointOfView(PointOfView pointOfView)
    {
        this.pointOfView = pointOfView;
    }
}
