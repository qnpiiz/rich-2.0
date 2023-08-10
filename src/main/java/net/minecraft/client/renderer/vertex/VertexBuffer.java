package net.minecraft.client.renderer.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Matrix4f;
import net.optifine.render.MultiTextureData;
import net.optifine.render.MultiTextureRenderer;
import net.optifine.render.VboRange;
import net.optifine.render.VboRegion;

public class VertexBuffer implements AutoCloseable
{
    private int glBufferId;
    private final VertexFormat vertexFormat;
    private int count;
    private VboRegion vboRegion;
    private VboRange vboRange;
    private int drawModeCustom;
    private MultiTextureData multiTextureData;

    public VertexBuffer(VertexFormat vertexFormatIn)
    {
        this.vertexFormat = vertexFormatIn;
        RenderSystem.glGenBuffers((p_lambda$new$0_1_) ->
        {
            this.glBufferId = p_lambda$new$0_1_;
        });
    }

    public void bindBuffer()
    {
        GlStateManager.bindBuffer(34962, this.glBufferId);
    }

    public void upload(BufferBuilder bufferIn)
    {
        if (!RenderSystem.isOnRenderThread())
        {
            RenderSystem.recordRenderCall(() ->
            {
                this.uploadRaw(bufferIn);
            });
        }
        else
        {
            this.uploadRaw(bufferIn);
        }
    }

    public CompletableFuture<Void> uploadLater(BufferBuilder bufferIn)
    {
        if (!RenderSystem.isOnRenderThread())
        {
            return CompletableFuture.runAsync(() ->
            {
                this.uploadRaw(bufferIn);
            }, (p_lambda$uploadLater$3_0_) ->
            {
                RenderSystem.recordRenderCall(p_lambda$uploadLater$3_0_::run);
            });
        }
        else
        {
            this.uploadRaw(bufferIn);
            return CompletableFuture.completedFuture((Void)null);
        }
    }

    private void uploadRaw(BufferBuilder bufferIn)
    {
        Pair<BufferBuilder.DrawState, ByteBuffer> pair = bufferIn.getNextBuffer();
        this.drawModeCustom = 0;
        BufferBuilder.DrawState bufferbuilder$drawstate = pair.getFirst();

        if (bufferbuilder$drawstate.getDrawMode() != 7)
        {
            this.drawModeCustom = bufferbuilder$drawstate.getDrawMode();
        }

        if (this.vboRegion != null)
        {
            ByteBuffer bytebuffer1 = pair.getSecond();
            this.vboRegion.bufferData(bytebuffer1, this.vboRange);
        }
        else
        {
            this.multiTextureData = bufferbuilder$drawstate.getMultiTextureData();

            if (this.glBufferId != -1)
            {
                ByteBuffer bytebuffer = pair.getSecond();
                this.count = bytebuffer.remaining() / this.vertexFormat.getSize();
                this.bindBuffer();
                RenderSystem.glBufferData(34962, bytebuffer, 35044);
                unbindBuffer();
            }
        }
    }

    public void draw(Matrix4f matrixIn, int modeIn)
    {
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(matrixIn);

        if (this.drawModeCustom > 0)
        {
            modeIn = this.drawModeCustom;
        }

        if (this.vboRegion != null)
        {
            this.vboRegion.drawArrays(modeIn, this.vboRange);
        }
        else if (this.multiTextureData != null)
        {
            MultiTextureRenderer.draw(modeIn, this.multiTextureData);
        }
        else
        {
            RenderSystem.drawArrays(modeIn, 0, this.count);
        }

        RenderSystem.popMatrix();
    }

    public void draw(int p_draw_1_)
    {
        if (this.drawModeCustom > 0)
        {
            p_draw_1_ = this.drawModeCustom;
        }

        if (this.vboRegion != null)
        {
            this.vboRegion.drawArrays(p_draw_1_, this.vboRange);
        }
        else if (this.multiTextureData != null)
        {
            MultiTextureRenderer.draw(p_draw_1_, this.multiTextureData);
        }
        else
        {
            RenderSystem.drawArrays(p_draw_1_, 0, this.count);
        }
    }

    public static void unbindBuffer()
    {
        GlStateManager.bindBuffer(34962, 0);
    }

    public void close()
    {
        if (this.glBufferId >= 0)
        {
            RenderSystem.glDeleteBuffers(this.glBufferId);
            this.glBufferId = -1;
        }
    }

    public void setVboRegion(VboRegion p_setVboRegion_1_)
    {
        if (p_setVboRegion_1_ != null)
        {
            this.close();
            this.vboRegion = p_setVboRegion_1_;
            this.vboRange = new VboRange();
        }
    }

    public VboRegion getVboRegion()
    {
        return this.vboRegion;
    }
}
