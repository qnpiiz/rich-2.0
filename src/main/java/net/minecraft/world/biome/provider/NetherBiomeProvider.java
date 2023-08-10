package net.minecraft.world.biome.provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.MaxMinNoiseMixer;

public class NetherBiomeProvider extends BiomeProvider
{
    private static final NetherBiomeProvider.Noise DEFAULT_NOISE = new NetherBiomeProvider.Noise(-7, ImmutableList.of(1.0D, 1.0D));
    public static final MapCodec<NetherBiomeProvider> PACKET_CODEC = RecordCodecBuilder.mapCodec((builder) ->
    {
        return builder.group(Codec.LONG.fieldOf("seed").forGetter((netherProvider) -> {
            return netherProvider.seed;
        }), RecordCodecBuilder.<Pair<Biome.Attributes, Supplier<Biome>>>create((biomeAttributes) -> {
            return biomeAttributes.group(Biome.Attributes.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.BIOME_CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(biomeAttributes, Pair::of);
        }).listOf().fieldOf("biomes").forGetter((netherProvider) -> {
            return netherProvider.biomeAttributes;
        }), NetherBiomeProvider.Noise.CODEC.fieldOf("temperature_noise").forGetter((netherProvider) -> {
            return netherProvider.temperatureNoise;
        }), NetherBiomeProvider.Noise.CODEC.fieldOf("humidity_noise").forGetter((netherProvider) -> {
            return netherProvider.humidityNoise;
        }), NetherBiomeProvider.Noise.CODEC.fieldOf("altitude_noise").forGetter((netherProvider) -> {
            return netherProvider.altitudeNoise;
        }), NetherBiomeProvider.Noise.CODEC.fieldOf("weirdness_noise").forGetter((netherProvider) -> {
            return netherProvider.weirdnessNoise;
        })).apply(builder, NetherBiomeProvider::new);
    });
    public static final Codec<NetherBiomeProvider> CODEC = Codec.mapEither(NetherBiomeProvider.DefaultBuilder.CODEC, PACKET_CODEC).xmap((either) ->
    {
        return either.map(NetherBiomeProvider.DefaultBuilder::build, Function.identity());
    }, (netherProvider) ->
    {
        return netherProvider.getDefaultBuilder().map(Either::<DefaultBuilder, NetherBiomeProvider>left).orElseGet(() -> {
            return Either.right(netherProvider);
        });
    }).codec();
    private final NetherBiomeProvider.Noise temperatureNoise;
    private final NetherBiomeProvider.Noise humidityNoise;
    private final NetherBiomeProvider.Noise altitudeNoise;
    private final NetherBiomeProvider.Noise weirdnessNoise;
    private final MaxMinNoiseMixer temperatureNoiseMixer;
    private final MaxMinNoiseMixer humidityNoiseMixer;
    private final MaxMinNoiseMixer altitudeNoiseMixer;
    private final MaxMinNoiseMixer weirdnessNoiseMixer;
    private final List<Pair<Biome.Attributes, Supplier<Biome>>> biomeAttributes;
    private final boolean useHeightForNoise;
    private final long seed;
    private final Optional<Pair<Registry<Biome>, NetherBiomeProvider.Preset>> netherProviderPreset;

    private NetherBiomeProvider(long seed, List<Pair<Biome.Attributes, Supplier<Biome>>> biomeAttributes, Optional<Pair<Registry<Biome>, NetherBiomeProvider.Preset>> netherProviderPreset)
    {
        this(seed, biomeAttributes, DEFAULT_NOISE, DEFAULT_NOISE, DEFAULT_NOISE, DEFAULT_NOISE, netherProviderPreset);
    }

    private NetherBiomeProvider(long seed, List<Pair<Biome.Attributes, Supplier<Biome>>> biomeAttributes, NetherBiomeProvider.Noise temperatureNoise, NetherBiomeProvider.Noise humidityNoise, NetherBiomeProvider.Noise altitudeNoise, NetherBiomeProvider.Noise weirdnessNoise)
    {
        this(seed, biomeAttributes, temperatureNoise, humidityNoise, altitudeNoise, weirdnessNoise, Optional.empty());
    }

    private NetherBiomeProvider(long seed, List<Pair<Biome.Attributes, Supplier<Biome>>> biomeAttributes, NetherBiomeProvider.Noise temperatureNoise, NetherBiomeProvider.Noise humidityNoise, NetherBiomeProvider.Noise altitudeNoise, NetherBiomeProvider.Noise weirdnessNoise, Optional<Pair<Registry<Biome>, NetherBiomeProvider.Preset>> netherProviderPreset)
    {
        super(biomeAttributes.stream().map(Pair::getSecond));
        this.seed = seed;
        this.netherProviderPreset = netherProviderPreset;
        this.temperatureNoise = temperatureNoise;
        this.humidityNoise = humidityNoise;
        this.altitudeNoise = altitudeNoise;
        this.weirdnessNoise = weirdnessNoise;
        this.temperatureNoiseMixer = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seed), temperatureNoise.getNumberOfOctaves(), temperatureNoise.getAmplitudes());
        this.humidityNoiseMixer = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seed + 1L), humidityNoise.getNumberOfOctaves(), humidityNoise.getAmplitudes());
        this.altitudeNoiseMixer = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seed + 2L), altitudeNoise.getNumberOfOctaves(), altitudeNoise.getAmplitudes());
        this.weirdnessNoiseMixer = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seed + 3L), weirdnessNoise.getNumberOfOctaves(), weirdnessNoise.getAmplitudes());
        this.biomeAttributes = biomeAttributes;
        this.useHeightForNoise = false;
    }

    protected Codec <? extends BiomeProvider > getBiomeProviderCodec()
    {
        return CODEC;
    }

    public BiomeProvider getBiomeProvider(long seed)
    {
        return new NetherBiomeProvider(seed, this.biomeAttributes, this.temperatureNoise, this.humidityNoise, this.altitudeNoise, this.weirdnessNoise, this.netherProviderPreset);
    }

    private Optional<NetherBiomeProvider.DefaultBuilder> getDefaultBuilder()
    {
        return this.netherProviderPreset.map((registryPresetPair) ->
        {
            return new NetherBiomeProvider.DefaultBuilder(registryPresetPair.getSecond(), registryPresetPair.getFirst(), this.seed);
        });
    }

    public Biome getNoiseBiome(int x, int y, int z)
    {
        int i = this.useHeightForNoise ? y : 0;
        Biome.Attributes biome$attributes = new Biome.Attributes((float)this.temperatureNoiseMixer.func_237211_a_((double)x, (double)i, (double)z), (float)this.humidityNoiseMixer.func_237211_a_((double)x, (double)i, (double)z), (float)this.altitudeNoiseMixer.func_237211_a_((double)x, (double)i, (double)z), (float)this.weirdnessNoiseMixer.func_237211_a_((double)x, (double)i, (double)z), 0.0F);
        return this.biomeAttributes.stream().min(Comparator.comparing((attributeBiomePair) ->
        {
            return attributeBiomePair.getFirst().getAttributeDifference(biome$attributes);
        })).map(Pair::getSecond).map(Supplier::get).orElse(BiomeRegistry.THE_VOID);
    }

    public boolean isDefaultPreset(long seed)
    {
        return this.seed == seed && this.netherProviderPreset.isPresent() && Objects.equals(this.netherProviderPreset.get().getSecond(), NetherBiomeProvider.Preset.DEFAULT_NETHER_PROVIDER_PRESET);
    }

    static final class DefaultBuilder
    {
        public static final MapCodec<NetherBiomeProvider.DefaultBuilder> CODEC = RecordCodecBuilder.mapCodec((builder) ->
        {
            return builder.group(ResourceLocation.CODEC.flatXmap((id) -> {
                return Optional.ofNullable(NetherBiomeProvider.Preset.PRESETS.get(id)).map(DataResult::success).orElseGet(() -> {
                    return DataResult.error("Unknown preset: " + id);
                });
            }, (preset) -> {
                return DataResult.success(preset.id);
            }).fieldOf("preset").stable().forGetter(NetherBiomeProvider.DefaultBuilder::getPreset), RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(NetherBiomeProvider.DefaultBuilder::getLookupRegistry), Codec.LONG.fieldOf("seed").stable().forGetter(NetherBiomeProvider.DefaultBuilder::getSeed)).apply(builder, builder.stable(NetherBiomeProvider.DefaultBuilder::new));
        });
        private final NetherBiomeProvider.Preset preset;
        private final Registry<Biome> lookupRegistry;
        private final long seed;

        private DefaultBuilder(NetherBiomeProvider.Preset preset, Registry<Biome> lookupRegistry, long seed)
        {
            this.preset = preset;
            this.lookupRegistry = lookupRegistry;
            this.seed = seed;
        }

        public NetherBiomeProvider.Preset getPreset()
        {
            return this.preset;
        }

        public Registry<Biome> getLookupRegistry()
        {
            return this.lookupRegistry;
        }

        public long getSeed()
        {
            return this.seed;
        }

        public NetherBiomeProvider build()
        {
            return this.preset.build(this.lookupRegistry, this.seed);
        }
    }

    static class Noise
    {
        private final int numOctaves;
        private final DoubleList amplitudes;
        public static final Codec<NetherBiomeProvider.Noise> CODEC = RecordCodecBuilder.create((builder) ->
        {
            return builder.group(Codec.INT.fieldOf("firstOctave").forGetter(NetherBiomeProvider.Noise::getNumberOfOctaves), Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(NetherBiomeProvider.Noise::getAmplitudes)).apply(builder, NetherBiomeProvider.Noise::new);
        });

        public Noise(int numOctaves, List<Double> amplitudes)
        {
            this.numOctaves = numOctaves;
            this.amplitudes = new DoubleArrayList(amplitudes);
        }

        public int getNumberOfOctaves()
        {
            return this.numOctaves;
        }

        public DoubleList getAmplitudes()
        {
            return this.amplitudes;
        }
    }

    public static class Preset
    {
        private static final Map<ResourceLocation, NetherBiomeProvider.Preset> PRESETS = Maps.newHashMap();
        public static final NetherBiomeProvider.Preset DEFAULT_NETHER_PROVIDER_PRESET = new NetherBiomeProvider.Preset(new ResourceLocation("nether"), (preset, lookupRegistry, seed) ->
        {
            return new NetherBiomeProvider(seed, ImmutableList.of(Pair.of(new Biome.Attributes(0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
                return lookupRegistry.getOrThrow(Biomes.NETHER_WASTES);
            }), Pair.of(new Biome.Attributes(0.0F, -0.5F, 0.0F, 0.0F, 0.0F), () -> {
                return lookupRegistry.getOrThrow(Biomes.SOUL_SAND_VALLEY);
            }), Pair.of(new Biome.Attributes(0.4F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
                return lookupRegistry.getOrThrow(Biomes.CRIMSON_FOREST);
            }), Pair.of(new Biome.Attributes(0.0F, 0.5F, 0.0F, 0.0F, 0.375F), () -> {
                return lookupRegistry.getOrThrow(Biomes.WARPED_FOREST);
            }), Pair.of(new Biome.Attributes(-0.5F, 0.0F, 0.0F, 0.0F, 0.175F), () -> {
                return lookupRegistry.getOrThrow(Biomes.BASALT_DELTAS);
            })), Optional.of(Pair.of(lookupRegistry, preset)));
        });
        private final ResourceLocation id;
        private final Function3<NetherBiomeProvider.Preset, Registry<Biome>, Long, NetherBiomeProvider> netherProviderFunction;

        public Preset(ResourceLocation id, Function3<NetherBiomeProvider.Preset, Registry<Biome>, Long, NetherBiomeProvider> netherProviderFunction)
        {
            this.id = id;
            this.netherProviderFunction = netherProviderFunction;
            PRESETS.put(id, this);
        }

        public NetherBiomeProvider build(Registry<Biome> lookupRegistry, long seed)
        {
            return this.netherProviderFunction.apply(this, lookupRegistry, seed);
        }
    }
}
