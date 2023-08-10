package fun.rich.ui.clickgui.component.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import fun.rich.ui.clickgui.Panel;
import fun.rich.ui.clickgui.component.Component;
import fun.rich.ui.clickgui.component.ExpandableComponent;
import fun.rich.ui.clickgui.component.PropertyComponent;
import fun.rich.ui.settings.Setting;
import fun.rich.ui.settings.impl.ColorSetting;
import fun.rich.utils.render.RenderUtils;

import java.awt.Color;

public class ColorPickerComponent extends ExpandableComponent implements PropertyComponent {

    private static final int COLOR_PICKER_HEIGHT = 80;
    public static Tessellator tessellator = Tessellator.getInstance();
    public static BufferBuilder buffer = tessellator.getBuffer();
    private final ColorSetting colorSetting;
    private float hue;
    private float saturation;
    private float brightness;
    private float alpha;

    private boolean colorSelectorDragging;

    private boolean hueSelectorDragging;

    private boolean alphaSelectorDragging;

    public ColorPickerComponent(Component parent, ColorSetting colorSetting, int x, int y, int width, int height) {
        super(parent, colorSetting.getName(), x, y, width, height);
        this.colorSetting = colorSetting;

        int value = colorSetting.getColorValue();
        float[] hsb = getHSBFromColor(value);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];

