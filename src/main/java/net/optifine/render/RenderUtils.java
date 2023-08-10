package net.optifine.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeBuffers;

public class RenderUtils
{
    private static boolean flushRenderBuffers = true;
    private static Minecraft mc = Minecraft.getInstance();

    public static boolean setFlushRenderBuffers(boolean flushRenderBuffers)
    {
        boolean flag = RenderUtils.flushRenderBuffers;
        RenderUtils.flushRenderBuffers = flushRenderBuffers;
        return flag;
    }

    public static boolean isFlushRenderBuffers()
    {
        return flushRenderBuffers;
    }

    public static void flushRenderBuffers()
    {
        if (flushRenderBuffers)
        {
            RenderTypeBuffers rendertypebuffers = mc.getRenderTypeBuffers();
            rendertypebuffers.getBufferSource().flushRenderBuffers();
            rendertypebuffers.getCrumblingBufferSource().flushRenderBuffers();
        }
    }
}
