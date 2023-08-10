package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;

public interface ICastleTransformer extends IAreaTransformer1, IDimOffset1Transformer
{
    int apply(INoiseRandom context, int north, int west, int south, int east, int center);

default int apply(IExtendedNoiseRandom<?> context, IArea area, int x, int z)
    {
        return this.apply(context, area.getValue(this.getOffsetX(x + 1), this.getOffsetZ(z + 0)), area.getValue(this.getOffsetX(x + 2), this.getOffsetZ(z + 1)), area.getValue(this.getOffsetX(x + 1), this.getOffsetZ(z + 2)), area.getValue(this.getOffsetX(x + 0), this.getOffsetZ(z + 1)), area.getValue(this.getOffsetX(x + 1), this.getOffsetZ(z + 1)));
    }
}
