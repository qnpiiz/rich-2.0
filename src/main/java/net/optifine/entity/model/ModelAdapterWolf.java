package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterWolf extends ModelAdapter
{
    public ModelAdapterWolf()
    {
        super(EntityType.WOLF, "wolf", 0.5F);
    }

    public Model makeModel()
    {
        return new WolfModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof WolfModel))
        {
            return null;
        }
        else
        {
            WolfModel wolfmodel = (WolfModel)model;

            if (modelPart.equals("head"))
            {
                return (ModelRenderer)Reflector.ModelWolf_ModelRenderers.getValue(wolfmodel, 0);
            }
            else if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.ModelWolf_ModelRenderers.getValue(wolfmodel, 2);
            }
            else if (modelPart.equals("leg1"))
            {
                return (ModelRenderer)Reflector.ModelWolf_ModelRenderers.getValue(wolfmodel, 3);
            }
            else if (modelPart.equals("leg2"))
            {
                return (ModelRenderer)Reflector.ModelWolf_ModelRenderers.getValue(wolfmodel, 4);
            }
            else if (modelPart.equals("leg3"))
            {
                return (ModelRenderer)Reflector.ModelWolf_ModelRenderers.getValue(wolfmodel, 5);
            }
            else if (modelPart.equals("leg4"))
            {
                return (ModelRenderer)Reflector.ModelWolf_ModelRenderers.getValue(wolfmodel, 6);
            }
            else if (modelPart.equals("tail"))
            {
                return (ModelRenderer)Reflector.ModelWolf_ModelRenderers.getValue(wolfmodel, 7);
            }
            else
            {
                return modelPart.equals("mane") ? (ModelRenderer)Reflector.ModelWolf_ModelRenderers.getValue(wolfmodel, 9) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "body", "leg1", "leg2", "leg3", "leg4", "tail", "mane"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        WolfRenderer wolfrenderer = new WolfRenderer(entityrenderermanager);
        wolfrenderer.entityModel = (WolfModel)modelBase;
        wolfrenderer.shadowSize = shadowSize;
        return wolfrenderer;
    }
}
