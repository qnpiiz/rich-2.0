package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;

public interface IBishopTransformer extends IAreaTransformer1, IDimOffset1Transformer
{
    int apply(INoiseRandom context, int x, int southEast, int p_202792_4_, int p_202792_5_, int p_202792_6_);

default int apply(IExtendedNoiseRandom<?> context, IArea area, int x, int z)
    {
        return this.apply(context, area.getValue(this.getOffsetX(x + 0), this.getOffsetZ(z + 2)), area.getValue(this.getOffsetX(x + 2), this.getOffsetZ(z + 2)), area.getValue(this.getOffsetX(x + 2), this.getOffsetZ(z + 0)), area.getValue(this.getOffsetX(x + 0), this.getOffsetZ(z + 0)), area.getValue(this.getOffsetX(x + 1), this.getOffsetZ(z + 1)));
    }
}
