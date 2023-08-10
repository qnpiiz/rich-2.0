package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum MixOceansLayer implements IAreaTransformer2, IDimOffset0Transformer
{
    INSTANCE;

    public int apply(INoiseRandom p_215723_1_, IArea p_215723_2_, IArea p_215723_3_, int p_215723_4_, int p_215723_5_)
    {
        int i = p_215723_2_.getValue(this.getOffsetX(p_215723_4_), this.getOffsetZ(p_215723_5_));
        int j = p_215723_3_.getValue(this.getOffsetX(p_215723_4_), this.getOffsetZ(p_215723_5_));

        if (!LayerUtil.isOcean(i))
        {
            return i;
        }
        else
        {
            int k = 8;
            int l = 4;

            for (int i1 = -8; i1 <= 8; i1 += 4)
            {
                for (int j1 = -8; j1 <= 8; j1 += 4)
                {
                    int k1 = p_215723_2_.getValue(this.getOffsetX(p_215723_4_ + i1), this.getOffsetZ(p_215723_5_ + j1));

                    if (!LayerUtil.isOcean(k1))
                    {
                        if (j == 44)
                        {
                            return 45;
                        }

                        if (j == 10)
                        {
                            return 46;
                        }
                    }
                }
            }

            if (i == 24)
            {
                if (j == 45)
                {
                    return 48;
                }

                if (j == 0)
                {
                    return 24;
                }

                if (j == 46)
                {
                    return 49;
                }

                if (j == 10)
                {
                    return 50;
                }
            }

            return j;
        }
    }
}
