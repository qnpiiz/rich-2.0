package net.minecraft.inventory.container;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

public abstract class AbstractRepairContainer extends Container
{
    protected final CraftResultInventory field_234642_c_ = new CraftResultInventory();
    protected final IInventory field_234643_d_ = new Inventory(2)
    {
        public void markDirty()
        {
            super.markDirty();
            AbstractRepairContainer.this.onCraftMatrixChanged(this);
        }
    };
    protected final IWorldPosCallable field_234644_e_;
    protected final PlayerEntity field_234645_f_;

    protected abstract boolean func_230303_b_(PlayerEntity p_230303_1_, boolean p_230303_2_);

    protected abstract ItemStack func_230301_a_(PlayerEntity p_230301_1_, ItemStack p_230301_2_);

    protected abstract boolean func_230302_a_(BlockState p_230302_1_);

    public AbstractRepairContainer(@Nullable ContainerType<?> p_i231587_1_, int p_i231587_2_, PlayerInventory p_i231587_3_, IWorldPosCallable p_i231587_4_)
    {
        super(p_i231587_1_, p_i231587_2_);
        this.field_234644_e_ = p_i231587_4_;
        this.field_234645_f_ = p_i231587_3_.player;
        this.addSlot(new Slot(this.field_234643_d_, 0, 27, 47));
        this.addSlot(new Slot(this.field_234643_d_, 1, 76, 47));
        this.addSlot(new Slot(this.field_234642_c_, 2, 134, 47)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return false;
            }
            public boolean canTakeStack(PlayerEntity playerIn)
            {
                return AbstractRepairContainer.this.func_230303_b_(playerIn, this.getHasStack());
            }
            public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack)
            {
                return AbstractRepairContainer.this.func_230301_a_(thePlayer, stack);
            }
        });

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(p_i231587_3_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlot(new Slot(p_i231587_3_, k, 8 + k * 18, 142));
        }
    }

    /**
     * called when the Anvil Input Slot changes, calculates the new result and puts it in the output slot
     */
    public abstract void updateRepairOutput();

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        super.onCraftMatrixChanged(inventoryIn);

        if (inventoryIn == this.field_234643_d_)
        {
            this.updateRepairOutput();
        }
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);
        this.field_234644_e_.consume((p_234647_2_, p_234647_3_) ->
        {
            this.clearContainer(playerIn, p_234647_2_, this.field_234643_d_);
        });
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return this.field_234644_e_.applyOrElse((p_234646_2_, p_234646_3_) ->
        {
            return !this.func_230302_a_(p_234646_2_.getBlockState(p_234646_3_)) ? false : playerIn.getDistanceSq((double)p_234646_3_.getX() + 0.5D, (double)p_234646_3_.getY() + 0.5D, (double)p_234646_3_.getZ() + 0.5D) <= 64.0D;
        }, true);
    }

    protected boolean func_241210_a_(ItemStack p_241210_1_)
    {
        return false;
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
            else if (index != 0 && index != 1)
            {
                if (index >= 3 && index < 39)
                {
                    int i = this.func_241210_a_(itemstack) ? 1 : 0;

                    if (!this.mergeItemStack(itemstack1, i, 2, false))
                    {
                        return ItemStack.EMPTY;
                    }
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
}
