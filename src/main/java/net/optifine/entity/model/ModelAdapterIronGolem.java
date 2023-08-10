package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.reflect.Reflector;

public class ModelAdapterIronGolem extends ModelAdapter
{
    public ModelAdapterIronGolem()
    {
        super(EntityType.IRON_GOLEM, "iron_golem", 0.5F);
    }

    public Model makeModel()
    {
        return new IronGolemModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof IronGolemModel))
        {
            return null;
        }
        else
        {
            IronGolemModel irongolemmodel = (IronGolemModel)model;

            if (modelPart.equals("head"))
            {
                return (ModelRenderer)Reflector.ModelIronGolem_ModelRenderers.getValue(irongolemmodel, 0);
            }
            else if (modelPart.equals("body"))
            {
                return (ModelRenderer)Reflector.ModelIronGolem_ModelRenderers.getValue(irongolemmodel, 1);
            }
            else if (modelPart.equals("right_arm"))
            {
                return (ModelRenderer)Reflector.ModelIronGolem_ModelRenderers.getValue(irongolemmodel, 2);
            }
            else if (modelPart.equals("left_arm"))
            {
                return (ModelRenderer)Reflector.ModelIronGolem_ModelRenderers.getValue(irongolemmodel, 3);
            }
            else if (modelPart.equals("left_leg"))
            {
                return (ModelRenderer)Reflector.ModelIronGolem_ModelRenderers.getValue(irongolemmodel, 4);
            }
            else
            {
                return modelPart.equals("right_leg") ? (ModelRenderer)Reflector.ModelIronGolem_ModelRenderers.getValue(irongolemmodel, 5) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "body", "right_arm", "left_arm", "left_leg", "right_leg"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        IronGolemRenderer irongolemrenderer = new IronGolemRenderer(entityrenderermanager);
        irongolemrenderer.entityModel = (IronGolemModel)modelBase;
        irongolemrenderer.shadowSize = shadowSize;
        return irongolemrenderer;
    }
}
