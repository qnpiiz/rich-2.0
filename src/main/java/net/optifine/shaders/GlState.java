package net.optifine.shaders;

import com.mojang.blaze3d.platform.GlStateManager;

public class GlState
{
    private static ShadersFramebuffer activeFramebuffer;

    public static void bindFramebuffer(ShadersFramebuffer framebufferIn)
    {
        activeFramebuffer = framebufferIn;
        GlStateManager.bindFramebuffer(36160, activeFramebuffer.getGlFramebuffer());
    }

    public static ShadersFramebuffer getFramebuffer()
    {
        return activeFramebuffer;
    }

    public static void setFramebufferTexture2D(int target, int attachment, int texTarget, int texture, int level)
    {
        activeFramebuffer.setFramebufferTexture2D(target, attachment, texTarget, texture, level);
    }

    public static void setDrawBuffers(DrawBuffers drawBuffers)
    {
        activeFramebuffer.setDrawBuffers(drawBuffers);
    }

    public static DrawBuffers getDrawBuffers()
    {
        return activeFramebuffer.getDrawBuffers();
    }
}
