package net.optifine;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ResourceLoadProgressGui;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.optifine.config.GlVersion;
import net.optifine.gui.GuiMessage;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.Shaders;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.TextureUtils;
import net.optifine.util.TimedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;

public class Config
{
    public static final String OF_NAME = "OptiFine";
    public static final String MC_VERSION = "1.16.5";
    public static final String OF_EDITION = "HD_U";
    public static final String OF_RELEASE = "G8";
    public static final String VERSION = "OptiFine_1.16.5_HD_U_G8";
    private static String build = null;
    private static String newRelease = null;
    private static boolean notify64BitJava = false;
    public static String openGlVersion = null;
    public static String openGlRenderer = null;
    public static String openGlVendor = null;
    public static String[] openGlExtensions = null;
    public static GlVersion glVersion = null;
    public static GlVersion glslVersion = null;
    public static int minecraftVersionInt = -1;
    public static boolean fancyFogAvailable = false;
    public static boolean occlusionAvailable = false;
    private static GameSettings gameSettings = null;
    private static Minecraft minecraft = Minecraft.getInstance();
    private static boolean initialized = false;
    private static Thread minecraftThread = null;
    private static int antialiasingLevel = 0;
    private static int availableProcessors = 0;
    public static boolean zoomMode = false;
    public static boolean zoomSmoothCamera = false;
    private static int texturePackClouds = 0;
    private static boolean fullscreenModeChecked = false;
    private static boolean desktopModeChecked = false;
    public static final Float DEF_ALPHA_FUNC_LEVEL = 0.1F;
    private static final Logger LOGGER = LogManager.getLogger();
    public static final boolean logDetail = System.getProperty("log.detail", "false").equals("true");
    private static String mcDebugLast = null;
    private static int fpsMinLast = 0;
    private static int chunkUpdatesLast = 0;
    private static AtlasTexture textureMapTerrain;
    private static long timeLastFrameMs;
    private static long averageFrameTimeMs;
    private static boolean showFrameTime = Boolean.getBoolean("frame.time");

    private Config()
    {
    }

    public static String getVersion()
    {
        return "OptiFine_1.16.5_HD_U_G8";
    }

    public static String getVersionDebug()
    {
        StringBuffer stringbuffer = new StringBuffer(32);

        if (isDynamicLights())
        {
            stringbuffer.append("DL: ");
            stringbuffer.append(String.valueOf(DynamicLights.getCount()));
            stringbuffer.append(", ");
        }

        stringbuffer.append("OptiFine_1.16.5_HD_U_G8");
        String s = Shaders.getShaderPackName();

        if (s != null)
        {
            stringbuffer.append(", ");
            stringbuffer.append(s);
        }

        return stringbuffer.toString();
    }

    public static void initGameSettings(GameSettings settings)
    {
        if (gameSettings == null)
        {
            gameSettings = settings;
            updateAvailableProcessors();
            ReflectorForge.putLaunchBlackboard("optifine.ForgeSplashCompatible", Boolean.TRUE);
            antialiasingLevel = gameSettings.ofAaLevel;
        }
    }

    public static void initDisplay()
    {
        checkInitialized();
        minecraftThread = Thread.currentThread();
        updateThreadPriorities();
        Shaders.startup(Minecraft.getInstance());
    }

    public static void checkInitialized()
    {
        if (!initialized)
        {
            if (Minecraft.getInstance().getMainWindow() != null)
            {
                initialized = true;
                checkOpenGlCaps();
                startVersionCheckThread();
            }
        }
    }

