package net.optifine.model;

import java.util.Arrays;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class BakedQuadRetextured extends BakedQuad
{
    public BakedQuadRetextured(BakedQuad quad, TextureAtlasSprite spriteIn)
    {
        super(remapVertexData(quad.getVertexData(), quad.getSprite(), spriteIn), quad.getTintIndex(), FaceBakery.getFacingFromVertexData(quad.getVertexData()), spriteIn, quad.applyDiffuseLighting());
    }

    private static int[] remapVertexData(int[] vertexData, TextureAtlasSprite sprite, TextureAtlasSprite spriteNew)
    {
        int[] aint = Arrays.copyOf(vertexData, vertexData.length);

        for (int i = 0; i < 4; ++i)
        {
            VertexFormat vertexformat = DefaultVertexFormats.BLOCK;
            int j = vertexformat.getIntegerSize() * i;
            int k = vertexformat.getOffset(2) / 4;
            aint[j + k] = Float.floatToRawIntBits(spriteNew.getInterpolatedU((double)sprite.getUnInterpolatedU(Float.intBitsToFloat(vertexData[j + k]))));
            aint[j + k + 1] = Float.floatToRawIntBits(spriteNew.getInterpolatedV((double)sprite.getUnInterpolatedV(Float.intBitsToFloat(vertexData[j + k + 1]))));
        }

        return aint;
    }
}
