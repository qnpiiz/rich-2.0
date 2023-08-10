package net.minecraft.realms;

import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.ExtendedList;

public abstract class RealmsObjectSelectionList<E extends ExtendedList.AbstractListEntry<E>> extends ExtendedList<E>
{
    protected RealmsObjectSelectionList(int p_i50516_1_, int p_i50516_2_, int p_i50516_3_, int p_i50516_4_, int p_i50516_5_)
    {
        super(Minecraft.getInstance(), p_i50516_1_, p_i50516_2_, p_i50516_3_, p_i50516_4_, p_i50516_5_);
    }

    public void func_239561_k_(int p_239561_1_)
    {
        if (p_239561_1_ == -1)
        {
            this.setSelected((E)null);
        }
        else if (super.getItemCount() != 0)
        {
            this.setSelected(this.getEntry(p_239561_1_));
        }
    }

    public void func_231400_a_(int p_231400_1_)
    {
        this.func_239561_k_(p_231400_1_);
    }

    public void func_231401_a_(int p_231401_1_, int p_231401_2_, double p_231401_3_, double p_231401_5_, int p_231401_7_)
    {
    }

    public int getMaxPosition()
    {
        return 0;
    }

    public int getScrollbarPosition()
    {
        return this.getRowLeft() + this.getRowWidth();
    }

    public int getRowWidth()
    {
        return (int)((double)this.width * 0.6D);
    }

    public void replaceEntries(Collection<E> entries)
    {
        super.replaceEntries(entries);
    }

    public int getItemCount()
    {
        return super.getItemCount();
    }

    public int getRowTop(int p_230962_1_)
    {
        return super.getRowTop(p_230962_1_);
    }

    public int getRowLeft()
    {
        return super.getRowLeft();
    }

    public int addEntry(E entry)
    {
        return super.addEntry(entry);
    }

    public void func_231409_q_()
    {
        this.clearEntries();
    }
}
