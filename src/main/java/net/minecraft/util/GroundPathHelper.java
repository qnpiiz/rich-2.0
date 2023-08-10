package net.minecraft.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.GroundPathNavigator;

public class GroundPathHelper
{
    public static boolean isGroundNavigator(MobEntity mob)
    {
        return mob.getNavigator() instanceof GroundPathNavigator;
    }
}
