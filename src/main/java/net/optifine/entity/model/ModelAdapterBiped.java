package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;

public abstract class ModelAdapterBiped extends ModelAdapter
{
    public ModelAdapterBiped(EntityType type, String name, float shadowSize)
    {
        super(type, name, shadowSize);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof BipedModel))
        {
            return null;
        }
        else
        {
            BipedModel bipedmodel = (BipedModel)model;

            if (modelPart.equals("head"))
            {
                return bipedmodel.bipedHead;
            }
            else if (modelPart.equals("headwear"))
            {
                return bipedmodel.bipedHeadwear;
            }
            else if (modelPart.equals("body"))
            {
                return bipedmodel.bipedBody;
            }
            else if (modelPart.equals("left_arm"))
            {
                return bipedmodel.bipedLeftArm;
            }
            else if (modelPart.equals("right_arm"))
            {
                return bipedmodel.bipedRightArm;
            }
            else if (modelPart.equals("left_leg"))
            {
                return bipedmodel.bipedLeftLeg;
            }
            else
            {
                return modelPart.equals("right_leg") ? bipedmodel.bipedRightLeg : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "headwear", "body", "left_arm", "right_arm", "left_leg", "right_leg"};
    }
}
