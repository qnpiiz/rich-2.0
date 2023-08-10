package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.DrownedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterDrowned extends ModelAdapterZombie
{
    public ModelAdapterDrowned()
    {
        super(EntityType.DROWNED, "drowned", 0.5F);
    }

    public Model makeModel()
    {
        return new DrownedModel(0.0F, 0.0F, 64, 64);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        DrownedRenderer drownedrenderer = new DrownedRenderer(entityrenderermanager);
        drownedrenderer.entityModel = (DrownedModel)modelBase;
        drownedrenderer.shadowSize = shadowSize;
        return drownedrenderer;
    }
}
