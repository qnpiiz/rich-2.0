package net.optifine.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.awt.Dimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;

public class PlayerItemModel
{
    private Dimension textureSize = null;
    private boolean usePlayerTexture = false;
    private PlayerItemRenderer[] modelRenderers = new PlayerItemRenderer[0];
    private ResourceLocation textureLocation = null;
    private NativeImage textureImage = null;
    private DynamicTexture texture = null;
    private ResourceLocation locationMissing = new ResourceLocation("textures/block/red_wool.png");
    public static final int ATTACH_BODY = 0;
    public static final int ATTACH_HEAD = 1;
    public static final int ATTACH_LEFT_ARM = 2;
    public static final int ATTACH_RIGHT_ARM = 3;
    public static final int ATTACH_LEFT_LEG = 4;
    public static final int ATTACH_RIGHT_LEG = 5;
    public static final int ATTACH_CAPE = 6;

    public PlayerItemModel(Dimension textureSize, boolean usePlayerTexture, PlayerItemRenderer[] modelRenderers)
    {
        this.textureSize = textureSize;
        this.usePlayerTexture = usePlayerTexture;
        this.modelRenderers = modelRenderers;
    }

    public void render(BipedModel modelBiped, AbstractClientPlayerEntity player, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, int packedOverlayIn)
    {
        ResourceLocation resourcelocation = this.locationMissing;

        if (this.usePlayerTexture)
        {
            resourcelocation = player.getLocationSkin();
        }
        else if (this.textureLocation != null)
        {
            if (this.texture == null && this.textureImage != null)
            {
                this.texture = new DynamicTexture(this.textureImage);
                Minecraft.getInstance().getTextureManager().loadTexture(this.textureLocation, this.texture);
            }

            resourcelocation = this.textureLocation;
        }
        else
        {
            resourcelocation = this.locationMissing;
        }

        for (int i = 0; i < this.modelRenderers.length; ++i)
        {
            PlayerItemRenderer playeritemrenderer = this.modelRenderers[i];
            matrixStackIn.push();
            RenderType rendertype = RenderType.getEntityCutoutNoCull(resourcelocation);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
            playeritemrenderer.render(modelBiped, matrixStackIn, ivertexbuilder, packedLightIn, packedOverlayIn);
            matrixStackIn.pop();
        }
    }

    public static ModelRenderer getAttachModel(BipedModel modelBiped, int attachTo)
    {
        switch (attachTo)
        {
            case 0:
                return modelBiped.bipedBody;

            case 1:
                return modelBiped.bipedHead;

            case 2:
                return modelBiped.bipedLeftArm;

            case 3:
                return modelBiped.bipedRightArm;

            case 4:
                return modelBiped.bipedLeftLeg;

            case 5:
                return modelBiped.bipedRightLeg;

            default:
                return null;
        }
    }

    public NativeImage getTextureImage()
    {
        return this.textureImage;
    }

    public void setTextureImage(NativeImage textureImage)
    {
        this.textureImage = textureImage;
    }

    public DynamicTexture getTexture()
    {
        return this.texture;
    }

    public ResourceLocation getTextureLocation()
    {
        return this.textureLocation;
    }

    public void setTextureLocation(ResourceLocation textureLocation)
    {
        this.textureLocation = textureLocation;
    }

    public boolean isUsePlayerTexture()
    {
        return this.usePlayerTexture;
    }
}
