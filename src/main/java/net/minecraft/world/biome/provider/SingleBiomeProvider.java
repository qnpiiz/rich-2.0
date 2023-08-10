package net.minecraft.world.biome.provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class SingleBiomeProvider extends BiomeProvider
{
    public static final Codec<SingleBiomeProvider> field_235260_e_ = Biome.BIOME_CODEC.fieldOf("biome").xmap(SingleBiomeProvider::new, (provider) ->
    {
        return provider.biome;
    }).stable().codec();
    private final Supplier<Biome> biome;

    public SingleBiomeProvider(Biome p_i46709_1_)
    {
        this(() ->
        {
            return p_i46709_1_;
        });
    }

    public SingleBiomeProvider(Supplier<Biome> biome)
    {
        super(ImmutableList.of(biome.get()));
        this.biome = biome;
    }

    protected Codec <? extends BiomeProvider > getBiomeProviderCodec()
    {
        return field_235260_e_;
    }

    public BiomeProvider getBiomeProvider(long seed)
    {
        return this;
    }

    public Biome getNoiseBiome(int x, int y, int z)
    {
        return this.biome.get();
    }

    @Nullable
    public BlockPos findBiomePosition(int x, int y, int z, int radius, int increment, Predicate<Biome> biomes, Random rand, boolean findClosest)
    {
        if (biomes.test(this.biome.get()))
        {
            return findClosest ? new BlockPos(x, y, z) : new BlockPos(x - radius + rand.nextInt(radius * 2 + 1), y, z - radius + rand.nextInt(radius * 2 + 1));
        }
        else
        {
            return null;
        }
    }

    public Set<Biome> getBiomes(int xIn, int yIn, int zIn, int radius)
    {
        return Sets.newHashSet(this.biome.get());
    }
}
