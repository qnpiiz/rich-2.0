package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.VindicatorRenderer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterVindicator extends ModelAdapterIllager
{
    public ModelAdapterVindicator()
    {
        super(EntityType.VINDICATOR, "vindicator", 0.5F, new String[] {"vindication_illager"});
    }

    public Model makeModel()
    {
        return new IllagerModel(0.0F, 0.0F, 64, 64);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        VindicatorRenderer vindicatorrenderer = new VindicatorRenderer(entityrenderermanager);
        vindicatorrenderer.entityModel = (IllagerModel)modelBase;
        vindicatorrenderer.shadowSize = shadowSize;
        return vindicatorrenderer;
    }
}
