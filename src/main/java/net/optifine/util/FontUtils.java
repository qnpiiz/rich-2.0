package net.optifine.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.fonts.FontResourceManager;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class FontUtils
{
    public static Properties readFontProperties(ResourceLocation locationFontTexture)
    {
        String s = locationFontTexture.getPath();
        Properties properties = new PropertiesOrdered();
        String s1 = ".png";

        if (!s.endsWith(s1))
        {
            return properties;
        }
        else
        {
            String s2 = s.substring(0, s.length() - s1.length()) + ".properties";

            try
            {
                ResourceLocation resourcelocation = new ResourceLocation(locationFontTexture.getNamespace(), s2);
                InputStream inputstream = Config.getResourceStream(Config.getResourceManager(), resourcelocation);

                if (inputstream == null)
                {
                    return properties;
                }

                Config.log("Loading " + s2);
                properties.load(inputstream);
                inputstream.close();
            }
            catch (FileNotFoundException filenotfoundexception)
            {
            }
            catch (IOException ioexception)
            {
                ioexception.printStackTrace();
            }

            return properties;
        }
    }

    public static Int2ObjectMap<Float> readCustomCharWidths(Properties props)
    {
        Int2ObjectMap<Float> int2objectmap = new Int2ObjectOpenHashMap<>();

        for (String s : (Iterable<String>)(Iterable<?>)props.keySet())
        {
            String s1 = "width.";

            if (s.startsWith(s1))
            {
                String s2 = s.substring(s1.length());
                int i = Config.parseInt(s2, -1);

                if (i >= 0)
                {
                    String s3 = props.getProperty(s);
                    float f = Config.parseFloat(s3, -1.0F);

                    if (f >= 0.0F)
                    {
                        char c0 = (char)i;
                        int2objectmap.put(c0, new Float(f));
                    }
                }
            }
        }

        return int2objectmap;
    }

    public static float readFloat(Properties props, String key, float defOffset)
    {
        String s = props.getProperty(key);

        if (s == null)
        {
            return defOffset;
        }
        else
        {
            float f = Config.parseFloat(s, Float.MIN_VALUE);

            if (f == Float.MIN_VALUE)
            {
                Config.warn("Invalid value for " + key + ": " + s);
                return defOffset;
            }
            else
            {
                return f;
            }
        }
    }

    public static boolean readBoolean(Properties props, String key, boolean defVal)
    {
        String s = props.getProperty(key);

        if (s == null)
        {
            return defVal;
        }
        else
        {
            String s1 = s.toLowerCase().trim();

            if (!s1.equals("true") && !s1.equals("on"))
            {
                if (!s1.equals("false") && !s1.equals("off"))
                {
                    Config.warn("Invalid value for " + key + ": " + s);
                    return defVal;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return true;
            }
        }
    }

    public static ResourceLocation getHdFontLocation(ResourceLocation fontLoc)
    {
        if (!Config.isCustomFonts())
        {
            return fontLoc;
        }
        else if (fontLoc == null)
        {
            return fontLoc;
        }
        else if (!Config.isMinecraftThread())
        {
            return fontLoc;
        }
        else
        {
            String s = fontLoc.getPath();
            String s1 = "textures/";
            String s2 = "optifine/";

            if (!s.startsWith(s1))
            {
                return fontLoc;
            }
            else
            {
                s = s.substring(s1.length());
                s = s2 + s;
                ResourceLocation resourcelocation = new ResourceLocation(fontLoc.getNamespace(), s);
                return Config.hasResource(Config.getResourceManager(), resourcelocation) ? resourcelocation : fontLoc;
            }
        }
    }

    public static void reloadFonts()
    {
        IFutureReloadListener.IStage ifuturereloadlistener$istage = new IFutureReloadListener.IStage()
        {
            public <T> CompletableFuture<T> markCompleteAwaitingOthers(T x)
            {
                return CompletableFuture.completedFuture(x);
            }
        };
        Executor executor = Util.getServerExecutor();
        Minecraft minecraft = Minecraft.getInstance();
        FontResourceManager fontresourcemanager = (FontResourceManager)Reflector.getFieldValue(minecraft, Reflector.Minecraft_fontResourceManager);

        if (fontresourcemanager != null)
        {
            fontresourcemanager.getReloadListener().reload(ifuturereloadlistener$istage, Config.getResourceManager(), EmptyProfiler.INSTANCE, EmptyProfiler.INSTANCE, executor, minecraft);
        }
    }
}
