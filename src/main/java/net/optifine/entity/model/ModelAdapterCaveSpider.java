package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.CaveSpiderRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterCaveSpider extends ModelAdapterSpider
{
    public ModelAdapterCaveSpider()
    {
        super(EntityType.CAVE_SPIDER, "cave_spider", 0.7F);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        CaveSpiderRenderer cavespiderrenderer = new CaveSpiderRenderer(entityrenderermanager);
        cavespiderrenderer.entityModel = (SpiderModel)modelBase;
        cavespiderrenderer.shadowSize = shadowSize;
        return cavespiderrenderer;
    }
}
