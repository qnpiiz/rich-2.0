package net.minecraft.client.gui;

public interface IGuiEventListener
{
default void mouseMoved(double xPos, double mouseY)
    {
    }

default boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return false;
    }

default boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        return false;
    }

default boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        return false;
    }

default boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        return false;
    }

default boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return false;
    }

default boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        return false;
    }

default boolean charTyped(char codePoint, int modifiers)
    {
        return false;
    }

default boolean changeFocus(boolean focus)
    {
        return false;
    }

default boolean isMouseOver(double mouseX, double mouseY)
    {
        return false;
    }
}
