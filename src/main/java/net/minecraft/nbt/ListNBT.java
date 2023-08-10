package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ListNBT extends CollectionNBT<INBT>
{
    public static final INBTType<ListNBT> TYPE = new INBTType<ListNBT>()
    {
        public ListNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException
        {
            accounter.read(296L);

            if (depth > 512)
            {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            }
            else
            {
                byte b0 = input.readByte();
                int i = input.readInt();

                if (b0 == 0 && i > 0)
                {
                    throw new RuntimeException("Missing type on ListTag");
                }
                else
                {
                    accounter.read(32L * (long)i);
                    INBTType<?> inbttype = NBTTypes.getGetTypeByID(b0);
                    List<INBT> list = Lists.newArrayListWithCapacity(i);

                    for (int j = 0; j < i; ++j)
                    {
                        list.add(inbttype.readNBT(input, depth + 1, accounter));
                    }

                    return new ListNBT(list, b0);
                }
            }
        }
        public String getName()
        {
            return "LIST";
        }
        public String getTagName()
        {
            return "TAG_List";
        }
    };
    private static final ByteSet typeSet = new ByteOpenHashSet(Arrays.asList((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6));
    private final List<INBT> tagList;
    private byte tagType;

    private ListNBT(List<INBT> tagList, byte tagType)
    {
        this.tagList = tagList;
        this.tagType = tagType;
    }

    public ListNBT()
    {
        this(Lists.newArrayList(), (byte)0);
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    public void write(DataOutput output) throws IOException
    {
        if (this.tagList.isEmpty())
        {
            this.tagType = 0;
        }
        else
        {
            this.tagType = this.tagList.get(0).getId();
        }

        output.writeByte(this.tagType);
        output.writeInt(this.tagList.size());

        for (INBT inbt : this.tagList)
        {
            inbt.write(output);
        }
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 9;
    }

    public INBTType<ListNBT> getType()
    {
        return TYPE;
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder("[");

        for (int i = 0; i < this.tagList.size(); ++i)
        {
            if (i != 0)
            {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.tagList.get(i));
        }

        return stringbuilder.append(']').toString();
    }

    private void checkEmpty()
    {
        if (this.tagList.isEmpty())
        {
            this.tagType = 0;
        }
    }

    public INBT remove(int p_remove_1_)
    {
        INBT inbt = this.tagList.remove(p_remove_1_);
        this.checkEmpty();
        return inbt;
    }

    public boolean isEmpty()
    {
        return this.tagList.isEmpty();
    }

    /**
     * Retrieves the NBTTagCompound at the specified index in the list
     */
    public CompoundNBT getCompound(int i)
    {
        if (i >= 0 && i < this.tagList.size())
        {
            INBT inbt = this.tagList.get(i);

            if (inbt.getId() == 10)
            {
                return (CompoundNBT)inbt;
            }
        }

        return new CompoundNBT();
    }

    public ListNBT getList(int iIn)
    {
        if (iIn >= 0 && iIn < this.tagList.size())
        {
            INBT inbt = this.tagList.get(iIn);

            if (inbt.getId() == 9)
            {
                return (ListNBT)inbt;
            }
        }

        return new ListNBT();
    }

    public short getShort(int iIn)
    {
        if (iIn >= 0 && iIn < this.tagList.size())
        {
            INBT inbt = this.tagList.get(iIn);

            if (inbt.getId() == 2)
            {
                return ((ShortNBT)inbt).getShort();
            }
        }

        return 0;
    }

    public int getInt(int iIn)
    {
        if (iIn >= 0 && iIn < this.tagList.size())
        {
            INBT inbt = this.tagList.get(iIn);

            if (inbt.getId() == 3)
            {
                return ((IntNBT)inbt).getInt();
            }
        }

        return 0;
    }

    public int[] getIntArray(int i)
    {
        if (i >= 0 && i < this.tagList.size())
        {
            INBT inbt = this.tagList.get(i);

            if (inbt.getId() == 11)
            {
                return ((IntArrayNBT)inbt).getIntArray();
            }
        }

        return new int[0];
    }

    public double getDouble(int i)
    {
        if (i >= 0 && i < this.tagList.size())
        {
            INBT inbt = this.tagList.get(i);

            if (inbt.getId() == 6)
            {
                return ((DoubleNBT)inbt).getDouble();
            }
        }

        return 0.0D;
    }

    public float getFloat(int i)
    {
        if (i >= 0 && i < this.tagList.size())
        {
            INBT inbt = this.tagList.get(i);

            if (inbt.getId() == 5)
            {
                return ((FloatNBT)inbt).getFloat();
            }
        }

        return 0.0F;
    }

    /**
     * Retrieves the tag String value at the specified index in the list
     */
    public String getString(int i)
    {
        if (i >= 0 && i < this.tagList.size())
        {
            INBT inbt = this.tagList.get(i);
            return inbt.getId() == 8 ? inbt.getString() : inbt.toString();
        }
        else
        {
            return "";
        }
    }

    public int size()
    {
        return this.tagList.size();
    }

    public INBT get(int p_get_1_)
    {
        return this.tagList.get(p_get_1_);
    }

    public INBT set(int p_set_1_, INBT p_set_2_)
    {
        INBT inbt = this.get(p_set_1_);

        if (!this.setNBTByIndex(p_set_1_, p_set_2_))
        {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", p_set_2_.getId(), this.tagType));
        }
        else
        {
            return inbt;
        }
    }

    public void add(int p_add_1_, INBT p_add_2_)
    {
        if (!this.addNBTByIndex(p_add_1_, p_add_2_))
        {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", p_add_2_.getId(), this.tagType));
        }
    }

    public boolean setNBTByIndex(int index, INBT nbt)
    {
        if (this.canInsert(nbt))
        {
            this.tagList.set(index, nbt);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean addNBTByIndex(int index, INBT nbt)
    {
        if (this.canInsert(nbt))
        {
            this.tagList.add(index, nbt);
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean canInsert(INBT nbt)
    {
        if (nbt.getId() == 0)
        {
            return false;
        }
        else if (this.tagType == 0)
        {
            this.tagType = nbt.getId();
            return true;
        }
        else
        {
            return this.tagType == nbt.getId();
        }
    }

    /**
     * Creates a clone of the tag.
     */
    public ListNBT copy()
    {
        Iterable<INBT> iterable = (Iterable<INBT>)(NBTTypes.getGetTypeByID(this.tagType).isPrimitive() ? this.tagList : Iterables.transform(this.tagList, INBT::copy));
        List<INBT> list = Lists.newArrayList(iterable);
        return new ListNBT(list, this.tagType);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else
        {
            return p_equals_1_ instanceof ListNBT && Objects.equals(this.tagList, ((ListNBT)p_equals_1_).tagList);
        }
    }

    public int hashCode()
    {
        return this.tagList.hashCode();
    }

    public ITextComponent toFormattedComponent(String indentation, int indentDepth)
    {
        if (this.isEmpty())
        {
            return new StringTextComponent("[]");
        }
        else if (typeSet.contains(this.tagType) && this.size() <= 8)
        {
            String s1 = ", ";
            IFormattableTextComponent iformattabletextcomponent2 = new StringTextComponent("[");

            for (int j = 0; j < this.tagList.size(); ++j)
            {
                if (j != 0)
                {
                    iformattabletextcomponent2.appendString(", ");
                }

                iformattabletextcomponent2.append(this.tagList.get(j).toFormattedComponent());
            }

            iformattabletextcomponent2.appendString("]");
            return iformattabletextcomponent2;
        }
        else
        {
            IFormattableTextComponent iformattabletextcomponent = new StringTextComponent("[");

            if (!indentation.isEmpty())
            {
                iformattabletextcomponent.appendString("\n");
            }

            String s = String.valueOf(',');

            for (int i = 0; i < this.tagList.size(); ++i)
            {
                IFormattableTextComponent iformattabletextcomponent1 = new StringTextComponent(Strings.repeat(indentation, indentDepth + 1));
                iformattabletextcomponent1.append(this.tagList.get(i).toFormattedComponent(indentation, indentDepth + 1));

                if (i != this.tagList.size() - 1)
                {
                    iformattabletextcomponent1.appendString(s).appendString(indentation.isEmpty() ? " " : "\n");
                }

                iformattabletextcomponent.append(iformattabletextcomponent1);
            }

            if (!indentation.isEmpty())
            {
                iformattabletextcomponent.appendString("\n").appendString(Strings.repeat(indentation, indentDepth));
            }

            iformattabletextcomponent.appendString("]");
            return iformattabletextcomponent;
        }
    }

    public byte getTagType()
    {
        return this.tagType;
    }

    public void clear()
    {
        this.tagList.clear();
        this.tagType = 0;
    }
}
