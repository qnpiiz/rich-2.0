package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PufferfishRenderer;
import net.minecraft.client.renderer.entity.model.PufferFishBigModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterPufferFishBig extends ModelAdapter
{
    public ModelAdapterPufferFishBig()
    {
        super(EntityType.PUFFERFISH, "puffer_fish_big", 0.2F);
    }

    public Model makeModel()
    {
        return new PufferFishBigModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof PufferFishBigModel))
        {
            return null;
        }
        else
        {
            PufferFishBigModel pufferfishbigmodel = (PufferFishBigModel)model;

            if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishBig_ModelRenderers.getValue(pufferfishbigmodel, 0);
            }
            else if (modelPart.equals("fin_right"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishBig_ModelRenderers.getValue(pufferfishbigmodel, 1);
            }
            else if (modelPart.equals("fin_left"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishBig_ModelRenderers.getValue(pufferfishbigmodel, 2);
            }
            else if (modelPart.equals("spikes_front_top"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishBig_ModelRenderers.getValue(pufferfishbigmodel, 3);
            }
            else if (modelPart.equals("spikes_middle_top"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishBig_ModelRenderers.getValue(pufferfishbigmodel, 4);
            }
            else if (modelPart.equals("spikes_back_top"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishBig_ModelRenderers.getValue(pufferfishbigmodel, 5);
            }
            else if (modelPart.equals("spikes_front_right"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishBig_ModelRenderers.getValue(pufferfishbigmodel, 6);
            }
            else if (modelPart.equals("spikes_front_left"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishBig_ModelRenderers.getValue(pufferfishbigmodel, 7);
            }
            else if (modelPart.equals("spikes_front_bottom"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishBig_ModelRenderers.getValue(pufferfishbigmodel, 8);
            }
            else if (modelPart.equals("spikes_middle_bottom"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishBig_ModelRenderers.getValue(pufferfishbigmodel, 9);
            }
            else if (modelPart.equals("spikes_back_bottom"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishBig_ModelRenderers.getValue(pufferfishbigmodel, 10);
            }
            else if (modelPart.equals("spikes_back_right"))
            {
                return (ModelRenderer)Reflector.ModelPufferFishBig_ModelRenderers.getValue(pufferfishbigmodel, 11);
            }
            else
            {
                return modelPart.equals("spikes_back_left") ? (ModelRenderer)Reflector.ModelPufferFishBig_ModelRenderers.getValue(pufferfishbigmodel, 12) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body", "fin_right", "fin_left", "spikes_front_top", "spikes_middle_top", "spikes_back_top", "spikes_front_right", "spikes_front_left", "spikes_front_bottom", "spikes_middle_bottom", "spikes_back_bottom", "spikes_back_right", "spikes_back_left"};
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

            if (!Reflector.RenderPufferfish_modelBig.exists())
            {
                Config.warn("Model field not found: RenderPufferfish.modelBig");
                return null;
            }
            else
            {
                Reflector.RenderPufferfish_modelBig.setValue(pufferfishrenderer1, modelBase);
                return pufferfishrenderer1;
            }
        }
    }
}
