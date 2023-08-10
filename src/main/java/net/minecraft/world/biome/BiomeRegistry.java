package net.minecraft.world.biome;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;

public abstract class BiomeRegistry
{
    private static final Int2ObjectMap<RegistryKey<Biome>> idToKeyMap = new Int2ObjectArrayMap<>();
    public static final Biome PLAINS = register(1, Biomes.PLAINS, BiomeMaker.makePlainsBiome(false));
    public static final Biome THE_VOID = register(127, Biomes.THE_VOID, BiomeMaker.makeVoidBiome());

    private static Biome register(int id, RegistryKey<Biome> key, Biome biome)
    {
        idToKeyMap.put(id, key);
        return WorldGenRegistries.register(WorldGenRegistries.BIOME, id, key, biome);
    }

    public static RegistryKey<Biome> getKeyFromID(int id)
    {
        return idToKeyMap.get(id);
    }

    static
    {
        register(0, Biomes.OCEAN, BiomeMaker.makeOceanBiome(false));
        register(2, Biomes.DESERT, BiomeMaker.makeDesertBiome(0.125F, 0.05F, true, true, true));
        register(3, Biomes.MOUNTAINS, BiomeMaker.makeMountainBiome(1.0F, 0.5F, ConfiguredSurfaceBuilders.field_244181_m, false));
        register(4, Biomes.FOREST, BiomeMaker.makeForestBiome(0.1F, 0.2F));
        register(5, Biomes.TAIGA, BiomeMaker.makeTaigaBiome(0.2F, 0.2F, false, false, true, false));
        register(6, Biomes.SWAMP, BiomeMaker.makeGenericSwampBiome(-0.2F, 0.1F, false));
        register(7, Biomes.RIVER, BiomeMaker.makeRiverBiome(-0.5F, 0.0F, 0.5F, 4159204, false));
        register(8, Biomes.NETHER_WASTES, BiomeMaker.makeNetherWastesBiome());
        register(9, Biomes.THE_END, BiomeMaker.makeTheEndBiome());
        register(10, Biomes.FROZEN_OCEAN, BiomeMaker.makeFrozenOceanBiome(false));
        register(11, Biomes.FROZEN_RIVER, BiomeMaker.makeRiverBiome(-0.5F, 0.0F, 0.0F, 3750089, true));
        register(12, Biomes.SNOWY_TUNDRA, BiomeMaker.makeSnowyBiome(0.125F, 0.05F, false, false));
        register(13, Biomes.SNOWY_MOUNTAINS, BiomeMaker.makeSnowyBiome(0.45F, 0.3F, false, true));
        register(14, Biomes.MUSHROOM_FIELDS, BiomeMaker.makeMushroomBiome(0.2F, 0.3F));
        register(15, Biomes.MUSHROOM_FIELD_SHORE, BiomeMaker.makeMushroomBiome(0.0F, 0.025F));
        register(16, Biomes.BEACH, BiomeMaker.makeGenericBeachBiome(0.0F, 0.025F, 0.8F, 0.4F, 4159204, false, false));
        register(17, Biomes.DESERT_HILLS, BiomeMaker.makeDesertBiome(0.45F, 0.3F, false, true, false));
        register(18, Biomes.WOODED_HILLS, BiomeMaker.makeForestBiome(0.45F, 0.3F));
        register(19, Biomes.TAIGA_HILLS, BiomeMaker.makeTaigaBiome(0.45F, 0.3F, false, false, false, false));
        register(20, Biomes.MOUNTAIN_EDGE, BiomeMaker.makeMountainBiome(0.8F, 0.3F, ConfiguredSurfaceBuilders.field_244178_j, true));
        register(21, Biomes.JUNGLE, BiomeMaker.makeJungleBiome());
        register(22, Biomes.JUNGLE_HILLS, BiomeMaker.makeJungleHillsBiome());
        register(23, Biomes.JUNGLE_EDGE, BiomeMaker.makeJungleEdgeBiome());
        register(24, Biomes.DEEP_OCEAN, BiomeMaker.makeOceanBiome(true));
        register(25, Biomes.STONE_SHORE, BiomeMaker.makeGenericBeachBiome(0.1F, 0.8F, 0.2F, 0.3F, 4159204, false, true));
        register(26, Biomes.SNOWY_BEACH, BiomeMaker.makeGenericBeachBiome(0.0F, 0.025F, 0.05F, 0.3F, 4020182, true, false));
        register(27, Biomes.BIRCH_FOREST, BiomeMaker.makeBirchForestBiome(0.1F, 0.2F, false));
        register(28, Biomes.BIRCH_FOREST_HILLS, BiomeMaker.makeBirchForestBiome(0.45F, 0.3F, false));
        register(29, Biomes.DARK_FOREST, BiomeMaker.makeDarkForestBiome(0.1F, 0.2F, false));
        register(30, Biomes.SNOWY_TAIGA, BiomeMaker.makeTaigaBiome(0.2F, 0.2F, true, false, false, true));
        register(31, Biomes.SNOWY_TAIGA_HILLS, BiomeMaker.makeTaigaBiome(0.45F, 0.3F, true, false, false, false));
        register(32, Biomes.GIANT_TREE_TAIGA, BiomeMaker.makeGiantTaigaBiome(0.2F, 0.2F, 0.3F, false));
        register(33, Biomes.GIANT_TREE_TAIGA_HILLS, BiomeMaker.makeGiantTaigaBiome(0.45F, 0.3F, 0.3F, false));
        register(34, Biomes.WOODED_MOUNTAINS, BiomeMaker.makeMountainBiome(1.0F, 0.5F, ConfiguredSurfaceBuilders.field_244178_j, true));
        register(35, Biomes.SAVANNA, BiomeMaker.makeGenericSavannaBiome(0.125F, 0.05F, 1.2F, false, false));
        register(36, Biomes.SAVANNA_PLATEAU, BiomeMaker.makeSavannaPlateauBiome());
        register(37, Biomes.BADLANDS, BiomeMaker.makeBadlandsBiome(0.1F, 0.2F, false));
        register(38, Biomes.WOODED_BADLANDS_PLATEAU, BiomeMaker.makeWoodedBadlandsPlateauBiome(1.5F, 0.025F));
        register(39, Biomes.BADLANDS_PLATEAU, BiomeMaker.makeBadlandsBiome(1.5F, 0.025F, true));
        register(40, Biomes.SMALL_END_ISLANDS, BiomeMaker.makeSmallEndIslandsBiome());
        register(41, Biomes.END_MIDLANDS, BiomeMaker.makeEndMidlandsBiome());
        register(42, Biomes.END_HIGHLANDS, BiomeMaker.makeEndHighlandsBiome());
        register(43, Biomes.END_BARRENS, BiomeMaker.makeEndBarrensBiome());
        register(44, Biomes.WARM_OCEAN, BiomeMaker.makeWarmOceanBiome());
        register(45, Biomes.LUKEWARM_OCEAN, BiomeMaker.makeLukewarmOceanBiome(false));
        register(46, Biomes.COLD_OCEAN, BiomeMaker.makeColdOceanBiome(false));
        register(47, Biomes.DEEP_WARM_OCEAN, BiomeMaker.makeDeepWarmOceanBiome());
        register(48, Biomes.DEEP_LUKEWARM_OCEAN, BiomeMaker.makeLukewarmOceanBiome(true));
        register(49, Biomes.DEEP_COLD_OCEAN, BiomeMaker.makeColdOceanBiome(true));
        register(50, Biomes.DEEP_FROZEN_OCEAN, BiomeMaker.makeFrozenOceanBiome(true));
        register(129, Biomes.SUNFLOWER_PLAINS, BiomeMaker.makePlainsBiome(true));
        register(130, Biomes.DESERT_LAKES, BiomeMaker.makeDesertBiome(0.225F, 0.25F, false, false, false));
        register(131, Biomes.GRAVELLY_MOUNTAINS, BiomeMaker.makeMountainBiome(1.0F, 0.5F, ConfiguredSurfaceBuilders.field_244179_k, false));
        register(132, Biomes.FLOWER_FOREST, BiomeMaker.makeFlowerForestBiome());
        register(133, Biomes.TAIGA_MOUNTAINS, BiomeMaker.makeTaigaBiome(0.3F, 0.4F, false, true, false, false));
        register(134, Biomes.SWAMP_HILLS, BiomeMaker.makeGenericSwampBiome(-0.1F, 0.3F, true));
        register(140, Biomes.ICE_SPIKES, BiomeMaker.makeSnowyBiome(0.425F, 0.45000002F, true, false));
        register(149, Biomes.MODIFIED_JUNGLE, BiomeMaker.makeModifiedJungleBiome());
        register(151, Biomes.MODIFIED_JUNGLE_EDGE, BiomeMaker.makeModifiedJungleEdgeBiome());
        register(155, Biomes.TALL_BIRCH_FOREST, BiomeMaker.makeBirchForestBiome(0.2F, 0.4F, true));
        register(156, Biomes.TALL_BIRCH_HILLS, BiomeMaker.makeBirchForestBiome(0.55F, 0.5F, true));
        register(157, Biomes.DARK_FOREST_HILLS, BiomeMaker.makeDarkForestBiome(0.2F, 0.4F, true));
        register(158, Biomes.SNOWY_TAIGA_MOUNTAINS, BiomeMaker.makeTaigaBiome(0.3F, 0.4F, true, true, false, false));
        register(160, Biomes.GIANT_SPRUCE_TAIGA, BiomeMaker.makeGiantTaigaBiome(0.2F, 0.2F, 0.25F, true));
        register(161, Biomes.GIANT_SPRUCE_TAIGA_HILLS, BiomeMaker.makeGiantTaigaBiome(0.2F, 0.2F, 0.25F, true));
        register(162, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, BiomeMaker.makeMountainBiome(1.0F, 0.5F, ConfiguredSurfaceBuilders.field_244179_k, false));
        register(163, Biomes.SHATTERED_SAVANNA, BiomeMaker.makeGenericSavannaBiome(0.3625F, 1.225F, 1.1F, true, true));
        register(164, Biomes.SHATTERED_SAVANNA_PLATEAU, BiomeMaker.makeGenericSavannaBiome(1.05F, 1.2125001F, 1.0F, true, true));
        register(165, Biomes.ERODED_BADLANDS, BiomeMaker.makeErodedBadlandsBiome());
        register(166, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, BiomeMaker.makeWoodedBadlandsPlateauBiome(0.45F, 0.3F));
        register(167, Biomes.MODIFIED_BADLANDS_PLATEAU, BiomeMaker.makeBadlandsBiome(0.45F, 0.3F, true));
        register(168, Biomes.BAMBOO_JUNGLE, BiomeMaker.makeBambooJungleBiome());
        register(169, Biomes.BAMBOO_JUNGLE_HILLS, BiomeMaker.makeBambooJungleHillsBiome());
        register(170, Biomes.SOUL_SAND_VALLEY, BiomeMaker.makeSoulSandValleyBiome());
        register(171, Biomes.CRIMSON_FOREST, BiomeMaker.makeCrimsonForestBiome());
        register(172, Biomes.WARPED_FOREST, BiomeMaker.makeWarpedForestBiome());
        register(173, Biomes.BASALT_DELTAS, BiomeMaker.makeBasaltDeltasBiome());
    }
}
