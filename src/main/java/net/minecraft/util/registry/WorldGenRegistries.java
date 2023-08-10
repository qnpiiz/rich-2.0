package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenRegistries
{
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Map < ResourceLocation, Supplier<? >> REGISTRY_NAME_TO_DEFAULT = Maps.newLinkedHashMap();
    private static final MutableRegistry < MutableRegistry<? >> INTERNAL_ROOT_REGISTRIES = new SimpleRegistry<>(RegistryKey.getOrCreateRootKey(new ResourceLocation("root")), Lifecycle.experimental());
    public static final Registry <? extends Registry<? >> ROOT_REGISTRIES = INTERNAL_ROOT_REGISTRIES;
    public static final Registry < ConfiguredSurfaceBuilder<? >> CONFIGURED_SURFACE_BUILDER = createRegistry(Registry.CONFIGURED_SURFACE_BUILDER_KEY, () ->
    {
        return ConfiguredSurfaceBuilders.field_244184_p;
    });
    public static final Registry < ConfiguredCarver<? >> CONFIGURED_CARVER = createRegistry(Registry.CONFIGURED_CARVER_KEY, () ->
    {
        return ConfiguredCarvers.field_243767_a;
    });
    public static final Registry < ConfiguredFeature <? , ? >> CONFIGURED_FEATURE = createRegistry(Registry.CONFIGURED_FEATURE_KEY, () ->
    {
        return Features.OAK;
    });
    public static final Registry < StructureFeature <? , ? >> CONFIGURED_STRUCTURE_FEATURE = createRegistry(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, () ->
    {
        return StructureFeatures.field_244136_b;
    });
    public static final Registry<StructureProcessorList> STRUCTURE_PROCESSOR_LIST = createRegistry(Registry.STRUCTURE_PROCESSOR_LIST_KEY, () ->
    {
        return ProcessorLists.field_244102_b;
    });
    public static final Registry<JigsawPattern> JIGSAW_POOL = createRegistry(Registry.JIGSAW_POOL_KEY, JigsawPatternRegistry::func_244093_a);
    public static final Registry<Biome> BIOME = createRegistry(Registry.BIOME_KEY, () ->
    {
        return BiomeRegistry.PLAINS;
    });
    public static final Registry<DimensionSettings> NOISE_SETTINGS = createRegistry(Registry.NOISE_SETTINGS_KEY, DimensionSettings::func_242746_i);

    private static <T> Registry<T> createRegistry(RegistryKey <? extends Registry<T >> registryKey, Supplier<T> defaultSupplier)
    {
        return createRegistry(registryKey, Lifecycle.stable(), defaultSupplier);
    }

    private static <T> Registry<T> createRegistry(RegistryKey <? extends Registry<T >> registryKey, Lifecycle lifecycle, Supplier<T> defaultSupplier)
    {
        return createRegistry(registryKey, new SimpleRegistry<>(registryKey, lifecycle), defaultSupplier, lifecycle);
    }

    private static <T, R extends MutableRegistry<T>> R createRegistry(RegistryKey <? extends Registry<T >> registryKey, R registry, Supplier<T> defaultSupplier, Lifecycle lifecycle)
    {
        ResourceLocation resourcelocation = registryKey.getLocation();
        REGISTRY_NAME_TO_DEFAULT.put(resourcelocation, defaultSupplier);
        MutableRegistry<R> mutableregistry = (MutableRegistry<R>)INTERNAL_ROOT_REGISTRIES;
        return (R)mutableregistry.register((RegistryKey)registryKey, registry, lifecycle);
    }

    public static <T> T register(Registry <? super T > registry, String id, T value)
    {
        return register(registry, new ResourceLocation(id), value);
    }

    public static <V, T extends V> T register(Registry<V> registry, ResourceLocation id, T value)
    {
        return ((MutableRegistry<V>)registry).register(RegistryKey.getOrCreateKey(registry.getRegistryKey(), id), value, Lifecycle.stable());
    }

    public static <V, T extends V> T register(Registry<V> registry, int index, RegistryKey<V> registryKey, T value)
    {
        return ((MutableRegistry<V>)registry).register(index, registryKey, value, Lifecycle.stable());
    }

    /**
     * Dummy method to ensure all static variables are loaded before Registry loads registries.
     */
    public static void init()
    {
    }

    static
    {
        REGISTRY_NAME_TO_DEFAULT.forEach((id, defaultSupplier) ->
        {
            if (defaultSupplier.get() == null)
            {
                LOGGER.error("Unable to bootstrap registry '{}'", (Object)id);
            }
        });
        Registry.validateMutableRegistry(INTERNAL_ROOT_REGISTRIES);
    }
}
