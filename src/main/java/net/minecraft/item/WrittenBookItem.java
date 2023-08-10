package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class WrittenBookItem extends Item
{
    public WrittenBookItem(Item.Properties builder)
    {
        super(builder);
    }

    public static boolean validBookTagContents(@Nullable CompoundNBT nbt)
    {
        if (!WritableBookItem.isNBTValid(nbt))
        {
            return false;
        }
        else if (!nbt.contains("title", 8))
        {
            return false;
        }
        else
        {
            String s = nbt.getString("title");
            return s.length() > 32 ? false : nbt.contains("author", 8);
        }
    }

    /**
     * Gets the generation of the book (how many times it has been cloned)
     */
    public static int getGeneration(ItemStack book)
    {
        return book.getTag().getInt("generation");
    }

    public static int getPageCount(ItemStack stack)
    {
        CompoundNBT compoundnbt = stack.getTag();
        return compoundnbt != null ? compoundnbt.getList("pages", 8).size() : 0;
    }

    public ITextComponent getDisplayName(ItemStack stack)
    {
        if (stack.hasTag())
        {
            CompoundNBT compoundnbt = stack.getTag();
            String s = compoundnbt.getString("title");

            if (!StringUtils.isNullOrEmpty(s))
            {
                return new StringTextComponent(s);
            }
        }

        return super.getDisplayName(stack);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if (stack.hasTag())
        {
            CompoundNBT compoundnbt = stack.getTag();
            String s = compoundnbt.getString("author");

            if (!StringUtils.isNullOrEmpty(s))
            {
                tooltip.add((new TranslationTextComponent("book.byAuthor", s)).mergeStyle(TextFormatting.GRAY));
            }

            tooltip.add((new TranslationTextComponent("book.generation." + compoundnbt.getInt("generation"))).mergeStyle(TextFormatting.GRAY));
        }
    }

    /**
     * Called when this item is used when targetting a Block
     */
    public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockState blockstate = world.getBlockState(blockpos);

        if (blockstate.isIn(Blocks.LECTERN))
        {
            return LecternBlock.tryPlaceBook(world, blockpos, blockstate, context.getItem()) ? ActionResultType.func_233537_a_(world.isRemote) : ActionResultType.PASS;
        }
        else
        {
            return ActionResultType.PASS;
        }
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        playerIn.openBook(itemstack, handIn);
        playerIn.addStat(Stats.ITEM_USED.get(this));
        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
    }

    public static boolean resolveContents(ItemStack stack, @Nullable CommandSource resolvingSource, @Nullable PlayerEntity resolvingPlayer)
    {
        CompoundNBT compoundnbt = stack.getTag();

        if (compoundnbt != null && !compoundnbt.getBoolean("resolved"))
        {
            compoundnbt.putBoolean("resolved", true);

            if (!validBookTagContents(compoundnbt))
            {
                return false;
            }
            else
            {
                ListNBT listnbt = compoundnbt.getList("pages", 8);

                for (int i = 0; i < listnbt.size(); ++i)
                {
                    String s = listnbt.getString(i);
                    ITextComponent itextcomponent;

                    try
                    {
                        itextcomponent = ITextComponent.Serializer.getComponentFromJsonLenient(s);
                        itextcomponent = TextComponentUtils.func_240645_a_(resolvingSource, itextcomponent, resolvingPlayer, 0);
                    }
                    catch (Exception exception)
                    {
                        itextcomponent = new StringTextComponent(s);
                    }

                    listnbt.set(i, (INBT)StringNBT.valueOf(ITextComponent.Serializer.toJson(itextcomponent)));
                }

                compoundnbt.put("pages", listnbt);
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns true if this item has an enchantment glint. By default, this returns
     * <code>stack.isItemEnchanted()</code>, but other items can override it (for instance, written books always return
     * true).
     *  
     * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
     * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
     */
    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }
}
