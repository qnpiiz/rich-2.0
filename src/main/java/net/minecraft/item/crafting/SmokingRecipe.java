package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SmokingRecipe extends AbstractCookingRecipe
{
    public SmokingRecipe(ResourceLocation id, String group, Ingredient ingredient, ItemStack result, float experience, int cookTime)
    {
        super(IRecipeType.SMOKING, id, group, ingredient, result, experience, cookTime);
    }

    public ItemStack getIcon()
    {
        return new ItemStack(Blocks.SMOKER);
    }

    public IRecipeSerializer<?> getSerializer()
    {
        return IRecipeSerializer.SMOKING;
    }
}
