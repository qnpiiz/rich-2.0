package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PufferfishRenderer;
import net.minecraft.client.renderer.entity.model.PufferFishMediumModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterPufferFishMedium extends ModelAdapter
{
    public ModelAdapterPufferFishMedium()
    {
        super(EntityType.PUFFERFISH, "puffer_fish_medium", 0.2F);
    }

    public Model makeModel()
    {
        return new PufferFishMediumModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof PufferFishMediumModel))
        {
            return null;
        }
        else
        {
            PufferFishMediumModel pufferfishmediummodel = (PufferFishMediumModel)model;

            if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishMedium_ModelRenderers.getValue(pufferfishmediummodel, 0);
            }
            else if (modelPart.equals("fin_right"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishMedium_ModelRenderers.getValue(pufferfishmediummodel, 1);
            }
            else if (modelPart.equals("fin_left"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishMedium_ModelRenderers.getValue(pufferfishmediummodel, 2);
            }
            else if (modelPart.equals("spikes_front_top"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishMedium_ModelRenderers.getValue(pufferfishmediummodel, 3);
            }
            else if (modelPart.equals("spikes_back_top"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishMedium_ModelRenderers.getValue(pufferfishmediummodel, 4);
            }
            else if (modelPart.equals("spikes_front_right"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishMedium_ModelRenderers.getValue(pufferfishmediummodel, 5);
            }
            else if (modelPart.equals("spikes_back_right"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishMedium_ModelRenderers.getValue(pufferfishmediummodel, 6);
            }
            else if (modelPart.equals("spikes_back_left"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishMedium_ModelRenderers.getValue(pufferfishmediummodel, 7);
            }
            else if (modelPart.equals("spikes_front_left"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishMedium_ModelRenderers.getValue(pufferfishmediummodel, 8);
            }
            else if (modelPart.equals("spikes_back_bottom"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishMedium_ModelRenderers.getValue(pufferfishmediummodel, 9);
            }
            else
            {
                return modelPart.equals("spikes_front_bottom") ? (ModelRenderer)Reflector.ModelPufferFishMedium_ModelRenderers.getValue(pufferfishmediummodel, 10) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body", "fin_right", "fin_left", "spikes_front_top", "spikes_back_top", "spikes_front_right", "spikes_back_right", "spikes_back_left", "spikes_front_left", "spikes_back_bottom", "spikes_front_bottom"};
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

            if (!Reflector.RenderPufferfish_modelMedium.exists())
            {
                Config.warn("Model field not found: RenderPufferfish.modelMedium");
                return null;
            }
            else
            {
                Reflector.RenderPufferfish_modelMedium.setValue(pufferfishrenderer1, modelBase);
                return pufferfishrenderer1;
            }
        }
    }
}
