package net.optifine.entity.model;

import java.util.Map;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterHeadSkeleton extends ModelAdapter
{
    public ModelAdapterHeadSkeleton()
    {
        super(TileEntityType.SKULL, "head_skeleton", 0.0F);
    }

    public Model makeModel()
    {
        return new GenericHeadModel(0, 0, 64, 32);
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof GenericHeadModel))
        {
            return null;
        }
        else
        {
            GenericHeadModel genericheadmodel = (GenericHeadModel)model;
            return modelPart.equals("head") ? (ModelRenderer)Reflector.ModelGenericHead_skeletonHead.getValue(genericheadmodel) : null;
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head"};
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
                map.put(SkullBlock.Types.SKELETON, modelBase);
                return tileentityrenderer;
            }
        }
    }
}
