package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TNTMinecartRenderer;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterMinecartTnt extends ModelAdapterMinecart
{
    public ModelAdapterMinecartTnt()
    {
        super(EntityType.TNT_MINECART, "tnt_minecart", 0.5F);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        TNTMinecartRenderer tntminecartrenderer = new TNTMinecartRenderer(entityrenderermanager);

        if (!Reflector.RenderMinecart_modelMinecart.exists())
        {
            Config.warn("Field not found: RenderMinecart.modelMinecart");
            return null;
        }
        else
        {
            Reflector.setFieldValue(tntminecartrenderer, Reflector.RenderMinecart_modelMinecart, modelBase);
            tntminecartrenderer.shadowSize = shadowSize;
            return tntminecartrenderer;
        }
    }
}
