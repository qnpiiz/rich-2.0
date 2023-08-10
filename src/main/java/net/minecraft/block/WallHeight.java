package net.minecraft.block;

import net.minecraft.util.IStringSerializable;

public enum WallHeight implements IStringSerializable
{
    NONE("none"),
    LOW("low"),
    TALL("tall");

    private final String heightName;

    private WallHeight(String name)
    {
        this.heightName = name;
    }

    public String toString()
    {
        return this.getString();
    }

    public String getString()
    {
        return this.heightName;
    }
}
