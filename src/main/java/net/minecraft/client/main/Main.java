package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import com.mojang.blaze3d.Empty3i;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.GameConfiguration;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.util.UndeclaredException;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Session;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] p_main_0_)
    {
        OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("disableMultiplayer");
        optionparser.accepts("disableChat");
        optionparser.accepts("fullscreen");
        optionparser.accepts("checkGlErrors");
        OptionSpec<String> optionspec = optionparser.accepts("server").withRequiredArg();
        OptionSpec<Integer> optionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        OptionSpec<File> optionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        OptionSpec<File> optionspec3 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        OptionSpec<File> optionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        OptionSpec<File> optionspec5 = optionparser.accepts("dataPackDir").withRequiredArg().ofType(File.class);
        OptionSpec<String> optionspec6 = optionparser.accepts("proxyHost").withRequiredArg();
        OptionSpec<Integer> optionspec7 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
        OptionSpec<String> optionspec8 = optionparser.accepts("proxyUser").withRequiredArg();
        OptionSpec<String> optionspec9 = optionparser.accepts("proxyPass").withRequiredArg();
        OptionSpec<String> optionspec10 = optionparser.accepts("username").withRequiredArg().defaultsTo("Player" + Util.milliTime() % 1000L);
        OptionSpec<String> optionspec11 = optionparser.accepts("uuid").withRequiredArg();
        OptionSpec<String> optionspec12 = optionparser.accepts("accessToken").withRequiredArg().required();
        OptionSpec<String> optionspec13 = optionparser.accepts("version").withRequiredArg().required();
        OptionSpec<Integer> optionspec14 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        OptionSpec<Integer> optionspec15 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        OptionSpec<Integer> optionspec16 = optionparser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
        OptionSpec<Integer> optionspec17 = optionparser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
        OptionSpec<String> optionspec18 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionspec19 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionspec20 = optionparser.accepts("assetIndex").withRequiredArg();
        OptionSpec<String> optionspec21 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy");
        OptionSpec<String> optionspec22 = optionparser.accepts("versionType").withRequiredArg().defaultsTo("release");
        OptionSpec<String> optionspec23 = optionparser.nonOptions();
        OptionSet optionset = optionparser.parse(p_main_0_);
        List<String> list = optionset.valuesOf(optionspec23);

        if (!list.isEmpty())
        {
            System.out.println("Completely ignored arguments: " + list);
        }

        String s = getValue(optionset, optionspec6);
        Proxy proxy = Proxy.NO_PROXY;

        if (s != null)
        {
            try
            {
                proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, getValue(optionset, optionspec7)));
            }
            catch (Exception exception)
            {
            }
        }

        final String s1 = getValue(optionset, optionspec8);
        final String s2 = getValue(optionset, optionspec9);

        if (!proxy.equals(Proxy.NO_PROXY) && isNotEmpty(s1) && isNotEmpty(s2))
        {
            Authenticator.setDefault(new Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(s1, s2.toCharArray());
                }
            });
        }

        int i = getValue(optionset, optionspec14);
        int j = getValue(optionset, optionspec15);
        OptionalInt optionalint = toOptionalInt(getValue(optionset, optionspec16));
        OptionalInt optionalint1 = toOptionalInt(getValue(optionset, optionspec17));
        boolean flag = optionset.has("fullscreen");
        boolean flag1 = optionset.has("demo");
        boolean flag2 = optionset.has("disableMultiplayer");
        boolean flag3 = optionset.has("disableChat");
        String s3 = getValue(optionset, optionspec13);
        Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
        PropertyMap propertymap = JSONUtils.fromJson(gson, getValue(optionset, optionspec18), PropertyMap.class);
        PropertyMap propertymap1 = JSONUtils.fromJson(gson, getValue(optionset, optionspec19), PropertyMap.class);
        String s4 = getValue(optionset, optionspec22);
        File file1 = getValue(optionset, optionspec2);
        File file2 = optionset.has(optionspec3) ? getValue(optionset, optionspec3) : new File(file1, "assets/");
        File file3 = optionset.has(optionspec4) ? getValue(optionset, optionspec4) : new File(file1, "resourcepacks/");
        String s5 = optionset.has(optionspec11) ? optionspec11.value(optionset) : PlayerEntity.getOfflineUUID(optionspec10.value(optionset)).toString();
        String s6 = optionset.has(optionspec20) ? optionspec20.value(optionset) : null;
        String s7 = getValue(optionset, optionspec);
        Integer integer = getValue(optionset, optionspec1);
        CrashReport.crash();
        Bootstrap.register();
        Bootstrap.checkTranslations();
        Util.func_240994_l_();
        Session session = new Session(optionspec10.value(optionset), s5, optionspec12.value(optionset), optionspec21.value(optionset));
        GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy), new ScreenSize(i, j, optionalint, optionalint1, flag), new GameConfiguration.FolderInformation(file1, file3, file2, s6), new GameConfiguration.GameInformation(flag1, s3, s4, flag2, flag3), new GameConfiguration.ServerInformation(s7, integer));
        Thread thread = new Thread("Client Shutdown Thread")
        {
            public void run()
            {
                Minecraft minecraft1 = Minecraft.getInstance();

                if (minecraft1 != null)
                {
                    IntegratedServer integratedserver = minecraft1.getIntegratedServer();

                    if (integratedserver != null)
                    {
                        integratedserver.initiateShutdown(true);
                    }
                }
            }
        };
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        Runtime.getRuntime().addShutdownHook(thread);
        new Empty3i();
        final Minecraft minecraft;

        try
        {
            Thread.currentThread().setName("Render thread");
            RenderSystem.initRenderThread();
            RenderSystem.beginInitialization();
            minecraft = new Minecraft(gameconfiguration);
            RenderSystem.finishInitialization();
        }
        catch (UndeclaredException undeclaredexception)
        {
            LOGGER.warn("Failed to create window: ", (Throwable)undeclaredexception);
            return;
        }
        catch (Throwable throwable1)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Initializing game");
            crashreport.makeCategory("Initialization");
            Minecraft.fillCrashReport((LanguageManager)null, gameconfiguration.gameInfo.version, (GameSettings)null, crashreport);
            Minecraft.displayCrashReport(crashreport);
            return;
        }

        Thread thread1;

        if (minecraft.isRenderOnThread())
        {
            thread1 = new Thread("Game thread")
            {
                public void run()
                {
                    try
                    {
                        RenderSystem.initGameThread(true);
                        minecraft.run();
                    }
                    catch (Throwable throwable2)
                    {
                        Main.LOGGER.error("Exception in client thread", throwable2);
                    }
                }
            };
            thread1.start();

            while (minecraft.isRunning())
            {
            }
        }
        else
        {
            thread1 = null;

            try
            {
                RenderSystem.initGameThread(false);
                minecraft.run();
            }
            catch (Throwable throwable)
            {
                LOGGER.error("Unhandled game exception", throwable);
            }
        }

        try
        {
            minecraft.shutdown();

            if (thread1 != null)
            {
                thread1.join();
            }
        }
        catch (InterruptedException interruptedexception)
        {
            LOGGER.error("Exception during client thread shutdown", (Throwable)interruptedexception);
        }
        finally
        {
            minecraft.shutdownMinecraftApplet();
        }
    }

    private static OptionalInt toOptionalInt(@Nullable Integer value)
    {
        return value != null ? OptionalInt.of(value) : OptionalInt.empty();
    }

    @Nullable
    private static <T> T getValue(OptionSet set, OptionSpec<T> option)
    {
        try
        {
            return set.valueOf(option);
        }
        catch (Throwable throwable)
        {
            if (option instanceof ArgumentAcceptingOptionSpec)
            {
                ArgumentAcceptingOptionSpec<T> argumentacceptingoptionspec = (ArgumentAcceptingOptionSpec)option;
                List<T> list = argumentacceptingoptionspec.defaultValues();

                if (!list.isEmpty())
                {
                    return list.get(0);
                }
            }

            throw throwable;
        }
    }

    /**
     * Returns true if the given string is neither null nor empty.
     */
    private static boolean isNotEmpty(@Nullable String str)
    {
        return str != null && !str.isEmpty();
    }

    static
    {
        System.setProperty("java.awt.headless", "true");
    }
}
