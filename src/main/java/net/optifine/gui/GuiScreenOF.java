package net.optifine.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.optifine.util.GuiUtils;

public class GuiScreenOF extends Screen
{
    protected List<Widget> buttonList = this.buttons;
    protected FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    protected boolean mousePressed = false;

    public GuiScreenOF(ITextComponent title)
    {
        super(title);
    }

    protected void actionPerformed(Widget button)
    {
    }

    protected void actionPerformedRightClick(Widget button)
    {
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        boolean flag = super.mouseClicked(mouseX, mouseY, mouseButton);
        this.mousePressed = true;
        Widget widget = getSelectedButton((int)mouseX, (int)mouseY, this.buttonList);

        if (widget != null && widget.active)
        {
            if (mouseButton == 1 && widget instanceof IOptionControl)
            {
                IOptionControl ioptioncontrol = (IOptionControl)widget;

                if (ioptioncontrol.getControlOption() == AbstractOption.GUI_SCALE)
                {
                    widget.playDownSound(super.mc.getSoundHandler());
                }
            }

            if (mouseButton == 0)
            {
                this.actionPerformed(widget);
            }
            else if (mouseButton == 1)
            {
                this.actionPerformedRightClick(widget);
            }

            return true;
        }
        else
        {
            return flag;
        }
    }

    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_)
    {
        if (!this.mousePressed)
        {
            return false;
        }
        else
        {
            this.mousePressed = false;
            this.setDragging(false);
            return this.getListener() != null && this.getListener().mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_) ? true : super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
        }
    }

    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_)
    {
        return !this.mousePressed ? false : super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
    }

    public static Widget getSelectedButton(int x, int y, List<Widget> listButtons)
    {
        for (int i = 0; i < listButtons.size(); ++i)
        {
            Widget widget = listButtons.get(i);

            if (widget.visible)
            {
                int j = GuiUtils.getWidth(widget);
                int k = GuiUtils.getHeight(widget);

                if (x >= widget.x && y >= widget.y && x < widget.x + j && y < widget.y + k)
                {
                    return widget;
                }
            }
        }

        return null;
    }

    public static void drawCenteredString(MatrixStack matrixStackIn, FontRenderer fontRendererIn, IReorderingProcessor textIn, int xIn, int yIn, int colorIn)
    {
        fontRendererIn.func_238407_a_(matrixStackIn, textIn, (float)(xIn - fontRendererIn.func_243245_a(textIn) / 2), (float)yIn, colorIn);
    }
}
