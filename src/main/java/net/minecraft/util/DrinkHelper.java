package net.minecraft.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DrinkHelper
{
    public static ActionResult<ItemStack> startDrinking(World world, PlayerEntity player, Hand hand)
    {
        player.setActiveHand(hand);
        return ActionResult.resultConsume(player.getHeldItem(hand));
    }

    public static ItemStack fill(ItemStack empty, PlayerEntity player, ItemStack filled, boolean preventDuplicates)
    {
        boolean flag = player.abilities.isCreativeMode;

        if (preventDuplicates && flag)
        {
            if (!player.inventory.hasItemStack(filled))
            {
                player.inventory.addItemStackToInventory(filled);
            }

            return empty;
        }
        else
        {
            if (!flag)
            {
                empty.shrink(1);
            }

            if (empty.isEmpty())
            {
                return filled;
            }
            else
            {
                if (!player.inventory.addItemStackToInventory(filled))
                {
                    player.dropItem(filled, false);
                }

                return empty;
            }
        }
    }

    public static ItemStack fill(ItemStack empty, PlayerEntity player, ItemStack filled)
    {
        return fill(empty, player, filled, true);
    }
}
