package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.model.ParrotModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterParrot extends ModelAdapter
{
    public ModelAdapterParrot()
    {
        super(EntityType.PARROT, "parrot", 0.3F);
    }

    public Model makeModel()
    {
        return new ParrotModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof ParrotModel))
        {
            return null;
        }
        else
        {
            ParrotModel parrotmodel = (ParrotModel)model;

            if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 0);
            }
            else if (modelPart.equals("tail"))
            {
                return (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 1);
            }
            else if (modelPart.equals("left_wing"))
            {
                return (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 2);
            }
            else if (modelPart.equals("right_wing"))
            {
                return (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 3);
            }
            else if (modelPart.equals("head"))
            {
                return (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 4);
            }
            else if (modelPart.equals("left_leg"))
            {
                return (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 9);
            }
            else
            {
                return modelPart.equals("right_leg") ? (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 10) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body", "tail", "left_wing", "right_wing", "head", "left_leg", "right_leg"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ParrotRenderer parrotrenderer = new ParrotRenderer(entityrenderermanager);
        parrotrenderer.entityModel = (ParrotModel)modelBase;
        parrotrenderer.shadowSize = shadowSize;
        return parrotrenderer;
    }
}
