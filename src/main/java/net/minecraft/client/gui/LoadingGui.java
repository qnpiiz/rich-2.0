package net.minecraft.client.gui;

public abstract class LoadingGui extends AbstractGui implements IRenderable
{
    public boolean isPauseScreen()
    {
        return true;
    }
}
