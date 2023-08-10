package net.optifine.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;

public class GuiUtils
{
    public static int getWidth(Widget widget)
    {
        return OptionSlider.getWidth(widget);
    }

    public static int getHeight(Widget widget)
    {
        return OptionSlider.getHeight(widget);
    }

    public static void fill(Matrix4f matrixIn, GuiRect[] rects, int color)
    {
        float f = (float)(color >> 24 & 255) / 255.0F;
        float f1 = (float)(color >> 16 & 255) / 255.0F;
        float f2 = (float)(color >> 8 & 255) / 255.0F;
        float f3 = (float)(color & 255) / 255.0F;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

        for (int i = 0; i < rects.length; ++i)
        {
            GuiRect guirect = rects[i];

            if (guirect != null)
            {
                int j = guirect.getLeft();
                int k = guirect.getTop();
                int l = guirect.getRight();
                int i1 = guirect.getBottom();

                if (j < l)
                {
                    int j1 = j;
                    j = l;
                    l = j1;
                }

                if (k < i1)
                {
                    int k1 = k;
                    k = i1;
                    i1 = k1;
                }

                bufferbuilder.pos(matrixIn, (float)j, (float)i1, 0.0F).color(f1, f2, f3, f).endVertex();
                bufferbuilder.pos(matrixIn, (float)l, (float)i1, 0.0F).color(f1, f2, f3, f).endVertex();
                bufferbuilder.pos(matrixIn, (float)l, (float)k, 0.0F).color(f1, f2, f3, f).endVertex();
                bufferbuilder.pos(matrixIn, (float)j, (float)k, 0.0F).color(f1, f2, f3, f).endVertex();
            }
        }

        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
