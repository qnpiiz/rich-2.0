package net.minecraft.client.gui.widget.list;

import net.minecraft.client.Minecraft;

public abstract class ExtendedList<E extends AbstractList.AbstractListEntry<E>> extends AbstractList<E>
{
    private boolean field_230698_a_;

    public ExtendedList(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
    {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
    }

    public boolean changeFocus(boolean focus)
    {
        if (!this.field_230698_a_ && this.getItemCount() == 0)
        {
            return false;
        }
        else
        {
            this.field_230698_a_ = !this.field_230698_a_;

            if (this.field_230698_a_ && this.getSelected() == null && this.getItemCount() > 0)
            {
                this.moveSelection(AbstractList.Ordering.DOWN);
            }
            else if (this.field_230698_a_ && this.getSelected() != null)
            {
                this.func_241574_n_();
            }

            return this.field_230698_a_;
        }
    }

    public abstract static class AbstractListEntry<E extends ExtendedList.AbstractListEntry<E>> extends AbstractList.AbstractListEntry<E>
    {
        public boolean changeFocus(boolean focus)
        {
            return false;
        }
    }
}
