package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class DoubleNBT extends NumberNBT
{
    public static final DoubleNBT ZERO = new DoubleNBT(0.0D);
    public static final INBTType<DoubleNBT> TYPE = new INBTType<DoubleNBT>()
    {
        public DoubleNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException
        {
            accounter.read(128L);
            return DoubleNBT.valueOf(input.readDouble());
        }
        public String getName()
        {
            return "DOUBLE";
        }
        public String getTagName()
        {
            return "TAG_Double";
        }
        public boolean isPrimitive()
        {
            return true;
        }
    };
    private final double data;

    private DoubleNBT(double data)
    {
        this.data = data;
    }

    public static DoubleNBT valueOf(double value)
    {
        return value == 0.0D ? ZERO : new DoubleNBT(value);
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    public void write(DataOutput output) throws IOException
    {
        output.writeDouble(this.data);
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 6;
    }

    public INBTType<DoubleNBT> getType()
    {
        return TYPE;
    }

    public String toString()
    {
        return this.data + "d";
    }

    /**
     * Creates a clone of the tag.
     */
    public DoubleNBT copy()
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
            return p_equals_1_ instanceof DoubleNBT && this.data == ((DoubleNBT)p_equals_1_).data;
        }
    }

    public int hashCode()
    {
        long i = Double.doubleToLongBits(this.data);
        return (int)(i ^ i >>> 32);
    }

    public ITextComponent toFormattedComponent(String indentation, int indentDepth)
    {
        ITextComponent itextcomponent = (new StringTextComponent("d")).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return (new StringTextComponent(String.valueOf(this.data))).append(itextcomponent).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    public long getLong()
    {
        return (long)Math.floor(this.data);
    }

    public int getInt()
    {
        return MathHelper.floor(this.data);
    }

    public short getShort()
    {
        return (short)(MathHelper.floor(this.data) & 65535);
    }

    public byte getByte()
    {
        return (byte)(MathHelper.floor(this.data) & 255);
    }

    public double getDouble()
    {
        return this.data;
    }

    public float getFloat()
    {
        return (float)this.data;
    }

    public Number getAsNumber()
    {
        return this.data;
    }
}
