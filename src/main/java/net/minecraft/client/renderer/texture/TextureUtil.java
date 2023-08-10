package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;

public class TextureUtil
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static int generateTextureId()
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

        if (SharedConstants.developmentMode)
        {
            int[] aint = new int[ThreadLocalRandom.current().nextInt(15) + 1];
            GlStateManager.genTextures(aint);
            int i = GlStateManager.genTexture();
            GlStateManager.deleteTextures(aint);
            return i;
        }
        else
        {
            return GlStateManager.genTexture();
        }
    }

    public static void releaseTextureId(int textureId)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.deleteTexture(textureId);
    }

    public static void prepareImage(int textureId, int width, int height)
    {
        prepareImage(NativeImage.PixelFormatGLCode.RGBA, textureId, 0, width, height);
    }

    public static void prepareImage(NativeImage.PixelFormatGLCode pixelFormat, int textureId, int width, int height)
    {
        prepareImage(pixelFormat, textureId, 0, width, height);
    }

    public static void prepareImage(int textureId, int mipmapLevel, int width, int height)
    {
        prepareImage(NativeImage.PixelFormatGLCode.RGBA, textureId, mipmapLevel, width, height);
    }

    public static void prepareImage(NativeImage.PixelFormatGLCode pixelFormat, int textureId, int mipmapLevel, int width, int height)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        bindTexture(textureId);

        if (mipmapLevel >= 0)
        {
            GlStateManager.texParameter(3553, 33085, mipmapLevel);
            GlStateManager.texParameter(3553, 33082, 0);
            GlStateManager.texParameter(3553, 33083, mipmapLevel);
            GlStateManager.texParameter(3553, 34049, 0.0F);
        }

        for (int i = 0; i <= mipmapLevel; ++i)
        {
            GlStateManager.texImage2D(3553, i, pixelFormat.getGlFormat(), width >> i, height >> i, 0, 6408, 5121, (IntBuffer)null);
        }
    }

    private static void bindTexture(int textureId)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.bindTexture(textureId);
    }

    public static ByteBuffer readToBuffer(InputStream inputStreamIn) throws IOException
    {
        ByteBuffer bytebuffer;

        if (inputStreamIn instanceof FileInputStream)
        {
            FileInputStream fileinputstream = (FileInputStream)inputStreamIn;
            FileChannel filechannel = fileinputstream.getChannel();
            bytebuffer = MemoryUtil.memAlloc((int)filechannel.size() + 1);

            while (filechannel.read(bytebuffer) != -1)
            {
            }
        }
        else
        {
            bytebuffer = MemoryUtil.memAlloc(8192);
            ReadableByteChannel readablebytechannel = Channels.newChannel(inputStreamIn);

            while (readablebytechannel.read(bytebuffer) != -1)
            {
                if (bytebuffer.remaining() == 0)
                {
                    bytebuffer = MemoryUtil.memRealloc(bytebuffer, bytebuffer.capacity() * 2);
                }
            }
        }

        return bytebuffer;
    }

    public static String readResourceAsString(InputStream inputStreamIn)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        ByteBuffer bytebuffer = null;

        try
        {
            bytebuffer = readToBuffer(inputStreamIn);
            int i = bytebuffer.position();
            ((Buffer)bytebuffer).rewind();
            return MemoryUtil.memASCII(bytebuffer, i);
        }
        catch (IOException ioexception)
        {
        }
        finally
        {
            if (bytebuffer != null)
            {
                MemoryUtil.memFree(bytebuffer);
            }
        }

        return null;
    }

    public static void initTexture(IntBuffer bufferIn, int width, int height)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPixelStorei(GL11.GL_UNPACK_SWAP_BYTES, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_LSB_FIRST, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, bufferIn);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    }
}
