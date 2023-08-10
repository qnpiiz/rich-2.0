package net.minecraft.item;

import net.minecraft.entity.IEquipable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;

public class SaddleItem extends Item
{
    public SaddleItem(Item.Properties builder)
    {
        super(builder);
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand)
    {
        if (target instanceof IEquipable && target.isAlive())
        {
            IEquipable iequipable = (IEquipable)target;

            if (!iequipable.isHorseSaddled() && iequipable.func_230264_L__())
            {
                if (!playerIn.world.isRemote)
                {
                    iequipable.func_230266_a_(SoundCategory.NEUTRAL);
                    stack.shrink(1);
                }

                return ActionResultType.func_233537_a_(playerIn.world.isRemote);
            }
        }

        return ActionResultType.PASS;
    }
}
