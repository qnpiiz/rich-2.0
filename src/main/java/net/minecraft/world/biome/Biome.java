package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Biome
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<Biome> CODEC = RecordCodecBuilder.create((builder) ->
    {
        return builder.group(Biome.Climate.CODEC.forGetter((biome) -> {
            return biome.climate;
        }), Biome.Category.CODEC.fieldOf("category").forGetter((biome) -> {
            return biome.category;
        }), Codec.FLOAT.fieldOf("depth").forGetter((biome) -> {
            return biome.depth;
        }), Codec.FLOAT.fieldOf("scale").forGetter((biome) -> {
            return biome.scale;
        }), BiomeAmbience.CODEC.fieldOf("effects").forGetter((biome) -> {
            return biome.effects;
        }), BiomeGenerationSettings.CODEC.forGetter((biome) -> {
            return biome.biomeGenerationSettings;
        }), MobSpawnInfo.CODEC.forGetter((biome) -> {
            return biome.mobSpawnInfo;
        })).apply(builder, Biome::new);
    });
    public static final Codec<Biome> PACKET_CODEC = RecordCodecBuilder.create((builder) ->
    {
        return builder.group(Biome.Climate.CODEC.forGetter((biome) -> {
            return biome.climate;
        }), Biome.Category.CODEC.fieldOf("category").forGetter((biome) -> {
            return biome.category;
        }), Codec.FLOAT.fieldOf("depth").forGetter((biome) -> {
            return biome.depth;
        }), Codec.FLOAT.fieldOf("scale").forGetter((biome) -> {
            return biome.scale;
        }), BiomeAmbience.CODEC.fieldOf("effects").forGetter((biome) -> {
            return biome.effects;
        })).apply(builder, (climate, category, depth, scale, ambience) -> {
            return new Biome(climate, category, depth, scale, ambience, BiomeGenerationSettings.DEFAULT_SETTINGS, MobSpawnInfo.EMPTY);
        });
    });
    public static final Codec<Supplier<Biome>> BIOME_CODEC = RegistryKeyCodec.create(Registry.BIOME_KEY, CODEC);
    public static final Codec<List<Supplier<Biome>>> BIOMES_CODEC = RegistryKeyCodec.getValueCodecs(Registry.BIOME_KEY, CODEC);
    private final Map < Integer, List < Structure<? >>> biomeStructures = Registry.STRUCTURE_FEATURE.stream().collect(Collectors.groupingBy((structure) ->
    {
        return structure.func_236396_f_().ordinal();
    }));
    private static final PerlinNoiseGenerator TEMPERATURE_NOISE = new PerlinNoiseGenerator(new SharedSeedRandom(1234L), ImmutableList.of(0));
    private static final PerlinNoiseGenerator FROZEN_TEMPERATURE_NOISE = new PerlinNoiseGenerator(new SharedSeedRandom(3456L), ImmutableList.of(-2, -1, 0));
    public static final PerlinNoiseGenerator INFO_NOISE = new PerlinNoiseGenerator(new SharedSeedRandom(2345L), ImmutableList.of(0));
    private final Biome.Climate climate;
    private final BiomeGenerationSettings biomeGenerationSettings;
    private final MobSpawnInfo mobSpawnInfo;
    private final float depth;
    private final float scale;
    private final Biome.Category category;
    private final BiomeAmbience effects;
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() ->
    {
        return Util.make(() -> {
            Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(1024, 0.25F)
            {
                protected void rehash(int p_rehash_1_)
                {
                }
            };
            long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
            return long2floatlinkedopenhashmap;
        });
    });

    private Biome(Biome.Climate climate, Biome.Category category, float depth, float scale, BiomeAmbience effects, BiomeGenerationSettings biomeGenerationSettings, MobSpawnInfo mobSpawnInfo)
    {
        this.climate = climate;
        this.biomeGenerationSettings = biomeGenerationSettings;
        this.mobSpawnInfo = mobSpawnInfo;
        this.category = category;
        this.depth = depth;
        this.scale = scale;
        this.effects = effects;
    }

    public int getSkyColor()
    {
        return this.effects.getSkyColor();
    }

    public MobSpawnInfo getMobSpawnInfo()
    {
        return this.mobSpawnInfo;
    }

    public Biome.RainType getPrecipitation()
    {
        return this.climate.precipitation;
    }

    /**
     * Checks to see if the rainfall level of the biome is extremely high
     */
    public boolean isHighHumidity()
    {
        return this.getDownfall() > 0.85F;
    }

    private float getTemperatureAtPosition(BlockPos pos)
    {
        float f = this.climate.temperatureModifier.getTemperatureAtPosition(pos, this.getTemperature());

        if (pos.getY() > 64)
        {
            float f1 = (float)(TEMPERATURE_NOISE.noiseAt((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F), false) * 4.0D);
            return f - (f1 + (float)pos.getY() - 64.0F) * 0.05F / 30.0F;
        }
        else
        {
            return f;
        }
    }

    public final float getTemperature(BlockPos pos)
    {
        long i = pos.toLong();
        Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = this.temperatureCache.get();
        float f = long2floatlinkedopenhashmap.get(i);

        if (!Float.isNaN(f))
        {
            return f;
        }
        else
        {
            float f1 = this.getTemperatureAtPosition(pos);

            if (long2floatlinkedopenhashmap.size() == 1024)
            {
                long2floatlinkedopenhashmap.removeFirstFloat();
            }

            long2floatlinkedopenhashmap.put(i, f1);
            return f1;
        }
    }

    public boolean doesWaterFreeze(IWorldReader worldIn, BlockPos pos)
    {
        return this.doesWaterFreeze(worldIn, pos, true);
    }

    public boolean doesWaterFreeze(IWorldReader worldIn, BlockPos water, boolean mustBeAtEdge)
    {
        if (this.getTemperature(water) >= 0.15F)
        {
            return false;
        }
        else
        {
            if (water.getY() >= 0 && water.getY() < 256 && worldIn.getLightFor(LightType.BLOCK, water) < 10)
            {
                BlockState blockstate = worldIn.getBlockState(water);
                FluidState fluidstate = worldIn.getFluidState(water);

                if (fluidstate.getFluid() == Fluids.WATER && blockstate.getBlock() instanceof FlowingFluidBlock)
                {
                    if (!mustBeAtEdge)
                    {
                        return true;
                    }

                    boolean flag = worldIn.hasWater(water.west()) && worldIn.hasWater(water.east()) && worldIn.hasWater(water.north()) && worldIn.hasWater(water.south());

                    if (!flag)
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean doesSnowGenerate(IWorldReader worldIn, BlockPos pos)
    {
        if (this.getTemperature(pos) >= 0.15F)
        {
            return false;
        }
        else
        {
            if (pos.getY() >= 0 && pos.getY() < 256 && worldIn.getLightFor(LightType.BLOCK, pos) < 10)
            {
                BlockState blockstate = worldIn.getBlockState(pos);

                if (blockstate.isAir() && Blocks.SNOW.getDefaultState().isValidPosition(worldIn, pos))
                {
                    return true;
                }
            }

            return false;
        }
    }

    public BiomeGenerationSettings getGenerationSettings()
    {
        return this.biomeGenerationSettings;
    }

    public void generateFeatures(StructureManager structureManager, ChunkGenerator chunkGenerator, WorldGenRegion worldGenRegion, long seed, SharedSeedRandom rand, BlockPos pos)
    {
        List < List < Supplier < ConfiguredFeature <? , ? >>> > list = this.biomeGenerationSettings.getFeatures();
        int i = GenerationStage.Decoration.values().length;

        for (int j = 0; j < i; ++j)
        {
            int k = 0;

            if (structureManager.func_235005_a_())
            {
                for (Structure<?> structure : this.biomeStructures.getOrDefault(j, Collections.emptyList()))
                {
                    rand.setFeatureSeed(seed, k, j);
                    int l = pos.getX() >> 4;
                    int i1 = pos.getZ() >> 4;
                    int j1 = l << 4;
                    int k1 = i1 << 4;

                    try
                    {
                        structureManager.func_235011_a_(SectionPos.from(pos), structure).forEach((structureStart) ->
                        {
                            structureStart.func_230366_a_(worldGenRegion, structureManager, chunkGenerator, rand, new MutableBoundingBox(j1, k1, j1 + 15, k1 + 15), new ChunkPos(l, i1));
                        });
                    }
                    catch (Exception exception)
                    {
                        CrashReport crashreport = CrashReport.makeCrashReport(exception, "Feature placement");
                        crashreport.makeCategory("Feature").addDetail("Id", Registry.STRUCTURE_FEATURE.getKey(structure)).addDetail("Description", () ->
                        {
                            return structure.toString();
                        });
                        throw new ReportedException(crashreport);
                    }

                    ++k;
                }
            }

            if (list.size() > j)
            {
                for (Supplier < ConfiguredFeature <? , ? >> supplier : list.get(j))
                {
                    ConfiguredFeature <? , ? > configuredfeature = supplier.get();
                    rand.setFeatureSeed(seed, k, j);

                    try
                    {
                        configuredfeature.func_242765_a(worldGenRegion, chunkGenerator, rand, pos);
                    }
                    catch (Exception exception1)
                    {
                        CrashReport crashreport1 = CrashReport.makeCrashReport(exception1, "Feature placement");
                        crashreport1.makeCategory("Feature").addDetail("Id", Registry.FEATURE.getKey(configuredfeature.feature)).addDetail("Config", configuredfeature.config).addDetail("Description", () ->
                        {
                            return configuredfeature.feature.toString();
                        });
                        throw new ReportedException(crashreport1);
                    }

                    ++k;
                }
            }
        }
    }

    public int getFogColor()
    {
        return this.effects.getFogColor();
    }

    public int getGrassColor(double posX, double posZ)
    {
        int i = this.effects.getGrassColor().orElseGet(this::getGrassColorByClimate);
        return this.effects.getGrassColorModifier().getModifiedGrassColor(posX, posZ, i);
    }

    private int getGrassColorByClimate()
    {
        double d0 = (double)MathHelper.clamp(this.climate.temperature, 0.0F, 1.0F);
        double d1 = (double)MathHelper.clamp(this.climate.downfall, 0.0F, 1.0F);
        return GrassColors.get(d0, d1);
    }

    public int getFoliageColor()
    {
        return this.effects.getFoliageColor().orElseGet(this::getFoliageColorByClimate);
    }

    private int getFoliageColorByClimate()
    {
        double d0 = (double)MathHelper.clamp(this.climate.temperature, 0.0F, 1.0F);
        double d1 = (double)MathHelper.clamp(this.climate.downfall, 0.0F, 1.0F);
        return FoliageColors.get(d0, d1);
    }

    public void buildSurface(Random random, IChunk chunkIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed)
    {
        ConfiguredSurfaceBuilder<?> configuredsurfacebuilder = this.biomeGenerationSettings.getSurfaceBuilder().get();
        configuredsurfacebuilder.setSeed(seed);
        configuredsurfacebuilder.buildSurface(random, chunkIn, this, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed);
    }

    public final float getDepth()
    {
        return this.depth;
    }

    /**
     * Gets a floating point representation of this biome's rainfall
     */
    public final float getDownfall()
    {
        return this.climate.downfall;
    }

    public final float getScale()
    {
        return this.scale;
    }

    public final float getTemperature()
    {
        return this.climate.temperature;
    }

    public BiomeAmbience getAmbience()
    {
        return this.effects;
    }

    public final int getWaterColor()
    {
        return this.effects.getWaterColor();
    }

    public final int getWaterFogColor()
    {
        return this.effects.getWaterFogColor();
    }

    public Optional<ParticleEffectAmbience> getAmbientParticle()
    {
        return this.effects.getParticle();
    }

    public Optional<SoundEvent> getAmbientSound()
    {
        return this.effects.getAmbientSound();
    }

    public Optional<MoodSoundAmbience> getMoodSound()
    {
        return this.effects.getMoodSound();
    }

    public Optional<SoundAdditionsAmbience> getAdditionalAmbientSound()
    {
        return this.effects.getAdditionsSound();
    }

    public Optional<BackgroundMusicSelector> getBackgroundMusic()
    {
        return this.effects.getMusic();
    }

    public final Biome.Category getCategory()
    {
        return this.category;
    }

    public String toString()
    {
        ResourceLocation resourcelocation = WorldGenRegistries.BIOME.getKey(this);
        return resourcelocation == null ? super.toString() : resourcelocation.toString();
    }

    public static class Attributes
    {
        public static final Codec<Biome.Attributes> CODEC = RecordCodecBuilder.create((builder) ->
        {
            return builder.group(Codec.floatRange(-2.0F, 2.0F).fieldOf("temperature").forGetter((attributes) -> {
                return attributes.temperature;
            }), Codec.floatRange(-2.0F, 2.0F).fieldOf("humidity").forGetter((attributes) -> {
                return attributes.humidity;
            }), Codec.floatRange(-2.0F, 2.0F).fieldOf("altitude").forGetter((attributes) -> {
                return attributes.altitude;
            }), Codec.floatRange(-2.0F, 2.0F).fieldOf("weirdness").forGetter((attributes) -> {
                return attributes.weirdness;
            }), Codec.floatRange(0.0F, 1.0F).fieldOf("offset").forGetter((attributes) -> {
                return attributes.offset;
            })).apply(builder, Biome.Attributes::new);
        });
        private final float temperature;
        private final float humidity;
        private final float altitude;
        private final float weirdness;
        private final float offset;

        public Attributes(float temperature, float humidity, float altitude, float weirdness, float offset)
        {
            this.temperature = temperature;
            this.humidity = humidity;
            this.altitude = altitude;
            this.weirdness = weirdness;
            this.offset = offset;
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
            {
                Biome.Attributes biome$attributes = (Biome.Attributes)p_equals_1_;

                if (Float.compare(biome$attributes.temperature, this.temperature) != 0)
                {
                    return false;
                }
                else if (Float.compare(biome$attributes.humidity, this.humidity) != 0)
                {
                    return false;
                }
                else if (Float.compare(biome$attributes.altitude, this.altitude) != 0)
                {
                    return false;
                }
                else
                {
                    return Float.compare(biome$attributes.weirdness, this.weirdness) == 0;
                }
            }
            else
            {
                return false;
            }
        }

        public int hashCode()
        {
            int i = this.temperature != 0.0F ? Float.floatToIntBits(this.temperature) : 0;
            i = 31 * i + (this.humidity != 0.0F ? Float.floatToIntBits(this.humidity) : 0);
            i = 31 * i + (this.altitude != 0.0F ? Float.floatToIntBits(this.altitude) : 0);
            return 31 * i + (this.weirdness != 0.0F ? Float.floatToIntBits(this.weirdness) : 0);
        }

        public float getAttributeDifference(Biome.Attributes attributes)
        {
            return (this.temperature - attributes.temperature) * (this.temperature - attributes.temperature) + (this.humidity - attributes.humidity) * (this.humidity - attributes.humidity) + (this.altitude - attributes.altitude) * (this.altitude - attributes.altitude) + (this.weirdness - attributes.weirdness) * (this.weirdness - attributes.weirdness) + (this.offset - attributes.offset) * (this.offset - attributes.offset);
        }
    }

    public static class Builder
    {
        @Nullable
        private Biome.RainType precipitation;
        @Nullable
        private Biome.Category category;
        @Nullable
        private Float depth;
        @Nullable
        private Float scale;
        @Nullable
        private Float temperature;
        private Biome.TemperatureModifier temperatureModifier = Biome.TemperatureModifier.NONE;
        @Nullable
        private Float downfall;
        @Nullable
        private BiomeAmbience effects;
        @Nullable
        private MobSpawnInfo mobSpawnSettings;
        @Nullable
        private BiomeGenerationSettings generationSettings;

        public Biome.Builder precipitation(Biome.RainType precipitationIn)
        {
            this.precipitation = precipitationIn;
            return this;
        }

        public Biome.Builder category(Biome.Category biomeCategory)
        {
            this.category = biomeCategory;
            return this;
        }

        public Biome.Builder depth(float depthIn)
        {
            this.depth = depthIn;
            return this;
        }

        public Biome.Builder scale(float scaleIn)
        {
            this.scale = scaleIn;
            return this;
        }

        public Biome.Builder temperature(float temperatureIn)
        {
            this.temperature = temperatureIn;
            return this;
        }

        public Biome.Builder downfall(float downfallIn)
        {
            this.downfall = downfallIn;
            return this;
        }

        public Biome.Builder setEffects(BiomeAmbience effects)
        {
            this.effects = effects;
            return this;
        }

        public Biome.Builder withMobSpawnSettings(MobSpawnInfo mobSpawnSettings)
        {
            this.mobSpawnSettings = mobSpawnSettings;
            return this;
        }

        public Biome.Builder withGenerationSettings(BiomeGenerationSettings generationSettings)
        {
            this.generationSettings = generationSettings;
            return this;
        }

        public Biome.Builder withTemperatureModifier(Biome.TemperatureModifier temperatureSettings)
        {
            this.temperatureModifier = temperatureSettings;
            return this;
        }

        public Biome build()
        {
            if (this.precipitation != null && this.category != null && this.depth != null && this.scale != null && this.temperature != null && this.downfall != null && this.effects != null && this.mobSpawnSettings != null && this.generationSettings != null)
            {
                return new Biome(new Biome.Climate(this.precipitation, this.temperature, this.temperatureModifier, this.downfall), this.category, this.depth, this.scale, this.effects, this.generationSettings, this.mobSpawnSettings);
            }
            else
            {
                throw new IllegalStateException("You are missing parameters to build a proper biome\n" + this);
            }
        }

        public String toString()
        {
            return "BiomeBuilder{\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.category + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + this.temperatureModifier + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.effects + ",\nmobSpawnSettings=" + this.mobSpawnSettings + ",\ngenerationSettings=" + this.generationSettings + ",\n" + '}';
        }
    }

    public static enum Category implements IStringSerializable
    {
        NONE("none"),
        TAIGA("taiga"),
        EXTREME_HILLS("extreme_hills"),
        JUNGLE("jungle"),
        MESA("mesa"),
        PLAINS("plains"),
        SAVANNA("savanna"),
        ICY("icy"),
        THEEND("the_end"),
        BEACH("beach"),
        FOREST("forest"),
        OCEAN("ocean"),
        DESERT("desert"),
        RIVER("river"),
        SWAMP("swamp"),
        MUSHROOM("mushroom"),
        NETHER("nether");

        public static final Codec<Biome.Category> CODEC = IStringSerializable.createEnumCodec(Biome.Category::values, Biome.Category::byName);
        private static final Map<String, Biome.Category> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Biome.Category::getName, (category) -> {
            return category;
        }));
        private final String name;

        private Category(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }

        public static Biome.Category byName(String name)
        {
            return BY_NAME.get(name);
        }

        public String getString()
        {
            return this.name;
        }
    }

    static class Climate
    {
        public static final MapCodec<Biome.Climate> CODEC = RecordCodecBuilder.mapCodec((builder) ->
        {
            return builder.group(Biome.RainType.CODEC.fieldOf("precipitation").forGetter((precipitation) -> {
                return precipitation.precipitation;
            }), Codec.FLOAT.fieldOf("temperature").forGetter((climate) -> {
                return climate.temperature;
            }), Biome.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", Biome.TemperatureModifier.NONE).forGetter((climate) -> {
                return climate.temperatureModifier;
            }), Codec.FLOAT.fieldOf("downfall").forGetter((climate) -> {
                return climate.downfall;
            })).apply(builder, Biome.Climate::new);
        });
        private final Biome.RainType precipitation;
        private final float temperature;
        private final Biome.TemperatureModifier temperatureModifier;
        private final float downfall;

        private Climate(Biome.RainType precipitation, float temperature, Biome.TemperatureModifier temperatureModifier, float downfall)
        {
            this.precipitation = precipitation;
            this.temperature = temperature;
            this.temperatureModifier = temperatureModifier;
            this.downfall = downfall;
        }
    }

    public static enum RainType implements IStringSerializable
    {
        NONE("none"),
        RAIN("rain"),
        SNOW("snow");

        public static final Codec<Biome.RainType> CODEC = IStringSerializable.createEnumCodec(Biome.RainType::values, Biome.RainType::getRainType);
        private static final Map<String, Biome.RainType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Biome.RainType::getName, (precipitation) -> {
            return precipitation;
        }));
        private final String name;

        private RainType(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }

        public static Biome.RainType getRainType(String name)
        {
            return BY_NAME.get(name);
        }

        public String getString()
        {
            return this.name;
        }
    }

    public static enum TemperatureModifier implements IStringSerializable
    {
        NONE("none")
        {
            public float getTemperatureAtPosition(BlockPos pos, float temperature)
            {
                return temperature;
            }
        },
        FROZEN("frozen")
        {
            public float getTemperatureAtPosition(BlockPos pos, float temperature)
            {
                double d0 = Biome.FROZEN_TEMPERATURE_NOISE.noiseAt((double)pos.getX() * 0.05D, (double)pos.getZ() * 0.05D, false) * 7.0D;
                double d1 = Biome.INFO_NOISE.noiseAt((double)pos.getX() * 0.2D, (double)pos.getZ() * 0.2D, false);
                double d2 = d0 + d1;

                if (d2 < 0.3D)
                {
                    double d3 = Biome.INFO_NOISE.noiseAt((double)pos.getX() * 0.09D, (double)pos.getZ() * 0.09D, false);

                    if (d3 < 0.8D)
                    {
                        return 0.2F;
                    }
                }

                return temperature;
            }
        };

        private final String name;
        public static final Codec<Biome.TemperatureModifier> CODEC = IStringSerializable.createEnumCodec(Biome.TemperatureModifier::values, Biome.TemperatureModifier::byName);
        private static final Map<String, Biome.TemperatureModifier> NAME_TO_MODIFIER_MAP = Arrays.stream(values()).collect(Collectors.toMap(Biome.TemperatureModifier::getName, (temperatureModifier) -> {
            return temperatureModifier;
        }));

        public abstract float getTemperatureAtPosition(BlockPos pos, float temperature);

        private TemperatureModifier(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }

        public String getString()
        {
            return this.name;
        }

        public static Biome.TemperatureModifier byName(String name)
        {
            return NAME_TO_MODIFIER_MAP.get(name);
        }
    }
}
