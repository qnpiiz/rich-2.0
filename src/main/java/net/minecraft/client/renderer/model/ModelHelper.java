package net.minecraft.client.renderer.model;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

public class ModelHelper
{
    public static void func_239104_a_(ModelRenderer rightArm, ModelRenderer leftArm, ModelRenderer head, boolean leftHanded)
    {
        ModelRenderer modelrenderer = leftHanded ? rightArm : leftArm;
        ModelRenderer modelrenderer1 = leftHanded ? leftArm : rightArm;
        modelrenderer.rotateAngleY = (leftHanded ? -0.3F : 0.3F) + head.rotateAngleY;
        modelrenderer1.rotateAngleY = (leftHanded ? 0.6F : -0.6F) + head.rotateAngleY;
        modelrenderer.rotateAngleX = (-(float)Math.PI / 2F) + head.rotateAngleX + 0.1F;
        modelrenderer1.rotateAngleX = -1.5F + head.rotateAngleX;
    }

    public static void func_239102_a_(ModelRenderer rightArm, ModelRenderer leftArm, LivingEntity entity, boolean leftHanded)
    {
        ModelRenderer modelrenderer = leftHanded ? rightArm : leftArm;
        ModelRenderer modelrenderer1 = leftHanded ? leftArm : rightArm;
        modelrenderer.rotateAngleY = leftHanded ? -0.8F : 0.8F;
        modelrenderer.rotateAngleX = -0.97079635F;
        modelrenderer1.rotateAngleX = modelrenderer.rotateAngleX;
        float f = (float)CrossbowItem.getChargeTime(entity.getActiveItemStack());
        float f1 = MathHelper.clamp((float)entity.getItemInUseMaxCount(), 0.0F, f);
        float f2 = f1 / f;
        modelrenderer1.rotateAngleY = MathHelper.lerp(f2, 0.4F, 0.85F) * (float)(leftHanded ? 1 : -1);
        modelrenderer1.rotateAngleX = MathHelper.lerp(f2, modelrenderer1.rotateAngleX, (-(float)Math.PI / 2F));
    }

    public static <T extends MobEntity> void func_239103_a_(ModelRenderer rightArm, ModelRenderer leftArm, T entity, float swingProgress, float ageInTicks)
    {
        float f = MathHelper.sin(swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin((1.0F - (1.0F - swingProgress) * (1.0F - swingProgress)) * (float)Math.PI);
        rightArm.rotateAngleZ = 0.0F;
        leftArm.rotateAngleZ = 0.0F;
        rightArm.rotateAngleY = 0.15707964F;
        leftArm.rotateAngleY = -0.15707964F;

        if (entity.getPrimaryHand() == HandSide.RIGHT)
        {
            rightArm.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
            leftArm.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
            rightArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
            leftArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
        }
        else
        {
            rightArm.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
            leftArm.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
            rightArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
            leftArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
        }

        func_239101_a_(rightArm, leftArm, ageInTicks);
    }

    public static void func_239101_a_(ModelRenderer rightArm, ModelRenderer leftArm, float ageInTicks)
    {
        rightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        leftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        leftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
    }

    public static void func_239105_a_(ModelRenderer leftArm, ModelRenderer rightArm, boolean isAggresive, float swingProgress, float ageInTicks)
    {
        float f = MathHelper.sin(swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin((1.0F - (1.0F - swingProgress) * (1.0F - swingProgress)) * (float)Math.PI);
        rightArm.rotateAngleZ = 0.0F;
        leftArm.rotateAngleZ = 0.0F;
        rightArm.rotateAngleY = -(0.1F - f * 0.6F);
        leftArm.rotateAngleY = 0.1F - f * 0.6F;
        float f2 = -(float)Math.PI / (isAggresive ? 1.5F : 2.25F);
        rightArm.rotateAngleX = f2;
        leftArm.rotateAngleX = f2;
        rightArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
        leftArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
        func_239101_a_(rightArm, leftArm, ageInTicks);
    }
}
