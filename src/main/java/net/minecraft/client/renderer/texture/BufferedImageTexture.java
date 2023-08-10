package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.resources.IResourceManager;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;

public class BufferedImageTexture extends Texture {

    private static final IntBuffer DATA_BUFFER = GLAllocation.createDirectByteBuffer(4194304 << 2).asIntBuffer();

    private final int[] dynamicTextureData;
    private final int width, height;

    public BufferedImageTexture(BufferedImage bufferedImage) {
        this(bufferedImage.getWidth(), bufferedImage.getHeight());
        bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.dynamicTextureData, 0, bufferedImage.getWidth());
        this.updateDynamicTexture();
    }

    public BufferedImageTexture(int textureWidth, int textureHeight) {
        boolean shadersInitialized = false;
        this.width = textureWidth;
        this.height = textureHeight;
        this.dynamicTextureData = new int[textureWidth * textureHeight * 3];

        allocateTexture(this.getGlTextureId(), textureWidth, textureHeight);
    }

    public static void allocateTexture(int textureId, int width, int height) {
        allocateTextureImpl(textureId, 0, width, height);
    }

    public static void allocateTextureImpl(int glTextureId, int mipmapLevels, int width, int height) {
        GlStateManager.deleteTexture(glTextureId);
        GlStateManager.bindTexture(glTextureId);

        if (mipmapLevels >= 0) {
            GL11.glTexParameteri(3553, 33085, mipmapLevels);
            GL11.glTexParameteri(3553, 33082, 0);
            GL11.glTexParameteri(3553, 33083, mipmapLevels);
            GL11.glTexParameterf(3553, 34049, 0.0F);
        }

        for (int i = 0; i <= mipmapLevels; ++i) {
            GL11.glTexImage2D(3553, i, 6408, width >> i, height >> i, 0, 32993, 33639, (IntBuffer) null);
        }
    }

    @Override
    public void loadTexture(IResourceManager manager) throws IOException {}

    public void updateDynamicTexture() {
        uploadTexture(this.getGlTextureId(), this.dynamicTextureData, this.width, this.height);
    }

    public static void uploadTexture(int textureId, int[] p_110988_1_, int p_110988_2_, int p_110988_3_) {
        GlStateManager.bindTexture(textureId);
        uploadTextureSub(0, p_110988_1_, p_110988_2_, p_110988_3_, 0, 0, false, false, false);
    }

    public static void uploadTextureSub(int p_147947_0_, int[] p_147947_1_, int p_147947_2_, int p_147947_3_, int p_147947_4_, int p_147947_5_, boolean p_147947_6_, boolean p_147947_7_, boolean p_147947_8_) {
        int i = 4194304 / p_147947_2_;
        setTextureBlurMipmap(p_147947_6_, p_147947_8_);
        setTextureClamped(p_147947_7_);
        int j;

        for (int k = 0; k < p_147947_2_ * p_147947_3_; k += p_147947_2_ * j)
        {
            int l = k / p_147947_2_;
            j = Math.min(i, p_147947_3_ - l);
            int i1 = p_147947_2_ * j;
            copyToBufferPos(p_147947_1_, k, i1);
            GL11.glTexSubImage2D(3553, p_147947_0_, p_147947_4_, p_147947_5_ + l, p_147947_2_, j, 32993, 33639, DATA_BUFFER);
        }
    }

    public static void copyToBufferPos(int[] p_110994_0_, int p_110994_1_, int p_110994_2_) {
        int[] aint = p_110994_0_;

        DATA_BUFFER.clear();
        DATA_BUFFER.put(aint, p_110994_1_, p_110994_2_);
        DATA_BUFFER.position(0).limit(p_110994_2_);
    }

    public static void setTextureBlurMipmap(boolean p_147954_0_, boolean p_147954_1_) {
        if (p_147954_0_)
        {
            GL11.glTexParameteri(3553, 10241, p_147954_1_ ? 9987 : 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
        }
        else
        {
            int i = 9986;
            GL11.glTexParameteri(3553, 10241, p_147954_1_ ? i : 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
        }
    }

    public static void setTextureClamped(boolean p_110997_0_) {
        if (p_110997_0_)
        {
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
        }
        else
        {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }
    }

    public static int uploadTextureImageAllocate(int textureId, BufferedImage texture, boolean blur, boolean clamp) {
        allocateTexture(textureId, texture.getWidth(), texture.getHeight());
        return uploadTextureImageSub(textureId, texture, 0, 0, blur, clamp);
    }

    public static int uploadTextureImageSub(int textureId, BufferedImage p_110995_1_, int p_110995_2_, int p_110995_3_, boolean p_110995_4_, boolean p_110995_5_) {
        GlStateManager.bindTexture(textureId);
        uploadTextureImageSubImpl(p_110995_1_, p_110995_2_, p_110995_3_, p_110995_4_, p_110995_5_);
        return textureId;
    }

    private static void uploadTextureImageSubImpl(BufferedImage p_110993_0_, int p_110993_1_, int p_110993_2_, boolean p_110993_3_, boolean p_110993_4_) {
        int i = p_110993_0_.getWidth();
        int j = p_110993_0_.getHeight();
        int k = 4194304 / i;
        int[] aint = new int[k * i];
        setTextureBlurMipmap(p_110993_3_, false);
        setTextureClamped(p_110993_4_);

        for (int l = 0; l < i * j; l += i * k) {
            int i1 = l / i;
            int j1 = Math.min(k, j - i1);
            int k1 = i * j1;
            p_110993_0_.getRGB(0, i1, i, j1, aint, 0, i);
            copyToBuffer(aint, k1);
            GL11.glTexSubImage2D(3553, 0, p_110993_1_, p_110993_2_ + i1, i, j1, 32993, 33639, DATA_BUFFER);
        }
    }

    private static void copyToBuffer(int[] p_110990_0_, int p_110990_1_) {
        copyToBufferPos(p_110990_0_, 0, p_110990_1_);
    }

    public int[] getTextureData() {
        return this.dynamicTextureData;
    }
}
