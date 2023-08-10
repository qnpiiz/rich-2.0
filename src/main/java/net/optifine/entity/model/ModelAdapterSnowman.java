package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SnowManRenderer;
import net.minecraft.client.renderer.entity.model.SnowManModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterSnowman extends ModelAdapter
{
    public ModelAdapterSnowman()
    {
        super(EntityType.SNOW_GOLEM, "snow_golem", 0.5F);
    }

    public Model makeModel()
    {
        return new SnowManModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof SnowManModel))
        {
            return null;
        }
        else
        {
            SnowManModel snowmanmodel = (SnowManModel)model;

            if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.ModelSnowman_ModelRenderers.getValue(snowmanmodel, 0);
            }
            else if (modelPart.equals("body_bottom"))
            {
                return (ModelRenderer)Reflector.ModelSnowman_ModelRenderers.getValue(snowmanmodel, 1);
            }
            else if (modelPart.equals("head"))
            {
                return (ModelRenderer)Reflector.ModelSnowman_ModelRenderers.getValue(snowmanmodel, 2);
            }
            else if (modelPart.equals("right_hand"))
            {
                return (ModelRenderer)Reflector.ModelSnowman_ModelRenderers.getValue(snowmanmodel, 3);
            }
            else
            {
                return modelPart.equals("left_hand") ? (ModelRenderer)Reflector.ModelSnowman_ModelRenderers.getValue(snowmanmodel, 4) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body", "body_bottom", "head", "right_hand", "left_hand"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        SnowManRenderer snowmanrenderer = new SnowManRenderer(entityrenderermanager);
        snowmanrenderer.entityModel = (SnowManModel)modelBase;
        snowmanrenderer.shadowSize = shadowSize;
        return snowmanrenderer;
    }
}
