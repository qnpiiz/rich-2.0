package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class SpiderEyesLayer<T extends Entity, M extends SpiderModel<T>> extends AbstractEyesLayer<T, M>
{
    private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation("textures/entity/spider_eyes.png"));

    public SpiderEyesLayer(IEntityRenderer<T, M> rendererIn)
    {
        super(rendererIn);
    }

    public RenderType getRenderType()
    {
        return RENDER_TYPE;
    }
}
