package net.minecraft.world.biome;

import javax.annotation.Nullable;
import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.provider.BiomeProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeContainer implements BiomeManager.IBiomeReader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int WIDTH_BITS = (int)Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;
    private static final int HEIGHT_BITS = (int)Math.round(Math.log(256.0D) / Math.log(2.0D)) - 2;
    public static final int BIOMES_SIZE = 1 << WIDTH_BITS + WIDTH_BITS + HEIGHT_BITS;
    public static final int HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1;
    public static final int VERTICAL_MASK = (1 << HEIGHT_BITS) - 1;
    private final IObjectIntIterable<Biome> biomeRegistry;
    private final Biome[] biomes;

    public BiomeContainer(IObjectIntIterable<Biome> biomeRegistry, Biome[] biomes)
    {
        this.biomeRegistry = biomeRegistry;
        this.biomes = biomes;
    }

    private BiomeContainer(IObjectIntIterable<Biome> biomeRegistry)
    {
        this(biomeRegistry, new Biome[BIOMES_SIZE]);
    }

    public BiomeContainer(IObjectIntIterable<Biome> biomeRegistry, int[] biomes)
    {
        this(biomeRegistry);

        for (int i = 0; i < this.biomes.length; ++i)
        {
            int j = biomes[i];
            Biome biome = biomeRegistry.getByValue(j);

            if (biome == null)
            {
                LOGGER.warn("Received invalid biome id: " + j);
                this.biomes[i] = biomeRegistry.getByValue(0);
            }
            else
            {
                this.biomes[i] = biome;
            }
        }
    }

    public BiomeContainer(IObjectIntIterable<Biome> biomeRegistry, ChunkPos chunkPos, BiomeProvider provider)
    {
        this(biomeRegistry);
        int i = chunkPos.getXStart() >> 2;
        int j = chunkPos.getZStart() >> 2;

        for (int k = 0; k < this.biomes.length; ++k)
        {
            int l = k & HORIZONTAL_MASK;
            int i1 = k >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK;
            int j1 = k >> WIDTH_BITS & HORIZONTAL_MASK;
            this.biomes[k] = provider.getNoiseBiome(i + l, i1, j + j1);
        }
    }

    public BiomeContainer(IObjectIntIterable<Biome> biomeRegistry, ChunkPos chunkPos, BiomeProvider provider, @Nullable int[] biomes)
    {
        this(biomeRegistry);
        int i = chunkPos.getXStart() >> 2;
        int j = chunkPos.getZStart() >> 2;

        if (biomes != null)
        {
            for (int k = 0; k < biomes.length; ++k)
            {
                this.biomes[k] = biomeRegistry.getByValue(biomes[k]);

                if (this.biomes[k] == null)
                {
                    int l = k & HORIZONTAL_MASK;
                    int i1 = k >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK;
                    int j1 = k >> WIDTH_BITS & HORIZONTAL_MASK;
                    this.biomes[k] = provider.getNoiseBiome(i + l, i1, j + j1);
                }
            }
        }
        else
        {
            for (int k1 = 0; k1 < this.biomes.length; ++k1)
            {
                int l1 = k1 & HORIZONTAL_MASK;
                int i2 = k1 >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK;
                int j2 = k1 >> WIDTH_BITS & HORIZONTAL_MASK;
                this.biomes[k1] = provider.getNoiseBiome(i + l1, i2, j + j2);
            }
        }
    }

    public int[] getBiomeIds()
    {
        int[] aint = new int[this.biomes.length];

        for (int i = 0; i < this.biomes.length; ++i)
        {
            aint[i] = this.biomeRegistry.getId(this.biomes[i]);
        }

        return aint;
    }

    public Biome getNoiseBiome(int x, int y, int z)
    {
        int i = x & HORIZONTAL_MASK;
        int j = MathHelper.clamp(y, 0, VERTICAL_MASK);
        int k = z & HORIZONTAL_MASK;
        return this.biomes[j << WIDTH_BITS + WIDTH_BITS | k << WIDTH_BITS | i];
    }
}