    private static void checkOpenGlCaps()
    {
        log("");
        log(getVersion());
        log("Build: " + getBuild());
        log("OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        log("Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        log("VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        log("LWJGL: " + GLFW.glfwGetVersionString());
        openGlVersion = GL11.glGetString(GL11.GL_VERSION);
        openGlRenderer = GL11.glGetString(GL11.GL_RENDERER);
        openGlVendor = GL11.glGetString(GL11.GL_VENDOR);
        log("OpenGL: " + openGlRenderer + ", version " + openGlVersion + ", " + openGlVendor);
        log("OpenGL Version: " + getOpenGlVersionString());
        GLCapabilities glcapabilities = GL.getCapabilities();

        if (!glcapabilities.OpenGL12)
        {
            log("OpenGL Mipmap levels: Not available (GL12.GL_TEXTURE_MAX_LEVEL)");
        }

        fancyFogAvailable = glcapabilities.GL_NV_fog_distance;

        if (!fancyFogAvailable)
        {
            log("OpenGL Fancy fog: Not available (GL_NV_fog_distance)");
        }

        occlusionAvailable = glcapabilities.GL_ARB_occlusion_query;

        if (!occlusionAvailable)
        {
            log("OpenGL Occlussion culling: Not available (GL_ARB_occlusion_query)");
        }

        int i = TextureUtils.getGLMaximumTextureSize();
        dbg("Maximum texture size: " + i + "x" + i);
    }

    public static String getBuild()
    {
        if (build == null)
        {
            try
            {
                InputStream inputstream = getOptiFineResourceStream("/buildof.txt");

                if (inputstream == null)
                {
                    return null;
                }

                build = readLines(inputstream)[0];
            }
            catch (Exception exception)
            {
                warn("" + exception.getClass().getName() + ": " + exception.getMessage());
                build = "";
            }
        }

        return build;
    }

    public static InputStream getOptiFineResourceStream(String name)
    {
        InputStream inputstream = ReflectorForge.getOptiFineResourceStream(name);
        return inputstream != null ? inputstream : Config.class.getResourceAsStream(name);
    }

    public static boolean isFancyFogAvailable()
    {
        return fancyFogAvailable;
    }

    public static boolean isOcclusionAvailable()
    {
        return occlusionAvailable;
    }

    public static int getMinecraftVersionInt()
    {
        if (minecraftVersionInt < 0)
        {
            String[] astring = tokenize("1.16.5", ".");
            int i = 0;

            if (astring.length > 0)
            {
                i += 10000 * parseInt(astring[0], 0);
            }

            if (astring.length > 1)
            {
                i += 100 * parseInt(astring[1], 0);
            }

            if (astring.length > 2)
            {
                i += 1 * parseInt(astring[2], 0);
            }

            minecraftVersionInt = i;
        }

        return minecraftVersionInt;
    }

    public static String getOpenGlVersionString()
    {
        GlVersion glversion = getGlVersion();
        return "" + glversion.getMajor() + "." + glversion.getMinor() + "." + glversion.getRelease();
    }

    private static GlVersion getGlVersionLwjgl()
    {
        GLCapabilities glcapabilities = GL.getCapabilities();

        if (glcapabilities.OpenGL44)
        {
            return new GlVersion(4, 4);
        }
        else if (glcapabilities.OpenGL43)
        {
            return new GlVersion(4, 3);
        }
        else if (glcapabilities.OpenGL42)
        {
            return new GlVersion(4, 2);
        }
        else if (glcapabilities.OpenGL41)
        {
            return new GlVersion(4, 1);
        }
        else if (glcapabilities.OpenGL40)
        {
            return new GlVersion(4, 0);
        }
        else if (glcapabilities.OpenGL33)
        {
            return new GlVersion(3, 3);
        }
        else if (glcapabilities.OpenGL32)
        {
            return new GlVersion(3, 2);
        }
        else if (glcapabilities.OpenGL31)
        {
            return new GlVersion(3, 1);
        }
        else if (glcapabilities.OpenGL30)
        {
            return new GlVersion(3, 0);
        }
        else if (glcapabilities.OpenGL21)
        {
            return new GlVersion(2, 1);
        }
        else if (glcapabilities.OpenGL20)
        {
            return new GlVersion(2, 0);
        }
        else if (glcapabilities.OpenGL15)
        {
            return new GlVersion(1, 5);
        }
        else if (glcapabilities.OpenGL14)
        {
            return new GlVersion(1, 4);
        }
        else if (glcapabilities.OpenGL13)
        {
            return new GlVersion(1, 3);
        }
        else if (glcapabilities.OpenGL12)
        {
            return new GlVersion(1, 2);
        }
        else
        {
            return glcapabilities.OpenGL11 ? new GlVersion(1, 1) : new GlVersion(1, 0);
        }
    }

    public static GlVersion getGlVersion()
    {
        if (glVersion == null)
        {
            String s = GL11.glGetString(GL11.GL_VERSION);
            glVersion = parseGlVersion(s, (GlVersion)null);

            if (glVersion == null)
            {
                glVersion = getGlVersionLwjgl();
            }

            if (glVersion == null)
            {
                glVersion = new GlVersion(1, 0);
            }
        }

        return glVersion;
    }

    public static GlVersion getGlslVersion()
    {
        if (glslVersion == null)
        {
            String s = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
            glslVersion = parseGlVersion(s, (GlVersion)null);

            if (glslVersion == null)
            {
                glslVersion = new GlVersion(1, 10);
            }
        }

        return glslVersion;
    }

    public static GlVersion parseGlVersion(String versionString, GlVersion def)
    {
        try
        {
            if (versionString == null)
            {
                return def;
            }
            else
            {
                Pattern pattern = Pattern.compile("([0-9]+)\\.([0-9]+)(\\.([0-9]+))?(.+)?");
                Matcher matcher = pattern.matcher(versionString);

                if (!matcher.matches())
                {
                    return def;
                }
                else
                {
                    int i = Integer.parseInt(matcher.group(1));
                    int j = Integer.parseInt(matcher.group(2));
                    int k = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
                    String s = matcher.group(5);
                    return new GlVersion(i, j, k, s);
                }
            }
        }
        catch (Exception exception)
        {
            error("", exception);
            return def;
        }
    }

    public static String[] getOpenGlExtensions()
    {
        if (openGlExtensions == null)
        {
            openGlExtensions = detectOpenGlExtensions();
        }

        return openGlExtensions;
    }

    private static String[] detectOpenGlExtensions()
    {
        try
        {
            GlVersion glversion = getGlVersion();

            if (glversion.getMajor() >= 3)
            {
                int i = GL11.glGetInteger(33309);

                if (i > 0)
                {
                    String[] astring = new String[i];

                    for (int j = 0; j < i; ++j)
                    {
                        astring[j] = GL30.glGetStringi(7939, j);
                    }

                    return astring;
                }
            }
        }
        catch (Exception exception1)
        {
            error("", exception1);
        }

        try
        {
            String s = GL11.glGetString(GL11.GL_EXTENSIONS);
            return s.split(" ");
        }
        catch (Exception exception)
        {
            error("", exception);
            return new String[0];
        }
    }

    public static void updateThreadPriorities()
    {
        updateAvailableProcessors();
        int i = 8;

        if (isSingleProcessor())
        {
            if (isSmoothWorld())
            {
                minecraftThread.setPriority(10);
                setThreadPriority("Server thread", 1);
            }
            else
            {
                minecraftThread.setPriority(5);
                setThreadPriority("Server thread", 5);
            }
        }
        else
        {
            minecraftThread.setPriority(10);
            setThreadPriority("Server thread", 5);
        }
    }

    private static void setThreadPriority(String prefix, int priority)
    {
        try
        {
            ThreadGroup threadgroup = Thread.currentThread().getThreadGroup();

            if (threadgroup == null)
            {
                return;
            }

            int i = (threadgroup.activeCount() + 10) * 2;
            Thread[] athread = new Thread[i];
            threadgroup.enumerate(athread, false);

            for (int j = 0; j < athread.length; ++j)
            {
                Thread thread = athread[j];

                if (thread != null && thread.getName().startsWith(prefix))
                {
                    thread.setPriority(priority);
                }
            }
        }
        catch (Throwable throwable)
        {
            warn(throwable.getClass().getName() + ": " + throwable.getMessage());
        }
    }

    public static boolean isMinecraftThread()
    {
        return Thread.currentThread() == minecraftThread;
    }

    private static void startVersionCheckThread()
    {
        VersionCheckThread versioncheckthread = new VersionCheckThread();
        versioncheckthread.start();
    }

    public static boolean isMipmaps()
    {
        return gameSettings.mipmapLevels > 0;
    }

    public static int getMipmapLevels()
    {
        return gameSettings.mipmapLevels;
    }

    public static int getMipmapType()
    {
        switch (gameSettings.ofMipmapType)
        {
            case 0:
                return 9986;

            case 1:
                return 9986;

            case 2:
                if (isMultiTexture())
                {
                    return 9985;
                }

                return 9986;

            case 3:
                if (isMultiTexture())
                {
                    return 9987;
                }

                return 9986;

            default:
                return 9986;
        }
    }

    public static boolean isUseAlphaFunc()
    {
        float f = getAlphaFuncLevel();
        return f > DEF_ALPHA_FUNC_LEVEL + 1.0E-5F;
    }

    public static float getAlphaFuncLevel()
    {
        return DEF_ALPHA_FUNC_LEVEL;
    }

    public static boolean isFogFancy()
    {
        if (!isFancyFogAvailable())
        {
            return false;
        }
        else
        {
            return gameSettings.ofFogType == 2;
        }
    }

    public static boolean isFogFast()
    {
        return gameSettings.ofFogType == 1;
    }

    public static boolean isFogOff()
    {
        return gameSettings.ofFogType == 3;
    }

    public static boolean isFogOn()
    {
        return gameSettings.ofFogType != 3;
    }

    public static float getFogStart()
    {
        return gameSettings.ofFogStart;
    }

    public static void detail(String s)
    {
        if (logDetail)
        {
            LOGGER.info("[OptiFine] " + s);
        }
    }

    public static void dbg(String s)
    {
        LOGGER.info("[OptiFine] " + s);
    }

    public static void warn(String s)
    {
        LOGGER.warn("[OptiFine] " + s);
    }

    public static void warn(String s, Throwable t)
    {
        LOGGER.warn("[OptiFine] " + s, t);
    }

    public static void error(String s)
    {
        LOGGER.error("[OptiFine] " + s);
    }

    public static void error(String s, Throwable t)
    {
        LOGGER.error("[OptiFine] " + s, t);
    }

    public static void log(String s)
    {
        dbg(s);
    }

    public static int getUpdatesPerFrame()
    {
        return gameSettings.ofChunkUpdates;
    }

    public static boolean isDynamicUpdates()
    {
        return gameSettings.ofChunkUpdatesDynamic;
    }

    public static boolean isGraphicsFancy()
    {
        return gameSettings.graphicFanciness != GraphicsFanciness.FAST;
    }

    public static boolean isGraphicsFabulous()
    {
        return gameSettings.graphicFanciness == GraphicsFanciness.FABULOUS;
    }

    public static boolean isRainFancy()
    {
        if (gameSettings.ofRain == 0)
        {
            return isGraphicsFancy();
        }
        else
        {
            return gameSettings.ofRain == 2;
        }
    }

    public static boolean isRainOff()
    {
        return gameSettings.ofRain == 3;
    }

    public static boolean isCloudsFancy()
    {
        if (gameSettings.ofClouds != 0)
        {
            return gameSettings.ofClouds == 2;
        }
        else if (isShaders() && !Shaders.shaderPackClouds.isDefault())
        {
            return Shaders.shaderPackClouds.isFancy();
        }
        else if (texturePackClouds != 0)
        {
            return texturePackClouds == 2;
        }
        else
        {
            return isGraphicsFancy();
        }
    }

    public static boolean isCloudsOff()
    {
        if (gameSettings.ofClouds != 0)
        {
            return gameSettings.ofClouds == 3;
        }
        else if (isShaders() && !Shaders.shaderPackClouds.isDefault())
        {
            return Shaders.shaderPackClouds.isOff();
        }
        else if (texturePackClouds != 0)
        {
            return texturePackClouds == 3;
        }
        else
        {
            return false;
        }
    }

    public static void updateTexturePackClouds()
    {
        texturePackClouds = 0;
        IResourceManager iresourcemanager = getResourceManager();

        if (iresourcemanager != null)
        {
            try
            {
                InputStream inputstream = iresourcemanager.getResource(new ResourceLocation("optifine/color.properties")).getInputStream();

                if (inputstream == null)
                {
                    return;
                }

                Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                String s = properties.getProperty("clouds");

                if (s == null)
                {
                    return;
                }

                dbg("Texture pack clouds: " + s);
                s = s.toLowerCase();

                if (s.equals("fast"))
                {
                    texturePackClouds = 1;
                }

                if (s.equals("fancy"))
                {
                    texturePackClouds = 2;
                }

                if (s.equals("off"))
                {
                    texturePackClouds = 3;
                }
            }
            catch (Exception exception)
            {
            }
        }
    }

    public static ModelManager getModelManager()
    {
        return minecraft.getItemRenderer().modelManager;
    }

    public static boolean isTreesFancy()
    {
        if (gameSettings.ofTrees == 0)
        {
            return isGraphicsFancy();
        }
        else
        {
            return gameSettings.ofTrees != 1;
        }
    }

    public static boolean isTreesSmart()
    {
        return gameSettings.ofTrees == 4;
    }

    public static boolean isCullFacesLeaves()
    {
        if (gameSettings.ofTrees == 0)
        {
            return !isGraphicsFancy();
        }
        else
        {
            return gameSettings.ofTrees == 4;
        }
    }

    public static boolean isDroppedItemsFancy()
    {
        if (gameSettings.ofDroppedItems == 0)
        {
            return isGraphicsFancy();
        }
        else
        {
            return gameSettings.ofDroppedItems == 2;
        }
    }

    public static int limit(int val, int min, int max)
    {
        if (val < min)
        {
            return min;
        }
        else
        {
            return val > max ? max : val;
        }
    }

    public static long limit(long val, long min, long max)
    {
        if (val < min)
        {
            return min;
        }
        else
        {
            return val > max ? max : val;
        }
    }

    public static float limit(float val, float min, float max)
    {
        if (val < min)
        {
            return min;
        }
        else
        {
            return val > max ? max : val;
        }
    }

    public static double limit(double val, double min, double max)
    {
        if (val < min)
        {
            return min;
        }
        else
        {
            return val > max ? max : val;
        }
    }

    public static float limitTo1(float val)
    {
        if (val < 0.0F)
        {
            return 0.0F;
        }
        else
        {
            return val > 1.0F ? 1.0F : val;
        }
    }

    public static boolean isAnimatedWater()
    {
        return gameSettings.ofAnimatedWater != 2;
    }

    public static boolean isGeneratedWater()
    {
        return gameSettings.ofAnimatedWater == 1;
    }

    public static boolean isAnimatedPortal()
    {
        return gameSettings.ofAnimatedPortal;
    }

    public static boolean isAnimatedLava()
    {
        return gameSettings.ofAnimatedLava != 2;
    }

    public static boolean isGeneratedLava()
    {
        return gameSettings.ofAnimatedLava == 1;
    }

    public static boolean isAnimatedFire()
    {
        return gameSettings.ofAnimatedFire;
    }

    public static boolean isAnimatedRedstone()
    {
        return gameSettings.ofAnimatedRedstone;
    }

    public static boolean isAnimatedExplosion()
    {
        return gameSettings.ofAnimatedExplosion;
    }

    public static boolean isAnimatedFlame()
    {
        return gameSettings.ofAnimatedFlame;
    }

    public static boolean isAnimatedSmoke()
    {
        return gameSettings.ofAnimatedSmoke;
    }

    public static boolean isVoidParticles()
    {
        return gameSettings.ofVoidParticles;
    }

    public static boolean isWaterParticles()
    {
        return gameSettings.ofWaterParticles;
    }

    public static boolean isRainSplash()
    {
        return gameSettings.ofRainSplash;
    }

    public static boolean isPortalParticles()
    {
        return gameSettings.ofPortalParticles;
    }

    public static boolean isPotionParticles()
    {
        return gameSettings.ofPotionParticles;
    }

    public static boolean isFireworkParticles()
    {
        return gameSettings.ofFireworkParticles;
    }

    public static float getAmbientOcclusionLevel()
    {
        return isShaders() && Shaders.aoLevel >= 0.0F ? Shaders.aoLevel : (float)gameSettings.ofAoLevel;
    }

    public static String listToString(List list)
    {
        return listToString(list, ", ");
    }

    public static String listToString(List list, String separator)
    {
        if (list == null)
        {
            return "";
        }
        else
        {
            StringBuffer stringbuffer = new StringBuffer(list.size() * 5);

            for (int i = 0; i < list.size(); ++i)
            {
                Object object = list.get(i);

                if (i > 0)
                {
                    stringbuffer.append(separator);
                }

                stringbuffer.append(String.valueOf(object));
            }

            return stringbuffer.toString();
        }
    }

    public static String arrayToString(Object[] arr)
    {
        return arrayToString(arr, ", ");
    }

    public static String arrayToString(Object[] arr, String separator)
    {
        if (arr == null)
        {
            return "";
        }
        else
        {
            StringBuffer stringbuffer = new StringBuffer(arr.length * 5);

            for (int i = 0; i < arr.length; ++i)
            {
                Object object = arr[i];

                if (i > 0)
                {
                    stringbuffer.append(separator);
                }

                stringbuffer.append(String.valueOf(object));
            }

            return stringbuffer.toString();
        }
    }

    public static String arrayToString(int[] arr)
    {
        return arrayToString(arr, ", ");
    }

    public static String arrayToString(int[] arr, String separator)
    {
        if (arr == null)
        {
            return "";
        }
        else
        {
            StringBuffer stringbuffer = new StringBuffer(arr.length * 5);

            for (int i = 0; i < arr.length; ++i)
            {
                int j = arr[i];

                if (i > 0)
                {
                    stringbuffer.append(separator);
                }

                stringbuffer.append(String.valueOf(j));
            }

            return stringbuffer.toString();
        }
    }

    public static String arrayToString(float[] arr)
    {
        return arrayToString(arr, ", ");
    }

    public static String arrayToString(float[] arr, String separator)
    {
        if (arr == null)
        {
            return "";
        }
        else
        {
            StringBuffer stringbuffer = new StringBuffer(arr.length * 5);

            for (int i = 0; i < arr.length; ++i)
            {
                float f = arr[i];

                if (i > 0)
                {
                    stringbuffer.append(separator);
                }

                stringbuffer.append(String.valueOf(f));
            }

            return stringbuffer.toString();
        }
    }

    public static Minecraft getMinecraft()
    {
        return minecraft;
    }

    public static TextureManager getTextureManager()
    {
        return minecraft.getTextureManager();
    }

    public static IResourceManager getResourceManager()
    {
        return minecraft.getResourceManager();
    }

    public static InputStream getResourceStream(ResourceLocation location) throws IOException
    {
        return getResourceStream(minecraft.getResourceManager(), location);
    }

    public static InputStream getResourceStream(IResourceManager resourceManager, ResourceLocation location) throws IOException
    {
        IResource iresource = resourceManager.getResource(location);
        return iresource == null ? null : iresource.getInputStream();
    }

    public static IResource getResource(ResourceLocation location) throws IOException
    {
        return minecraft.getResourceManager().getResource(location);
    }

    public static boolean hasResource(ResourceLocation location)
    {
        if (location == null)
        {
            return false;
        }
        else
        {
            IResourcePack iresourcepack = getDefiningResourcePack(location);
            return iresourcepack != null;
        }
    }

    public static boolean hasResource(IResourceManager resourceManager, ResourceLocation location)
    {
        try
        {
            IResource iresource = resourceManager.getResource(location);
            return iresource != null;
        }
        catch (IOException ioexception)
        {
            return false;
        }
    }

    public static boolean hasResource(IResourcePack rp, ResourceLocation loc)
    {
        return rp != null && loc != null ? rp.resourceExists(ResourcePackType.CLIENT_RESOURCES, loc) : false;
    }

    public static IResourcePack[] getResourcePacks()
    {
        ResourcePackList resourcepacklist = minecraft.getResourcePackList();
        Collection<ResourcePackInfo> collection = resourcepacklist.getEnabledPacks();
        List list = new ArrayList();

        for (ResourcePackInfo resourcepackinfo : collection)
        {
            IResourcePack iresourcepack = resourcepackinfo.getResourcePack();

            if (iresourcepack != getDefaultResourcePack())
            {
                list.add(iresourcepack);
            }
        }

        IResourcePack[] airesourcepack = (IResourcePack[]) list.toArray(new IResourcePack[list.size()]);
        return airesourcepack;
    }

    public static String getResourcePackNames()
    {
        if (minecraft.getResourceManager() == null)
        {
            return "";
        }
        else
        {
            IResourcePack[] airesourcepack = getResourcePacks();

            if (airesourcepack.length <= 0)
            {
                return getDefaultResourcePack().getName();
            }
            else
            {
                String[] astring = new String[airesourcepack.length];

                for (int i = 0; i < airesourcepack.length; ++i)
                {
                    astring[i] = airesourcepack[i].getName();
                }

                return arrayToString((Object[])astring);
            }
        }
    }

    public static VanillaPack getDefaultResourcePack()
    {
        return minecraft.getPackFinder().getVanillaPack();
    }

    public static boolean isFromDefaultResourcePack(ResourceLocation loc)
    {
        return getDefiningResourcePack(loc) == getDefaultResourcePack();
    }

    public static IResourcePack getDefiningResourcePack(ResourceLocation location)
    {
        ResourcePackList resourcepacklist = minecraft.getResourcePackList();
        Collection<ResourcePackInfo> collection = resourcepacklist.getEnabledPacks();
        List<ResourcePackInfo> list = (List)collection;

        for (int i = list.size() - 1; i >= 0; --i)
        {
            ResourcePackInfo resourcepackinfo = list.get(i);
            IResourcePack iresourcepack = resourcepackinfo.getResourcePack();

            if (iresourcepack.resourceExists(ResourcePackType.CLIENT_RESOURCES, location))
            {
                return iresourcepack;
            }
        }

        return null;
    }

    public static WorldRenderer getRenderGlobal()
    {
        return minecraft.worldRenderer;
    }

    public static WorldRenderer getWorldRenderer()
    {
        return minecraft.worldRenderer;
    }

    public static GameRenderer getGameRenderer()
    {
        return minecraft.gameRenderer;
    }

    public static boolean isBetterGrass()
    {
        return gameSettings.ofBetterGrass != 3;
    }

    public static boolean isBetterGrassFancy()
    {
        return gameSettings.ofBetterGrass == 2;
    }

    public static boolean isWeatherEnabled()
    {
        return gameSettings.ofWeather;
    }

    public static boolean isSkyEnabled()
    {
        return gameSettings.ofSky;
    }

    public static boolean isSunMoonEnabled()
    {
        return gameSettings.ofSunMoon;
    }

    public static boolean isSunTexture()
    {
        if (!isSunMoonEnabled())
        {
            return false;
        }
        else
        {
            return !isShaders() || Shaders.isSun();
        }
    }

    public static boolean isMoonTexture()
    {
        if (!isSunMoonEnabled())
        {
            return false;
        }
        else
        {
            return !isShaders() || Shaders.isMoon();
        }
    }

    public static boolean isVignetteEnabled()
    {
        if (isShaders() && !Shaders.isVignette())
        {
            return false;
        }
        else if (gameSettings.ofVignette == 0)
        {
            return isGraphicsFancy();
        }
        else
        {
            return gameSettings.ofVignette == 2;
        }
    }

    public static boolean isStarsEnabled()
    {
        return gameSettings.ofStars;
    }

    public static void sleep(long ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException interruptedexception)
        {
            error("", interruptedexception);
        }
    }

    public static boolean isTimeDayOnly()
    {
        return gameSettings.ofTime == 1;
    }

    public static boolean isTimeDefault()
    {
        return gameSettings.ofTime == 0;
    }

    public static boolean isTimeNightOnly()
    {
        return gameSettings.ofTime == 2;
    }

    public static int getAnisotropicFilterLevel()
    {
        return gameSettings.ofAfLevel;
    }

    public static boolean isAnisotropicFiltering()
    {
        return getAnisotropicFilterLevel() > 1;
    }

    public static int getAntialiasingLevel()
    {
        return antialiasingLevel;
    }

    public static boolean isAntialiasing()
    {
        return getAntialiasingLevel() > 0;
    }

    public static boolean isAntialiasingConfigured()
    {
        return getGameSettings().ofAaLevel > 0;
    }

    public static boolean isMultiTexture()
    {
        if (getAnisotropicFilterLevel() > 1)
        {
            return true;
        }
        else
        {
            return getAntialiasingLevel() > 0;
        }
    }

    public static boolean between(int val, int min, int max)
    {
        return val >= min && val <= max;
    }

    public static boolean between(float val, float min, float max)
    {
        return val >= min && val <= max;
    }

    public static boolean between(double val, double min, double max)
    {
        return val >= min && val <= max;
    }

    public static boolean isDrippingWaterLava()
    {
        return gameSettings.ofDrippingWaterLava;
    }

    public static boolean isBetterSnow()
    {
        return gameSettings.ofBetterSnow;
    }

    public static int parseInt(String str, int defVal)
    {
        try
        {
            if (str == null)
            {
                return defVal;
            }
            else
            {
                str = str.trim();
                return Integer.parseInt(str);
            }
        }
        catch (NumberFormatException numberformatexception)
        {
            return defVal;
        }
    }

    public static int parseHexInt(String str, int defVal)
    {
        try
        {
            if (str == null)
            {
                return defVal;
            }
            else
            {
                str = str.trim();

                if (str.startsWith("0x"))
                {
                    str = str.substring(2);
                }

                return Integer.parseInt(str, 16);
            }
        }
        catch (NumberFormatException numberformatexception)
        {
            return defVal;
        }
    }

    public static float parseFloat(String str, float defVal)
    {
        try
        {
            if (str == null)
            {
                return defVal;
            }
            else
            {
                str = str.trim();
                return Float.parseFloat(str);
            }
        }
        catch (NumberFormatException numberformatexception)
        {
            return defVal;
        }
    }

    public static boolean parseBoolean(String str, boolean defVal)
    {
        try
        {
            if (str == null)
            {
                return defVal;
            }
            else
            {
                str = str.trim();
                return Boolean.parseBoolean(str);
            }
        }
        catch (NumberFormatException numberformatexception)
        {
            return defVal;
        }
    }

    public static Boolean parseBoolean(String str, Boolean defVal)
    {
        try
        {
            if (str == null)
            {
                return defVal;
            }
            else
            {
                str = str.trim().toLowerCase();

                if (str.equals("true"))
                {
                    return Boolean.TRUE;
                }
                else
                {
                    return str.equals("false") ? Boolean.FALSE : defVal;
                }
            }
        }
        catch (NumberFormatException numberformatexception)
        {
            return defVal;
        }
    }

    public static String[] tokenize(String str, String delim)
    {
        StringTokenizer stringtokenizer = new StringTokenizer(str, delim);
        List list = new ArrayList();

        while (stringtokenizer.hasMoreTokens())
        {
            String s = stringtokenizer.nextToken();
            list.add(s);
        }

        String[] astring = (String[]) list.toArray(new String[list.size()]);
        return astring;
    }

    public static boolean isAnimatedTerrain()
    {
        return gameSettings.ofAnimatedTerrain;
    }

    public static boolean isAnimatedTextures()
    {
        return gameSettings.ofAnimatedTextures;
    }

    public static boolean isSwampColors()
    {
        return gameSettings.ofSwampColors;
    }

    public static boolean isRandomEntities()
    {
        return gameSettings.ofRandomEntities;
    }

    public static void checkGlError(String loc)
    {
        int i = GlStateManager.getError();

        if (i != 0 && GlErrors.isEnabled(i))
        {
            String s = getGlErrorString(i);
            String s1 = String.format("OpenGL error: %s (%s), at: %s", i, s, loc);
            error(s1);

            if (isShowGlErrors() && TimedEvent.isActive("ShowGlError", 10000L))
            {
                String s2 = I18n.format("of.message.openglError", i, s);
                minecraft.ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(s2));
            }
        }
    }

