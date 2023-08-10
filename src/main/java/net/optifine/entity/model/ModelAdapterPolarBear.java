package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PolarBearRenderer;
import net.minecraft.client.renderer.entity.model.PolarBearModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterPolarBear extends ModelAdapterQuadruped
{
    public ModelAdapterPolarBear()
    {
        super(EntityType.POLAR_BEAR, "polar_bear", 0.7F);
    }

    public Model makeModel()
    {
        return new PolarBearModel();
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        PolarBearRenderer polarbearrenderer = new PolarBearRenderer(entityrenderermanager);
        polarbearrenderer.entityModel = (PolarBearModel)modelBase;
        polarbearrenderer.shadowSize = shadowSize;
        return polarbearrenderer;
    }
}
