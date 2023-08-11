package fun.rich.utils.render;

import fun.rich.feature.impl.hud.FeatureList;
import fun.rich.font.MCFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import java.awt.*;

public class ClientHelper {

    private static final Minecraft mc = Minecraft.getInstance();
    
    public static ServerData serverData;

    public static Color getClientColor() {
        Color color = Color.white;
        Color onecolor = new Color(FeatureList.oneColor.getColorValue());
        Color twoColor = new Color(FeatureList.twoColor.getColorValue());
        double time = 10;
        String mode = FeatureList.colorList.getOptions();
        float yDist = (float) 4;
        int yTotal = 0;
        for (int i = 0; i < 30; i++)
            yTotal += Minecraft.getInstance().sfui18.getFontHeight() + 5;
        
        if (mode.equalsIgnoreCase("Rainbow")) {
            color = ColorUtils.rainbow((int) (1 * 200 * 0.1f), 0.5f, 1.0f);
        } else if (mode.equalsIgnoreCase("Astolfo")) {
            color = ColorUtils.astolfo((int) yDist, yTotal);
        } else if (mode.equalsIgnoreCase("Custom")) {
            color = ColorUtils.TwoColoreffect(new Color(onecolor.getRGB()), new Color(twoColor.getRGB()), Math.abs(System.currentTimeMillis() / time) / 100.0 + 3.0F * (yDist * 2.55) / 60);
        } else if (mode.equalsIgnoreCase("Fade")) {
            color = ColorUtils.TwoColoreffect(new Color(onecolor.getRGB()), new Color(onecolor.darker().darker().getRGB()), Math.abs(System.currentTimeMillis() / time) / 100.0 + 3.0F * (yDist * 2.55) / 60);
        }
        return color;
    }

    public static Color getClientColor(float yStep, float yStepFull, int speed) {
        Color color = Color.white;
        Color onecolor = new Color(FeatureList.oneColor.getColorValue());
        Color twoColor = new Color(FeatureList.twoColor.getColorValue());
        double time = 10;

        String mode = FeatureList.colorList.getOptions();
        float yDist = (float) 4;
        int yTotal = 0;
        for (int i = 0; i < 30; i++)
            yTotal += Minecraft.getInstance().sfui18.getFontHeight() + 5;

        if (mode.equalsIgnoreCase("Rainbow")) {
            color = ColorUtils.rainbow((int) (yStep * time), 0.5f, 1.0f);
        } else if (mode.equalsIgnoreCase("Astolfo")) {
            color = ColorUtils.astolfo(yStep, yStepFull, 0.5F, speed);
        } else if (mode.equalsIgnoreCase("Custom")) {
            color = ColorUtils.TwoColoreffect(new Color(onecolor.getRGB()), new Color(twoColor.getRGB()), Math.abs(System.currentTimeMillis() / time) / 100.0 + 3.0F * (yStep * 2.55) / 60);
        } else if (mode.equalsIgnoreCase("Fade")) {
            color = ColorUtils.TwoColoreffect(new Color(onecolor.getRGB()), new Color(onecolor.darker().darker().getRGB()), Math.abs(System.currentTimeMillis() / time) / 100.0 + 3.0F * (yStep * 2.55) / 60);
        }

        return color;
    }

    public static Color getClientColor(float yStep, float astolfoastep, float yStepFull, int speed) {
        Color color = Color.white;
        Color onecolor = new Color(FeatureList.oneColor.getColorValue());
        Color twoColor = new Color(FeatureList.twoColor.getColorValue());
        double time = 11;
        String mode = FeatureList.colorList.getOptions();
        int yTotal = 0;
        for (int i = 0; i < 30; i++)
            yTotal += Minecraft.getInstance().sfui18.getFontHeight() + 5;

        if (mode.equalsIgnoreCase("Rainbow")) {
            color = ColorUtils.rainbowCol(yStep, yStepFull, 0.5f, speed);
        } else if (mode.equalsIgnoreCase("Astolfo")) {
            color = ColorUtils.astolfo(astolfoastep, yStepFull, 0.5f, speed);
        } else if (mode.equalsIgnoreCase("Custom")) {
            color = ColorUtils.TwoColoreffect(new Color(onecolor.getRGB()), new Color(twoColor.getRGB()), Math.abs(System.currentTimeMillis() / time) / 100.0 + 3.0F * (yStep * 2.55) / 60);
        } else if (mode.equalsIgnoreCase("Fade")) {
            color = ColorUtils.TwoColoreffect(new Color(onecolor.getRGB()), new Color(onecolor.darker().darker().getRGB()), Math.abs(System.currentTimeMillis() / time) / 100.0 + 3.0F * (yStep * 2.55) / 60);
        }
        return color;
    }

    public static MCFontRenderer getFontRender() {
        Minecraft mc = Minecraft.getInstance();
        MCFontRenderer font = mc.sfui18;
        String mode = FeatureList.fontList.getOptions();
        switch (mode) {
            case "Myseo":
                font = mc.neverlose500_16;
                break;
            case "Tahoma":
                font = mc.tahoma16;
                break;
            case "SF-UI":
                font = mc.sfui16;
                break;
            case "Rubik":
                font = mc.rubik_16;
                break;
        }

        return font;
    }
}
