package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public class BiomeLayer implements IC0Transformer
{
    private static final int[] field_202743_q = new int[] {2, 4, 3, 6, 1, 5};
    private static final int[] field_202744_r = new int[] {2, 2, 2, 35, 35, 1};
    private static final int[] field_202745_s = new int[] {4, 29, 3, 1, 27, 6};
    private static final int[] field_202746_t = new int[] {4, 3, 5, 1};
    private static final int[] field_202747_u = new int[] {12, 12, 12, 30};
    private int[] warmBiomes = field_202744_r;

    public BiomeLayer(boolean p_i232147_1_)
    {
        if (p_i232147_1_)
        {
            this.warmBiomes = field_202743_q;
        }
    }

    public int apply(INoiseRandom context, int value)
    {
        int i = (value & 3840) >> 8;
        value = value & -3841;

        if (!LayerUtil.isOcean(value) && value != 14)
        {
            switch (value)
            {
                case 1:
                    if (i > 0)
                    {
                        return context.random(3) == 0 ? 39 : 38;
                    }

                    return this.warmBiomes[context.random(this.warmBiomes.length)];

                case 2:
                    if (i > 0)
                    {
                        return 21;
                    }

                    return field_202745_s[context.random(field_202745_s.length)];

                case 3:
                    if (i > 0)
                    {
                        return 32;
                    }

                    return field_202746_t[context.random(field_202746_t.length)];

                case 4:
                    return field_202747_u[context.random(field_202747_u.length)];

                default:
                    return 14;
            }
        }
        else
        {
            return value;
        }
    }
}
