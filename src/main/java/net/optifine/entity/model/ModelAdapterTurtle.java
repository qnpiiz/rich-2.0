package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TurtleRenderer;
import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.client.renderer.entity.model.TurtleModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterTurtle extends ModelAdapterQuadruped
{
    public ModelAdapterTurtle()
    {
        super(EntityType.TURTLE, "turtle", 0.7F);
    }

    public Model makeModel()
    {
        return new TurtleModel(0.0F);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof QuadrupedModel))
        {
            return null;
        }
        else
        {
            TurtleModel turtlemodel = (TurtleModel)model;
            return modelPart.equals("body2") ? (ModelRenderer)Reflector.ModelTurtle_body2.getValue(turtlemodel) : super.getModelRenderer(model, modelPart);
        }
    }

    public String[] getModelRendererNames()
    {
        String[] astring = super.getModelRendererNames();
        return (String[])Config.addObjectToArray(astring, "body2");
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        TurtleRenderer turtlerenderer = new TurtleRenderer(entityrenderermanager);
        turtlerenderer.entityModel = (TurtleModel)modelBase;
        turtlerenderer.shadowSize = shadowSize;
        return turtlerenderer;
    }
}
