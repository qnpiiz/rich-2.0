package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;

public interface INBTType<T extends INBT>
{
    T readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException;

default boolean isPrimitive()
    {
        return false;
    }

    String getName();

    String getTagName();

    static INBTType<EndNBT> getEndNBT(final int id)
    {
        return new INBTType<EndNBT>()
        {
            public EndNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException
            {
                throw new IllegalArgumentException("Invalid tag id: " + id);
            }
            public String getName()
            {
                return "INVALID[" + id + "]";
            }
            public String getTagName()
            {
                return "UNKNOWN_" + id;
            }
        };
    }
}
