package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterDragon extends ModelAdapter
{
    public ModelAdapterDragon()
    {
        super(EntityType.ENDER_DRAGON, "dragon", 0.5F);
    }

    public Model makeModel()
    {
        return new EnderDragonRenderer.EnderDragonModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof EnderDragonRenderer.EnderDragonModel))
        {
            return null;
        }
        else
        {
            EnderDragonRenderer.EnderDragonModel enderdragonrenderer$enderdragonmodel = (EnderDragonRenderer.EnderDragonModel)model;

            if (modelPart.equals("head"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 0);
            }
            else if (modelPart.equals("spine"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 1);
            }
            else if (modelPart.equals("jaw"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 2);
            }
            else if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 3);
            }
            else if (modelPart.equals("left_wing"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 4);
            }
            else if (modelPart.equals("left_wing_tip"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 5);
            }
            else if (modelPart.equals("front_left_leg"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 6);
            }
            else if (modelPart.equals("front_left_shin"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 7);
            }
            else if (modelPart.equals("front_left_foot"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 8);
            }
            else if (modelPart.equals("back_left_leg"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 9);
            }
            else if (modelPart.equals("back_left_shin"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 10);
            }
            else if (modelPart.equals("back_left_foot"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 11);
            }
            else if (modelPart.equals("right_wing"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 12);
            }
            else if (modelPart.equals("right_wing_tip"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 13);
            }
            else if (modelPart.equals("front_right_leg"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 14);
            }
            else if (modelPart.equals("front_right_shin"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 15);
            }
            else if (modelPart.equals("front_right_foot"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 16);
            }
            else if (modelPart.equals("back_right_leg"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 17);
            }
            else if (modelPart.equals("back_right_shin"))
            {
                return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 18);
            }
            else
            {
                return modelPart.equals("back_right_foot") ? (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 19) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "spine", "jaw", "body", "left_wing", "left_wing_tip", "front_left_leg", "front_left_shin", "front_left_foot", "back_left_leg", "back_left_shin", "back_left_foot", "right_wing", "right_wing_tip", "front_right_leg", "front_right_shin", "front_right_foot", "back_right_leg", "back_right_shin", "back_right_foot"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        EnderDragonRenderer enderdragonrenderer = new EnderDragonRenderer(entityrenderermanager);

        if (!Reflector.EnderDragonRenderer_model.exists())
        {
            Config.warn("Field not found: EnderDragonRenderer.model");
            return null;
        }
        else
        {
            Reflector.setFieldValue(enderdragonrenderer, Reflector.EnderDragonRenderer_model, modelBase);
            enderdragonrenderer.shadowSize = shadowSize;
            return enderdragonrenderer;
        }
    }
}
