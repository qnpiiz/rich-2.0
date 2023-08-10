package net.minecraft.world.biome;

import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class BiomeMaker
{
    private static int getSkyColorWithTemperatureModifier(float temperature)
    {
        float lvt_1_1_ = temperature / 3.0F;
        lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
        return MathHelper.hsvToRGB(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
    }

    public static Biome makeGiantTaigaBiome(float depth, float scale, float temperature, boolean isSpruceVariant)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withPassiveMobs(mobspawninfo$builder);
        mobspawninfo$builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.WOLF, 8, 4, 4));
        mobspawninfo$builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 4, 2, 3));
        mobspawninfo$builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.FOX, 8, 2, 4));

        if (isSpruceVariant)
        {
            DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
        }
        else
        {
            DefaultBiomeFeatures.withBats(mobspawninfo$builder);
            DefaultBiomeFeatures.withHostileMobs(mobspawninfo$builder, 100, 25, 100);
        }

        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244177_i);
        DefaultBiomeFeatures.withStrongholdAndMineshaft(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244159_y);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withForestRocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLargeFern(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, isSpruceVariant ? Features.TREES_GIANT_SPRUCE : Features.TREES_GIANT);
        DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withGiantTaigaGrassVegetation(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSparseBerries(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.TAIGA).depth(depth).scale(scale).temperature(temperature).downfall(0.8F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(temperature)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeBirchForestBiome(float depth, float scale, boolean isTallVariant)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withPassiveMobs(mobspawninfo$builder);
        DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244178_j);
        DefaultBiomeFeatures.withStrongholdAndMineshaft(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244159_y);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withAllForestFlowerGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);

        if (isTallVariant)
        {
            DefaultBiomeFeatures.withTallBirches(biomegenerationsettings$builder);
        }
        else
        {
            DefaultBiomeFeatures.withBirchTrees(biomegenerationsettings$builder);
        }

        DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withForestGrass(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.FOREST).depth(depth).scale(scale).temperature(0.6F).downfall(0.6F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(0.6F)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeJungleBiome()
    {
        return makeGenericJungleBiome(0.1F, 0.2F, 40, 2, 3);
    }

    public static Biome makeJungleEdgeBiome()
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withSpawnsWithExtraChickens(mobspawninfo$builder);
        return makeTropicalBiome(0.1F, 0.2F, 0.8F, false, true, false, mobspawninfo$builder);
    }

    public static Biome makeModifiedJungleEdgeBiome()
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withSpawnsWithExtraChickens(mobspawninfo$builder);
        return makeTropicalBiome(0.2F, 0.4F, 0.8F, false, true, true, mobspawninfo$builder);
    }

    public static Biome makeModifiedJungleBiome()
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withSpawnsWithExtraChickens(mobspawninfo$builder);
        mobspawninfo$builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.PARROT, 10, 1, 1)).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.OCELOT, 2, 1, 1));
        return makeTropicalBiome(0.2F, 0.4F, 0.9F, false, false, true, mobspawninfo$builder);
    }

    public static Biome makeJungleHillsBiome()
    {
        return makeGenericJungleBiome(0.45F, 0.3F, 10, 1, 1);
    }

    public static Biome makeBambooJungleBiome()
    {
        return makeGenericBambooBiome(0.1F, 0.2F, 40, 2);
    }

    public static Biome makeBambooJungleHillsBiome()
    {
        return makeGenericBambooBiome(0.45F, 0.3F, 10, 1);
    }

    private static Biome makeGenericJungleBiome(float depth, float scale, int parrotWeight, int parrotMaxCount, int ocelotMaxCount)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withSpawnsWithExtraChickens(mobspawninfo$builder);
        mobspawninfo$builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.PARROT, parrotWeight, 1, parrotMaxCount)).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.OCELOT, 2, 1, ocelotMaxCount)).withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.PANDA, 1, 1, 2));
        mobspawninfo$builder.isValidSpawnBiomeForPlayer();
        return makeTropicalBiome(depth, scale, 0.9F, false, false, false, mobspawninfo$builder);
    }

    private static Biome makeGenericBambooBiome(float depth, float scale, int parrotWeight, int parrotMaxCount)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withSpawnsWithExtraChickens(mobspawninfo$builder);
        mobspawninfo$builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.PARROT, parrotWeight, 1, parrotMaxCount)).withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.PANDA, 80, 1, 2)).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.OCELOT, 2, 1, 1));
        return makeTropicalBiome(depth, scale, 0.9F, true, false, false, mobspawninfo$builder);
    }

    private static Biome makeTropicalBiome(float depth, float scale, float downfall, boolean hasOnlyBambooVegetation, boolean isEdgeBiome, boolean isModified, MobSpawnInfo.Builder mobSpawnBuilder)
    {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244178_j);

        if (!isEdgeBiome && !isModified)
        {
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244139_e);
        }

        DefaultBiomeFeatures.withStrongholdAndMineshaft(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244130_A);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);

        if (hasOnlyBambooVegetation)
        {
            DefaultBiomeFeatures.withBambooVegetation(biomegenerationsettings$builder);
        }
        else
        {
            if (!isEdgeBiome && !isModified)
            {
                DefaultBiomeFeatures.withLightBambooVegetation(biomegenerationsettings$builder);
            }

            if (isEdgeBiome)
            {
                DefaultBiomeFeatures.withJungleEdgeTrees(biomegenerationsettings$builder);
            }
            else
            {
                DefaultBiomeFeatures.withJungleTrees(biomegenerationsettings$builder);
            }
        }

        DefaultBiomeFeatures.withWarmFlowers(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withJungleGrass(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMelonPatchesAndVines(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.JUNGLE).depth(depth).scale(scale).temperature(0.95F).downfall(downfall).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(0.95F)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobSpawnBuilder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeMountainBiome(float depth, float scale, ConfiguredSurfaceBuilder<SurfaceBuilderConfig> surfaceBuilder, boolean isEdgeBiome)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withPassiveMobs(mobspawninfo$builder);
        mobspawninfo$builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.LLAMA, 5, 4, 6));
        DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(surfaceBuilder);
        DefaultBiomeFeatures.withStrongholdAndMineshaft(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244132_C);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);

        if (isEdgeBiome)
        {
            DefaultBiomeFeatures.withMountainEdgeTrees(biomegenerationsettings$builder);
        }
        else
        {
            DefaultBiomeFeatures.withMountainTrees(biomegenerationsettings$builder);
        }

        DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withBadlandsGrass(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withEmeraldOre(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withInfestedStone(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.EXTREME_HILLS).depth(depth).scale(scale).temperature(0.2F).downfall(0.3F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(0.2F)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeDesertBiome(float depth, float scale, boolean hasVillageAndOutpost, boolean hasDesertPyramid, boolean hasFossils)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withDesertMobs(mobspawninfo$builder);
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244172_d);

        if (hasVillageAndOutpost)
        {
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244155_u);
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244135_a);
        }

        if (hasDesertPyramid)
        {
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244140_f);
        }

        if (hasFossils)
        {
            DefaultBiomeFeatures.withFossils(biomegenerationsettings$builder);
        }

        DefaultBiomeFeatures.withStrongholdAndMineshaft(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244160_z);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withBadlandsGrass(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDesertDeadBushes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDesertVegetation(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDesertWells(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.NONE).category(Biome.Category.DESERT).depth(depth).scale(scale).temperature(2.0F).downfall(0.0F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(2.0F)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makePlainsBiome(boolean isSunflowerVariant)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withSpawnsWithHorseAndDonkey(mobspawninfo$builder);

        if (!isSunflowerVariant)
        {
            mobspawninfo$builder.isValidSpawnBiomeForPlayer();
        }

        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244178_j);

        if (!isSunflowerVariant)
        {
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244154_t).withStructure(StructureFeatures.field_244135_a);
        }

        DefaultBiomeFeatures.withStrongholdAndMineshaft(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244159_y);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNoiseTallGrass(biomegenerationsettings$builder);

        if (isSunflowerVariant)
        {
            biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_SUNFLOWER);
        }

        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withPlainGrassVegetation(biomegenerationsettings$builder);

        if (isSunflowerVariant)
        {
            biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_SUGAR_CANE);
        }

        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);

        if (isSunflowerVariant)
        {
            biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.PATCH_PUMPKIN);
        }
        else
        {
            DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        }

        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.PLAINS).depth(0.125F).scale(0.05F).temperature(0.8F).downfall(0.4F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(0.8F)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    private static Biome makeEndBiome(BiomeGenerationSettings.Builder generationSettingsBuilder)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withEndermen(mobspawninfo$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.NONE).category(Biome.Category.THEEND).depth(0.1F).scale(0.2F).temperature(0.5F).downfall(0.5F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(10518688).withSkyColor(0).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(generationSettingsBuilder.build()).build();
    }

    public static Biome makeEndBarrensBiome()
    {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244173_e);
        return makeEndBiome(biomegenerationsettings$builder);
    }

    public static Biome makeTheEndBiome()
    {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244173_e).withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Features.END_SPIKE);
        return makeEndBiome(biomegenerationsettings$builder);
    }

    public static Biome makeEndMidlandsBiome()
    {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244173_e).withStructure(StructureFeatures.field_244151_q);
        return makeEndBiome(biomegenerationsettings$builder);
    }

    public static Biome makeEndHighlandsBiome()
    {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244173_e).withStructure(StructureFeatures.field_244151_q).withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Features.END_GATEWAY).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.CHORUS_PLANT);
        return makeEndBiome(biomegenerationsettings$builder);
    }

    public static Biome makeSmallEndIslandsBiome()
    {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244173_e).withFeature(GenerationStage.Decoration.RAW_GENERATION, Features.END_ISLAND_DECORATED);
        return makeEndBiome(biomegenerationsettings$builder);
    }

    public static Biome makeMushroomBiome(float depth, float scale)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withMooshroomsAndBats(mobspawninfo$builder);
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244182_n);
        DefaultBiomeFeatures.withStrongholdAndMineshaft(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244159_y);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMushroomBiomeVegetation(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.MUSHROOM).depth(depth).scale(scale).temperature(0.9F).downfall(1.0F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(0.9F)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    private static Biome makeGenericSavannaBiome(float depth, float scale, float temperature, boolean isHighland, boolean isShatteredSavanna, MobSpawnInfo.Builder mobSpawnBuilder)
    {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(isShatteredSavanna ? ConfiguredSurfaceBuilders.field_244186_r : ConfiguredSurfaceBuilders.field_244178_j);

        if (!isHighland && !isShatteredSavanna)
        {
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244156_v).withStructure(StructureFeatures.field_244135_a);
        }

        DefaultBiomeFeatures.withStrongholdAndMineshaft(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withStructure(isHighland ? StructureFeatures.field_244132_C : StructureFeatures.field_244159_y);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);

        if (!isShatteredSavanna)
        {
            DefaultBiomeFeatures.withTallGrass(biomegenerationsettings$builder);
        }

        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);

        if (isShatteredSavanna)
        {
            DefaultBiomeFeatures.withShatteredSavannaTrees(biomegenerationsettings$builder);
            DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
            DefaultBiomeFeatures.withNormalGrassPatch(biomegenerationsettings$builder);
        }
        else
        {
            DefaultBiomeFeatures.withSavannaTrees(biomegenerationsettings$builder);
            DefaultBiomeFeatures.withWarmFlowers(biomegenerationsettings$builder);
            DefaultBiomeFeatures.withSavannaGrass(biomegenerationsettings$builder);
        }

        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.NONE).category(Biome.Category.SAVANNA).depth(depth).scale(scale).temperature(temperature).downfall(0.0F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(temperature)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobSpawnBuilder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeGenericSavannaBiome(float depth, float scale, float temperature, boolean isHighland, boolean isShatteredSavanna)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = getSpawnsWithHorseAndDonkey();
        return makeGenericSavannaBiome(depth, scale, temperature, isHighland, isShatteredSavanna, mobspawninfo$builder);
    }

    private static MobSpawnInfo.Builder getSpawnsWithHorseAndDonkey()
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withPassiveMobs(mobspawninfo$builder);
        mobspawninfo$builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.HORSE, 1, 2, 6)).withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.DONKEY, 1, 1, 1));
        DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
        return mobspawninfo$builder;
    }

    public static Biome makeSavannaPlateauBiome()
    {
        MobSpawnInfo.Builder mobspawninfo$builder = getSpawnsWithHorseAndDonkey();
        mobspawninfo$builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.LLAMA, 8, 4, 4));
        return makeGenericSavannaBiome(1.5F, 0.025F, 1.0F, true, false, mobspawninfo$builder);
    }

    private static Biome makeGenericBadlandsBiome(ConfiguredSurfaceBuilder<SurfaceBuilderConfig> surfaceBuilder, float depth, float scale, boolean isHighland, boolean hasOakTrees)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(surfaceBuilder);
        DefaultBiomeFeatures.withBadlandsStructures(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withStructure(isHighland ? StructureFeatures.field_244132_C : StructureFeatures.field_244159_y);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withExtraGoldOre(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);

        if (hasOakTrees)
        {
            DefaultBiomeFeatures.withBadlandsOakTrees(biomegenerationsettings$builder);
        }

        DefaultBiomeFeatures.withBadlandsGrassAndBush(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withBadlandsVegetation(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.NONE).category(Biome.Category.MESA).depth(depth).scale(scale).temperature(2.0F).downfall(0.0F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(2.0F)).withFoliageColor(10387789).withGrassColor(9470285).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeBadlandsBiome(float depth, float scale, boolean isHighland)
    {
        return makeGenericBadlandsBiome(ConfiguredSurfaceBuilders.field_244169_a, depth, scale, isHighland, false);
    }

    public static Biome makeWoodedBadlandsPlateauBiome(float depth, float scale)
    {
        return makeGenericBadlandsBiome(ConfiguredSurfaceBuilders.field_244191_w, depth, scale, true, true);
    }

    public static Biome makeErodedBadlandsBiome()
    {
        return makeGenericBadlandsBiome(ConfiguredSurfaceBuilders.field_244174_f, 0.1F, 0.2F, true, false);
    }

    private static Biome makeGenericOceanBiome(MobSpawnInfo.Builder mobSpawnBuilder, int waterColor, int waterFogColor, boolean isDeepVariant, BiomeGenerationSettings.Builder generationSettingsBuilder)
    {
        return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.OCEAN).depth(isDeepVariant ? -1.8F : -1.0F).scale(0.1F).temperature(0.5F).downfall(0.5F).setEffects((new BiomeAmbience.Builder()).setWaterColor(waterColor).setWaterFogColor(waterFogColor).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(0.5F)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobSpawnBuilder.copy()).withGenerationSettings(generationSettingsBuilder.build()).build();
    }

    private static BiomeGenerationSettings.Builder getOceanGenerationSettingsBuilder(ConfiguredSurfaceBuilder<SurfaceBuilderConfig> surfaceBuilder, boolean hasOceanMonument, boolean isWarmOcean, boolean isDeepVariant)
    {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(surfaceBuilder);
        StructureFeature <? , ? > structurefeature = isWarmOcean ? StructureFeatures.field_244148_n : StructureFeatures.field_244147_m;

        if (isDeepVariant)
        {
            if (hasOceanMonument)
            {
                biomegenerationsettings$builder.withStructure(StructureFeatures.field_244146_l);
            }

            DefaultBiomeFeatures.withOceanStructures(biomegenerationsettings$builder);
            biomegenerationsettings$builder.withStructure(structurefeature);
        }
        else
        {
            biomegenerationsettings$builder.withStructure(structurefeature);

            if (hasOceanMonument)
            {
                biomegenerationsettings$builder.withStructure(StructureFeatures.field_244146_l);
            }

            DefaultBiomeFeatures.withOceanStructures(biomegenerationsettings$builder);
        }

        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244133_D);
        DefaultBiomeFeatures.withOceanCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withTreesInWater(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withBadlandsGrass(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        return biomegenerationsettings$builder;
    }

    public static Biome makeColdOceanBiome(boolean isDeepVariant)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withOceanMobs(mobspawninfo$builder, 3, 4, 15);
        mobspawninfo$builder.withSpawner(EntityClassification.WATER_AMBIENT, new MobSpawnInfo.Spawners(EntityType.SALMON, 15, 1, 5));
        boolean flag = !isDeepVariant;
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = getOceanGenerationSettingsBuilder(ConfiguredSurfaceBuilders.field_244178_j, isDeepVariant, false, flag);
        biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, isDeepVariant ? Features.SEAGRASS_DEEP_COLD : Features.SEAGRASS_COLD);
        DefaultBiomeFeatures.withSimpleSeagrass(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withColdKelp(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return makeGenericOceanBiome(mobspawninfo$builder, 4020182, 329011, isDeepVariant, biomegenerationsettings$builder);
    }

    public static Biome makeOceanBiome(boolean isDeepVariant)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withOceanMobs(mobspawninfo$builder, 1, 4, 10);
        mobspawninfo$builder.withSpawner(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(EntityType.DOLPHIN, 1, 1, 2));
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = getOceanGenerationSettingsBuilder(ConfiguredSurfaceBuilders.field_244178_j, isDeepVariant, false, true);
        biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, isDeepVariant ? Features.SEAGRASS_DEEP : Features.SEAGRASS_NORMAL);
        DefaultBiomeFeatures.withSimpleSeagrass(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withColdKelp(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return makeGenericOceanBiome(mobspawninfo$builder, 4159204, 329011, isDeepVariant, biomegenerationsettings$builder);
    }

    public static Biome makeLukewarmOceanBiome(boolean isDeepVariant)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();

        if (isDeepVariant)
        {
            DefaultBiomeFeatures.withOceanMobs(mobspawninfo$builder, 8, 4, 8);
        }
        else
        {
            DefaultBiomeFeatures.withOceanMobs(mobspawninfo$builder, 10, 2, 15);
        }

        mobspawninfo$builder.withSpawner(EntityClassification.WATER_AMBIENT, new MobSpawnInfo.Spawners(EntityType.PUFFERFISH, 5, 1, 3)).withSpawner(EntityClassification.WATER_AMBIENT, new MobSpawnInfo.Spawners(EntityType.TROPICAL_FISH, 25, 8, 8)).withSpawner(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(EntityType.DOLPHIN, 2, 1, 2));
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = getOceanGenerationSettingsBuilder(ConfiguredSurfaceBuilders.field_244185_q, isDeepVariant, true, false);
        biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, isDeepVariant ? Features.SEAGRASS_DEEP_WARM : Features.SEAGRASS_WARM);

        if (isDeepVariant)
        {
            DefaultBiomeFeatures.withSimpleSeagrass(biomegenerationsettings$builder);
        }

        DefaultBiomeFeatures.withWarmKelp(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return makeGenericOceanBiome(mobspawninfo$builder, 4566514, 267827, isDeepVariant, biomegenerationsettings$builder);
    }

    public static Biome makeWarmOceanBiome()
    {
        MobSpawnInfo.Builder mobspawninfo$builder = (new MobSpawnInfo.Builder()).withSpawner(EntityClassification.WATER_AMBIENT, new MobSpawnInfo.Spawners(EntityType.PUFFERFISH, 15, 1, 3));
        DefaultBiomeFeatures.withWarmOceanMobs(mobspawninfo$builder, 10, 4);
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = getOceanGenerationSettingsBuilder(ConfiguredSurfaceBuilders.field_244176_h, false, true, false).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.WARM_OCEAN_VEGETATION).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_WARM).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SEA_PICKLE);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return makeGenericOceanBiome(mobspawninfo$builder, 4445678, 270131, false, biomegenerationsettings$builder);
    }

    public static Biome makeDeepWarmOceanBiome()
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withWarmOceanMobs(mobspawninfo$builder, 5, 1);
        mobspawninfo$builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.DROWNED, 5, 1, 1));
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = getOceanGenerationSettingsBuilder(ConfiguredSurfaceBuilders.field_244176_h, true, true, false).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_DEEP_WARM);
        DefaultBiomeFeatures.withSimpleSeagrass(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return makeGenericOceanBiome(mobspawninfo$builder, 4445678, 270131, true, biomegenerationsettings$builder);
    }

    public static Biome makeFrozenOceanBiome(boolean isDeepVariant)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = (new MobSpawnInfo.Builder()).withSpawner(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(EntityType.SQUID, 1, 1, 4)).withSpawner(EntityClassification.WATER_AMBIENT, new MobSpawnInfo.Spawners(EntityType.SALMON, 15, 1, 5)).withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.POLAR_BEAR, 1, 1, 2));
        DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
        mobspawninfo$builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.DROWNED, 5, 1, 1));
        float f = isDeepVariant ? 0.5F : 0.0F;
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244175_g);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244147_m);

        if (isDeepVariant)
        {
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244146_l);
        }

        DefaultBiomeFeatures.withOceanStructures(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244133_D);
        DefaultBiomeFeatures.withOceanCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withIcebergs(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withBlueIce(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withTreesInWater(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withBadlandsGrass(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(isDeepVariant ? Biome.RainType.RAIN : Biome.RainType.SNOW).category(Biome.Category.OCEAN).depth(isDeepVariant ? -1.8F : -1.0F).scale(0.1F).temperature(f).withTemperatureModifier(Biome.TemperatureModifier.FROZEN).downfall(0.5F).setEffects((new BiomeAmbience.Builder()).setWaterColor(3750089).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(f)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    private static Biome makeGenericForestBiome(float depth, float scale, boolean isFlowerForestVariant, MobSpawnInfo.Builder mobSpawnBuilder)
    {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244178_j);
        DefaultBiomeFeatures.withStrongholdAndMineshaft(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244159_y);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);

        if (isFlowerForestVariant)
        {
            biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.FOREST_FLOWER_VEGETATION_COMMON);
        }
        else
        {
            DefaultBiomeFeatures.withAllForestFlowerGeneration(biomegenerationsettings$builder);
        }

        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);

        if (isFlowerForestVariant)
        {
            biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.FOREST_FLOWER_TREES);
            biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.FLOWER_FOREST);
            DefaultBiomeFeatures.withBadlandsGrass(biomegenerationsettings$builder);
        }
        else
        {
            DefaultBiomeFeatures.withForestBirchTrees(biomegenerationsettings$builder);
            DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
            DefaultBiomeFeatures.withForestGrass(biomegenerationsettings$builder);
        }

        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.FOREST).depth(depth).scale(scale).temperature(0.7F).downfall(0.8F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(0.7F)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobSpawnBuilder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    private static MobSpawnInfo.Builder getStandardMobSpawnBuilder()
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withPassiveMobs(mobspawninfo$builder);
        DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
        return mobspawninfo$builder;
    }

    public static Biome makeForestBiome(float depth, float scale)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = getStandardMobSpawnBuilder().withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.WOLF, 5, 4, 4)).isValidSpawnBiomeForPlayer();
        return makeGenericForestBiome(depth, scale, false, mobspawninfo$builder);
    }

    public static Biome makeFlowerForestBiome()
    {
        MobSpawnInfo.Builder mobspawninfo$builder = getStandardMobSpawnBuilder().withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 4, 2, 3));
        return makeGenericForestBiome(0.1F, 0.4F, true, mobspawninfo$builder);
    }

    public static Biome makeTaigaBiome(float depth, float scale, boolean isSnowyVariant, boolean isMountainVariant, boolean hasVillageAndOutpost, boolean hasIgloos)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withPassiveMobs(mobspawninfo$builder);
        mobspawninfo$builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.WOLF, 8, 4, 4)).withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 4, 2, 3)).withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.FOX, 8, 2, 4));

        if (!isSnowyVariant && !isMountainVariant)
        {
            mobspawninfo$builder.isValidSpawnBiomeForPlayer();
        }

        DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
        float f = isSnowyVariant ? -0.5F : 0.25F;
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244178_j);

        if (hasVillageAndOutpost)
        {
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244158_x);
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244135_a);
        }

        if (hasIgloos)
        {
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244141_g);
        }

        DefaultBiomeFeatures.withStrongholdAndMineshaft(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withStructure(isMountainVariant ? StructureFeatures.field_244132_C : StructureFeatures.field_244159_y);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLargeFern(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withTaigaVegetation(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withTaigaGrassVegetation(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);

        if (isSnowyVariant)
        {
            DefaultBiomeFeatures.withChanceBerries(biomegenerationsettings$builder);
        }
        else
        {
            DefaultBiomeFeatures.withSparseBerries(biomegenerationsettings$builder);
        }

        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(isSnowyVariant ? Biome.RainType.SNOW : Biome.RainType.RAIN).category(Biome.Category.TAIGA).depth(depth).scale(scale).temperature(f).downfall(isSnowyVariant ? 0.4F : 0.8F).setEffects((new BiomeAmbience.Builder()).setWaterColor(isSnowyVariant ? 4020182 : 4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(f)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeDarkForestBiome(float depth, float scale, boolean isHillsVariant)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withPassiveMobs(mobspawninfo$builder);
        DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244178_j);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244138_d);
        DefaultBiomeFeatures.withStrongholdAndMineshaft(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244159_y);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, isHillsVariant ? Features.DARK_FOREST_VEGETATION_RED : Features.DARK_FOREST_VEGETATION_BROWN);
        DefaultBiomeFeatures.withAllForestFlowerGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withForestGrass(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.FOREST).depth(depth).scale(scale).temperature(0.7F).downfall(0.8F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(0.7F)).withGrassColorModifier(BiomeAmbience.GrassColorModifier.DARK_FOREST).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeGenericSwampBiome(float depth, float scale, boolean isHillsVariant)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withPassiveMobs(mobspawninfo$builder);
        DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
        mobspawninfo$builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.SLIME, 1, 1, 1));
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244189_u);

        if (!isHillsVariant)
        {
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244144_j);
        }

        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244136_b);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244131_B);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);

        if (!isHillsVariant)
        {
            DefaultBiomeFeatures.withFossils(biomegenerationsettings$builder);
        }

        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withClayDisks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSwampVegetation(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSwampSugarcaneAndPumpkin(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);

        if (isHillsVariant)
        {
            DefaultBiomeFeatures.withFossils(biomegenerationsettings$builder);
        }
        else
        {
            biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_SWAMP);
        }

        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.SWAMP).depth(depth).scale(scale).temperature(0.8F).downfall(0.9F).setEffects((new BiomeAmbience.Builder()).setWaterColor(6388580).setWaterFogColor(2302743).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(0.8F)).withFoliageColor(6975545).withGrassColorModifier(BiomeAmbience.GrassColorModifier.SWAMP).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeSnowyBiome(float depth, float scale, boolean isIceSpikesBiome, boolean isMountainVariant)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = (new MobSpawnInfo.Builder()).withCreatureSpawnProbability(0.07F);
        DefaultBiomeFeatures.withSnowyBiomeMobs(mobspawninfo$builder);
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(isIceSpikesBiome ? ConfiguredSurfaceBuilders.field_244180_l : ConfiguredSurfaceBuilders.field_244178_j);

        if (!isIceSpikesBiome && !isMountainVariant)
        {
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244157_w).withStructure(StructureFeatures.field_244141_g);
        }

        DefaultBiomeFeatures.withStrongholdAndMineshaft(biomegenerationsettings$builder);

        if (!isIceSpikesBiome && !isMountainVariant)
        {
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244135_a);
        }

        biomegenerationsettings$builder.withStructure(isMountainVariant ? StructureFeatures.field_244132_C : StructureFeatures.field_244159_y);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);

        if (isIceSpikesBiome)
        {
            biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Features.ICE_SPIKE);
            biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Features.ICE_PATCH);
        }

        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSnowySpruces(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withBadlandsGrass(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.SNOW).category(Biome.Category.ICY).depth(depth).scale(scale).temperature(0.0F).downfall(0.5F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(0.0F)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeRiverBiome(float depth, float scale, float temperature, int waterColor, boolean isSnowy)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = (new MobSpawnInfo.Builder()).withSpawner(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(EntityType.SQUID, 2, 1, 4)).withSpawner(EntityClassification.WATER_AMBIENT, new MobSpawnInfo.Spawners(EntityType.SALMON, 5, 1, 5));
        DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
        mobspawninfo$builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.DROWNED, isSnowy ? 1 : 100, 1, 1));
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244178_j);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244136_b);
        biomegenerationsettings$builder.withStructure(StructureFeatures.field_244159_y);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withTreesInWater(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withBadlandsGrass(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);

        if (!isSnowy)
        {
            biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_RIVER);
        }

        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(isSnowy ? Biome.RainType.SNOW : Biome.RainType.RAIN).category(Biome.Category.RIVER).depth(depth).scale(scale).temperature(temperature).downfall(0.5F).setEffects((new BiomeAmbience.Builder()).setWaterColor(waterColor).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(temperature)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeGenericBeachBiome(float depth, float scale, float temperature, float downfall, int waterColor, boolean isColdBiome, boolean isStoneVariant)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();

        if (!isStoneVariant && !isColdBiome)
        {
            mobspawninfo$builder.withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.TURTLE, 5, 2, 5));
        }

        DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(isStoneVariant ? ConfiguredSurfaceBuilders.field_244188_t : ConfiguredSurfaceBuilders.field_244172_d);

        if (isStoneVariant)
        {
            DefaultBiomeFeatures.withStrongholdAndMineshaft(biomegenerationsettings$builder);
        }
        else
        {
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244136_b);
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244152_r);
            biomegenerationsettings$builder.withStructure(StructureFeatures.field_244143_i);
        }

        biomegenerationsettings$builder.withStructure(isStoneVariant ? StructureFeatures.field_244132_C : StructureFeatures.field_244159_y);
        DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDisks(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withDefaultFlowers(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withBadlandsGrass(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
        DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(isColdBiome ? Biome.RainType.SNOW : Biome.RainType.RAIN).category(isStoneVariant ? Biome.Category.NONE : Biome.Category.BEACH).depth(depth).scale(scale).temperature(temperature).downfall(downfall).setEffects((new BiomeAmbience.Builder()).setWaterColor(waterColor).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(temperature)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.copy()).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeVoidBiome()
    {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244184_p);
        biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, Features.VOID_START_PLATFORM);
        return (new Biome.Builder()).precipitation(Biome.RainType.NONE).category(Biome.Category.NONE).depth(0.1F).scale(0.2F).temperature(0.5F).downfall(0.5F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(12638463).withSkyColor(getSkyColorWithTemperatureModifier(0.5F)).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(MobSpawnInfo.EMPTY).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeNetherWastesBiome()
    {
        MobSpawnInfo mobspawninfo = (new MobSpawnInfo.Builder()).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.GHAST, 50, 4, 4)).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ZOMBIFIED_PIGLIN, 100, 4, 4)).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.MAGMA_CUBE, 2, 4, 4)).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 1, 4, 4)).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.PIGLIN, 15, 4, 4)).withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.STRIDER, 60, 1, 2)).copy();
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244183_o).withStructure(StructureFeatures.field_244134_E).withStructure(StructureFeatures.field_244149_o).withStructure(StructureFeatures.field_244153_s).withCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.field_243772_f).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_OPEN).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_FIRE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_SOUL_FIRE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE_EXTRA).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.BROWN_MUSHROOM_NETHER).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.RED_MUSHROOM_NETHER).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_MAGMA).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_CLOSED);
        DefaultBiomeFeatures.withCommonNetherBlocks(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.NONE).category(Biome.Category.NETHER).depth(0.1F).scale(0.2F).temperature(2.0F).downfall(0.0F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(3344392).withSkyColor(getSkyColorWithTemperatureModifier(2.0F)).setAmbientSound(SoundEvents.AMBIENT_NETHER_WASTES_LOOP).setMoodSound(new MoodSoundAmbience(SoundEvents.AMBIENT_NETHER_WASTES_MOOD, 6000, 8, 2.0D)).setAdditionsSound(new SoundAdditionsAmbience(SoundEvents.AMBIENT_NETHER_WASTES_ADDITIONS, 0.0111D)).setMusic(BackgroundMusicTracks.getDefaultBackgroundMusicSelector(SoundEvents.MUSIC_NETHER_NETHER_WASTES)).build()).withMobSpawnSettings(mobspawninfo).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeSoulSandValleyBiome()
    {
        double d0 = 0.7D;
        double d1 = 0.15D;
        MobSpawnInfo mobspawninfo = (new MobSpawnInfo.Builder()).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.SKELETON, 20, 5, 5)).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.GHAST, 50, 4, 4)).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 1, 4, 4)).withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.STRIDER, 60, 1, 2)).withSpawnCost(EntityType.SKELETON, 0.7D, 0.15D).withSpawnCost(EntityType.GHAST, 0.7D, 0.15D).withSpawnCost(EntityType.ENDERMAN, 0.7D, 0.15D).withSpawnCost(EntityType.STRIDER, 0.7D, 0.15D).copy();
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244187_s).withStructure(StructureFeatures.field_244149_o).withStructure(StructureFeatures.field_244150_p).withStructure(StructureFeatures.field_244134_E).withStructure(StructureFeatures.field_244153_s).withCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.field_243772_f).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA).withFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, Features.BASALT_PILLAR).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_OPEN).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE_EXTRA).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_CRIMSON_ROOTS).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_FIRE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_SOUL_FIRE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_MAGMA).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_CLOSED).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_SOUL_SAND);
        DefaultBiomeFeatures.withCommonNetherBlocks(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.NONE).category(Biome.Category.NETHER).depth(0.1F).scale(0.2F).temperature(2.0F).downfall(0.0F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(1787717).withSkyColor(getSkyColorWithTemperatureModifier(2.0F)).setParticle(new ParticleEffectAmbience(ParticleTypes.ASH, 0.00625F)).setAmbientSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP).setMoodSound(new MoodSoundAmbience(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD, 6000, 8, 2.0D)).setAdditionsSound(new SoundAdditionsAmbience(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS, 0.0111D)).setMusic(BackgroundMusicTracks.getDefaultBackgroundMusicSelector(SoundEvents.MUSIC_NETHER_SOUL_SAND_VALLEY)).build()).withMobSpawnSettings(mobspawninfo).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeBasaltDeltasBiome()
    {
        MobSpawnInfo mobspawninfo = (new MobSpawnInfo.Builder()).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.GHAST, 40, 1, 1)).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.MAGMA_CUBE, 100, 2, 5)).withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.STRIDER, 60, 1, 2)).copy();
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244170_b).withStructure(StructureFeatures.field_244134_E).withCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.field_243772_f).withStructure(StructureFeatures.field_244149_o).withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Features.DELTA).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA_DOUBLE).withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Features.SMALL_BASALT_COLUMNS).withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Features.LARGE_BASALT_COLUMNS).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.BASALT_BLOBS).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.BLACKSTONE_BLOBS).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_DELTA).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_FIRE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_SOUL_FIRE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE_EXTRA).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.BROWN_MUSHROOM_NETHER).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.RED_MUSHROOM_NETHER).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_MAGMA).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_CLOSED_DOUBLE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_GOLD_DELTAS).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_QUARTZ_DELTAS);
        DefaultBiomeFeatures.withDebrisOre(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.NONE).category(Biome.Category.NETHER).depth(0.1F).scale(0.2F).temperature(2.0F).downfall(0.0F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(4341314).setFogColor(6840176).withSkyColor(getSkyColorWithTemperatureModifier(2.0F)).setParticle(new ParticleEffectAmbience(ParticleTypes.WHITE_ASH, 0.118093334F)).setAmbientSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP).setMoodSound(new MoodSoundAmbience(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, 2.0D)).setAdditionsSound(new SoundAdditionsAmbience(SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.0111D)).setMusic(BackgroundMusicTracks.getDefaultBackgroundMusicSelector(SoundEvents.MUSIC_NETHER_BASALT_DELTAS)).build()).withMobSpawnSettings(mobspawninfo).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeCrimsonForestBiome()
    {
        MobSpawnInfo mobspawninfo = (new MobSpawnInfo.Builder()).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ZOMBIFIED_PIGLIN, 1, 2, 4)).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.HOGLIN, 9, 3, 4)).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.PIGLIN, 5, 3, 4)).withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.STRIDER, 60, 1, 2)).copy();
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244171_c).withStructure(StructureFeatures.field_244134_E).withCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.field_243772_f).withStructure(StructureFeatures.field_244149_o).withStructure(StructureFeatures.field_244153_s).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_OPEN).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_FIRE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE_EXTRA).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_MAGMA).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_CLOSED).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.WEEPING_VINES).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.CRIMSON_FUNGI).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.CRIMSON_FOREST_VEGETATION);
        DefaultBiomeFeatures.withCommonNetherBlocks(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.NONE).category(Biome.Category.NETHER).depth(0.1F).scale(0.2F).temperature(2.0F).downfall(0.0F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(3343107).withSkyColor(getSkyColorWithTemperatureModifier(2.0F)).setParticle(new ParticleEffectAmbience(ParticleTypes.CRIMSON_SPORE, 0.025F)).setAmbientSound(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP).setMoodSound(new MoodSoundAmbience(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0D)).setAdditionsSound(new SoundAdditionsAmbience(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111D)).setMusic(BackgroundMusicTracks.getDefaultBackgroundMusicSelector(SoundEvents.MUSIC_NETHER_CRIMSON_FOREST)).build()).withMobSpawnSettings(mobspawninfo).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static Biome makeWarpedForestBiome()
    {
        MobSpawnInfo mobspawninfo = (new MobSpawnInfo.Builder()).withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 1, 4, 4)).withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.STRIDER, 60, 1, 2)).withSpawnCost(EntityType.ENDERMAN, 1.0D, 0.12D).copy();
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244190_v).withStructure(StructureFeatures.field_244149_o).withStructure(StructureFeatures.field_244153_s).withStructure(StructureFeatures.field_244134_E).withCarver(GenerationStage.Carving.AIR, ConfiguredCarvers.field_243772_f).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SPRING_LAVA);
        DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
        biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_OPEN).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_FIRE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_SOUL_FIRE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE_EXTRA).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.GLOWSTONE).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.ORE_MAGMA).withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.SPRING_CLOSED).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.WARPED_FUNGI).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.WARPED_FOREST_VEGETATION).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.NETHER_SPROUTS).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.TWISTING_VINES);
        DefaultBiomeFeatures.withCommonNetherBlocks(biomegenerationsettings$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.NONE).category(Biome.Category.NETHER).depth(0.1F).scale(0.2F).temperature(2.0F).downfall(0.0F).setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204).setWaterFogColor(329011).setFogColor(1705242).withSkyColor(getSkyColorWithTemperatureModifier(2.0F)).setParticle(new ParticleEffectAmbience(ParticleTypes.WARPED_SPORE, 0.01428F)).setAmbientSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP).setMoodSound(new MoodSoundAmbience(SoundEvents.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2.0D)).setAdditionsSound(new SoundAdditionsAmbience(SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS, 0.0111D)).setMusic(BackgroundMusicTracks.getDefaultBackgroundMusicSelector(SoundEvents.MUSIC_NETHER_WARPED_FOREST)).build()).withMobSpawnSettings(mobspawninfo).withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }
}
