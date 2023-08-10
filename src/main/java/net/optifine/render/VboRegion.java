package net.optifine.render;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.optifine.Config;
import net.optifine.shaders.ShadersRender;
import net.optifine.util.LinkedList;

public class VboRegion
{
    private RenderType layer = null;
    private int glBufferId = GlStateManager.genBuffers();
    private int capacity = 4096;
    private int positionTop = 0;
    private int sizeUsed;
    private LinkedList<VboRange> rangeList = new LinkedList<>();
    private VboRange compactRangeLast = null;
    private IntBuffer bufferIndexVertex = Config.createDirectIntBuffer(this.capacity);
    private IntBuffer bufferCountVertex = Config.createDirectIntBuffer(this.capacity);
    private final int vertexBytes = DefaultVertexFormats.BLOCK.getSize();
    private int drawMode = 7;
    private boolean isShaders = Config.isShaders();

    public VboRegion(RenderType layer)
    {
        this.layer = layer;
        this.bindBuffer();
        long i = this.toBytes(this.capacity);
        GlStateManager.bufferData(GlStateManager.GL_ARRAY_BUFFER, i, GlStateManager.GL_STATIC_DRAW);
        this.unbindBuffer();
    }

    public void bufferData(ByteBuffer data, VboRange range)
    {
        if (this.glBufferId >= 0)
        {
            int i = range.getPosition();
            int j = range.getSize();
            int k = this.toVertex((long)data.limit());

            if (k <= 0)
            {
                if (i >= 0)
                {
                    range.setPosition(-1);
                    range.setSize(0);
                    this.rangeList.remove(range.getNode());
                    this.sizeUsed -= j;
                }
            }
            else
            {
                if (k > j)
                {
                    range.setPosition(this.positionTop);
                    range.setSize(k);
                    this.positionTop += k;

                    if (i >= 0)
                    {
                        this.rangeList.remove(range.getNode());
                    }

                    this.rangeList.addLast(range.getNode());
                }

                range.setSize(k);
                this.sizeUsed += k - j;
                this.checkVboSize(range.getPositionNext());
                long l = this.toBytes(range.getPosition());
                this.bindBuffer();
                GlStateManager.bufferSubData(GlStateManager.GL_ARRAY_BUFFER, l, data);
                this.unbindBuffer();

                if (this.positionTop > this.sizeUsed * 11 / 10)
                {
                    this.compactRanges(1);
                }
            }
        }
    }

    private void compactRanges(int countMax)
    {
        if (!this.rangeList.isEmpty())
        {
            VboRange vborange = this.compactRangeLast;

            if (vborange == null || !this.rangeList.contains(vborange.getNode()))
            {
                vborange = this.rangeList.getFirst().getItem();
            }

            int i = vborange.getPosition();
            VboRange vborange1 = vborange.getPrev();

            if (vborange1 == null)
            {
                i = 0;
            }
            else
            {
                i = vborange1.getPositionNext();
            }

            int j = 0;

            while (vborange != null && j < countMax)
            {
                ++j;

                if (vborange.getPosition() == i)
                {
                    i += vborange.getSize();
                    vborange = vborange.getNext();
                }
                else
                {
                    int k = vborange.getPosition() - i;

                    if (vborange.getSize() <= k)
                    {
                        this.copyVboData(vborange.getPosition(), i, vborange.getSize());
                        vborange.setPosition(i);
                        i += vborange.getSize();
                        vborange = vborange.getNext();
                    }
                    else
                    {
                        this.checkVboSize(this.positionTop + vborange.getSize());
                        this.copyVboData(vborange.getPosition(), this.positionTop, vborange.getSize());
                        vborange.setPosition(this.positionTop);
                        this.positionTop += vborange.getSize();
                        VboRange vborange2 = vborange.getNext();
                        this.rangeList.remove(vborange.getNode());
                        this.rangeList.addLast(vborange.getNode());
                        vborange = vborange2;
                    }
                }
            }

            if (vborange == null)
            {
                this.positionTop = this.rangeList.getLast().getItem().getPositionNext();
            }

            this.compactRangeLast = vborange;
        }
    }

    private void checkRanges()
    {
        int i = 0;
        int j = 0;

        for (VboRange vborange = this.rangeList.getFirst().getItem(); vborange != null; vborange = vborange.getNext())
        {
            ++i;
            j += vborange.getSize();

            if (vborange.getPosition() < 0 || vborange.getSize() <= 0 || vborange.getPositionNext() > this.positionTop)
            {
                throw new RuntimeException("Invalid range: " + vborange);
            }

            VboRange vborange1 = vborange.getPrev();

            if (vborange1 != null && vborange.getPosition() < vborange1.getPositionNext())
            {
                throw new RuntimeException("Invalid range: " + vborange);
            }

            VboRange vborange2 = vborange.getNext();

            if (vborange2 != null && vborange.getPositionNext() > vborange2.getPosition())
            {
                throw new RuntimeException("Invalid range: " + vborange);
            }
        }

        if (i != this.rangeList.getSize())
        {
            throw new RuntimeException("Invalid count: " + i + " <> " + this.rangeList.getSize());
        }
        else if (j != this.sizeUsed)
        {
            throw new RuntimeException("Invalid size: " + j + " <> " + this.sizeUsed);
        }
    }

