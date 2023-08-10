package net.minecraft.client.renderer;

public interface IWindowEventListener
{
    void setGameFocused(boolean focused);

    void updateWindowSize();

    void ignoreFirstMove();
}
