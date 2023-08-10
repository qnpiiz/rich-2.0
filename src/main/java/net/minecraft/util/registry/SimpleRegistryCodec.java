package net.minecraft.util.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.util.RegistryKey;

public final class SimpleRegistryCodec<E> implements Codec<SimpleRegistry<E>>
{
    private final Codec<SimpleRegistry<E>> registryCodec;
    private final RegistryKey <? extends Registry<E >> registryKey;
    private final Codec<E> rawCodec;

    public static <E> SimpleRegistryCodec<E> create(RegistryKey <? extends Registry<E >> registryKey, Lifecycle lifecycle, Codec<E> rawCodec)
    {
        return new SimpleRegistryCodec<>(registryKey, lifecycle, rawCodec);
    }

    private SimpleRegistryCodec(RegistryKey <? extends Registry<E >> registryKey, Lifecycle lifecycle, Codec<E> rawCodec)
    {
        this.registryCodec = SimpleRegistry.getUnboundedRegistryCodec(registryKey, lifecycle, rawCodec);
        this.registryKey = registryKey;
        this.rawCodec = rawCodec;
    }

    public <T> DataResult<T> encode(SimpleRegistry<E> p_encode_1_, DynamicOps<T> p_encode_2_, T p_encode_3_)
    {
        return this.registryCodec.encode(p_encode_1_, p_encode_2_, p_encode_3_);
    }

    public <T> DataResult<Pair<SimpleRegistry<E>, T>> decode(DynamicOps<T> p_decode_1_, T p_decode_2_)
    {
        DataResult<Pair<SimpleRegistry<E>, T>> dataresult = this.registryCodec.decode(p_decode_1_, p_decode_2_);
        return p_decode_1_ instanceof WorldSettingsImport ? dataresult.flatMap((registryPair) ->
        {
            return ((WorldSettingsImport)p_decode_1_).decode(registryPair.getFirst(), this.registryKey, this.rawCodec).map((registry) -> {
                return Pair.of(registry, (T)registryPair.getSecond());
            });
        }) : dataresult;
    }

    public String toString()
    {
        return "RegistryDataPackCodec[" + this.registryCodec + " " + this.registryKey + " " + this.rawCodec + "]";
    }
}
