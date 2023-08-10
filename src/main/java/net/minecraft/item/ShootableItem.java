package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;

public abstract class ShootableItem extends Item
{
    public static final Predicate<ItemStack> ARROWS = (stack) ->
    {
        return stack.getItem().isIn(ItemTags.ARROWS);
    };
    public static final Predicate<ItemStack> ARROWS_OR_FIREWORKS = ARROWS.or((stack) ->
    {
        return stack.getItem() == Items.FIREWORK_ROCKET;
    });

    public ShootableItem(Item.Properties builder)
    {
        super(builder);
    }

    public Predicate<ItemStack> getAmmoPredicate()
    {
        return this.getInventoryAmmoPredicate();
    }

    public abstract Predicate<ItemStack> getInventoryAmmoPredicate();

    public static ItemStack getHeldAmmo(LivingEntity living, Predicate<ItemStack> isAmmo)
    {
        if (isAmmo.test(living.getHeldItem(Hand.OFF_HAND)))
        {
            return living.getHeldItem(Hand.OFF_HAND);
        }
        else
        {
            return isAmmo.test(living.getHeldItem(Hand.MAIN_HAND)) ? living.getHeldItem(Hand.MAIN_HAND) : ItemStack.EMPTY;
        }
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability()
    {
        return 1;
    }

    public abstract int func_230305_d_();
}
