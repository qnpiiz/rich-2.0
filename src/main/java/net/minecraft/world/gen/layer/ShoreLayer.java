package net.minecraft.world.gen.layer;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum ShoreLayer implements ICastleTransformer
{
    INSTANCE;

    private static final IntSet field_242942_b = new IntOpenHashSet(new int[]{26, 11, 12, 13, 140, 30, 31, 158, 10});
    private static final IntSet field_242943_c = new IntOpenHashSet(new int[]{168, 169, 21, 22, 23, 149, 151});

    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (center == 14)
        {
            if (LayerUtil.isShallowOcean(north) || LayerUtil.isShallowOcean(west) || LayerUtil.isShallowOcean(south) || LayerUtil.isShallowOcean(east))
            {
                return 15;
            }
        }
        else if (field_242943_c.contains(center))
        {
            if (!isJungleCompatible(north) || !isJungleCompatible(west) || !isJungleCompatible(south) || !isJungleCompatible(east))
            {
                return 23;
            }

            if (LayerUtil.isOcean(north) || LayerUtil.isOcean(west) || LayerUtil.isOcean(south) || LayerUtil.isOcean(east))
            {
                return 16;
            }
        }
        else if (center != 3 && center != 34 && center != 20)
        {
            if (field_242942_b.contains(center))
            {
                if (!LayerUtil.isOcean(center) && (LayerUtil.isOcean(north) || LayerUtil.isOcean(west) || LayerUtil.isOcean(south) || LayerUtil.isOcean(east)))
                {
                    return 26;
                }
            }
            else if (center != 37 && center != 38)
            {
                if (!LayerUtil.isOcean(center) && center != 7 && center != 6 && (LayerUtil.isOcean(north) || LayerUtil.isOcean(west) || LayerUtil.isOcean(south) || LayerUtil.isOcean(east)))
                {
                    return 16;
                }
            }
            else if (!LayerUtil.isOcean(north) && !LayerUtil.isOcean(west) && !LayerUtil.isOcean(south) && !LayerUtil.isOcean(east) && (!this.isMesa(north) || !this.isMesa(west) || !this.isMesa(south) || !this.isMesa(east)))
            {
                return 2;
            }
        }
        else if (!LayerUtil.isOcean(center) && (LayerUtil.isOcean(north) || LayerUtil.isOcean(west) || LayerUtil.isOcean(south) || LayerUtil.isOcean(east)))
        {
            return 25;
        }

        return center;
    }

    private static boolean isJungleCompatible(int p_151631_0_)
    {
        return field_242943_c.contains(p_151631_0_) || p_151631_0_ == 4 || p_151631_0_ == 5 || LayerUtil.isOcean(p_151631_0_);
    }

    private boolean isMesa(int p_151633_1_)
    {
        return p_151633_1_ == 37 || p_151633_1_ == 38 || p_151633_1_ == 39 || p_151633_1_ == 165 || p_151633_1_ == 166 || p_151633_1_ == 167;
    }
}
