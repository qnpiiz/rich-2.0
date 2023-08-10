package net.minecraft.item;

import net.minecraft.util.ResourceLocation;

public class HorseArmorItem extends Item
{
    private final int armorValue;
    private final String field_219979_b;

    public HorseArmorItem(int armorValue, String tierArmor, Item.Properties builder)
    {
        super(builder);
        this.armorValue = armorValue;
        this.field_219979_b = "textures/entity/horse/armor/horse_armor_" + tierArmor + ".png";
    }

    public ResourceLocation getArmorTexture()
    {
        return new ResourceLocation(this.field_219979_b);
    }

    public int getArmorValue()
    {
        return this.armorValue;
    }
}
