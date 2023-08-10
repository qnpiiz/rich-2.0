package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.client.renderer.entity.model.TropicalFishAModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterTropicalFishA extends ModelAdapter
{
    public ModelAdapterTropicalFishA()
    {
        super(EntityType.TROPICAL_FISH, "tropical_fish_a", 0.2F);
    }

    public Model makeModel()
    {
        return new TropicalFishAModel(0.0F);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof TropicalFishAModel))
        {
            return null;
        }
        else
        {
            TropicalFishAModel tropicalfishamodel = (TropicalFishAModel)model;

            if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.ModelTropicalFishA_ModelRenderers.getValue(tropicalfishamodel, 0);
            }
            else if (modelPart.equals("tail"))
            {
                return (ModelRenderer)Reflector.ModelTropicalFishA_ModelRenderers.getValue(tropicalfishamodel, 1);
            }
            else if (modelPart.equals("fin_right"))
            {
                return (ModelRenderer)Reflector.ModelTropicalFishA_ModelRenderers.getValue(tropicalfishamodel, 2);
            }
            else if (modelPart.equals("fin_left"))
            {
                return (ModelRenderer)Reflector.ModelTropicalFishA_ModelRenderers.getValue(tropicalfishamodel, 3);
            }
            else
            {
                return modelPart.equals("fin_top") ? (ModelRenderer)Reflector.ModelTropicalFishA_ModelRenderers.getValue(tropicalfishamodel, 4) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body", "tail", "fin_right", "fin_left", "fin_top"};
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

            if (!Reflector.RenderTropicalFish_modelA.exists())
            {
                Config.warn("Model field not found: RenderTropicalFish.modelA");
                return null;
            }
            else
            {
                Reflector.RenderTropicalFish_modelA.setValue(tropicalfishrenderer1, modelBase);
                return tropicalfishrenderer1;
            }
        }
    }
}
