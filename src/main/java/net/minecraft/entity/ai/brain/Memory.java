package net.minecraft.entity.ai.brain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public class Memory<T>
{
    private final T value;
    private long timeToLive;

    public Memory(T value, long timeToLive)
    {
        this.value = value;
        this.timeToLive = timeToLive;
    }

    public void tick()
    {
        if (this.isForgettable())
        {
            --this.timeToLive;
        }
    }

    public static <T> Memory<T> create(T value)
    {
        return new Memory<>(value, Long.MAX_VALUE);
    }

    public static <T> Memory<T> create(T value, long timeToLive)
    {
        return new Memory<>(value, timeToLive);
    }

    public T getValue()
    {
        return this.value;
    }

    public boolean isForgotten()
    {
        return this.timeToLive <= 0L;
    }

    public String toString()
    {
        return this.value.toString() + (this.isForgettable() ? " (ttl: " + this.timeToLive + ")" : "");
    }

    public boolean isForgettable()
    {
        return this.timeToLive != Long.MAX_VALUE;
    }

    public static <T> Codec<Memory<T>> createCodec(Codec<T> valueCodec)
    {
        return RecordCodecBuilder.create((builder) ->
        {
            return builder.group(valueCodec.fieldOf("value").forGetter((memory) -> {
                return memory.value;
            }), Codec.LONG.optionalFieldOf("ttl").forGetter((memory) -> {
                return memory.isForgettable() ? Optional.of(memory.timeToLive) : Optional.empty();
            })).apply(builder, (value, timeToLive) -> {
                return new Memory<>(value, timeToLive.orElse(Long.MAX_VALUE));
            });
        });
    }
}
