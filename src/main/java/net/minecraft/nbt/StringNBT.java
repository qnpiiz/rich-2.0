package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class StringNBT implements INBT
{
    public static final INBTType<StringNBT> TYPE = new INBTType<StringNBT>()
    {
        public StringNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException
        {
            accounter.read(288L);
            String s = input.readUTF();
            accounter.read((long)(16 * s.length()));
            return StringNBT.valueOf(s);
        }
        public String getName()
        {
            return "STRING";
        }
        public String getTagName()
        {
            return "TAG_String";
        }
        public boolean isPrimitive()
        {
            return true;
        }
    };
    private static final StringNBT EMPTY_STRING = new StringNBT("");
    private final String data;

    private StringNBT(String data)
    {
        Objects.requireNonNull(data, "Null string not allowed");
        this.data = data;
    }

    public static StringNBT valueOf(String value)
    {
        return value.isEmpty() ? EMPTY_STRING : new StringNBT(value);
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    public void write(DataOutput output) throws IOException
    {
        output.writeUTF(this.data);
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 8;
    }

    public INBTType<StringNBT> getType()
    {
        return TYPE;
    }

    public String toString()
    {
        return quoteAndEscape(this.data);
    }

    /**
     * Creates a clone of the tag.
     */
    public StringNBT copy()
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
            return p_equals_1_ instanceof StringNBT && Objects.equals(this.data, ((StringNBT)p_equals_1_).data);
        }
    }

    public int hashCode()
    {
        return this.data.hashCode();
    }

    public String getString()
    {
        return this.data;
    }

    public ITextComponent toFormattedComponent(String indentation, int indentDepth)
    {
        String s = quoteAndEscape(this.data);
        String s1 = s.substring(0, 1);
        ITextComponent itextcomponent = (new StringTextComponent(s.substring(1, s.length() - 1))).mergeStyle(SYNTAX_HIGHLIGHTING_STRING);
        return (new StringTextComponent(s1)).append(itextcomponent).appendString(s1);
    }

    public static String quoteAndEscape(String name)
    {
        StringBuilder stringbuilder = new StringBuilder(" ");
        char c0 = 0;

        for (int i = 0; i < name.length(); ++i)
        {
            char c1 = name.charAt(i);

            if (c1 == '\\')
            {
                stringbuilder.append('\\');
            }
            else if (c1 == '"' || c1 == '\'')
            {
                if (c0 == 0)
                {
                    c0 = (char)(c1 == '"' ? 39 : 34);
                }

                if (c0 == c1)
                {
                    stringbuilder.append('\\');
                }
            }

            stringbuilder.append(c1);
        }

        if (c0 == 0)
        {
            c0 = '"';
        }

        stringbuilder.setCharAt(0, c0);
        stringbuilder.append(c0);
        return stringbuilder.toString();
    }
}
