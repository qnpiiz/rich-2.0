package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum EdgeBiomeLayer implements ICastleTransformer
{
    INSTANCE;

    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        int[] aint = new int[1];

        if (!this.func_242935_a(aint, center) && !this.replaceBiomeEdge(aint, north, west, south, east, center, 38, 37) && !this.replaceBiomeEdge(aint, north, west, south, east, center, 39, 37) && !this.replaceBiomeEdge(aint, north, west, south, east, center, 32, 5))
        {
            if (center != 2 || north != 12 && west != 12 && east != 12 && south != 12)
            {
                if (center == 6)
                {
                    if (north == 2 || west == 2 || east == 2 || south == 2 || north == 30 || west == 30 || east == 30 || south == 30 || north == 12 || west == 12 || east == 12 || south == 12)
                    {
                        return 1;
                    }

                    if (north == 21 || south == 21 || west == 21 || east == 21 || north == 168 || south == 168 || west == 168 || east == 168)
                    {
                        return 23;
                    }
                }

                return center;
            }
            else
            {
                return 34;
            }
        }
        else
        {
            return aint[0];
        }
    }

    private boolean func_242935_a(int[] p_242935_1_, int p_242935_2_)
    {
        if (!LayerUtil.areBiomesSimilar(p_242935_2_, 3))
        {
            return false;
        }
        else
        {
            p_242935_1_[0] = p_242935_2_;
            return true;
        }
    }

    /**
     * Creates a border around a biome.
     */
    private boolean replaceBiomeEdge(int[] p_151635_1_, int p_151635_2_, int p_151635_3_, int p_151635_4_, int p_151635_5_, int p_151635_6_, int p_151635_7_, int p_151635_8_)
    {
        if (p_151635_6_ != p_151635_7_)
        {
            return false;
        }
        else
        {
            if (LayerUtil.areBiomesSimilar(p_151635_2_, p_151635_7_) && LayerUtil.areBiomesSimilar(p_151635_3_, p_151635_7_) && LayerUtil.areBiomesSimilar(p_151635_5_, p_151635_7_) && LayerUtil.areBiomesSimilar(p_151635_4_, p_151635_7_))
            {
                p_151635_1_[0] = p_151635_6_;
            }
            else
            {
                p_151635_1_[0] = p_151635_8_;
            }

            return true;
        }
    }
}
