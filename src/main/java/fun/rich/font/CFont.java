package fun.rich.font;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.BufferedImageTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class CFont {

    protected CharData[] charData = new CharData[1104];
    @Getter
    protected Font font;
    @Getter
    protected boolean antiAlias;
    @Getter
    protected boolean fractionalMetrics;
    protected int fontHeight = -1;
    protected int charOffset = 0;
    protected BufferedImageTexture tex;

    public CFont(Font font, boolean antiAlias, boolean fractionalMetrics) {
        this.font = font;
        this.antiAlias = antiAlias;
        this.fractionalMetrics = fractionalMetrics;
        tex = setupTexture(font, antiAlias, fractionalMetrics, this.charData);
    }

    protected BufferedImageTexture setupTexture(Font font, boolean antiAlias, boolean fractionalMetrics, CharData[] chars) {
        BufferedImage img = generateFontImage(font, antiAlias, fractionalMetrics, chars);

        try {
            return new BufferedImageTexture(img);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected BufferedImage generateFontImage(Font font, boolean antiAlias, boolean fractionalMetrics,
                                              CharData[] chars) {
        int imageSize = 512;
        BufferedImage bufferedImage = new BufferedImage((int) imageSize, (int) imageSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        g.setFont(font);
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, (int) imageSize, (int) imageSize);
        g.setColor(Color.WHITE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON
                        : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        FontMetrics fontMetrics = g.getFontMetrics();
        int charHeight = 0;
        int positionX = 0;
        int positionY = 1;

        for (int i = 0; i < chars.length; i++) {
            char ch = (char) i;
            if ((ch > 1039 && ch < 1104) || ch < 256) {

                CharData charData = new CharData();
                Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(ch), g);
                charData.width = (dimensions.getBounds().width + 8);
                charData.height = dimensions.getBounds().height;

                if (positionX + charData.width >= (int) imageSize) {
                    positionX = 0;
                    positionY += charHeight;
                    charHeight = 0;
                }

                if (charData.height > charHeight) {
                    charHeight = charData.height;
                }

                charData.storedX = positionX;
                charData.storedY = positionY;

                if (charData.height > this.fontHeight) {
                    this.fontHeight = charData.height;
                }

                chars[i] = charData;
                g.drawString(String.valueOf(ch), positionX + 2, positionY + fontMetrics.getAscent());
                positionX += charData.width;
            }
        }

        return bufferedImage;
    }

    public void drawChar(MatrixStack matrixStack, CharData[] chars, char c, float x, float y) throws ArrayIndexOutOfBoundsException {
        try {
            drawQuad(matrixStack, x, y, chars[c].width, chars[c].height, chars[c].storedX, chars[c].storedY, chars[c].width,
                    chars[c].height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void drawQuad(MatrixStack matrixStack, float x, float y, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight) {
        try {
            int imageSize = 512;
            float renderSRCX = srcX / imageSize;
            float renderSRCY = srcY / imageSize;
            float renderSRCWidth = srcWidth / imageSize;
            float renderSRCHeight = srcHeight / imageSize;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            bufferBuilder.pos((x + width), y, 0.0).tex(renderSRCX + renderSRCWidth, renderSRCY).endVertex();
            bufferBuilder.pos(x, y, 0.0).tex(renderSRCX, renderSRCY).endVertex();
            bufferBuilder.pos(x, (y + height), 0.0).tex(renderSRCX, renderSRCY + renderSRCHeight).endVertex();
            bufferBuilder.pos(x, (y + height), 0.0).tex(renderSRCX, renderSRCY + renderSRCHeight).endVertex();
            bufferBuilder.pos((x + width), (y + height), 0.0).tex(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight).endVertex();
            bufferBuilder.pos((x + width), y, 0.0).tex(renderSRCX + renderSRCWidth, renderSRCY).endVertex();
            tessellator.draw();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public int getFontHeight() {
        return (this.fontHeight - 8) / 2;
    }

    public int getStringWidth(String text) {
        int width = 0;

        for (char c : text.toCharArray()) {
            if (c < this.charData.length) {
                width += this.charData[c].width - 8 + this.charOffset;
            }
        }

        return width / 2;
    }

    public void setAntiAlias(boolean antiAlias) {
        if (this.antiAlias != antiAlias) {
            this.antiAlias = antiAlias;
            tex = setupTexture(this.font, antiAlias, this.fractionalMetrics, this.charData);
        }
    }

    public void setFractionalMetrics(boolean fractionalMetrics) {
        if (this.fractionalMetrics != fractionalMetrics) {
            this.fractionalMetrics = fractionalMetrics;
            tex = setupTexture(this.font, this.antiAlias, fractionalMetrics, this.charData);
        }
    }

    public void setFont(Font font) {
        this.font = font;
        tex = setupTexture(font, this.antiAlias, this.fractionalMetrics, this.charData);
    }

    protected static class CharData {
        public int width;
        public int height;
        public int storedX;
        public int storedY;
    }
}
