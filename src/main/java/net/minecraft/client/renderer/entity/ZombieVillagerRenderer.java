package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.client.renderer.entity.model.ZombieVillagerModel;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

public class ZombieVillagerRenderer extends BipedRenderer<ZombieVillagerEntity, ZombieVillagerModel<ZombieVillagerEntity>>
{
    private static final ResourceLocation ZOMBIE_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");

    public ZombieVillagerRenderer(EntityRendererManager renderManagerIn, IReloadableResourceManager resourceManagerIn)
    {
        super(renderManagerIn, new ZombieVillagerModel<>(0.0F, false), 0.5F);
        this.addLayer(new BipedArmorLayer<>(this, new ZombieVillagerModel(0.5F, true), new ZombieVillagerModel(1.0F, true)));
        this.addLayer(new VillagerLevelPendantLayer<>(this, resourceManagerIn, "zombie_villager"));
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(ZombieVillagerEntity entity)
    {
        return ZOMBIE_VILLAGER_TEXTURES;
    }

    protected boolean func_230495_a_(ZombieVillagerEntity p_230495_1_)
    {
        return p_230495_1_.isConverting();
    }
}
