package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum RiverLayer implements ICastleTransformer
{
    INSTANCE;

    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        int i = riverFilter(center);
        return i == riverFilter(east) && i == riverFilter(north) && i == riverFilter(west) && i == riverFilter(south) ? -1 : 7;
    }

    private static int riverFilter(int p_151630_0_)
    {
        return p_151630_0_ >= 2 ? 2 + (p_151630_0_ & 1) : p_151630_0_;
    }
}
