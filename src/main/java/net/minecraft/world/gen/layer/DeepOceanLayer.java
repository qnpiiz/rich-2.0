package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum DeepOceanLayer implements ICastleTransformer
{
    INSTANCE;

    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (LayerUtil.isShallowOcean(center))
        {
            int i = 0;

            if (LayerUtil.isShallowOcean(north))
            {
                ++i;
            }

            if (LayerUtil.isShallowOcean(west))
            {
                ++i;
            }

            if (LayerUtil.isShallowOcean(east))
            {
                ++i;
            }

            if (LayerUtil.isShallowOcean(south))
            {
                ++i;
            }

            if (i > 3)
            {
                if (center == 44)
                {
                    return 47;
                }

                if (center == 45)
                {
                    return 48;
                }

                if (center == 0)
                {
                    return 24;
                }

                if (center == 46)
                {
                    return 49;
                }

                if (center == 10)
                {
                    return 50;
                }

                return 24;
            }
        }

        return center;
    }
}
