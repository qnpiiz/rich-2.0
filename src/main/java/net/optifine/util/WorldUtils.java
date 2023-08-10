package net.optifine.util;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

public class WorldUtils
{
    public static int getDimensionId(World world)
    {
        return world == null ? 0 : getDimensionId(world.getDimensionKey());
    }

    public static int getDimensionId(RegistryKey<World> dimension)
    {
        if (dimension == World.THE_NETHER)
        {
            return -1;
        }
        else if (dimension == World.OVERWORLD)
        {
            return 0;
        }
        else
        {
            return dimension == World.THE_END ? 1 : 0;
        }
    }

    public static boolean isNether(World world)
    {
        return world.getDimensionKey() == World.THE_NETHER;
    }

    public static boolean isOverworld(World world)
    {
        RegistryKey<World> registrykey = world.getDimensionKey();
        return getDimensionId(registrykey) == 0;
    }

    public static boolean isEnd(World world)
    {
        return world.getDimensionKey() == World.THE_END;
    }
}
