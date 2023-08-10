package net.minecraft.inventory.container;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class LoomContainer extends Container
{
    private final IWorldPosCallable worldPos;
    private final IntReferenceHolder field_217034_d = IntReferenceHolder.single();
    private Runnable runnable = () ->
    {
    };
    private final Slot slotBanner;
    private final Slot slotDye;
    private final Slot slotPattern;
    private final Slot output;
    private long field_226622_j_;
    private final IInventory inputInventory = new Inventory(3)
    {
        public void markDirty()
        {
            super.markDirty();
            LoomContainer.this.onCraftMatrixChanged(this);
            LoomContainer.this.runnable.run();
        }
    };
    private final IInventory outputInventory = new Inventory(1)
    {
        public void markDirty()
        {
            super.markDirty();
            LoomContainer.this.runnable.run();
        }
    };

    public LoomContainer(int id, PlayerInventory playerInventory)
    {
        this(id, playerInventory, IWorldPosCallable.DUMMY);
    }

    public LoomContainer(int id, PlayerInventory playerInventory, final IWorldPosCallable worldCallable)
    {
        super(ContainerType.LOOM, id);
        this.worldPos = worldCallable;
        this.slotBanner = this.addSlot(new Slot(this.inputInventory, 0, 13, 26)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() instanceof BannerItem;
            }
        });
        this.slotDye = this.addSlot(new Slot(this.inputInventory, 1, 33, 26)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() instanceof DyeItem;
            }
        });
        this.slotPattern = this.addSlot(new Slot(this.inputInventory, 2, 23, 45)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() instanceof BannerPatternItem;
            }
        });
        this.output = this.addSlot(new Slot(this.outputInventory, 0, 143, 58)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return false;
            }
            public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack)
            {
                LoomContainer.this.slotBanner.decrStackSize(1);
                LoomContainer.this.slotDye.decrStackSize(1);

                if (!LoomContainer.this.slotBanner.getHasStack() || !LoomContainer.this.slotDye.getHasStack())
                {
                    LoomContainer.this.field_217034_d.set(0);
                }

                worldCallable.consume((p_216951_1_, p_216951_2_) ->
                {
                    long l = p_216951_1_.getGameTime();

                    if (LoomContainer.this.field_226622_j_ != l)
                    {
                        p_216951_1_.playSound((PlayerEntity)null, p_216951_2_, SoundEvents.UI_LOOM_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        LoomContainer.this.field_226622_j_ = l;
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

        this.trackInt(this.field_217034_d);
    }

    public int func_217023_e()
    {
        return this.field_217034_d.get();
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return isWithinUsableDistance(this.worldPos, playerIn, Blocks.LOOM);
    }

    /**
     * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
     */
    public boolean enchantItem(PlayerEntity playerIn, int id)
    {
        if (id > 0 && id <= BannerPattern.PATTERN_ITEM_INDEX)
        {
            this.field_217034_d.set(id);
            this.createOutputStack();
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        ItemStack itemstack = this.slotBanner.getStack();
        ItemStack itemstack1 = this.slotDye.getStack();
        ItemStack itemstack2 = this.slotPattern.getStack();
        ItemStack itemstack3 = this.output.getStack();

        if (itemstack3.isEmpty() || !itemstack.isEmpty() && !itemstack1.isEmpty() && this.field_217034_d.get() > 0 && (this.field_217034_d.get() < BannerPattern.BANNER_PATTERNS_COUNT - BannerPattern.BANNERS_WITH_ITEMS || !itemstack2.isEmpty()))
        {
            if (!itemstack2.isEmpty() && itemstack2.getItem() instanceof BannerPatternItem)
            {
                CompoundNBT compoundnbt = itemstack.getOrCreateChildTag("BlockEntityTag");
                boolean flag = compoundnbt.contains("Patterns", 9) && !itemstack.isEmpty() && compoundnbt.getList("Patterns", 10).size() >= 6;

                if (flag)
                {
                    this.field_217034_d.set(0);
                }
                else
                {
                    this.field_217034_d.set(((BannerPatternItem)itemstack2.getItem()).getBannerPattern().ordinal());
                }
            }
        }
        else
        {
            this.output.putStack(ItemStack.EMPTY);
            this.field_217034_d.set(0);
        }

        this.createOutputStack();
        this.detectAndSendChanges();
    }

    public void func_217020_a(Runnable p_217020_1_)
    {
        this.runnable = p_217020_1_;
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

            if (index == this.output.slotNumber)
            {
                if (!this.mergeItemStack(itemstack1, 4, 40, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index != this.slotDye.slotNumber && index != this.slotBanner.slotNumber && index != this.slotPattern.slotNumber)
            {
                if (itemstack1.getItem() instanceof BannerItem)
                {
                    if (!this.mergeItemStack(itemstack1, this.slotBanner.slotNumber, this.slotBanner.slotNumber + 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (itemstack1.getItem() instanceof DyeItem)
                {
                    if (!this.mergeItemStack(itemstack1, this.slotDye.slotNumber, this.slotDye.slotNumber + 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (itemstack1.getItem() instanceof BannerPatternItem)
                {
                    if (!this.mergeItemStack(itemstack1, this.slotPattern.slotNumber, this.slotPattern.slotNumber + 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= 4 && index < 31)
                {
                    if (!this.mergeItemStack(itemstack1, 31, 40, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= 31 && index < 40 && !this.mergeItemStack(itemstack1, 4, 31, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 4, 40, false))
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

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);
        this.worldPos.consume((p_217028_2_, p_217028_3_) ->
        {
            this.clearContainer(playerIn, playerIn.world, this.inputInventory);
        });
    }

    /**
     * Creates an output banner ItemStack based on the patterns, dyes, etc. in the loom.
     */
    private void createOutputStack()
    {
        if (this.field_217034_d.get() > 0)
        {
            ItemStack itemstack = this.slotBanner.getStack();
            ItemStack itemstack1 = this.slotDye.getStack();
            ItemStack itemstack2 = ItemStack.EMPTY;

            if (!itemstack.isEmpty() && !itemstack1.isEmpty())
            {
                itemstack2 = itemstack.copy();
                itemstack2.setCount(1);
                BannerPattern bannerpattern = BannerPattern.values()[this.field_217034_d.get()];
                DyeColor dyecolor = ((DyeItem)itemstack1.getItem()).getDyeColor();
                CompoundNBT compoundnbt = itemstack2.getOrCreateChildTag("BlockEntityTag");
                ListNBT listnbt;

                if (compoundnbt.contains("Patterns", 9))
                {
                    listnbt = compoundnbt.getList("Patterns", 10);
                }
                else
                {
                    listnbt = new ListNBT();
                    compoundnbt.put("Patterns", listnbt);
                }

                CompoundNBT compoundnbt1 = new CompoundNBT();
                compoundnbt1.putString("Pattern", bannerpattern.getHashname());
                compoundnbt1.putInt("Color", dyecolor.getId());
                listnbt.add(compoundnbt1);
            }

            if (!ItemStack.areItemStacksEqual(itemstack2, this.output.getStack()))
            {
                this.output.putStack(itemstack2);
            }
        }
    }

    public Slot getBannerSlot()
    {
        return this.slotBanner;
    }

    public Slot getDyeSlot()
    {
        return this.slotDye;
    }

    public Slot getPatternSlot()
    {
        return this.slotPattern;
    }

    public Slot getOutputSlot()
    {
        return this.output;
    }
}
