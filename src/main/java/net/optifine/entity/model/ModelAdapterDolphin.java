package net.optifine.entity.model;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.DolphinModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;

public class ModelAdapterDolphin extends ModelAdapter
{
    public ModelAdapterDolphin()
    {
        super(EntityType.DOLPHIN, "dolphin", 0.7F);
    }

    public Model makeModel()
    {
        return new DolphinModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof DolphinModel))
        {
            return null;
        }
        else
        {
            DolphinModel dolphinmodel = (DolphinModel)model;
            Iterator<ModelRenderer> iterator = dolphinmodel.getParts().iterator();
            ModelRenderer modelrenderer = ModelRendererUtils.getModelRenderer(iterator, 0);

            if (modelrenderer == null)
            {
                return null;
            }
            else if (modelPart.equals("body"))
            {
                return modelrenderer;
            }
            else if (modelPart.equals("back_fin"))
            {
                return modelrenderer.getChild(0);
            }
            else if (modelPart.equals("left_fin"))
            {
                return modelrenderer.getChild(1);
            }
            else if (modelPart.equals("right_fin"))
            {
                return modelrenderer.getChild(2);
            }
            else if (modelPart.equals("tail"))
            {
                return modelrenderer.getChild(3);
            }
            else if (modelPart.equals("tail_fin"))
            {
                return modelrenderer.getChild(3).getChild(0);
            }
            else
            {
                return modelPart.equals("head") ? modelrenderer.getChild(4) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body", "back_fin", "left_fin", "right_fin", "tail", "tail_fin", "head"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        DolphinRenderer dolphinrenderer = new DolphinRenderer(entityrenderermanager);
        dolphinrenderer.entityModel = (DolphinModel)modelBase;
        dolphinrenderer.shadowSize = shadowSize;
        return dolphinrenderer;
    }
}
