package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterSkeleton extends ModelAdapterBiped
{
    public ModelAdapterSkeleton()
    {
        super(EntityType.SKELETON, "skeleton", 0.7F);
    }

    public Model makeModel()
    {
        return new SkeletonModel();
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        SkeletonRenderer skeletonrenderer = new SkeletonRenderer(entityrenderermanager);
        skeletonrenderer.entityModel = (SkeletonModel)modelBase;
        skeletonrenderer.shadowSize = shadowSize;
        return skeletonrenderer;
    }
}
