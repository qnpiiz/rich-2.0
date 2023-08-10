package com.mojang.blaze3d.vertex;

import net.optifine.render.VertexBuilderWrapper;

public class VertexBuilderUtils
{
    public static IVertexBuilder newDelegate(IVertexBuilder vertexBuilder, IVertexBuilder delegateBuilder)
    {
        return new VertexBuilderUtils.DelegatingVertexBuilder(vertexBuilder, delegateBuilder);
    }

    static class DelegatingVertexBuilder extends VertexBuilderWrapper implements IVertexBuilder
    {
        private final IVertexBuilder vertexBuilder;
        private final IVertexBuilder delegateBuilder;
        private boolean fixMultitextureUV;

        public DelegatingVertexBuilder(IVertexBuilder vertexBuilder, IVertexBuilder delegateBuilder)
        {
            super(delegateBuilder);

            if (vertexBuilder == delegateBuilder)
            {
                throw new IllegalArgumentException("Duplicate delegates");
            }
            else
            {
                this.vertexBuilder = vertexBuilder;
                this.delegateBuilder = delegateBuilder;
                this.updateFixMultitextureUv();
            }
        }

        public IVertexBuilder pos(double x, double y, double z)
        {
            this.vertexBuilder.pos(x, y, z);
            this.delegateBuilder.pos(x, y, z);
            return this;
        }

        public IVertexBuilder color(int red, int green, int blue, int alpha)
        {
            this.vertexBuilder.color(red, green, blue, alpha);
            this.delegateBuilder.color(red, green, blue, alpha);
            return this;
        }

        public IVertexBuilder tex(float u, float v)
        {
            this.vertexBuilder.tex(u, v);
            this.delegateBuilder.tex(u, v);
            return this;
        }

        public IVertexBuilder overlay(int u, int v)
        {
            this.vertexBuilder.overlay(u, v);
            this.delegateBuilder.overlay(u, v);
            return this;
        }

        public IVertexBuilder lightmap(int u, int v)
        {
            this.vertexBuilder.lightmap(u, v);
            this.delegateBuilder.lightmap(u, v);
            return this;
        }

        public IVertexBuilder normal(float x, float y, float z)
        {
            this.vertexBuilder.normal(x, y, z);
            this.delegateBuilder.normal(x, y, z);
            return this;
        }

        public void addVertex(float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ)
        {
            if (this.fixMultitextureUV)
            {
                this.vertexBuilder.addVertex(x, y, z, red, green, blue, alpha, texU / 32.0F, texV / 32.0F, overlayUV, lightmapUV, normalX, normalY, normalZ);
            }
            else
            {
                this.vertexBuilder.addVertex(x, y, z, red, green, blue, alpha, texU, texV, overlayUV, lightmapUV, normalX, normalY, normalZ);
            }

            this.delegateBuilder.addVertex(x, y, z, red, green, blue, alpha, texU, texV, overlayUV, lightmapUV, normalX, normalY, normalZ);
        }

        public void endVertex()
        {
            this.vertexBuilder.endVertex();
            this.delegateBuilder.endVertex();
        }

        public void setRenderBlocks(boolean p_setRenderBlocks_1_)
        {
            super.setRenderBlocks(p_setRenderBlocks_1_);
            this.updateFixMultitextureUv();
        }

        private void updateFixMultitextureUv()
        {
            this.fixMultitextureUV = !this.vertexBuilder.isMultiTexture() && this.delegateBuilder.isMultiTexture();
        }

        public IVertexBuilder getSecondaryBuilder()
        {
            return this.vertexBuilder;
        }
    }
}
