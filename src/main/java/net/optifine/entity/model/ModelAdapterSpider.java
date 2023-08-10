package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterSpider extends ModelAdapter
{
    public ModelAdapterSpider()
    {
        super(EntityType.SPIDER, "spider", 1.0F);
    }

    protected ModelAdapterSpider(EntityType type, String name, float shadowSize)
    {
        super(type, name, shadowSize);
    }

    public Model makeModel()
    {
        return new SpiderModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof SpiderModel))
        {
            return null;
        }
        else
        {
            SpiderModel spidermodel = (SpiderModel)model;

            if (modelPart.equals("head"))
            {
                return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 0);
            }
            else if (modelPart.equals("neck"))
            {
                return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 1);
            }
            else if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 2);
            }
            else if (modelPart.equals("leg1"))
            {
                return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 3);
            }
            else if (modelPart.equals("leg2"))
            {
                return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 4);
            }
            else if (modelPart.equals("leg3"))
            {
                return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 5);
            }
            else if (modelPart.equals("leg4"))
            {
                return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 6);
            }
            else if (modelPart.equals("leg5"))
            {
                return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 7);
            }
            else if (modelPart.equals("leg6"))
            {
                return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 8);
            }
            else if (modelPart.equals("leg7"))
            {
                return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 9);
            }
            else
            {
                return modelPart.equals("leg8") ? (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 10) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "neck", "body", "leg1", "leg2", "leg3", "leg4", "leg5", "leg6", "leg7", "leg8"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        SpiderRenderer spiderrenderer = new SpiderRenderer(entityrenderermanager);
        spiderrenderer.entityModel = (EntityModel)modelBase;
        spiderrenderer.shadowSize = shadowSize;
        return spiderrenderer;
    }
}
