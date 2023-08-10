package net.optifine.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.optifine.util.TextureUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class HFNoiseTexture implements ICustomTexture
{
    private int texID = GL11.glGenTextures();
    private int textureUnit = 15;

    public HFNoiseTexture(int width, int height)
    {
        byte[] abyte = this.genHFNoiseImage(width, height);
        ByteBuffer bytebuffer = BufferUtils.createByteBuffer(abyte.length);
        bytebuffer.put(abyte);
        ((Buffer)bytebuffer).flip();
        GlStateManager.bindTexture(this.texID);
        TextureUtils.resetDataUnpacking();
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, bytebuffer);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GlStateManager.bindTexture(0);
    }

    public int getID()
    {
        return this.texID;
    }

    public void deleteTexture()
    {
        GlStateManager.deleteTexture(this.texID);
        this.texID = 0;
    }

    private int random(int seed)
    {
        seed = seed ^ seed << 13;
        seed = seed ^ seed >> 17;
        return seed ^ seed << 5;
    }

    private byte random(int x, int y, int z)
    {
        int i = (this.random(x) + this.random(y * 19)) * this.random(z * 23) - z;
        return (byte)(this.random(i) % 128);
    }

    private byte[] genHFNoiseImage(int width, int height)
    {
        byte[] abyte = new byte[width * height * 3];
        int i = 0;

        for (int j = 0; j < height; ++j)
        {
            for (int k = 0; k < width; ++k)
            {
                for (int l = 1; l < 4; ++l)
                {
                    abyte[i++] = this.random(k, j, l);
                }
            }
        }

        return abyte;
    }

    public int getTextureId()
    {
        return this.texID;
    }

    public int getTextureUnit()
    {
        return this.textureUnit;
    }
}
