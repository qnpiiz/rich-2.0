package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BlastingRecipe extends AbstractCookingRecipe
{
    public BlastingRecipe(ResourceLocation id, String group, Ingredient ingredient, ItemStack result, float experience, int cookTime)
    {
        super(IRecipeType.BLASTING, id, group, ingredient, result, experience, cookTime);
    }

    public ItemStack getIcon()
    {
        return new ItemStack(Blocks.BLAST_FURNACE);
    }

    public IRecipeSerializer<?> getSerializer()
    {
        return IRecipeSerializer.BLASTING;
    }
}
