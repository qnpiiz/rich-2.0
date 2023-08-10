package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IFlinging;
import net.minecraft.util.math.MathHelper;

public class BoarModel<T extends MobEntity & IFlinging> extends AgeableModel<T>
{
    private final ModelRenderer field_239106_a_;
    private final ModelRenderer field_239107_b_;
    private final ModelRenderer field_239108_f_;
    private final ModelRenderer field_239109_g_;
    private final ModelRenderer field_239110_h_;
    private final ModelRenderer field_239111_i_;
    private final ModelRenderer field_239112_j_;
    private final ModelRenderer field_239113_k_;
    private final ModelRenderer field_239114_l_;

    public BoarModel()
    {
        super(true, 8.0F, 6.0F, 1.9F, 2.0F, 24.0F);
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.field_239109_g_ = new ModelRenderer(this);
        this.field_239109_g_.setRotationPoint(0.0F, 7.0F, 0.0F);
        this.field_239109_g_.setTextureOffset(1, 1).addBox(-8.0F, -7.0F, -13.0F, 16.0F, 14.0F, 26.0F);
        this.field_239114_l_ = new ModelRenderer(this);
        this.field_239114_l_.setRotationPoint(0.0F, -14.0F, -5.0F);
        this.field_239114_l_.setTextureOffset(90, 33).addBox(0.0F, 0.0F, -9.0F, 0.0F, 10.0F, 19.0F, 0.001F);
        this.field_239109_g_.addChild(this.field_239114_l_);
        this.field_239106_a_ = new ModelRenderer(this);
        this.field_239106_a_.setRotationPoint(0.0F, 2.0F, -12.0F);
        this.field_239106_a_.setTextureOffset(61, 1).addBox(-7.0F, -3.0F, -19.0F, 14.0F, 6.0F, 19.0F);
        this.field_239107_b_ = new ModelRenderer(this);
        this.field_239107_b_.setRotationPoint(-6.0F, -2.0F, -3.0F);
        this.field_239107_b_.setTextureOffset(1, 1).addBox(-6.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F);
        this.field_239107_b_.rotateAngleZ = -((float)Math.PI * 2F / 9F);
        this.field_239106_a_.addChild(this.field_239107_b_);
        this.field_239108_f_ = new ModelRenderer(this);
        this.field_239108_f_.setRotationPoint(6.0F, -2.0F, -3.0F);
        this.field_239108_f_.setTextureOffset(1, 6).addBox(0.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F);
        this.field_239108_f_.rotateAngleZ = ((float)Math.PI * 2F / 9F);
        this.field_239106_a_.addChild(this.field_239108_f_);
        ModelRenderer modelrenderer = new ModelRenderer(this);
        modelrenderer.setRotationPoint(-7.0F, 2.0F, -12.0F);
        modelrenderer.setTextureOffset(10, 13).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F);
        this.field_239106_a_.addChild(modelrenderer);
        ModelRenderer modelrenderer1 = new ModelRenderer(this);
        modelrenderer1.setRotationPoint(7.0F, 2.0F, -12.0F);
        modelrenderer1.setTextureOffset(1, 13).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F);
        this.field_239106_a_.addChild(modelrenderer1);
        this.field_239106_a_.rotateAngleX = 0.87266463F;
        int i = 14;
        int j = 11;
        this.field_239110_h_ = new ModelRenderer(this);
        this.field_239110_h_.setRotationPoint(-4.0F, 10.0F, -8.5F);
        this.field_239110_h_.setTextureOffset(66, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F);
        this.field_239111_i_ = new ModelRenderer(this);
        this.field_239111_i_.setRotationPoint(4.0F, 10.0F, -8.5F);
        this.field_239111_i_.setTextureOffset(41, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F);
        this.field_239112_j_ = new ModelRenderer(this);
        this.field_239112_j_.setRotationPoint(-5.0F, 13.0F, 10.0F);
        this.field_239112_j_.setTextureOffset(21, 45).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F);
        this.field_239113_k_ = new ModelRenderer(this);
        this.field_239113_k_.setRotationPoint(5.0F, 13.0F, 10.0F);
        this.field_239113_k_.setTextureOffset(0, 45).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F);
    }

    protected Iterable<ModelRenderer> getHeadParts()
    {
        return ImmutableList.of(this.field_239106_a_);
    }

    protected Iterable<ModelRenderer> getBodyParts()
    {
        return ImmutableList.of(this.field_239109_g_, this.field_239110_h_, this.field_239111_i_, this.field_239112_j_, this.field_239113_k_);
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        this.field_239107_b_.rotateAngleZ = -((float)Math.PI * 2F / 9F) - limbSwingAmount * MathHelper.sin(limbSwing);
        this.field_239108_f_.rotateAngleZ = ((float)Math.PI * 2F / 9F) + limbSwingAmount * MathHelper.sin(limbSwing);
        this.field_239106_a_.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        int i = entityIn.func_230290_eL_();
        float f = 1.0F - (float)MathHelper.abs(10 - 2 * i) / 10.0F;
        this.field_239106_a_.rotateAngleX = MathHelper.lerp(f, 0.87266463F, -0.34906584F);

        if (entityIn.isChild())
        {
            this.field_239106_a_.rotationPointY = MathHelper.lerp(f, 2.0F, 5.0F);
            this.field_239114_l_.rotationPointZ = -3.0F;
        }
        else
        {
            this.field_239106_a_.rotationPointY = 2.0F;
            this.field_239114_l_.rotationPointZ = -7.0F;
        }

        float f1 = 1.2F;
        this.field_239110_h_.rotateAngleX = MathHelper.cos(limbSwing) * 1.2F * limbSwingAmount;
        this.field_239111_i_.rotateAngleX = MathHelper.cos(limbSwing + (float)Math.PI) * 1.2F * limbSwingAmount;
        this.field_239112_j_.rotateAngleX = this.field_239111_i_.rotateAngleX;
        this.field_239113_k_.rotateAngleX = this.field_239110_h_.rotateAngleX;
    }
}
