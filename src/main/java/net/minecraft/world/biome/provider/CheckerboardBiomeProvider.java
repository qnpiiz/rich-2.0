package net.minecraft.world.biome.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.biome.Biome;

public class CheckerboardBiomeProvider extends BiomeProvider
{
    public static final Codec<CheckerboardBiomeProvider> CODEC = RecordCodecBuilder.create((checkerProviderCodecInstance) ->
    {
        return checkerProviderCodecInstance.group(Biome.BIOMES_CODEC.fieldOf("biomes").forGetter((checkerProvider) -> {
            return checkerProvider.biomes;
        }), Codec.intRange(0, 62).fieldOf("scale").orElse(2).forGetter((checkerProvider) -> {
            return checkerProvider.biomeScale;
        })).apply(checkerProviderCodecInstance, CheckerboardBiomeProvider::new);
    });
    private final List<Supplier<Biome>> biomes;
    private final int biomeScaleShift;
    private final int biomeScale;

    public CheckerboardBiomeProvider(List<Supplier<Biome>> biomes, int biomeScale)
    {
        super(biomes.stream());
        this.biomes = biomes;
        this.biomeScaleShift = biomeScale + 2;
        this.biomeScale = biomeScale;
    }

    protected Codec <? extends BiomeProvider > getBiomeProviderCodec()
    {
        return CODEC;
    }

    public BiomeProvider getBiomeProvider(long seed)
    {
        return this;
    }

    public Biome getNoiseBiome(int x, int y, int z)
    {
        return this.biomes.get(Math.floorMod((x >> this.biomeScaleShift) + (z >> this.biomeScaleShift), this.biomes.size())).get();
    }
}
