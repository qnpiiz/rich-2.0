package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRideable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class OnAStickItem<T extends Entity & IRideable> extends Item
{
    private final EntityType<T> temptedEntity;
    private final int damageAmount;

    public OnAStickItem(Item.Properties properties, EntityType<T> temptedEntity, int damageAmount)
    {
        super(properties);
        this.temptedEntity = temptedEntity;
        this.damageAmount = damageAmount;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (worldIn.isRemote)
        {
            return ActionResult.resultPass(itemstack);
        }
        else
        {
            Entity entity = playerIn.getRidingEntity();

            if (playerIn.isPassenger() && entity instanceof IRideable && entity.getType() == this.temptedEntity)
            {
                IRideable irideable = (IRideable)entity;

                if (irideable.boost())
                {
                    itemstack.damageItem(this.damageAmount, playerIn, (player) ->
                    {
                        player.sendBreakAnimation(handIn);
                    });

                    if (itemstack.isEmpty())
                    {
                        ItemStack itemstack1 = new ItemStack(Items.FISHING_ROD);
                        itemstack1.setTag(itemstack.getTag());
                        return ActionResult.resultSuccess(itemstack1);
                    }

                    return ActionResult.resultSuccess(itemstack);
                }
            }

            playerIn.addStat(Stats.ITEM_USED.get(this));
            return ActionResult.resultPass(itemstack);
        }
    }
}
