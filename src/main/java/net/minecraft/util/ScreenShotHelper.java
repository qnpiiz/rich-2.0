package net.minecraft.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScreenShotHelper
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    /**
     * Saves a screenshot in the game directory with a time-stamped filename.
     * Returns an ITextComponent indicating the success/failure of the saving.
     */
    public static void saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer, Consumer<ITextComponent> messageConsumer)
    {
        saveScreenshot(gameDirectory, (String)null, width, height, buffer, messageConsumer);
    }

    /**
     * Saves a screenshot in the game directory with the given file name (or null to generate a time-stamped name).
     * Returns an ITextComponent indicating the success/failure of the saving.
     */
    public static void saveScreenshot(File gameDirectory, @Nullable String screenshotName, int width, int height, Framebuffer buffer, Consumer<ITextComponent> messageConsumer)
    {
        if (!RenderSystem.isOnRenderThread())
        {
            RenderSystem.recordRenderCall(() ->
            {
                saveScreenshotRaw(gameDirectory, screenshotName, width, height, buffer, messageConsumer);
            });
        }
        else
        {
            saveScreenshotRaw(gameDirectory, screenshotName, width, height, buffer, messageConsumer);
        }
    }

    private static void saveScreenshotRaw(File gameDirectory, @Nullable String screenshotName, int width, int height, Framebuffer buffer, Consumer<ITextComponent> messageConsumer)
    {
        Minecraft minecraft = Config.getMinecraft();
        MainWindow mainwindow = minecraft.getMainWindow();
        GameSettings gamesettings = Config.getGameSettings();
        int i = mainwindow.getFramebufferWidth();
        int j = mainwindow.getFramebufferHeight();
        int k = gamesettings.guiScale;
        int l = mainwindow.calcGuiScale(minecraft.gameSettings.guiScale, minecraft.gameSettings.forceUnicodeFont);
        int i1 = Config.getScreenshotSize();
        boolean flag = GLX.isUsingFBOs() && i1 > 1;

        if (flag)
        {
            gamesettings.guiScale = l * i1;
            mainwindow.resizeFramebuffer(i * i1, j * i1);
            GlStateManager.pushMatrix();
            GlStateManager.clear(16640);
            minecraft.getFramebuffer().bindFramebuffer(true);
            GlStateManager.enableTexture();
            minecraft.gameRenderer.updateCameraAndRender(minecraft.getRenderPartialTicks(), System.nanoTime(), true);
        }

        NativeImage nativeimage = createScreenshot(width, height, buffer);

        if (flag)
        {
            minecraft.getFramebuffer().unbindFramebuffer();
            GlStateManager.popMatrix();
            Config.getGameSettings().guiScale = k;
            mainwindow.resizeFramebuffer(i, j);
        }

        File file1 = new File(gameDirectory, "screenshots");
        file1.mkdir();
        File file2;

        if (screenshotName == null)
        {
            file2 = getTimestampedPNGFileForDirectory(file1);
        }
        else
        {
            file2 = new File(file1, screenshotName);
        }

        Object object = null;

        if (Reflector.ForgeHooksClient_onScreenshot.exists())
        {
            object = Reflector.call(Reflector.ForgeHooksClient_onScreenshot, nativeimage, file2);

            if (Reflector.callBoolean(object, Reflector.Event_isCanceled))
            {
                ITextComponent itextcomponent = (ITextComponent)Reflector.call(object, Reflector.ScreenshotEvent_getCancelMessage);
                messageConsumer.accept(itextcomponent);
                return;
            }

            file2 = (File)Reflector.call(object, Reflector.ScreenshotEvent_getScreenshotFile);
        }

        File file3 = file2;
        Object object1 = object;
        Util.getRenderingService().execute(() ->
        {
            try {
                nativeimage.write(file3);
                ITextComponent itextcomponent1 = (new StringTextComponent(file3.getName())).mergeStyle(TextFormatting.UNDERLINE).modifyStyle((p_lambda$null$1_1_) -> {
                    return p_lambda$null$1_1_.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file3.getAbsolutePath()));
                });

                if (object1 != null && Reflector.call(object1, Reflector.ScreenshotEvent_getResultMessage) != null)
                {
                    messageConsumer.accept((ITextComponent)Reflector.call(object1, Reflector.ScreenshotEvent_getResultMessage));
                }
                else {
                    messageConsumer.accept(new TranslationTextComponent("screenshot.success", itextcomponent1));
                }
            }
            catch (Exception exception1)
            {
                LOGGER.warn("Couldn't save screenshot", (Throwable)exception1);
                messageConsumer.accept(new TranslationTextComponent("screenshot.failure", exception1.getMessage()));
            }
            finally {
                nativeimage.close();
            }
        });
    }

    public static NativeImage createScreenshot(int width, int height, Framebuffer framebufferIn)
    {
        if (!GLX.isUsingFBOs())
        {
            NativeImage nativeimage1 = new NativeImage(width, height, false);
            nativeimage1.downloadFromFramebuffer(true);
            nativeimage1.flip();
            return nativeimage1;
        }
        else
        {
            width = framebufferIn.framebufferTextureWidth;
            height = framebufferIn.framebufferTextureHeight;
            NativeImage nativeimage = new NativeImage(width, height, false);
            RenderSystem.bindTexture(framebufferIn.func_242996_f());
            nativeimage.downloadFromTexture(0, true);
            nativeimage.flip();
            return nativeimage;
        }
    }

    /**
     * Creates a unique PNG file in the given directory named by a timestamp.  Handles cases where the timestamp alone
     * is not enough to create a uniquely named file, though it still might suffer from an unlikely race condition where
     * the filename was unique when this method was called, but another process or thread created a file at the same
     * path immediately after this method returned.
     */
    private static File getTimestampedPNGFileForDirectory(File gameDirectory)
    {
        String s = DATE_FORMAT.format(new Date());
        int i = 1;

        while (true)
        {
            File file1 = new File(gameDirectory, s + (i == 1 ? "" : "_" + i) + ".png");

            if (!file1.exists())
            {
                return file1;
            }

            ++i;
        }
    }
}
