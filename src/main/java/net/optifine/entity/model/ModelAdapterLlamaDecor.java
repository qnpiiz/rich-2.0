package net.optifine.entity.model;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LlamaDecorLayer;
import net.minecraft.client.renderer.entity.model.LlamaModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterLlamaDecor extends ModelAdapterLlama
{
    public ModelAdapterLlamaDecor()
    {
        super(EntityType.LLAMA, "llama_decor", 0.7F);
    }

    public Model makeModel()
    {
        return new LlamaModel(0.5F);
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        EntityRenderer entityrenderer = entityrenderermanager.getEntityRenderMap().get(EntityType.LLAMA);

        if (!(entityrenderer instanceof LlamaRenderer))
        {
            Config.warn("Not a RenderLlama: " + entityrenderer);
            return null;
        }
        else
        {
            if (entityrenderer.getType() == null)
            {
                LlamaRenderer llamarenderer = new LlamaRenderer(entityrenderermanager);
                llamarenderer.entityModel = new LlamaModel<>(0.0F);
                llamarenderer.shadowSize = 0.7F;
                entityrenderer = llamarenderer;
            }

            LlamaRenderer llamarenderer1 = (LlamaRenderer)entityrenderer;
            List list = llamarenderer1.getLayerRenderers();
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                LayerRenderer layerrenderer = (LayerRenderer)iterator.next();

                if (layerrenderer instanceof LlamaDecorLayer)
                {
                    iterator.remove();
                }
            }

            LlamaDecorLayer llamadecorlayer = new LlamaDecorLayer(llamarenderer1);

            if (!Reflector.LayerLlamaDecor_model.exists())
            {
                Config.warn("Field not found: LayerLlamaDecor.model");
                return null;
            }
            else
            {
                Reflector.LayerLlamaDecor_model.setValue(llamadecorlayer, modelBase);
                llamarenderer1.addLayer(llamadecorlayer);
                return llamarenderer1;
            }
        }
    }
}
