package net.minecraft.client.gui;

import javax.annotation.Nullable;

public abstract class FocusableGui extends AbstractGui implements INestedGuiEventHandler
{
    @Nullable
    private IGuiEventListener field_230699_a_;
    private boolean isDragging;

    public final boolean isDragging()
    {
        return this.isDragging;
    }

    public final void setDragging(boolean dragging)
    {
        this.isDragging = dragging;
    }

    @Nullable
    public IGuiEventListener getListener()
    {
        return this.field_230699_a_;
    }

    public void setListener(@Nullable IGuiEventListener listener)
    {
        this.field_230699_a_ = listener;
    }
}
