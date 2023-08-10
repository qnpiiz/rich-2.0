package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class BookModel extends Model
{
    private final ModelRenderer coverRight = (new ModelRenderer(64, 32, 0, 0)).addBox(-6.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F);
    private final ModelRenderer coverLeft = (new ModelRenderer(64, 32, 16, 0)).addBox(0.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F);
    private final ModelRenderer pagesRight;
    private final ModelRenderer pagesLeft;
    private final ModelRenderer flippingPageRight;
    private final ModelRenderer flippingPageLeft;
    private final ModelRenderer bookSpine = (new ModelRenderer(64, 32, 12, 0)).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 10.0F, 0.005F);
    private final List<ModelRenderer> bookParts;

    public BookModel()
    {
        super(RenderType::getEntitySolid);
        this.pagesRight = (new ModelRenderer(64, 32, 0, 10)).addBox(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F);
        this.pagesLeft = (new ModelRenderer(64, 32, 12, 10)).addBox(0.0F, -4.0F, -0.01F, 5.0F, 8.0F, 1.0F);
        this.flippingPageRight = (new ModelRenderer(64, 32, 24, 10)).addBox(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
        this.flippingPageLeft = (new ModelRenderer(64, 32, 24, 10)).addBox(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
        this.bookParts = ImmutableList.of(this.coverRight, this.coverLeft, this.bookSpine, this.pagesRight, this.pagesLeft, this.flippingPageRight, this.flippingPageLeft);
        this.coverRight.setRotationPoint(0.0F, 0.0F, -1.0F);
        this.coverLeft.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.bookSpine.rotateAngleY = ((float)Math.PI / 2F);
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        this.renderAll(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public void renderAll(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        this.bookParts.forEach((p_228248_8_) ->
        {
            p_228248_8_.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        });
    }

    public void setBookState(float p_228247_1_, float rightPageFlipAmount, float leftPageFlipAmount, float bookOpenAmount)
    {
        float f = (MathHelper.sin(p_228247_1_ * 0.02F) * 0.1F + 1.25F) * bookOpenAmount;
        this.coverRight.rotateAngleY = (float)Math.PI + f;
        this.coverLeft.rotateAngleY = -f;
        this.pagesRight.rotateAngleY = f;
        this.pagesLeft.rotateAngleY = -f;
        this.flippingPageRight.rotateAngleY = f - f * 2.0F * rightPageFlipAmount;
        this.flippingPageLeft.rotateAngleY = f - f * 2.0F * leftPageFlipAmount;
        this.pagesRight.rotationPointX = MathHelper.sin(f);
        this.pagesLeft.rotationPointX = MathHelper.sin(f);
        this.flippingPageRight.rotationPointX = MathHelper.sin(f);
        this.flippingPageLeft.rotationPointX = MathHelper.sin(f);
    }
}
