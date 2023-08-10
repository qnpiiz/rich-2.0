package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.realms.RealmsObjectSelectionList;

public abstract class ListButton
{
    public final int field_225125_a;
    public final int field_225126_b;
    public final int field_225127_c;
    public final int field_225128_d;

    public ListButton(int p_i51779_1_, int p_i51779_2_, int p_i51779_3_, int p_i51779_4_)
    {
        this.field_225125_a = p_i51779_1_;
        this.field_225126_b = p_i51779_2_;
        this.field_225127_c = p_i51779_3_;
        this.field_225128_d = p_i51779_4_;
    }

    public void func_237726_a_(MatrixStack p_237726_1_, int p_237726_2_, int p_237726_3_, int p_237726_4_, int p_237726_5_)
    {
        int i = p_237726_2_ + this.field_225127_c;
        int j = p_237726_3_ + this.field_225128_d;
        boolean flag = false;

        if (p_237726_4_ >= i && p_237726_4_ <= i + this.field_225125_a && p_237726_5_ >= j && p_237726_5_ <= j + this.field_225126_b)
        {
            flag = true;
        }

        this.func_230435_a_(p_237726_1_, i, j, flag);
    }

    protected abstract void func_230435_a_(MatrixStack p_230435_1_, int p_230435_2_, int p_230435_3_, boolean p_230435_4_);

    public int func_225122_a()
    {
        return this.field_225127_c + this.field_225125_a;
    }

    public int func_225123_b()
    {
        return this.field_225128_d + this.field_225126_b;
    }

    public abstract void func_225121_a(int p_225121_1_);

    public static void func_237727_a_(MatrixStack p_237727_0_, List<ListButton> p_237727_1_, RealmsObjectSelectionList<?> p_237727_2_, int p_237727_3_, int p_237727_4_, int p_237727_5_, int p_237727_6_)
    {
        for (ListButton listbutton : p_237727_1_)
        {
            if (p_237727_2_.getRowWidth() > listbutton.func_225122_a())
            {
                listbutton.func_237726_a_(p_237727_0_, p_237727_3_, p_237727_4_, p_237727_5_, p_237727_6_);
            }
        }
    }

    public static void func_237728_a_(RealmsObjectSelectionList<?> p_237728_0_, ExtendedList.AbstractListEntry<?> p_237728_1_, List<ListButton> p_237728_2_, int p_237728_3_, double p_237728_4_, double p_237728_6_)
    {
        if (p_237728_3_ == 0)
        {
            int i = p_237728_0_.getEventListeners().indexOf(p_237728_1_);

            if (i > -1)
            {
                p_237728_0_.func_231400_a_(i);
                int j = p_237728_0_.getRowLeft();
                int k = p_237728_0_.getRowTop(i);
                int l = (int)(p_237728_4_ - (double)j);
                int i1 = (int)(p_237728_6_ - (double)k);

                for (ListButton listbutton : p_237728_2_)
                {
                    if (l >= listbutton.field_225127_c && l <= listbutton.func_225122_a() && i1 >= listbutton.field_225128_d && i1 <= listbutton.func_225123_b())
                    {
                        listbutton.func_225121_a(i);
                    }
                }
            }
        }
    }
}
