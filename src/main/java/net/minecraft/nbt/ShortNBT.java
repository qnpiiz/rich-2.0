package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ShortNBT extends NumberNBT
{
    public static final INBTType<ShortNBT> TYPE = new INBTType<ShortNBT>()
    {
        public ShortNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException
        {
            accounter.read(80L);
            return ShortNBT.valueOf(input.readShort());
        }
        public String getName()
        {
            return "SHORT";
        }
        public String getTagName()
        {
            return "TAG_Short";
        }
        public boolean isPrimitive()
        {
            return true;
        }
    };
    private final short data;

    private ShortNBT(short data)
    {
        this.data = data;
    }

    public static ShortNBT valueOf(short value)
    {
        return value >= -128 && value <= 1024 ? ShortNBT.Cache.CACHE[value + 128] : new ShortNBT(value);
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    public void write(DataOutput output) throws IOException
    {
        output.writeShort(this.data);
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 2;
    }

    public INBTType<ShortNBT> getType()
    {
        return TYPE;
    }

    public String toString()
    {
        return this.data + "s";
    }

    /**
     * Creates a clone of the tag.
     */
    public ShortNBT copy()
    {
        return this;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else
        {
            return p_equals_1_ instanceof ShortNBT && this.data == ((ShortNBT)p_equals_1_).data;
        }
    }

    public int hashCode()
    {
        return this.data;
    }

    public ITextComponent toFormattedComponent(String indentation, int indentDepth)
    {
        ITextComponent itextcomponent = (new StringTextComponent("s")).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return (new StringTextComponent(String.valueOf((int)this.data))).append(itextcomponent).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    public long getLong()
    {
        return (long)this.data;
    }

    public int getInt()
    {
        return this.data;
    }

    public short getShort()
    {
        return this.data;
    }

    public byte getByte()
    {
        return (byte)(this.data & 255);
    }

    public double getDouble()
    {
        return (double)this.data;
    }

    public float getFloat()
    {
        return (float)this.data;
    }

    public Number getAsNumber()
    {
        return this.data;
    }

    static class Cache
    {
        static final ShortNBT[] CACHE = new ShortNBT[1153];

        static
        {
            for (int i = 0; i < CACHE.length; ++i)
            {
                CACHE[i] = new ShortNBT((short)(-128 + i));
            }
        }
    }
}
