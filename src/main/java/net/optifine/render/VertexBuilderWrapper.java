package net.optifine.render;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.vector.Vector3f;

public abstract class VertexBuilderWrapper implements IVertexBuilder
{
    private IVertexBuilder vertexBuilder;

    public VertexBuilderWrapper(IVertexBuilder vertexBuilder)
    {
        this.vertexBuilder = vertexBuilder;
    }

    public IVertexBuilder getVertexBuilder()
    {
        return this.vertexBuilder;
    }

    public void putSprite(TextureAtlasSprite sprite)
    {
        this.vertexBuilder.putSprite(sprite);
    }

    public void setSprite(TextureAtlasSprite sprite)
    {
        this.vertexBuilder.setSprite(sprite);
    }

    public boolean isMultiTexture()
    {
        return this.vertexBuilder.isMultiTexture();
    }

    public void setRenderType(RenderType renderType)
    {
        this.vertexBuilder.setRenderType(renderType);
    }

    public RenderType getRenderType()
    {
        return this.vertexBuilder.getRenderType();
    }

    public void setRenderBlocks(boolean renderBlocks)
    {
        this.vertexBuilder.setRenderBlocks(renderBlocks);
    }

    public Vector3f getTempVec3f(Vector3f vec)
    {
        return this.vertexBuilder.getTempVec3f(vec);
    }

    public Vector3f getTempVec3f(float x, float y, float z)
    {
        return this.vertexBuilder.getTempVec3f(x, y, z);
    }

    public float[] getTempFloat4(float f1, float f2, float f3, float f4)
    {
        return this.vertexBuilder.getTempFloat4(f1, f2, f3, f4);
    }

    public int[] getTempInt4(int i1, int i2, int i3, int i4)
    {
        return this.vertexBuilder.getTempInt4(i1, i2, i3, i4);
    }

    public IRenderTypeBuffer.Impl getRenderTypeBuffer()
    {
        return this.vertexBuilder.getRenderTypeBuffer();
    }

    public void setQuadVertexPositions(VertexPosition[] vps)
    {
        this.vertexBuilder.setQuadVertexPositions(vps);
    }

    public void setMidBlock(float mbx, float mby, float mbz)
    {
        this.vertexBuilder.setMidBlock(mbx, mby, mbz);
    }
}
