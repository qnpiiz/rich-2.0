package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ShulkerBoxTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterShulkerBox extends ModelAdapter
{
    public ModelAdapterShulkerBox()
    {
        super(TileEntityType.SHULKER_BOX, "shulker_box", 0.0F);
    }

    public Model makeModel()
    {
        return new ShulkerModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof ShulkerModel))
        {
            return null;
        }
        else
        {
            ShulkerModel shulkermodel = (ShulkerModel)model;

            if (modelPart.equals("base"))
            {
                return (ModelRenderer)Reflector.ModelShulker_ModelRenderers.getValue(shulkermodel, 0);
            }
            else if (modelPart.equals("lid"))
            {
                return (ModelRenderer)Reflector.ModelShulker_ModelRenderers.getValue(shulkermodel, 1);
            }
            else
            {
                return modelPart.equals("head") ? (ModelRenderer)Reflector.ModelShulker_ModelRenderers.getValue(shulkermodel, 2) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"base", "lid", "head"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.SHULKER_BOX);

        if (!(tileentityrenderer instanceof ShulkerBoxTileEntityRenderer))
        {
            return null;
        }
        else
        {
            if (tileentityrenderer.getType() == null)
            {
                tileentityrenderer = new ShulkerBoxTileEntityRenderer((ShulkerModel)modelBase, tileentityrendererdispatcher);
            }

            if (!Reflector.TileEntityShulkerBoxRenderer_model.exists())
            {
                Config.warn("Field not found: TileEntityShulkerBoxRenderer.model");
                return null;
            }
            else
            {
                Reflector.setFieldValue(tileentityrenderer, Reflector.TileEntityShulkerBoxRenderer_model, modelBase);
                return tileentityrenderer;
            }
        }
    }
}
