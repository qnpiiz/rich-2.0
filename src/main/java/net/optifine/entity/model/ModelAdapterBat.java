package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BatModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterBat extends ModelAdapter
{
    public ModelAdapterBat()
    {
        super(EntityType.BAT, "bat", 0.25F);
    }

    public Model makeModel()
    {
        return new BatModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof BatModel))
        {
            return null;
        }
        else
        {
            BatModel batmodel = (BatModel)model;

            if (modelPart.equals("head"))
            {
                return (ModelRenderer)Reflector.getFieldValue(batmodel, Reflector.ModelBat_ModelRenderers, 0);
            }
            else if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.getFieldValue(batmodel, Reflector.ModelBat_ModelRenderers, 1);
            }
            else if (modelPart.equals("right_wing"))
            {
                return (ModelRenderer)Reflector.getFieldValue(batmodel, Reflector.ModelBat_ModelRenderers, 2);
            }
            else if (modelPart.equals("left_wing"))
            {
                return (ModelRenderer)Reflector.getFieldValue(batmodel, Reflector.ModelBat_ModelRenderers, 3);
            }
            else if (modelPart.equals("outer_right_wing"))
            {
                return (ModelRenderer)Reflector.getFieldValue(batmodel, Reflector.ModelBat_ModelRenderers, 4);
            }
            else
            {
                return modelPart.equals("outer_left_wing") ? (ModelRenderer)Reflector.getFieldValue(batmodel, Reflector.ModelBat_ModelRenderers, 5) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "body", "right_wing", "left_wing", "outer_right_wing", "outer_left_wing"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        BatRenderer batrenderer = new BatRenderer(entityrenderermanager);
        batrenderer.entityModel = (BatModel)modelBase;
        batrenderer.shadowSize = shadowSize;
        return batrenderer;
    }
}
