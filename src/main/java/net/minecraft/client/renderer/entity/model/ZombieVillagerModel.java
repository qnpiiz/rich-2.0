package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.ZombieEntity;

public class ZombieVillagerModel<T extends ZombieEntity> extends BipedModel<T> implements IHeadToggle
{
    private ModelRenderer field_217150_a;

    public ZombieVillagerModel(float p_i51058_1_, boolean p_i51058_2_)
    {
        super(p_i51058_1_, 0.0F, 64, p_i51058_2_ ? 32 : 64);

        if (p_i51058_2_)
        {
            this.bipedHead = new ModelRenderer(this, 0, 0);
            this.bipedHead.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_i51058_1_);
            this.bipedBody = new ModelRenderer(this, 16, 16);
            this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_i51058_1_ + 0.1F);
            this.bipedRightLeg = new ModelRenderer(this, 0, 16);
            this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
            this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_ + 0.1F);
            this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
            this.bipedLeftLeg.mirror = true;
            this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
            this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_ + 0.1F);
        }
        else
        {
            this.bipedHead = new ModelRenderer(this, 0, 0);
            this.bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, p_i51058_1_);
            this.bipedHead.setTextureOffset(24, 0).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, p_i51058_1_);
            this.bipedHeadwear = new ModelRenderer(this, 32, 0);
            this.bipedHeadwear.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, p_i51058_1_ + 0.5F);
            this.field_217150_a = new ModelRenderer(this);
            this.field_217150_a.setTextureOffset(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F, p_i51058_1_);
            this.field_217150_a.rotateAngleX = (-(float)Math.PI / 2F);
            this.bipedHeadwear.addChild(this.field_217150_a);
            this.bipedBody = new ModelRenderer(this, 16, 20);
            this.bipedBody.addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, p_i51058_1_);
            this.bipedBody.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, p_i51058_1_ + 0.05F);
            this.bipedRightArm = new ModelRenderer(this, 44, 22);
            this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_);
            this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
            this.bipedLeftArm = new ModelRenderer(this, 44, 22);
            this.bipedLeftArm.mirror = true;
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
            this.bipedRightLeg = new ModelRenderer(this, 0, 22);
            this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
            this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_);
            this.bipedLeftLeg = new ModelRenderer(this, 0, 22);
            this.bipedLeftLeg.mirror = true;
            this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
            this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_);
        }
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        ModelHelper.func_239105_a_(this.bipedLeftArm, this.bipedRightArm, entityIn.isAggressive(), this.swingProgress, ageInTicks);
    }

    public void func_217146_a(boolean p_217146_1_)
    {
        this.bipedHead.showModel = p_217146_1_;
        this.bipedHeadwear.showModel = p_217146_1_;
        this.field_217150_a.showModel = p_217146_1_;
    }
}
