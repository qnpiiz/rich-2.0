package net.minecraft.world.biome.provider;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.LayerUtil;

public class OverworldBiomeProvider extends BiomeProvider
{
    public static final Codec<OverworldBiomeProvider> CODEC = RecordCodecBuilder.create((builder) ->
    {
        return builder.group(Codec.LONG.fieldOf("seed").stable().forGetter((overworldProvider) -> {
            return overworldProvider.seed;
        }), Codec.BOOL.optionalFieldOf("legacy_biome_init_layer", Boolean.valueOf(false), Lifecycle.stable()).forGetter((overworldProvider) -> {
            return overworldProvider.legacyBiomes;
        }), Codec.BOOL.fieldOf("large_biomes").orElse(false).stable().forGetter((overworldProvider) -> {
            return overworldProvider.largeBiomes;
        }), RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter((overworldProvider) -> {
            return overworldProvider.lookupRegistry;
        })).apply(builder, builder.stable(OverworldBiomeProvider::new));
    });
    private final Layer genBiomes;
    private static final List<RegistryKey<Biome>> biomes = ImmutableList.of(Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.MOUNTAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMP, Biomes.RIVER, Biomes.FROZEN_OCEAN, Biomes.FROZEN_RIVER, Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS, Biomes.MUSHROOM_FIELDS, Biomes.MUSHROOM_FIELD_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.WOODED_HILLS, Biomes.TAIGA_HILLS, Biomes.MOUNTAIN_EDGE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_SHORE, Biomes.SNOWY_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.DARK_FOREST, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.WOODED_MOUNTAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.BADLANDS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.BADLANDS_PLATEAU, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SUNFLOWER_PLAINS, Biomes.DESERT_LAKES, Biomes.GRAVELLY_MOUNTAINS, Biomes.FLOWER_FOREST, Biomes.TAIGA_MOUNTAINS, Biomes.SWAMP_HILLS, Biomes.ICE_SPIKES, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE, Biomes.TALL_BIRCH_FOREST, Biomes.TALL_BIRCH_HILLS, Biomes.DARK_FOREST_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SHATTERED_SAVANNA, Biomes.SHATTERED_SAVANNA_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.MODIFIED_BADLANDS_PLATEAU);
    private final long seed;
    private final boolean legacyBiomes;
    private final boolean largeBiomes;
    private final Registry<Biome> lookupRegistry;

    public OverworldBiomeProvider(long seed, boolean legacyBiomes, boolean largeBiomes, Registry<Biome> lookupRegistry)
    {
        super(biomes.stream().map((key) ->
        {
            return () -> {
                return lookupRegistry.getOrThrow(key);
            };
        }));
        this.seed = seed;
        this.legacyBiomes = legacyBiomes;
        this.largeBiomes = largeBiomes;
        this.lookupRegistry = lookupRegistry;
        this.genBiomes = LayerUtil.func_237215_a_(seed, legacyBiomes, largeBiomes ? 6 : 4, 4);
    }

    protected Codec <? extends BiomeProvider > getBiomeProviderCodec()
    {
        return CODEC;
    }

    public BiomeProvider getBiomeProvider(long seed)
    {
        return new OverworldBiomeProvider(seed, this.legacyBiomes, this.largeBiomes, this.lookupRegistry);
    }

    public Biome getNoiseBiome(int x, int y, int z)
    {
        return this.genBiomes.func_242936_a(this.lookupRegistry, x, z);
    }
}