    public static boolean isSmoothBiomes()
    {
        return gameSettings.biomeBlendRadius > 0;
    }

    public static int getBiomeBlendRadius()
    {
        return gameSettings.biomeBlendRadius;
    }

    public static boolean isCustomColors()
    {
        return gameSettings.ofCustomColors;
    }

    public static boolean isCustomSky()
    {
        return gameSettings.ofCustomSky;
    }

    public static boolean isCustomFonts()
    {
        return gameSettings.ofCustomFonts;
    }

    public static boolean isShowCapes()
    {
        return gameSettings.ofShowCapes;
    }

    public static boolean isConnectedTextures()
    {
        return gameSettings.ofConnectedTextures != 3;
    }

    public static boolean isNaturalTextures()
    {
        return gameSettings.ofNaturalTextures;
    }

    public static boolean isEmissiveTextures()
    {
        return gameSettings.ofEmissiveTextures;
    }

    public static boolean isConnectedTexturesFancy()
    {
        return gameSettings.ofConnectedTextures == 2;
    }

    public static boolean isFastRender()
    {
        return gameSettings.ofFastRender;
    }

    public static boolean isTranslucentBlocksFancy()
    {
        if (gameSettings.ofTranslucentBlocks == 0)
        {
            return isGraphicsFancy();
        }
        else
        {
            return gameSettings.ofTranslucentBlocks == 2;
        }
    }

