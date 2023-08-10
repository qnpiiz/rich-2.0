package net.optifine.config;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.optifine.util.EnchantmentUtils;

public class ParserEnchantmentId implements IParserInt
{
    public int parse(String str, int defVal)
    {
        ResourceLocation resourcelocation = new ResourceLocation(str);
        Enchantment enchantment = EnchantmentUtils.getEnchantment(resourcelocation);
        return enchantment == null ? defVal : Registry.ENCHANTMENT.getId(enchantment);
    }
}
