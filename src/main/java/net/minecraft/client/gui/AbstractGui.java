package net.minecraft.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.BiConsumer;

import lombok.Getter;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;

@Getter
public abstract class AbstractGui
{
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/options_background.png");
    public static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
    private int blitOffset;

    protected void hLine(MatrixStack matrixStack, int minX, int maxX, int y, int color)
    {
        if (maxX < minX)
        {
            int i = minX;
            minX = maxX;
            maxX = i;
        }

        fill(matrixStack, minX, y, maxX + 1, y + 1, color);
    }

    protected void vLine(MatrixStack matrixStack, int x, int minY, int maxY, int color)
    {
        if (maxY < minY)
        {
            int i = minY;
            minY = maxY;
            maxY = i;
        }

        fill(matrixStack, x, minY + 1, x + 1, maxY, color);
    }

    public static void fill(MatrixStack matrixStack, float minX, float minY, float maxX, float maxY, int color)
    {
        fill(matrixStack.getLast().getMatrix(), minX, minY, maxX, maxY, color);
    }

    public static void fill(Matrix4f matrix, float minX, float minY, float maxX, float maxY, int color)
    {
        if (minX < maxX)
        {
            float i = minX;
            minX = maxX;
            maxX = i;
        }

        if (minY < maxY)
        {
            float j = minY;
            minY = maxY;
            maxY = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(matrix, (float)minX, (float)maxY, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(matrix, (float)maxX, (float)maxY, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(matrix, (float)maxX, (float)minY, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(matrix, (float)minX, (float)minY, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    protected void fillGradient(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo)
    {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        fillGradient(matrixStack.getLast().getMatrix(), bufferbuilder, x1, y1, x2, y2, this.blitOffset, colorFrom, colorTo);
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    protected static void fillGradient(Matrix4f matrix, BufferBuilder builder, int x1, int y1, int x2, int y2, int z, int colorA, int colorB)
    {
        float f = (float)(colorA >> 24 & 255) / 255.0F;
        float f1 = (float)(colorA >> 16 & 255) / 255.0F;
        float f2 = (float)(colorA >> 8 & 255) / 255.0F;
        float f3 = (float)(colorA & 255) / 255.0F;
        float f4 = (float)(colorB >> 24 & 255) / 255.0F;
        float f5 = (float)(colorB >> 16 & 255) / 255.0F;
        float f6 = (float)(colorB >> 8 & 255) / 255.0F;
        float f7 = (float)(colorB & 255) / 255.0F;
        builder.pos(matrix, (float)x2, (float)y1, (float)z).color(f1, f2, f3, f).endVertex();
        builder.pos(matrix, (float)x1, (float)y1, (float)z).color(f1, f2, f3, f).endVertex();
        builder.pos(matrix, (float)x1, (float)y2, (float)z).color(f5, f6, f7, f4).endVertex();
        builder.pos(matrix, (float)x2, (float)y2, (float)z).color(f5, f6, f7, f4).endVertex();
    }

    public static void drawCenteredString(MatrixStack matrixStack, FontRenderer fontRenderer, String font, int text, int x, int y)
    {
        fontRenderer.drawStringWithShadow(matrixStack, font, (float)(text - fontRenderer.getStringWidth(font) / 2), (float)x, y);
    }

    public static void drawCenteredString(MatrixStack matrixStack, FontRenderer fontRenderer, ITextComponent font, int text, int x, int y)
    {
        IReorderingProcessor ireorderingprocessor = font.func_241878_f();
        fontRenderer.func_238407_a_(matrixStack, ireorderingprocessor, (float)(text - fontRenderer.func_243245_a(ireorderingprocessor) / 2), (float)x, y);
    }

    public static void drawString(MatrixStack matrixStack, FontRenderer fontRenderer, String font, int text, int x, int y)
    {
        fontRenderer.drawStringWithShadow(matrixStack, font, (float)text, (float)x, y);
    }

    public static void drawString(MatrixStack matrixStack, FontRenderer fontRenderer, ITextComponent font, int text, int x, int y)
    {
        fontRenderer.func_243246_a(matrixStack, font, (float)text, (float)x, y);
    }

    public void blitBlackOutline(int width, int height, BiConsumer<Integer, Integer> boxXYConsumer)
    {
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        boxXYConsumer.accept(width + 1, height);
        boxXYConsumer.accept(width - 1, height);
        boxXYConsumer.accept(width, height + 1);
        boxXYConsumer.accept(width, height - 1);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        boxXYConsumer.accept(width, height);
    }

    public static void blit(MatrixStack matrixStack, int x, int y, int blitOffset, int width, int height, TextureAtlasSprite sprite)
    {
        innerBlit(matrixStack.getLast().getMatrix(), x, x + width, y, y + height, blitOffset, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    public void blit(MatrixStack matrixStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        blit(matrixStack, x, y, this.blitOffset, (float)uOffset, (float)vOffset, uWidth, vHeight, 256, 256);
    }

    public static void blit(MatrixStack matrixStack, int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureHeight, int textureWidth)
    {
        innerBlit(matrixStack, x, x + uWidth, y, y + vHeight, blitOffset, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    public static void blit(MatrixStack matrixStack, int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight)
    {
        innerBlit(matrixStack, x, x + width, y, y + height, 0, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    public static void blit(MatrixStack matrixStack, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight)
    {
        blit(matrixStack, x, y, width, height, uOffset, vOffset, width, height, textureWidth, textureHeight);
    }

    private static void innerBlit(MatrixStack matrixStack, int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight)
    {
        innerBlit(matrixStack.getLast().getMatrix(), x1, x2, y1, y2, blitOffset, (uOffset + 0.0F) / (float)textureWidth, (uOffset + (float)uWidth) / (float)textureWidth, (vOffset + 0.0F) / (float)textureHeight, (vOffset + (float)vHeight) / (float)textureHeight);
    }

    private static void innerBlit(Matrix4f matrix, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV)
    {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(matrix, (float)x1, (float)y2, (float)blitOffset).tex(minU, maxV).endVertex();
        bufferbuilder.pos(matrix, (float)x2, (float)y2, (float)blitOffset).tex(maxU, maxV).endVertex();
        bufferbuilder.pos(matrix, (float)x2, (float)y1, (float)blitOffset).tex(maxU, minV).endVertex();
        bufferbuilder.pos(matrix, (float)x1, (float)y1, (float)blitOffset).tex(minU, minV).endVertex();
        bufferbuilder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(bufferbuilder);
    }

    public void setBlitOffset(int value)
    {
        this.blitOffset = value;
    }
}
