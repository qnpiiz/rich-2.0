package fun.rich.utils.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RoundedUtil {

    public static ShaderUtil roundedShader = new ShaderUtil("roundedRect");
    public static ShaderUtil roundedOutlineShader = new ShaderUtil("rich/shaders/roundRectOutline.frag");
    private static final ShaderUtil roundedTexturedShader = new ShaderUtil("rich/shaders/roundRectTextured.frag");
    private static final ShaderUtil roundedGradientShader = new ShaderUtil("roundedRectGradient");

    public static void drawRound(float x, float y, float width, float height, float radius, Color color, MatrixStack matrixStack) {
        drawRound(x, y, width, height, radius, false, color, matrixStack);
    }

    public static void drawRoundScale(float x, float y, float width, float height, float radius, Color color, float scale, MatrixStack matrixStack) {
        drawRound(x + width - width * scale, y + height / 2f - ((height / 2f) * scale),
                width * scale, height * scale, radius, false, color, matrixStack);
    }

    public static void drawGradientHorizontal(float x, float y, float width, float height, float radius, Color left, Color right, MatrixStack matrixStack) {
        drawGradientRound(x, y, width, height, radius, left, left, right, right, matrixStack);
    }
    public static void drawGradientVertical(float x, float y, float width, float height, float radius, Color top, Color bottom, MatrixStack matrixStack) {
        drawGradientRound(x, y, width, height, radius, bottom, top, bottom, top, matrixStack);
    }
    public static void drawGradientCornerLR(float x, float y, float width, float height, float radius, Color topLeft, Color bottomRight, MatrixStack matrixStack) {
        Color mixedColor = ColorUtils.interpolateColorC(topLeft, bottomRight, .5f);
        drawGradientRound(x, y, width, height, radius, mixedColor, topLeft, bottomRight, mixedColor, matrixStack);
    }

    public static void drawGradientCornerRL(float x, float y, float width, float height, float radius, Color bottomLeft, Color topRight, MatrixStack matrixStack) {
        Color mixedColor = ColorUtils.interpolateColorC(topRight, bottomLeft, .5f);
        drawGradientRound(x, y, width, height, radius, bottomLeft, mixedColor, mixedColor, topRight, matrixStack);
    }

    public static void drawGradientRound(float x, float y, float width, float height, float radius, Color bottomLeft, Color topLeft, Color bottomRight, Color topRight, MatrixStack matrixStack) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedGradientShader.init();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedGradientShader);
        // Bottom Left
        roundedGradientShader.setUniformf("color1", bottomLeft.getRed() / 255f, bottomLeft.getGreen() / 255f, bottomLeft.getBlue() / 255f, bottomLeft.getAlpha() / 255f);
        //Top left
        roundedGradientShader.setUniformf("color2", topLeft.getRed() / 255f, topLeft.getGreen() / 255f, topLeft.getBlue() / 255f, topLeft.getAlpha() / 255f);
        //Bottom Right
        roundedGradientShader.setUniformf("color3", bottomRight.getRed() / 255f, bottomRight.getGreen() / 255f, bottomRight.getBlue() / 255f, bottomRight.getAlpha() / 255f);
        //Top Right
        roundedGradientShader.setUniformf("color4", topRight.getRed() / 255f, topRight.getGreen() / 255f, topRight.getBlue() / 255f, topRight.getAlpha() / 255f);
        ShaderUtil.drawQuads(x - 1, y - 1, width + 2, height + 2, matrixStack);
        roundedGradientShader.unload();
        RenderSystem.disableBlend();
    }

    public static void drawRound(float x, float y, float width, float height, float radius, boolean blur, Color color, MatrixStack matrixStack) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedShader.init();

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformi("blur", blur ? 1 : 0);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        ShaderUtil.drawQuads(x - 1, y - 1, width + 2, height + 2, matrixStack);
        roundedShader.unload();
        RenderSystem.disableBlend();
    }

    public static void drawRoundOutline(float x, float y, float width, float height, float radius, float outlineThickness, Color color, Color outlineColor, MatrixStack matrixStack) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedOutlineShader.init();

        MainWindow sr = Minecraft.getInstance().getMainWindow();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedOutlineShader);
        roundedOutlineShader.setUniformf("outlineThickness", (float) (outlineThickness * sr.getGuiScaleFactor()));
        roundedOutlineShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        roundedOutlineShader.setUniformf("outlineColor", outlineColor.getRed() / 255f, outlineColor.getGreen() / 255f, outlineColor.getBlue() / 255f, outlineColor.getAlpha() / 255f);

        ShaderUtil.drawQuads(x - (2 + outlineThickness), y - (2 + outlineThickness), width + (4 + outlineThickness * 2), height + (4 + outlineThickness * 2), matrixStack);
        roundedOutlineShader.unload();
        RenderSystem.disableBlend();
    }

    public static void drawRoundTextured(float x, float y, float width, float height, float radius, float alpha, MatrixStack matrixStack) {
        RenderUtils.setColor(-1);
        roundedTexturedShader.init();
        roundedTexturedShader.setUniformi("textureIn", 0);
        setupRoundedRectUniforms(x, y, width, height, radius, roundedTexturedShader);
        roundedTexturedShader.setUniformf("alpha", alpha);
        ShaderUtil.drawQuads(x - 1, y - 1, width + 2, height + 2, matrixStack);
        roundedTexturedShader.unload();
        RenderSystem.disableBlend();
    }

    private static void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, ShaderUtil roundedTexturedShader) {
        MainWindow sr = Minecraft.getInstance().getMainWindow();
        roundedTexturedShader.setUniformf("location", (float) (x * sr.getGuiScaleFactor()),
                (float) ((sr.getHeight() - (height * sr.getGuiScaleFactor())) - (y * sr.getGuiScaleFactor())));
        roundedTexturedShader.setUniformf("rectSize", (float) (width * sr.getGuiScaleFactor()), (float) (height * sr.getGuiScaleFactor()));
        roundedTexturedShader.setUniformf("radius", (float) (radius * sr.getGuiScaleFactor()));
    }
}
