package net.minecraft.client.gui.fonts;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

public class EmptyGlyph extends TexturedGlyph
{
    public EmptyGlyph()
    {
        super(RenderType.getText(new ResourceLocation("")), RenderType.getTextSeeThrough(new ResourceLocation("")), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
    }

    public void render(boolean italicIn, float xIn, float yIn, Matrix4f matrixIn, IVertexBuilder bufferIn, float redIn, float greenIn, float blueIn, float alphaIn, int packedLight)
    {
    }
}
