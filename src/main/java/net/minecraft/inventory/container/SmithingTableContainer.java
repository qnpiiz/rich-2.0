package net.minecraft.inventory.container;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

public class SmithingTableContainer extends AbstractRepairContainer
{
    private final World field_234651_g_;
    @Nullable
    private SmithingRecipe field_234652_h_;
    private final List<SmithingRecipe> field_241443_i_;

    public SmithingTableContainer(int p_i231590_1_, PlayerInventory p_i231590_2_)
    {
        this(p_i231590_1_, p_i231590_2_, IWorldPosCallable.DUMMY);
    }

    public SmithingTableContainer(int p_i231591_1_, PlayerInventory p_i231591_2_, IWorldPosCallable p_i231591_3_)
    {
        super(ContainerType.SMITHING, p_i231591_1_, p_i231591_2_, p_i231591_3_);
        this.field_234651_g_ = p_i231591_2_.player.world;
        this.field_241443_i_ = this.field_234651_g_.getRecipeManager().getRecipesForType(IRecipeType.SMITHING);
    }

    protected boolean func_230302_a_(BlockState p_230302_1_)
    {
        return p_230302_1_.isIn(Blocks.SMITHING_TABLE);
    }

    protected boolean func_230303_b_(PlayerEntity p_230303_1_, boolean p_230303_2_)
    {
        return this.field_234652_h_ != null && this.field_234652_h_.matches(this.field_234643_d_, this.field_234651_g_);
    }

    protected ItemStack func_230301_a_(PlayerEntity p_230301_1_, ItemStack p_230301_2_)
    {
        p_230301_2_.onCrafting(p_230301_1_.world, p_230301_1_, p_230301_2_.getCount());
        this.field_234642_c_.onCrafting(p_230301_1_);
        this.func_234654_d_(0);
        this.func_234654_d_(1);
        this.field_234644_e_.consume((p_234653_0_, p_234653_1_) ->
        {
            p_234653_0_.playEvent(1044, p_234653_1_, 0);
        });
        return p_230301_2_;
    }

    private void func_234654_d_(int p_234654_1_)
    {
        ItemStack itemstack = this.field_234643_d_.getStackInSlot(p_234654_1_);
        itemstack.shrink(1);
        this.field_234643_d_.setInventorySlotContents(p_234654_1_, itemstack);
    }

    /**
     * called when the Anvil Input Slot changes, calculates the new result and puts it in the output slot
     */
    public void updateRepairOutput()
    {
        List<SmithingRecipe> list = this.field_234651_g_.getRecipeManager().getRecipes(IRecipeType.SMITHING, this.field_234643_d_, this.field_234651_g_);

        if (list.isEmpty())
        {
            this.field_234642_c_.setInventorySlotContents(0, ItemStack.EMPTY);
        }
        else
        {
            this.field_234652_h_ = list.get(0);
            ItemStack itemstack = this.field_234652_h_.getCraftingResult(this.field_234643_d_);
            this.field_234642_c_.setRecipeUsed(this.field_234652_h_);
            this.field_234642_c_.setInventorySlotContents(0, itemstack);
        }
    }

    protected boolean func_241210_a_(ItemStack p_241210_1_)
    {
        return this.field_241443_i_.stream().anyMatch((p_241444_1_) ->
        {
            return p_241444_1_.isValidAdditionItem(p_241210_1_);
        });
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != this.field_234642_c_ && super.canMergeSlot(stack, slotIn);
    }
}
