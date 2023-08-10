package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractZombieRenderer<T extends ZombieEntity, M extends ZombieModel<T>> extends BipedRenderer<T, M>
{
    private static final ResourceLocation field_217771_a = new ResourceLocation("textures/entity/zombie/zombie.png");

    protected AbstractZombieRenderer(EntityRendererManager p_i50974_1_, M p_i50974_2_, M p_i50974_3_, M p_i50974_4_)
    {
        super(p_i50974_1_, p_i50974_2_, 0.5F);
        this.addLayer(new BipedArmorLayer<>(this, p_i50974_3_, p_i50974_4_));
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(ZombieEntity entity)
    {
        return field_217771_a;
    }

    protected boolean func_230495_a_(T p_230495_1_)
    {
        return p_230495_1_.isDrowning();
    }
}
