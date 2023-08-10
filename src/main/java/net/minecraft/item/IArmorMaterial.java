package net.minecraft.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;

public interface IArmorMaterial
{
    int getDurability(EquipmentSlotType slotIn);

    int getDamageReductionAmount(EquipmentSlotType slotIn);

    int getEnchantability();

    SoundEvent getSoundEvent();

    Ingredient getRepairMaterial();

    String getName();

    float getToughness();

    /**
     * Gets the percentage of knockback resistance provided by armor of the material.
     */
    float getKnockbackResistance();
}
