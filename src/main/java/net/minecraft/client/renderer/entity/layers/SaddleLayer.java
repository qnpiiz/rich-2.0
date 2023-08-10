package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEquipable;
import net.minecraft.util.ResourceLocation;

public class SaddleLayer<T extends Entity & IEquipable, M extends EntityModel<T>> extends LayerRenderer<T, M>
{
    private final ResourceLocation field_239408_a_;
    private final M field_239409_b_;

    public SaddleLayer(IEntityRenderer<T, M> p_i232478_1_, M p_i232478_2_, ResourceLocation p_i232478_3_)
    {
        super(p_i232478_1_);
        this.field_239409_b_ = p_i232478_2_;
        this.field_239408_a_ = p_i232478_3_;
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (entitylivingbaseIn.isHorseSaddled())
        {
            this.getEntityModel().copyModelAttributesTo(this.field_239409_b_);
            this.field_239409_b_.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.field_239409_b_.setRotationAngles(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(this.field_239408_a_));
            this.field_239409_b_.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
