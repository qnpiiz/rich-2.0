package net.minecraft.block;

import java.util.Random;

public class PlantBlockHelper
{
    public static boolean isAir(BlockState state)
    {
        return state.isAir();
    }

    public static int getGrowthAmount(Random rand)
    {
        double d0 = 1.0D;
        int i;

        for (i = 0; rand.nextDouble() < d0; ++i)
        {
            d0 *= 0.826D;
        }

        return i;
    }
}
