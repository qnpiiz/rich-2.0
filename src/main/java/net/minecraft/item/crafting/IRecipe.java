package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IRecipe<C extends IInventory>
{
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    boolean matches(C inv, World worldIn);

    /**
     * Returns an Item that is the result of this recipe
     */
    ItemStack getCraftingResult(C inv);

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    boolean canFit(int width, int height);

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    ItemStack getRecipeOutput();

default NonNullList<ItemStack> getRemainingItems(C inv)
    {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i)
        {
            Item item = inv.getStackInSlot(i).getItem();

            if (item.hasContainerItem())
            {
                nonnulllist.set(i, new ItemStack(item.getContainerItem()));
            }
        }

        return nonnulllist;
    }

default NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.create();
    }

default boolean isDynamic()
    {
        return false;
    }

default String getGroup()
    {
        return "";
    }

default ItemStack getIcon()
    {
        return new ItemStack(Blocks.CRAFTING_TABLE);
    }

    ResourceLocation getId();

    IRecipeSerializer<?> getSerializer();

    IRecipeType<?> getType();
}
