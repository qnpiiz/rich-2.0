package net.optifine.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL30;

public class FixedFramebuffer
{
    private String name;
    private int width;
    private int height;
    private int[] colorTextures;
    private int[] colorAttachments;
    private boolean depthFilterNearest;
    private boolean depthFilterHardware;
    private int glFramebuffer;
    private int depthTexture;
    private IntBuffer glDrawBuffers;

    public FixedFramebuffer(String name, int width, int height, int[] colorTextures, int[] colorAttachments, boolean depthFilterNearest, boolean depthFilterHardware)
    {
        this.name = name;
        this.width = width;
        this.height = height;
        this.colorTextures = colorTextures;
        this.colorAttachments = colorAttachments;
        this.depthFilterNearest = depthFilterNearest;
        this.depthFilterHardware = depthFilterHardware;
    }

    public void setup()
    {
        if (this.exists())
        {
            this.delete();
        }

        this.glFramebuffer = EXTFramebufferObject.glGenFramebuffersEXT();
        this.bindFramebuffer();
        GL30.glDrawBuffers(0);
        GL30.glReadBuffer(0);
        this.depthTexture = GL30.glGenTextures();
        GlStateManager.bindTexture(this.depthTexture);
        GL30.glTexParameteri(3553, 10242, 33071);
        GL30.glTexParameteri(3553, 10243, 33071);
        int i = this.depthFilterNearest ? 9728 : 9729;
        GL30.glTexParameteri(3553, 10241, i);
        GL30.glTexParameteri(3553, 10240, i);

        if (this.depthFilterHardware)
        {
            GL30.glTexParameteri(3553, 34892, 34894);
        }

        GL30.glTexImage2D(3553, 0, 6402, this.width, this.height, 0, 6402, 5126, (FloatBuffer)null);
        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, this.depthTexture, 0);
        Shaders.checkGLError("FBS " + this.name + " depth");

        for (int j = 0; j < this.colorTextures.length; ++j)
        {
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, this.colorAttachments[j], 3553, this.colorTextures[j], 0);
            Shaders.checkGLError("FBS " + this.name + " color");
        }

        GlStateManager.bindTexture(0);

        if (this.colorTextures.length > 0)
        {
            this.glDrawBuffers = BufferUtils.createIntBuffer(this.colorAttachments.length);

            for (int l = 0; l < this.colorAttachments.length; ++l)
            {
                int k = this.colorAttachments[l];
                this.glDrawBuffers.put(l, k);
            }

            GL30.glDrawBuffers(this.glDrawBuffers);
            GL30.glReadBuffer(0);
        }

        int i1 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);

        if (i1 != 36053)
        {
            Shaders.printChatAndLogError("[Shaders] Error creating framebuffer: " + this.name + ", status: " + i1);
        }
        else
        {
            SMCLog.info("Framebuffer created: " + this.name);
        }
    }

    public void bindFramebuffer()
    {
        GlStateManager.bindFramebuffer(36160, this.glFramebuffer);
    }

    public void delete()
    {
        if (this.glFramebuffer != 0)
        {
            EXTFramebufferObject.glDeleteFramebuffersEXT(this.glFramebuffer);
            this.glFramebuffer = 0;
        }

        if (this.depthTexture != 0)
        {
            GlStateManager.deleteTexture(this.depthTexture);
            this.depthTexture = 0;
        }

        this.glDrawBuffers = null;
    }

    public boolean exists()
    {
        return this.glFramebuffer != 0;
    }

    public String toString()
    {
        return "" + this.name;
    }
}
