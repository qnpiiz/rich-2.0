package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ByteNBT extends NumberNBT
{
    public static final INBTType<ByteNBT> TYPE = new INBTType<ByteNBT>()
    {
        public ByteNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException
        {
            accounter.read(72L);
            return ByteNBT.valueOf(input.readByte());
        }
        public String getName()
        {
            return "BYTE";
        }
        public String getTagName()
        {
            return "TAG_Byte";
        }
        public boolean isPrimitive()
        {
            return true;
        }
    };
    public static final ByteNBT ZERO = valueOf((byte)0);
    public static final ByteNBT ONE = valueOf((byte)1);
    private final byte data;

    private ByteNBT(byte data)
    {
        this.data = data;
    }

    public static ByteNBT valueOf(byte byteIn)
    {
        return ByteNBT.Cache.CACHE[128 + byteIn];
    }

    public static ByteNBT valueOf(boolean one)
    {
        return one ? ONE : ZERO;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    public void write(DataOutput output) throws IOException
    {
        output.writeByte(this.data);
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 1;
    }

    public INBTType<ByteNBT> getType()
    {
        return TYPE;
    }

    public String toString()
    {
        return this.data + "b";
    }

    /**
     * Creates a clone of the tag.
     */
    public ByteNBT copy()
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
            return p_equals_1_ instanceof ByteNBT && this.data == ((ByteNBT)p_equals_1_).data;
        }
    }

    public int hashCode()
    {
        return this.data;
    }

    public ITextComponent toFormattedComponent(String indentation, int indentDepth)
    {
        ITextComponent itextcomponent = (new StringTextComponent("b")).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
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
        return (short)this.data;
    }

    public byte getByte()
    {
        return this.data;
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
        private static final ByteNBT[] CACHE = new ByteNBT[256];

        static
        {
            for (int i = 0; i < CACHE.length; ++i)
            {
                CACHE[i] = new ByteNBT((byte)(i - 128));
            }
        }
    }
}
