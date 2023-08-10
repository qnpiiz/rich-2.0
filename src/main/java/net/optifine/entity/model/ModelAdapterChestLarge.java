package net.optifine.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;

public class ModelAdapterChestLarge extends ModelAdapter
{
    public ModelAdapterChestLarge()
    {
        super(TileEntityType.CHEST, "chest_large", 0.0F);
    }

    public Model makeModel()
    {
        return new ChestLargeModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof ChestLargeModel))
        {
            return null;
        }
        else
        {
            ChestLargeModel chestlargemodel = (ChestLargeModel)model;

            if (modelPart.equals("lid_left"))
            {
                return chestlargemodel.lid_left;
            }
            else if (modelPart.equals("base_left"))
            {
                return chestlargemodel.base_left;
            }
            else if (modelPart.equals("knob_left"))
            {
                return chestlargemodel.knob_left;
            }
            else if (modelPart.equals("lid_right"))
            {
                return chestlargemodel.lid_right;
            }
            else if (modelPart.equals("base_right"))
            {
                return chestlargemodel.base_right;
            }
            else
            {
                return modelPart.equals("knob_right") ? chestlargemodel.knob_right : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"lid_left", "base_left", "knob_left", "lid_right", "base_right", "knob_right"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.CHEST);

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

            if (!(modelBase instanceof ChestLargeModel))
            {
                Config.warn("Not a large chest model: " + modelBase);
                return null;
            }
            else
            {
                ChestLargeModel chestlargemodel = (ChestLargeModel)modelBase;
                return chestlargemodel.updateRenderer(tileentityrenderer);
            }
        }
    }
}
