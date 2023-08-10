package net.minecraft.world.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.structure.StructureFeatures;

public class DefaultBiomeFeatures
{
    public static void withBadlandsStructures(BiomeGenerationSettings.Builder builder)
    {
        builder.withStructure(StructureFeatures.field_244137_c);
        builder.withStructure(StructureFeatures.field_244145_k);
    }

    public static void withStrongholdAndMineshaft(BiomeGenerationSettings.Builder builder)
    {
        builder.withStructure(StructureFeatures.field_244136_b);
        builder.withStructure(StructureFeatures.field_244145_k);
    }

    public static void withOceanStructures(BiomeGenerationSettings.Builder builder)
    {
        builder.withStructure(StructureFeatures.field_244136_b);
        builder.withStructure(StructureFeatures.field_244142_h);
    }

    public static void withCavesAndCanyons(BiomeGenerationSettings.Builder builder)
    {
        builder.withCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.field_243767_a);
        builder.withCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.field_243768_b);
    }

    public static void withOceanCavesAndCanyons(BiomeGenerationSettings.Builder builder)
    {
        builder.withCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.field_243769_c);
        builder.withCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.field_243768_b);
        builder.withCarver(GenerationStage.Carving.LIQUID, ConfiguredCarvers.field_243770_d);
        builder.withCarver(GenerationStage.Carving.LIQUID, ConfiguredCarvers.field_243771_e);
    }

    public static void withLavaAndWaterLakes(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.LAKES, Features.LAKE_WATER);
        builder.withFeature(GenerationStage.Decoration.LAKES, Features.LAKE_LAVA);
    }

    public static void withLavaLakes(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.LAKES, Features.LAKE_LAVA);
    }

    public static void withMonsterRoom(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, Features.MONSTER_ROOM);
    }

    public static void withCommonOverworldBlocks(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_DIRT);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GRAVEL);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GRANITE);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_DIORITE);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_ANDESITE);
    }

    public static void withOverworldOres(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_COAL);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_IRON);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GOLD);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_REDSTONE);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_DIAMOND);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_LAPIS);
    }

    public static void withExtraGoldOre(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_GOLD_EXTRA);
    }

    public static void withEmeraldOre(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.ORE_EMERALD);
    }

    public static void withInfestedStone(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_INFESTED);
    }

    public static void withDisks(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.DISK_SAND);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.DISK_CLAY);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.DISK_GRAVEL);
    }

    public static void withClayDisks(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Features.DISK_CLAY);
    }

    public static void withForestRocks(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Features.FOREST_ROCK);
    }

    public static void withLargeFern(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_LARGE_FERN);
    }

    public static void withChanceBerries(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_BERRY_DECORATED);
    }

    public static void withSparseBerries(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_BERRY_SPARSE);
    }

    public static void withLightBambooVegetation(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BAMBOO_LIGHT);
    }

    public static void withBambooVegetation(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BAMBOO);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BAMBOO_VEGETATION);
    }

    public static void withTaigaVegetation(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TAIGA_VEGETATION);
    }

    public static void withTreesInWater(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_WATER);
    }

    public static void withBirchTrees(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_BIRCH);
    }

    public static void withForestBirchTrees(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BIRCH_OTHER);
    }

    public static void withTallBirches(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BIRCH_TALL);
    }

    public static void withSavannaTrees(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_SAVANNA);
    }

    public static void withShatteredSavannaTrees(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_SHATTERED_SAVANNA);
    }

    public static void withMountainTrees(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_MOUNTAIN);
    }

    public static void withMountainEdgeTrees(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_MOUNTAIN_EDGE);
    }

    public static void withJungleTrees(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_JUNGLE);
    }

    public static void withJungleEdgeTrees(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TREES_JUNGLE_EDGE);
    }

    public static void withBadlandsOakTrees(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.OAK_BADLANDS);
    }

    public static void withSnowySpruces(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRUCE_SNOWY);
    }

    public static void withJungleGrass(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_JUNGLE);
    }

    public static void withTallGrass(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_TALL_GRASS);
    }

    public static void withNormalGrassPatch(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_NORMAL);
    }

    public static void withSavannaGrass(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_SAVANNA);
    }

    public static void withBadlandsGrassAndBush(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_BADLANDS);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH_BADLANDS);
    }

    public static void withAllForestFlowerGeneration(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.FOREST_FLOWER_VEGETATION);
    }

    public static void withForestGrass(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_FOREST);
    }

    public static void withSwampVegetation(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SWAMP_TREE);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.FLOWER_SWAMP);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_NORMAL);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_WATERLILLY);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_SWAMP);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_SWAMP);
    }

    public static void withMushroomBiomeVegetation(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.MUSHROOM_FIELD_VEGETATION);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_TAIGA);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_TAIGA);
    }

    public static void withPlainGrassVegetation(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PLAIN_VEGETATION);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.FLOWER_PLAIN_DECORATED);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_PLAIN);
    }

    public static void withDesertDeadBushes(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH_2);
    }

    public static void withGiantTaigaGrassVegetation(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_TAIGA);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_DEAD_BUSH);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_GIANT);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_GIANT);
    }

    public static void withDefaultFlowers(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.FLOWER_DEFAULT);
    }

    public static void withWarmFlowers(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.FLOWER_WARM);
    }

    public static void withBadlandsGrass(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_BADLANDS);
    }

    public static void withTaigaGrassVegetation(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_GRASS_TAIGA_2);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_TAIGA);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_TAIGA);
    }

    public static void withNoiseTallGrass(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_TALL_GRASS_2);
    }

    public static void withNormalMushroomGeneration(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.BROWN_MUSHROOM_NORMAL);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.RED_MUSHROOM_NORMAL);
    }

    public static void withSugarCaneAndPumpkins(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
    }

    public static void withBadlandsVegetation(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE_BADLANDS);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_CACTUS_DECORATED);
    }

    public static void withMelonPatchesAndVines(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_MELON);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.VINES);
    }

    public static void withDesertVegetation(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE_DESERT);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_CACTUS_DESERT);
    }

    public static void withSwampSugarcaneAndPumpkin(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE_SWAMP);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
    }

    public static void withDesertWells(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Features.DESERT_WELL);
    }

    public static void withFossils(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, Features.FOSSIL);
    }

    public static void withColdKelp(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.KELP_COLD);
    }

    public static void withSimpleSeagrass(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_SIMPLE);
    }

    public static void withWarmKelp(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.KELP_WARM);
    }

    public static void withLavaAndWaterSprings(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_WATER);
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA);
    }

    public static void withIcebergs(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Features.ICEBERG_PACKED);
        builder.withFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Features.ICEBERG_BLUE);
    }

    public static void withBlueIce(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Features.BLUE_ICE);
    }

    public static void withFrozenTopLayer(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Features.FREEZE_TOP_LAYER);
    }

    public static void withCommonNetherBlocks(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_GRAVEL_NETHER);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_BLACKSTONE);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_GOLD_NETHER);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_QUARTZ_NETHER);
        withDebrisOre(builder);
    }

    public static void withDebrisOre(BiomeGenerationSettings.Builder builder)
    {
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_DEBRIS_LARGE);
        builder.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_DEBRIS_SMALL);
    }

    public static void withPassiveMobs(MobSpawnInfo.Builder builder)
    {
        builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.SHEEP, 12, 4, 4));
        builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.PIG, 10, 4, 4));
        builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.CHICKEN, 10, 4, 4));
        builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.COW, 8, 4, 4));
    }

    public static void withBats(MobSpawnInfo.Builder builder)
    {
        builder.withSpawner(EntityClassification.AMBIENT, new MobSpawnInfo.Spawners(EntityType.BAT, 10, 8, 8));
    }

    public static void withBatsAndHostiles(MobSpawnInfo.Builder builder)
    {
        withBats(builder);
        withHostileMobs(builder, 95, 5, 100);
    }

    public static void withOceanMobs(MobSpawnInfo.Builder builder, int squidWeight, int squidMaxCount, int codWeight)
    {
        builder.withSpawner(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(EntityType.SQUID, squidWeight, 1, squidMaxCount));
        builder.withSpawner(EntityClassification.WATER_AMBIENT, new MobSpawnInfo.Spawners(EntityType.COD, codWeight, 3, 6));
        withBatsAndHostiles(builder);
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.DROWNED, 5, 1, 1));
    }

    public static void withWarmOceanMobs(MobSpawnInfo.Builder builder, int squidWeight, int squidMinCount)
    {
        builder.withSpawner(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(EntityType.SQUID, squidWeight, squidMinCount, 4));
        builder.withSpawner(EntityClassification.WATER_AMBIENT, new MobSpawnInfo.Spawners(EntityType.TROPICAL_FISH, 25, 8, 8));
        builder.withSpawner(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(EntityType.DOLPHIN, 2, 1, 2));
        withBatsAndHostiles(builder);
    }

    public static void withSpawnsWithHorseAndDonkey(MobSpawnInfo.Builder builder)
    {
        withPassiveMobs(builder);
        builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.HORSE, 5, 2, 6));
        builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.DONKEY, 1, 1, 3));
        withBatsAndHostiles(builder);
    }

    public static void withSnowyBiomeMobs(MobSpawnInfo.Builder builder)
    {
        builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 10, 2, 3));
        builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.POLAR_BEAR, 1, 1, 2));
        withBats(builder);
        withHostileMobs(builder, 95, 5, 20);
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.STRAY, 80, 4, 4));
    }

    public static void withDesertMobs(MobSpawnInfo.Builder builder)
    {
        builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 4, 2, 3));
        withBats(builder);
        withHostileMobs(builder, 19, 1, 100);
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.HUSK, 80, 4, 4));
    }

    public static void withHostileMobs(MobSpawnInfo.Builder builder, int zombieWeight, int zombieVillagerWeight, int skeletonWeight)
    {
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.SPIDER, 100, 4, 4));
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ZOMBIE, zombieWeight, 4, 4));
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ZOMBIE_VILLAGER, zombieVillagerWeight, 1, 1));
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.SKELETON, skeletonWeight, 4, 4));
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.CREEPER, 100, 4, 4));
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.SLIME, 100, 4, 4));
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 10, 1, 4));
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.WITCH, 5, 1, 1));
    }

    public static void withMooshroomsAndBats(MobSpawnInfo.Builder builder)
    {
        builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.MOOSHROOM, 8, 4, 8));
        withBats(builder);
    }

    public static void withSpawnsWithExtraChickens(MobSpawnInfo.Builder builder)
    {
        withPassiveMobs(builder);
        builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.CHICKEN, 10, 4, 4));
        withBatsAndHostiles(builder);
    }

    public static void withEndermen(MobSpawnInfo.Builder builder)
    {
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 10, 4, 4));
    }
}