    public static boolean isShaders()
    {
        return Shaders.shaderPackLoaded;
    }

    public static String[] readLines(File file) throws IOException
    {
        FileInputStream fileinputstream = new FileInputStream(file);
        return readLines(fileinputstream);
    }

    public static String[] readLines(InputStream is) throws IOException
    {
        List list = new ArrayList();
        InputStreamReader inputstreamreader = new InputStreamReader(is, "ASCII");
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

        while (true)
        {
            String s = bufferedreader.readLine();

            if (s == null)
            {
                return (String[]) list.toArray(new String[list.size()]);
            }

            list.add(s);
        }
    }

    public static String readFile(File file) throws IOException
    {
        FileInputStream fileinputstream = new FileInputStream(file);
        return readInputStream(fileinputstream, "ASCII");
    }

    public static String readInputStream(InputStream in) throws IOException
    {
        return readInputStream(in, "ASCII");
    }

    public static String readInputStream(InputStream in, String encoding) throws IOException
    {
        InputStreamReader inputstreamreader = new InputStreamReader(in, encoding);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        StringBuffer stringbuffer = new StringBuffer();

        while (true)
        {
            String s = bufferedreader.readLine();

            if (s == null)
            {
                in.close();
                return stringbuffer.toString();
            }

            stringbuffer.append(s);
            stringbuffer.append("\n");
        }
    }

