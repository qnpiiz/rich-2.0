package net.minecraft.server.management;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public abstract class UserListEntry<T>
{
    @Nullable
    private final T value;

    public UserListEntry(@Nullable T valueIn)
    {
        this.value = valueIn;
    }

    @Nullable
    T getValue()
    {
        return this.value;
    }

    boolean hasBanExpired()
    {
        return false;
    }

    protected abstract void onSerialization(JsonObject data);
}
