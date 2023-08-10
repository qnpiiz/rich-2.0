package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum DoubleBlockHalf implements IStringSerializable
{
    UPPER,
    LOWER;

    public String toString()
    {
        return this.getString();
    }

    public String getString()
    {
        return this == UPPER ? "upper" : "lower";
    }
}
