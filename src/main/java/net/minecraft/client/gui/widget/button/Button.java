package net.minecraft.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

public class Button extends AbstractButton
{
    public static final Button.ITooltip field_238486_s_ = (button, matrixStack, mouseX, mouseY) ->
    {
    };
    protected final Button.IPressable onPress;
    protected final Button.ITooltip onTooltip;

    public Button(int x, int y, int width, int height, ITextComponent title, Button.IPressable pressedAction)
    {
        this(x, y, width, height, title, pressedAction, field_238486_s_);
    }

    public Button(int x, int y, int width, int height, ITextComponent title, Button.IPressable pressedAction, Button.ITooltip onTooltip)
    {
        super(x, y, width, height, title);
        this.onPress = pressedAction;
        this.onTooltip = onTooltip;
    }

    public void onPress()
    {
        this.onPress.onPress(this);
    }

    private int fade = 20;

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        /*super.renderButton(matrixStack, mouseX, mouseY, partialTicks);

        if (this.isHovered())
        {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }*/

        if (visible) {
            Color text = new Color(255, 255, 255, 255);
            Color color = new Color(this.fade + 14, this.fade + 14, this.fade + 14, 100);
            if (isHovered) {
                if (this.fade < 100)
                    this.fade += 8;

                text = Color.white;
            } else {
                if (this.fade > 20)
                    this.fade -= 8;
            }

            fill(matrixStack, x, y, x+width, y+height, new Color(0, 0, 0, 120).getRGB());
            fill(matrixStack, x, y, x+width, y+height, color.getRGB());
            Minecraft.getInstance().rubik_18.drawCenteredStringWithShadow(getMessage().getString(), this.x + this.width / 2F, this.y + (this.height - 4) / 2, text.getRGB(), matrixStack);
        }
    }

    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        this.onTooltip.onTooltip(this, matrixStack, mouseX, mouseY);
    }

    public interface IPressable
    {
        void onPress(Button p_onPress_1_);
    }

    public interface ITooltip
    {
        void onTooltip(Button p_onTooltip_1_, MatrixStack p_onTooltip_2_, int p_onTooltip_3_, int p_onTooltip_4_);
    }
}
