package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.BoarModel;
import net.minecraft.entity.monster.ZoglinEntity;
import net.minecraft.util.ResourceLocation;

public class ZoglinRenderer extends MobRenderer<ZoglinEntity, BoarModel<ZoglinEntity>>
{
    private static final ResourceLocation field_239399_a_ = new ResourceLocation("textures/entity/hoglin/zoglin.png");

    public ZoglinRenderer(EntityRendererManager p_i232474_1_)
    {
        super(p_i232474_1_, new BoarModel<>(), 0.7F);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(ZoglinEntity entity)
    {
        return field_239399_a_;
    }
}
