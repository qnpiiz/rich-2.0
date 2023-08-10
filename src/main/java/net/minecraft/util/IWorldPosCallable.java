package net.minecraft.util;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWorldPosCallable
{
    IWorldPosCallable DUMMY = new IWorldPosCallable()
    {
        public <T> Optional<T> apply(BiFunction<World, BlockPos, T> worldPosConsumer)
        {
            return Optional.empty();
        }
    };

    static IWorldPosCallable of(final World world, final BlockPos pos)
    {
        return new IWorldPosCallable()
        {
            public <T> Optional<T> apply(BiFunction<World, BlockPos, T> worldPosConsumer)
            {
                return Optional.of(worldPosConsumer.apply(world, pos));
            }
        };
    }

    <T> Optional<T> apply(BiFunction<World, BlockPos, T> worldPosConsumer);

default <T> T applyOrElse(BiFunction<World, BlockPos, T> worldPosConsumer, T defaultValue)
    {
        return this.apply(worldPosConsumer).orElse(defaultValue);
    }

default void consume(BiConsumer<World, BlockPos> worldPosConsumer)
    {
        this.apply((world, pos) ->
        {
            worldPosConsumer.accept(world, pos);
            return Optional.empty();
        });
    }
}
