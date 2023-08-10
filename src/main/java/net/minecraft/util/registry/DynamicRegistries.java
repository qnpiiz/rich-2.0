package net.minecraft.util.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class DynamicRegistries
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map < RegistryKey <? extends Registry<? >> , DynamicRegistries.CodecHolder<? >> registryCodecMap = Util.make(() ->
    {
        Builder < RegistryKey <? extends Registry<? >> , DynamicRegistries.CodecHolder<? >> builder = ImmutableMap.builder();
        put(builder, Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC, DimensionType.CODEC);
        put(builder, Registry.BIOME_KEY, Biome.CODEC, Biome.PACKET_CODEC);
        put(builder, Registry.CONFIGURED_SURFACE_BUILDER_KEY, ConfiguredSurfaceBuilder.field_237168_a_);
        put(builder, Registry.CONFIGURED_CARVER_KEY, ConfiguredCarver.field_236235_a_);
        put(builder, Registry.CONFIGURED_FEATURE_KEY, ConfiguredFeature.field_242763_a);
        put(builder, Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, StructureFeature.field_236267_a_);
        put(builder, Registry.STRUCTURE_PROCESSOR_LIST_KEY, IStructureProcessorType.field_242921_l);
        put(builder, Registry.JIGSAW_POOL_KEY, JigsawPattern.field_236852_a_);
        put(builder, Registry.NOISE_SETTINGS_KEY, DimensionSettings.field_236097_a_);
        return builder.build();
    });
    private static final DynamicRegistries.Impl registries = Util.make(() ->
    {
        DynamicRegistries.Impl dynamicregistries$impl = new DynamicRegistries.Impl();
        DimensionType.registerTypes(dynamicregistries$impl);
        registryCodecMap.keySet().stream().filter((registryKey) -> {
            return !registryKey.equals(Registry.DIMENSION_TYPE_KEY);
        }).forEach((registerKey) -> {
            getWorldGenRegistry(dynamicregistries$impl, registerKey);
        });
        return dynamicregistries$impl;
    });

    public abstract <E> Optional<MutableRegistry<E>> func_230521_a_(RegistryKey <? extends Registry<E >> p_230521_1_);

    public <E> MutableRegistry<E> getRegistry(RegistryKey <? extends Registry<E >> registryKey)
    {
        return this.func_230521_a_(registryKey).orElseThrow(() ->
        {
            return new IllegalStateException("Missing registry: " + registryKey);
        });
    }

    public Registry<DimensionType> func_230520_a_()
    {
        return this.getRegistry(Registry.DIMENSION_TYPE_KEY);
    }

    private static <E> void put(Builder < RegistryKey <? extends Registry<? >> , DynamicRegistries.CodecHolder<? >> codecHolder, RegistryKey <? extends Registry<E >> registryKey, Codec<E> codec)
    {
        codecHolder.put(registryKey, new DynamicRegistries.CodecHolder<>(registryKey, codec, (Codec<E>)null));
    }

    private static <E> void put(Builder < RegistryKey <? extends Registry<? >> , DynamicRegistries.CodecHolder<? >> codecHolder, RegistryKey <? extends Registry<E >> registryKey, Codec<E> codec, Codec<E> codec2)
    {
        codecHolder.put(registryKey, new DynamicRegistries.CodecHolder<>(registryKey, codec, codec2));
    }

    public static DynamicRegistries.Impl func_239770_b_()
    {
        DynamicRegistries.Impl dynamicregistries$impl = new DynamicRegistries.Impl();
        WorldSettingsImport.IResourceAccess.RegistryAccess worldsettingsimport$iresourceaccess$registryaccess = new WorldSettingsImport.IResourceAccess.RegistryAccess();

        for (DynamicRegistries.CodecHolder<?> codecholder : registryCodecMap.values())
        {
            registerRegistry(dynamicregistries$impl, worldsettingsimport$iresourceaccess$registryaccess, codecholder);
        }

        WorldSettingsImport.create(JsonOps.INSTANCE, worldsettingsimport$iresourceaccess$registryaccess, dynamicregistries$impl);
        return dynamicregistries$impl;
    }

    private static <E> void registerRegistry(DynamicRegistries.Impl dynamicRegistries, WorldSettingsImport.IResourceAccess.RegistryAccess registryAccess, DynamicRegistries.CodecHolder<E> codecHolder)
    {
        RegistryKey <? extends Registry<E >> registrykey = codecHolder.getRegistryKey();
        boolean flag = !registrykey.equals(Registry.NOISE_SETTINGS_KEY) && !registrykey.equals(Registry.DIMENSION_TYPE_KEY);
        Registry<E> registry = registries.getRegistry(registrykey);
        MutableRegistry<E> mutableregistry = dynamicRegistries.getRegistry(registrykey);

        for (Entry<RegistryKey<E>, E> entry : registry.getEntries())
        {
            E e = entry.getValue();

            if (flag)
            {
                registryAccess.encode(registries, entry.getKey(), codecHolder.getRegistryCodec(), registry.getId(e), e, registry.getLifecycleByRegistry(e));
            }
            else
            {
                mutableregistry.register(registry.getId(e), entry.getKey(), e, registry.getLifecycleByRegistry(e));
            }
        }
    }

    private static < R extends Registry<? >> void getWorldGenRegistry(DynamicRegistries.Impl dynamicRegistries, RegistryKey<R> key)
    {
        Registry<R> registry = (Registry<R>)WorldGenRegistries.ROOT_REGISTRIES;
        Registry<?> registry1 = registry.getValueForKey(key);

        if (registry1 == null)
        {
            throw new IllegalStateException("Missing builtin registry: " + key);
        }
        else
        {
            registerRegistry(dynamicRegistries, registry1);
        }
    }

    private static <E> void registerRegistry(DynamicRegistries.Impl dynamicRegistries, Registry<E> registry)
    {
        MutableRegistry<E> mutableregistry = dynamicRegistries.<E>func_230521_a_(registry.getRegistryKey()).orElseThrow(() ->
        {
            return new IllegalStateException("Missing registry: " + registry.getRegistryKey());
        });

        for (Entry<RegistryKey<E>, E> entry : registry.getEntries())
        {
            E e = entry.getValue();
            mutableregistry.register(registry.getId(e), entry.getKey(), e, registry.getLifecycleByRegistry(e));
        }
    }

    public static void loadRegistryData(DynamicRegistries.Impl dynamicRegistries, WorldSettingsImport<?> settingsImport)
    {
        for (DynamicRegistries.CodecHolder<?> codecholder : registryCodecMap.values())
        {
            loadRegistryData(settingsImport, dynamicRegistries, codecholder);
        }
    }

    private static <E> void loadRegistryData(WorldSettingsImport<?> settingsImport, DynamicRegistries.Impl dynamicRegistries, DynamicRegistries.CodecHolder<E> codecHolder)
    {
        RegistryKey <? extends Registry<E >> registrykey = codecHolder.getRegistryKey();
        SimpleRegistry<E> simpleregistry = Optional.ofNullable((SimpleRegistry<E>)dynamicRegistries.keyToSimpleRegistryMap.get(registrykey)).map((simpleRegistry) ->
        {
            return simpleRegistry;
        }).orElseThrow(() ->
        {
            return new IllegalStateException("Missing registry: " + registrykey);
        });
        DataResult<SimpleRegistry<E>> dataresult = settingsImport.decode(simpleregistry, codecHolder.getRegistryKey(), codecHolder.getRegistryCodec());
        dataresult.error().ifPresent((result) ->
        {
            LOGGER.error("Error loading registry data: {}", (Object)result.message());
        });
    }

    static final class CodecHolder<E>
    {
        private final RegistryKey <? extends Registry<E >> registryKey;
        private final Codec<E> registryCodec;
        @Nullable
        private final Codec<E> packetCodec;

        public CodecHolder(RegistryKey <? extends Registry<E >> registryKey, Codec<E> registryCodec, @Nullable Codec<E> packetCodec)
        {
            this.registryKey = registryKey;
            this.registryCodec = registryCodec;
            this.packetCodec = packetCodec;
        }

        public RegistryKey <? extends Registry<E >> getRegistryKey()
        {
            return this.registryKey;
        }

        public Codec<E> getRegistryCodec()
        {
            return this.registryCodec;
        }

        @Nullable
        public Codec<E> getPacketCodec()
        {
            return this.packetCodec;
        }

        public boolean hasPacketCodec()
        {
            return this.packetCodec != null;
        }
    }

    public static final class Impl extends DynamicRegistries
    {
        public static final Codec<DynamicRegistries.Impl> registryCodec = getCodec();
        private final Map <? extends RegistryKey <? extends Registry<? >> , ? extends SimpleRegistry<? >> keyToSimpleRegistryMap;

        private static <E> Codec<DynamicRegistries.Impl> getCodec()
        {
            Codec < RegistryKey <? extends Registry<E >>> codec = ResourceLocation.CODEC.xmap(RegistryKey::getOrCreateRootKey, RegistryKey::getLocation);
            Codec<SimpleRegistry<E>> codec1 = codec.partialDispatch("type", (simpleRegistry) ->
            {
                return DataResult.success(simpleRegistry.getRegistryKey());
            }, (registryKey) ->
            {
                return serializeRegistry(registryKey).map((codec2) -> {
                    return SimpleRegistry.createSimpleRegistryCodec(registryKey, Lifecycle.experimental(), codec2);
                });
            });
            UnboundedMapCodec <? extends RegistryKey <? extends Registry<? >> , ? extends SimpleRegistry<? >> unboundedmapcodec = Codec.unboundedMap(codec, codec1);
            return getSerializableCodec(unboundedmapcodec);
        }

        private static < K extends RegistryKey <? extends Registry<? >> , V extends SimpleRegistry<? >> Codec<DynamicRegistries.Impl> getSerializableCodec(UnboundedMapCodec<K, V> unboundedCodec)
        {
            return unboundedCodec.xmap(DynamicRegistries.Impl::new, (dynamicRegistries) ->
            {
                return ((java.util.Set<Map.Entry<K, V>>)(Object)(dynamicRegistries.keyToSimpleRegistryMap.entrySet())).stream().filter((entry) -> {
                    return DynamicRegistries.registryCodecMap.get(entry.getKey()).hasPacketCodec();
                }).collect(ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue));
            });
        }

        private static <E> DataResult <? extends Codec<E >> serializeRegistry(RegistryKey <? extends Registry<E >> registryKey)
        {
            return Optional.ofNullable((CodecHolder<E>)DynamicRegistries.registryCodecMap.get(registryKey)).map((codecHolder) ->
            {
                return codecHolder.getPacketCodec();
            }).map(DataResult::success).orElseGet(() ->
            {
                return DataResult.error("Unknown or not serializable registry: " + registryKey);
            });
        }

        public Impl()
        {
            this(DynamicRegistries.registryCodecMap.keySet().stream().collect(Collectors.toMap(Function.identity(), DynamicRegistries.Impl::createStableRegistry)));
        }

        private Impl(Map <? extends RegistryKey <? extends Registry<? >> , ? extends SimpleRegistry<? >> keyToSimpleRegistryMap)
        {
            this.keyToSimpleRegistryMap = keyToSimpleRegistryMap;
        }

        private static <E> SimpleRegistry<?> createStableRegistry(RegistryKey <? extends Registry<? >> registerKey)
        {
            return new SimpleRegistry(registerKey, Lifecycle.stable());
        }

        public <E> Optional<MutableRegistry<E>> func_230521_a_(RegistryKey <? extends Registry<E >> p_230521_1_)
        {
            return Optional.ofNullable((MutableRegistry<E>)this.keyToSimpleRegistryMap.get(p_230521_1_)).map((mutable) ->
            {
                return mutable;
            });
        }
    }
}
