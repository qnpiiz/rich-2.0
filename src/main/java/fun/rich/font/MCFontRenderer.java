package fun.rich.font;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fun.rich.utils.render.RenderUtils;
import net.minecraft.client.renderer.texture.BufferedImageTexture;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MCFontRenderer extends CFont {

    private final int[] colorCode = new int[32];
    protected CharData[] boldChars = new CharData[256];
    protected CharData[] italicChars = new CharData[256];
    protected CharData[] boldItalicChars = new CharData[256];
    protected BufferedImageTexture texBold;
    protected BufferedImageTexture texItalic;
    protected BufferedImageTexture texItalicBold;

    public MCFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        setupBoldItalicIDs();

        for (int index = 0; index < 32; index++) {
            int noClue = (index >> 3 & 0x1) * 85;
            int red = (index >> 2 & 0x1) * 170 + noClue;
            int green = (index >> 1 & 0x1) * 170 + noClue;
            int blue = (index & 0x1) * 170 + noClue;

            if (index == 6) {
                red += 85;
            }

            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            this.colorCode[index] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF);
        }
    }

    public void drawBlurredStringWithShadow(String text, double x, double y, int blurRadius, Color blurColor, int color, MatrixStack matrixStack) {
        RenderUtils.drawBlurredShadow((int) x, (int) y, (int) getStringWidth(text), (int) getFontHeight(), blurRadius, blurColor);
        drawStringWithShadow(text, (float) x, (float) y, color, matrixStack);
    }

    public void drawBlurredString(String text, double x, double y, int blurRadius, Color blurColor, int color, MatrixStack matrixStack) {
        RenderUtils.drawBlurredShadow((int) x, (int) y, (int) getStringWidth(text), (int) getFontHeight(), blurRadius, blurColor);
        drawString(text, (float) x, (float) y, color, matrixStack);
    }

    public void drawCenteredBlurredString(String text, double x, double y, int blurRadius, Color blurColor, int color, MatrixStack matrixStack) {
        RenderUtils.drawBlurredShadow((int) ((int) x - (float)this.getStringWidth(text) / 2.0f), (int) y, (int) getStringWidth(text), (int) getFontHeight(), blurRadius, blurColor);
        drawString(text, (float) (x - this.getStringWidth(text) / 2F), (float) y, color, matrixStack);
    }

    public static void drawStringWithOutline(MCFontRenderer fontRenderer, String text, float x, float y, int color, MatrixStack matrixStack) {
        fontRenderer.drawString(text, x - 0.8F, y, Color.BLACK.getRGB(), matrixStack);
        fontRenderer.drawString(text, x + 0.8F, y, Color.BLACK.getRGB(), matrixStack);
        fontRenderer.drawString(text, x, y - 0.8F, Color.BLACK.getRGB(), matrixStack);
        fontRenderer.drawString(text, x, y + 0.8F, Color.BLACK.getRGB(), matrixStack);
        fontRenderer.drawString(text, x, y, color, matrixStack);
    }

    public static void drawStringWithOutline(net.minecraft.client.gui.FontRenderer fontRenderer, String text, float x, float y, int color, MatrixStack matrixStack) {
        fontRenderer.drawString(matrixStack, text, x - 1, y, Color.BLACK.getRGB());
        fontRenderer.drawString(matrixStack, text, x + 1, y, Color.BLACK.getRGB());
        fontRenderer.drawString(matrixStack, text, x, y - 1, Color.BLACK.getRGB());
        fontRenderer.drawString(matrixStack, text, x, y + 1, Color.BLACK.getRGB());
        fontRenderer.drawString(matrixStack, text, x, y, color);
    }

    public static void drawCenteredStringWithOutline(net.minecraft.client.gui.FontRenderer fontRenderer, String text, float x, float y, int color, MatrixStack matrixStack) {
        fontRenderer.drawCenteredString(text, x - 1, y, Color.BLACK.getRGB(), matrixStack);
        fontRenderer.drawCenteredString(text, x + 1, y, Color.BLACK.getRGB(), matrixStack);
        fontRenderer.drawCenteredString(text, x, y - 1, Color.BLACK.getRGB(), matrixStack);
        fontRenderer.drawCenteredString(text, x, y + 1, Color.BLACK.getRGB(), matrixStack);
        fontRenderer.drawCenteredString(text, x, y, color, matrixStack);
    }

    public static float drawCenteredStringWithShadow(net.minecraft.client.gui.FontRenderer fontRenderer, String text, float x, float y, int color, MatrixStack matrixStack) {
        return fontRenderer.drawString(matrixStack, text, x - fontRenderer.getStringWidth(text) / 2F, y, color);
    }

    public void drawCenteredStringWithOutline(MCFontRenderer fontRenderer, String text, float x, float y, int color, MatrixStack matrixStack) {
        drawCenteredString(text, x - 1, y, Color.BLACK.getRGB(), matrixStack);
        drawCenteredString(text, x + 1, y, Color.BLACK.getRGB(), matrixStack);
        drawCenteredString(text, x, y - 1, Color.BLACK.getRGB(), matrixStack);
        drawCenteredString(text, x, y + 1, Color.BLACK.getRGB(), matrixStack);
        drawCenteredString(text, x, y, color, matrixStack);
    }

    public float drawStringWithShadow(String text, double x, double y, int color, MatrixStack matrixStack) {
        float shadowWidth = drawString(text, x + 0.9D, y + 0.7D, color, true, matrixStack);
        return Math.max(shadowWidth, drawString(text, x, y, color, false, matrixStack));
    }

    public void drawStringWithOutline(String text, double x, double y, int color, MatrixStack matrixStack) {
        drawString(text, x - 0.5, y, Color.BLACK.getRGB(), false, matrixStack);
        drawString(text, x + 0.5F, y, Color.BLACK.getRGB(), false, matrixStack);
        drawString(text, x, y - 0.5F, Color.BLACK.getRGB(), false, matrixStack);
        drawString(text, x, y + 0.5F, Color.BLACK.getRGB(), false, matrixStack);
        drawString(text, x, y, color, false, matrixStack);
    }

    public void drawCenteredStringWithOutline(String text, float x, float y, int color, MatrixStack matrixStack) {
        drawCenteredString(text, x - 0.5F, y, Color.BLACK.getRGB(), matrixStack);
        drawCenteredString(text, x + 0.5F, y, Color.BLACK.getRGB(), matrixStack);
        drawCenteredString(text, x, y - 0.5F, Color.BLACK.getRGB(), matrixStack);
        drawCenteredString(text, x, y + 0.5F, Color.BLACK.getRGB(), matrixStack);
        drawCenteredString(text, x, y, color, matrixStack);
    }

    public float drawString(String text, float x, float y, int color, MatrixStack matrixStack) {
        return drawString(text, x, y, color, false, matrixStack);
    }


    public float drawCenteredString(String text, float x, float y, int color, MatrixStack matrixStack) {
        return drawString(text, x - getStringWidth(text) / 2F, y, color, matrixStack);
    }

    public void drawCenteredStringWithShadow(String text, float x, float y, int color, MatrixStack matrixStack) {
        drawString(text, x - getStringWidth(text) / 2F, y, color, matrixStack);
    }

    public void drawCenteredBlurredStringWithShadow(String text, double x, double y, int blurRadius, Color blurColor, int color, MatrixStack matrixStack) {
        RenderUtils.drawBlurredShadow((int) ((int) x - (float) this.getStringWidth(text) / 2.0f), (int) y, (int) getStringWidth(text), (int) getFontHeight(), blurRadius, blurColor);
        drawStringWithShadow(text, (float) (x - this.getStringWidth(text) / 2F), (float) y, color, matrixStack);
    }

    public float drawString(String text, double x, double y, int color, boolean shadow, MatrixStack matrixStack) {
        RenderUtils.setColor(-1);
        x -= 1.0;
        if (color == 0x20FFFFFF) {
            color = 0xFFFFFF;
        }
        if ((color & 0xFC000000) == 0) {
            color |= 0xFF000000;
        }
        if (shadow) {
            color = (color & 0xFCFCFC) >> 2 | color & new Color(20, 20, 20, 200).getRGB();
        }
        CharData[] currentData = this.charData;
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        boolean bold = false;
        boolean italic = false;
        boolean strikethrough = false;
        boolean underline = false;
        x *= 2;
        y = (y - 3) * 2;

        RenderUtils.setColor(color);
        boolean blend = !GL11.glIsEnabled(GL11.GL_BLEND);
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.scaled(0.5, 0.5, 0.5);
        RenderSystem.color4f((color >> 16 & 0xFF) / 255.0f, (color >> 8 & 0xFF) / 255.0f, (color & 0xFF) / 255.0f, alpha);
        RenderSystem.enableTexture();
        RenderSystem.bindTexture(tex.getGlTextureId());

        int size = text.length();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.tex.getGlTextureId());
        int i = 0;

        while (i < size) {
            char character = text.charAt(i);
            if (String.valueOf(character).equals("§")) {
                int colorIndex = 21;
                try {
                    colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    underline = false;
                    strikethrough = false;
                    RenderSystem.bindTexture(this.tex.getGlTextureId());
                    currentData = this.charData;
                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }
                    if (shadow) {
                        colorIndex += 16;
                    }
                    int colorcode = this.colorCode[colorIndex];
                    RenderSystem.color4f((colorcode >> 16 & 0xFF) / 255.0f, (colorcode >> 8 & 0xFF) / 255.0f, (colorcode & 0xFF) / 255.0f, alpha);
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        RenderSystem.bindTexture(this.texItalicBold.getGlTextureId());
                        currentData = this.boldItalicChars;
                    } else {
                        RenderSystem.bindTexture(this.texBold.getGlTextureId());
                        currentData = this.boldChars;
                    }
                } else if (colorIndex == 18) {
                    strikethrough = true;
                } else if (colorIndex == 19) {
                    underline = true;
                } else if (colorIndex == 20) {
                    italic = true;
                    if (bold) {
                        RenderSystem.bindTexture(texItalicBold.getGlTextureId());
                        currentData = boldItalicChars;
                    } else {
                        RenderSystem.bindTexture(this.texItalic.getGlTextureId());
                        currentData = italicChars;
                    }
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    underline = false;
                    strikethrough = false;
                    RenderSystem.color4f((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, alpha);
                    RenderSystem.bindTexture(this.tex.getGlTextureId());
                    currentData = this.charData;
                }
                ++i;
            } else if (character < currentData.length) {
                this.drawChar(matrixStack, currentData, character, (float) x, (float) y);
                x += currentData[character].width - 8 + this.charOffset;
            }

            ++i;
        }

        RenderSystem.bindTexture(0);
        if (blend)
            RenderSystem.disableBlend();
        RenderSystem.disableTexture();
        RenderSystem.popMatrix();
        return (float) (x / 2);
    }

    @Override
    public int getStringWidth(String text) {
        int width = 0;
        CharData[] currentData = this.charData;
        boolean bold = false;
        boolean italic = false;
        int size = text.length();
        int i = 0;
        while (i < size) {
            char character = text.charAt(i);
            if (String.valueOf(character).equals("�")) {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                } else if (colorIndex == 17) {
                    bold = true;
                    currentData = italic ? this.boldItalicChars : this.boldChars;
                } else if (colorIndex == 20) {
                    italic = true;
                    currentData = bold ? this.boldItalicChars : this.italicChars;
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    currentData = this.charData;
                }
                ++i;
            } else if (character < currentData.length) {
                width += currentData[character].width - 8 + this.charOffset;
            }
            ++i;
        }
        return width / 2;
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        setupBoldItalicIDs();
    }

    @Override
    public void setAntiAlias(boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        setupBoldItalicIDs();
    }

    @Override
    public void setFractionalMetrics(boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        setupBoldItalicIDs();
    }

    private void setupBoldItalicIDs() {
        texBold = setupTexture(this.font.deriveFont(Font.BOLD), this.antiAlias, this.fractionalMetrics, this.boldChars);
        texItalic = setupTexture(this.font.deriveFont(Font.ITALIC), this.antiAlias, this.fractionalMetrics, this.italicChars);
        texItalicBold = setupTexture(this.font.deriveFont(Font.BOLD | Font.ITALIC), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
    }
}
