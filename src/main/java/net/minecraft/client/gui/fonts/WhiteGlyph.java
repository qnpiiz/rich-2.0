package net.minecraft.client.gui.fonts;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.Util;

public enum WhiteGlyph implements IGlyphInfo
{
    INSTANCE;

    private static final NativeImage WHITE_GLYPH = Util.make(new NativeImage(NativeImage.PixelFormat.RGBA, 5, 8, false), (nativeImage) -> {
        for (int i = 0; i < 8; ++i)
        {
            for (int j = 0; j < 5; ++j)
            {
                if (j != 0 && j + 1 != 5 && i != 0 && i + 1 != 8)
                {
                    boolean flag = false;
                }
                else
                {
                    boolean flag1 = true;
                }

                nativeImage.setPixelRGBA(j, i, -1);
            }
        }

        nativeImage.untrack();
    });

    public int getWidth()
    {
        return 5;
    }

    public int getHeight()
    {
        return 8;
    }

    public float getAdvance()
    {
        return 6.0F;
    }

    public float getOversample()
    {
        return 1.0F;
    }

    public void uploadGlyph(int xOffset, int yOffset)
    {
        WHITE_GLYPH.uploadTextureSub(0, xOffset, yOffset, false);
    }

    public boolean isColored()
    {
        return true;
    }
}
