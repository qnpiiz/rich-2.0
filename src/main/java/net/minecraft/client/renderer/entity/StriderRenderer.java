package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.client.renderer.entity.model.StriderModel;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.ResourceLocation;

public class StriderRenderer extends MobRenderer<StriderEntity, StriderModel<StriderEntity>>
{
    private static final ResourceLocation field_239397_a_ = new ResourceLocation("textures/entity/strider/strider.png");
    private static final ResourceLocation field_239398_g_ = new ResourceLocation("textures/entity/strider/strider_cold.png");

    public StriderRenderer(EntityRendererManager p_i232473_1_)
    {
        super(p_i232473_1_, new StriderModel<>(), 0.5F);
        this.addLayer(new SaddleLayer<>(this, new StriderModel<>(), new ResourceLocation("textures/entity/strider/strider_saddle.png")));
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(StriderEntity entity)
    {
        return entity.func_234315_eI_() ? field_239398_g_ : field_239397_a_;
    }

    protected void preRenderCallback(StriderEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime)
    {
        if (entitylivingbaseIn.isChild())
        {
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            this.shadowSize = 0.25F;
        }
        else
        {
            this.shadowSize = 0.5F;
        }
    }

    protected boolean func_230495_a_(StriderEntity p_230495_1_)
    {
        return p_230495_1_.func_234315_eI_();
    }
}
