package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterZombieHorse extends ModelAdapterHorse
{
    public ModelAdapterZombieHorse()
    {
        super(EntityType.ZOMBIE_HORSE, "zombie_horse", 0.75F);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        UndeadHorseRenderer undeadhorserenderer = new UndeadHorseRenderer(entityrenderermanager);
        undeadhorserenderer.entityModel = (HorseModel)modelBase;
        undeadhorserenderer.shadowSize = shadowSize;
        return undeadhorserenderer;
    }
}
