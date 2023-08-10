package net.optifine.shaders.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.optifine.shaders.config.ShaderOption;

public class GuiSliderShaderOption extends GuiButtonShaderOption
{
    private float sliderValue;
    public boolean dragging;
    private ShaderOption shaderOption = null;

    public GuiSliderShaderOption(int buttonId, int x, int y, int w, int h, ShaderOption shaderOption, String text)
    {
        super(buttonId, x, y, w, h, shaderOption, text);
        this.sliderValue = 1.0F;
        this.shaderOption = shaderOption;
        this.sliderValue = shaderOption.getIndexNormalized();
        this.setMessage(GuiShaderOptions.getButtonText(shaderOption, this.width));
    }

    protected int getYImage(boolean p_getYImage_1_)
    {
        return 0;
    }

    protected void renderBg(MatrixStack matrixStackIn, Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            if (this.dragging && !Screen.hasShiftDown())
            {
                this.sliderValue = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
                this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
                this.shaderOption.setIndexNormalized(this.sliderValue);
                this.sliderValue = this.shaderOption.getIndexNormalized();
                this.setMessage(GuiShaderOptions.getButtonText(this.shaderOption, this.width));
            }

            mc.getTextureManager().bindTexture(WIDGETS_LOCATION);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = (this.isHovered() ? 2 : 1) * 20;
            this.blit(matrixStackIn, this.x + (int)(this.sliderValue * (float)(this.width - 8)), this.y, 0, 46 + i, 4, 20);
            this.blit(matrixStackIn, this.x + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.y, 196, 46 + i, 4, 20);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (super.mouseClicked(mouseX, mouseY, button))
        {
            this.sliderValue = (float)(mouseX - (double)(this.x + 4)) / (float)(this.width - 8);
            this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
            this.shaderOption.setIndexNormalized(this.sliderValue);
            this.setMessage(GuiShaderOptions.getButtonText(this.shaderOption, this.width));
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        this.dragging = false;
        return true;
    }

    public void valueChanged()
    {
        this.sliderValue = this.shaderOption.getIndexNormalized();
    }

    public boolean isSwitchable()
    {
        return false;
    }
}
