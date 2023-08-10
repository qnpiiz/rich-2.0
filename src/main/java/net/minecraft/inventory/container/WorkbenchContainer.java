package net.minecraft.inventory.container;

import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

public class WorkbenchContainer extends RecipeBookContainer<CraftingInventory>
{
    private final CraftingInventory craftMatrix = new CraftingInventory(this, 3, 3);
    private final CraftResultInventory craftResult = new CraftResultInventory();
    private final IWorldPosCallable worldPosCallable;
    private final PlayerEntity player;

    public WorkbenchContainer(int id, PlayerInventory playerInventory)
    {
        this(id, playerInventory, IWorldPosCallable.DUMMY);
    }

    public WorkbenchContainer(int id, PlayerInventory playerInventory, IWorldPosCallable p_i50090_3_)
    {
        super(ContainerType.CRAFTING, id);
        this.worldPosCallable = p_i50090_3_;
        this.player = playerInventory.player;
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                this.addSlot(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l)
        {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }

    protected static void updateCraftingResult(int id, World world, PlayerEntity player, CraftingInventory inventory, CraftResultInventory inventoryResult)
    {
        if (!world.isRemote)
        {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)player;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<ICraftingRecipe> optional = world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, inventory, world);

            if (optional.isPresent())
            {
                ICraftingRecipe icraftingrecipe = optional.get();

                if (inventoryResult.canUseRecipe(world, serverplayerentity, icraftingrecipe))
                {
                    itemstack = icraftingrecipe.getCraftingResult(inventory);
                }
            }

            inventoryResult.setInventorySlotContents(0, itemstack);
            serverplayerentity.connection.sendPacket(new SSetSlotPacket(id, 0, itemstack));
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.worldPosCallable.consume((p_217069_1_, p_217069_2_) ->
        {
            updateCraftingResult(this.windowId, p_217069_1_, this.player, this.craftMatrix, this.craftResult);
        });
    }

    public void fillStackedContents(RecipeItemHelper itemHelperIn)
    {
        this.craftMatrix.fillStackedContents(itemHelperIn);
    }

    public void clear()
    {
        this.craftMatrix.clear();
        this.craftResult.clear();
    }

    public boolean matches(IRecipe <? super CraftingInventory > recipeIn)
    {
        return recipeIn.matches(this.craftMatrix, this.player.world);
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);
        this.worldPosCallable.consume((p_217068_2_, p_217068_3_) ->
        {
            this.clearContainer(playerIn, p_217068_2_, this.craftMatrix);
        });
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return isWithinUsableDistance(this.worldPosCallable, playerIn, Blocks.CRAFTING_TABLE);
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

            if (index == 0)
            {
                this.worldPosCallable.consume((p_217067_2_, p_217067_3_) ->
                {
                    itemstack1.getItem().onCreated(itemstack1, p_217067_2_, playerIn);
                });

                if (!this.mergeItemStack(itemstack1, 10, 46, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 10 && index < 46)
            {
                if (!this.mergeItemStack(itemstack1, 1, 10, false))
                {
                    if (index < 37)
                    {
                        if (!this.mergeItemStack(itemstack1, 37, 46, false))
                        {
                            return ItemStack.EMPTY;
                        }
                    }
                    else if (!this.mergeItemStack(itemstack1, 10, 37, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (!this.mergeItemStack(itemstack1, 10, 46, false))
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

            ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

            if (index == 0)
            {
                playerIn.dropItem(itemstack2, false);
            }
        }

        return itemstack;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }

    public int getOutputSlot()
    {
        return 0;
    }

    public int getWidth()
    {
        return this.craftMatrix.getWidth();
    }

    public int getHeight()
    {
        return this.craftMatrix.getHeight();
    }

    public int getSize()
    {
        return 10;
    }

    public RecipeBookCategory func_241850_m()
    {
        return RecipeBookCategory.CRAFTING;
    }
}
