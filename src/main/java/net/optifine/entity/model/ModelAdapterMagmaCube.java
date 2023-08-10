package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MagmaCubeRenderer;
import net.minecraft.client.renderer.entity.model.MagmaCubeModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterMagmaCube extends ModelAdapter
{
    public ModelAdapterMagmaCube()
    {
        super(EntityType.MAGMA_CUBE, "magma_cube", 0.5F);
    }

    public Model makeModel()
    {
        return new MagmaCubeModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof MagmaCubeModel))
        {
            return null;
        }
        else
        {
            MagmaCubeModel magmacubemodel = (MagmaCubeModel)model;

            if (modelPart.equals("core"))
            {
                return (ModelRenderer)Reflector.getFieldValue(magmacubemodel, Reflector.ModelMagmaCube_core);
            }
            else
            {
                String s = "segment";

                if (modelPart.startsWith(s))
                {
                    ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(magmacubemodel, Reflector.ModelMagmaCube_segments);

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
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"core", "segment1", "segment2", "segment3", "segment4", "segment5", "segment6", "segment7", "segment8"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        MagmaCubeRenderer magmacuberenderer = new MagmaCubeRenderer(entityrenderermanager);
        magmacuberenderer.entityModel = (MagmaCubeModel)modelBase;
        magmacuberenderer.shadowSize = shadowSize;
        return magmacuberenderer;
    }
}