    private void checkVboSize(int sizeMin)
    {
        if (this.capacity < sizeMin)
        {
            this.expandVbo(sizeMin);
        }
    }

    private void copyVboData(int posFrom, int posTo, int size)
    {
        long i = this.toBytes(posFrom);
        long j = this.toBytes(posTo);
        long k = this.toBytes(size);
        GlStateManager.bindBuffer(GlStateManager.GL_COPY_READ_BUFFER, this.glBufferId);
        GlStateManager.bindBuffer(GlStateManager.GL_COPY_WRITE_BUFFER, this.glBufferId);
        GlStateManager.copyBufferSubData(GlStateManager.GL_COPY_READ_BUFFER, GlStateManager.GL_COPY_WRITE_BUFFER, i, j, k);
        Config.checkGlError("Copy VBO range");
        GlStateManager.bindBuffer(GlStateManager.GL_COPY_READ_BUFFER, 0);
        GlStateManager.bindBuffer(GlStateManager.GL_COPY_WRITE_BUFFER, 0);
    }

    private void expandVbo(int sizeMin)
    {
        int i;

        for (i = this.capacity * 6 / 4; i < sizeMin; i = i * 6 / 4)
        {
        }

        long j = this.toBytes(this.capacity);
        long k = this.toBytes(i);
        int l = GlStateManager.genBuffers();
        GlStateManager.bindBuffer(GlStateManager.GL_ARRAY_BUFFER, l);
        GlStateManager.bufferData(GlStateManager.GL_ARRAY_BUFFER, k, GlStateManager.GL_STATIC_DRAW);
        Config.checkGlError("Expand VBO");
        GlStateManager.bindBuffer(GlStateManager.GL_ARRAY_BUFFER, 0);
        GlStateManager.bindBuffer(GlStateManager.GL_COPY_READ_BUFFER, this.glBufferId);
        GlStateManager.bindBuffer(GlStateManager.GL_COPY_WRITE_BUFFER, l);
        GlStateManager.copyBufferSubData(GlStateManager.GL_COPY_READ_BUFFER, GlStateManager.GL_COPY_WRITE_BUFFER, 0L, 0L, j);
        Config.checkGlError("Copy VBO: " + k);
        GlStateManager.bindBuffer(GlStateManager.GL_COPY_READ_BUFFER, 0);
        GlStateManager.bindBuffer(GlStateManager.GL_COPY_WRITE_BUFFER, 0);
        GlStateManager.deleteBuffers(this.glBufferId);
        this.bufferIndexVertex = Config.createDirectIntBuffer(i);
        this.bufferCountVertex = Config.createDirectIntBuffer(i);
        this.glBufferId = l;
        this.capacity = i;
    }

    public void bindBuffer()
    {
        GlStateManager.bindBuffer(GlStateManager.GL_ARRAY_BUFFER, this.glBufferId);
    }

    public void drawArrays(int drawMode, VboRange range)
    {
        if (this.drawMode != drawMode)
        {
            if (this.bufferIndexVertex.position() > 0)
            {
                throw new IllegalArgumentException("Mixed region draw modes: " + this.drawMode + " != " + drawMode);
            }

            this.drawMode = drawMode;
        }

        this.bufferIndexVertex.put(range.getPosition());
        this.bufferCountVertex.put(range.getSize());
    }

    public void finishDraw()
    {
        this.bindBuffer();
        DefaultVertexFormats.BLOCK.setupBufferState(0L);

        if (this.isShaders)
        {
            ShadersRender.setupArrayPointersVbo();
        }

        ((Buffer)this.bufferIndexVertex).flip();
        ((Buffer)this.bufferCountVertex).flip();
        GlStateManager.glMultiDrawArrays(this.drawMode, this.bufferIndexVertex, this.bufferCountVertex);
        ((Buffer)this.bufferIndexVertex).limit(this.bufferIndexVertex.capacity());
        ((Buffer)this.bufferCountVertex).limit(this.bufferCountVertex.capacity());

        if (this.positionTop > this.sizeUsed * 11 / 10)
        {
            this.compactRanges(1);
        }
    }

    public void unbindBuffer()
    {
        GlStateManager.bindBuffer(GlStateManager.GL_ARRAY_BUFFER, 0);
    }

    public void deleteGlBuffers()
    {
        if (this.glBufferId >= 0)
        {
            GlStateManager.deleteBuffers(this.glBufferId);
            this.glBufferId = -1;
        }
    }

    private long toBytes(int vertex)
    {
        return (long)vertex * (long)this.vertexBytes;
    }

    private int toVertex(long bytes)
    {
        return (int)(bytes / (long)this.vertexBytes);
    }

    public int getPositionTop()
    {
        return this.positionTop;
    }
}
