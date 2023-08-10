package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer1 extends IDimTransformer
{
default <R extends IArea> IAreaFactory<R> apply(IExtendedNoiseRandom<R> context, IAreaFactory<R> areaFactory)
    {
        return () ->
        {
            R r = areaFactory.make();
            return context.makeArea((p_202711_3_, p_202711_4_) -> {
                context.setPosition((long)p_202711_3_, (long)p_202711_4_);
                return this.apply(context, r, p_202711_3_, p_202711_4_);
            }, r);
        };
    }

    int apply(IExtendedNoiseRandom<?> context, IArea area, int x, int z);
}
