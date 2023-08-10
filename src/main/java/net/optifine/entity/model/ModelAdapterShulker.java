package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterShulker extends ModelAdapter
{
    public ModelAdapterShulker()
    {
        super(EntityType.SHULKER, "shulker", 0.0F);
    }

    public Model makeModel()
    {
        return new ShulkerModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof ShulkerModel))
        {
            return null;
        }
        else
        {
            ShulkerModel shulkermodel = (ShulkerModel)model;

            if (modelPart.equals("base"))
            {
                return (ModelRenderer)Reflector.ModelShulker_ModelRenderers.getValue(shulkermodel, 0);
            }
            else if (modelPart.equals("lid"))
            {
                return (ModelRenderer)Reflector.ModelShulker_ModelRenderers.getValue(shulkermodel, 1);
            }
            else
            {
                return modelPart.equals("head") ? (ModelRenderer)Reflector.ModelShulker_ModelRenderers.getValue(shulkermodel, 2) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"base", "lid", "head"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ShulkerRenderer shulkerrenderer = new ShulkerRenderer(entityrenderermanager);
        shulkerrenderer.entityModel = (ShulkerModel)modelBase;
        shulkerrenderer.shadowSize = shadowSize;
        return shulkerrenderer;
    }
}
