package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EndermanModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;

public class ModelAdapterEnderman extends ModelAdapterBiped
{
    public ModelAdapterEnderman()
    {
        super(EntityType.ENDERMAN, "enderman", 0.5F);
    }

    public Model makeModel()
    {
        return new EndermanModel(0.0F);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        EndermanRenderer endermanrenderer = new EndermanRenderer(entityrenderermanager);
        endermanrenderer.entityModel = (EndermanModel)modelBase;
        endermanrenderer.shadowSize = shadowSize;
        return endermanrenderer;
    }
}
