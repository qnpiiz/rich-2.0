package fun.rich.utils.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;

public enum GLUtils {

    INSTANCE;

    public Minecraft mc = Minecraft.getInstance();

    public void rescale(double factor) {
        rescale(mc.getMainWindow().getWidth() / factor, mc.getMainWindow().getHeight() / factor);
    }

    public void rescaleMC() {
        rescale(mc.getMainWindow().getWidth() / mc.getMainWindow().getGuiScaleFactor(),mc.getMainWindow().getHeight() / mc.getMainWindow().getGuiScaleFactor());
    }

    public static int getScreenWidth() {
        return Minecraft.getInstance().getMainWindow().getWidth() / GLUtils.getScaleFactor();
    }

    public static int getScreenHeight() {
        return Minecraft.getInstance().getMainWindow().getHeight() / GLUtils.getScaleFactor();
    }

    public static int getScaleFactor() {
        int scaleFactor = 1;
        boolean isUnicode = Minecraft.getInstance().getForceUnicodeFont();
        int guiScale = Minecraft.getInstance().gameSettings.guiScale;
        if (guiScale == 0)
            guiScale = 1000;

        while (scaleFactor < guiScale && Minecraft.getInstance().getMainWindow().getWidth() / (scaleFactor + 1) >= 320 && Minecraft.getInstance().getMainWindow().getHeight() / (scaleFactor + 1) >= 240)
            ++scaleFactor;
        if (isUnicode && scaleFactor % 2 != 0 && scaleFactor != 1)
            --scaleFactor;

        return scaleFactor;
    }

    public void rescale(double width, double height) {
        GlStateManager.clear(256);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, width, height, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
    }
}
