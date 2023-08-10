package fun.rich.utils.render;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.BufferedImageTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import fun.rich.utils.jhlabs.GaussianFilter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtils {

    /**
     * Рисует вертикальную линию ввиде тени
     *
     * @param lineWidth Жирность линии
     * @param startAlpha Прозрачность начала линии
     * @param size Длина линии
     * @param posX Ось X начала линии
     * @param posY1 Ось Y начала линии
     * @param right Направление линии (true - направо, false - налево)
     * @param edges Следует ли рисовать границы
     * @param red Значение красного цвета [0-1]
     * @param green Значение зелёного цвета [0-1]
     * @param blue Значение синего цвета [0-1]
     * @param matrixStack Стек с матрицой для новой системы отрисовки
     */
    public static void renderShadowVertical(float lineWidth, double startAlpha, int size, double posX, double posY1, boolean right, boolean edges, float red, float green, float blue, MatrixStack matrixStack) {
        renderShadowVertical(lineWidth, startAlpha, size, posX, posY1, 0, right, edges, red, green, blue, matrixStack);
    }

    /**
     * Рисует вертикальную линию ввиде тени
     *
     * @param lineWidth Жирность линии
     * @param startAlpha Прозрачность начала линии
     * @param size Длина линии
     * @param posX Ось X начала линии
     * @param posY1 Ось Y начала линии
     * @param posY2 Видимо оригинальный создатель забыл убрать аргумент
     * @param right Направление линии (true - направо, false - налево)
     * @param edges Следует ли рисовать границы
     * @param red Значение красного цвета [0-1]
     * @param green Значение зелёного цвета [0-1]
     * @param blue Значение синего цвета [0-1]
     * @param matrixStack Стек с матрицой для новой системы отрисовки
     */
    public static void renderShadowVertical(float lineWidth, double startAlpha, int size, double posX, double posY1, double posY2, boolean right, boolean edges, float red, float green, float blue, MatrixStack matrixStack) {
        start2Draw(() -> {
            double alpha = startAlpha;
            glAlphaFunc(GL_GREATER, 0.0f);
            glLineWidth(lineWidth);

            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            if (right) {
                for (double x = 0.5; x < (double) size; x += 0.5) {
                    bufferBuilder.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    bufferBuilder.pos(matrixStack, posX + x, posY1 - (edges ? x : 0.0)).color(red, green, blue, (float) alpha).endVertex();
                    bufferBuilder.pos(matrixStack, posX + x, posY2 + (edges ? x : 0.0)).color(red, green, blue, (float) alpha).endVertex();
                    bufferBuilder.finishDrawing();
                    WorldVertexBufferUploader.draw(bufferBuilder);

                    alpha = startAlpha - x / (double) size;
                }
            } else {
                for (double x = 0.5; x < (double) size; x += 0.5) {
                    bufferBuilder.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    bufferBuilder.pos(matrixStack, posX - x, posY1 - (edges ? x : 0.0)).color(red, green, blue, (float) alpha).endVertex();
                    bufferBuilder.pos(matrixStack, posX - x, posY2 + (edges ? x : 0.0)).color(red, green, blue, (float) alpha).endVertex();
                    bufferBuilder.finishDrawing();
                    WorldVertexBufferUploader.draw(bufferBuilder);

                    alpha = startAlpha - x / (double) size;
                }
            }
        });
    }

    /* Map которая содержит hashCode цвет+радиус и ID текстуры */
    private static final HashMap<Integer, Integer> shadowCache = Maps.newLinkedHashMap();

    /**
     * Рисует свечение используя gaussian blur
     *
     * @param x Ось X прямоугольника
     * @param y Ось Y прямоугольника
     * @param width Длина прямоугольника
     * @param height Ширина прямоугольника
     * @param blurRadius Радиус свечения
     * @param color Цвет
     */
    public static void drawBlurredShadow(float x, float y, float width, float height, int blurRadius, Color color) {
        glPushMatrix();
        glAlphaFunc(GL11.GL_GREATER, 0.01f);

        width = width + blurRadius * 2;
        height = height + blurRadius * 2;
        x = x - blurRadius;
        y = y - blurRadius;

        float _X = x - 0.25f;
        float _Y = y + 0.25f;

        int identifier = (int) (width * height + width + color.hashCode() + color.getAlpha() * blurRadius + blurRadius);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        RenderSystem.enableBlend();

        int texId = -1;
        if (shadowCache.containsKey(identifier)) {
            texId = shadowCache.get(identifier);

            bindTexture(texId);
        } else {
            if (width <= 0) width = 1;
            if (height <= 0) height = 1;
            BufferedImage original = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB_PRE);

            Graphics g = original.getGraphics();
            g.setColor(color);
            g.fillRect(blurRadius, blurRadius, (int) (width - blurRadius * 2), (int) (height - blurRadius * 2));
            g.dispose();

            /* GaussianFilter с оптимизацией вендовского из библиотеки jhlabs */
            GaussianFilter op = new GaussianFilter(blurRadius);
            BufferedImage blurred = op.filter(original, null);
            texId = BufferedImageTexture.uploadTextureImageAllocate(TextureUtil.generateTextureId(), blurred, true, false);

            shadowCache.put(identifier, texId);
        }

        GL11.glColor4f(1f, 1f, 1f, 1f);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0); // top left
        GL11.glVertex2f(_X, _Y);

        GL11.glTexCoord2f(0, 1); // bottom left
        GL11.glVertex2f(_X, _Y + height);

        GL11.glTexCoord2f(1, 1); // bottom right
        GL11.glVertex2f(_X + width, _Y + height);

        GL11.glTexCoord2f(1, 0); // top right
        GL11.glVertex2f(_X + width, _Y);
        GL11.glEnd();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        setColor(-1);

        glEnable(GL_CULL_FACE);
        glPopMatrix();
    }

    /**
     * Отрисосывает прямоугольник с 4 цветами ввиде градиента используя прямоугольную систему координат
     *
     * @param left Координата левой границы прямоугольника
     * @param top Координата верхней границы прямоугольника
     * @param right Координата правой границы прямоугольника
     * @param bottom Координата нижней границы прямоугольника
     * @param color1 Цвет сверху слева
     * @param color2 Цвет снизу слева
     * @param color3 Цвет снизу справа
     * @param color4 Цвет сверху справа
     * @param matrixStack Стек с матрицой для новой системы отрисовки
     */
    public static void drawColorRect(double left, double top, double right, double bottom, Color color1, Color color2, Color color3, Color color4, MatrixStack matrixStack) {
        start2Draw(() -> {
            glShadeModel(GL_SMOOTH);
            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            bufferBuilder.pos(matrixStack, left, bottom).color(color2).endVertex();
            bufferBuilder.pos(matrixStack, right, bottom).color(color3).endVertex();
            bufferBuilder.pos(matrixStack, right, top).color(color4).endVertex();
            bufferBuilder.pos(matrixStack, left, top).color(color1).endVertex();
            bufferBuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferBuilder);

            glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
            glDisable(GL_LINE_SMOOTH);
            glShadeModel(GL_FLAT);
        });
    }

    /**
     * Рисует вертикальную линию ввиде тени
     *
     * @param color Цвет линии
     * @param lineWidth Жирность линии
     * @param startAlpha Прозрачность начала линии
     * @param size Длина линии
     * @param posX Ось X начала линии
     * @param posY1 Ось Y начала линии
     * @param right Направление линии (true - направо, false - налево)
     * @param edges Следует ли рисовать границы
     * @param matrixStack Стек с матрицой для новой системы отрисовки
     */
    public static void renderShadowVertical(Color color, float lineWidth, double startAlpha, int size, double posX, double posY1, boolean right, boolean edges, MatrixStack matrixStack) {
        renderShadowVertical(color, lineWidth, startAlpha, size, posX, posY1, 0, right, edges, matrixStack);
    }

    /**
     * Рисует вертикальную линию ввиде тени
     *
     * @param color Цвет линии
     * @param lineWidth Жирность линии
     * @param startAlpha Прозрачность начала линии
     * @param size Длина линии
     * @param posX Ось X начала линии
     * @param posY1 Ось Y начала линии
     * @param posY2 Видимо оригинальный создатель забыл убрать аргумент
     * @param right Направление линии (true - направо, false - налево)
     * @param edges Следует ли рисовать границы
     * @param matrixStack Стек с матрицой для новой системы отрисовки
     */
    public static void renderShadowVertical(Color color, float lineWidth, double startAlpha, int size, double posX, double posY1, double posY2, boolean right, boolean edges, MatrixStack matrixStack) {
        renderShadowVertical(lineWidth, startAlpha, size, posX, posY1, posY2, right, edges, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, matrixStack);
    }

    /**
     * Меняет активный цвет opengl при отрисовке
     *
     * @param color Цвет
     */
    public static void setColor(int color) {
        glColor4ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF), (byte) (color >> 24 & 0xFF));
    }

    /**
     * Рисует конус
     *
     * @param radius Радиус конуса
     * @param height Ширина конуса
     * @param segments Количество сегментов (360 самое оптимальное)
     * @param flag Если false включает отрисовку с двух сторон
     * @param matrixStack Стек с матрицой для новой системы отрисовки
     */
    public static void drawCone(float radius, float height, int segments, boolean flag, MatrixStack matrixStack) {
        start2Draw(() -> {
            glDisable(GL_CULL_FACE);
            if (!flag)
                glEnable(GL_CULL_FACE);

            float[][] vertices = new float[segments][3];
            float[] topVertex = new float[] { 0, 0, 0 };
            for (int i = 0; i < segments; i++) {
                vertices[i][0] = MathHelper.cos(MathHelper.PI * 2 / (float) segments * i) * radius;
                vertices[i][1] = -height;
                vertices[i][2] = MathHelper.sin(MathHelper.PI * 2 / (float) segments * i) * radius;
            }

            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(GL_POLYGON, DefaultVertexFormats.POSITION);
            for (int i = 0; i < segments; i++)
                bufferBuilder.pos(matrixStack, vertices[i][0], vertices[i][1], vertices[i][2]).endVertex();
            bufferBuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferBuilder);

            for (int i = 0; i < segments; i++) {
                bufferBuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION);
                bufferBuilder.pos(matrixStack, vertices[i][0], vertices[i][1], vertices[i][2]).endVertex();
                bufferBuilder.pos(matrixStack, topVertex[0], topVertex[1], topVertex[2]).endVertex();
                bufferBuilder.pos(matrixStack, vertices[(i + 1) % segments][0], vertices[(i + 1) % segments][1], vertices[(i + 1) % segments][2]).endVertex();
                bufferBuilder.finishDrawing();
                WorldVertexBufferUploader.draw(bufferBuilder);
            }

            glEnable(GL_CULL_FACE);
        });
    }

    /**
     * Рисует прямоугольник с обводкой
     *
     * @param x Ось X прямоугольника
     * @param y Ось Y прямоугольника
     * @param width Длина прямоугольника
     * @param height Ширина прямоугольника
     * @param lineSize Жирность линии обводки
     * @param borderColor Цвет обводки
     * @param color Цвет прямоугольника
     * @param matrixStack Стек с матрицой для новой системы отрисовки
     */
    public static void drawBorderedRect1(double x, double y, double width, double height, double lineSize, int borderColor, int color, MatrixStack matrixStack) {
        drawRect(x, y, x + width, y + height, color, matrixStack);
        drawRect(x, y, x + width, y + lineSize, borderColor, matrixStack);
        drawRect(x, y, x + lineSize, y + height, borderColor, matrixStack);
        drawRect(x + width, y, x + width - lineSize, y + height, borderColor, matrixStack);
        drawRect(x, y + height, x + width, y + height - lineSize, borderColor, matrixStack);
    }

    /**
     * Рисует градиентный прямоугольник используя прямоугольную систему координат
     *
     * @param left Координата левой границы прямоугольника
     * @param top Координата верхней границы прямоугольника
     * @param right Координата правой границы прямоугольника
     * @param bottom Координата нижней границы прямоугольника
     * @param startColor Цвет начала прямоугольника
     * @param endColor Цвет конца прямоугольника
     * @param matrixStack Стек с матрицой для новой системы отрисовки
     */
    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor, MatrixStack matrixStack) {
        start2Draw(() -> {
            glShadeModel(GL_SMOOTH);

            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            bufferBuilder.pos(matrixStack, right, top).color(startColor).endVertex();
            bufferBuilder.pos(matrixStack, left, top).color(startColor).endVertex();
            bufferBuilder.pos(matrixStack, left, bottom).color(endColor).endVertex();
            bufferBuilder.pos(matrixStack, right, bottom).color(endColor).endVertex();
            bufferBuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferBuilder);

            glShadeModel(GL_FLAT);
        });
    }

    /**
     * ДЦП не приговор?
     */
    public static void drawSmoothRect(double left, double top, double right, double bottom, int color, MatrixStack matrixStack) {
        drawRect(left, top, right, bottom, color, matrixStack);

        glScalef(0.5f, 0.5f, 0.5f);
        drawRect(left * 2.0f - 1.0f, top * 2.0f, left * 2.0f, bottom * 2.0f - 1.0f, color, matrixStack);
        drawRect(left * 2.0f, top * 2.0f - 1.0f, right * 2.0f, top * 2.0f, color, matrixStack);
        drawRect(right * 2.0f, top * 2.0f, right * 2.0f + 1.0f, bottom * 2.0f - 1.0f, color, matrixStack);
        glScalef(2.0f, 2.0f, 2.0f);
    }

    /**
     * Рисует градиентный прямоугольник используя прямоугольную систему координат
     *
     * @param left Координата левой границы прямоугольника
     * @param top Координата верхней границы прямоугольника
     * @param right Координата правой границы прямоугольника
     * @param bottom Координата нижней границы прямоугольника
     * @param col1 Цвет начала прямоугольника
     * @param col2 Цвет конца прямоугольника
     * @param matrixStack Стек с матрицой для новой системы отрисовки
     */
    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2, MatrixStack matrixStack) {
        start2Draw(() -> {
            glShadeModel(GL_SMOOTH);

            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            bufferBuilder.pos(matrixStack, left, top).color(col1).endVertex();
            bufferBuilder.pos(matrixStack, left, bottom).color(col1).endVertex();
            bufferBuilder.pos(matrixStack, right, bottom).color(col2).endVertex();
            bufferBuilder.pos(matrixStack, right, top).color(col2).endVertex();
            bufferBuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferBuilder);

            glShadeModel(GL_FLAT);
        });
    }

    /**
     * Изменяет прозрачность цвету
     *
     * @param color Цвет
     * @param alpha Прозрачность
     * @return Цвет с установленной прозрачностью
     */
    public static Color injectAlpha(Color color, int alpha) {
        alpha = MathHelper.clamp(alpha, 0, 255);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    /**
     * Биндит текстуру в opengl при отрисовке
     *
     * @param textureID ID текстуры
     */
    public static void bindTexture(int textureID) {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public static void setAlphaLimit(float limit) {
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, limit * 0.01f);
    }

    /**
     * Рисует круг
     *
     * @param x Ось X круга
     * @param y Ось Y круга
     * @param radius Радиус круга
     * @param color Цвет круга
     * @param matrixStack Стек с матрицой для новой системы отрисовки
     */
    public static void drawCircle(float x, float y, float radius, int color, MatrixStack matrixStack) {
        start2Draw(() -> {
            setColor(color);
            glLineWidth(1);
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION);
            for (int i = 0; i <= 360; i++)
                bufferBuilder.pos(matrixStack, x + Math.sin(Math.toRadians(i)) * radius, y + Math.cos(Math.toRadians(i)) * radius).endVertex();
            bufferBuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferBuilder);
            setColor(-1);
        });
    }

    /**
     * Рисует неполный круг
     *
     * @param x Ось X круга
     * @param y Ось Y круга
     * @param start Градус начала круга
     * @param end Градус конца круга
     * @param radius Радиус круга
     * @param filled Если true, значит круг будет заполнен цветом
     * @param color Цвет круга
     * @param matrixStack Стек с матрицой для новой системы отрисовки
     */
    public static void drawCircle(float x, float y, float start, float end, float radius, boolean filled, Color color, MatrixStack matrixStack) {
        start2Draw(() -> {
            setColor(color.getRGB());
            glLineWidth(2);
            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION);
            for (float i = end; i >= start; i -= 4) {
                double cos = Math.cos(Math.toRadians(i)) * radius;
                double sin = Math.sin(Math.toRadians(i)) * radius;

                bufferBuilder.pos(matrixStack, x + cos, y + sin).endVertex();
            }
            bufferBuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferBuilder);

            bufferBuilder.begin(filled ? GL_TRIANGLE_FAN : GL_LINE_STRIP, DefaultVertexFormats.POSITION);
            for (float i = end; i >= start; i -= 4) {
                double cos = Math.cos(Math.toRadians(i)) * radius;
                double sin = Math.sin(Math.toRadians(i)) * radius;

                bufferBuilder.pos(matrixStack, x + cos, y + sin).endVertex();
            }
            bufferBuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferBuilder);

            glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
            glDisable(GL_LINE_SMOOTH);
        });
    }

    /**
     * Рисует круг
     *
     * @param x Ось X круга
     * @param y Ось Y круга
     * @param radius Радиус круга
     * @param filled Если true, значит круг будет заполнен цветом
     * @param color Цвет круга
     * @param matrixStack Стек с матрицой для новой системы отрисовки
     */
    public static void drawCircle(float x, float y, float radius, boolean filled, Color color, MatrixStack matrixStack) {
        drawCircle(x, y, 0, 360, radius, filled, color, matrixStack);
    }

    /**
     * Рисует прямоугольник используя прямоугольную систему координат
     *
     * @param left Координата левой границы прямоугольника
     * @param top Координата верхней границы прямоугольника
     * @param right Координата правой границы прямоугольника
     * @param bottom Координата нижней границы прямоугольника
     * @param color Цвет прямоугольника
     * @param matrixStack Стек с матрицой для новой системы отрисовки
     */
    public static void drawRect(double left, double top, double right, double bottom, int color, MatrixStack matrixStack) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        double finalLeft = left;
        double finalTop = top;
        double finalRight = right;
        double finalBottom = bottom;
        start2Draw(() -> {
            setColor(color);
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION);
            bufferBuilder.pos(matrixStack, finalLeft, finalBottom).endVertex();
            bufferBuilder.pos(matrixStack, finalRight, finalBottom).endVertex();
            bufferBuilder.pos(matrixStack, finalRight, finalTop).endVertex();
            bufferBuilder.pos(matrixStack, finalLeft, finalTop).endVertex();
            bufferBuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferBuilder);
        });
    }

    /**
     * Начинает правильную отрисовку оверлея без багов
     *
     * @param runnable Действие после включения отрисовки
     */
    public static void start2Draw(Runnable runnable) {
        boolean isEnabled = glIsEnabled(GL_BLEND);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_ALPHA_TEST);

        runnable.run();

        if (!isEnabled)
            glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_ALPHA_TEST);
    }

    public static void drawFilledCircle(double x, double y, double radius, Color color, MatrixStack matrixStack) {
        start2Draw(() -> {
            double angle = 6.283185307179586D / 50D;
            setColor(color.getRGB());
            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
            for (int i = 0; i < 50; i++) {
                double xx = Math.sin(i * angle) * radius;
                double yy = Math.cos(i * angle) * radius;

                bufferBuilder.pos(matrixStack, x+xx, y+yy).endVertex();
            }
            bufferBuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferBuilder);

            setColor(-1);
            glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
            glDisable(GL_LINE_SMOOTH);
        });
    }
}
