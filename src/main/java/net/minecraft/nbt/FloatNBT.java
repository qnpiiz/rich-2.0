package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class FloatNBT extends NumberNBT
{
    public static final FloatNBT ZERO = new FloatNBT(0.0F);
    public static final INBTType<FloatNBT> TYPE = new INBTType<FloatNBT>()
    {
        public FloatNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException
        {
            accounter.read(96L);
            return FloatNBT.valueOf(input.readFloat());
        }
        public String getName()
        {
            return "FLOAT";
        }
        public String getTagName()
        {
            return "TAG_Float";
        }
        public boolean isPrimitive()
        {
            return true;
        }
    };
    private final float data;

    private FloatNBT(float data)
    {
        this.data = data;
    }

    public static FloatNBT valueOf(float value)
    {
        return value == 0.0F ? ZERO : new FloatNBT(value);
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    public void write(DataOutput output) throws IOException
    {
        output.writeFloat(this.data);
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 5;
    }

    public INBTType<FloatNBT> getType()
    {
        return TYPE;
    }

    public String toString()
    {
        return this.data + "f";
    }

    /**
     * Creates a clone of the tag.
     */
    public FloatNBT copy()
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
            return p_equals_1_ instanceof FloatNBT && this.data == ((FloatNBT)p_equals_1_).data;
        }
    }

    public int hashCode()
    {
        return Float.floatToIntBits(this.data);
    }

    public ITextComponent toFormattedComponent(String indentation, int indentDepth)
    {
        ITextComponent itextcomponent = (new StringTextComponent("f")).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return (new StringTextComponent(String.valueOf(this.data))).append(itextcomponent).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    public long getLong()
    {
        return (long)this.data;
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
        return (double)this.data;
    }

    public float getFloat()
    {
        return this.data;
    }

    public Number getAsNumber()
    {
        return this.data;
    }
}
