package net.optifine.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ConduitTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;

public class ModelAdapterConduit extends ModelAdapter
{
    public ModelAdapterConduit()
    {
        super(TileEntityType.CONDUIT, "conduit", 0.0F);
    }

    public Model makeModel()
    {
        return new ConduitModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof ConduitModel))
        {
            return null;
        }
        else
        {
            ConduitModel conduitmodel = (ConduitModel)model;

            if (modelPart.equals("eye"))
            {
                return conduitmodel.eye;
            }
            else if (modelPart.equals("wind"))
            {
                return conduitmodel.wind;
            }
            else if (modelPart.equals("base"))
            {
                return conduitmodel.base;
            }
            else
            {
                return modelPart.equals("cage") ? conduitmodel.cage : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"eye", "wind", "base", "cage"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.CONDUIT);

        if (!(tileentityrenderer instanceof ConduitTileEntityRenderer))
        {
            return null;
        }
        else
        {
            if (tileentityrenderer.getType() == null)
            {
                tileentityrenderer = new ConduitTileEntityRenderer(tileentityrendererdispatcher);
            }

            if (!(modelBase instanceof ConduitModel))
            {
                Config.warn("Not a conduit model: " + modelBase);
                return null;
            }
            else
            {
                ConduitModel conduitmodel = (ConduitModel)modelBase;
                return conduitmodel.updateRenderer(tileentityrenderer);
            }
        }
    }
}
