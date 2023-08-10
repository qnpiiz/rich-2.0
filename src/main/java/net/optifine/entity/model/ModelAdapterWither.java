package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.WitherRenderer;
import net.minecraft.client.renderer.entity.model.WitherModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterWither extends ModelAdapter
{
    public ModelAdapterWither()
    {
        super(EntityType.WITHER, "wither", 0.5F);
    }

    public Model makeModel()
    {
        return new WitherModel(0.0F);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof WitherModel))
        {
            return null;
        }
        else
        {
            WitherModel withermodel = (WitherModel)model;
            String s = "body";

            if (modelPart.startsWith(s))
            {
                ModelRenderer[] amodelrenderer1 = (ModelRenderer[])Reflector.getFieldValue(withermodel, Reflector.ModelWither_bodyParts);

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
                String s1 = "head";

                if (modelPart.startsWith(s1))
                {
                    ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(withermodel, Reflector.ModelWither_heads);

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
        return new String[] {"body1", "body2", "body3", "head1", "head2", "head3"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        WitherRenderer witherrenderer = new WitherRenderer(entityrenderermanager);
        witherrenderer.entityModel = (WitherModel)modelBase;
        witherrenderer.shadowSize = shadowSize;
        return witherrenderer;
    }
}
