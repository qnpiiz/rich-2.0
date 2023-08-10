package net.minecraft.client.gui.widget.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractButton extends Widget
{
    public AbstractButton(int x, int y, int width, int height, ITextComponent title)
    {
        super(x, y, width, height, title);
    }

    public abstract void onPress();

    public void onClick(double mouseX, double mouseY)
    {
        this.onPress();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.active && this.visible)
        {
            if (keyCode != 257 && keyCode != 32 && keyCode != 335)
            {
                return false;
            }
            else
            {
                this.playDownSound(Minecraft.getInstance().getSoundHandler());
                this.onPress();
                return true;
            }
        }
        else
        {
            return false;
        }
    }
}
