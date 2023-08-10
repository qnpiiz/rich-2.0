package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PandaRenderer;
import net.minecraft.client.renderer.entity.model.PandaModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterPanda extends ModelAdapterQuadruped
{
    public ModelAdapterPanda()
    {
        super(EntityType.PANDA, "panda", 0.9F);
    }

    public Model makeModel()
    {
        return new PandaModel(9, 0.0F);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        PandaRenderer pandarenderer = new PandaRenderer(entityrenderermanager);
        pandarenderer.entityModel = (PandaModel)modelBase;
        pandarenderer.shadowSize = shadowSize;
        return pandarenderer;
    }
}
