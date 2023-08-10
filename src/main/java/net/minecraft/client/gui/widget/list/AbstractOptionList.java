package net.minecraft.client.gui.widget.list;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;

public abstract class AbstractOptionList<E extends AbstractOptionList.Entry<E>> extends AbstractList<E>
{
    public AbstractOptionList(Minecraft p_i51139_1_, int p_i51139_2_, int p_i51139_3_, int p_i51139_4_, int p_i51139_5_, int p_i51139_6_)
    {
        super(p_i51139_1_, p_i51139_2_, p_i51139_3_, p_i51139_4_, p_i51139_5_, p_i51139_6_);
    }

    public boolean changeFocus(boolean focus)
    {
        boolean flag = super.changeFocus(focus);

        if (flag)
        {
            this.ensureVisible(this.getListener());
        }

        return flag;
    }

    protected boolean isSelectedItem(int index)
    {
        return false;
    }

    public abstract static class Entry<E extends AbstractOptionList.Entry<E>> extends AbstractList.AbstractListEntry<E> implements INestedGuiEventHandler
    {
        @Nullable
        private IGuiEventListener field_214380_a;
        private boolean field_214381_b;

        public boolean isDragging()
        {
            return this.field_214381_b;
        }

        public void setDragging(boolean dragging)
        {
            this.field_214381_b = dragging;
        }

        public void setListener(@Nullable IGuiEventListener listener)
        {
            this.field_214380_a = listener;
        }

        @Nullable
        public IGuiEventListener getListener()
        {
            return this.field_214380_a;
        }
    }
}
