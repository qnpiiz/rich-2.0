package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;
import net.minecraft.util.FastRandom;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public class LazyAreaLayerContext implements IExtendedNoiseRandom<LazyArea>
{
    private final Long2IntLinkedOpenHashMap cache;
    private final int maxCacheSize;
    private final ImprovedNoiseGenerator noise;
    private final long seed;
    private long positionSeed;

    public LazyAreaLayerContext(int maxCacheSizeIn, long seedIn, long seedModifierIn)
    {
        this.seed = hash(seedIn, seedModifierIn);
        this.noise = new ImprovedNoiseGenerator(new Random(seedIn));
        this.cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
        this.cache.defaultReturnValue(Integer.MIN_VALUE);
        this.maxCacheSize = maxCacheSizeIn;
    }

    public LazyArea makeArea(IPixelTransformer pixelTransformer)
    {
        return new LazyArea(this.cache, this.maxCacheSize, pixelTransformer);
    }

    public LazyArea makeArea(IPixelTransformer pixelTransformer, LazyArea area)
    {
        return new LazyArea(this.cache, Math.min(1024, area.getmaxCacheSize() * 4), pixelTransformer);
    }

    public LazyArea makeArea(IPixelTransformer p_212860_1_, LazyArea firstArea, LazyArea secondArea)
    {
        return new LazyArea(this.cache, Math.min(1024, Math.max(firstArea.getmaxCacheSize(), secondArea.getmaxCacheSize()) * 4), p_212860_1_);
    }

    public void setPosition(long x, long z)
    {
        long i = this.seed;
        i = FastRandom.mix(i, x);
        i = FastRandom.mix(i, z);
        i = FastRandom.mix(i, x);
        i = FastRandom.mix(i, z);
        this.positionSeed = i;
    }

    public int random(int bound)
    {
        int i = (int)Math.floorMod(this.positionSeed >> 24, (long)bound);
        this.positionSeed = FastRandom.mix(this.positionSeed, this.seed);
        return i;
    }

    public ImprovedNoiseGenerator getNoiseGenerator()
    {
        return this.noise;
    }

    private static long hash(long left, long right)
    {
        long lvt_4_1_ = FastRandom.mix(right, right);
        lvt_4_1_ = FastRandom.mix(lvt_4_1_, right);
        lvt_4_1_ = FastRandom.mix(lvt_4_1_, right);
        long lvt_6_1_ = FastRandom.mix(left, lvt_4_1_);
        lvt_6_1_ = FastRandom.mix(lvt_6_1_, lvt_4_1_);
        return FastRandom.mix(lvt_6_1_, lvt_4_1_);
    }
}
