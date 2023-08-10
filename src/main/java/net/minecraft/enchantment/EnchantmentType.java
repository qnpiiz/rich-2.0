package net.minecraft.enchantment;

import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;

public enum EnchantmentType
{
    ARMOR {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ArmorItem;
        }
    },
    ARMOR_FEET {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ArmorItem && ((ArmorItem)itemIn).getEquipmentSlot() == EquipmentSlotType.FEET;
        }
    },
    ARMOR_LEGS {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ArmorItem && ((ArmorItem)itemIn).getEquipmentSlot() == EquipmentSlotType.LEGS;
        }
    },
    ARMOR_CHEST {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ArmorItem && ((ArmorItem)itemIn).getEquipmentSlot() == EquipmentSlotType.CHEST;
        }
    },
    ARMOR_HEAD {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ArmorItem && ((ArmorItem)itemIn).getEquipmentSlot() == EquipmentSlotType.HEAD;
        }
    },
    WEAPON {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof SwordItem;
        }
    },
    DIGGER {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ToolItem;
        }
    },
    FISHING_ROD {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof FishingRodItem;
        }
    },
    TRIDENT {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof TridentItem;
        }
    },
    BREAKABLE {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn.isDamageable();
        }
    },
    BOW {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof BowItem;
        }
    },
    WEARABLE {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof IArmorVanishable || Block.getBlockFromItem(itemIn) instanceof IArmorVanishable;
        }
    },
    CROSSBOW {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof CrossbowItem;
        }
    },
    VANISHABLE {
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof IVanishable || Block.getBlockFromItem(itemIn) instanceof IVanishable || BREAKABLE.canEnchantItem(itemIn);
        }
    };

    private EnchantmentType()
    {
    }

    /**
     * Return true if the item passed can be enchanted by a enchantment of this type.
     */
    public abstract boolean canEnchantItem(Item itemIn);
}
