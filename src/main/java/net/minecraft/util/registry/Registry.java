package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootEntryManager;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootPoolEntryType;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureSizeType;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.IPosRuleTests;
import net.minecraft.world.gen.feature.template.IRuleTestType;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Registry<T> implements Codec<T>, Keyable, IObjectIntIterable<T>
{
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Map < ResourceLocation, Supplier<? >> LOCATION_TO_SUPPLIER = Maps.newLinkedHashMap();
    public static final ResourceLocation ROOT = new ResourceLocation("root");
    protected static final MutableRegistry < MutableRegistry<? >> ROOT_REGISTRY = new SimpleRegistry<>(createKey("root"), Lifecycle.experimental());
    public static final Registry <? extends Registry<? >> REGISTRY = ROOT_REGISTRY;
    public static final RegistryKey<Registry<SoundEvent>> SOUND_EVENT_KEY = createKey("sound_event");
    public static final RegistryKey<Registry<Fluid>> FLUID_KEY = createKey("fluid");
    public static final RegistryKey<Registry<Effect>> MOB_EFFECT_KEY = createKey("mob_effect");
    public static final RegistryKey<Registry<Block>> BLOCK_KEY = createKey("block");
    public static final RegistryKey<Registry<Enchantment>> ENCHANTMENT_KEY = createKey("enchantment");
    public static final RegistryKey < Registry < EntityType<? >>> ENTITY_TYPE_KEY = createKey("entity_type");
    public static final RegistryKey<Registry<Item>> ITEM_KEY = createKey("item");
    public static final RegistryKey<Registry<Potion>> POTION_KEY = createKey("potion");
    public static final RegistryKey < Registry < ParticleType<? >>> PARTICLE_TYPE_KEY = createKey("particle_type");
    public static final RegistryKey < Registry < TileEntityType<? >>> BLOCK_ENTITY_TYPE_KEY = createKey("block_entity_type");
    public static final RegistryKey<Registry<PaintingType>> MOTIVE_KEY = createKey("motive");
    public static final RegistryKey<Registry<ResourceLocation>> CUSTOM_STAT_KEY = createKey("custom_stat");
    public static final RegistryKey<Registry<ChunkStatus>> CHUNK_STATUS_KEY = createKey("chunk_status");
    public static final RegistryKey < Registry < IRuleTestType<? >>> RULE_TEST_KEY = createKey("rule_test");
    public static final RegistryKey < Registry < IPosRuleTests<? >>> POS_RULE_TEST_KEY = createKey("pos_rule_test");
    public static final RegistryKey < Registry < ContainerType<? >>> MENU_KEY = createKey("menu");
    public static final RegistryKey < Registry < IRecipeType<? >>> RECIPE_TYPE_KEY = createKey("recipe_type");
    public static final RegistryKey < Registry < IRecipeSerializer<? >>> RECIPE_SERIALIZER_KEY = createKey("recipe_serializer");
    public static final RegistryKey<Registry<Attribute>> ATTRIBUTE_KEY = createKey("attribute");
    public static final RegistryKey < Registry < StatType<? >>> STAT_TYPE_KEY = createKey("stat_type");
    public static final RegistryKey<Registry<VillagerType>> VILLAGER_TYPE_KEY = createKey("villager_type");
    public static final RegistryKey<Registry<VillagerProfession>> VILLAGER_PROFESSION_KEY = createKey("villager_profession");
    public static final RegistryKey<Registry<PointOfInterestType>> POINT_OF_INTEREST_TYPE_KEY = createKey("point_of_interest_type");
    public static final RegistryKey < Registry < MemoryModuleType<? >>> MEMORY_MODULE_TYPE_KEY = createKey("memory_module_type");
    public static final RegistryKey < Registry < SensorType<? >>> SENSOR_TYPE_KEY = createKey("sensor_type");
    public static final RegistryKey<Registry<Schedule>> SCHEDULE_KEY = createKey("schedule");
    public static final RegistryKey<Registry<Activity>> ACTIVITY_KEY = createKey("activity");
    public static final RegistryKey<Registry<LootPoolEntryType>> LOOT_POOL_ENTRY_TYPE_KEY = createKey("loot_pool_entry_type");
    public static final RegistryKey<Registry<LootFunctionType>> LOOT_FUNCTION_TYPE_KEY = createKey("loot_function_type");
    public static final RegistryKey<Registry<LootConditionType>> LOOT_CONDITION_TYPE_KEY = createKey("loot_condition_type");
    public static final RegistryKey<Registry<DimensionType>> DIMENSION_TYPE_KEY = createKey("dimension_type");
    public static final RegistryKey<Registry<World>> WORLD_KEY = createKey("dimension");
    public static final RegistryKey<Registry<Dimension>> DIMENSION_KEY = createKey("dimension");
    public static final Registry<SoundEvent> SOUND_EVENT = createRegistry(SOUND_EVENT_KEY, () ->
    {
        return SoundEvents.ENTITY_ITEM_PICKUP;
    });
    public static final DefaultedRegistry<Fluid> FLUID = registerDefaulted(FLUID_KEY, "empty", () ->
    {
        return Fluids.EMPTY;
    });
    public static final Registry<Effect> EFFECTS = createRegistry(MOB_EFFECT_KEY, () ->
    {
        return Effects.LUCK;
    });
    public static final DefaultedRegistry<Block> BLOCK = registerDefaulted(BLOCK_KEY, "air", () ->
    {
        return Blocks.AIR;
    });
    public static final Registry<Enchantment> ENCHANTMENT = createRegistry(ENCHANTMENT_KEY, () ->
    {
        return Enchantments.FORTUNE;
    });
    public static final DefaultedRegistry < EntityType<? >> ENTITY_TYPE = registerDefaulted(ENTITY_TYPE_KEY, "pig", () ->
    {
        return EntityType.PIG;
    });
    public static final DefaultedRegistry<Item> ITEM = registerDefaulted(ITEM_KEY, "air", () ->
    {
        return Items.AIR;
    });
    public static final DefaultedRegistry<Potion> POTION = registerDefaulted(POTION_KEY, "empty", () ->
    {
        return Potions.EMPTY;
    });
    public static final Registry < ParticleType<? >> PARTICLE_TYPE = createRegistry(PARTICLE_TYPE_KEY, () ->
    {
        return ParticleTypes.BLOCK;
    });
    public static final Registry < TileEntityType<? >> BLOCK_ENTITY_TYPE = createRegistry(BLOCK_ENTITY_TYPE_KEY, () ->
    {
        return TileEntityType.FURNACE;
    });
    public static final DefaultedRegistry<PaintingType> MOTIVE = registerDefaulted(MOTIVE_KEY, "kebab", () ->
    {
        return PaintingType.KEBAB;
    });
    public static final Registry<ResourceLocation> CUSTOM_STAT = createRegistry(CUSTOM_STAT_KEY, () ->
    {
        return Stats.JUMP;
    });
    public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = registerDefaulted(CHUNK_STATUS_KEY, "empty", () ->
    {
        return ChunkStatus.EMPTY;
    });
    public static final Registry < IRuleTestType<? >> RULE_TEST = createRegistry(RULE_TEST_KEY, () ->
    {
        return IRuleTestType.ALWAYS_TRUE;
    });
    public static final Registry < IPosRuleTests<? >> POS_RULE_TEST = createRegistry(POS_RULE_TEST_KEY, () ->
    {
        return IPosRuleTests.field_237103_a_;
    });
    public static final Registry < ContainerType<? >> MENU = createRegistry(MENU_KEY, () ->
    {
        return ContainerType.ANVIL;
    });
    public static final Registry < IRecipeType<? >> RECIPE_TYPE = createRegistry(RECIPE_TYPE_KEY, () ->
    {
        return IRecipeType.CRAFTING;
    });
    public static final Registry < IRecipeSerializer<? >> RECIPE_SERIALIZER = createRegistry(RECIPE_SERIALIZER_KEY, () ->
    {
        return IRecipeSerializer.CRAFTING_SHAPELESS;
    });
    public static final Registry<Attribute> ATTRIBUTE = createRegistry(ATTRIBUTE_KEY, () ->
    {
        return Attributes.LUCK;
    });
    public static final Registry < StatType<? >> STATS = createRegistry(STAT_TYPE_KEY, () ->
    {
        return Stats.ITEM_USED;
    });
    public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = registerDefaulted(VILLAGER_TYPE_KEY, "plains", () ->
    {
        return VillagerType.PLAINS;
    });
    public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = registerDefaulted(VILLAGER_PROFESSION_KEY, "none", () ->
    {
        return VillagerProfession.NONE;
    });
    public static final DefaultedRegistry<PointOfInterestType> POINT_OF_INTEREST_TYPE = registerDefaulted(POINT_OF_INTEREST_TYPE_KEY, "unemployed", () ->
    {
        return PointOfInterestType.UNEMPLOYED;
    });
    public static final DefaultedRegistry < MemoryModuleType<? >> MEMORY_MODULE_TYPE = registerDefaulted(MEMORY_MODULE_TYPE_KEY, "dummy", () ->
    {
        return MemoryModuleType.DUMMY;
    });
    public static final DefaultedRegistry < SensorType<? >> SENSOR_TYPE = registerDefaulted(SENSOR_TYPE_KEY, "dummy", () ->
    {
        return SensorType.DUMMY;
    });
    public static final Registry<Schedule> SCHEDULE = createRegistry(SCHEDULE_KEY, () ->
    {
        return Schedule.EMPTY;
    });
    public static final Registry<Activity> ACTIVITY = createRegistry(ACTIVITY_KEY, () ->
    {
        return Activity.IDLE;
    });
    public static final Registry<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = createRegistry(LOOT_POOL_ENTRY_TYPE_KEY, () ->
    {
        return LootEntryManager.EMPTY;
    });
    public static final Registry<LootFunctionType> LOOT_FUNCTION_TYPE = createRegistry(LOOT_FUNCTION_TYPE_KEY, () ->
    {
        return LootFunctionManager.SET_COUNT;
    });
    public static final Registry<LootConditionType> LOOT_CONDITION_TYPE = createRegistry(LOOT_CONDITION_TYPE_KEY, () ->
    {
        return LootConditionManager.INVERTED;
    });
    public static final RegistryKey<Registry<DimensionSettings>> NOISE_SETTINGS_KEY = createKey("worldgen/noise_settings");
    public static final RegistryKey < Registry < ConfiguredSurfaceBuilder<? >>> CONFIGURED_SURFACE_BUILDER_KEY = createKey("worldgen/configured_surface_builder");
    public static final RegistryKey < Registry < ConfiguredCarver<? >>> CONFIGURED_CARVER_KEY = createKey("worldgen/configured_carver");
    public static final RegistryKey < Registry < ConfiguredFeature <? , ? >>> CONFIGURED_FEATURE_KEY = createKey("worldgen/configured_feature");
    public static final RegistryKey < Registry < StructureFeature <? , ? >>> CONFIGURED_STRUCTURE_FEATURE_KEY = createKey("worldgen/configured_structure_feature");
    public static final RegistryKey<Registry<StructureProcessorList>> STRUCTURE_PROCESSOR_LIST_KEY = createKey("worldgen/processor_list");
    public static final RegistryKey<Registry<JigsawPattern>> JIGSAW_POOL_KEY = createKey("worldgen/template_pool");
    public static final RegistryKey<Registry<Biome>> BIOME_KEY = createKey("worldgen/biome");
    public static final RegistryKey < Registry < SurfaceBuilder<? >>> SURFACE_BUILDER_KEY = createKey("worldgen/surface_builder");
    public static final Registry < SurfaceBuilder<? >> SURFACE_BUILDER = createRegistry(SURFACE_BUILDER_KEY, () ->
    {
        return SurfaceBuilder.DEFAULT;
    });
    public static final RegistryKey < Registry < WorldCarver<? >>> CARVER_KEY = createKey("worldgen/carver");
    public static final Registry < WorldCarver<? >> CARVER = createRegistry(CARVER_KEY, () ->
    {
        return WorldCarver.CAVE;
    });
    public static final RegistryKey < Registry < Feature<? >>> FEATURE_KEY = createKey("worldgen/feature");
    public static final Registry < Feature<? >> FEATURE = createRegistry(FEATURE_KEY, () ->
    {
        return Feature.ORE;
    });
    public static final RegistryKey < Registry < Structure<? >>> STRUCTURE_FEATURE_KEY = createKey("worldgen/structure_feature");
    public static final Registry < Structure<? >> STRUCTURE_FEATURE = createRegistry(STRUCTURE_FEATURE_KEY, () ->
    {
        return Structure.field_236367_c_;
    });
    public static final RegistryKey<Registry<IStructurePieceType>> STRUCTURE_PIECE_KEY = createKey("worldgen/structure_piece");
    public static final Registry<IStructurePieceType> STRUCTURE_PIECE = createRegistry(STRUCTURE_PIECE_KEY, () ->
    {
        return IStructurePieceType.MSROOM;
    });
    public static final RegistryKey < Registry < Placement<? >>> DECORATOR_KEY = createKey("worldgen/decorator");
    public static final Registry < Placement<? >> DECORATOR = createRegistry(DECORATOR_KEY, () ->
    {
        return Placement.NOPE;
    });
    public static final RegistryKey < Registry < BlockStateProviderType<? >>> BLOCK_STATE_PROVIDER_TYPE_KEY = createKey("worldgen/block_state_provider_type");
    public static final RegistryKey < Registry < BlockPlacerType<? >>> BLOCK_PLACER_TYPE_KEY = createKey("worldgen/block_placer_type");
    public static final RegistryKey < Registry < FoliagePlacerType<? >>> FOLIAGE_PLACER_TYPE_KEY = createKey("worldgen/foliage_placer_type");
    public static final RegistryKey < Registry < TrunkPlacerType<? >>> TRUNK_PLACER_TYPE_KEY = createKey("worldgen/trunk_placer_type");
    public static final RegistryKey < Registry < TreeDecoratorType<? >>> TREE_DECORATOR_TYPE_KEY = createKey("worldgen/tree_decorator_type");
    public static final RegistryKey < Registry < FeatureSizeType<? >>> FEATURE_SIZE_TYPE_KEY = createKey("worldgen/feature_size_type");
    public static final RegistryKey < Registry < Codec <? extends BiomeProvider >>> BIOME_SOURCE_KEY = createKey("worldgen/biome_source");
    public static final RegistryKey < Registry < Codec <? extends ChunkGenerator >>> CHUNK_GENERATOR_KEY = createKey("worldgen/chunk_generator");
    public static final RegistryKey < Registry < IStructureProcessorType<? >>> STRUCTURE_PROCESSOR_KEY = createKey("worldgen/structure_processor");
    public static final RegistryKey < Registry < IJigsawDeserializer<? >>> STRUCTURE_POOL_ELEMENT_KEY = createKey("worldgen/structure_pool_element");
    public static final Registry < BlockStateProviderType<? >> BLOCK_STATE_PROVIDER_TYPE = createRegistry(BLOCK_STATE_PROVIDER_TYPE_KEY, () ->
    {
        return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
    });
    public static final Registry < BlockPlacerType<? >> BLOCK_PLACER_TYPE = createRegistry(BLOCK_PLACER_TYPE_KEY, () ->
    {
        return BlockPlacerType.SIMPLE_BLOCK;
    });
    public static final Registry < FoliagePlacerType<? >> FOLIAGE_PLACER_TYPE = createRegistry(FOLIAGE_PLACER_TYPE_KEY, () ->
    {
        return FoliagePlacerType.BLOB;
    });
    public static final Registry < TrunkPlacerType<? >> TRUNK_REPLACER = createRegistry(TRUNK_PLACER_TYPE_KEY, () ->
    {
        return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
    });
    public static final Registry < TreeDecoratorType<? >> TREE_DECORATOR_TYPE = createRegistry(TREE_DECORATOR_TYPE_KEY, () ->
    {
        return TreeDecoratorType.LEAVE_VINE;
    });
    public static final Registry < FeatureSizeType<? >> FEATURE_SIZE_TYPE = createRegistry(FEATURE_SIZE_TYPE_KEY, () ->
    {
        return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
    });
    public static final Registry < Codec <? extends BiomeProvider >> BIOME_PROVIDER_CODEC = register(BIOME_SOURCE_KEY, Lifecycle.stable(), () ->
    {
        return BiomeProvider.CODEC;
    });
    public static final Registry < Codec <? extends ChunkGenerator >> CHUNK_GENERATOR_CODEC = register(CHUNK_GENERATOR_KEY, Lifecycle.stable(), () ->
    {
        return ChunkGenerator.field_235948_a_;
    });
    public static final Registry < IStructureProcessorType<? >> STRUCTURE_PROCESSOR = createRegistry(STRUCTURE_PROCESSOR_KEY, () ->
    {
        return IStructureProcessorType.BLOCK_IGNORE;
    });
    public static final Registry < IJigsawDeserializer<? >> STRUCTURE_POOL_ELEMENT = createRegistry(STRUCTURE_POOL_ELEMENT_KEY, () ->
    {
        return IJigsawDeserializer.EMPTY_POOL_ELEMENT;
    });
    private final RegistryKey <? extends Registry<T >> registryKey;
    private final Lifecycle lifecycle;

    private static <T> RegistryKey<Registry<T>> createKey(String name)
    {
        return RegistryKey.getOrCreateRootKey(new ResourceLocation(name));
    }

    public static < T extends MutableRegistry<? >> void validateMutableRegistry(MutableRegistry<T> registry)
    {
        registry.forEach((registryElement) ->
        {
            if (registryElement.keySet().isEmpty())
            {
                LOGGER.error("Registry '{}' was empty after loading", (Object)registry.getKey(registryElement));

                if (SharedConstants.developmentMode)
                {
                    throw new IllegalStateException("Registry: '" + registry.getKey(registryElement) + "' is empty, not allowed, fix me!");
                }
            }

            if (registryElement instanceof DefaultedRegistry)
            {
                ResourceLocation resourcelocation = ((DefaultedRegistry)registryElement).getDefaultKey();
                Validate.notNull(registryElement.getOrDefault(resourcelocation), "Missing default of DefaultedMappedRegistry: " + resourcelocation);
            }
        });
    }

    private static <T> Registry<T> createRegistry(RegistryKey <? extends Registry<T >> registryKey, Supplier<T> supplier)
    {
        return register(registryKey, Lifecycle.experimental(), supplier);
    }

    private static <T> DefaultedRegistry<T> registerDefaulted(RegistryKey <? extends Registry<T >> registryKey, String defaultedValueKey, Supplier<T> supplier)
    {
        return registerDefaulted(registryKey, defaultedValueKey, Lifecycle.experimental(), supplier);
    }

    private static <T> Registry<T> register(RegistryKey <? extends Registry<T >> registryKey, Lifecycle lifecycle, Supplier<T> supplier)
    {
        return addRegistry(registryKey, new SimpleRegistry<>(registryKey, lifecycle), supplier, lifecycle);
    }

    private static <T> DefaultedRegistry<T> registerDefaulted(RegistryKey <? extends Registry<T >> registryKey, String defaultedValueKey, Lifecycle lifecycle, Supplier<T> supplier)
    {
        return addRegistry(registryKey, new DefaultedRegistry<>(defaultedValueKey, registryKey, lifecycle), supplier, lifecycle);
    }

    private static <T, R extends MutableRegistry<T>> R addRegistry(RegistryKey <? extends Registry<T >> registryKey, R instance, Supplier<T> objectSupplier, Lifecycle lifecycle)
    {
        ResourceLocation resourcelocation = registryKey.getLocation();
        LOCATION_TO_SUPPLIER.put(resourcelocation, objectSupplier);
        MutableRegistry<R> mutableregistry = (MutableRegistry<R>)ROOT_REGISTRY;
        return (R)mutableregistry.register((RegistryKey)registryKey, instance, lifecycle);
    }

    protected Registry(RegistryKey <? extends Registry<T >> registryKey, Lifecycle lifecycle)
    {
        this.registryKey = registryKey;
        this.lifecycle = lifecycle;
    }

    public RegistryKey <? extends Registry<T >> getRegistryKey()
    {
        return this.registryKey;
    }

    public String toString()
    {
        return "Registry[" + this.registryKey + " (" + this.lifecycle + ")]";
    }

    public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> p_decode_1_, U p_decode_2_)
    {
        return p_decode_1_.compressMaps() ? p_decode_1_.getNumberValue(p_decode_2_).flatMap((registryId) ->
        {
            T t = this.getByValue(registryId.intValue());
            return t == null ? DataResult.error("Unknown registry id: " + registryId) : DataResult.success(t, this.getLifecycleByRegistry(t));
        }).map((encodedValue) ->
        {
            return Pair.of((T)encodedValue, p_decode_1_.empty());
        }) : ResourceLocation.CODEC.decode(p_decode_1_, p_decode_2_).flatMap((encodedRegistryPair) ->
        {
            T t = this.getOrDefault(encodedRegistryPair.getFirst());
            return t == null ? DataResult.error("Unknown registry key: " + encodedRegistryPair.getFirst()) : DataResult.success(Pair.of(t, encodedRegistryPair.getSecond()), this.getLifecycleByRegistry(t));
        });
    }

    public <U> DataResult<U> encode(T p_encode_1_, DynamicOps<U> p_encode_2_, U p_encode_3_)
    {
        ResourceLocation resourcelocation = this.getKey(p_encode_1_);

        if (resourcelocation == null)
        {
            return DataResult.error("Unknown registry element " + p_encode_1_);
        }
        else
        {
            return p_encode_2_.compressMaps() ? p_encode_2_.mergeToPrimitive(p_encode_3_, p_encode_2_.createInt(this.getId(p_encode_1_))).setLifecycle(this.lifecycle) : p_encode_2_.mergeToPrimitive(p_encode_3_, p_encode_2_.createString(resourcelocation.toString())).setLifecycle(this.lifecycle);
        }
    }

    public <U> Stream<U> keys(DynamicOps<U> p_keys_1_)
    {
        return this.keySet().stream().map((registryID) ->
        {
            return p_keys_1_.createString(registryID.toString());
        });
    }

    @Nullable

    /**
     * Gets the name we use to identify the given object.
     */
    public abstract ResourceLocation getKey(T value);

    public abstract Optional<RegistryKey<T>> getOptionalKey(T value);

    /**
     * Gets the integer ID we use to identify the given object.
     */
    public abstract int getId(@Nullable T value);

    @Nullable
    public abstract T getValueForKey(@Nullable RegistryKey<T> key);

    @Nullable
    public abstract T getOrDefault(@Nullable ResourceLocation name);

    protected abstract Lifecycle getLifecycleByRegistry(T object);

    public abstract Lifecycle getLifecycle();

    public Optional<T> getOptional(@Nullable ResourceLocation id)
    {
        return Optional.ofNullable(this.getOrDefault(id));
    }

    public Optional<T> getOptionalValue(@Nullable RegistryKey<T> registryKey)
    {
        return Optional.ofNullable(this.getValueForKey(registryKey));
    }

    public T getOrThrow(RegistryKey<T> key)
    {
        T t = this.getValueForKey(key);

        if (t == null)
        {
            throw new IllegalStateException("Missing: " + key);
        }
        else
        {
            return t;
        }
    }

    public abstract Set<ResourceLocation> keySet();

    public abstract Set<Entry<RegistryKey<T>, T>> getEntries();

    public Stream<T> stream()
    {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public abstract boolean containsKey(ResourceLocation name);

    public static <T> T register(Registry <? super T > registry, String identifier, T value)
    {
        return register(registry, new ResourceLocation(identifier), value);
    }

    public static <V, T extends V> T register(Registry<V> registry, ResourceLocation identifier, T value)
    {
        return ((MutableRegistry<V>)registry).register(RegistryKey.getOrCreateKey(registry.registryKey, identifier), value, Lifecycle.stable());
    }

    public static <V, T extends V> T register(Registry<V> registry, int id, String identifier, T value)
    {
        return ((MutableRegistry<V>)registry).register(id, RegistryKey.getOrCreateKey(registry.registryKey, new ResourceLocation(identifier)), value, Lifecycle.stable());
    }

    static
    {
        WorldGenRegistries.init();
        LOCATION_TO_SUPPLIER.forEach((registry, registrySupplier) ->
        {
            if (registrySupplier.get() == null)
            {
                LOGGER.error("Unable to bootstrap registry '{}'", (Object)registry);
            }
        });
        validateMutableRegistry(ROOT_REGISTRY);
    }
}
