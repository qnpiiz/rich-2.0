package net.optifine.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ConduitTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ConduitModel extends Model
{
    public ModelRenderer eye;
    public ModelRenderer wind;
    public ModelRenderer base;
    public ModelRenderer cage;

    public ConduitModel()
    {
        super(RenderType::getEntityCutout);
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        ConduitTileEntityRenderer conduittileentityrenderer = new ConduitTileEntityRenderer(tileentityrendererdispatcher);
        this.eye = (ModelRenderer)Reflector.TileEntityConduitRenderer_modelRenderers.getValue(conduittileentityrenderer, 0);
        this.wind = (ModelRenderer)Reflector.TileEntityConduitRenderer_modelRenderers.getValue(conduittileentityrenderer, 1);
        this.base = (ModelRenderer)Reflector.TileEntityConduitRenderer_modelRenderers.getValue(conduittileentityrenderer, 2);
        this.cage = (ModelRenderer)Reflector.TileEntityConduitRenderer_modelRenderers.getValue(conduittileentityrenderer, 3);
    }

    public TileEntityRenderer updateRenderer(TileEntityRenderer renderer)
    {
        if (!Reflector.TileEntityConduitRenderer_modelRenderers.exists())
        {
            Config.warn("Field not found: TileEntityConduitRenderer.modelRenderers");
            return null;
        }
        else
        {
            Reflector.TileEntityConduitRenderer_modelRenderers.setValue(renderer, 0, this.eye);
            Reflector.TileEntityConduitRenderer_modelRenderers.setValue(renderer, 1, this.wind);
            Reflector.TileEntityConduitRenderer_modelRenderers.setValue(renderer, 2, this.base);
            Reflector.TileEntityConduitRenderer_modelRenderers.setValue(renderer, 3, this.cage);
            return renderer;
        }
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
    }
}
