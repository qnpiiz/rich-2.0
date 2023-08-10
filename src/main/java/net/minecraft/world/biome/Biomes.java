package net.minecraft.world.biome;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public abstract class Biomes
{
    public static final RegistryKey<Biome> OCEAN = makeKey("ocean");
    public static final RegistryKey<Biome> PLAINS = makeKey("plains");
    public static final RegistryKey<Biome> DESERT = makeKey("desert");
    public static final RegistryKey<Biome> MOUNTAINS = makeKey("mountains");
    public static final RegistryKey<Biome> FOREST = makeKey("forest");
    public static final RegistryKey<Biome> TAIGA = makeKey("taiga");
    public static final RegistryKey<Biome> SWAMP = makeKey("swamp");
    public static final RegistryKey<Biome> RIVER = makeKey("river");
    public static final RegistryKey<Biome> NETHER_WASTES = makeKey("nether_wastes");
    public static final RegistryKey<Biome> THE_END = makeKey("the_end");
    public static final RegistryKey<Biome> FROZEN_OCEAN = makeKey("frozen_ocean");
    public static final RegistryKey<Biome> FROZEN_RIVER = makeKey("frozen_river");
    public static final RegistryKey<Biome> SNOWY_TUNDRA = makeKey("snowy_tundra");
    public static final RegistryKey<Biome> SNOWY_MOUNTAINS = makeKey("snowy_mountains");
    public static final RegistryKey<Biome> MUSHROOM_FIELDS = makeKey("mushroom_fields");
    public static final RegistryKey<Biome> MUSHROOM_FIELD_SHORE = makeKey("mushroom_field_shore");
    public static final RegistryKey<Biome> BEACH = makeKey("beach");
    public static final RegistryKey<Biome> DESERT_HILLS = makeKey("desert_hills");
    public static final RegistryKey<Biome> WOODED_HILLS = makeKey("wooded_hills");
    public static final RegistryKey<Biome> TAIGA_HILLS = makeKey("taiga_hills");
    public static final RegistryKey<Biome> MOUNTAIN_EDGE = makeKey("mountain_edge");
    public static final RegistryKey<Biome> JUNGLE = makeKey("jungle");
    public static final RegistryKey<Biome> JUNGLE_HILLS = makeKey("jungle_hills");
    public static final RegistryKey<Biome> JUNGLE_EDGE = makeKey("jungle_edge");
    public static final RegistryKey<Biome> DEEP_OCEAN = makeKey("deep_ocean");
    public static final RegistryKey<Biome> STONE_SHORE = makeKey("stone_shore");
    public static final RegistryKey<Biome> SNOWY_BEACH = makeKey("snowy_beach");
    public static final RegistryKey<Biome> BIRCH_FOREST = makeKey("birch_forest");
    public static final RegistryKey<Biome> BIRCH_FOREST_HILLS = makeKey("birch_forest_hills");
    public static final RegistryKey<Biome> DARK_FOREST = makeKey("dark_forest");
    public static final RegistryKey<Biome> SNOWY_TAIGA = makeKey("snowy_taiga");
    public static final RegistryKey<Biome> SNOWY_TAIGA_HILLS = makeKey("snowy_taiga_hills");
    public static final RegistryKey<Biome> GIANT_TREE_TAIGA = makeKey("giant_tree_taiga");
    public static final RegistryKey<Biome> GIANT_TREE_TAIGA_HILLS = makeKey("giant_tree_taiga_hills");
    public static final RegistryKey<Biome> WOODED_MOUNTAINS = makeKey("wooded_mountains");
    public static final RegistryKey<Biome> SAVANNA = makeKey("savanna");
    public static final RegistryKey<Biome> SAVANNA_PLATEAU = makeKey("savanna_plateau");
    public static final RegistryKey<Biome> BADLANDS = makeKey("badlands");
    public static final RegistryKey<Biome> WOODED_BADLANDS_PLATEAU = makeKey("wooded_badlands_plateau");
    public static final RegistryKey<Biome> BADLANDS_PLATEAU = makeKey("badlands_plateau");
    public static final RegistryKey<Biome> SMALL_END_ISLANDS = makeKey("small_end_islands");
    public static final RegistryKey<Biome> END_MIDLANDS = makeKey("end_midlands");
    public static final RegistryKey<Biome> END_HIGHLANDS = makeKey("end_highlands");
    public static final RegistryKey<Biome> END_BARRENS = makeKey("end_barrens");
    public static final RegistryKey<Biome> WARM_OCEAN = makeKey("warm_ocean");
    public static final RegistryKey<Biome> LUKEWARM_OCEAN = makeKey("lukewarm_ocean");
    public static final RegistryKey<Biome> COLD_OCEAN = makeKey("cold_ocean");
    public static final RegistryKey<Biome> DEEP_WARM_OCEAN = makeKey("deep_warm_ocean");
    public static final RegistryKey<Biome> DEEP_LUKEWARM_OCEAN = makeKey("deep_lukewarm_ocean");
    public static final RegistryKey<Biome> DEEP_COLD_OCEAN = makeKey("deep_cold_ocean");
    public static final RegistryKey<Biome> DEEP_FROZEN_OCEAN = makeKey("deep_frozen_ocean");
    public static final RegistryKey<Biome> THE_VOID = makeKey("the_void");
    public static final RegistryKey<Biome> SUNFLOWER_PLAINS = makeKey("sunflower_plains");
    public static final RegistryKey<Biome> DESERT_LAKES = makeKey("desert_lakes");
    public static final RegistryKey<Biome> GRAVELLY_MOUNTAINS = makeKey("gravelly_mountains");
    public static final RegistryKey<Biome> FLOWER_FOREST = makeKey("flower_forest");
    public static final RegistryKey<Biome> TAIGA_MOUNTAINS = makeKey("taiga_mountains");
    public static final RegistryKey<Biome> SWAMP_HILLS = makeKey("swamp_hills");
    public static final RegistryKey<Biome> ICE_SPIKES = makeKey("ice_spikes");
    public static final RegistryKey<Biome> MODIFIED_JUNGLE = makeKey("modified_jungle");
    public static final RegistryKey<Biome> MODIFIED_JUNGLE_EDGE = makeKey("modified_jungle_edge");
    public static final RegistryKey<Biome> TALL_BIRCH_FOREST = makeKey("tall_birch_forest");
    public static final RegistryKey<Biome> TALL_BIRCH_HILLS = makeKey("tall_birch_hills");
    public static final RegistryKey<Biome> DARK_FOREST_HILLS = makeKey("dark_forest_hills");
    public static final RegistryKey<Biome> SNOWY_TAIGA_MOUNTAINS = makeKey("snowy_taiga_mountains");
    public static final RegistryKey<Biome> GIANT_SPRUCE_TAIGA = makeKey("giant_spruce_taiga");
    public static final RegistryKey<Biome> GIANT_SPRUCE_TAIGA_HILLS = makeKey("giant_spruce_taiga_hills");
    public static final RegistryKey<Biome> MODIFIED_GRAVELLY_MOUNTAINS = makeKey("modified_gravelly_mountains");
    public static final RegistryKey<Biome> SHATTERED_SAVANNA = makeKey("shattered_savanna");
    public static final RegistryKey<Biome> SHATTERED_SAVANNA_PLATEAU = makeKey("shattered_savanna_plateau");
    public static final RegistryKey<Biome> ERODED_BADLANDS = makeKey("eroded_badlands");
    public static final RegistryKey<Biome> MODIFIED_WOODED_BADLANDS_PLATEAU = makeKey("modified_wooded_badlands_plateau");
    public static final RegistryKey<Biome> MODIFIED_BADLANDS_PLATEAU = makeKey("modified_badlands_plateau");
    public static final RegistryKey<Biome> BAMBOO_JUNGLE = makeKey("bamboo_jungle");
    public static final RegistryKey<Biome> BAMBOO_JUNGLE_HILLS = makeKey("bamboo_jungle_hills");
    public static final RegistryKey<Biome> SOUL_SAND_VALLEY = makeKey("soul_sand_valley");
    public static final RegistryKey<Biome> CRIMSON_FOREST = makeKey("crimson_forest");
    public static final RegistryKey<Biome> WARPED_FOREST = makeKey("warped_forest");
    public static final RegistryKey<Biome> BASALT_DELTAS = makeKey("basalt_deltas");

    private static RegistryKey<Biome> makeKey(String name)
    {
        return RegistryKey.getOrCreateKey(Registry.BIOME_KEY, new ResourceLocation(name));
    }
}
