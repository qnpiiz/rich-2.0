package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.client.renderer.entity.model.PigModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.ResourceLocation;

public class PigRenderer extends MobRenderer<PigEntity, PigModel<PigEntity>>
{
    private static final ResourceLocation PIG_TEXTURES = new ResourceLocation("textures/entity/pig/pig.png");

    public PigRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new PigModel<>(), 0.7F);
        this.addLayer(new SaddleLayer<>(this, new PigModel<>(0.5F), new ResourceLocation("textures/entity/pig/pig_saddle.png")));
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(PigEntity entity)
    {
        return PIG_TEXTURES;
    }
}
