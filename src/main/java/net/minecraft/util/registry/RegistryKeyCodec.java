package net.minecraft.util.registry;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.RegistryKey;

public final class RegistryKeyCodec<E> implements Codec<Supplier<E>>
{
    private final RegistryKey <? extends Registry<E >> registryKey;
    private final Codec<E> registryCodec;
    private final boolean allowInlineDefinitions;

    public static <E> RegistryKeyCodec<E> create(RegistryKey <? extends Registry<E >> registryKey, Codec<E> codec)
    {
        return create(registryKey, codec, true);
    }

    public static <E> Codec<List<Supplier<E>>> getValueCodecs(RegistryKey <? extends Registry<E >> registryKey, Codec<E> registryKeyCodec)
    {
        return Codec.either(create(registryKey, registryKeyCodec, false).listOf(), registryKeyCodec.<Supplier<E>>xmap((value) ->
        {
            return () -> {
                return value;
            };
        }, Supplier::get).listOf()).xmap((either) ->
        {
            return either.map((left) -> {
                return left;
            }, (right) -> {
                return right;
            });
        }, Either::left);
    }

    private static <E> RegistryKeyCodec<E> create(RegistryKey <? extends Registry<E >> registryKey, Codec<E> registryKeyCodec, boolean allowInlineDefinitions)
    {
        return new RegistryKeyCodec<>(registryKey, registryKeyCodec, allowInlineDefinitions);
    }

    private RegistryKeyCodec(RegistryKey <? extends Registry<E >> registryKey, Codec<E> registryKeyCodec, boolean allowInlineDefinitions)
    {
        this.registryKey = registryKey;
        this.registryCodec = registryKeyCodec;
        this.allowInlineDefinitions = allowInlineDefinitions;
    }

    public <T> DataResult<T> encode(Supplier<E> p_encode_1_, DynamicOps<T> p_encode_2_, T p_encode_3_)
    {
        return p_encode_2_ instanceof WorldGenSettingsExport ? ((WorldGenSettingsExport)p_encode_2_).encode(p_encode_1_.get(), p_encode_3_, this.registryKey, this.registryCodec) : this.registryCodec.encode(p_encode_1_.get(), p_encode_2_, p_encode_3_);
    }

    public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> p_decode_1_, T p_decode_2_)
    {
        return p_decode_1_ instanceof WorldSettingsImport ? ((WorldSettingsImport)p_decode_1_).decode(p_decode_2_, this.registryKey, this.registryCodec, this.allowInlineDefinitions) : this.registryCodec.decode(p_decode_1_, p_decode_2_).map((elementPair) ->
        {
            return elementPair.mapFirst((element) -> {
                return () -> {
                    return element;
                };
            });
        });
    }

    public String toString()
    {
        return "RegistryFileCodec[" + this.registryKey + " " + this.registryCodec + "]";
    }
}
