package net.optifine.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.optifine.Config;

public class Blender
{
    public static final int BLEND_ALPHA = 0;
    public static final int BLEND_ADD = 1;
    public static final int BLEND_SUBSTRACT = 2;
    public static final int BLEND_MULTIPLY = 3;
    public static final int BLEND_DODGE = 4;
    public static final int BLEND_BURN = 5;
    public static final int BLEND_SCREEN = 6;
    public static final int BLEND_OVERLAY = 7;
    public static final int BLEND_REPLACE = 8;
    public static final int BLEND_DEFAULT = 1;

    public static int parseBlend(String str)
    {
        if (str == null)
        {
            return 1;
        }
        else
        {
            str = str.toLowerCase().trim();

            if (str.equals("alpha"))
            {
                return 0;
            }
            else if (str.equals("add"))
            {
                return 1;
            }
            else if (str.equals("subtract"))
            {
                return 2;
            }
            else if (str.equals("multiply"))
            {
                return 3;
            }
            else if (str.equals("dodge"))
            {
                return 4;
            }
            else if (str.equals("burn"))
            {
                return 5;
            }
            else if (str.equals("screen"))
            {
                return 6;
            }
            else if (str.equals("overlay"))
            {
                return 7;
            }
            else if (str.equals("replace"))
            {
                return 8;
            }
            else
            {
                Config.warn("Unknown blend: " + str);
                return 1;
            }
        }
    }

    public static void setupBlend(int blend, float brightness)
    {
        switch (blend)
        {
            case 0:
                GlStateManager.disableAlphaTest();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, brightness);
                break;

            case 1:
                GlStateManager.disableAlphaTest();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 1);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, brightness);
                break;

            case 2:
                GlStateManager.disableAlphaTest();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(775, 0);
                GlStateManager.color4f(brightness, brightness, brightness, 1.0F);
                break;

            case 3:
                GlStateManager.disableAlphaTest();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(774, 771);
                GlStateManager.color4f(brightness, brightness, brightness, brightness);
                break;

            case 4:
                GlStateManager.disableAlphaTest();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(1, 1);
                GlStateManager.color4f(brightness, brightness, brightness, 1.0F);
                break;

            case 5:
                GlStateManager.disableAlphaTest();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(0, 769);
                GlStateManager.color4f(brightness, brightness, brightness, 1.0F);
                break;

            case 6:
                GlStateManager.disableAlphaTest();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(1, 769);
                GlStateManager.color4f(brightness, brightness, brightness, 1.0F);
                break;

            case 7:
                GlStateManager.disableAlphaTest();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(774, 768);
                GlStateManager.color4f(brightness, brightness, brightness, 1.0F);
                break;

            case 8:
                GlStateManager.enableAlphaTest();
                GlStateManager.disableBlend();
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, brightness);
        }

        GlStateManager.enableTexture();
    }

    public static void clearBlend(float rainBrightness)
    {
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, rainBrightness);
    }
}
