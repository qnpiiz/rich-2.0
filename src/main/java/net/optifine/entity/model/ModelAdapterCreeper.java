package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.CreeperModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterCreeper extends ModelAdapter
{
    public ModelAdapterCreeper()
    {
        super(EntityType.CREEPER, "creeper", 0.5F);
    }

    public Model makeModel()
    {
        return new CreeperModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof CreeperModel))
        {
            return null;
        }
        else
        {
            CreeperModel creepermodel = (CreeperModel)model;

            if (modelPart.equals("head"))
            {
                return (ModelRenderer)Reflector.ModelCreeper_ModelRenderers.getValue(creepermodel, 0);
            }
            else if (modelPart.equals("armor"))
            {
                return (ModelRenderer)Reflector.ModelCreeper_ModelRenderers.getValue(creepermodel, 1);
            }
            else if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.ModelCreeper_ModelRenderers.getValue(creepermodel, 2);
            }
            else if (modelPart.equals("leg1"))
            {
                return (ModelRenderer)Reflector.ModelCreeper_ModelRenderers.getValue(creepermodel, 3);
            }
            else if (modelPart.equals("leg2"))
            {
                return (ModelRenderer)Reflector.ModelCreeper_ModelRenderers.getValue(creepermodel, 4);
            }
            else if (modelPart.equals("leg3"))
            {
                return (ModelRenderer)Reflector.ModelCreeper_ModelRenderers.getValue(creepermodel, 5);
            }
            else
            {
                return modelPart.equals("leg4") ? (ModelRenderer)Reflector.ModelCreeper_ModelRenderers.getValue(creepermodel, 6) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "armor", "body", "leg1", "leg2", "leg3", "leg4"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        CreeperRenderer creeperrenderer = new CreeperRenderer(entityrenderermanager);
        creeperrenderer.entityModel = (CreeperModel)modelBase;
        creeperrenderer.shadowSize = shadowSize;
        return creeperrenderer;
    }
}
