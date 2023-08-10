package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PiglinModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.util.ResourceLocation;

public class PiglinRenderer extends BipedRenderer<MobEntity, PiglinModel<MobEntity>>
{
    private static final Map < EntityType<?>, ResourceLocation > field_243503_a = ImmutableMap.of(EntityType.PIGLIN, new ResourceLocation("textures/entity/piglin/piglin.png"), EntityType.ZOMBIFIED_PIGLIN, new ResourceLocation("textures/entity/piglin/zombified_piglin.png"), EntityType.field_242287_aj, new ResourceLocation("textures/entity/piglin/piglin_brute.png"));

    public PiglinRenderer(EntityRendererManager p_i232472_1_, boolean p_i232472_2_)
    {
        super(p_i232472_1_, func_239395_a_(p_i232472_2_), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel(0.5F), new BipedModel(1.02F)));
    }

    private static PiglinModel<MobEntity> func_239395_a_(boolean p_239395_0_)
    {
        PiglinModel<MobEntity> piglinmodel = new PiglinModel<>(0.0F, 64, 64);

        if (p_239395_0_)
        {
            piglinmodel.field_239116_b_.showModel = false;
        }

        return piglinmodel;
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(MobEntity entity)
    {
        ResourceLocation resourcelocation = field_243503_a.get(entity.getType());

        if (resourcelocation == null)
        {
            throw new IllegalArgumentException("I don't know what texture to use for " + entity.getType());
        }
        else
        {
            return resourcelocation;
        }
    }

    protected boolean func_230495_a_(MobEntity p_230495_1_)
    {
        return p_230495_1_ instanceof AbstractPiglinEntity && ((AbstractPiglinEntity)p_230495_1_).func_242336_eL();
    }
}
