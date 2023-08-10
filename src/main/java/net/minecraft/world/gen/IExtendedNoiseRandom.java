package net.minecraft.world.gen;

import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public interface IExtendedNoiseRandom<R extends IArea> extends INoiseRandom
{
    void setPosition(long x, long z);

    R makeArea(IPixelTransformer pixelTransformer);

default R makeArea(IPixelTransformer pixelTransformer, R area)
    {
        return this.makeArea(pixelTransformer);
    }

default R makeArea(IPixelTransformer p_212860_1_, R firstArea, R secondArea)
    {
        return this.makeArea(p_212860_1_);
    }

default int pickRandom(int p_215715_1_, int p_215715_2_)
    {
        return this.random(2) == 0 ? p_215715_1_ : p_215715_2_;
    }

default int pickRandom(int p_215714_1_, int p_215714_2_, int p_215714_3_, int p_215714_4_)
    {
        int i = this.random(4);

        if (i == 0)
        {
            return p_215714_1_;
        }
        else if (i == 1)
        {
            return p_215714_2_;
        }
        else
        {
            return i == 2 ? p_215714_3_ : p_215714_4_;
        }
    }
}
