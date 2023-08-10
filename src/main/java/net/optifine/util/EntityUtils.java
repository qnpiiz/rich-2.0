package net.optifine.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.optifine.Config;

public class EntityUtils
{
    private static final Map<EntityType, Integer> mapIdByType = new HashMap<>();
    private static final Map<String, Integer> mapIdByLocation = new HashMap<>();
    private static final Map<String, Integer> mapIdByName = new HashMap<>();

    public static int getEntityIdByClass(Entity entity)
    {
        return entity == null ? -1 : getEntityIdByType(entity.getType());
    }

    public static int getEntityIdByType(EntityType type)
    {
        Integer integer = mapIdByType.get(type);
        return integer == null ? -1 : integer;
    }

    public static int getEntityIdByLocation(String locStr)
    {
        Integer integer = mapIdByLocation.get(locStr);
        return integer == null ? -1 : integer;
    }

    public static int getEntityIdByName(String name)
    {
        name = name.toLowerCase(Locale.ROOT);
        Integer integer = mapIdByName.get(name);
        return integer == null ? -1 : integer;
    }

    static
    {
        for (EntityType entitytype : Registry.ENTITY_TYPE)
        {
            int i = Registry.ENTITY_TYPE.getId(entitytype);
            ResourceLocation resourcelocation = Registry.ENTITY_TYPE.getKey(entitytype);
            String s = resourcelocation.toString();
            String s1 = resourcelocation.getPath();

            if (mapIdByType.containsKey(entitytype))
            {
                Config.warn("Duplicate entity type: " + entitytype + ", id1: " + mapIdByType.get(entitytype) + ", id2: " + i);
            }

            if (mapIdByLocation.containsKey(s))
            {
                Config.warn("Duplicate entity location: " + s + ", id1: " + mapIdByLocation.get(s) + ", id2: " + i);
            }

            if (mapIdByName.containsKey(s))
            {
                Config.warn("Duplicate entity name: " + s1 + ", id1: " + mapIdByName.get(s1) + ", id2: " + i);
            }

            mapIdByType.put(entitytype, i);
            mapIdByLocation.put(s, i);
            mapIdByName.put(s1, i);
        }
    }
}
