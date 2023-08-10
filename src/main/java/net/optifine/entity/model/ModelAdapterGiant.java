package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.GiantZombieRenderer;
import net.minecraft.client.renderer.entity.model.GiantModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterGiant extends ModelAdapterZombie
{
    public ModelAdapterGiant()
    {
        super(EntityType.GIANT, "giant", 3.0F);
    }

    public Model makeModel()
    {
        return new GiantModel();
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        GiantZombieRenderer giantzombierenderer = new GiantZombieRenderer(entityrenderermanager, 6.0F);
        giantzombierenderer.entityModel = (GiantModel)modelBase;
        giantzombierenderer.shadowSize = shadowSize;
        return giantzombierenderer;
    }
}
