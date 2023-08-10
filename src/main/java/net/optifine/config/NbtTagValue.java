package net.optifine.config;

import java.util.Arrays;
import java.util.regex.Pattern;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.nbt.StringNBT;
import net.optifine.Config;
import net.optifine.util.StrUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class NbtTagValue
{
    private String[] parents = null;
    private String name = null;
    private boolean negative = false;
    private int type = 0;
    private String value = null;
    private int valueFormat = 0;
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_PATTERN = 1;
    private static final int TYPE_IPATTERN = 2;
    private static final int TYPE_REGEX = 3;
    private static final int TYPE_IREGEX = 4;
    private static final String PREFIX_PATTERN = "pattern:";
    private static final String PREFIX_IPATTERN = "ipattern:";
    private static final String PREFIX_REGEX = "regex:";
    private static final String PREFIX_IREGEX = "iregex:";
    private static final int FORMAT_DEFAULT = 0;
    private static final int FORMAT_HEX_COLOR = 1;
    private static final String PREFIX_HEX_COLOR = "#";
    private static final Pattern PATTERN_HEX_COLOR = Pattern.compile("^#[0-9a-f]{6}+$");

    public NbtTagValue(String tag, String value)
    {
        String[] astring = Config.tokenize(tag, ".");
        this.parents = Arrays.copyOfRange(astring, 0, astring.length - 1);
        this.name = astring[astring.length - 1];

        if (value.startsWith("!"))
        {
            this.negative = true;
            value = value.substring(1);
        }

        if (value.startsWith("pattern:"))
        {
            this.type = 1;
            value = value.substring("pattern:".length());
        }
        else if (value.startsWith("ipattern:"))
        {
            this.type = 2;
            value = value.substring("ipattern:".length()).toLowerCase();
        }
        else if (value.startsWith("regex:"))
        {
            this.type = 3;
            value = value.substring("regex:".length());
        }
        else if (value.startsWith("iregex:"))
        {
            this.type = 4;
            value = value.substring("iregex:".length()).toLowerCase();
        }
        else
        {
            this.type = 0;
        }

        value = StringEscapeUtils.unescapeJava(value);

        if (this.type == 0 && PATTERN_HEX_COLOR.matcher(value).matches())
        {
            this.valueFormat = 1;
        }

        this.value = value;
    }

    public boolean matches(CompoundNBT nbt)
    {
        if (this.negative)
        {
            return !this.matchesCompound(nbt);
        }
        else
        {
            return this.matchesCompound(nbt);
        }
    }

    public boolean matchesCompound(CompoundNBT nbt)
    {
        if (nbt == null)
        {
            return false;
        }
        else
        {
            INBT inbt = nbt;

            for (int i = 0; i < this.parents.length; ++i)
            {
                String s = this.parents[i];
                inbt = getChildTag(inbt, s);

                if (inbt == null)
                {
                    return false;
                }
            }

            if (this.name.equals("*"))
            {
                return this.matchesAnyChild(inbt);
            }
            else
            {
                INBT inbt1 = getChildTag(inbt, this.name);

                if (inbt1 == null)
                {
                    return false;
                }
                else
                {
                    return this.matchesBase(inbt1);
                }
            }
        }
    }

    private boolean matchesAnyChild(INBT tagBase)
    {
        if (tagBase instanceof CompoundNBT)
        {
            CompoundNBT compoundnbt = (CompoundNBT)tagBase;

            for (String s : compoundnbt.keySet())
            {
                INBT inbt = compoundnbt.get(s);

                if (this.matchesBase(inbt))
                {
                    return true;
                }
            }
        }

        if (tagBase instanceof ListNBT)
        {
            ListNBT listnbt = (ListNBT)tagBase;
            int i = listnbt.size();

            for (int j = 0; j < i; ++j)
            {
                INBT inbt1 = listnbt.get(j);

                if (this.matchesBase(inbt1))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private static INBT getChildTag(INBT tagBase, String tag)
    {
        if (tagBase instanceof CompoundNBT)
        {
            CompoundNBT compoundnbt = (CompoundNBT)tagBase;
            return compoundnbt.get(tag);
        }
        else if (tagBase instanceof ListNBT)
        {
            ListNBT listnbt = (ListNBT)tagBase;

            if (tag.equals("count"))
            {
                return IntNBT.valueOf(listnbt.size());
            }
            else
            {
                int i = Config.parseInt(tag, -1);
                return i >= 0 && i < listnbt.size() ? listnbt.get(i) : null;
            }
        }
        else
        {
            return null;
        }
    }

    public boolean matchesBase(INBT nbtBase)
    {
        if (nbtBase == null)
        {
            return false;
        }
        else
        {
            String s = getNbtString(nbtBase, this.valueFormat);
            return this.matchesValue(s);
        }
    }

    public boolean matchesValue(String nbtValue)
    {
        if (nbtValue == null)
        {
            return false;
        }
        else
        {
            switch (this.type)
            {
                case 0:
                    return nbtValue.equals(this.value);

                case 1:
                    return this.matchesPattern(nbtValue, this.value);

                case 2:
                    return this.matchesPattern(nbtValue.toLowerCase(), this.value);

                case 3:
                    return this.matchesRegex(nbtValue, this.value);

                case 4:
                    return this.matchesRegex(nbtValue.toLowerCase(), this.value);

                default:
                    throw new IllegalArgumentException("Unknown NbtTagValue type: " + this.type);
            }
        }
    }

    private boolean matchesPattern(String str, String pattern)
    {
        return StrUtils.equalsMask(str, pattern, '*', '?');
    }

    private boolean matchesRegex(String str, String regex)
    {
        return str.matches(regex);
    }

    private static String getNbtString(INBT nbtBase, int format)
    {
        if (nbtBase == null)
        {
            return null;
        }
        else if (!(nbtBase instanceof StringNBT))
        {
            if (nbtBase instanceof IntNBT)
            {
                IntNBT intnbt = (IntNBT)nbtBase;
                return format == 1 ? "#" + StrUtils.fillLeft(Integer.toHexString(intnbt.getInt()), 6, '0') : Integer.toString(intnbt.getInt());
            }
            else if (nbtBase instanceof ByteNBT)
            {
                ByteNBT bytenbt = (ByteNBT)nbtBase;
                return Byte.toString(bytenbt.getByte());
            }
            else if (nbtBase instanceof ShortNBT)
            {
                ShortNBT shortnbt = (ShortNBT)nbtBase;
                return Short.toString(shortnbt.getShort());
            }
            else if (nbtBase instanceof LongNBT)
            {
                LongNBT longnbt = (LongNBT)nbtBase;
                return Long.toString(longnbt.getLong());
            }
            else if (nbtBase instanceof FloatNBT)
            {
                FloatNBT floatnbt = (FloatNBT)nbtBase;
                return Float.toString(floatnbt.getFloat());
            }
            else if (nbtBase instanceof DoubleNBT)
            {
                DoubleNBT doublenbt = (DoubleNBT)nbtBase;
                return Double.toString(doublenbt.getDouble());
            }
            else
            {
                return nbtBase.toString();
            }
        }
        else
        {
            StringNBT stringnbt = (StringNBT)nbtBase;
            String s = stringnbt.getString();

            if (s.startsWith("{") && s.endsWith("}"))
            {
                s = getMergedJsonText(s);
            }
            else if (s.startsWith("[{") && s.endsWith("}]"))
            {
                s = getMergedJsonText(s);
            }

            return s;
        }
    }

    private static String getMergedJsonText(String text)
    {
        StringBuilder stringbuilder = new StringBuilder();
        String s = "\"text\":\"";
        int i = -1;

        while (true)
        {
            i = text.indexOf(s, i + 1);

            if (i < 0)
            {
                return stringbuilder.toString();
            }

            String s1 = parseString(text, i + s.length());

            if (s1 != null)
            {
                stringbuilder.append(s1);
            }
        }
    }

    private static String parseString(String text, int pos)
    {
        StringBuilder stringbuilder = new StringBuilder();
        boolean flag = false;

        for (int i = pos; i < text.length(); ++i)
        {
            char c0 = text.charAt(i);

            if (flag)
            {
                if (c0 == 'b')
                {
                    stringbuilder.append('\b');
                }
                else if (c0 == 'f')
                {
                    stringbuilder.append('\f');
                }
                else if (c0 == 'n')
                {
                    stringbuilder.append('\n');
                }
                else if (c0 == 'r')
                {
                    stringbuilder.append('\r');
                }
                else if (c0 == 't')
                {
                    stringbuilder.append('\t');
                }
                else
                {
                    stringbuilder.append(c0);
                }

                flag = false;
            }
            else if (c0 == '\\')
            {
                flag = true;
            }
            else
            {
                if (c0 == '"')
                {
                    break;
                }

                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer();

        for (int i = 0; i < this.parents.length; ++i)
        {
            String s = this.parents[i];

            if (i > 0)
            {
                stringbuffer.append(".");
            }

            stringbuffer.append(s);
        }

        if (stringbuffer.length() > 0)
        {
            stringbuffer.append(".");
        }

        stringbuffer.append(this.name);
        stringbuffer.append(" = ");
        stringbuffer.append(this.value);
        return stringbuffer.toString();
    }
}
