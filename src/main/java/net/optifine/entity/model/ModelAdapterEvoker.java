package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.EvokerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterEvoker extends ModelAdapterIllager
{
    public ModelAdapterEvoker()
    {
        super(EntityType.EVOKER, "evoker", 0.5F, new String[] {"evocation_illager"});
    }

    public Model makeModel()
    {
        return new IllagerModel(0.0F, 0.0F, 64, 64);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        EvokerRenderer evokerrenderer = new EvokerRenderer(entityrenderermanager);
        evokerrenderer.entityModel = (EntityModel)modelBase;
        evokerrenderer.shadowSize = shadowSize;
        return evokerrenderer;
    }
}
