package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import java.util.OptionalInt;
import net.minecraft.util.RegistryKey;

public abstract class MutableRegistry<T> extends Registry<T>
{
    public MutableRegistry(RegistryKey <? extends Registry<T >> registryKey, Lifecycle lifecycle)
    {
        super(registryKey, lifecycle);
    }

    public abstract <V extends T> V register(int id, RegistryKey<T> name, V instance, Lifecycle lifecycle);

    public abstract <V extends T> V register(RegistryKey<T> name, V instance, Lifecycle lifecycle);

    public abstract <V extends T> V validateAndRegister(OptionalInt index, RegistryKey<T> registryKey, V value, Lifecycle lifecycle);
}
