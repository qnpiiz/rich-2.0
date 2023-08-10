package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.BoarModel;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.util.ResourceLocation;

public class HoglinRenderer extends MobRenderer<HoglinEntity, BoarModel<HoglinEntity>>
{
    private static final ResourceLocation field_239382_a_ = new ResourceLocation("textures/entity/hoglin/hoglin.png");

    public HoglinRenderer(EntityRendererManager p_i232470_1_)
    {
        super(p_i232470_1_, new BoarModel<>(), 0.7F);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(HoglinEntity entity)
    {
        return field_239382_a_;
    }

    protected boolean func_230495_a_(HoglinEntity p_230495_1_)
    {
        return p_230495_1_.func_234364_eK_();
    }
}