        this.alpha = (value >> 24 & 0xFF) / 255.0F;
    }

    @Override
    public void drawComponent(MainWindow scaledResolution, int mouseX, int mouseY, MatrixStack matrixStack) {
        super.drawComponent(scaledResolution, mouseX, mouseY, matrixStack);

        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        float left = x + width - 13;
        float top = y + height / 2.0F - 2;
        float right = x + width - 2;
        float bottom = y + height / 2.0F + 2;

        int textColor = new Color(222,222,222).getRGB();

        RenderUtils.drawSmoothRect(x, y, x + width, y + height, new Color(20, 20, 20, 111).getRGB(), matrixStack);
        mc.rubik_16.drawStringWithShadow(getName(), x + 2, y + height / 2F - 3, textColor, matrixStack);
        RenderUtils.drawSmoothRect(left, top, right, bottom, colorSetting.getColorValue(), matrixStack);

        if (isExpanded()) {
            // Draw Background
            RenderUtils.drawRect(x + Panel.X_ITEM_OFFSET, y + height, x + width - Panel.X_ITEM_OFFSET, y + getHeightWithExpand(), new Color(20, 20, 20, 160).getRGB(), matrixStack);

            // Draw Color Picker
            {
                // Box with gradient
                float cpLeft = x + 2;
                float cpTop = y + height + 2;
                float cpRight = x + COLOR_PICKER_HEIGHT - 2;
                float cpBottom = y + height + COLOR_PICKER_HEIGHT - 2;

                if (mouseX <= cpLeft || mouseY <= cpTop || mouseX >= cpRight || mouseY >= cpBottom)
                    colorSelectorDragging = false;

                float colorSelectorX = saturation * (cpRight - cpLeft);
                float colorSelectorY = (1 - brightness) * (cpBottom - cpTop);

                if (colorSelectorDragging) {
                    float wWidth = cpRight - cpLeft;
                    float xDif = mouseX - cpLeft;
                    this.saturation = xDif / wWidth;
                    colorSelectorX = xDif;

                    float hHeight = cpBottom - cpTop;
                    float yDif = mouseY - cpTop;
                    this.brightness = 1 - (yDif / hHeight);
                    colorSelectorY = yDif;

                    updateColor(Color.HSBtoRGB(hue, saturation, brightness), false);
                }

                RenderUtils.drawRect(cpLeft, cpTop, cpRight, cpBottom, 0xFF000000, matrixStack);
                drawColorPickerRect(cpLeft + 0.5F, cpTop + 0.5F, cpRight - 0.5F, cpBottom - 0.5F);
                // Selector
                float selectorWidth = 2;
                float outlineWidth = 0.5F;
                float half = selectorWidth / 2;

                float csLeft = cpLeft + colorSelectorX - half;
                float csTop = cpTop + colorSelectorY - half;
                float csRight = cpLeft + colorSelectorX + half;
                float csBottom = cpTop + colorSelectorY + half;

                RenderUtils.drawRect(csLeft - outlineWidth,
                        csTop - outlineWidth,
                        csRight + outlineWidth,
                        csBottom + outlineWidth,
                        0xFF000000,
                        matrixStack);
                RenderUtils.drawRect(
                        csLeft,
                        csTop,
                        csRight,
                        csBottom,
                        Color.HSBtoRGB(hue, saturation, brightness),
                        matrixStack
                );
            }

            // Hue Slider
            {
                float sLeft = x + COLOR_PICKER_HEIGHT - 1;
                float sTop = y + height + 2;
                float sRight = sLeft + 5;
                float sBottom = y + height + COLOR_PICKER_HEIGHT - 2;

                if (mouseX <= sLeft || mouseY <= sTop || mouseX >= sRight || mouseY >= sBottom)
                    hueSelectorDragging = false;

                float hueSelectorY = this.hue * (sBottom - sTop);

                if (hueSelectorDragging) {
                    float hsHeight = sBottom - sTop;
                    float yDif = mouseY - sTop;
                    this.hue = yDif / hsHeight;
                    hueSelectorY = yDif;

                    updateColor(Color.HSBtoRGB(hue, saturation, brightness), false);
                }

                // Outline
                RenderUtils.drawRect(sLeft, sTop, sRight, sBottom, 0xFF000000, matrixStack);
                float inc = 0.2F;
                float times = 1 / inc;
                float sHeight = sBottom - sTop;
                float sY = sTop + 0.5F;
                float size = sHeight / times;
                // Color
                for (int i = 0; i < times; i++) {
                    boolean last = i == times - 1;
                    if (last)
                        size--;
                    RenderUtils.drawGradientRect(
                            sLeft + 0.5F, sY, sRight - 0.5F,
                            sY + size,
                            Color.HSBtoRGB(inc * i, 1.0F, 1.0F),
                            Color.HSBtoRGB(inc * (i + 1), 1.0F, 1.0F),
                            matrixStack
                    );
                    if (!last)
                        sY += size;
                }

                float selectorHeight = 2;
                float outlineWidth = 0.5F;
                float half = selectorHeight / 2;

                float csTop = sTop + hueSelectorY - half;
                float csBottom = sTop + hueSelectorY + half;

                RenderUtils.drawRect(
                        sLeft - outlineWidth,
                        csTop - outlineWidth,
                        sRight + outlineWidth,
                        csBottom + outlineWidth,
                        0xFF000000,
                        matrixStack
                );
                RenderUtils.drawRect(
                        sLeft,
                        csTop,
                        sRight,
                        csBottom,
                        Color.HSBtoRGB(hue, 1.0F, 1.0F),
                        matrixStack
                );
            }

            // Alpha Slider
            {
                float sLeft = x + COLOR_PICKER_HEIGHT + 6;
                float sTop = y + height + 2;
                float sRight = sLeft + 5;
                float sBottom = y + height + COLOR_PICKER_HEIGHT - 2;

                if (mouseX <= sLeft || mouseY <= sTop || mouseX >= sRight || mouseY >= sBottom)
                    alphaSelectorDragging = false;

                int color = Color.HSBtoRGB(hue, saturation, brightness);

                int r = color >> 16 & 0xFF;
                int g = color >> 8 & 0xFF;
                int b = color & 0xFF;

                float alphaSelectorY = alpha * (sBottom - sTop);

                if (alphaSelectorDragging) {
                    float hsHeight = sBottom - sTop;
                    float yDif = mouseY - sTop;
                    this.alpha = yDif / hsHeight;
                    alphaSelectorY = yDif;

                    updateColor(new Color(r, g, b, (int) (alpha * 255)).getRGB(), true);
                }

                // Outline
                RenderUtils.drawRect(sLeft, sTop, sRight, sBottom, 0xFF000000, matrixStack);
                // Background
                drawCheckeredBackground(sLeft + 0.5F, sTop + 0.5F, sRight - 0.5F, sBottom - 0.5F, matrixStack);
                // Colored bit
                RenderUtils.drawGradientRect(sLeft + 0.5F, sTop + 0.5F, sRight - 0.5F, sBottom - 0.5F, new Color(0,0,0, 100).getRGB(), new Color(r, g, b, 255).getRGB(), matrixStack);

                float selectorHeight = 2;
                float outlineWidth = 0.5F;
                float half = selectorHeight / 2;

                float csTop = sTop + alphaSelectorY - half;
                float csBottom = sTop + alphaSelectorY + half;

                float bx = sRight + outlineWidth;
                float ay = csTop - outlineWidth;
                float by = csBottom + outlineWidth;
                // Selector thingy
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                RenderUtils.setColor(0xFF000000);
                GL11.glBegin(GL11.GL_LINE_LOOP);
                GL11.glVertex2f(sLeft, ay);
                GL11.glVertex2f(sLeft, by);
                GL11.glVertex2f(bx, by);
                GL11.glVertex2f(bx, ay);
                GL11.glEnd();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            // Color Preview Section

        }
    }

    private void drawCheckeredBackground(float x, float y, float right, float bottom, MatrixStack matrixStack) {
        RenderUtils.drawRect(x, y, right, bottom, -1, matrixStack);

        for (boolean off = false; y < bottom; y++)
            for (float x1 = x + ((off = !off) ? 1 : 0); x1 < right; x1 += 2)
                RenderUtils.drawRect(x1, y, x1 + 1, y + 1, 0xFF808080, matrixStack);
    }

    private void updateColor(int hex, boolean hasAlpha) {
        if (hasAlpha)
            colorSetting.setColorValue(hex);
        else {
            colorSetting.setColorValue(new Color(
                    hex >> 16 & 0xFF,
                    hex >> 8 & 0xFF,
                    hex & 0xFF,
                    (int) (alpha * 255)).getRGB());
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        super.onMouseClick(mouseX, mouseY, button);

        if (isExpanded()) {
            if (button == 0) {
                int x = getX();
                int y = getY();
                // Color Picker Dimensions
                float cpLeft = x + 2;
                float cpTop = y + getHeight() + 2;
                float cpRight = x + COLOR_PICKER_HEIGHT - 2;
                float cpBottom = y + getHeight() + COLOR_PICKER_HEIGHT - 2;
                // Hue Slider Dimensions
                float sLeft = x + COLOR_PICKER_HEIGHT - 1;
                float sTop = y + getHeight() + 2;
                float sRight = sLeft + 5;
                float sBottom = y + getHeight() + COLOR_PICKER_HEIGHT - 2;
                // Alpha Slider Dimensions
                float asLeft = x + COLOR_PICKER_HEIGHT + 6;
                float asTop = y + getHeight() + 2;
                float asRight = asLeft + 5;
                float asBottom = y + getHeight() + COLOR_PICKER_HEIGHT - 2;
                // If hovered over color picker
                colorSelectorDragging = !colorSelectorDragging && mouseX > cpLeft && mouseY > cpTop && mouseX < cpRight && mouseY < cpBottom;
                // If hovered over hue slider
                hueSelectorDragging = !hueSelectorDragging && mouseX > sLeft && mouseY > sTop && mouseX < sRight && mouseY < sBottom;
                // If hovered over alpha slider
                alphaSelectorDragging = !alphaSelectorDragging && mouseX > asLeft && mouseY > asTop && mouseX < asRight && mouseY < asBottom;
            }
        }
    }

    @Override
    public void onMouseRelease(int button) {
        if (hueSelectorDragging)
            hueSelectorDragging = false;
        else if (colorSelectorDragging)
            colorSelectorDragging = false;
        else if (alphaSelectorDragging)
            alphaSelectorDragging = false;
    }

    private float[] getHSBFromColor(int hex) {
        int r = hex >> 16 & 0xFF;
        int g = hex >> 8 & 0xFF;
        int b = hex & 0xFF;
        return Color.RGBtoHSB(r, g, b, null);
    }

    public void drawColorPickerRect(float left, float top, float right, float bottom) {
        int hueBasedColor = Color.HSBtoRGB(hue, 1, 1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        RenderSystem.enableBlend();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right, top, 0).color(hueBasedColor).endVertex();
        buffer.pos(left, top, 0).color(-1).endVertex();
        buffer.pos(left, bottom, 0).color(-1).endVertex();
        buffer.pos(right, bottom, 0).color(hueBasedColor).endVertex();
        tessellator.draw();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right, top, 0).color(0x18000000).endVertex();
        buffer.pos(left, top, 0).color(0x18000000).endVertex();
        buffer.pos(left, bottom, 0).color(-16777216).endVertex();
        buffer.pos(right, bottom, 0).color(-16777216).endVertex();
        tessellator.draw();
        RenderSystem.disableBlend();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @Override
    public boolean canExpand() {
        return true;
    }

    @Override
    public int getHeightWithExpand() {
        return getHeight() + COLOR_PICKER_HEIGHT;
    }

    @Override
    public void onPress(int mouseX, int mouseY, int button) {

    }

    @Override
    public Setting getSetting() {
        return colorSetting;
    }
}
