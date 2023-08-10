package net.optifine.util;

import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EntityTypeUtils
{
    public static EntityType getEntityType(ResourceLocation loc)
    {
        return !Registry.ENTITY_TYPE.containsKey(loc) ? null : Registry.ENTITY_TYPE.getOrDefault(loc);
    }
}
