package net.minecraft.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class CheckboxButton extends AbstractButton
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
    private boolean checked;
    private final boolean field_238499_c_;

    public CheckboxButton(int x, int y, int width, int height, ITextComponent title, boolean checked)
    {
        this(x, y, width, height, title, checked, true);
    }

    public CheckboxButton(int p_i232258_1_, int p_i232258_2_, int p_i232258_3_, int p_i232258_4_, ITextComponent p_i232258_5_, boolean p_i232258_6_, boolean drawTitle)
    {
        super(p_i232258_1_, p_i232258_2_, p_i232258_3_, p_i232258_4_, p_i232258_5_);
        this.checked = p_i232258_6_;
        this.field_238499_c_ = drawTitle;
    }

    public void onPress()
    {
        this.checked = !this.checked;
    }

    public boolean isChecked()
    {
        return this.checked;
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(TEXTURE);
        RenderSystem.enableDepthTest();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(matrixStack, this.x, this.y, this.isFocused() ? 20.0F : 0.0F, this.checked ? 20.0F : 0.0F, 20, this.height, 64, 64);
        this.renderBg(matrixStack, minecraft, mouseX, mouseY);

        if (this.field_238499_c_)
        {
            drawString(matrixStack, fontrenderer, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }
}
