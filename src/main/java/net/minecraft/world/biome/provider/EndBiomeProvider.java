package net.minecraft.world.biome.provider;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.SimplexNoiseGenerator;

public class EndBiomeProvider extends BiomeProvider
{
    public static final Codec<EndBiomeProvider> CODEC = RecordCodecBuilder.create((builder) ->
    {
        return builder.group(RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter((provider) -> {
            return provider.lookupRegistry;
        }), Codec.LONG.fieldOf("seed").stable().forGetter((provider) -> {
            return provider.seed;
        })).apply(builder, builder.stable(EndBiomeProvider::new));
    });
    private final SimplexNoiseGenerator generator;
    private final Registry<Biome> lookupRegistry;
    private final long seed;
    private final Biome theEndBiome;
    private final Biome endHighlandsBiome;
    private final Biome endMidlandsBiome;
    private final Biome smallEndIslandsBiome;
    private final Biome endBarrensBiome;

    public EndBiomeProvider(Registry<Biome> lookupRegistry, long seed)
    {
        this(lookupRegistry, seed, lookupRegistry.getOrThrow(Biomes.THE_END), lookupRegistry.getOrThrow(Biomes.END_HIGHLANDS), lookupRegistry.getOrThrow(Biomes.END_MIDLANDS), lookupRegistry.getOrThrow(Biomes.SMALL_END_ISLANDS), lookupRegistry.getOrThrow(Biomes.END_BARRENS));
    }

    private EndBiomeProvider(Registry<Biome> lookupRegistry, long seed, Biome theEndBiome, Biome endHighlandsBiome, Biome endMidlandsBiome, Biome smallEndIslandsBiome, Biome endBarrensBiome)
    {
        super(ImmutableList.of(theEndBiome, endHighlandsBiome, endMidlandsBiome, smallEndIslandsBiome, endBarrensBiome));
        this.lookupRegistry = lookupRegistry;
        this.seed = seed;
        this.theEndBiome = theEndBiome;
        this.endHighlandsBiome = endHighlandsBiome;
        this.endMidlandsBiome = endMidlandsBiome;
        this.smallEndIslandsBiome = smallEndIslandsBiome;
        this.endBarrensBiome = endBarrensBiome;
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom(seed);
        sharedseedrandom.skip(17292);
        this.generator = new SimplexNoiseGenerator(sharedseedrandom);
    }

    protected Codec <? extends BiomeProvider > getBiomeProviderCodec()
    {
        return CODEC;
    }

    public BiomeProvider getBiomeProvider(long seed)
    {
        return new EndBiomeProvider(this.lookupRegistry, seed, this.theEndBiome, this.endHighlandsBiome, this.endMidlandsBiome, this.smallEndIslandsBiome, this.endBarrensBiome);
    }

    public Biome getNoiseBiome(int x, int y, int z)
    {
        int i = x >> 2;
        int j = z >> 2;

        if ((long)i * (long)i + (long)j * (long)j <= 4096L)
        {
            return this.theEndBiome;
        }
        else
        {
            float f = getRandomNoise(this.generator, i * 2 + 1, j * 2 + 1);

            if (f > 40.0F)
            {
                return this.endHighlandsBiome;
            }
            else if (f >= 0.0F)
            {
                return this.endMidlandsBiome;
            }
            else
            {
                return f < -20.0F ? this.smallEndIslandsBiome : this.endBarrensBiome;
            }
        }
    }

    public boolean areProvidersEqual(long seed)
    {
        return this.seed == seed;
    }

    /**
     * Generates a random noise value from -100 to 80 based on the current coordinates bitshifted right by 1
     */
    public static float getRandomNoise(SimplexNoiseGenerator noiseGenerator, int x, int z)
    {
        int i = x / 2;
        int j = z / 2;
        int k = x % 2;
        int l = z % 2;
        float f = 100.0F - MathHelper.sqrt((float)(x * x + z * z)) * 8.0F;
        f = MathHelper.clamp(f, -100.0F, 80.0F);

        for (int i1 = -12; i1 <= 12; ++i1)
        {
            for (int j1 = -12; j1 <= 12; ++j1)
            {
                long k1 = (long)(i + i1);
                long l1 = (long)(j + j1);

                if (k1 * k1 + l1 * l1 > 4096L && noiseGenerator.getValue((double)k1, (double)l1) < (double) - 0.9F)
                {
                    float f1 = (MathHelper.abs((float)k1) * 3439.0F + MathHelper.abs((float)l1) * 147.0F) % 13.0F + 9.0F;
                    float f2 = (float)(k - i1 * 2);
                    float f3 = (float)(l - j1 * 2);
                    float f4 = 100.0F - MathHelper.sqrt(f2 * f2 + f3 * f3) * f1;
                    f4 = MathHelper.clamp(f4, -100.0F, 80.0F);
                    f = Math.max(f, f4);
                }
            }
        }

        return f;
    }
}
