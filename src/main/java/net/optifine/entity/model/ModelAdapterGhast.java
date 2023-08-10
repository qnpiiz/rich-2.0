package net.optifine.entity.model;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.client.renderer.entity.model.GhastModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;

public class ModelAdapterGhast extends ModelAdapter
{
    public ModelAdapterGhast()
    {
        super(EntityType.GHAST, "ghast", 0.5F);
    }

    public Model makeModel()
    {
        return new GhastModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof GhastModel))
        {
            return null;
        }
        else
        {
            GhastModel ghastmodel = (GhastModel)model;
            Iterator<ModelRenderer> iterator = ghastmodel.getParts().iterator();

            if (modelPart.equals("body"))
            {
                return ModelRendererUtils.getModelRenderer(iterator, 0);
            }
            else
            {
                String s = "tentacle";

                if (modelPart.startsWith(s))
                {
                    String s1 = modelPart.substring(s.length());
                    int i = Config.parseInt(s1, -1);
                    return ModelRendererUtils.getModelRenderer(iterator, i);
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
        return new String[] {"body", "tentacle1", "tentacle2", "tentacle3", "tentacle4", "tentacle5", "tentacle6", "tentacle7", "tentacle8", "tentacle9"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        GhastRenderer ghastrenderer = new GhastRenderer(entityrenderermanager);
        ghastrenderer.entityModel = (GhastModel)modelBase;
        ghastrenderer.shadowSize = shadowSize;
        return ghastrenderer;
    }
}
