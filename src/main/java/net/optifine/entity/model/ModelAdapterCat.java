package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.CatModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterCat extends ModelAdapterOcelot
{
    public ModelAdapterCat()
    {
        super(EntityType.CAT, "cat", 0.4F);
    }

    public Model makeModel()
    {
        return new CatModel(0.0F);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        CatRenderer catrenderer = new CatRenderer(entityrenderermanager);
        catrenderer.entityModel = (CatModel)modelBase;
        catrenderer.shadowSize = shadowSize;
        return catrenderer;
    }
}
