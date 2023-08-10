package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompoundNBT implements INBT
{
    public static final Codec<CompoundNBT> CODEC = Codec.PASSTHROUGH.comapFlatMap((dynamic) ->
    {
        INBT inbt = dynamic.convert(NBTDynamicOps.INSTANCE).getValue();
        return inbt instanceof CompoundNBT ? DataResult.success((CompoundNBT)inbt) : DataResult.error("Not a compound tag: " + inbt);
    }, (nbt) ->
    {
        return new Dynamic<>(NBTDynamicOps.INSTANCE, nbt);
    });
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
    public static final INBTType<CompoundNBT> TYPE = new INBTType<CompoundNBT>()
    {
        public CompoundNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException
        {
            accounter.read(384L);

            if (depth > 512)
            {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            }
            else
            {
                Map<String, INBT> map = Maps.newHashMap();
                byte b0;

                while ((b0 = CompoundNBT.readType(input, accounter)) != 0)
                {
                    String s = CompoundNBT.readKey(input, accounter);
                    accounter.read((long)(224 + 16 * s.length()));
                    INBT inbt = CompoundNBT.loadNBT(NBTTypes.getGetTypeByID(b0), s, input, depth + 1, accounter);

                    if (map.put(s, inbt) != null)
                    {
                        accounter.read(288L);
                    }
                }

                return new CompoundNBT(map);
            }
        }
        public String getName()
        {
            return "COMPOUND";
        }
        public String getTagName()
        {
            return "TAG_Compound";
        }
    };
    private final Map<String, INBT> tagMap;

    protected CompoundNBT(Map<String, INBT> tagMap)
    {
        this.tagMap = tagMap;
    }

    public CompoundNBT()
    {
        this(Maps.newHashMap());
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    public void write(DataOutput output) throws IOException
    {
        for (String s : this.tagMap.keySet())
        {
            INBT inbt = this.tagMap.get(s);
            writeEntry(s, inbt, output);
        }

        output.writeByte(0);
    }

    public Set<String> keySet()
    {
        return this.tagMap.keySet();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 10;
    }

    public INBTType<CompoundNBT> getType()
    {
        return TYPE;
    }

    public int size()
    {
        return this.tagMap.size();
    }

    @Nullable
    public INBT put(String key, INBT value)
    {
        return this.tagMap.put(key, value);
    }

    /**
     * Stores a new NBTTagByte with the given byte value into the map with the given string key.
     */
    public void putByte(String key, byte value)
    {
        this.tagMap.put(key, ByteNBT.valueOf(value));
    }

    /**
     * Stores a new NBTTagShort with the given short value into the map with the given string key.
     */
    public void putShort(String key, short value)
    {
        this.tagMap.put(key, ShortNBT.valueOf(value));
    }

    /**
     * Stores a new NBTTagInt with the given integer value into the map with the given string key.
     */
    public void putInt(String key, int value)
    {
        this.tagMap.put(key, IntNBT.valueOf(value));
    }

    /**
     * Stores a new NBTTagLong with the given long value into the map with the given string key.
     */
    public void putLong(String key, long value)
    {
        this.tagMap.put(key, LongNBT.valueOf(value));
    }

    public void putUniqueId(String key, UUID value)
    {
        this.tagMap.put(key, NBTUtil.func_240626_a_(value));
    }

    public UUID getUniqueId(String key)
    {
        return NBTUtil.readUniqueId(this.get(key));
    }

    public boolean hasUniqueId(String key)
    {
        INBT inbt = this.get(key);
        return inbt != null && inbt.getType() == IntArrayNBT.TYPE && ((IntArrayNBT)inbt).getIntArray().length == 4;
    }

    /**
     * Stores a new NBTTagFloat with the given float value into the map with the given string key.
     */
    public void putFloat(String key, float value)
    {
        this.tagMap.put(key, FloatNBT.valueOf(value));
    }

    /**
     * Stores a new NBTTagDouble with the given double value into the map with the given string key.
     */
    public void putDouble(String key, double value)
    {
        this.tagMap.put(key, DoubleNBT.valueOf(value));
    }

    /**
     * Stores a new NBTTagString with the given string value into the map with the given string key.
     */
    public void putString(String key, String value)
    {
        this.tagMap.put(key, StringNBT.valueOf(value));
    }

    /**
     * Stores a new NBTTagByteArray with the given array as data into the map with the given string key.
     */
    public void putByteArray(String key, byte[] value)
    {
        this.tagMap.put(key, new ByteArrayNBT(value));
    }

    /**
     * Stores a new NBTTagIntArray with the given array as data into the map with the given string key.
     */
    public void putIntArray(String key, int[] value)
    {
        this.tagMap.put(key, new IntArrayNBT(value));
    }

    public void putIntArray(String key, List<Integer> value)
    {
        this.tagMap.put(key, new IntArrayNBT(value));
    }

    public void putLongArray(String key, long[] value)
    {
        this.tagMap.put(key, new LongArrayNBT(value));
    }

    public void putLongArray(String key, List<Long> value)
    {
        this.tagMap.put(key, new LongArrayNBT(value));
    }

    /**
     * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for false, using the given string key.
     */
    public void putBoolean(String key, boolean value)
    {
        this.tagMap.put(key, ByteNBT.valueOf(value));
    }

    @Nullable

    /**
     * gets a generic tag with the specified name
     */
    public INBT get(String key)
    {
        return this.tagMap.get(key);
    }

    /**
     * Gets the ID byte for the given tag key
     */
    public byte getTagId(String key)
    {
        INBT inbt = this.tagMap.get(key);
        return inbt == null ? 0 : inbt.getId();
    }

    /**
     * Returns whether the given string has been previously stored as a key in the map.
     */
    public boolean contains(String key)
    {
        return this.tagMap.containsKey(key);
    }

    /**
     * Returns whether the given string has been previously stored as a key in this tag compound as a particular type,
     * denoted by a parameter in the form of an ordinal. If the provided ordinal is 99, this method will match tag types
     * representing numbers.
     */
    public boolean contains(String key, int type)
    {
        int i = this.getTagId(key);

        if (i == type)
        {
            return true;
        }
        else if (type != 99)
        {
            return false;
        }
        else
        {
            return i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6;
        }
    }

    /**
     * Retrieves a byte value using the specified key, or 0 if no such key was stored.
     */
    public byte getByte(String key)
    {
        try
        {
            if (this.contains(key, 99))
            {
                return ((NumberNBT)this.tagMap.get(key)).getByte();
            }
        }
        catch (ClassCastException classcastexception)
        {
        }

        return 0;
    }

    /**
     * Retrieves a short value using the specified key, or 0 if no such key was stored.
     */
    public short getShort(String key)
    {
        try
        {
            if (this.contains(key, 99))
            {
                return ((NumberNBT)this.tagMap.get(key)).getShort();
            }
        }
        catch (ClassCastException classcastexception)
        {
        }

        return 0;
    }

    /**
     * Retrieves an integer value using the specified key, or 0 if no such key was stored.
     */
    public int getInt(String key)
    {
        try
        {
            if (this.contains(key, 99))
            {
                return ((NumberNBT)this.tagMap.get(key)).getInt();
            }
        }
        catch (ClassCastException classcastexception)
        {
        }

        return 0;
    }

    /**
     * Retrieves a long value using the specified key, or 0 if no such key was stored.
     */
    public long getLong(String key)
    {
        try
        {
            if (this.contains(key, 99))
            {
                return ((NumberNBT)this.tagMap.get(key)).getLong();
            }
        }
        catch (ClassCastException classcastexception)
        {
        }

        return 0L;
    }

    /**
     * Retrieves a float value using the specified key, or 0 if no such key was stored.
     */
    public float getFloat(String key)
    {
        try
        {
            if (this.contains(key, 99))
            {
                return ((NumberNBT)this.tagMap.get(key)).getFloat();
            }
        }
        catch (ClassCastException classcastexception)
        {
        }

        return 0.0F;
    }

    /**
     * Retrieves a double value using the specified key, or 0 if no such key was stored.
     */
    public double getDouble(String key)
    {
        try
        {
            if (this.contains(key, 99))
            {
                return ((NumberNBT)this.tagMap.get(key)).getDouble();
            }
        }
        catch (ClassCastException classcastexception)
        {
        }

        return 0.0D;
    }

    /**
     * Retrieves a string value using the specified key, or an empty string if no such key was stored.
     */
    public String getString(String key)
    {
        try
        {
            if (this.contains(key, 8))
            {
                return this.tagMap.get(key).getString();
            }
        }
        catch (ClassCastException classcastexception)
        {
        }

        return "";
    }

    /**
     * Retrieves a byte array using the specified key, or a zero-length array if no such key was stored.
     */
    public byte[] getByteArray(String key)
    {
        try
        {
            if (this.contains(key, 7))
            {
                return ((ByteArrayNBT)this.tagMap.get(key)).getByteArray();
            }
        }
        catch (ClassCastException classcastexception)
        {
            throw new ReportedException(this.generateCrashReport(key, ByteArrayNBT.TYPE, classcastexception));
        }

        return new byte[0];
    }

    /**
     * Retrieves an int array using the specified key, or a zero-length array if no such key was stored.
     */
    public int[] getIntArray(String key)
    {
        try
        {
            if (this.contains(key, 11))
            {
                return ((IntArrayNBT)this.tagMap.get(key)).getIntArray();
            }
        }
        catch (ClassCastException classcastexception)
        {
            throw new ReportedException(this.generateCrashReport(key, IntArrayNBT.TYPE, classcastexception));
        }

        return new int[0];
    }

    public long[] getLongArray(String key)
    {
        try
        {
            if (this.contains(key, 12))
            {
                return ((LongArrayNBT)this.tagMap.get(key)).getAsLongArray();
            }
        }
        catch (ClassCastException classcastexception)
        {
            throw new ReportedException(this.generateCrashReport(key, LongArrayNBT.TYPE, classcastexception));
        }

        return new long[0];
    }

    /**
     * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty NBTTagCompound if no such key was
     * stored.
     */
    public CompoundNBT getCompound(String key)
    {
        try
        {
            if (this.contains(key, 10))
            {
                return (CompoundNBT)this.tagMap.get(key);
            }
        }
        catch (ClassCastException classcastexception)
        {
            throw new ReportedException(this.generateCrashReport(key, TYPE, classcastexception));
        }

        return new CompoundNBT();
    }

    /**
     * Gets the NBTTagList object with the given name.
     */
    public ListNBT getList(String key, int type)
    {
        try
        {
            if (this.getTagId(key) == 9)
            {
                ListNBT listnbt = (ListNBT)this.tagMap.get(key);

                if (!listnbt.isEmpty() && listnbt.getTagType() != type)
                {
                    return new ListNBT();
                }

                return listnbt;
            }
        }
        catch (ClassCastException classcastexception)
        {
            throw new ReportedException(this.generateCrashReport(key, ListNBT.TYPE, classcastexception));
        }

        return new ListNBT();
    }

    /**
     * Retrieves a boolean value using the specified key, or false if no such key was stored. This uses the getByte
     * method.
     */
    public boolean getBoolean(String key)
    {
        return this.getByte(key) != 0;
    }

    /**
     * Remove the specified tag.
     */
    public void remove(String key)
    {
        this.tagMap.remove(key);
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder("{");
        Collection<String> collection = this.tagMap.keySet();

        if (LOGGER.isDebugEnabled())
        {
            List<String> list = Lists.newArrayList(this.tagMap.keySet());
            Collections.sort(list);
            collection = list;
        }

        for (String s : collection)
        {
            if (stringbuilder.length() != 1)
            {
                stringbuilder.append(',');
            }

            stringbuilder.append(handleEscape(s)).append(':').append(this.tagMap.get(s));
        }

        return stringbuilder.append('}').toString();
    }

    public boolean isEmpty()
    {
        return this.tagMap.isEmpty();
    }

    private CrashReport generateCrashReport(String tagName, INBTType<?> type, ClassCastException exception)
    {
        CrashReport crashreport = CrashReport.makeCrashReport(exception, "Reading NBT data");
        CrashReportCategory crashreportcategory = crashreport.makeCategoryDepth("Corrupt NBT tag", 1);
        crashreportcategory.addDetail("Tag type found", () ->
        {
            return this.tagMap.get(tagName).getType().getName();
        });
        crashreportcategory.addDetail("Tag type expected", type::getName);
        crashreportcategory.addDetail("Tag name", tagName);
        return crashreport;
    }

    /**
     * Creates a clone of the tag.
     */
    public CompoundNBT copy()
    {
        Map<String, INBT> map = Maps.newHashMap(Maps.transformValues(this.tagMap, INBT::copy));
        return new CompoundNBT(map);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else
        {
            return p_equals_1_ instanceof CompoundNBT && Objects.equals(this.tagMap, ((CompoundNBT)p_equals_1_).tagMap);
        }
    }

    public int hashCode()
    {
        return this.tagMap.hashCode();
    }

    private static void writeEntry(String name, INBT data, DataOutput output) throws IOException
    {
        output.writeByte(data.getId());

        if (data.getId() != 0)
        {
            output.writeUTF(name);
            data.write(output);
        }
    }

    private static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException
    {
        return input.readByte();
    }

    private static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException
    {
        return input.readUTF();
    }

    private static INBT loadNBT(INBTType<?> type, String name, DataInput input, int depth, NBTSizeTracker accounter)
    {
        try
        {
            return type.readNBT(input, depth, accounter);
        }
        catch (IOException ioexception)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(ioexception, "Loading NBT data");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("NBT Tag");
            crashreportcategory.addDetail("Tag name", name);
            crashreportcategory.addDetail("Tag type", type.getName());
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Deep copies all the tags of {@code other} into this tag, then returns itself.
     */
    public CompoundNBT merge(CompoundNBT other)
    {
        for (String s : other.tagMap.keySet())
        {
            INBT inbt = other.tagMap.get(s);

            if (inbt.getId() == 10)
            {
                if (this.contains(s, 10))
                {
                    CompoundNBT compoundnbt = this.getCompound(s);
                    compoundnbt.merge((CompoundNBT)inbt);
                }
                else
                {
                    this.put(s, inbt.copy());
                }
            }
            else
            {
                this.put(s, inbt.copy());
            }
        }

        return this;
    }

    protected static String handleEscape(String name)
    {
        return SIMPLE_VALUE.matcher(name).matches() ? name : StringNBT.quoteAndEscape(name);
    }

    protected static ITextComponent getNameComponent(String name)
    {
        if (SIMPLE_VALUE.matcher(name).matches())
        {
            return (new StringTextComponent(name)).mergeStyle(SYNTAX_HIGHLIGHTING_KEY);
        }
        else
        {
            String s = StringNBT.quoteAndEscape(name);
            String s1 = s.substring(0, 1);
            ITextComponent itextcomponent = (new StringTextComponent(s.substring(1, s.length() - 1))).mergeStyle(SYNTAX_HIGHLIGHTING_KEY);
            return (new StringTextComponent(s1)).append(itextcomponent).appendString(s1);
        }
    }

    public ITextComponent toFormattedComponent(String indentation, int indentDepth)
    {
        if (this.tagMap.isEmpty())
        {
            return new StringTextComponent("{}");
        }
        else
        {
            IFormattableTextComponent iformattabletextcomponent = new StringTextComponent("{");
            Collection<String> collection = this.tagMap.keySet();

            if (LOGGER.isDebugEnabled())
            {
                List<String> list = Lists.newArrayList(this.tagMap.keySet());
                Collections.sort(list);
                collection = list;
            }

            if (!indentation.isEmpty())
            {
                iformattabletextcomponent.appendString("\n");
            }

            IFormattableTextComponent iformattabletextcomponent1;

            for (Iterator<String> iterator = collection.iterator(); iterator.hasNext(); iformattabletextcomponent.append(iformattabletextcomponent1))
            {
                String s = iterator.next();
                iformattabletextcomponent1 = (new StringTextComponent(Strings.repeat(indentation, indentDepth + 1))).append(getNameComponent(s)).appendString(String.valueOf(':')).appendString(" ").append(this.tagMap.get(s).toFormattedComponent(indentation, indentDepth + 1));

                if (iterator.hasNext())
                {
                    iformattabletextcomponent1.appendString(String.valueOf(',')).appendString(indentation.isEmpty() ? " " : "\n");
                }
            }

            if (!indentation.isEmpty())
            {
                iformattabletextcomponent.appendString("\n").appendString(Strings.repeat(indentation, indentDepth));
            }

            iformattabletextcomponent.appendString("}");
            return iformattabletextcomponent;
        }
    }

    protected Map<String, INBT> getTagMap()
    {
        return Collections.unmodifiableMap(this.tagMap);
    }
}
