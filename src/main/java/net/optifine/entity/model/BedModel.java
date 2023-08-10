package net.optifine.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.BedTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class BedModel extends Model
{
    public ModelRenderer headPiece;
    public ModelRenderer footPiece;
    public ModelRenderer[] legs = new ModelRenderer[4];

    public BedModel()
    {
        super(RenderType::getEntityCutoutNoCull);
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        BedTileEntityRenderer bedtileentityrenderer = new BedTileEntityRenderer(tileentityrendererdispatcher);
        this.headPiece = (ModelRenderer)Reflector.TileEntityBedRenderer_headModel.getValue(bedtileentityrenderer);
        this.footPiece = (ModelRenderer)Reflector.TileEntityBedRenderer_footModel.getValue(bedtileentityrenderer);
        this.legs = (ModelRenderer[])Reflector.TileEntityBedRenderer_legModels.getValue(bedtileentityrenderer);
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
    }

    public TileEntityRenderer updateRenderer(TileEntityRenderer renderer)
    {
        if (!Reflector.TileEntityBedRenderer_headModel.exists())
        {
            Config.warn("Field not found: TileEntityBedRenderer.head");
            return null;
        }
        else if (!Reflector.TileEntityBedRenderer_footModel.exists())
        {
            Config.warn("Field not found: TileEntityBedRenderer.footModel");
            return null;
        }
        else if (!Reflector.TileEntityBedRenderer_legModels.exists())
        {
            Config.warn("Field not found: TileEntityBedRenderer.legModels");
            return null;
        }
        else
        {
            Reflector.setFieldValue(renderer, Reflector.TileEntityBedRenderer_headModel, this.headPiece);
            Reflector.setFieldValue(renderer, Reflector.TileEntityBedRenderer_footModel, this.footPiece);
            Reflector.setFieldValue(renderer, Reflector.TileEntityBedRenderer_legModels, this.legs);
            return renderer;
        }
    }
}
