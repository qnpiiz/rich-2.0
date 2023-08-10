package net.minecraft.util;

import javax.annotation.Nullable;

public interface IObjectIntIterable<T> extends Iterable<T>
{
    /**
     * Gets the integer ID we use to identify the given object.
     */
    int getId(T value);

    @Nullable
    T getByValue(int value);
}
