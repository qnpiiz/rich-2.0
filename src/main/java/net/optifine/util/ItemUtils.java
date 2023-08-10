package net.optifine.util;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ItemUtils
{
    public static Item getItem(ResourceLocation loc)
    {
        return !Registry.ITEM.containsKey(loc) ? null : Registry.ITEM.getOrDefault(loc);
    }

    public static int getId(Item item)
    {
        return Registry.ITEM.getId(item);
    }
}
