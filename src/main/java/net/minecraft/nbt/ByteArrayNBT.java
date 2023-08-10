package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayNBT extends CollectionNBT<ByteNBT>
{
    public static final INBTType<ByteArrayNBT> TYPE = new INBTType<ByteArrayNBT>()
    {
        public ByteArrayNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException
        {
            accounter.read(192L);
            int i = input.readInt();
            accounter.read(8L * (long)i);
            byte[] abyte = new byte[i];
            input.readFully(abyte);
            return new ByteArrayNBT(abyte);
        }
        public String getName()
        {
            return "BYTE[]";
        }
        public String getTagName()
        {
            return "TAG_Byte_Array";
        }
    };
    private byte[] data;

    public ByteArrayNBT(byte[] data)
    {
        this.data = data;
    }

    public ByteArrayNBT(List<Byte> bytes)
    {
        this(toArray(bytes));
    }

    private static byte[] toArray(List<Byte> bytes)
    {
        byte[] abyte = new byte[bytes.size()];

        for (int i = 0; i < bytes.size(); ++i)
        {
            Byte obyte = bytes.get(i);
            abyte[i] = obyte == null ? 0 : obyte;
        }

        return abyte;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    public void write(DataOutput output) throws IOException
    {
        output.writeInt(this.data.length);
        output.write(this.data);
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 7;
    }

    public INBTType<ByteArrayNBT> getType()
    {
        return TYPE;
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder("[B;");

        for (int i = 0; i < this.data.length; ++i)
        {
            if (i != 0)
            {
                stringbuilder.append(',');
            }

            stringbuilder.append((int)this.data[i]).append('B');
        }

        return stringbuilder.append(']').toString();
    }

    /**
     * Creates a clone of the tag.
     */
    public INBT copy()
    {
        byte[] abyte = new byte[this.data.length];
        System.arraycopy(this.data, 0, abyte, 0, this.data.length);
        return new ByteArrayNBT(abyte);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else
        {
            return p_equals_1_ instanceof ByteArrayNBT && Arrays.equals(this.data, ((ByteArrayNBT)p_equals_1_).data);
        }
    }

    public int hashCode()
    {
        return Arrays.hashCode(this.data);
    }

    public ITextComponent toFormattedComponent(String indentation, int indentDepth)
    {
        ITextComponent itextcomponent = (new StringTextComponent("B")).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        IFormattableTextComponent iformattabletextcomponent = (new StringTextComponent("[")).append(itextcomponent).appendString(";");

        for (int i = 0; i < this.data.length; ++i)
        {
            IFormattableTextComponent iformattabletextcomponent1 = (new StringTextComponent(String.valueOf((int)this.data[i]))).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER);
            iformattabletextcomponent.appendString(" ").append(iformattabletextcomponent1).append(itextcomponent);

            if (i != this.data.length - 1)
            {
                iformattabletextcomponent.appendString(",");
            }
        }

        iformattabletextcomponent.appendString("]");
        return iformattabletextcomponent;
    }

    public byte[] getByteArray()
    {
        return this.data;
    }

    public int size()
    {
        return this.data.length;
    }

    public ByteNBT get(int p_get_1_)
    {
        return ByteNBT.valueOf(this.data[p_get_1_]);
    }

    public ByteNBT set(int p_set_1_, ByteNBT p_set_2_)
    {
        byte b0 = this.data[p_set_1_];
        this.data[p_set_1_] = p_set_2_.getByte();
        return ByteNBT.valueOf(b0);
    }

    public void add(int p_add_1_, ByteNBT p_add_2_)
    {
        this.data = ArrayUtils.add(this.data, p_add_1_, p_add_2_.getByte());
    }

    public boolean setNBTByIndex(int index, INBT nbt)
    {
        if (nbt instanceof NumberNBT)
        {
            this.data[index] = ((NumberNBT)nbt).getByte();
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean addNBTByIndex(int index, INBT nbt)
    {
        if (nbt instanceof NumberNBT)
        {
            this.data = ArrayUtils.add(this.data, index, ((NumberNBT)nbt).getByte());
            return true;
        }
        else
        {
            return false;
        }
    }

    public ByteNBT remove(int p_remove_1_)
    {
        byte b0 = this.data[p_remove_1_];
        this.data = ArrayUtils.remove(this.data, p_remove_1_);
        return ByteNBT.valueOf(b0);
    }

    public byte getTagType()
    {
        return 1;
    }

    public void clear()
    {
        this.data = new byte[0];
    }
}
