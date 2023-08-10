package net.optifine.render;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.renderer.RenderType;

public class ChunkLayerMap<T>
{
    private T[] values = (T[])(new Object[RenderType.CHUNK_RENDER_TYPES.length]);
    private Supplier<T> defaultValue;

    public ChunkLayerMap(Function<RenderType, T> initialValue)
    {
        RenderType[] arendertype = RenderType.CHUNK_RENDER_TYPES;
        this.values = (T[])(new Object[arendertype.length]);

        for (int i = 0; i < arendertype.length; ++i)
        {
            RenderType rendertype = arendertype[i];
            T t = initialValue.apply(rendertype);
            this.values[rendertype.ordinal()] = t;
        }

        for (int j = 0; j < this.values.length; ++j)
        {
            if (this.values[j] == null)
            {
                throw new RuntimeException("Missing value at index: " + j);
            }
        }
    }

    public T get(RenderType layer)
    {
        return this.values[layer.ordinal()];
    }

    public Collection<T> values()
    {
        return Arrays.asList(this.values);
    }
}
