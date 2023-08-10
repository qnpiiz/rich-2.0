package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ChorusFruitItem extends Item
{
    public ChorusFruitItem(Item.Properties builder)
    {
        super(builder);
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving)
    {
        ItemStack itemstack = super.onItemUseFinish(stack, worldIn, entityLiving);

        if (!worldIn.isRemote)
        {
            double d0 = entityLiving.getPosX();
            double d1 = entityLiving.getPosY();
            double d2 = entityLiving.getPosZ();

            for (int i = 0; i < 16; ++i)
            {
                double d3 = entityLiving.getPosX() + (entityLiving.getRNG().nextDouble() - 0.5D) * 16.0D;
                double d4 = MathHelper.clamp(entityLiving.getPosY() + (double)(entityLiving.getRNG().nextInt(16) - 8), 0.0D, (double)(worldIn.func_234938_ad_() - 1));
                double d5 = entityLiving.getPosZ() + (entityLiving.getRNG().nextDouble() - 0.5D) * 16.0D;

                if (entityLiving.isPassenger())
                {
                    entityLiving.stopRiding();
                }

                if (entityLiving.attemptTeleport(d3, d4, d5, true))
                {
                    SoundEvent soundevent = entityLiving instanceof FoxEntity ? SoundEvents.ENTITY_FOX_TELEPORT : SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                    worldIn.playSound((PlayerEntity)null, d0, d1, d2, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    entityLiving.playSound(soundevent, 1.0F, 1.0F);
                    break;
                }
            }

            if (entityLiving instanceof PlayerEntity)
            {
                ((PlayerEntity)entityLiving).getCooldownTracker().setCooldown(this, 20);
            }
        }

        return itemstack;
    }
}
