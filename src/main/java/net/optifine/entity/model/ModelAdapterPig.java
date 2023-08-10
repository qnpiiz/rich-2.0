package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.client.renderer.entity.model.PigModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterPig extends ModelAdapterQuadruped
{
    public ModelAdapterPig()
    {
        super(EntityType.PIG, "pig", 0.7F);
    }

    public Model makeModel()
    {
        return new PigModel();
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        PigRenderer pigrenderer = new PigRenderer(entityrenderermanager);
        pigrenderer.entityModel = (PigModel)modelBase;
        pigrenderer.shadowSize = shadowSize;
        return pigrenderer;
    }
}
