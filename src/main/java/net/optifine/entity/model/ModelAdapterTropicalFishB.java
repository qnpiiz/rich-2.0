package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.client.renderer.entity.model.TropicalFishBModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterTropicalFishB extends ModelAdapter
{
    public ModelAdapterTropicalFishB()
    {
        super(EntityType.TROPICAL_FISH, "tropical_fish_b", 0.2F);
    }

    public Model makeModel()
    {
        return new TropicalFishBModel(0.0F);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof TropicalFishBModel))
        {
            return null;
        }
        else
        {
            TropicalFishBModel tropicalfishbmodel = (TropicalFishBModel)model;

            if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.ModelTropicalFishB_ModelRenderers.getValue(tropicalfishbmodel, 0);
            }
            else if (modelPart.equals("tail"))
            {
                return (ModelRenderer)Reflector.ModelTropicalFishB_ModelRenderers.getValue(tropicalfishbmodel, 1);
            }
            else if (modelPart.equals("fin_right"))
            {
                return (ModelRenderer)Reflector.ModelTropicalFishB_ModelRenderers.getValue(tropicalfishbmodel, 2);
            }
            else if (modelPart.equals("fin_left"))
            {
                return (ModelRenderer)Reflector.ModelTropicalFishB_ModelRenderers.getValue(tropicalfishbmodel, 3);
            }
            else if (modelPart.equals("fin_top"))
            {
                return (ModelRenderer)Reflector.ModelTropicalFishB_ModelRenderers.getValue(tropicalfishbmodel, 4);
            }
            else
            {
                return modelPart.equals("fin_bottom") ? (ModelRenderer)Reflector.ModelTropicalFishB_ModelRenderers.getValue(tropicalfishbmodel, 5) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body", "tail", "fin_right", "fin_left", "fin_top", "fin_bottom"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        EntityRenderer entityrenderer = entityrenderermanager.getEntityRenderMap().get(EntityType.TROPICAL_FISH);

        if (!(entityrenderer instanceof TropicalFishRenderer))
        {
            Config.warn("Not a TropicalFishRenderer: " + entityrenderer);
            return null;
        }
        else
        {
            if (entityrenderer.getType() == null)
            {
                TropicalFishRenderer tropicalfishrenderer = new TropicalFishRenderer(entityrenderermanager);
                tropicalfishrenderer.shadowSize = shadowSize;
                entityrenderer = tropicalfishrenderer;
            }

            TropicalFishRenderer tropicalfishrenderer1 = (TropicalFishRenderer)entityrenderer;

            if (!Reflector.RenderTropicalFish_modelB.exists())
            {
                Config.warn("Model field not found: RenderTropicalFish.modelB");
                return null;
            }
            else
            {
                Reflector.RenderTropicalFish_modelB.setValue(tropicalfishrenderer1, modelBase);
                return tropicalfishrenderer1;
            }
        }
    }
}
