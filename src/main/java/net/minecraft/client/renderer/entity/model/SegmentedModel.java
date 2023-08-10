package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public abstract class SegmentedModel<E extends Entity> extends EntityModel<E>
{
    public SegmentedModel()
    {
        this(RenderType::getEntityCutoutNoCull);
    }

    public SegmentedModel(Function<ResourceLocation, RenderType> p_i232335_1_)
    {
        super(p_i232335_1_);
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        this.getParts().forEach((p_228272_8_) ->
        {
            p_228272_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    public abstract Iterable<ModelRenderer> getParts();
}
