package net.optifine.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class BannerModel extends Model
{
    public ModelRenderer bannerSlate;
    public ModelRenderer bannerStand;
    public ModelRenderer bannerTop;

    public BannerModel()
    {
        super(RenderType::getEntityCutoutNoCull);
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        BannerTileEntityRenderer bannertileentityrenderer = new BannerTileEntityRenderer(tileentityrendererdispatcher);
        this.bannerSlate = (ModelRenderer)Reflector.TileEntityBannerRenderer_modelRenderers.getValue(bannertileentityrenderer, 0);
        this.bannerStand = (ModelRenderer)Reflector.TileEntityBannerRenderer_modelRenderers.getValue(bannertileentityrenderer, 1);
        this.bannerTop = (ModelRenderer)Reflector.TileEntityBannerRenderer_modelRenderers.getValue(bannertileentityrenderer, 2);
    }

    public TileEntityRenderer updateRenderer(TileEntityRenderer renderer)
    {
        if (!Reflector.TileEntityBannerRenderer_modelRenderers.exists())
        {
            Config.warn("Field not found: TileEntityBannerRenderer.modelRenderers");
            return null;
        }
        else
        {
            Reflector.TileEntityBannerRenderer_modelRenderers.setValue(renderer, 0, this.bannerSlate);
            Reflector.TileEntityBannerRenderer_modelRenderers.setValue(renderer, 1, this.bannerStand);
            Reflector.TileEntityBannerRenderer_modelRenderers.setValue(renderer, 2, this.bannerTop);
            return renderer;
        }
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
    }
}
