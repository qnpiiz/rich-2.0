package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterSheep extends ModelAdapterQuadruped
{
    public ModelAdapterSheep()
    {
        super(EntityType.SHEEP, "sheep", 0.7F);
    }

    public Model makeModel()
    {
        return new SheepModel();
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        SheepRenderer sheeprenderer = new SheepRenderer(entityrenderermanager);
        sheeprenderer.entityModel = (SheepModel)modelBase;
        sheeprenderer.shadowSize = shadowSize;
        return sheeprenderer;
    }
}
