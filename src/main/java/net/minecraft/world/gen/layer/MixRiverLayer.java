package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum MixRiverLayer implements IAreaTransformer2, IDimOffset0Transformer
{
    INSTANCE;

    public int apply(INoiseRandom p_215723_1_, IArea p_215723_2_, IArea p_215723_3_, int p_215723_4_, int p_215723_5_)
    {
        int i = p_215723_2_.getValue(this.getOffsetX(p_215723_4_), this.getOffsetZ(p_215723_5_));
        int j = p_215723_3_.getValue(this.getOffsetX(p_215723_4_), this.getOffsetZ(p_215723_5_));

        if (LayerUtil.isOcean(i))
        {
            return i;
        }
        else if (j == 7)
        {
            if (i == 12)
            {
                return 11;
            }
            else
            {
                return i != 14 && i != 15 ? j & 255 : 15;
            }
        }
        else
        {
            return i;
        }
    }
}
