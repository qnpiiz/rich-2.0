package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleRegistry<T> extends MutableRegistry<T>
{
    protected static final Logger LOGGER0 = LogManager.getLogger();
    private final ObjectList<T> entryList = new ObjectArrayList<>(256);
    private final Object2IntMap<T> entryIndexMap = new Object2IntOpenCustomHashMap<>(Util.identityHashStrategy());
    private final BiMap<ResourceLocation, T> registryObjects;
    private final BiMap<RegistryKey<T>, T> keyToObjectMap;
    private final Map<T, Lifecycle> objectToLifecycleMap;
    private Lifecycle lifecycle;
    protected Object[] values;
    private int nextFreeId;

    public SimpleRegistry(RegistryKey <? extends Registry<T >> registryKey, Lifecycle lifecycle)
    {
        super(registryKey, lifecycle);
        this.entryIndexMap.defaultReturnValue(-1);
        this.registryObjects = HashBiMap.create();
        this.keyToObjectMap = HashBiMap.create();
        this.objectToLifecycleMap = Maps.newIdentityHashMap();
        this.lifecycle = lifecycle;
    }

    public static <T> MapCodec<SimpleRegistry.Entry<T>> getRegistryEntryCodec(RegistryKey <? extends Registry<T >> registryKey, MapCodec<T> mapCodec)
    {
        return RecordCodecBuilder.mapCodec((builder) ->
        {
            return builder.group(ResourceLocation.CODEC.xmap(RegistryKey.getKeyCreator(registryKey), RegistryKey::getLocation).fieldOf("name").forGetter((entry) -> {
                return entry.name;
            }), Codec.INT.fieldOf("id").forGetter((entry) -> {
                return entry.index;
            }), mapCodec.forGetter((entry) -> {
                return entry.value;
            })).apply(builder, SimpleRegistry.Entry::new);
        });
    }

    public <V extends T> V register(int id, RegistryKey<T> name, V instance, Lifecycle lifecycle)
    {
        return this.register(id, name, instance, lifecycle, true);
    }

    private <V extends T> V register(int index, RegistryKey<T> registryKey, V value, Lifecycle lifecycle, boolean logDuplicateKeys)
    {
        Validate.notNull(registryKey);
        Validate.notNull((T)value);
        this.entryList.size(Math.max(this.entryList.size(), index + 1));
        this.entryList.set(index, value);
        this.entryIndexMap.put((T)value, index);
        this.values = null;

        if (logDuplicateKeys && this.keyToObjectMap.containsKey(registryKey))
        {
            LOGGER0.debug("Adding duplicate key '{}' to registry", (Object)registryKey);
        }

        if (this.registryObjects.containsValue(value))
        {
            LOGGER0.error("Adding duplicate value '{}' to registry", value);
        }

        this.registryObjects.put(registryKey.getLocation(), (T)value);
        this.keyToObjectMap.put(registryKey, (T)value);
        this.objectToLifecycleMap.put((T)value, lifecycle);
        this.lifecycle = this.lifecycle.add(lifecycle);

        if (this.nextFreeId <= index)
        {
            this.nextFreeId = index + 1;
        }

        return value;
    }

    public <V extends T> V register(RegistryKey<T> name, V instance, Lifecycle lifecycle)
    {
        return this.register(this.nextFreeId, name, instance, lifecycle);
    }

    public <V extends T> V validateAndRegister(OptionalInt index, RegistryKey<T> registryKey, V value, Lifecycle lifecycle)
    {
        Validate.notNull(registryKey);
        Validate.notNull((T)value);
        T t = this.keyToObjectMap.get(registryKey);
        int i;

        if (t == null)
        {
            i = index.isPresent() ? index.getAsInt() : this.nextFreeId;
        }
        else
        {
            i = this.entryIndexMap.getInt(t);

            if (index.isPresent() && index.getAsInt() != i)
            {
                throw new IllegalStateException("ID mismatch");
            }

            this.entryIndexMap.removeInt(t);
            this.objectToLifecycleMap.remove(t);
        }

        return this.register(i, registryKey, value, lifecycle, false);
    }

    @Nullable

    /**
     * Gets the name we use to identify the given object.
     */
    public ResourceLocation getKey(T value)
    {
        return this.registryObjects.inverse().get(value);
    }

    public Optional<RegistryKey<T>> getOptionalKey(T value)
    {
        return Optional.ofNullable(this.keyToObjectMap.inverse().get(value));
    }

    /**
     * Gets the integer ID we use to identify the given object.
     */
    public int getId(@Nullable T value)
    {
        return this.entryIndexMap.getInt(value);
    }

    @Nullable
    public T getValueForKey(@Nullable RegistryKey<T> key)
    {
        return this.keyToObjectMap.get(key);
    }

    @Nullable
    public T getByValue(int value)
    {
        return (T)(value >= 0 && value < this.entryList.size() ? this.entryList.get(value) : null);
    }

    public Lifecycle getLifecycleByRegistry(T object)
    {
        return this.objectToLifecycleMap.get(object);
    }

    public Lifecycle getLifecycle()
    {
        return this.lifecycle;
    }

    public Iterator<T> iterator()
    {
        return Iterators.filter(this.entryList.iterator(), Objects::nonNull);
    }

    @Nullable
    public T getOrDefault(@Nullable ResourceLocation name)
    {
        return this.registryObjects.get(name);
    }

    public Set<ResourceLocation> keySet()
    {
        return Collections.unmodifiableSet(this.registryObjects.keySet());
    }

    public Set<Map.Entry<RegistryKey<T>, T>> getEntries()
    {
        return Collections.unmodifiableMap(this.keyToObjectMap).entrySet();
    }

    @Nullable
    public T getRandom(Random random)
    {
        if (this.values == null)
        {
            Collection<?> collection = this.registryObjects.values();

            if (collection.isEmpty())
            {
                return (T)null;
            }

            this.values = collection.toArray(new Object[collection.size()]);
        }

        return Util.getRandomObject((T[])this.values, random);
    }

    public boolean containsKey(ResourceLocation name)
    {
        return this.registryObjects.containsKey(name);
    }

    public static <T> Codec<SimpleRegistry<T>> createSimpleRegistryCodec(RegistryKey <? extends Registry<T >> registryKey, Lifecycle lifecycle, Codec<T> codec)
    {
        return getRegistryEntryCodec(registryKey, codec.fieldOf("element")).codec().listOf().xmap((entries) ->
        {
            SimpleRegistry<T> simpleregistry = new SimpleRegistry<>(registryKey, lifecycle);

            for (SimpleRegistry.Entry<T> entry : entries)
            {
                simpleregistry.register(entry.index, entry.name, entry.value, lifecycle);
            }

            return simpleregistry;
        }, (registry) ->
        {
            Builder<SimpleRegistry.Entry<T>> builder = ImmutableList.builder();

            for (T t : registry)
            {
                builder.add(new SimpleRegistry.Entry<>(registry.getOptionalKey(t).get(), registry.getId(t), t));
            }

            return builder.build();
        });
    }

    public static <T> Codec<SimpleRegistry<T>> getSimpleRegistryCodec(RegistryKey <? extends Registry<T >> registryKey, Lifecycle lifecycle, Codec<T> mapCodec)
    {
        return SimpleRegistryCodec.create(registryKey, lifecycle, mapCodec);
    }

    public static <T> Codec<SimpleRegistry<T>> getUnboundedRegistryCodec(RegistryKey <? extends Registry<T >> registryKey, Lifecycle lifecycle, Codec<T> mapCodec)
    {
        return Codec.unboundedMap(ResourceLocation.CODEC.xmap(RegistryKey.getKeyCreator(registryKey), RegistryKey::getLocation), mapCodec).xmap((registryMap) ->
        {
            SimpleRegistry<T> simpleregistry = new SimpleRegistry<>(registryKey, lifecycle);
            registryMap.forEach((key, value) -> {
                simpleregistry.register(key, value, lifecycle);
            });
            return simpleregistry;
        }, (registry) ->
        {
            return ImmutableMap.copyOf(registry.keyToObjectMap);
        });
    }

    public static class Entry<T>
    {
        public final RegistryKey<T> name;
        public final int index;
        public final T value;

        public Entry(RegistryKey<T> name, int index, T value)
        {
            this.name = name;
            this.index = index;
            this.value = value;
        }
    }
}
