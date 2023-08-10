package net.minecraft.inventory;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;

public class ItemStackHelper
{
    public static ItemStack getAndSplit(List<ItemStack> stacks, int index, int amount)
    {
        return index >= 0 && index < stacks.size() && !stacks.get(index).isEmpty() && amount > 0 ? stacks.get(index).split(amount) : ItemStack.EMPTY;
    }

    public static ItemStack getAndRemove(List<ItemStack> stacks, int index)
    {
        return index >= 0 && index < stacks.size() ? stacks.set(index, ItemStack.EMPTY) : ItemStack.EMPTY;
    }

    public static CompoundNBT saveAllItems(CompoundNBT tag, NonNullList<ItemStack> list)
    {
        return saveAllItems(tag, list, true);
    }

    public static CompoundNBT saveAllItems(CompoundNBT tag, NonNullList<ItemStack> list, boolean saveEmpty)
    {
        ListNBT listnbt = new ListNBT();

        for (int i = 0; i < list.size(); ++i)
        {
            ItemStack itemstack = list.get(i);

            if (!itemstack.isEmpty())
            {
                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.putByte("Slot", (byte)i);
                itemstack.write(compoundnbt);
                listnbt.add(compoundnbt);
            }
        }

        if (!listnbt.isEmpty() || saveEmpty)
        {
            tag.put("Items", listnbt);
        }

        return tag;
    }

    public static void loadAllItems(CompoundNBT tag, NonNullList<ItemStack> list)
    {
        ListNBT listnbt = tag.getList("Items", 10);

        for (int i = 0; i < listnbt.size(); ++i)
        {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            int j = compoundnbt.getByte("Slot") & 255;

            if (j >= 0 && j < list.size())
            {
                list.set(j, ItemStack.read(compoundnbt));
            }
        }
    }

    public static int func_233534_a_(IInventory p_233534_0_, Predicate<ItemStack> p_233534_1_, int p_233534_2_, boolean p_233534_3_)
    {
        int i = 0;

        for (int j = 0; j < p_233534_0_.getSizeInventory(); ++j)
        {
            ItemStack itemstack = p_233534_0_.getStackInSlot(j);
            int k = func_233535_a_(itemstack, p_233534_1_, p_233534_2_ - i, p_233534_3_);

            if (k > 0 && !p_233534_3_ && itemstack.isEmpty())
            {
                p_233534_0_.setInventorySlotContents(j, ItemStack.EMPTY);
            }

            i += k;
        }

        return i;
    }

    public static int func_233535_a_(ItemStack p_233535_0_, Predicate<ItemStack> p_233535_1_, int p_233535_2_, boolean p_233535_3_)
    {
        if (!p_233535_0_.isEmpty() && p_233535_1_.test(p_233535_0_))
        {
            if (p_233535_3_)
            {
                return p_233535_0_.getCount();
            }
            else
            {
                int i = p_233535_2_ < 0 ? p_233535_0_.getCount() : Math.min(p_233535_2_, p_233535_0_.getCount());
                p_233535_0_.shrink(i);
                return i;
            }
        }
        else
        {
            return 0;
        }
    }
}
