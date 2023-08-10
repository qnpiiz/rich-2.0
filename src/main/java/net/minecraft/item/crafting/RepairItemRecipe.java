package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class RepairItemRecipe extends SpecialRecipe
{
    public RepairItemRecipe(ResourceLocation idIn)
    {
        super(idIn);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(CraftingInventory inv, World worldIn)
    {
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                list.add(itemstack);

                if (list.size() > 1)
                {
                    ItemStack itemstack1 = list.get(0);

                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !itemstack1.getItem().isDamageable())
                    {
                        return false;
                    }
                }
            }
        }

        return list.size() == 2;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(CraftingInventory inv)
    {
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                list.add(itemstack);

                if (list.size() > 1)
                {
                    ItemStack itemstack1 = list.get(0);

                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !itemstack1.getItem().isDamageable())
                    {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if (list.size() == 2)
        {
            ItemStack itemstack3 = list.get(0);
            ItemStack itemstack4 = list.get(1);

            if (itemstack3.getItem() == itemstack4.getItem() && itemstack3.getCount() == 1 && itemstack4.getCount() == 1 && itemstack3.getItem().isDamageable())
            {
                Item item = itemstack3.getItem();
                int j = item.getMaxDamage() - itemstack3.getDamage();
                int k = item.getMaxDamage() - itemstack4.getDamage();
                int l = j + k + item.getMaxDamage() * 5 / 100;
                int i1 = item.getMaxDamage() - l;

                if (i1 < 0)
                {
                    i1 = 0;
                }

                ItemStack itemstack2 = new ItemStack(itemstack3.getItem());
                itemstack2.setDamage(i1);
                Map<Enchantment, Integer> map = Maps.newHashMap();
                Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack3);
                Map<Enchantment, Integer> map2 = EnchantmentHelper.getEnchantments(itemstack4);
                Registry.ENCHANTMENT.stream().filter(Enchantment::isCurse).forEach((curse) ->
                {
                    int j1 = Math.max(map1.getOrDefault(curse, 0), map2.getOrDefault(curse, 0));

                    if (j1 > 0)
                    {
                        map.put(curse, j1);
                    }
                });

                if (!map.isEmpty())
                {
                    EnchantmentHelper.setEnchantments(map, itemstack2);
                }

                return itemstack2;
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canFit(int width, int height)
    {
        return width * height >= 2;
    }

    public IRecipeSerializer<?> getSerializer()
    {
        return IRecipeSerializer.CRAFTING_SPECIAL_REPAIRITEM;
    }
}
