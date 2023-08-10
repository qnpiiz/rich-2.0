package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EndermiteRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EndermiteModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterEndermite extends ModelAdapter
{
    public ModelAdapterEndermite()
    {
        super(EntityType.ENDERMITE, "endermite", 0.3F);
    }

    public Model makeModel()
    {
        return new EndermiteModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof EndermiteModel))
        {
            return null;
        }
        else
        {
            EndermiteModel endermitemodel = (EndermiteModel)model;
            String s = "body";

            if (modelPart.startsWith(s))
            {
                ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(endermitemodel, Reflector.ModelEnderMite_bodyParts);

                if (amodelrenderer == null)
                {
                    return null;
                }
                else
                {
                    String s1 = modelPart.substring(s.length());
                    int i = Config.parseInt(s1, -1);
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

    public String[] getModelRendererNames()
    {
        return new String[] {"body1", "body2", "body3", "body4"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        EndermiteRenderer endermiterenderer = new EndermiteRenderer(entityrenderermanager);
        endermiterenderer.entityModel = (EndermiteModel)modelBase;
        endermiterenderer.shadowSize = shadowSize;
        return endermiterenderer;
    }
}
