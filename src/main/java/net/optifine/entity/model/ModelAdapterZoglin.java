package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZoglinRenderer;
import net.minecraft.client.renderer.entity.model.BoarModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterZoglin extends ModelAdapterHoglin
{
    public ModelAdapterZoglin()
    {
        super(EntityType.ZOGLIN, "zoglin", 0.7F);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ZoglinRenderer zoglinrenderer = new ZoglinRenderer(entityrenderermanager);
        zoglinrenderer.entityModel = (BoarModel)modelBase;
        zoglinrenderer.shadowSize = shadowSize;
        return zoglinrenderer;
    }
}
