package net.optifine.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.BellTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;

public class ModelAdapterBell extends ModelAdapter
{
    public ModelAdapterBell()
    {
        super(TileEntityType.BELL, "bell", 0.0F);
    }

    public Model makeModel()
    {
        return new BellModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof BellModel))
        {
            return null;
        }
        else
        {
            BellModel bellmodel = (BellModel)model;
            return modelPart.equals("body") ? bellmodel.bellBody : null;
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"body"};
    }

    public IEntityRenderer makeEntityRender(Model model, float shadowSize)
    {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.BELL);

        if (!(tileentityrenderer instanceof BellTileEntityRenderer))
        {
            return null;
        }
        else
        {
            if (tileentityrenderer.getType() == null)
            {
                tileentityrenderer = new BellTileEntityRenderer(tileentityrendererdispatcher);
            }

            if (!(model instanceof BellModel))
            {
                Config.warn("Not a bell model: " + model);
                return null;
            }
            else
            {
                BellModel bellmodel = (BellModel)model;
                return bellmodel.updateRenderer(tileentityrenderer);
            }
        }
    }
}
