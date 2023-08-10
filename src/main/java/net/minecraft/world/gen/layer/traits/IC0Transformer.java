package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;

public interface IC0Transformer extends IAreaTransformer1, IDimOffset0Transformer
{
    int apply(INoiseRandom context, int value);

default int apply(IExtendedNoiseRandom<?> context, IArea area, int x, int z)
    {
        return this.apply(context, area.getValue(this.getOffsetX(x), this.getOffsetZ(z)));
    }
}
