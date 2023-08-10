package net.minecraft.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ElytraItem extends Item implements IArmorVanishable
{
    public ElytraItem(Item.Properties builder)
    {
        super(builder);
        DispenserBlock.registerDispenseBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    public static boolean isUsable(ItemStack stack)
    {
        return stack.getDamage() < stack.getMaxDamage() - 1;
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return repair.getItem() == Items.PHANTOM_MEMBRANE;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstack);
        ItemStack itemstack1 = playerIn.getItemStackFromSlot(equipmentslottype);

        if (itemstack1.isEmpty())
        {
            playerIn.setItemStackToSlot(equipmentslottype, itemstack.copy());
            itemstack.setCount(0);
            return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
        }
        else
        {
            return ActionResult.resultFail(itemstack);
        }
    }
}
