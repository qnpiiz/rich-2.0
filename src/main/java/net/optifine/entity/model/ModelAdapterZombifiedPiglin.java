package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.model.PiglinModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterZombifiedPiglin extends ModelAdapterPiglin
{
    public ModelAdapterZombifiedPiglin()
    {
        super(EntityType.ZOMBIFIED_PIGLIN, "zombified_piglin", 0.5F);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        PiglinRenderer piglinrenderer = new PiglinRenderer(entityrenderermanager, true);
        piglinrenderer.entityModel = (PiglinModel)modelBase;
        piglinrenderer.shadowSize = shadowSize;
        return piglinrenderer;
    }
}
