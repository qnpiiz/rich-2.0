package net.optifine.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.BellTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class BellModel extends Model
{
    public ModelRenderer bellBody;

    public BellModel()
    {
        super(RenderType::getEntityCutoutNoCull);
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        BellTileEntityRenderer belltileentityrenderer = new BellTileEntityRenderer(tileentityrendererdispatcher);
        this.bellBody = (ModelRenderer)Reflector.TileEntityBellRenderer_modelRenderer.getValue(belltileentityrenderer);
    }

    public TileEntityRenderer updateRenderer(TileEntityRenderer renderer)
    {
        if (!Reflector.TileEntityBellRenderer_modelRenderer.exists())
        {
            Config.warn("Field not found: TileEntityBellRenderer.modelRenderer");
            return null;
        }
        else
        {
            Reflector.TileEntityBellRenderer_modelRenderer.setValue(renderer, this.bellBody);
            return renderer;
        }
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
    }
}
