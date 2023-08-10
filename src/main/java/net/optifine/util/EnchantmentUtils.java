package net.optifine.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EnchantmentUtils
{
    private static final Map<String, Enchantment> MAP_ENCHANTMENTS = new HashMap<>();

    public static Enchantment getEnchantment(String name)
    {
        Enchantment enchantment = MAP_ENCHANTMENTS.get(name);

        if (enchantment == null)
        {
            ResourceLocation resourcelocation = new ResourceLocation(name);

            if (Registry.ENCHANTMENT.containsKey(resourcelocation))
            {
                enchantment = Registry.ENCHANTMENT.getOrDefault(resourcelocation);
            }

            MAP_ENCHANTMENTS.put(name, enchantment);
        }

        return enchantment;
    }

    public static Enchantment getEnchantment(ResourceLocation loc)
    {
        return !Registry.ENCHANTMENT.containsKey(loc) ? null : Registry.ENCHANTMENT.getOrDefault(loc);
    }
}
