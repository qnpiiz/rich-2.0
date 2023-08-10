package net.optifine.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;

public class ModelAdapterBanner extends ModelAdapter
{
    public ModelAdapterBanner()
    {
        super(TileEntityType.BANNER, "banner", 0.0F);
    }

    public Model makeModel()
    {
        return new BannerModel();
    }

    public ModelRenderer getModelRenderer(Model model, String modelPart)
    {
        if (!(model instanceof BannerModel))
        {
            return null;
        }
        else
        {
            BannerModel bannermodel = (BannerModel)model;

            if (modelPart.equals("slate"))
            {
                return bannermodel.bannerSlate;
            }
            else if (modelPart.equals("stand"))
            {
                return bannermodel.bannerStand;
            }
            else
            {
                return modelPart.equals("top") ? bannermodel.bannerTop : null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"slate", "stand", "top"};
    }

    public IEntityRenderer makeEntityRender(Model model, float shadowSize)
    {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityRenderer tileentityrenderer = tileentityrendererdispatcher.getRenderer(TileEntityType.BANNER);

        if (!(tileentityrenderer instanceof BannerTileEntityRenderer))
        {
            return null;
        }
        else
        {
            if (tileentityrenderer.getType() == null)
            {
                tileentityrenderer = new BannerTileEntityRenderer(tileentityrendererdispatcher);
            }

            if (!(model instanceof BannerModel))
            {
                Config.warn("Not a banner model: " + model);
                return null;
            }
            else
            {
                BannerModel bannermodel = (BannerModel)model;
                return bannermodel.updateRenderer(tileentityrenderer);
            }
        }
    }
}
