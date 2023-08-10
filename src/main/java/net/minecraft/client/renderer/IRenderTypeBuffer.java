package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.ResourceLocation;
import net.optifine.render.VertexBuilderDummy;
import net.optifine.util.TextureUtils;

public interface IRenderTypeBuffer
{
    static IRenderTypeBuffer.Impl getImpl(BufferBuilder builderIn)
    {
        return getImpl(ImmutableMap.of(), builderIn);
    }

    static IRenderTypeBuffer.Impl getImpl(Map<RenderType, BufferBuilder> mapBuildersIn, BufferBuilder builderIn)
    {
        return new IRenderTypeBuffer.Impl(builderIn, mapBuildersIn);
    }

    IVertexBuilder getBuffer(RenderType p_getBuffer_1_);

default void flushRenderBuffers()
    {
    }

    public static class Impl implements IRenderTypeBuffer
    {
        protected final BufferBuilder buffer;
        protected final Map<RenderType, BufferBuilder> fixedBuffers;
        protected RenderType lastRenderType = null;
        protected final Set<BufferBuilder> startedBuffers = Sets.newIdentityHashSet();
        private final IVertexBuilder DUMMY_BUFFER = new VertexBuilderDummy(this);

        protected Impl(BufferBuilder bufferIn, Map<RenderType, BufferBuilder> fixedBuffersIn)
        {
            this.buffer = bufferIn;
            this.fixedBuffers = fixedBuffersIn;
            this.buffer.setRenderTypeBuffer(this);

            for (BufferBuilder bufferbuilder : fixedBuffersIn.values())
            {
                bufferbuilder.setRenderTypeBuffer(this);
            }
        }

        public IVertexBuilder getBuffer(RenderType p_getBuffer_1_)
        {
            BufferBuilder bufferbuilder = this.getBufferRaw(p_getBuffer_1_);

            if (!Objects.equals(this.lastRenderType, p_getBuffer_1_))
            {
                if (this.lastRenderType != null)
                {
                    RenderType rendertype = this.lastRenderType;

                    if (!this.fixedBuffers.containsKey(rendertype))
                    {
                        this.finish(rendertype);
                    }
                }

                if (this.startedBuffers.add(bufferbuilder))
                {
                    bufferbuilder.setRenderType(p_getBuffer_1_);
                    bufferbuilder.begin(p_getBuffer_1_.getDrawMode(), p_getBuffer_1_.getVertexFormat());
                }

                this.lastRenderType = p_getBuffer_1_;
            }

            return (IVertexBuilder)(p_getBuffer_1_.getTextureLocation() == TextureUtils.LOCATION_TEXTURE_EMPTY ? this.DUMMY_BUFFER : bufferbuilder);
        }

        private BufferBuilder getBufferRaw(RenderType renderTypeIn)
        {
            return this.fixedBuffers.getOrDefault(renderTypeIn, this.buffer);
        }

        public void finish()
        {
            if (!this.startedBuffers.isEmpty())
            {
                if (this.lastRenderType != null)
                {
                    IVertexBuilder ivertexbuilder = this.getBuffer(this.lastRenderType);

                    if (ivertexbuilder == this.buffer)
                    {
                        this.finish(this.lastRenderType);
                    }
                }

                if (!this.startedBuffers.isEmpty())
                {
                    for (RenderType rendertype : this.fixedBuffers.keySet())
                    {
                        this.finish(rendertype);

                        if (this.startedBuffers.isEmpty())
                        {
                            break;
                        }
                    }
                }
            }
        }

        public void finish(RenderType renderTypeIn)
        {
            BufferBuilder bufferbuilder = this.getBufferRaw(renderTypeIn);
            boolean flag = Objects.equals(this.lastRenderType, renderTypeIn);

            if ((flag || bufferbuilder != this.buffer) && this.startedBuffers.remove(bufferbuilder))
            {
                renderTypeIn.finish(bufferbuilder, 0, 0, 0);

                if (flag)
                {
                    this.lastRenderType = null;
                }
            }
        }

        public IVertexBuilder getBuffer(ResourceLocation p_getBuffer_1_, IVertexBuilder p_getBuffer_2_)
        {
            if (!(this.lastRenderType instanceof RenderType.Type))
            {
                return p_getBuffer_2_;
            }
            else
            {
                p_getBuffer_1_ = RenderType.getCustomTexture(p_getBuffer_1_);
                RenderType.Type rendertype$type = (RenderType.Type)this.lastRenderType;
                RenderType.Type rendertype$type1 = rendertype$type.getTextured(p_getBuffer_1_);
                return this.getBuffer(rendertype$type1);
            }
        }

        public RenderType getLastRenderType()
        {
            return this.lastRenderType;
        }

        public void flushRenderBuffers()
        {
            RenderType rendertype = this.lastRenderType;
            this.finish();

            if (rendertype != null)
            {
                this.getBuffer(rendertype);
            }
        }

        public IVertexBuilder getDummyBuffer()
        {
            return this.DUMMY_BUFFER;
        }
    }
}
