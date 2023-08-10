package net.minecraft.util.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DelegatingDynamicOps;

public class WorldGenSettingsExport<T> extends DelegatingDynamicOps<T>
{
    private final DynamicRegistries dynamicRegistries;

    public static <T> WorldGenSettingsExport<T> create(DynamicOps<T> ops, DynamicRegistries dynamicRegistries)
    {
        return new WorldGenSettingsExport<>(ops, dynamicRegistries);
    }

    private WorldGenSettingsExport(DynamicOps<T> ops, DynamicRegistries dynamicRegistries)
    {
        super(ops);
        this.dynamicRegistries = dynamicRegistries;
    }

    protected <E> DataResult<T> encode(E instance, T prefix, RegistryKey <? extends Registry<E >> registryKey, Codec<E> mapCodec)
    {
        Optional<MutableRegistry<E>> optional = this.dynamicRegistries.func_230521_a_(registryKey);

        if (optional.isPresent())
        {
            MutableRegistry<E> mutableregistry = optional.get();
            Optional<RegistryKey<E>> optional1 = mutableregistry.getOptionalKey(instance);

            if (optional1.isPresent())
            {
                RegistryKey<E> registrykey = optional1.get();
                return ResourceLocation.CODEC.encode(registrykey.getLocation(), this.ops, prefix);
            }
        }

        return mapCodec.encode(instance, this, prefix);
    }
}
