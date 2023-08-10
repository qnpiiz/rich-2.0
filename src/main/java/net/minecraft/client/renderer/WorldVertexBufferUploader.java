package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.optifine.Config;
import net.optifine.render.MultiTextureData;
import net.optifine.render.MultiTextureRenderer;
import net.optifine.shaders.SVertexBuilder;
import org.lwjgl.system.MemoryUtil;

public class WorldVertexBufferUploader
{
    public static void draw(BufferBuilder bufferBuilderIn)
    {
        if (!RenderSystem.isOnRenderThread())
        {
            RenderSystem.recordRenderCall(() ->
            {
                Pair<BufferBuilder.DrawState, ByteBuffer> pair1 = bufferBuilderIn.getNextBuffer();
                BufferBuilder.DrawState bufferbuilder$drawstate1 = pair1.getFirst();
                draw(pair1.getSecond(), bufferbuilder$drawstate1.getDrawMode(), bufferbuilder$drawstate1.getFormat(), bufferbuilder$drawstate1.getVertexCount(), bufferbuilder$drawstate1.getMultiTextureData());
            });
        }
        else
        {
            Pair<BufferBuilder.DrawState, ByteBuffer> pair = bufferBuilderIn.getNextBuffer();
            BufferBuilder.DrawState bufferbuilder$drawstate = pair.getFirst();
            draw(pair.getSecond(), bufferbuilder$drawstate.getDrawMode(), bufferbuilder$drawstate.getFormat(), bufferbuilder$drawstate.getVertexCount(), bufferbuilder$drawstate.getMultiTextureData());
        }
    }

    private static void draw(ByteBuffer bufferIn, int modeIn, VertexFormat vertexFormatIn, int countIn)
    {
        draw(bufferIn, modeIn, vertexFormatIn, countIn, (MultiTextureData)null);
    }

    private static void draw(ByteBuffer p_draw_0_, int p_draw_1_, VertexFormat p_draw_2_, int p_draw_3_, MultiTextureData p_draw_4_)
    {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        ((Buffer)p_draw_0_).clear();

        if (p_draw_3_ > 0)
        {
            p_draw_2_.setupBufferState(MemoryUtil.memAddress(p_draw_0_));
            boolean flag = Config.isShaders() && SVertexBuilder.preDrawArrays(p_draw_2_, p_draw_0_);

            if (p_draw_4_ != null)
            {
                MultiTextureRenderer.draw(p_draw_1_, p_draw_4_);
            }
            else
            {
                GlStateManager.drawArrays(p_draw_1_, 0, p_draw_3_);
            }

            if (flag)
            {
                SVertexBuilder.postDrawArrays();
            }

            p_draw_2_.clearBufferState();
        }
    }
}
