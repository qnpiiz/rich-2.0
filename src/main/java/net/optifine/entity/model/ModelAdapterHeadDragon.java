package net.optifine.entity.model;

import java.util.Map;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.model.DragonHeadModel;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterHeadDragon extends ModelAdapter
{
    public ModelAdapterHeadDragon()
    {
        super(TileEntityType.SKULL, "head_dragon", 0.0F);
    }

    public Model makeModel()
    {
        return new DragonHeadModel(0.0F);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof DragonHeadModel))
        {
            return null;
        }
        else
        {
            DragonHeadModel dragonheadmodel = (DragonHeadModel)model;

            if (modelPart.equals("head"))
            {
                return (ModelRenderer)Reflector.getFieldValue(dragonheadmodel, Reflector.ModelDragonHead_head);
            }
            else
            {
                return modelPart.equals("jaw") ? (ModelRenderer)Reflector.getFieldValue(dragonheadmodel, Reflector.ModelDragonHead_jaw) : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "jaw"};
    }

    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize)
    {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.SKULL);

        if (!(tileentityrenderer instanceof SkullTileEntityRenderer))
        {
            return null;
        }
        else
        {
            if (tileentityrenderer.getType() == null)
            {
                tileentityrenderer = new SkullTileEntityRenderer(tileentityrendererdispatcher);
            }

            Map<SkullBlock.ISkullType, Model> map = (Map)Reflector.TileEntitySkullRenderer_MODELS.getValue();

            if (map == null)
            {
                Config.warn("Field not found: TileEntitySkullRenderer.MODELS");
                return null;
            }
            else
            {
                map.put(SkullBlock.Types.DRAGON, modelBase);
                return tileentityrenderer;
            }
        }
    }
}
