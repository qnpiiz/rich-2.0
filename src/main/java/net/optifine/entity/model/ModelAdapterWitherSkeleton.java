package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterWitherSkeleton extends ModelAdapterBiped
{
    public ModelAdapterWitherSkeleton()
    {
        super(EntityType.WITHER_SKELETON, "wither_skeleton", 0.7F);
    }

    public Model makeModel()
    {
        return new SkeletonModel();
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        WitherSkeletonRenderer witherskeletonrenderer = new WitherSkeletonRenderer(entityrenderermanager);
        witherskeletonrenderer.entityModel = (SkeletonModel)modelBase;
        witherskeletonrenderer.shadowSize = shadowSize;
        return witherskeletonrenderer;
    }
}
