package net.minecraft.world.gen.area;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public final class LazyArea implements IArea
{
    private final IPixelTransformer pixelTransformer;
    private final Long2IntLinkedOpenHashMap cachedValues;
    private final int maxCacheSize;

    public LazyArea(Long2IntLinkedOpenHashMap cachedValues, int maxCacheSize, IPixelTransformer pixelTransformer)
    {
        this.cachedValues = cachedValues;
        this.maxCacheSize = maxCacheSize;
        this.pixelTransformer = pixelTransformer;
    }

    public int getValue(int x, int z)
    {
        long i = ChunkPos.asLong(x, z);

        synchronized (this.cachedValues)
        {
            int j = this.cachedValues.get(i);

            if (j != Integer.MIN_VALUE)
            {
                return j;
            }
            else
            {
                int k = this.pixelTransformer.apply(x, z);
                this.cachedValues.put(i, k);

                if (this.cachedValues.size() > this.maxCacheSize)
                {
                    for (int l = 0; l < this.maxCacheSize / 16; ++l)
                    {
                        this.cachedValues.removeFirstInt();
                    }
                }

                return k;
            }
        }
    }

    public int getmaxCacheSize()
    {
        return this.maxCacheSize;
    }
}
