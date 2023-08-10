package net.minecraft.inventory.container;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public abstract class Container
{
    private final NonNullList<ItemStack> inventoryItemStacks = NonNullList.create();
    public final List<Slot> inventorySlots = Lists.newArrayList();
    private final List<IntReferenceHolder> trackedIntReferences = Lists.newArrayList();
    @Nullable
    private final ContainerType<?> containerType;
    public final int windowId;
    private short transactionID;
    private int dragMode = -1;
    private int dragEvent;
    private final Set<Slot> dragSlots = Sets.newHashSet();
    private final List<IContainerListener> listeners = Lists.newArrayList();
    private final Set<PlayerEntity> playerList = Sets.newHashSet();

    protected Container(@Nullable ContainerType<?> type, int id)
    {
        this.containerType = type;
        this.windowId = id;
    }

    protected static boolean isWithinUsableDistance(IWorldPosCallable worldPos, PlayerEntity playerIn, Block targetBlock)
    {
        return worldPos.applyOrElse((p_216960_2_, p_216960_3_) ->
        {
            return !p_216960_2_.getBlockState(p_216960_3_).isIn(targetBlock) ? false : playerIn.getDistanceSq((double)p_216960_3_.getX() + 0.5D, (double)p_216960_3_.getY() + 0.5D, (double)p_216960_3_.getZ() + 0.5D) <= 64.0D;
        }, true);
    }

    public ContainerType<?> getType()
    {
        if (this.containerType == null)
        {
            throw new UnsupportedOperationException("Unable to construct this menu by type");
        }
        else
        {
            return this.containerType;
        }
    }

    protected static void assertInventorySize(IInventory inventoryIn, int minSize)
    {
        int i = inventoryIn.getSizeInventory();

        if (i < minSize)
        {
            throw new IllegalArgumentException("Container size " + i + " is smaller than expected " + minSize);
        }
    }

    protected static void assertIntArraySize(IIntArray intArrayIn, int minSize)
    {
        int i = intArrayIn.size();

        if (i < minSize)
        {
            throw new IllegalArgumentException("Container data count " + i + " is smaller than expected " + minSize);
        }
    }

    /**
     * Adds an item slot to this container
     */
    protected Slot addSlot(Slot slotIn)
    {
        slotIn.slotNumber = this.inventorySlots.size();
        this.inventorySlots.add(slotIn);
        this.inventoryItemStacks.add(ItemStack.EMPTY);
        return slotIn;
    }

    protected IntReferenceHolder trackInt(IntReferenceHolder intIn)
    {
        this.trackedIntReferences.add(intIn);
        return intIn;
    }

    protected void trackIntArray(IIntArray arrayIn)
    {
        for (int i = 0; i < arrayIn.size(); ++i)
        {
            this.trackInt(IntReferenceHolder.create(arrayIn, i));
        }
    }

    public void addListener(IContainerListener listener)
    {
        if (!this.listeners.contains(listener))
        {
            this.listeners.add(listener);
            listener.sendAllContents(this, this.getInventory());
            this.detectAndSendChanges();
        }
    }

    /**
     * Remove the given Listener. Method name is for legacy.
     */
    public void removeListener(IContainerListener listener)
    {
        this.listeners.remove(listener);
    }

    public NonNullList<ItemStack> getInventory()
    {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();

        for (int i = 0; i < this.inventorySlots.size(); ++i)
        {
            nonnulllist.add(this.inventorySlots.get(i).getStack());
        }

        return nonnulllist;
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        for (int i = 0; i < this.inventorySlots.size(); ++i)
        {
            ItemStack itemstack = this.inventorySlots.get(i).getStack();
            ItemStack itemstack1 = this.inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack))
            {
                ItemStack itemstack2 = itemstack.copy();
                this.inventoryItemStacks.set(i, itemstack2);

                for (IContainerListener icontainerlistener : this.listeners)
                {
                    icontainerlistener.sendSlotContents(this, i, itemstack2);
                }
            }
        }

        for (int j = 0; j < this.trackedIntReferences.size(); ++j)
        {
            IntReferenceHolder intreferenceholder = this.trackedIntReferences.get(j);

            if (intreferenceholder.isDirty())
            {
                for (IContainerListener icontainerlistener1 : this.listeners)
                {
                    icontainerlistener1.sendWindowProperty(this, j, intreferenceholder.get());
                }
            }
        }
    }

    /**
     * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
     */
    public boolean enchantItem(PlayerEntity playerIn, int id)
    {
        return false;
    }

    public Slot getSlot(int slotId)
    {
        return this.inventorySlots.get(slotId);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        Slot slot = this.inventorySlots.get(index);
        return slot != null ? slot.getStack() : ItemStack.EMPTY;
    }

    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player)
    {
        try
        {
            return this.func_241440_b_(slotId, dragType, clickTypeIn, player);
        }
        catch (Exception exception)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(exception, "Container click");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Click info");
            crashreportcategory.addDetail("Menu Type", () ->
            {
                return this.containerType != null ? Registry.MENU.getKey(this.containerType).toString() : "<no type>";
            });
            crashreportcategory.addDetail("Menu Class", () ->
            {
                return this.getClass().getCanonicalName();
            });
            crashreportcategory.addDetail("Slot Count", this.inventorySlots.size());
            crashreportcategory.addDetail("Slot", slotId);
            crashreportcategory.addDetail("Button", dragType);
            crashreportcategory.addDetail("Type", clickTypeIn);
            throw new ReportedException(crashreport);
        }
    }

    private ItemStack func_241440_b_(int p_241440_1_, int p_241440_2_, ClickType p_241440_3_, PlayerEntity p_241440_4_)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        PlayerInventory playerinventory = p_241440_4_.inventory;

        if (p_241440_3_ == ClickType.QUICK_CRAFT)
        {
            int i1 = this.dragEvent;
            this.dragEvent = getDragEvent(p_241440_2_);

            if ((i1 != 1 || this.dragEvent != 2) && i1 != this.dragEvent)
            {
                this.resetDrag();
            }
            else if (playerinventory.getItemStack().isEmpty())
            {
                this.resetDrag();
            }
            else if (this.dragEvent == 0)
            {
                this.dragMode = extractDragMode(p_241440_2_);

                if (isValidDragMode(this.dragMode, p_241440_4_))
                {
                    this.dragEvent = 1;
                    this.dragSlots.clear();
                }
                else
                {
                    this.resetDrag();
                }
            }
            else if (this.dragEvent == 1)
            {
                Slot slot7 = this.inventorySlots.get(p_241440_1_);
                ItemStack itemstack12 = playerinventory.getItemStack();

                if (slot7 != null && canAddItemToSlot(slot7, itemstack12, true) && slot7.isItemValid(itemstack12) && (this.dragMode == 2 || itemstack12.getCount() > this.dragSlots.size()) && this.canDragIntoSlot(slot7))
                {
                    this.dragSlots.add(slot7);
                }
            }
            else if (this.dragEvent == 2)
            {
                if (!this.dragSlots.isEmpty())
                {
                    ItemStack itemstack10 = playerinventory.getItemStack().copy();
                    int k1 = playerinventory.getItemStack().getCount();

                    for (Slot slot8 : this.dragSlots)
                    {
                        ItemStack itemstack13 = playerinventory.getItemStack();

                        if (slot8 != null && canAddItemToSlot(slot8, itemstack13, true) && slot8.isItemValid(itemstack13) && (this.dragMode == 2 || itemstack13.getCount() >= this.dragSlots.size()) && this.canDragIntoSlot(slot8))
                        {
                            ItemStack itemstack14 = itemstack10.copy();
                            int j3 = slot8.getHasStack() ? slot8.getStack().getCount() : 0;
                            computeStackSize(this.dragSlots, this.dragMode, itemstack14, j3);
                            int k3 = Math.min(itemstack14.getMaxStackSize(), slot8.getItemStackLimit(itemstack14));

                            if (itemstack14.getCount() > k3)
                            {
                                itemstack14.setCount(k3);
                            }

                            k1 -= itemstack14.getCount() - j3;
                            slot8.putStack(itemstack14);
                        }
                    }

                    itemstack10.setCount(k1);
                    playerinventory.setItemStack(itemstack10);
                }

                this.resetDrag();
            }
            else
            {
                this.resetDrag();
            }
        }
        else if (this.dragEvent != 0)
        {
            this.resetDrag();
        }
        else if ((p_241440_3_ == ClickType.PICKUP || p_241440_3_ == ClickType.QUICK_MOVE) && (p_241440_2_ == 0 || p_241440_2_ == 1))
        {
            if (p_241440_1_ == -999)
            {
                if (!playerinventory.getItemStack().isEmpty())
                {
                    if (p_241440_2_ == 0)
                    {
                        p_241440_4_.dropItem(playerinventory.getItemStack(), true);
                        playerinventory.setItemStack(ItemStack.EMPTY);
                    }

                    if (p_241440_2_ == 1)
                    {
                        p_241440_4_.dropItem(playerinventory.getItemStack().split(1), true);
                    }
                }
            }
            else if (p_241440_3_ == ClickType.QUICK_MOVE)
            {
                if (p_241440_1_ < 0)
                {
                    return ItemStack.EMPTY;
                }

                Slot slot5 = this.inventorySlots.get(p_241440_1_);

                if (slot5 == null || !slot5.canTakeStack(p_241440_4_))
                {
                    return ItemStack.EMPTY;
                }

                for (ItemStack itemstack8 = this.transferStackInSlot(p_241440_4_, p_241440_1_); !itemstack8.isEmpty() && ItemStack.areItemsEqual(slot5.getStack(), itemstack8); itemstack8 = this.transferStackInSlot(p_241440_4_, p_241440_1_))
                {
                    itemstack = itemstack8.copy();
                }
            }
            else
            {
                if (p_241440_1_ < 0)
                {
                    return ItemStack.EMPTY;
                }

                Slot slot6 = this.inventorySlots.get(p_241440_1_);

                if (slot6 != null)
                {
                    ItemStack itemstack9 = slot6.getStack();
                    ItemStack itemstack11 = playerinventory.getItemStack();

                    if (!itemstack9.isEmpty())
                    {
                        itemstack = itemstack9.copy();
                    }

                    if (itemstack9.isEmpty())
                    {
                        if (!itemstack11.isEmpty() && slot6.isItemValid(itemstack11))
                        {
                            int j2 = p_241440_2_ == 0 ? itemstack11.getCount() : 1;

                            if (j2 > slot6.getItemStackLimit(itemstack11))
                            {
                                j2 = slot6.getItemStackLimit(itemstack11);
                            }

                            slot6.putStack(itemstack11.split(j2));
                        }
                    }
                    else if (slot6.canTakeStack(p_241440_4_))
                    {
                        if (itemstack11.isEmpty())
                        {
                            if (itemstack9.isEmpty())
                            {
                                slot6.putStack(ItemStack.EMPTY);
                                playerinventory.setItemStack(ItemStack.EMPTY);
                            }
                            else
                            {
                                int k2 = p_241440_2_ == 0 ? itemstack9.getCount() : (itemstack9.getCount() + 1) / 2;
                                playerinventory.setItemStack(slot6.decrStackSize(k2));

                                if (itemstack9.isEmpty())
                                {
                                    slot6.putStack(ItemStack.EMPTY);
                                }

                                slot6.onTake(p_241440_4_, playerinventory.getItemStack());
                            }
                        }
                        else if (slot6.isItemValid(itemstack11))
                        {
                            if (areItemsAndTagsEqual(itemstack9, itemstack11))
                            {
                                int l2 = p_241440_2_ == 0 ? itemstack11.getCount() : 1;

                                if (l2 > slot6.getItemStackLimit(itemstack11) - itemstack9.getCount())
                                {
                                    l2 = slot6.getItemStackLimit(itemstack11) - itemstack9.getCount();
                                }

                                if (l2 > itemstack11.getMaxStackSize() - itemstack9.getCount())
                                {
                                    l2 = itemstack11.getMaxStackSize() - itemstack9.getCount();
                                }

                                itemstack11.shrink(l2);
                                itemstack9.grow(l2);
                            }
                            else if (itemstack11.getCount() <= slot6.getItemStackLimit(itemstack11))
                            {
                                slot6.putStack(itemstack11);
                                playerinventory.setItemStack(itemstack9);
                            }
                        }
                        else if (itemstack11.getMaxStackSize() > 1 && areItemsAndTagsEqual(itemstack9, itemstack11) && !itemstack9.isEmpty())
                        {
                            int i3 = itemstack9.getCount();

                            if (i3 + itemstack11.getCount() <= itemstack11.getMaxStackSize())
                            {
                                itemstack11.grow(i3);
                                itemstack9 = slot6.decrStackSize(i3);

                                if (itemstack9.isEmpty())
                                {
                                    slot6.putStack(ItemStack.EMPTY);
                                }

                                slot6.onTake(p_241440_4_, playerinventory.getItemStack());
                            }
                        }
                    }

                    slot6.onSlotChanged();
                }
            }
        }
        else if (p_241440_3_ == ClickType.SWAP)
        {
            Slot slot = this.inventorySlots.get(p_241440_1_);
            ItemStack itemstack1 = playerinventory.getStackInSlot(p_241440_2_);
            ItemStack itemstack2 = slot.getStack();

            if (!itemstack1.isEmpty() || !itemstack2.isEmpty())
            {
                if (itemstack1.isEmpty())
                {
                    if (slot.canTakeStack(p_241440_4_))
                    {
                        playerinventory.setInventorySlotContents(p_241440_2_, itemstack2);
                        slot.onSwapCraft(itemstack2.getCount());
                        slot.putStack(ItemStack.EMPTY);
                        slot.onTake(p_241440_4_, itemstack2);
                    }
                }
                else if (itemstack2.isEmpty())
                {
                    if (slot.isItemValid(itemstack1))
                    {
                        int i = slot.getItemStackLimit(itemstack1);

                        if (itemstack1.getCount() > i)
                        {
                            slot.putStack(itemstack1.split(i));
                        }
                        else
                        {
                            slot.putStack(itemstack1);
                            playerinventory.setInventorySlotContents(p_241440_2_, ItemStack.EMPTY);
                        }
                    }
                }
                else if (slot.canTakeStack(p_241440_4_) && slot.isItemValid(itemstack1))
                {
                    int l1 = slot.getItemStackLimit(itemstack1);

                    if (itemstack1.getCount() > l1)
                    {
                        slot.putStack(itemstack1.split(l1));
                        slot.onTake(p_241440_4_, itemstack2);

                        if (!playerinventory.addItemStackToInventory(itemstack2))
                        {
                            p_241440_4_.dropItem(itemstack2, true);
                        }
                    }
                    else
                    {
                        slot.putStack(itemstack1);
                        playerinventory.setInventorySlotContents(p_241440_2_, itemstack2);
                        slot.onTake(p_241440_4_, itemstack2);
                    }
                }
            }
        }
        else if (p_241440_3_ == ClickType.CLONE && p_241440_4_.abilities.isCreativeMode && playerinventory.getItemStack().isEmpty() && p_241440_1_ >= 0)
        {
            Slot slot4 = this.inventorySlots.get(p_241440_1_);

            if (slot4 != null && slot4.getHasStack())
            {
                ItemStack itemstack7 = slot4.getStack().copy();
                itemstack7.setCount(itemstack7.getMaxStackSize());
                playerinventory.setItemStack(itemstack7);
            }
        }
        else if (p_241440_3_ == ClickType.THROW && playerinventory.getItemStack().isEmpty() && p_241440_1_ >= 0)
        {
            Slot slot3 = this.inventorySlots.get(p_241440_1_);

            if (slot3 != null && slot3.getHasStack() && slot3.canTakeStack(p_241440_4_))
            {
                ItemStack itemstack6 = slot3.decrStackSize(p_241440_2_ == 0 ? 1 : slot3.getStack().getCount());
                slot3.onTake(p_241440_4_, itemstack6);
                p_241440_4_.dropItem(itemstack6, true);
            }
        }
        else if (p_241440_3_ == ClickType.PICKUP_ALL && p_241440_1_ >= 0)
        {
            Slot slot2 = this.inventorySlots.get(p_241440_1_);
            ItemStack itemstack5 = playerinventory.getItemStack();

            if (!itemstack5.isEmpty() && (slot2 == null || !slot2.getHasStack() || !slot2.canTakeStack(p_241440_4_)))
            {
                int j1 = p_241440_2_ == 0 ? 0 : this.inventorySlots.size() - 1;
                int i2 = p_241440_2_ == 0 ? 1 : -1;

                for (int j = 0; j < 2; ++j)
                {
                    for (int k = j1; k >= 0 && k < this.inventorySlots.size() && itemstack5.getCount() < itemstack5.getMaxStackSize(); k += i2)
                    {
                        Slot slot1 = this.inventorySlots.get(k);

                        if (slot1.getHasStack() && canAddItemToSlot(slot1, itemstack5, true) && slot1.canTakeStack(p_241440_4_) && this.canMergeSlot(itemstack5, slot1))
                        {
                            ItemStack itemstack3 = slot1.getStack();

                            if (j != 0 || itemstack3.getCount() != itemstack3.getMaxStackSize())
                            {
                                int l = Math.min(itemstack5.getMaxStackSize() - itemstack5.getCount(), itemstack3.getCount());
                                ItemStack itemstack4 = slot1.decrStackSize(l);
                                itemstack5.grow(l);

                                if (itemstack4.isEmpty())
                                {
                                    slot1.putStack(ItemStack.EMPTY);
                                }

                                slot1.onTake(p_241440_4_, itemstack4);
                            }
                        }
                    }
                }
            }

            this.detectAndSendChanges();
        }

        return itemstack;
    }

    public static boolean areItemsAndTagsEqual(ItemStack stack1, ItemStack stack2)
    {
        return stack1.getItem() == stack2.getItem() && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return true;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(PlayerEntity playerIn)
    {
        PlayerInventory playerinventory = playerIn.inventory;

        if (!playerinventory.getItemStack().isEmpty())
        {
            playerIn.dropItem(playerinventory.getItemStack(), false);
            playerinventory.setItemStack(ItemStack.EMPTY);
        }
    }

    protected void clearContainer(PlayerEntity playerIn, World worldIn, IInventory inventoryIn)
    {
        if (!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerIn).hasDisconnected())
        {
            for (int j = 0; j < inventoryIn.getSizeInventory(); ++j)
            {
                playerIn.dropItem(inventoryIn.removeStackFromSlot(j), false);
            }
        }
        else
        {
            for (int i = 0; i < inventoryIn.getSizeInventory(); ++i)
            {
                playerIn.inventory.placeItemBackInInventory(worldIn, inventoryIn.removeStackFromSlot(i));
            }
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.detectAndSendChanges();
    }

    /**
     * Puts an ItemStack in a slot.
     */
    public void putStackInSlot(int slotID, ItemStack stack)
    {
        this.getSlot(slotID).putStack(stack);
    }

    public void setAll(List<ItemStack> p_190896_1_)
    {
        for (int i = 0; i < p_190896_1_.size(); ++i)
        {
            this.getSlot(i).putStack(p_190896_1_.get(i));
        }
    }

    public void updateProgressBar(int id, int data)
    {
        this.trackedIntReferences.get(id).set(data);
    }

    /**
     * Gets a unique transaction ID. Parameter is unused.
     */
    public short getNextTransactionID(PlayerInventory invPlayer)
    {
        ++this.transactionID;
        return this.transactionID;
    }

    /**
     * gets whether or not the player can craft in this inventory or not
     */
    public boolean getCanCraft(PlayerEntity player)
    {
        return !this.playerList.contains(player);
    }

    /**
     * sets whether the player can craft in this inventory or not
     */
    public void setCanCraft(PlayerEntity player, boolean canCraft)
    {
        if (canCraft)
        {
            this.playerList.remove(player);
        }
        else
        {
            this.playerList.add(player);
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    public abstract boolean canInteractWith(PlayerEntity playerIn);

    /**
     * Merges provided ItemStack with the first avaliable one in the container/player inventor between minIndex
     * (included) and maxIndex (excluded). Args : stack, minIndex, maxIndex, negativDirection. /!\ the Container
     * implementation do not check if the item is valid for the slot
     */
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection)
    {
        boolean flag = false;
        int i = startIndex;

        if (reverseDirection)
        {
            i = endIndex - 1;
        }

        if (stack.isStackable())
        {
            while (!stack.isEmpty())
            {
                if (reverseDirection)
                {
                    if (i < startIndex)
                    {
                        break;
                    }
                }
                else if (i >= endIndex)
                {
                    break;
                }

                Slot slot = this.inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();

                if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack))
                {
                    int j = itemstack.getCount() + stack.getCount();

                    if (j <= stack.getMaxStackSize())
                    {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.onSlotChanged();
                        flag = true;
                    }
                    else if (itemstack.getCount() < stack.getMaxStackSize())
                    {
                        stack.shrink(stack.getMaxStackSize() - itemstack.getCount());
                        itemstack.setCount(stack.getMaxStackSize());
                        slot.onSlotChanged();
                        flag = true;
                    }
                }

                if (reverseDirection)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty())
        {
            if (reverseDirection)
            {
                i = endIndex - 1;
            }
            else
            {
                i = startIndex;
            }

            while (true)
            {
                if (reverseDirection)
                {
                    if (i < startIndex)
                    {
                        break;
                    }
                }
                else if (i >= endIndex)
                {
                    break;
                }

                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();

                if (itemstack1.isEmpty() && slot1.isItemValid(stack))
                {
                    if (stack.getCount() > slot1.getSlotStackLimit())
                    {
                        slot1.putStack(stack.split(slot1.getSlotStackLimit()));
                    }
                    else
                    {
                        slot1.putStack(stack.split(stack.getCount()));
                    }

                    slot1.onSlotChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }

        return flag;
    }

    /**
     * Extracts the drag mode. Args : eventButton. Return (0 : evenly split, 1 : one item by slot, 2 : not used ?)
     */
    public static int extractDragMode(int eventButton)
    {
        return eventButton >> 2 & 3;
    }

    /**
     * Args : clickedButton, Returns (0 : start drag, 1 : add slot, 2 : end drag)
     */
    public static int getDragEvent(int clickedButton)
    {
        return clickedButton & 3;
    }

    public static int getQuickcraftMask(int p_94534_0_, int p_94534_1_)
    {
        return p_94534_0_ & 3 | (p_94534_1_ & 3) << 2;
    }

    public static boolean isValidDragMode(int dragModeIn, PlayerEntity player)
    {
        if (dragModeIn == 0)
        {
            return true;
        }
        else if (dragModeIn == 1)
        {
            return true;
        }
        else
        {
            return dragModeIn == 2 && player.abilities.isCreativeMode;
        }
    }

    /**
     * Reset the drag fields
     */
    protected void resetDrag()
    {
        this.dragEvent = 0;
        this.dragSlots.clear();
    }

    /**
     * Checks if it's possible to add the given itemstack to the given slot.
     */
    public static boolean canAddItemToSlot(@Nullable Slot slotIn, ItemStack stack, boolean stackSizeMatters)
    {
        boolean flag = slotIn == null || !slotIn.getHasStack();

        if (!flag && stack.isItemEqual(slotIn.getStack()) && ItemStack.areItemStackTagsEqual(slotIn.getStack(), stack))
        {
            return slotIn.getStack().getCount() + (stackSizeMatters ? 0 : stack.getCount()) <= stack.getMaxStackSize();
        }
        else
        {
            return flag;
        }
    }

    /**
     * Compute the new stack size, Returns the stack with the new size. Args : dragSlots, dragMode, dragStack,
     * slotStackSize
     */
    public static void computeStackSize(Set<Slot> dragSlotsIn, int dragModeIn, ItemStack stack, int slotStackSize)
    {
        switch (dragModeIn)
        {
            case 0:
                stack.setCount(MathHelper.floor((float)stack.getCount() / (float)dragSlotsIn.size()));
                break;

            case 1:
                stack.setCount(1);
                break;

            case 2:
                stack.setCount(stack.getItem().getMaxStackSize());
        }

        stack.grow(slotStackSize);
    }

    /**
     * Returns true if the player can "drag-spilt" items into this slot,. returns true by default. Called to check if
     * the slot can be added to a list of Slots to split the held ItemStack across.
     */
    public boolean canDragIntoSlot(Slot slotIn)
    {
        return true;
    }

    /**
     * Like the version that takes an inventory. If the given TileEntity is not an Inventory, 0 is returned instead.
     */
    public static int calcRedstone(@Nullable TileEntity te)
    {
        return te instanceof IInventory ? calcRedstoneFromInventory((IInventory)te) : 0;
    }

    public static int calcRedstoneFromInventory(@Nullable IInventory inv)
    {
        if (inv == null)
        {
            return 0;
        }
        else
        {
            int i = 0;
            float f = 0.0F;

            for (int j = 0; j < inv.getSizeInventory(); ++j)
            {
                ItemStack itemstack = inv.getStackInSlot(j);

                if (!itemstack.isEmpty())
                {
                    f += (float)itemstack.getCount() / (float)Math.min(inv.getInventoryStackLimit(), itemstack.getMaxStackSize());
                    ++i;
                }
            }

            f = f / (float)inv.getSizeInventory();
            return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
        }
    }
}
