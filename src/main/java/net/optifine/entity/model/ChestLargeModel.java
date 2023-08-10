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

public class ChestLargeModel extends Model
{
    public ModelRenderer lid_left;
    public ModelRenderer base_left;
    public ModelRenderer knob_left;
    public ModelRenderer lid_right;
    public ModelRenderer base_right;
    public ModelRenderer knob_right;

    public ChestLargeModel()
    {
        super(RenderType::getEntityCutout);
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        ChestTileEntityRenderer chesttileentityrenderer = new ChestTileEntityRenderer(tileentityrendererdispatcher);
        this.lid_left = (ModelRenderer)Reflector.TileEntityChestRenderer_modelRenderers.getValue(chesttileentityrenderer, 3);
        this.base_left = (ModelRenderer)Reflector.TileEntityChestRenderer_modelRenderers.getValue(chesttileentityrenderer, 4);
        this.knob_left = (ModelRenderer)Reflector.TileEntityChestRenderer_modelRenderers.getValue(chesttileentityrenderer, 5);
        this.lid_right = (ModelRenderer)Reflector.TileEntityChestRenderer_modelRenderers.getValue(chesttileentityrenderer, 6);
        this.base_right = (ModelRenderer)Reflector.TileEntityChestRenderer_modelRenderers.getValue(chesttileentityrenderer, 7);
        this.knob_right = (ModelRenderer)Reflector.TileEntityChestRenderer_modelRenderers.getValue(chesttileentityrenderer, 8);
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
            Reflector.TileEntityChestRenderer_modelRenderers.setValue(renderer, 3, this.lid_left);
            Reflector.TileEntityChestRenderer_modelRenderers.setValue(renderer, 4, this.base_left);
            Reflector.TileEntityChestRenderer_modelRenderers.setValue(renderer, 5, this.knob_left);
            Reflector.TileEntityChestRenderer_modelRenderers.setValue(renderer, 6, this.lid_right);
            Reflector.TileEntityChestRenderer_modelRenderers.setValue(renderer, 7, this.base_right);
            Reflector.TileEntityChestRenderer_modelRenderers.setValue(renderer, 8, this.knob_right);
            return renderer;
        }
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
    }
}
