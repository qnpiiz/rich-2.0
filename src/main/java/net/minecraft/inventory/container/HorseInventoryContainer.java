package net.minecraft.inventory.container;

import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class HorseInventoryContainer extends Container
{
    private final IInventory horseInventory;
    private final AbstractHorseEntity horse;

    public HorseInventoryContainer(int id, PlayerInventory playerInventory, IInventory horseInventory, final AbstractHorseEntity horse)
    {
        super((ContainerType<?>)null, id);
        this.horseInventory = horseInventory;
        this.horse = horse;
        int i = 3;
        horseInventory.openInventory(playerInventory.player);
        int j = -18;
        this.addSlot(new Slot(horseInventory, 0, 8, 18)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() == Items.SADDLE && !this.getHasStack() && horse.func_230264_L__();
            }
            public boolean isEnabled()
            {
                return horse.func_230264_L__();
            }
        });
        this.addSlot(new Slot(horseInventory, 1, 8, 36)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return horse.isArmor(stack);
            }
            public boolean isEnabled()
            {
                return horse.func_230276_fq_();
            }
            public int getSlotStackLimit()
            {
                return 1;
            }
        });

        if (horse instanceof AbstractChestedHorseEntity && ((AbstractChestedHorseEntity)horse).hasChest())
        {
            for (int k = 0; k < 3; ++k)
            {
                for (int l = 0; l < ((AbstractChestedHorseEntity)horse).getInventoryColumns(); ++l)
                {
                    this.addSlot(new Slot(horseInventory, 2 + l + k * ((AbstractChestedHorseEntity)horse).getInventoryColumns(), 80 + l * 18, 18 + k * 18));
                }
            }
        }

        for (int i1 = 0; i1 < 3; ++i1)
        {
            for (int k1 = 0; k1 < 9; ++k1)
            {
                this.addSlot(new Slot(playerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
            }
        }

        for (int j1 = 0; j1 < 9; ++j1)
        {
            this.addSlot(new Slot(playerInventory, j1, 8 + j1 * 18, 142));
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return this.horseInventory.isUsableByPlayer(playerIn) && this.horse.isAlive() && this.horse.getDistance(playerIn) < 8.0F;
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
            int i = this.horseInventory.getSizeInventory();

            if (index < i)
            {
                if (!this.mergeItemStack(itemstack1, i, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.getSlot(1).isItemValid(itemstack1) && !this.getSlot(1).getHasStack())
            {
                if (!this.mergeItemStack(itemstack1, 1, 2, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.getSlot(0).isItemValid(itemstack1))
            {
                if (!this.mergeItemStack(itemstack1, 0, 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (i <= 2 || !this.mergeItemStack(itemstack1, 2, i, false))
            {
                int j = i + 27;
                int k = j + 9;

                if (index >= j && index < k)
                {
                    if (!this.mergeItemStack(itemstack1, i, j, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= i && index < j)
                {
                    if (!this.mergeItemStack(itemstack1, j, k, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (!this.mergeItemStack(itemstack1, j, j, false))
                {
                    return ItemStack.EMPTY;
                }

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
        }

        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);
        this.horseInventory.closeInventory(playerIn);
    }
}
