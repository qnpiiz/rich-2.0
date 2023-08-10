package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SilverfishRenderer;
import net.minecraft.client.renderer.entity.model.SilverfishModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterSilverfish extends ModelAdapter
{
    public ModelAdapterSilverfish()
    {
        super(EntityType.SILVERFISH, "silverfish", 0.3F);
    }

    public Model makeModel()
    {
        return new SilverfishModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof SilverfishModel))
        {
            return null;
        }
        else
        {
            SilverfishModel silverfishmodel = (SilverfishModel)model;
            String s = "body";

            if (modelPart.startsWith(s))
            {
                ModelRenderer[] amodelrenderer1 = (ModelRenderer[])Reflector.getFieldValue(silverfishmodel, Reflector.ModelSilverfish_bodyParts);

                if (amodelrenderer1 == null)
                {
                    return null;
                }
                else
                {
                    String s3 = modelPart.substring(s.length());
                    int j = Config.parseInt(s3, -1);
                    --j;
                    return j >= 0 && j < amodelrenderer1.length ? amodelrenderer1[j] : null;
                }
            }
            else
            {
                String s1 = "wing";

                if (modelPart.startsWith(s1))
                {
                    ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(silverfishmodel, Reflector.ModelSilverfish_wingParts);

                    if (amodelrenderer == null)
                    {
                        return null;
                    }
                    else
                    {
                        String s2 = modelPart.substring(s1.length());
                        int i = Config.parseInt(s2, -1);
                        --i;
                        return i >= 0 && i < amodelrenderer.length ? amodelrenderer[i] : null;
                    }
                }
                else
                {
                    return null;
                }
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body1", "body2", "body3", "body4", "body5", "body6", "body7", "wing1", "wing2", "wing3"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        SilverfishRenderer silverfishrenderer = new SilverfishRenderer(entityrenderermanager);
        silverfishrenderer.entityModel = (SilverfishModel)modelBase;
        silverfishrenderer.shadowSize = shadowSize;
        return silverfishrenderer;
    }
}
