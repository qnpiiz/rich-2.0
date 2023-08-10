package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.client.renderer.entity.model.VexModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterVex extends ModelAdapterBiped
{
    public ModelAdapterVex()
    {
        super(EntityType.VEX, "vex", 0.3F);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof VexModel))
        {
            return null;
        }
        else
        {
            ModelRenderer modelrenderer = super.getModelRenderer(model, modelPart);

            if (modelrenderer != null)
            {
                return modelrenderer;
            }
            else
            {
                VexModel vexmodel = (VexModel)model;

                if (modelPart.equals("left_wing"))
                {
                    return (ModelRenderer)Reflector.getFieldValue(vexmodel, Reflector.ModelVex_leftWing);
                }
                else
                {
                    return modelPart.equals("right_wing") ? (ModelRenderer)Reflector.getFieldValue(vexmodel, Reflector.ModelVex_rightWing) : null;
                }
            }
        }
    }

    public String[] getModelRendererNames()
    {
        String[] astring = super.getModelRendererNames();
        return (String[])Config.addObjectsToArray(astring, new String[] {"left_wing", "right_wing"});
    }

    public Model makeModel()
    {
        return new VexModel();
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        VexRenderer vexrenderer = new VexRenderer(entityrenderermanager);
        vexrenderer.entityModel = (VexModel)modelBase;
        vexrenderer.shadowSize = shadowSize;
        return vexrenderer;
    }
}