    public static byte[] readAll(InputStream is) throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        byte[] abyte = new byte[1024];

        while (true)
        {
            int i = is.read(abyte);

            if (i < 0)
            {
                is.close();
                return bytearrayoutputstream.toByteArray();
            }

            bytearrayoutputstream.write(abyte, 0, i);
        }
    }

    public static GameSettings getGameSettings()
    {
        return gameSettings;
    }

    public static String getNewRelease()
    {
        return newRelease;
    }

    public static void setNewRelease(String newRelease)
    {
        Config.newRelease = newRelease;
    }

    public static int compareRelease(String rel1, String rel2)
    {
        String[] astring = splitRelease(rel1);
        String[] astring1 = splitRelease(rel2);
        String s = astring[0];
        String s1 = astring1[0];

        if (!s.equals(s1))
        {
            return s.compareTo(s1);
        }
        else
        {
            int i = parseInt(astring[1], -1);
            int j = parseInt(astring1[1], -1);

            if (i != j)
            {
                return i - j;
            }
            else
            {
                String s2 = astring[2];
                String s3 = astring1[2];

                if (!s2.equals(s3))
                {
                    if (s2.isEmpty())
                    {
                        return 1;
                    }

                    if (s3.isEmpty())
                    {
                        return -1;
                    }
                }

                return s2.compareTo(s3);
            }
        }
    }

    private static String[] splitRelease(String relStr)
    {
        if (relStr != null && relStr.length() > 0)
        {
            Pattern pattern = Pattern.compile("([A-Z])([0-9]+)(.*)");
            Matcher matcher = pattern.matcher(relStr);

            if (!matcher.matches())
            {
                return new String[] {"", "", ""};
            }
            else
            {
                String s = normalize(matcher.group(1));
                String s1 = normalize(matcher.group(2));
                String s2 = normalize(matcher.group(3));
                return new String[] {s, s1, s2};
            }
        }
        else
        {
            return new String[] {"", "", ""};
        }
    }

    public static int intHash(int x)
    {
        x = x ^ 61 ^ x >> 16;
        x = x + (x << 3);
        x = x ^ x >> 4;
        x = x * 668265261;
        return x ^ x >> 15;
    }

    public static int getRandom(BlockPos blockPos, int face)
    {
        int i = intHash(face + 37);
        i = intHash(i + blockPos.getX());
        i = intHash(i + blockPos.getZ());
        return intHash(i + blockPos.getY());
    }

    public static int getAvailableProcessors()
    {
        return availableProcessors;
    }

    public static void updateAvailableProcessors()
    {
        availableProcessors = Runtime.getRuntime().availableProcessors();
    }

    public static boolean isSingleProcessor()
    {
        return getAvailableProcessors() <= 1;
    }

    public static boolean isSmoothWorld()
    {
        return gameSettings.ofSmoothWorld;
    }

    public static boolean isLazyChunkLoading()
    {
        return gameSettings.ofLazyChunkLoading;
    }

    public static boolean isDynamicFov()
    {
        return gameSettings.ofDynamicFov;
    }

    public static boolean isAlternateBlocks()
    {
        return gameSettings.ofAlternateBlocks;
    }

    public static int getChunkViewDistance()
    {
        return gameSettings == null ? 10 : gameSettings.renderDistanceChunks;
    }

    public static boolean equals(Object o1, Object o2)
    {
        if (o1 == o2)
        {
            return true;
        }
        else
        {
            return o1 == null ? false : o1.equals(o2);
        }
    }

    public static boolean equalsOne(Object a, Object[] bs)
    {
        if (bs == null)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < bs.length; ++i)
            {
                Object object = bs[i];

                if (equals(a, object))
                {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean equalsOne(int val, int[] vals)
    {
        for (int i = 0; i < vals.length; ++i)
        {
            if (vals[i] == val)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isSameOne(Object a, Object[] bs)
    {
        if (bs == null)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < bs.length; ++i)
            {
                Object object = bs[i];

                if (a == object)
                {
                    return true;
                }
            }

            return false;
        }
    }

    public static String normalize(String s)
    {
        return s == null ? "" : s;
    }

    private static ByteBuffer readIconImage(InputStream is) throws IOException
    {
        BufferedImage bufferedimage = ImageIO.read(is);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), (int[])null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);

        for (int i : aint)
        {
            bytebuffer.putInt(i << 8 | i >> 24 & 255);
        }

        ((Buffer)bytebuffer).flip();
        return bytebuffer;
    }

    public static Object[] addObjectToArray(Object[] arr, Object obj)
    {
        if (arr == null)
        {
            throw new NullPointerException("The given array is NULL");
        }
        else
        {
            int i = arr.length;
            int j = i + 1;
            Object[] aobject = (Object[]) Array.newInstance(arr.getClass().getComponentType(), j);
            System.arraycopy(arr, 0, aobject, 0, i);
            aobject[i] = obj;
            return aobject;
        }
    }

    public static Object[] addObjectToArray(Object[] arr, Object obj, int index)
    {
        List list = new ArrayList<>(Arrays.asList(arr));
        list.add(index, obj);
        Object[] aobject = (Object[]) Array.newInstance(arr.getClass().getComponentType(), list.size());
        return list.toArray(aobject);
    }

    public static Object[] addObjectsToArray(Object[] arr, Object[] objs)
    {
        if (arr == null)
        {
            throw new NullPointerException("The given array is NULL");
        }
        else if (objs.length == 0)
        {
            return arr;
        }
        else
        {
            int i = arr.length;
            int j = i + objs.length;
            Object[] aobject = (Object[]) Array.newInstance(arr.getClass().getComponentType(), j);
            System.arraycopy(arr, 0, aobject, 0, i);
            System.arraycopy(objs, 0, aobject, i, objs.length);
            return aobject;
        }
    }

    public static Object[] removeObjectFromArray(Object[] arr, Object obj)
    {
        List list = new ArrayList<>(Arrays.asList(arr));
        list.remove(obj);
        return collectionToArray(list, arr.getClass().getComponentType());
    }

    public static Object[] collectionToArray(Collection coll, Class elementClass)
    {
        if (coll == null)
        {
            return null;
        }
        else if (elementClass == null)
        {
            return null;
        }
        else if (elementClass.isPrimitive())
        {
            throw new IllegalArgumentException("Can not make arrays with primitive elements (int, double), element class: " + elementClass);
        }
        else
        {
            Object[] aobject = (Object[]) Array.newInstance(elementClass, coll.size());
            return coll.toArray(aobject);
        }
    }

    public static boolean isCustomItems()
    {
        return gameSettings.ofCustomItems;
    }

    public static void drawFps(MatrixStack matrixStackIn)
    {
        int i = getChunkUpdates();
        int j = minecraft.worldRenderer.getCountActiveRenderers();
        int k = minecraft.worldRenderer.getCountEntitiesRendered();
        int l = minecraft.worldRenderer.getCountTileEntitiesRendered();
        String s = getFpsString() + ", C: " + j + ", E: " + k + "+" + l + ", U: " + i;
        minecraft.fontRenderer.drawString(matrixStackIn, s, 2.0F, 2.0F, -2039584);
    }

    public static String getFpsString()
    {
        int i = getFpsAverage();
        int j = getFpsMin();

        if (showFrameTime)
        {
            String s1 = String.format("%.1f", 1000.0D / (double)limit(i, 1, Integer.MAX_VALUE));
            String s = String.format("%.1f", 1000.0D / (double)limit(j, 1, Integer.MAX_VALUE));
            return "" + s1 + "/" + s + " ms";
        }
        else
        {
            return "" + i + "/" + j + " fps";
        }
    }

    public static boolean isShowFrameTime()
    {
        return showFrameTime;
    }

    public static int getFpsAverage()
    {
        return Reflector.getFieldValueInt(Reflector.Minecraft_debugFPS, -1);
    }

    public static int getFpsMin()
    {
        return fpsMinLast;
    }

    public static int getChunkUpdates()
    {
        return chunkUpdatesLast;
    }

    public static void updateFpsMin()
    {
        FrameTimer frametimer = minecraft.getFrameTimer();
        long[] along = frametimer.getFrames();
        int i = frametimer.getIndex();
        int j = frametimer.getLastIndex();

        if (i != j)
        {
            int k = Reflector.getFieldValueInt(Reflector.Minecraft_debugFPS, -1);

            if (k <= 0)
            {
                k = 1;
            }

            long l = (long)(1.0D / (double)k * 1.0E9D);
            long i1 = l;
            long j1 = 0L;

            for (int k1 = MathHelper.normalizeAngle(i - 1, along.length); k1 != j && (double)j1 < 1.0E9D; k1 = MathHelper.normalizeAngle(k1 - 1, along.length))
            {
                long l1 = along[k1];

                if (l1 > i1)
                {
                    i1 = l1;
                }

                j1 += l1;
            }

            double d0 = (double)i1 / 1.0E9D;
            fpsMinLast = (int)(1.0D / d0);
        }
    }

    private static void updateChunkUpdates()
    {
        chunkUpdatesLast = ChunkRenderDispatcher.renderChunksUpdated;
        ChunkRenderDispatcher.renderChunksUpdated = 0;
    }

    public static int getBitsOs()
    {
        String s = System.getenv("ProgramFiles(X86)");
        return s != null ? 64 : 32;
    }

    public static int getBitsJre()
    {
        String[] astring = new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

        for (int i = 0; i < astring.length; ++i)
        {
            String s = astring[i];
            String s1 = System.getProperty(s);

            if (s1 != null && s1.contains("64"))
            {
                return 64;
            }
        }

        return 32;
    }

    public static boolean isNotify64BitJava()
    {
        return notify64BitJava;
    }

    public static void setNotify64BitJava(boolean flag)
    {
        notify64BitJava = flag;
    }

    public static boolean isConnectedModels()
    {
        return false;
    }

    public static void showGuiMessage(String line1, String line2)
    {
        GuiMessage guimessage = new GuiMessage(minecraft.currentScreen, line1, line2);
        minecraft.displayGuiScreen(guimessage);
    }

    public static int[] addIntToArray(int[] intArray, int intValue)
    {
        return addIntsToArray(intArray, new int[] {intValue});
    }

    public static int[] addIntsToArray(int[] intArray, int[] copyFrom)
    {
        if (intArray != null && copyFrom != null)
        {
            int i = intArray.length;
            int j = i + copyFrom.length;
            int[] aint = new int[j];
            System.arraycopy(intArray, 0, aint, 0, i);

            for (int k = 0; k < copyFrom.length; ++k)
            {
                aint[k + i] = copyFrom[k];
            }

            return aint;
        }
        else
        {
            throw new NullPointerException("The given array is NULL");
        }
    }

    public static void writeFile(File file, String str) throws IOException
    {
        FileOutputStream fileoutputstream = new FileOutputStream(file);
        byte[] abyte = str.getBytes("ASCII");
        fileoutputstream.write(abyte);
        fileoutputstream.close();
    }

    public static void setTextureMap(AtlasTexture textureMapTerrain)
    {
        Config.textureMapTerrain = textureMapTerrain;
    }

    public static AtlasTexture getTextureMap()
    {
        return textureMapTerrain;
    }

    public static boolean isDynamicLights()
    {
        return gameSettings.ofDynamicLights != 3;
    }

    public static boolean isDynamicLightsFast()
    {
        return gameSettings.ofDynamicLights == 1;
    }

    public static boolean isDynamicHandLight()
    {
        if (!isDynamicLights())
        {
            return false;
        }
        else
        {
            return isShaders() ? Shaders.isDynamicHandLight() : true;
        }
    }

    public static boolean isCustomEntityModels()
    {
        return gameSettings.ofCustomEntityModels;
    }

    public static boolean isCustomGuis()
    {
        return gameSettings.ofCustomGuis;
    }

    public static int getScreenshotSize()
    {
        return gameSettings.ofScreenshotSize;
    }

    public static int[] toPrimitive(Integer[] arr)
    {
        if (arr == null)
        {
            return null;
        }
        else if (arr.length == 0)
        {
            return new int[0];
        }
        else
        {
            int[] aint = new int[arr.length];

            for (int i = 0; i < aint.length; ++i)
            {
                aint[i] = arr[i];
            }

            return aint;
        }
    }

    public static boolean isRenderRegions()
    {
        if (isMultiTexture())
        {
            return false;
        }
        else
        {
            return gameSettings.ofRenderRegions && GlStateManager.vboRegions;
        }
    }

    public static boolean isVbo()
    {
        return GLX.useVbo();
    }

    public static boolean isSmoothFps()
    {
        return gameSettings.ofSmoothFps;
    }

    public static boolean openWebLink(URI uri)
    {
        Util.setExceptionOpenUrl((Exception)null);
        Util.getOSType().openURI(uri);
        Exception exception = Util.getExceptionOpenUrl();
        return exception == null;
    }

    public static boolean isShowGlErrors()
    {
        return gameSettings.ofShowGlErrors;
    }

    public static String arrayToString(boolean[] arr, String separator)
    {
        if (arr == null)
        {
            return "";
        }
        else
        {
            StringBuffer stringbuffer = new StringBuffer(arr.length * 5);

            for (int i = 0; i < arr.length; ++i)
            {
                boolean flag = arr[i];

                if (i > 0)
                {
                    stringbuffer.append(separator);
                }

                stringbuffer.append(String.valueOf(flag));
            }

            return stringbuffer.toString();
        }
    }

    public static boolean isIntegratedServerRunning()
    {
        if (minecraft.getIntegratedServer() == null)
        {
            return false;
        }
        else
        {
            return minecraft.isIntegratedServerRunning();
        }
    }

    public static IntBuffer createDirectIntBuffer(int capacity)
    {
        return GLAllocation.createDirectByteBuffer(capacity << 2).asIntBuffer();
    }

    public static String getGlErrorString(int err)
    {
        switch (err)
        {
            case 0:
                return "No error";

            case 1280:
                return "Invalid enum";

            case 1281:
                return "Invalid value";

            case 1282:
                return "Invalid operation";

            case 1283:
                return "Stack overflow";

            case 1284:
                return "Stack underflow";

            case 1285:
                return "Out of memory";

            case 1286:
                return "Invalid framebuffer operation";

            default:
                return "Unknown";
        }
    }

    public static boolean isKeyDown(int key)
    {
        return GLFW.glfwGetKey(minecraft.getMainWindow().getHandle(), key) == 1;
    }

    public static boolean isTrue(Boolean val)
    {
        return val != null && val;
    }

    public static boolean isReloadingResources()
    {
        if (minecraft.loadingGui == null)
        {
            return false;
        }
        else
        {
            if (minecraft.loadingGui instanceof ResourceLoadProgressGui)
            {
                ResourceLoadProgressGui resourceloadprogressgui = (ResourceLoadProgressGui)minecraft.loadingGui;

                if (resourceloadprogressgui.isFadeOut())
                {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isQuadsToTriangles()
    {
        if (!isShaders())
        {
            return false;
        }
        else
        {
            return !Shaders.canRenderQuads();
        }
    }

    public static void frameStart()
    {
        long i = System.currentTimeMillis();
        long j = i - timeLastFrameMs;
        timeLastFrameMs = i;
        j = limit(j, 1L, 1000L);
        averageFrameTimeMs = (averageFrameTimeMs + j) / 2L;
        averageFrameTimeMs = limit(averageFrameTimeMs, 1L, 1000L);

        if (minecraft.debug != mcDebugLast)
        {
            mcDebugLast = minecraft.debug;
            updateFpsMin();
            updateChunkUpdates();
        }
    }

    public static long getAverageFrameTimeMs()
    {
        return averageFrameTimeMs;
    }

    public static float getAverageFrameTimeSec()
    {
        return (float)getAverageFrameTimeMs() / 1000.0F;
    }

    public static long getAverageFrameFps()
    {
        return 1000L / getAverageFrameTimeMs();
    }

    public static void checkNull(Object obj, String msg) throws NullPointerException
    {
        if (obj == null)
        {
            throw new NullPointerException(msg);
        }
    }
}
