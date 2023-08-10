package net.optifine.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EnderCrystalRenderer;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class EnderCrystalModel extends Model
{
    public ModelRenderer cube;
    public ModelRenderer glass;
    public ModelRenderer base;

    public EnderCrystalModel()
    {
        super(RenderType::getEntityCutoutNoCull);
        EnderCrystalRenderer endercrystalrenderer = new EnderCrystalRenderer(Minecraft.getInstance().getRenderManager());
        this.cube = (ModelRenderer)Reflector.RenderEnderCrystal_modelRenderers.getValue(endercrystalrenderer, 0);
        this.glass = (ModelRenderer)Reflector.RenderEnderCrystal_modelRenderers.getValue(endercrystalrenderer, 1);
        this.base = (ModelRenderer)Reflector.RenderEnderCrystal_modelRenderers.getValue(endercrystalrenderer, 2);
    }

    public EnderCrystalRenderer updateRenderer(EnderCrystalRenderer render)
    {
        if (!Reflector.RenderEnderCrystal_modelRenderers.exists())
        {
            Config.warn("Field not found: RenderEnderCrystal.modelEnderCrystal");
            return null;
        }
        else
        {
            Reflector.RenderEnderCrystal_modelRenderers.setValue(render, 0, this.cube);
            Reflector.RenderEnderCrystal_modelRenderers.setValue(render, 1, this.glass);
            Reflector.RenderEnderCrystal_modelRenderers.setValue(render, 2, this.base);
            return render;
        }
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
    }
}
