package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.util.IIntArray;

public class SmokerContainer extends AbstractFurnaceContainer
{
    public SmokerContainer(int id, PlayerInventory playerInventory)
    {
        super(ContainerType.SMOKER, IRecipeType.SMOKING, RecipeBookCategory.SMOKER, id, playerInventory);
    }

    public SmokerContainer(int id, PlayerInventory playerInventory, IInventory inventory, IIntArray p_i50062_4_)
    {
        super(ContainerType.SMOKER, IRecipeType.SMOKING, RecipeBookCategory.SMOKER, id, playerInventory, inventory, p_i50062_4_);
    }
}
