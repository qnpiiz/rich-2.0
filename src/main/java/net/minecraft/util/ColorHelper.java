package net.minecraft.util;

public class ColorHelper
{
    public static class PackedColor
    {
        public static int getAlpha(int packedColor)
        {
            return packedColor >>> 24;
        }

        public static int getRed(int packedColor)
        {
            return packedColor >> 16 & 255;
        }

        public static int getGreen(int packedColor)
        {
            return packedColor >> 8 & 255;
        }

        public static int getBlue(int packedColor)
        {
            return packedColor & 255;
        }

        public static int packColor(int alpha, int red, int green, int blue)
        {
            return alpha << 24 | red << 16 | green << 8 | blue;
        }

        public static int blendColors(int packedColourOne, int packedColorTwo)
        {
            return packColor(getAlpha(packedColourOne) * getAlpha(packedColorTwo) / 255, getRed(packedColourOne) * getRed(packedColorTwo) / 255, getGreen(packedColourOne) * getGreen(packedColorTwo) / 255, getBlue(packedColourOne) * getBlue(packedColorTwo) / 255);
        }
    }
}
