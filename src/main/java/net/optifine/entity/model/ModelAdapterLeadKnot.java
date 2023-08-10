package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LeashKnotRenderer;
import net.minecraft.client.renderer.entity.model.LeashKnotModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterLeadKnot extends ModelAdapter
{
    public ModelAdapterLeadKnot()
    {
        super(EntityType.LEASH_KNOT, "lead_knot", 0.0F);
    }

    public Model makeModel()
    {
        return new LeashKnotModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof LeashKnotModel))
        {
            return null;
        }
        else
        {
            LeashKnotModel leashknotmodel = (LeashKnotModel)model;
            return modelPart.equals("knot") ? (ModelRenderer)Reflector.ModelLeashKnot_knotRenderer.getValue(leashknotmodel) : null;
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"knot"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        LeashKnotRenderer leashknotrenderer = new LeashKnotRenderer(entityrenderermanager);

        if (!Reflector.RenderLeashKnot_leashKnotModel.exists())
        {
            Config.warn("Field not found: RenderLeashKnot.leashKnotModel");
            return null;
        }
        else
        {
            Reflector.setFieldValue(leashknotrenderer, Reflector.RenderLeashKnot_leashKnotModel, modelBase);
            leashknotrenderer.shadowSize = shadowSize;
            return leashknotrenderer;
        }
    }
}
