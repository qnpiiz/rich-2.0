package net.minecraft.inventory.container;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.storage.MapData;

public class CartographyContainer extends Container
{
    private final IWorldPosCallable worldPosCallable;
    private long field_226605_f_;
    public final IInventory tableInventory = new Inventory(2)
    {
        public void markDirty()
        {
            CartographyContainer.this.onCraftMatrixChanged(this);
            super.markDirty();
        }
    };
    private final CraftResultInventory field_217001_f = new CraftResultInventory()
    {
        public void markDirty()
        {
            CartographyContainer.this.onCraftMatrixChanged(this);
            super.markDirty();
        }
    };

    public CartographyContainer(int id, PlayerInventory playerInventory)
    {
        this(id, playerInventory, IWorldPosCallable.DUMMY);
    }

    public CartographyContainer(int id, PlayerInventory playerInventory, final IWorldPosCallable worldPosCallable)
    {
        super(ContainerType.CARTOGRAPHY_TABLE, id);
        this.worldPosCallable = worldPosCallable;
        this.addSlot(new Slot(this.tableInventory, 0, 15, 15)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() == Items.FILLED_MAP;
            }
        });
        this.addSlot(new Slot(this.tableInventory, 1, 15, 52)
        {
            public boolean isItemValid(ItemStack stack)
            {
                Item item = stack.getItem();
                return item == Items.PAPER || item == Items.MAP || item == Items.GLASS_PANE;
            }
        });
        this.addSlot(new Slot(this.field_217001_f, 2, 145, 39)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return false;
            }
            public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack)
            {
                CartographyContainer.this.inventorySlots.get(0).decrStackSize(1);
                CartographyContainer.this.inventorySlots.get(1).decrStackSize(1);
                stack.getItem().onCreated(stack, thePlayer.world, thePlayer);
                worldPosCallable.consume((p_242385_1_, p_242385_2_) ->
                {
                    long l = p_242385_1_.getGameTime();

                    if (CartographyContainer.this.field_226605_f_ != l)
                    {
                        p_242385_1_.playSound((PlayerEntity)null, p_242385_2_, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        CartographyContainer.this.field_226605_f_ = l;
                    }
                });
                return super.onTake(thePlayer, stack);
            }
        });

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return isWithinUsableDistance(this.worldPosCallable, playerIn, Blocks.CARTOGRAPHY_TABLE);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        ItemStack itemstack = this.tableInventory.getStackInSlot(0);
        ItemStack itemstack1 = this.tableInventory.getStackInSlot(1);
        ItemStack itemstack2 = this.field_217001_f.getStackInSlot(2);

        if (itemstack2.isEmpty() || !itemstack.isEmpty() && !itemstack1.isEmpty())
        {
            if (!itemstack.isEmpty() && !itemstack1.isEmpty())
            {
                this.func_216993_a(itemstack, itemstack1, itemstack2);
            }
        }
        else
        {
            this.field_217001_f.removeStackFromSlot(2);
        }
    }

    private void func_216993_a(ItemStack stack, ItemStack p_216993_2_, ItemStack p_216993_3_)
    {
        this.worldPosCallable.consume((p_216996_4_, p_216996_5_) ->
        {
            Item item = p_216993_2_.getItem();
            MapData mapdata = FilledMapItem.getData(stack, p_216996_4_);

            if (mapdata != null)
            {
                ItemStack itemstack;

                if (item == Items.PAPER && !mapdata.locked && mapdata.scale < 4)
                {
                    itemstack = stack.copy();
                    itemstack.setCount(1);
                    itemstack.getOrCreateTag().putInt("map_scale_direction", 1);
                    this.detectAndSendChanges();
                }
                else if (item == Items.GLASS_PANE && !mapdata.locked)
                {
                    itemstack = stack.copy();
                    itemstack.setCount(1);
                    itemstack.getOrCreateTag().putBoolean("map_to_lock", true);
                    this.detectAndSendChanges();
                }
                else
                {
                    if (item != Items.MAP)
                    {
                        this.field_217001_f.removeStackFromSlot(2);
                        this.detectAndSendChanges();
                        return;
                    }

                    itemstack = stack.copy();
                    itemstack.setCount(2);
                    this.detectAndSendChanges();
                }

                if (!ItemStack.areItemStacksEqual(itemstack, p_216993_3_))
                {
                    this.field_217001_f.setInventorySlotContents(2, itemstack);
                    this.detectAndSendChanges();
                }
            }
        });
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != this.field_217001_f && super.canMergeSlot(stack, slotIn);
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
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();

            if (index == 2)
            {
                item.onCreated(itemstack1, playerIn.world, playerIn);

                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index != 1 && index != 0)
            {
                if (item == Items.FILLED_MAP)
                {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (item != Items.PAPER && item != Items.MAP && item != Items.GLASS_PANE)
                {
                    if (index >= 3 && index < 30)
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
                else if (!this.mergeItemStack(itemstack1, 1, 2, false))
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

            slot.onSlotChanged();

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
            this.detectAndSendChanges();
        }

        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);
        this.field_217001_f.removeStackFromSlot(2);
        this.worldPosCallable.consume((p_216995_2_, p_216995_3_) ->
        {
            this.clearContainer(playerIn, playerIn.world, this.tableInventory);
        });
    }
}
