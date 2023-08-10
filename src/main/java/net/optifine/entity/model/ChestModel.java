package net.optifine.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ChestModel extends Model
{
    public ModelRenderer lid;
    public ModelRenderer base;
    public ModelRenderer knob;

    public ChestModel()
    {
        super(RenderType::getEntityCutout);
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        ChestTileEntityRenderer chesttileentityrenderer = new ChestTileEntityRenderer(tileentityrendererdispatcher);
        this.lid = (ModelRenderer)Reflector.TileEntityChestRenderer_modelRenderers.getValue(chesttileentityrenderer, 0);
        this.base = (ModelRenderer)Reflector.TileEntityChestRenderer_modelRenderers.getValue(chesttileentityrenderer, 1);
        this.knob = (ModelRenderer)Reflector.TileEntityChestRenderer_modelRenderers.getValue(chesttileentityrenderer, 2);
    }

    public TileEntityRenderer updateRenderer(TileEntityRenderer renderer)
    {
        if (!Reflector.TileEntityChestRenderer_modelRenderers.exists())
        {
            Config.warn("Field not found: TileEntityChestRenderer.modelRenderers");
            return null;
        }
        else
        {
            Reflector.TileEntityChestRenderer_modelRenderers.setValue(renderer, 0, this.lid);
            Reflector.TileEntityChestRenderer_modelRenderers.setValue(renderer, 1, this.base);
            Reflector.TileEntityChestRenderer_modelRenderers.setValue(renderer, 2, this.knob);
            return renderer;
        }
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
    }
}
