package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.entity.monster.MonsterEntity;

public abstract class AbstractZombieModel<T extends MonsterEntity> extends BipedModel<T>
{
    protected AbstractZombieModel(float modelSize, float yOffsetIn, int textureWidthIn, int textureHeightIn)
    {
        super(modelSize, yOffsetIn, textureWidthIn, textureHeightIn);
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        ModelHelper.func_239105_a_(this.bipedLeftArm, this.bipedRightArm, this.isAggressive(entityIn), this.swingProgress, ageInTicks);
    }

    public abstract boolean isAggressive(T entityIn);
}
