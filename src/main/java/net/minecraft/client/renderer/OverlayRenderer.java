package net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.optifine.Config;
import net.optifine.SmartAnimations;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.apache.commons.lang3.tuple.Pair;

public class OverlayRenderer
{
    private static final ResourceLocation TEXTURE_UNDERWATER = new ResourceLocation("textures/misc/underwater.png");

    public static void renderOverlays(Minecraft minecraftIn, MatrixStack matrixStackIn)
    {
        RenderSystem.disableAlphaTest();
        PlayerEntity playerentity = minecraftIn.player;

        if (!playerentity.noClip)
        {
            if (Reflector.ForgeEventFactory_renderBlockOverlay.exists() && Reflector.ForgeBlockModelShapes_getTexture3.exists())
            {
                Pair<BlockState, BlockPos> pair = getOverlayBlock(playerentity);

                if (pair != null)
                {
                    Object object = Reflector.getFieldValue(Reflector.RenderBlockOverlayEvent_OverlayType_BLOCK);

                    if (!Reflector.ForgeEventFactory_renderBlockOverlay.callBoolean(playerentity, matrixStackIn, object, pair.getLeft(), pair.getRight()))
                    {
                        TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)Reflector.call(minecraftIn.getBlockRendererDispatcher().getBlockModelShapes(), Reflector.ForgeBlockModelShapes_getTexture3, pair.getLeft(), minecraftIn.world, pair.getRight());
                        renderTexture(minecraftIn, textureatlassprite, matrixStackIn);
                    }
                }
            }
            else
            {
                BlockState blockstate = getViewBlockingState(playerentity);

                if (blockstate != null)
                {
                    renderTexture(minecraftIn, minecraftIn.getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockstate), matrixStackIn);
                }
            }
        }

        if (!minecraftIn.player.isSpectator())
        {
            if (minecraftIn.player.areEyesInFluid(FluidTags.WATER) && !Reflector.ForgeEventFactory_renderWaterOverlay.callBoolean(playerentity, matrixStackIn))
            {
                renderUnderwater(minecraftIn, matrixStackIn);
            }

            if (minecraftIn.player.isBurning() && !Reflector.ForgeEventFactory_renderFireOverlay.callBoolean(playerentity, matrixStackIn))
            {
                renderFire(minecraftIn, matrixStackIn);
            }
        }

        RenderSystem.enableAlphaTest();
    }

    @Nullable
    private static BlockState getViewBlockingState(PlayerEntity playerIn)
    {
        Pair<BlockState, BlockPos> pair = getOverlayBlock(playerIn);
        return pair == null ? null : pair.getLeft();
    }

    private static Pair<BlockState, BlockPos> getOverlayBlock(PlayerEntity p_getOverlayBlock_0_)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int i = 0; i < 8; ++i)
        {
            double d0 = p_getOverlayBlock_0_.getPosX() + (double)(((float)((i >> 0) % 2) - 0.5F) * p_getOverlayBlock_0_.getWidth() * 0.8F);
            double d1 = p_getOverlayBlock_0_.getPosYEye() + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
            double d2 = p_getOverlayBlock_0_.getPosZ() + (double)(((float)((i >> 2) % 2) - 0.5F) * p_getOverlayBlock_0_.getWidth() * 0.8F);
            blockpos$mutable.setPos(d0, d1, d2);
            BlockState blockstate = p_getOverlayBlock_0_.world.getBlockState(blockpos$mutable);

            if (blockstate.getRenderType() != BlockRenderType.INVISIBLE && blockstate.causesSuffocation(p_getOverlayBlock_0_.world, blockpos$mutable))
            {
                return Pair.of(blockstate, blockpos$mutable.toImmutable());
            }
        }

        return null;
    }

    private static void renderTexture(Minecraft minecraftIn, TextureAtlasSprite spriteIn, MatrixStack matrixStackIn)
    {
        if (SmartAnimations.isActive())
        {
            SmartAnimations.spriteRendered(spriteIn);
        }

        minecraftIn.getTextureManager().bindTexture(spriteIn.getAtlasTexture().getTextureLocation());
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        float f = 0.1F;
        float f1 = -1.0F;
        float f2 = 1.0F;
        float f3 = -1.0F;
        float f4 = 1.0F;
        float f5 = -0.5F;
        float f6 = spriteIn.getMinU();
        float f7 = spriteIn.getMaxU();
        float f8 = spriteIn.getMinV();
        float f9 = spriteIn.getMaxV();
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        bufferbuilder.pos(matrix4f, -1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).tex(f7, f9).endVertex();
        bufferbuilder.pos(matrix4f, 1.0F, -1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).tex(f6, f9).endVertex();
        bufferbuilder.pos(matrix4f, 1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).tex(f6, f8).endVertex();
        bufferbuilder.pos(matrix4f, -1.0F, 1.0F, -0.5F).color(0.1F, 0.1F, 0.1F, 1.0F).tex(f7, f8).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }

    private static void renderUnderwater(Minecraft minecraftIn, MatrixStack matrixStackIn)
    {
        if (!Config.isShaders() || Shaders.isUnderwaterOverlay())
        {
            RenderSystem.enableTexture();
            minecraftIn.getTextureManager().bindTexture(TEXTURE_UNDERWATER);

            if (SmartAnimations.isActive())
            {
                SmartAnimations.textureRendered(minecraftIn.getTextureManager().getTexture(TEXTURE_UNDERWATER).getGlTextureId());
            }

            BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
            float f = minecraftIn.player.getBrightness();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            float f1 = 4.0F;
            float f2 = -1.0F;
            float f3 = 1.0F;
            float f4 = -1.0F;
            float f5 = 1.0F;
            float f6 = -0.5F;
            float f7 = -minecraftIn.player.rotationYaw / 64.0F;
            float f8 = minecraftIn.player.rotationPitch / 64.0F;
            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
            bufferbuilder.pos(matrix4f, -1.0F, -1.0F, -0.5F).color(f, f, f, 0.1F).tex(4.0F + f7, 4.0F + f8).endVertex();
            bufferbuilder.pos(matrix4f, 1.0F, -1.0F, -0.5F).color(f, f, f, 0.1F).tex(0.0F + f7, 4.0F + f8).endVertex();
            bufferbuilder.pos(matrix4f, 1.0F, 1.0F, -0.5F).color(f, f, f, 0.1F).tex(0.0F + f7, 0.0F + f8).endVertex();
            bufferbuilder.pos(matrix4f, -1.0F, 1.0F, -0.5F).color(f, f, f, 0.1F).tex(4.0F + f7, 0.0F + f8).endVertex();
            bufferbuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferbuilder);
            RenderSystem.disableBlend();
        }
    }

    private static void renderFire(Minecraft minecraftIn, MatrixStack matrixStackIn)
    {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();
        TextureAtlasSprite textureatlassprite = ModelBakery.LOCATION_FIRE_1.getSprite();

        if (SmartAnimations.isActive())
        {
            SmartAnimations.spriteRendered(textureatlassprite);
        }

        minecraftIn.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
        float f = textureatlassprite.getMinU();
        float f1 = textureatlassprite.getMaxU();
        float f2 = (f + f1) / 2.0F;
        float f3 = textureatlassprite.getMinV();
        float f4 = textureatlassprite.getMaxV();
        float f5 = (f3 + f4) / 2.0F;
        float f6 = textureatlassprite.getUvShrinkRatio();
        float f7 = MathHelper.lerp(f6, f, f2);
        float f8 = MathHelper.lerp(f6, f1, f2);
        float f9 = MathHelper.lerp(f6, f3, f5);
        float f10 = MathHelper.lerp(f6, f4, f5);
        float f11 = 1.0F;

        for (int i = 0; i < 2; ++i)
        {
            matrixStackIn.push();
            float f12 = -0.5F;
            float f13 = 0.5F;
            float f14 = -0.5F;
            float f15 = 0.5F;
            float f16 = -0.5F;
            matrixStackIn.translate((double)((float)(-(i * 2 - 1)) * 0.24F), (double) - 0.3F, 0.0D);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float)(i * 2 - 1) * 10.0F));
            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
            bufferbuilder.pos(matrix4f, -0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).tex(f8, f10).endVertex();
            bufferbuilder.pos(matrix4f, 0.5F, -0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).tex(f7, f10).endVertex();
            bufferbuilder.pos(matrix4f, 0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).tex(f7, f9).endVertex();
            bufferbuilder.pos(matrix4f, -0.5F, 0.5F, -0.5F).color(1.0F, 1.0F, 1.0F, 0.9F).tex(f8, f9).endVertex();
            bufferbuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferbuilder);
            matrixStackIn.pop();
        }

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    }
}
