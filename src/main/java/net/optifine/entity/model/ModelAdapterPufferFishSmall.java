package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PufferfishRenderer;
import net.minecraft.client.renderer.entity.model.PufferFishSmallModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterPufferFishSmall extends ModelAdapter
{
    public ModelAdapterPufferFishSmall()
    {
        super(EntityType.PUFFERFISH, "puffer_fish_small", 0.2F);
    }

    public Model makeModel()
    {
        return new PufferFishSmallModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof PufferFishSmallModel))
        {
            return null;
        }
        else
        {
            PufferFishSmallModel pufferfishsmallmodel = (PufferFishSmallModel)model;

            if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishSmall_ModelRenderers.getValue(pufferfishsmallmodel, 0);
            }
            else if (modelPart.equals("eye_right"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishSmall_ModelRenderers.getValue(pufferfishsmallmodel, 1);
            }
            else if (modelPart.equals("eye_left"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishSmall_ModelRenderers.getValue(pufferfishsmallmodel, 2);
            }
            else if (modelPart.equals("fin_right"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishSmall_ModelRenderers.getValue(pufferfishsmallmodel, 3);
            }
            else if (modelPart.equals("fin_left"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishSmall_ModelRenderers.getValue(pufferfishsmallmodel, 4);
            }
            else
            {
                return modelPart.equals("tail") ? (ModelRenderer)Reflector.ModelPufferFishSmall_ModelRenderers.getValue(pufferfishsmallmodel, 5) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body", "eye_right", "eye_left", "tail", "fin_right", "fin_left"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        EntityRenderer entityrenderer = entityrenderermanager.getEntityRenderMap().get(EntityType.PUFFERFISH);

        if (!(entityrenderer instanceof PufferfishRenderer))
        {
            Config.warn("Not a PufferfishRenderer: " + entityrenderer);
            return null;
        }
        else
        {
            if (entityrenderer.getType() == null)
            {
                PufferfishRenderer pufferfishrenderer = new PufferfishRenderer(entityrenderermanager);
                pufferfishrenderer.shadowSize = shadowSize;
                entityrenderer = pufferfishrenderer;
            }

            PufferfishRenderer pufferfishrenderer1 = (PufferfishRenderer)entityrenderer;

            if (!Reflector.RenderPufferfish_modelSmall.exists())
            {
                Config.warn("Model field not found: RenderPufferfish.modelSmall");
                return null;
            }
            else
            {
                Reflector.RenderPufferfish_modelSmall.setValue(pufferfishrenderer1, modelBase);
                return pufferfishrenderer1;
            }
        }
    }
}
