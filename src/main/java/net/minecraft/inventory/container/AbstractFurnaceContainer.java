package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.item.crafting.ServerRecipePlacerFurnace;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.world.World;

public abstract class AbstractFurnaceContainer extends RecipeBookContainer<IInventory>
{
    private final IInventory furnaceInventory;
    private final IIntArray furnaceData;
    protected final World world;
    private final IRecipeType <? extends AbstractCookingRecipe > recipeType;
    private final RecipeBookCategory field_242384_g;

    protected AbstractFurnaceContainer(ContainerType<?> p_i241921_1_, IRecipeType <? extends AbstractCookingRecipe > p_i241921_2_, RecipeBookCategory p_i241921_3_, int p_i241921_4_, PlayerInventory p_i241921_5_)
    {
        this(p_i241921_1_, p_i241921_2_, p_i241921_3_, p_i241921_4_, p_i241921_5_, new Inventory(3), new IntArray(4));
    }

    protected AbstractFurnaceContainer(ContainerType<?> p_i241922_1_, IRecipeType <? extends AbstractCookingRecipe > p_i241922_2_, RecipeBookCategory p_i241922_3_, int p_i241922_4_, PlayerInventory p_i241922_5_, IInventory p_i241922_6_, IIntArray p_i241922_7_)
    {
        super(p_i241922_1_, p_i241922_4_);
        this.recipeType = p_i241922_2_;
        this.field_242384_g = p_i241922_3_;
        assertInventorySize(p_i241922_6_, 3);
        assertIntArraySize(p_i241922_7_, 4);
        this.furnaceInventory = p_i241922_6_;
        this.furnaceData = p_i241922_7_;
        this.world = p_i241922_5_.player.world;
        this.addSlot(new Slot(p_i241922_6_, 0, 56, 17));
        this.addSlot(new FurnaceFuelSlot(this, p_i241922_6_, 1, 56, 53));
        this.addSlot(new FurnaceResultSlot(p_i241922_5_.player, p_i241922_6_, 2, 116, 35));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(p_i241922_5_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlot(new Slot(p_i241922_5_, k, 8 + k * 18, 142));
        }

        this.trackIntArray(p_i241922_7_);
    }

    public void fillStackedContents(RecipeItemHelper itemHelperIn)
    {
        if (this.furnaceInventory instanceof IRecipeHelperPopulator)
        {
            ((IRecipeHelperPopulator)this.furnaceInventory).fillStackedContents(itemHelperIn);
        }
    }

    public void clear()
    {
        this.furnaceInventory.clear();
    }

    public void func_217056_a(boolean p_217056_1_, IRecipe<?> p_217056_2_, ServerPlayerEntity player)
    {
        (new ServerRecipePlacerFurnace<>(this)).place(player, (IRecipe<IInventory>) p_217056_2_, p_217056_1_);
    }

    public boolean matches(IRecipe <? super IInventory > recipeIn)
    {
        return recipeIn.matches(this.furnaceInventory, this.world);
    }

    public int getOutputSlot()
    {
        return 2;
    }

    public int getWidth()
    {
        return 1;
    }

    public int getHeight()
    {
        return 1;
    }

    public int getSize()
    {
        return 3;
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return this.furnaceInventory.isUsableByPlayer(playerIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 2)
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index != 1 && index != 0)
            {
                if (this.hasRecipe(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (this.isFuel(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= 3 && index < 30)
                {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    protected boolean hasRecipe(ItemStack stack)
    {
        return this.world.getRecipeManager().getRecipe((IRecipeType)this.recipeType, new Inventory(stack), this.world).isPresent();
    }

    protected boolean isFuel(ItemStack stack)
    {
        return AbstractFurnaceTileEntity.isFuel(stack);
    }

    public int getCookProgressionScaled()
    {
        int i = this.furnaceData.get(2);
        int j = this.furnaceData.get(3);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    public int getBurnLeftScaled()
    {
        int i = this.furnaceData.get(1);

        if (i == 0)
        {
            i = 200;
        }

        return this.furnaceData.get(0) * 13 / i;
    }

    public boolean isBurning()
    {
        return this.furnaceData.get(0) > 0;
    }

    public RecipeBookCategory func_241850_m()
    {
        return this.field_242384_g;
    }
}
