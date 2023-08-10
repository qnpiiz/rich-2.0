package net.minecraft.client.gui.fonts;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Matrix4f;
import net.optifine.util.MathUtils;

public class TexturedGlyph
{
    private final RenderType normalType;
    private final RenderType seeThroughType;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;
    private final float minX;
    private final float maxX;
    private final float minY;
    private final float maxY;
    public static final Matrix4f MATRIX_IDENTITY = MathUtils.makeMatrixIdentity();

    public TexturedGlyph(RenderType normalType, RenderType seeThroughType, float u0, float u1, float v0, float v1, float minX, float maxX, float minY, float maxY)
    {
        this.normalType = normalType;
        this.seeThroughType = seeThroughType;
        this.u0 = u0;
        this.u1 = u1;
        this.v0 = v0;
        this.v1 = v1;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public void render(boolean italicIn, float xIn, float yIn, Matrix4f matrixIn, IVertexBuilder bufferIn, float redIn, float greenIn, float blueIn, float alphaIn, int packedLight)
    {
        int i = 3;
        float f = xIn + this.minX;
        float f1 = xIn + this.maxX;
        float f2 = this.minY - 3.0F;
        float f3 = this.maxY - 3.0F;
        float f4 = yIn + f2;
        float f5 = yIn + f3;
        float f6 = italicIn ? 1.0F - 0.25F * f2 : 0.0F;
        float f7 = italicIn ? 1.0F - 0.25F * f3 : 0.0F;

        if (bufferIn instanceof BufferBuilder && matrixIn == MATRIX_IDENTITY)
        {
            BufferBuilder bufferbuilder = (BufferBuilder)bufferIn;
            int j = (int)(redIn * 255.0F);
            int k = (int)(greenIn * 255.0F);
            int l = (int)(blueIn * 255.0F);
            int i1 = (int)(alphaIn * 255.0F);
            int j1 = packedLight & 65535;
            int k1 = packedLight >> 16 & 65535;
            bufferbuilder.addVertexText(f + f6, f4, 0.0F, j, k, l, i1, this.u0, this.v0, j1, k1);
            bufferbuilder.addVertexText(f + f7, f5, 0.0F, j, k, l, i1, this.u0, this.v1, j1, k1);
            bufferbuilder.addVertexText(f1 + f7, f5, 0.0F, j, k, l, i1, this.u1, this.v1, j1, k1);
            bufferbuilder.addVertexText(f1 + f6, f4, 0.0F, j, k, l, i1, this.u1, this.v0, j1, k1);
        }
        else
        {
            bufferIn.pos(matrixIn, f + f6, f4, 0.0F).color(redIn, greenIn, blueIn, alphaIn).tex(this.u0, this.v0).lightmap(packedLight).endVertex();
            bufferIn.pos(matrixIn, f + f7, f5, 0.0F).color(redIn, greenIn, blueIn, alphaIn).tex(this.u0, this.v1).lightmap(packedLight).endVertex();
            bufferIn.pos(matrixIn, f1 + f7, f5, 0.0F).color(redIn, greenIn, blueIn, alphaIn).tex(this.u1, this.v1).lightmap(packedLight).endVertex();
            bufferIn.pos(matrixIn, f1 + f6, f4, 0.0F).color(redIn, greenIn, blueIn, alphaIn).tex(this.u1, this.v0).lightmap(packedLight).endVertex();
        }
    }

    public void renderEffect(TexturedGlyph.Effect effectIn, Matrix4f matrixIn, IVertexBuilder bufferIn, int packedLightIn)
    {
        bufferIn.pos(matrixIn, effectIn.x0, effectIn.y0, effectIn.depth).color(effectIn.r, effectIn.g, effectIn.b, effectIn.a).tex(this.u0, this.v0).lightmap(packedLightIn).endVertex();
        bufferIn.pos(matrixIn, effectIn.x1, effectIn.y0, effectIn.depth).color(effectIn.r, effectIn.g, effectIn.b, effectIn.a).tex(this.u0, this.v1).lightmap(packedLightIn).endVertex();
        bufferIn.pos(matrixIn, effectIn.x1, effectIn.y1, effectIn.depth).color(effectIn.r, effectIn.g, effectIn.b, effectIn.a).tex(this.u1, this.v1).lightmap(packedLightIn).endVertex();
        bufferIn.pos(matrixIn, effectIn.x0, effectIn.y1, effectIn.depth).color(effectIn.r, effectIn.g, effectIn.b, effectIn.a).tex(this.u1, this.v0).lightmap(packedLightIn).endVertex();
    }

    public RenderType getRenderType(boolean seeThroughIn)
    {
        return seeThroughIn ? this.seeThroughType : this.normalType;
    }

    public static class Effect
    {
        protected final float x0;
        protected final float y0;
        protected final float x1;
        protected final float y1;
        protected final float depth;
        protected final float r;
        protected final float g;
        protected final float b;
        protected final float a;

        public Effect(float x0, float y0, float x1, float y1, float depth, float red, float green, float blue, float alpha)
        {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            this.depth = depth;
            this.r = red;
            this.g = green;
            this.b = blue;
            this.a = alpha;
        }
    }
}
