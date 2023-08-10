package net.minecraft.client.renderer.texture;

import net.minecraft.util.Util;
import net.optifine.Mipmaps;
import net.optifine.texture.IColorBlender;

public class MipmapGenerator
{
    private static final float[] POWS22 = Util.make(new float[256], (p_lambda$static$0_0_) ->
    {
        for (int i = 0; i < p_lambda$static$0_0_.length; ++i)
        {
            p_lambda$static$0_0_[i] = (float)Math.pow((double)((float)i / 255.0F), 2.2D);
        }
    });

    public static NativeImage[] generateMipmaps(NativeImage imageIn, int mipmapLevelsIn)
    {
        return generateMipmaps(imageIn, mipmapLevelsIn, (IColorBlender)null);
    }

    public static NativeImage[] generateMipmaps(NativeImage p_generateMipmaps_0_, int p_generateMipmaps_1_, IColorBlender p_generateMipmaps_2_)
    {
        NativeImage[] anativeimage = new NativeImage[p_generateMipmaps_1_ + 1];
        anativeimage[0] = p_generateMipmaps_0_;

        if (p_generateMipmaps_1_ > 0)
        {
            boolean flag = false;

            for (int i = 1; i <= p_generateMipmaps_1_; ++i)
            {
                NativeImage nativeimage = anativeimage[i - 1];
                NativeImage nativeimage1 = new NativeImage(nativeimage.getWidth() >> 1, nativeimage.getHeight() >> 1, false);
                int j = nativeimage1.getWidth();
                int k = nativeimage1.getHeight();

                for (int l = 0; l < j; ++l)
                {
                    for (int i1 = 0; i1 < k; ++i1)
                    {
                        if (p_generateMipmaps_2_ != null)
                        {
                            nativeimage1.setPixelRGBA(l, i1, p_generateMipmaps_2_.blend(nativeimage.getPixelRGBA(l * 2 + 0, i1 * 2 + 0), nativeimage.getPixelRGBA(l * 2 + 1, i1 * 2 + 0), nativeimage.getPixelRGBA(l * 2 + 0, i1 * 2 + 1), nativeimage.getPixelRGBA(l * 2 + 1, i1 * 2 + 1)));
                        }
                        else
                        {
                            nativeimage1.setPixelRGBA(l, i1, alphaBlend(nativeimage.getPixelRGBA(l * 2 + 0, i1 * 2 + 0), nativeimage.getPixelRGBA(l * 2 + 1, i1 * 2 + 0), nativeimage.getPixelRGBA(l * 2 + 0, i1 * 2 + 1), nativeimage.getPixelRGBA(l * 2 + 1, i1 * 2 + 1), flag));
                        }
                    }
                }

                anativeimage[i] = nativeimage1;
            }
        }

        return anativeimage;
    }

    private static int alphaBlend(int col1, int col2, int col3, int col4, boolean transparent)
    {
        return Mipmaps.alphaBlend(col1, col2, col3, col4);
    }

    private static int gammaBlend(int col1, int col2, int col3, int col4, int bitOffset)
    {
        float f = getPow22(col1 >> bitOffset);
        float f1 = getPow22(col2 >> bitOffset);
        float f2 = getPow22(col3 >> bitOffset);
        float f3 = getPow22(col4 >> bitOffset);
        float f4 = (float)((double)((float)Math.pow((double)(f + f1 + f2 + f3) * 0.25D, 0.45454545454545453D)));
        return (int)((double)f4 * 255.0D);
    }

    private static float getPow22(int valIn)
    {
        return POWS22[valIn & 255];
    }
}
