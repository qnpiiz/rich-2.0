package net.minecraft.util.palette;

import java.util.function.Predicate;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ObjectIntIdentityMap;

public class IdentityPalette<T> implements IPalette<T>
{
    private final ObjectIntIdentityMap<T> registry;
    private final T defaultState;

    public IdentityPalette(ObjectIntIdentityMap<T> p_i48965_1_, T p_i48965_2_)
    {
        this.registry = p_i48965_1_;
        this.defaultState = p_i48965_2_;
    }

    public int idFor(T state)
    {
        int i = this.registry.getId(state);
        return i == -1 ? 0 : i;
    }

    public boolean func_230341_a_(Predicate<T> p_230341_1_)
    {
        return true;
    }

    /**
     * Gets the block state by the palette id.
     */
    public T get(int indexKey)
    {
        T t = this.registry.getByValue(indexKey);
        return (T)(t == null ? this.defaultState : t);
    }

    public void read(PacketBuffer buf)
    {
    }

    public void write(PacketBuffer buf)
    {
    }

    public int getSerializedSize()
    {
        return PacketBuffer.getVarIntSize(0);
    }

    public void read(ListNBT nbt)
    {
    }
}
