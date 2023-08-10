package net.optifine.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.render.RenderUtils;
import net.optifine.util.TextureUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ShadersTex
{
    public static final int initialBufferSize = 1048576;
    public static ByteBuffer byteBuffer = BufferUtils.createByteBuffer(4194304);
    public static IntBuffer intBuffer = byteBuffer.asIntBuffer();
    public static int[] intArray = new int[1048576];
    public static final int defBaseTexColor = 0;
    public static final int defNormTexColor = -8421377;
    public static final int defSpecTexColor = 0;
    public static Map<Integer, MultiTexID> multiTexMap = new HashMap<>();

    public static IntBuffer getIntBuffer(int size)
    {
        if (intBuffer.capacity() < size)
        {
            int i = roundUpPOT(size);
            byteBuffer = BufferUtils.createByteBuffer(i * 4);
            intBuffer = byteBuffer.asIntBuffer();
        }

        return intBuffer;
    }

    public static int[] getIntArray(int size)
    {
        if (intArray == null)
        {
            intArray = new int[1048576];
        }

        if (intArray.length < size)
        {
            intArray = new int[roundUpPOT(size)];
        }

        return intArray;
    }

    public static int roundUpPOT(int x)
    {
        int i = x - 1;
        i = i | i >> 1;
        i = i | i >> 2;
        i = i | i >> 4;
        i = i | i >> 8;
        i = i | i >> 16;
        return i + 1;
    }

    public static int log2(int x)
    {
        int i = 0;

        if ((x & -65536) != 0)
        {
            i += 16;
            x >>= 16;
        }

        if ((x & 65280) != 0)
        {
            i += 8;
            x >>= 8;
        }

        if ((x & 240) != 0)
        {
            i += 4;
            x >>= 4;
        }

        if ((x & 6) != 0)
        {
            i += 2;
            x >>= 2;
        }

        if ((x & 2) != 0)
        {
            ++i;
        }

        return i;
    }

    public static IntBuffer fillIntBuffer(int size, int value)
    {
        int[] aint = getIntArray(size);
        IntBuffer intbuffer = getIntBuffer(size);
        Arrays.fill(intArray, 0, size, value);
        intBuffer.put(intArray, 0, size);
        return intBuffer;
    }

    public static int[] createAIntImage(int size)
    {
        int[] aint = new int[size * 3];
        Arrays.fill(aint, 0, size, 0);
        Arrays.fill(aint, size, size * 2, -8421377);
        Arrays.fill(aint, size * 2, size * 3, 0);
        return aint;
    }

    public static int[] createAIntImage(int size, int color)
    {
        int[] aint = new int[size * 3];
        Arrays.fill(aint, 0, size, color);
        Arrays.fill(aint, size, size * 2, -8421377);
        Arrays.fill(aint, size * 2, size * 3, 0);
        return aint;
    }

    public static MultiTexID getMultiTexID(Texture tex)
    {
        MultiTexID multitexid = tex.multiTex;

        if (multitexid == null)
        {
            int i = tex.getGlTextureId();
            multitexid = multiTexMap.get(i);

            if (multitexid == null)
            {
                multitexid = new MultiTexID(i, GL11.glGenTextures(), GL11.glGenTextures());
                multiTexMap.put(i, multitexid);
            }

            tex.multiTex = multitexid;
        }

        return multitexid;
    }

    public static void deleteTextures(Texture atex, int texid)
    {
        MultiTexID multitexid = atex.multiTex;

        if (multitexid != null)
        {
            atex.multiTex = null;
            multiTexMap.remove(multitexid.base);
            GlStateManager.deleteTexture(multitexid.norm);
            GlStateManager.deleteTexture(multitexid.spec);

            if (multitexid.base != texid)
            {
                SMCLog.warning("Error : MultiTexID.base mismatch: " + multitexid.base + ", texid: " + texid);
                GlStateManager.deleteTexture(multitexid.base);
            }
        }
    }

    public static void bindNSTextures(int normTex, int specTex, boolean normalBlend, boolean specularBlend, boolean mipmaps)
    {
        if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984)
        {
            if (Shaders.configNormalMap)
            {
                GlStateManager.activeTexture(33985);
                GlStateManager.bindTexture(normTex);

                if (!normalBlend)
                {
                    int i = mipmaps ? 9984 : 9728;
                    GlStateManager.texParameter(3553, 10241, i);
                    GlStateManager.texParameter(3553, 10240, 9728);
                }
            }

            if (Shaders.configSpecularMap)
            {
                GlStateManager.activeTexture(33987);
                GlStateManager.bindTexture(specTex);

                if (!specularBlend)
                {
                    int j = mipmaps ? 9984 : 9728;
                    GlStateManager.texParameter(3553, 10241, j);
                    GlStateManager.texParameter(3553, 10240, 9728);
                }
            }

            GlStateManager.activeTexture(33984);
        }
    }

    public static void bindNSTextures(MultiTexID multiTex)
    {
        bindNSTextures(multiTex.norm, multiTex.spec, true, true, false);
    }

    public static void bindTextures(int baseTex, int normTex, int specTex)
    {
        if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984)
        {
            GlStateManager.activeTexture(33985);
            GlStateManager.bindTexture(normTex);
            GlStateManager.activeTexture(33987);
            GlStateManager.bindTexture(specTex);
            GlStateManager.activeTexture(33984);
        }

        GlStateManager.bindTexture(baseTex);
    }

    public static void bindTextures(MultiTexID multiTex, boolean normalBlend, boolean specularBlend, boolean mipmaps)
    {
        if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984)
        {
            if (Shaders.configNormalMap)
            {
                GlStateManager.activeTexture(33985);
                GlStateManager.bindTexture(multiTex.norm);

                if (!normalBlend)
                {
                    int i = mipmaps ? 9984 : 9728;
                    GlStateManager.texParameter(3553, 10241, i);
                    GlStateManager.texParameter(3553, 10240, 9728);
                }
            }

            if (Shaders.configSpecularMap)
            {
                GlStateManager.activeTexture(33987);
                GlStateManager.bindTexture(multiTex.spec);

                if (!specularBlend)
                {
                    int j = mipmaps ? 9984 : 9728;
                    GlStateManager.texParameter(3553, 10241, j);
                    GlStateManager.texParameter(3553, 10240, 9728);
                }
            }

            GlStateManager.activeTexture(33984);
        }

        GlStateManager.bindTexture(multiTex.base);
    }

    public static void bindTexture(Texture tex)
    {
        int i = tex.getGlTextureId();
        boolean flag = true;
        boolean flag1 = true;
        boolean flag2 = false;

        if (tex instanceof AtlasTexture)
        {
            AtlasTexture atlastexture = (AtlasTexture)tex;
            flag = atlastexture.isNormalBlend();
            flag1 = atlastexture.isSpecularBlend();
            flag2 = atlastexture.isMipmaps();
        }

        bindTextures(tex.getMultiTexID(), flag, flag1, flag2);

        if (GlStateManager.getActiveTextureUnit() == 33984)
        {
            int k = Shaders.atlasSizeX;
            int j = Shaders.atlasSizeY;

            if (tex instanceof AtlasTexture)
            {
                Shaders.atlasSizeX = ((AtlasTexture)tex).atlasWidth;
                Shaders.atlasSizeY = ((AtlasTexture)tex).atlasHeight;
            }
            else
            {
                Shaders.atlasSizeX = 0;
                Shaders.atlasSizeY = 0;
            }

            if (Shaders.atlasSizeX != k || Shaders.atlasSizeY != j)
            {
                boolean flag3 = RenderUtils.setFlushRenderBuffers(false);
                Shaders.uniform_atlasSize.setValue(Shaders.atlasSizeX, Shaders.atlasSizeY);
                RenderUtils.setFlushRenderBuffers(flag3);
            }
        }
    }

    public static void bindTextures(int baseTex)
    {
        MultiTexID multitexid = multiTexMap.get(baseTex);
        bindTextures(multitexid, true, true, false);
    }

    public static void initDynamicTextureNS(DynamicTexture tex)
    {
        MultiTexID multitexid = tex.getMultiTexID();
        NativeImage nativeimage = tex.getTextureData();
        int i = nativeimage.getWidth();
        int j = nativeimage.getHeight();
        NativeImage nativeimage1 = makeImageColor(i, j, -8421377);
        TextureUtil.prepareImage(multitexid.norm, i, j);
        nativeimage1.uploadTextureSub(0, 0, 0, 0, 0, i, j, false, false, false, true);
        NativeImage nativeimage2 = makeImageColor(i, j, 0);
        TextureUtil.prepareImage(multitexid.spec, i, j);
        nativeimage2.uploadTextureSub(0, 0, 0, 0, 0, i, j, false, false, false, true);
        GlStateManager.bindTexture(multitexid.base);
    }

    public static void updateDynTexSubImage1(int[] src, int width, int height, int posX, int posY, int page)
    {
        int i = width * height;
        IntBuffer intbuffer = getIntBuffer(i);
        ((Buffer)intbuffer).clear();
        int j = page * i;

        if (src.length >= j + i)
        {
            ((Buffer)intbuffer.put(src, j, i)).position(0).limit(i);
            TextureUtils.resetDataUnpacking();
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, posX, posY, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intbuffer);
            ((Buffer)intbuffer).clear();
        }
    }

    public static Texture createDefaultTexture()
    {
        DynamicTexture dynamictexture = new DynamicTexture(1, 1, true);
        dynamictexture.getTextureData().setPixelRGBA(0, 0, -1);
        dynamictexture.updateDynamicTexture();
        return dynamictexture;
    }

    public static void allocateTextureMapNS(int mipmapLevels, int width, int height, AtlasTexture tex)
    {
        MultiTexID multitexid = getMultiTexID(tex);

        if (Shaders.configNormalMap)
        {
            SMCLog.info("Allocate texture map normal: " + width + "x" + height + ", mipmaps: " + mipmapLevels);
            TextureUtil.prepareImage(multitexid.norm, mipmapLevels, width, height);
        }

        if (Shaders.configSpecularMap)
        {
            SMCLog.info("Allocate texture map specular: " + width + "x" + height + ", mipmaps: " + mipmapLevels);
            TextureUtil.prepareImage(multitexid.spec, mipmapLevels, width, height);
        }

        GlStateManager.bindTexture(multitexid.base);
    }

    private static NativeImage[] generateMipmaps(NativeImage image, int levels)
    {
        if (levels < 0)
        {
            levels = 0;
        }

        NativeImage[] anativeimage = new NativeImage[levels + 1];
        anativeimage[0] = image;

        if (levels > 0)
        {
            for (int i = 1; i <= levels; ++i)
            {
                NativeImage nativeimage = anativeimage[i - 1];
                NativeImage nativeimage1 = new NativeImage(nativeimage.getWidth() >> 1, nativeimage.getHeight() >> 1, false);
                int j = nativeimage1.getWidth();
                int k = nativeimage1.getHeight();

                for (int l = 0; l < j; ++l)
                {
                    for (int i1 = 0; i1 < k; ++i1)
                    {
                        nativeimage1.setPixelRGBA(l, i1, blend4Simple(nativeimage.getPixelRGBA(l * 2 + 0, i1 * 2 + 0), nativeimage.getPixelRGBA(l * 2 + 1, i1 * 2 + 0), nativeimage.getPixelRGBA(l * 2 + 0, i1 * 2 + 1), nativeimage.getPixelRGBA(l * 2 + 1, i1 * 2 + 1)));
                    }
                }

                anativeimage[i] = nativeimage1;
            }
        }

        return anativeimage;
    }

    public static BufferedImage readImage(ResourceLocation resLoc)
    {
        try
        {
            if (!Config.hasResource(resLoc))
            {
                return null;
            }
            else
            {
                InputStream inputstream = Config.getResourceStream(resLoc);

                if (inputstream == null)
                {
                    return null;
                }
                else
                {
                    BufferedImage bufferedimage = ImageIO.read(inputstream);
                    inputstream.close();
                    return bufferedimage;
                }
            }
        }
        catch (IOException ioexception)
        {
            return null;
        }
    }

    public static int[][] genMipmapsSimple(int maxLevel, int width, int[][] data)
    {
        for (int i = 1; i <= maxLevel; ++i)
        {
            if (data[i] == null)
            {
                int j = width >> i;
                int k = j * 2;
                int[] aint = data[i - 1];
                int[] aint1 = data[i] = new int[j * j];

                for (int i1 = 0; i1 < j; ++i1)
                {
                    for (int l = 0; l < j; ++l)
                    {
                        int j1 = i1 * 2 * k + l * 2;
                        aint1[i1 * j + l] = blend4Simple(aint[j1], aint[j1 + 1], aint[j1 + k], aint[j1 + k + 1]);
                    }
                }
            }
        }

        return data;
    }

    public static void uploadTexSub1(int[][] src, int width, int height, int posX, int posY, int page)
    {
        TextureUtils.resetDataUnpacking();
        int i = width * height;
        IntBuffer intbuffer = getIntBuffer(i);
        int j = src.length;
        int k = 0;
        int l = width;
        int i1 = height;
        int j1 = posX;

        for (int k1 = posY; l > 0 && i1 > 0 && k < j; ++k)
        {
            int l1 = l * i1;
            int[] aint = src[k];
            ((Buffer)intbuffer).clear();

            if (aint.length >= l1 * (page + 1))
            {
                ((Buffer)intbuffer.put(aint, l1 * page, l1)).position(0).limit(l1);
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, k, j1, k1, l, i1, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intbuffer);
            }

            l >>= 1;
            i1 >>= 1;
            j1 >>= 1;
            k1 >>= 1;
        }

        ((Buffer)intbuffer).clear();
    }

    public static int blend4Alpha(int c0, int c1, int c2, int c3)
    {
        int i = c0 >>> 24 & 255;
        int j = c1 >>> 24 & 255;
        int k = c2 >>> 24 & 255;
        int l = c3 >>> 24 & 255;
        int i1 = i + j + k + l;
        int j1 = (i1 + 2) / 4;
        int k1;

        if (i1 != 0)
        {
            k1 = i1;
        }
        else
        {
            k1 = 4;
            i = 1;
            j = 1;
            k = 1;
            l = 1;
        }

        int l1 = (k1 + 1) / 2;
        return j1 << 24 | ((c0 >>> 16 & 255) * i + (c1 >>> 16 & 255) * j + (c2 >>> 16 & 255) * k + (c3 >>> 16 & 255) * l + l1) / k1 << 16 | ((c0 >>> 8 & 255) * i + (c1 >>> 8 & 255) * j + (c2 >>> 8 & 255) * k + (c3 >>> 8 & 255) * l + l1) / k1 << 8 | ((c0 >>> 0 & 255) * i + (c1 >>> 0 & 255) * j + (c2 >>> 0 & 255) * k + (c3 >>> 0 & 255) * l + l1) / k1 << 0;
    }

    public static int blend4Simple(int c0, int c1, int c2, int c3)
    {
        return ((c0 >>> 24 & 255) + (c1 >>> 24 & 255) + (c2 >>> 24 & 255) + (c3 >>> 24 & 255) + 2) / 4 << 24 | ((c0 >>> 16 & 255) + (c1 >>> 16 & 255) + (c2 >>> 16 & 255) + (c3 >>> 16 & 255) + 2) / 4 << 16 | ((c0 >>> 8 & 255) + (c1 >>> 8 & 255) + (c2 >>> 8 & 255) + (c3 >>> 8 & 255) + 2) / 4 << 8 | ((c0 >>> 0 & 255) + (c1 >>> 0 & 255) + (c2 >>> 0 & 255) + (c3 >>> 0 & 255) + 2) / 4 << 0;
    }

    public static void genMipmapAlpha(int[] aint, int offset, int width, int height)
    {
        Math.min(width, height);
        int o2 = offset;
        int w2 = width;
        int h2 = height;
        int o1 = 0;
        int w1 = 0;
        int h1 = 0;
        int i;

        for (i = 0; w2 > 1 && h2 > 1; o2 = o1)
        {
            o1 = o2 + w2 * h2;
            w1 = w2 / 2;
            h1 = h2 / 2;

            for (int l1 = 0; l1 < h1; ++l1)
            {
                int i2 = o1 + l1 * w1;
                int j2 = o2 + l1 * 2 * w2;

                for (int k2 = 0; k2 < w1; ++k2)
                {
                    aint[i2 + k2] = blend4Alpha(aint[j2 + k2 * 2], aint[j2 + k2 * 2 + 1], aint[j2 + w2 + k2 * 2], aint[j2 + w2 + k2 * 2 + 1]);
                }
            }

            ++i;
            w2 = w1;
            h2 = h1;
        }

        while (i > 0)
        {
            --i;
            w2 = width >> i;
            h2 = height >> i;
            o2 = o1 - w2 * h2;
            int l2 = o2;

            for (int i3 = 0; i3 < h2; ++i3)
            {
                for (int j3 = 0; j3 < w2; ++j3)
                {
                    if (aint[l2] == 0)
                    {
                        aint[l2] = aint[o1 + i3 / 2 * w1 + j3 / 2] & 16777215;
                    }

                    ++l2;
                }
            }

            o1 = o2;
            w1 = w2;
        }
    }

    public static void genMipmapSimple(int[] aint, int offset, int width, int height)
    {
        Math.min(width, height);
        int o2 = offset;
        int w2 = width;
        int h2 = height;
        int o1 = 0;
        int w1 = 0;
        int h1 = 0;
        int i;

        for (i = 0; w2 > 1 && h2 > 1; o2 = o1)
        {
            o1 = o2 + w2 * h2;
            w1 = w2 / 2;
            h1 = h2 / 2;

            for (int l1 = 0; l1 < h1; ++l1)
            {
                int i2 = o1 + l1 * w1;
                int j2 = o2 + l1 * 2 * w2;

                for (int k2 = 0; k2 < w1; ++k2)
                {
                    aint[i2 + k2] = blend4Simple(aint[j2 + k2 * 2], aint[j2 + k2 * 2 + 1], aint[j2 + w2 + k2 * 2], aint[j2 + w2 + k2 * 2 + 1]);
                }
            }

            ++i;
            w2 = w1;
            h2 = h1;
        }

        while (i > 0)
        {
            --i;
            w2 = width >> i;
            h2 = height >> i;
            o2 = o1 - w2 * h2;
            int l2 = o2;

            for (int i3 = 0; i3 < h2; ++i3)
            {
                for (int j3 = 0; j3 < w2; ++j3)
                {
                    if (aint[l2] == 0)
                    {
                        aint[l2] = aint[o1 + i3 / 2 * w1 + j3 / 2] & 16777215;
                    }

                    ++l2;
                }
            }

            o1 = o2;
            w1 = w2;
        }
    }

    public static boolean isSemiTransparent(int[] aint, int width, int height)
    {
        int i = width * height;

        if (aint[0] >>> 24 == 255 && aint[i - 1] == 0)
        {
            return true;
        }
        else
        {
            for (int j = 0; j < i; ++j)
            {
                int k = aint[j] >>> 24;

                if (k != 0 && k != 255)
                {
                    return true;
                }
            }

            return false;
        }
    }

    public static void updateSubTex1(int[] src, int width, int height, int posX, int posY)
    {
        int i = 0;
        int j = width;
        int k = height;
        int l = posX;

        for (int i1 = posY; j > 0 && k > 0; i1 /= 2)
        {
            GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, i, l, i1, 0, 0, j, k);
            ++i;
            j /= 2;
            k /= 2;
            l /= 2;
        }
    }

    public static void updateSubImage(MultiTexID multiTex, int[] src, int width, int height, int posX, int posY, boolean linear, boolean clamp)
    {
        int i = width * height;
        IntBuffer intbuffer = getIntBuffer(i);
        TextureUtils.resetDataUnpacking();
        ((Buffer)intbuffer).clear();
        intbuffer.put(src, 0, i);
        ((Buffer)intbuffer).position(0).limit(i);
        GlStateManager.bindTexture(multiTex.base);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, posX, posY, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intbuffer);

        if (src.length == i * 3)
        {
            ((Buffer)intbuffer).clear();
            ((Buffer)intbuffer.put(src, i, i)).position(0);
            ((Buffer)intbuffer).position(0).limit(i);
        }

        GlStateManager.bindTexture(multiTex.norm);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, posX, posY, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intbuffer);

        if (src.length == i * 3)
        {
            ((Buffer)intbuffer).clear();
            intbuffer.put(src, i * 2, i);
            ((Buffer)intbuffer).position(0).limit(i);
        }

        GlStateManager.bindTexture(multiTex.spec);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, posX, posY, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intbuffer);
        GlStateManager.activeTexture(33984);
    }

    public static ResourceLocation getNSMapLocation(ResourceLocation location, String mapName)
    {
        if (location == null)
        {
            return null;
        }
        else
        {
            String s = location.getPath();
            String[] astring = s.split(".png");
            String s1 = astring[0];
            return new ResourceLocation(location.getNamespace(), s1 + "_" + mapName + ".png");
        }
    }

    private static NativeImage loadNSMapImage(IResourceManager manager, ResourceLocation location, int width, int height, int defaultColor)
    {
        NativeImage nativeimage = loadNSMapFile(manager, location, width, height);

        if (nativeimage == null)
        {
            nativeimage = new NativeImage(width, height, false);
            int i = TextureUtils.toAbgr(defaultColor);
            nativeimage.fillAreaRGBA(0, 0, width, height, i);
        }

        return nativeimage;
    }

    private static NativeImage makeImageColor(int width, int height, int defaultColor)
    {
        NativeImage nativeimage = new NativeImage(width, height, false);
        int i = TextureUtils.toAbgr(defaultColor);
        nativeimage.fillRGBA(i);
        return nativeimage;
    }

    private static NativeImage loadNSMapFile(IResourceManager manager, ResourceLocation location, int width, int height)
    {
        if (location == null)
        {
            return null;
        }
        else
        {
            try
            {
                IResource iresource = manager.getResource(location);
                NativeImage nativeimage = NativeImage.read(iresource.getInputStream());

                if (nativeimage == null)
                {
                    return null;
                }
                else if (nativeimage.getWidth() == width && nativeimage.getHeight() == height)
                {
                    return nativeimage;
                }
                else
                {
                    nativeimage.close();
                    return null;
                }
            }
            catch (IOException ioexception)
            {
                return null;
            }
        }
    }

    public static void loadSimpleTextureNS(int textureID, NativeImage nativeImage, boolean blur, boolean clamp, IResourceManager resourceManager, ResourceLocation location, MultiTexID multiTex)
    {
        int i = nativeImage.getWidth();
        int j = nativeImage.getHeight();
        ResourceLocation resourcelocation = getNSMapLocation(location, "n");
        NativeImage nativeimage = loadNSMapImage(resourceManager, resourcelocation, i, j, -8421377);
        TextureUtil.prepareImage(multiTex.norm, 0, i, j);
        nativeimage.uploadTextureSub(0, 0, 0, 0, 0, i, j, blur, clamp, false, true);
        ResourceLocation resourcelocation1 = getNSMapLocation(location, "s");
        NativeImage nativeimage1 = loadNSMapImage(resourceManager, resourcelocation1, i, j, 0);
        TextureUtil.prepareImage(multiTex.spec, 0, i, j);
        nativeimage1.uploadTextureSub(0, 0, 0, 0, 0, i, j, blur, clamp, false, true);
        GlStateManager.bindTexture(multiTex.base);
    }

    public static void mergeImage(int[] aint, int dstoff, int srcoff, int size)
    {
    }

    public static int blendColor(int color1, int color2, int factor1)
    {
        int i = 255 - factor1;
        return ((color1 >>> 24 & 255) * factor1 + (color2 >>> 24 & 255) * i) / 255 << 24 | ((color1 >>> 16 & 255) * factor1 + (color2 >>> 16 & 255) * i) / 255 << 16 | ((color1 >>> 8 & 255) * factor1 + (color2 >>> 8 & 255) * i) / 255 << 8 | ((color1 >>> 0 & 255) * factor1 + (color2 >>> 0 & 255) * i) / 255 << 0;
    }

    public static void updateTextureMinMagFilter()
    {
        TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
        Texture texture = texturemanager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

        if (texture != null)
        {
            MultiTexID multitexid = texture.getMultiTexID();
            GlStateManager.bindTexture(multitexid.base);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.texMinFilValue[Shaders.configTexMinFilB]);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, Shaders.texMagFilValue[Shaders.configTexMagFilB]);
            GlStateManager.bindTexture(multitexid.norm);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.texMinFilValue[Shaders.configTexMinFilN]);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, Shaders.texMagFilValue[Shaders.configTexMagFilN]);
            GlStateManager.bindTexture(multitexid.spec);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.texMinFilValue[Shaders.configTexMinFilS]);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, Shaders.texMagFilValue[Shaders.configTexMagFilS]);
            GlStateManager.bindTexture(0);
        }
    }

    public static int[][] getFrameTexData(int[][] src, int width, int height, int frameIndex)
    {
        int i = src.length;
        int[][] aint = new int[i][];

        for (int j = 0; j < i; ++j)
        {
            int[] aint1 = src[j];

            if (aint1 != null)
            {
                int k = (width >> j) * (height >> j);
                int[] aint2 = new int[k * 3];
                aint[j] = aint2;
                int l = aint1.length / 3;
                int i1 = k * frameIndex;
                int j1 = 0;
                System.arraycopy(aint1, i1, aint2, j1, k);
                i1 = i1 + l;
                j1 = j1 + k;
                System.arraycopy(aint1, i1, aint2, j1, k);
                i1 = i1 + l;
                j1 = j1 + k;
                System.arraycopy(aint1, i1, aint2, j1, k);
            }
        }

        return aint;
    }

    public static int[][] prepareAF(TextureAtlasSprite tas, int[][] src, int width, int height)
    {
        boolean flag = true;
        return src;
    }

    public static void fixTransparentColor(TextureAtlasSprite tas, int[] aint)
    {
    }
}
