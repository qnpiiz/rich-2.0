package net.optifine.entity.model;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.SheepWoolLayer;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.client.renderer.entity.model.SheepWoolModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.Config;

public class ModelAdapterSheepWool extends ModelAdapterQuadruped
{
    public ModelAdapterSheepWool()
    {
        super(EntityType.SHEEP, "sheep_wool", 0.7F);
    }

    public Model makeModel()
    {
        return new SheepWoolModel();
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        EntityRenderer entityrenderer = entityrenderermanager.getEntityRenderMap().get(EntityType.SHEEP);

        if (!(entityrenderer instanceof SheepRenderer))
        {
            Config.warn("Not a RenderSheep: " + entityrenderer);
            return null;
        }
        else
        {
            if (entityrenderer.getType() == null)
            {
                SheepRenderer sheeprenderer = new SheepRenderer(entityrenderermanager);
                sheeprenderer.entityModel = new SheepModel<>();
                sheeprenderer.shadowSize = 0.7F;
                entityrenderer = sheeprenderer;
            }

            SheepRenderer sheeprenderer1 = (SheepRenderer)entityrenderer;
            List list = sheeprenderer1.getLayerRenderers();
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                LayerRenderer layerrenderer = (LayerRenderer)iterator.next();

                if (layerrenderer instanceof SheepWoolLayer)
                {
                    iterator.remove();
                }
            }

            SheepWoolLayer sheepwoollayer = new SheepWoolLayer(sheeprenderer1);
            sheepwoollayer.sheepModel = (SheepWoolModel)modelBase;
            sheeprenderer1.addLayer(sheepwoollayer);
            return sheeprenderer1;
        }
    }
}
