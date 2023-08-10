package net.optifine.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterSign extends ModelAdapter
{
    public ModelAdapterSign()
    {
        super(TileEntityType.SIGN, "sign", 0.0F);
    }

    public Model makeModel()
    {
        return new SignTileEntityRenderer.SignModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof SignTileEntityRenderer.SignModel))
        {
            return null;
        }
        else
        {
            SignTileEntityRenderer.SignModel signtileentityrenderer$signmodel = (SignTileEntityRenderer.SignModel)model;

            if (modelPart.equals("board"))
            {
                return (ModelRenderer)Reflector.ModelSign_ModelRenderers.getValue(signtileentityrenderer$signmodel, 0);
            }
            else
            {
                return modelPart.equals("stick") ? (ModelRenderer)Reflector.ModelSign_ModelRenderers.getValue(signtileentityrenderer$signmodel, 1) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"board", "stick"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.SIGN);

        if (!(tileentityrenderer instanceof SignTileEntityRenderer))
        {
            return null;
        }
        else
        {
            if (tileentityrenderer.getType() == null)
            {
                tileentityrenderer = new SignTileEntityRenderer(tileentityrendererdispatcher);
            }

            if (!Reflector.TileEntitySignRenderer_model.exists())
            {
                Config.warn("Field not found: TileEntitySignRenderer.model");
                return null;
            }
            else
            {
                Reflector.setFieldValue(tileentityrenderer, Reflector.TileEntitySignRenderer_model, modelBase);
                return tileentityrenderer;
            }
        }
    }
}
