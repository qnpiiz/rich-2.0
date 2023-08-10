package net.optifine.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;

public class ModelAdapterTrappedChest extends ModelAdapter
{
    public ModelAdapterTrappedChest()
    {
        super(TileEntityType.TRAPPED_CHEST, "trapped_chest", 0.0F);
    }

    public Model makeModel()
    {
        return new ChestModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof ChestModel))
        {
            return null;
        }
        else
        {
            ChestModel chestmodel = (ChestModel)model;

            if (modelPart.equals("lid"))
            {
                return chestmodel.lid;
            }
            else if (modelPart.equals("base"))
            {
                return chestmodel.base;
            }
            else
            {
                return modelPart.equals("knob") ? chestmodel.knob : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"lid", "base", "knob"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.TRAPPED_CHEST);

        if (!(tileentityrenderer instanceof ChestTileEntityRenderer))
        {
            return null;
        }
        else
        {
            if (tileentityrenderer.getType() == null)
            {
                tileentityrenderer = new ChestTileEntityRenderer(tileentityrendererdispatcher);
            }

            if (!(modelBase instanceof ChestModel))
            {
                Config.warn("Not a chest model: " + modelBase);
                return null;
            }
            else
            {
                ChestModel chestmodel = (ChestModel)modelBase;
                return chestmodel.updateRenderer(tileentityrenderer);
            }
        }
    }
}
