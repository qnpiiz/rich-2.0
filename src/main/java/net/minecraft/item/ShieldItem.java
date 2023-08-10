package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ShieldItem extends Item
{
    public ShieldItem(Item.Properties builder)
    {
        super(builder);
        DispenserBlock.registerDispenseBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getTranslationKey(ItemStack stack)
    {
        return stack.getChildTag("BlockEntityTag") != null ? this.getTranslationKey() + '.' + getColor(stack).getTranslationKey() : super.getTranslationKey(stack);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        BannerItem.appendHoverTextFromTileEntityTag(stack, tooltip);
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.BLOCK;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getUseDuration(ItemStack stack)
    {
        return 72000;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(itemstack);
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return ItemTags.PLANKS.contains(repair.getItem()) || super.getIsRepairable(toRepair, repair);
    }

    public static DyeColor getColor(ItemStack stack)
    {
        return DyeColor.byId(stack.getOrCreateChildTag("BlockEntityTag").getInt("Base"));
    }
}
