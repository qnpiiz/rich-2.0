package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class RegistryKey<T>
{
    private static final Map < String, RegistryKey<? >> UNIVERSAL_KEY_MAP = Collections.synchronizedMap(Maps.newIdentityHashMap());
    private final ResourceLocation parent;
    private final ResourceLocation location;

    public static <T> RegistryKey<T> getOrCreateKey(RegistryKey <? extends Registry<T >> parent, ResourceLocation location)
    {
        return getOrCreateKey(parent.location, location);
    }

    public static <T> RegistryKey<Registry<T>> getOrCreateRootKey(ResourceLocation location)
    {
        return getOrCreateKey(Registry.ROOT, location);
    }

    private static <T> RegistryKey<T> getOrCreateKey(ResourceLocation parent, ResourceLocation location)
    {
        String s = (parent + ":" + location).intern();
        return (RegistryKey<T>)UNIVERSAL_KEY_MAP.computeIfAbsent(s, (concatKey) ->
        {
            return new RegistryKey(parent, location);
        });
    }

    private RegistryKey(ResourceLocation parent, ResourceLocation location)
    {
        this.parent = parent;
        this.location = location;
    }

    public String toString()
    {
        return "ResourceKey[" + this.parent + " / " + this.location + ']';
    }

    /**
     * Returns true if the registry represented by the parent key
     */
    public boolean isParent(RegistryKey <? extends Registry<? >> key)
    {
        return this.parent.equals(key.getLocation());
    }

    public ResourceLocation getLocation()
    {
        return this.location;
    }

    public static <T> Function<ResourceLocation, RegistryKey<T>> getKeyCreator(RegistryKey <? extends Registry<T >> parent)
    {
        return (location) ->
        {
            return getOrCreateKey(parent, location);
        };
    }
}
