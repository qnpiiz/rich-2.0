package net.optifine.gui;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class GuiButtonOF extends Button
{
    public final int id;

    public GuiButtonOF(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, Button.IPressable pressable)
    {
        super(x, y, widthIn, heightIn, new StringTextComponent(buttonText), pressable);
        this.id = buttonId;
    }

    public GuiButtonOF(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText)
    {
        this(buttonId, x, y, widthIn, heightIn, buttonText, (btn) ->
        {
        });
    }

    public GuiButtonOF(int buttonId, int x, int y, String buttonText)
    {
        this(buttonId, x, y, 200, 20, buttonText, (btn) ->
        {
        });
    }

    public void setMessage(String messageIn)
    {
        super.setMessage(new StringTextComponent(messageIn));
    }
}
