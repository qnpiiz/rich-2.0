package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterSlime extends ModelAdapter
{
    public ModelAdapterSlime()
    {
        super(EntityType.SLIME, "slime", 0.25F);
    }

    public Model makeModel()
    {
        return new SlimeModel(16);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof SlimeModel))
        {
            return null;
        }
        else
        {
            SlimeModel slimemodel = (SlimeModel)model;

            if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.getFieldValue(slimemodel, Reflector.ModelSlime_ModelRenderers, 0);
            }
            else if (modelPart.equals("left_eye"))
            {
                return (ModelRenderer)Reflector.getFieldValue(slimemodel, Reflector.ModelSlime_ModelRenderers, 1);
            }
            else if (modelPart.equals("right_eye"))
            {
                return (ModelRenderer)Reflector.getFieldValue(slimemodel, Reflector.ModelSlime_ModelRenderers, 2);
            }
            else
            {
                return modelPart.equals("mouth") ? (ModelRenderer)Reflector.getFieldValue(slimemodel, Reflector.ModelSlime_ModelRenderers, 3) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body", "left_eye", "right_eye", "mouth"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        SlimeRenderer slimerenderer = new SlimeRenderer(entityrenderermanager);
        slimerenderer.entityModel = (SlimeModel)modelBase;
        slimerenderer.shadowSize = shadowSize;
        return slimerenderer;
    }
}
