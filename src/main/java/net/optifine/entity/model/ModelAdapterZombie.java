package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterZombie extends ModelAdapterBiped
{
    public ModelAdapterZombie()
    {
        super(EntityType.ZOMBIE, "zombie", 0.5F);
    }

    protected ModelAdapterZombie(EntityType type, String name, float shadowSize)
    {
        super(type, name, shadowSize);
    }

    public Model makeModel()
    {
        return new ZombieModel(0.0F, false);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ZombieRenderer zombierenderer = new ZombieRenderer(entityrenderermanager);
        zombierenderer.entityModel = (ZombieModel)modelBase;
        zombierenderer.shadowSize = shadowSize;
        return zombierenderer;
    }
}
