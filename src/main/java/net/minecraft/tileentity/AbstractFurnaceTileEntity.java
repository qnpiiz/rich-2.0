package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public abstract class AbstractFurnaceTileEntity extends LockableTileEntity implements ISidedInventory, IRecipeHolder, IRecipeHelperPopulator, ITickableTileEntity
{
    private static final int[] SLOTS_UP = new int[] {0};
    private static final int[] SLOTS_DOWN = new int[] {2, 1};
    private static final int[] SLOTS_HORIZONTAL = new int[] {1};
    protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int burnTime;
    private int recipesUsed;
    private int cookTime;
    private int cookTimeTotal;
    protected final IIntArray furnaceData = new IIntArray()
    {
        public int get(int index)
        {
            switch (index)
            {
                case 0:
                    return AbstractFurnaceTileEntity.this.burnTime;

                case 1:
                    return AbstractFurnaceTileEntity.this.recipesUsed;

                case 2:
                    return AbstractFurnaceTileEntity.this.cookTime;

                case 3:
                    return AbstractFurnaceTileEntity.this.cookTimeTotal;

                default:
                    return 0;
            }
        }
        public void set(int index, int value)
        {
            switch (index)
            {
                case 0:
                    AbstractFurnaceTileEntity.this.burnTime = value;
                    break;

                case 1:
                    AbstractFurnaceTileEntity.this.recipesUsed = value;
                    break;

                case 2:
                    AbstractFurnaceTileEntity.this.cookTime = value;
                    break;

                case 3:
                    AbstractFurnaceTileEntity.this.cookTimeTotal = value;
            }
        }
        public int size()
        {
            return 4;
        }
    };
    private final Object2IntOpenHashMap<ResourceLocation> recipes = new Object2IntOpenHashMap<>();
    protected final IRecipeType <? extends AbstractCookingRecipe > recipeType;

    protected AbstractFurnaceTileEntity(TileEntityType<?> tileTypeIn, IRecipeType <? extends AbstractCookingRecipe > recipeTypeIn)
    {
        super(tileTypeIn);
        this.recipeType = recipeTypeIn;
    }

    public static Map<Item, Integer> getBurnTimes()
    {
        Map<Item, Integer> map = Maps.newLinkedHashMap();
        addItemBurnTime(map, Items.LAVA_BUCKET, 20000);
        addItemBurnTime(map, Blocks.COAL_BLOCK, 16000);
        addItemBurnTime(map, Items.BLAZE_ROD, 2400);
        addItemBurnTime(map, Items.COAL, 1600);
        addItemBurnTime(map, Items.CHARCOAL, 1600);
        addItemTagBurnTime(map, ItemTags.LOGS, 300);
        addItemTagBurnTime(map, ItemTags.PLANKS, 300);
        addItemTagBurnTime(map, ItemTags.WOODEN_STAIRS, 300);
        addItemTagBurnTime(map, ItemTags.WOODEN_SLABS, 150);
        addItemTagBurnTime(map, ItemTags.WOODEN_TRAPDOORS, 300);
        addItemTagBurnTime(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        addItemBurnTime(map, Blocks.OAK_FENCE, 300);
        addItemBurnTime(map, Blocks.BIRCH_FENCE, 300);
        addItemBurnTime(map, Blocks.SPRUCE_FENCE, 300);
        addItemBurnTime(map, Blocks.JUNGLE_FENCE, 300);
        addItemBurnTime(map, Blocks.DARK_OAK_FENCE, 300);
        addItemBurnTime(map, Blocks.ACACIA_FENCE, 300);
        addItemBurnTime(map, Blocks.OAK_FENCE_GATE, 300);
        addItemBurnTime(map, Blocks.BIRCH_FENCE_GATE, 300);
        addItemBurnTime(map, Blocks.SPRUCE_FENCE_GATE, 300);
        addItemBurnTime(map, Blocks.JUNGLE_FENCE_GATE, 300);
        addItemBurnTime(map, Blocks.DARK_OAK_FENCE_GATE, 300);
        addItemBurnTime(map, Blocks.ACACIA_FENCE_GATE, 300);
        addItemBurnTime(map, Blocks.NOTE_BLOCK, 300);
        addItemBurnTime(map, Blocks.BOOKSHELF, 300);
        addItemBurnTime(map, Blocks.LECTERN, 300);
        addItemBurnTime(map, Blocks.JUKEBOX, 300);
        addItemBurnTime(map, Blocks.CHEST, 300);
        addItemBurnTime(map, Blocks.TRAPPED_CHEST, 300);
        addItemBurnTime(map, Blocks.CRAFTING_TABLE, 300);
        addItemBurnTime(map, Blocks.DAYLIGHT_DETECTOR, 300);
        addItemTagBurnTime(map, ItemTags.BANNERS, 300);
        addItemBurnTime(map, Items.BOW, 300);
        addItemBurnTime(map, Items.FISHING_ROD, 300);
        addItemBurnTime(map, Blocks.LADDER, 300);
        addItemTagBurnTime(map, ItemTags.SIGNS, 200);
        addItemBurnTime(map, Items.WOODEN_SHOVEL, 200);
        addItemBurnTime(map, Items.WOODEN_SWORD, 200);
        addItemBurnTime(map, Items.WOODEN_HOE, 200);
        addItemBurnTime(map, Items.WOODEN_AXE, 200);
        addItemBurnTime(map, Items.WOODEN_PICKAXE, 200);
        addItemTagBurnTime(map, ItemTags.WOODEN_DOORS, 200);
        addItemTagBurnTime(map, ItemTags.BOATS, 1200);
        addItemTagBurnTime(map, ItemTags.WOOL, 100);
        addItemTagBurnTime(map, ItemTags.WOODEN_BUTTONS, 100);
        addItemBurnTime(map, Items.STICK, 100);
        addItemTagBurnTime(map, ItemTags.SAPLINGS, 100);
        addItemBurnTime(map, Items.BOWL, 100);
        addItemTagBurnTime(map, ItemTags.CARPETS, 67);
        addItemBurnTime(map, Blocks.DRIED_KELP_BLOCK, 4001);
        addItemBurnTime(map, Items.CROSSBOW, 300);
        addItemBurnTime(map, Blocks.BAMBOO, 50);
        addItemBurnTime(map, Blocks.DEAD_BUSH, 100);
        addItemBurnTime(map, Blocks.SCAFFOLDING, 400);
        addItemBurnTime(map, Blocks.LOOM, 300);
        addItemBurnTime(map, Blocks.BARREL, 300);
        addItemBurnTime(map, Blocks.CARTOGRAPHY_TABLE, 300);
        addItemBurnTime(map, Blocks.FLETCHING_TABLE, 300);
        addItemBurnTime(map, Blocks.SMITHING_TABLE, 300);
        addItemBurnTime(map, Blocks.COMPOSTER, 300);
        return map;
    }

    private static boolean isNonFlammable(Item item)
    {
        return ItemTags.NON_FLAMMABLE_WOOD.contains(item);
    }

    private static void addItemTagBurnTime(Map<Item, Integer> map, ITag<Item> itemTag, int burnTimeIn)
    {
        for (Item item : itemTag.getAllElements())
        {
            if (!isNonFlammable(item))
            {
                map.put(item, burnTimeIn);
            }
        }
    }

    private static void addItemBurnTime(Map<Item, Integer> map, IItemProvider itemProvider, int burnTimeIn)
    {
        Item item = itemProvider.asItem();

        if (isNonFlammable(item))
        {
            if (SharedConstants.developmentMode)
            {
                throw(IllegalStateException)Util.pauseDevMode(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item.getDisplayName((ItemStack)null).getString() + " a furnace fuel. That will not work!"));
            }
        }
        else
        {
            map.put(item, burnTimeIn);
        }
    }

    private boolean isBurning()
    {
        return this.burnTime > 0;
    }

    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, this.items);
        this.burnTime = nbt.getShort("BurnTime");
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");
        this.recipesUsed = this.getBurnTime(this.items.get(1));
        CompoundNBT compoundnbt = nbt.getCompound("RecipesUsed");

        for (String s : compoundnbt.keySet())
        {
            this.recipes.put(new ResourceLocation(s), compoundnbt.getInt(s));
        }
    }

    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        compound.putShort("BurnTime", (short)this.burnTime);
        compound.putShort("CookTime", (short)this.cookTime);
        compound.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        ItemStackHelper.saveAllItems(compound, this.items);
        CompoundNBT compoundnbt = new CompoundNBT();
        this.recipes.forEach((recipeId, craftedAmount) ->
        {
            compoundnbt.putInt(recipeId.toString(), craftedAmount);
        });
        compound.put("RecipesUsed", compoundnbt);
        return compound;
    }

    public void tick()
    {
        boolean flag = this.isBurning();
        boolean flag1 = false;

        if (this.isBurning())
        {
            --this.burnTime;
        }

        if (!this.world.isRemote)
        {
            ItemStack itemstack = this.items.get(1);

            if (this.isBurning() || !itemstack.isEmpty() && !this.items.get(0).isEmpty())
            {
                IRecipe<?> irecipe = this.world.getRecipeManager().getRecipe((IRecipeType<AbstractCookingRecipe>)this.recipeType, this, this.world).orElse(null);

                if (!this.isBurning() && this.canSmelt(irecipe))
                {
                    this.burnTime = this.getBurnTime(itemstack);
                    this.recipesUsed = this.burnTime;

                    if (this.isBurning())
                    {
                        flag1 = true;

                        if (!itemstack.isEmpty())
                        {
                            Item item = itemstack.getItem();
                            itemstack.shrink(1);

                            if (itemstack.isEmpty())
                            {
                                Item item1 = item.getContainerItem();
                                this.items.set(1, item1 == null ? ItemStack.EMPTY : new ItemStack(item1));
                            }
                        }
                    }
                }

                if (this.isBurning() && this.canSmelt(irecipe))
                {
                    ++this.cookTime;

                    if (this.cookTime == this.cookTimeTotal)
                    {
                        this.cookTime = 0;
                        this.cookTimeTotal = this.getCookTime();
                        this.smelt(irecipe);
                        flag1 = true;
                    }
                }
                else
                {
                    this.cookTime = 0;
                }
            }
            else if (!this.isBurning() && this.cookTime > 0)
            {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            }

            if (flag != this.isBurning())
            {
                flag1 = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, Boolean.valueOf(this.isBurning())), 3);
            }
        }

        if (flag1)
        {
            this.markDirty();
        }
    }

    protected boolean canSmelt(@Nullable IRecipe<?> recipeIn)
    {
        if (!this.items.get(0).isEmpty() && recipeIn != null)
        {
            ItemStack itemstack = recipeIn.getRecipeOutput();

            if (itemstack.isEmpty())
            {
                return false;
            }
            else
            {
                ItemStack itemstack1 = this.items.get(2);

                if (itemstack1.isEmpty())
                {
                    return true;
                }
                else if (!itemstack1.isItemEqual(itemstack))
                {
                    return false;
                }
                else if (itemstack1.getCount() < this.getInventoryStackLimit() && itemstack1.getCount() < itemstack1.getMaxStackSize())
                {
                    return true;
                }
                else
                {
                    return itemstack1.getCount() < itemstack.getMaxStackSize();
                }
            }
        }
        else
        {
            return false;
        }
    }

    private void smelt(@Nullable IRecipe<?> recipe)
    {
        if (recipe != null && this.canSmelt(recipe))
        {
            ItemStack itemstack = this.items.get(0);
            ItemStack itemstack1 = recipe.getRecipeOutput();
            ItemStack itemstack2 = this.items.get(2);

            if (itemstack2.isEmpty())
            {
                this.items.set(2, itemstack1.copy());
            }
            else if (itemstack2.getItem() == itemstack1.getItem())
            {
                itemstack2.grow(1);
            }

            if (!this.world.isRemote)
            {
                this.setRecipeUsed(recipe);
            }

            if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.items.get(1).isEmpty() && this.items.get(1).getItem() == Items.BUCKET)
            {
                this.items.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.shrink(1);
        }
    }

    protected int getBurnTime(ItemStack fuel)
    {
        if (fuel.isEmpty())
        {
            return 0;
        }
        else
        {
            Item item = fuel.getItem();
            return getBurnTimes().getOrDefault(item, 0);
        }
    }

    protected int getCookTime()
    {
        return this.world.getRecipeManager().getRecipe((IRecipeType<AbstractCookingRecipe>)this.recipeType, this, this.world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }

    public static boolean isFuel(ItemStack stack)
    {
        return getBurnTimes().containsKey(stack.getItem());
    }

    public int[] getSlotsForFace(Direction side)
    {
        if (side == Direction.DOWN)
        {
            return SLOTS_DOWN;
        }
        else
        {
            return side == Direction.UP ? SLOTS_UP : SLOTS_HORIZONTAL;
        }
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side.
     */
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction)
    {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side.
     */
    public boolean canExtractItem(int index, ItemStack stack, Direction direction)
    {
        if (direction == Direction.DOWN && index == 1)
        {
            Item item = stack.getItem();

            if (item != Items.WATER_BUCKET && item != Items.BUCKET)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.items.size();
    }

    public boolean isEmpty()
    {
        for (ItemStack itemstack : this.items)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the stack in the given slot.
     */
    public ItemStack getStackInSlot(int index)
    {
        return this.items.get(index);
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(this.items, index, count);
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(this.items, index);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        ItemStack itemstack = this.items.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        this.items.set(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }

        if (index == 0 && !flag)
        {
            this.cookTimeTotal = this.getCookTime();
            this.cookTime = 0;
            this.markDirty();
        }
    }

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    public boolean isUsableByPlayer(PlayerEntity player)
    {
        if (this.world.getTileEntity(this.pos) != this)
        {
            return false;
        }
        else
        {
            return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
     * guis use Slot.isItemValid
     */
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        if (index == 2)
        {
            return false;
        }
        else if (index != 1)
        {
            return true;
        }
        else
        {
            ItemStack itemstack = this.items.get(1);
            return isFuel(stack) || stack.getItem() == Items.BUCKET && itemstack.getItem() != Items.BUCKET;
        }
    }

    public void clear()
    {
        this.items.clear();
    }

    public void setRecipeUsed(@Nullable IRecipe<?> recipe)
    {
        if (recipe != null)
        {
            ResourceLocation resourcelocation = recipe.getId();
            this.recipes.addTo(resourcelocation, 1);
        }
    }

    @Nullable
    public IRecipe<?> getRecipeUsed()
    {
        return null;
    }

    public void onCrafting(PlayerEntity player)
    {
    }

    public void unlockRecipes(PlayerEntity player)
    {
        List < IRecipe<? >> list = this.grantStoredRecipeExperience(player.world, player.getPositionVec());
        player.unlockRecipes(list);
        this.recipes.clear();
    }

    public List < IRecipe<? >> grantStoredRecipeExperience(World world, Vector3d pos)
    {
        List < IRecipe<? >> list = Lists.newArrayList();

        for (Entry<ResourceLocation> entry : this.recipes.object2IntEntrySet())
        {
            world.getRecipeManager().getRecipe(entry.getKey()).ifPresent((recipe) ->
            {
                list.add(recipe);
                splitAndSpawnExperience(world, pos, entry.getIntValue(), ((AbstractCookingRecipe)recipe).getExperience());
            });
        }

        return list;
    }

    private static void splitAndSpawnExperience(World world, Vector3d pos, int craftedAmount, float experience)
    {
        int i = MathHelper.floor((float)craftedAmount * experience);
        float f = MathHelper.frac((float)craftedAmount * experience);

        if (f != 0.0F && Math.random() < (double)f)
        {
            ++i;
        }

        while (i > 0)
        {
            int j = ExperienceOrbEntity.getXPSplit(i);
            i -= j;
            world.addEntity(new ExperienceOrbEntity(world, pos.x, pos.y, pos.z, j));
        }
    }

    public void fillStackedContents(RecipeItemHelper helper)
    {
        for (ItemStack itemstack : this.items)
        {
            helper.accountStack(itemstack);
        }
    }
}
