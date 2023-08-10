package net.minecraft.util.registry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.stream.Stream;
import net.minecraft.util.RegistryKey;

public final class RegistryLookupCodec<E> extends MapCodec<Registry<E>>
{
    private final RegistryKey <? extends Registry<E >> registryKey;

    public static <E> RegistryLookupCodec<E> getLookUpCodec(RegistryKey <? extends Registry<E >> registryKey)
    {
        return new RegistryLookupCodec<>(registryKey);
    }

    private RegistryLookupCodec(RegistryKey <? extends Registry<E >> registryKey)
    {
        this.registryKey = registryKey;
    }

    public <T> RecordBuilder<T> encode(Registry<E> p_encode_1_, DynamicOps<T> p_encode_2_, RecordBuilder<T> p_encode_3_)
    {
        return p_encode_3_;
    }

    public <T> DataResult<Registry<E>> decode(DynamicOps<T> p_decode_1_, MapLike<T> p_decode_2_)
    {
        return p_decode_1_ instanceof WorldSettingsImport ? ((WorldSettingsImport)p_decode_1_).getRegistryByKey(this.registryKey) : DataResult.error("Not a registry ops");
    }

    public String toString()
    {
        return "RegistryLookupCodec[" + this.registryKey + "]";
    }

    public <T> Stream<T> keys(DynamicOps<T> p_keys_1_)
    {
        return Stream.empty();
    }
}
