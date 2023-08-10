package net.minecraft.nbt;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractStringBuilder;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class NBTDynamicOps implements DynamicOps<INBT>
{
    public static final NBTDynamicOps INSTANCE = new NBTDynamicOps();

    protected NBTDynamicOps()
    {
    }

    public INBT empty()
    {
        return EndNBT.INSTANCE;
    }

    public <U> U convertTo(DynamicOps<U> p_convertTo_1_, INBT p_convertTo_2_)
    {
        switch (p_convertTo_2_.getId())
        {
            case 0:
                return p_convertTo_1_.empty();

            case 1:
                return p_convertTo_1_.createByte(((NumberNBT)p_convertTo_2_).getByte());

            case 2:
                return p_convertTo_1_.createShort(((NumberNBT)p_convertTo_2_).getShort());

            case 3:
                return p_convertTo_1_.createInt(((NumberNBT)p_convertTo_2_).getInt());

            case 4:
                return p_convertTo_1_.createLong(((NumberNBT)p_convertTo_2_).getLong());

            case 5:
                return p_convertTo_1_.createFloat(((NumberNBT)p_convertTo_2_).getFloat());

            case 6:
                return p_convertTo_1_.createDouble(((NumberNBT)p_convertTo_2_).getDouble());

            case 7:
                return p_convertTo_1_.createByteList(ByteBuffer.wrap(((ByteArrayNBT)p_convertTo_2_).getByteArray()));

            case 8:
                return p_convertTo_1_.createString(p_convertTo_2_.getString());

            case 9:
                return this.convertList(p_convertTo_1_, p_convertTo_2_);

            case 10:
                return this.convertMap(p_convertTo_1_, p_convertTo_2_);

            case 11:
                return p_convertTo_1_.createIntList(Arrays.stream(((IntArrayNBT)p_convertTo_2_).getIntArray()));

            case 12:
                return p_convertTo_1_.createLongList(Arrays.stream(((LongArrayNBT)p_convertTo_2_).getAsLongArray()));

            default:
                throw new IllegalStateException("Unknown tag type: " + p_convertTo_2_);
        }
    }

    public DataResult<Number> getNumberValue(INBT p_getNumberValue_1_)
    {
        return p_getNumberValue_1_ instanceof NumberNBT ? DataResult.success(((NumberNBT)p_getNumberValue_1_).getAsNumber()) : DataResult.error("Not a number");
    }

    public INBT createNumeric(Number p_createNumeric_1_)
    {
        return DoubleNBT.valueOf(p_createNumeric_1_.doubleValue());
    }

    public INBT createByte(byte p_createByte_1_)
    {
        return ByteNBT.valueOf(p_createByte_1_);
    }

    public INBT createShort(short p_createShort_1_)
    {
        return ShortNBT.valueOf(p_createShort_1_);
    }

    public INBT createInt(int p_createInt_1_)
    {
        return IntNBT.valueOf(p_createInt_1_);
    }

    public INBT createLong(long p_createLong_1_)
    {
        return LongNBT.valueOf(p_createLong_1_);
    }

    public INBT createFloat(float p_createFloat_1_)
    {
        return FloatNBT.valueOf(p_createFloat_1_);
    }

    public INBT createDouble(double p_createDouble_1_)
    {
        return DoubleNBT.valueOf(p_createDouble_1_);
    }

    public INBT createBoolean(boolean p_createBoolean_1_)
    {
        return ByteNBT.valueOf(p_createBoolean_1_);
    }

    public DataResult<String> getStringValue(INBT p_getStringValue_1_)
    {
        return p_getStringValue_1_ instanceof StringNBT ? DataResult.success(p_getStringValue_1_.getString()) : DataResult.error("Not a string");
    }

    public INBT createString(String p_createString_1_)
    {
        return StringNBT.valueOf(p_createString_1_);
    }

    private static CollectionNBT<?> func_240602_a_(byte p_240602_0_, byte p_240602_1_)
    {
        if (func_240603_a_(p_240602_0_, p_240602_1_, (byte)4))
        {
            return new LongArrayNBT(new long[0]);
        }
        else if (func_240603_a_(p_240602_0_, p_240602_1_, (byte)1))
        {
            return new ByteArrayNBT(new byte[0]);
        }
        else
        {
            return (CollectionNBT<?>)(func_240603_a_(p_240602_0_, p_240602_1_, (byte)3) ? new IntArrayNBT(new int[0]) : new ListNBT());
        }
    }

    private static boolean func_240603_a_(byte p_240603_0_, byte p_240603_1_, byte p_240603_2_)
    {
        return p_240603_0_ == p_240603_2_ && (p_240603_1_ == p_240603_2_ || p_240603_1_ == 0);
    }

    private static <T extends INBT> void func_240609_a_(CollectionNBT<T> p_240609_0_, INBT p_240609_1_, INBT p_240609_2_)
    {
        if (p_240609_1_ instanceof CollectionNBT)
        {
            CollectionNBT<T> collectionnbt = (CollectionNBT<T>)p_240609_1_;
            collectionnbt.forEach((p_240616_1_) ->
            {
                p_240609_0_.add(p_240616_1_);
            });
        }

        p_240609_0_.add((T)p_240609_2_);
    }

    private static <T extends INBT> void func_240608_a_(CollectionNBT<T> p_240608_0_, INBT p_240608_1_, List<INBT> p_240608_2_)
    {
        if (p_240608_1_ instanceof CollectionNBT)
        {
            CollectionNBT<T> collectionnbt = (CollectionNBT<T>)p_240608_1_;
            collectionnbt.forEach((p_240614_1_) ->
            {
                p_240608_0_.add(p_240614_1_);
            });
        }

        ((List<T>)p_240608_2_).forEach((p_240607_1_) ->
        {
            p_240608_0_.add(p_240607_1_);
        });
    }

    public DataResult<INBT> mergeToList(INBT p_mergeToList_1_, INBT p_mergeToList_2_)
    {
        if (!(p_mergeToList_1_ instanceof CollectionNBT) && !(p_mergeToList_1_ instanceof EndNBT))
        {
            return DataResult.error("mergeToList called with not a list: " + p_mergeToList_1_, p_mergeToList_1_);
        }
        else
        {
            CollectionNBT<?> collectionnbt = func_240602_a_(p_mergeToList_1_ instanceof CollectionNBT ? ((CollectionNBT)p_mergeToList_1_).getTagType() : 0, p_mergeToList_2_.getId());
            func_240609_a_(collectionnbt, p_mergeToList_1_, p_mergeToList_2_);
            return DataResult.success(collectionnbt);
        }
    }

    public DataResult<INBT> mergeToList(INBT p_mergeToList_1_, List<INBT> p_mergeToList_2_)
    {
        if (!(p_mergeToList_1_ instanceof CollectionNBT) && !(p_mergeToList_1_ instanceof EndNBT))
        {
            return DataResult.error("mergeToList called with not a list: " + p_mergeToList_1_, p_mergeToList_1_);
        }
        else
        {
            CollectionNBT<?> collectionnbt = func_240602_a_(p_mergeToList_1_ instanceof CollectionNBT ? ((CollectionNBT)p_mergeToList_1_).getTagType() : 0, p_mergeToList_2_.stream().findFirst().map(INBT::getId).orElse((byte)0));
            func_240608_a_(collectionnbt, p_mergeToList_1_, p_mergeToList_2_);
            return DataResult.success(collectionnbt);
        }
    }

    public DataResult<INBT> mergeToMap(INBT p_mergeToMap_1_, INBT p_mergeToMap_2_, INBT p_mergeToMap_3_)
    {
        if (!(p_mergeToMap_1_ instanceof CompoundNBT) && !(p_mergeToMap_1_ instanceof EndNBT))
        {
            return DataResult.error("mergeToMap called with not a map: " + p_mergeToMap_1_, p_mergeToMap_1_);
        }
        else if (!(p_mergeToMap_2_ instanceof StringNBT))
        {
            return DataResult.error("key is not a string: " + p_mergeToMap_2_, p_mergeToMap_1_);
        }
        else
        {
            CompoundNBT compoundnbt = new CompoundNBT();

            if (p_mergeToMap_1_ instanceof CompoundNBT)
            {
                CompoundNBT compoundnbt1 = (CompoundNBT)p_mergeToMap_1_;
                compoundnbt1.keySet().forEach((p_240617_2_) ->
                {
                    compoundnbt.put(p_240617_2_, compoundnbt1.get(p_240617_2_));
                });
            }

            compoundnbt.put(p_mergeToMap_2_.getString(), p_mergeToMap_3_);
            return DataResult.success(compoundnbt);
        }
    }

    public DataResult<INBT> mergeToMap(INBT p_mergeToMap_1_, MapLike<INBT> p_mergeToMap_2_)
    {
        if (!(p_mergeToMap_1_ instanceof CompoundNBT) && !(p_mergeToMap_1_ instanceof EndNBT))
        {
            return DataResult.error("mergeToMap called with not a map: " + p_mergeToMap_1_, p_mergeToMap_1_);
        }
        else
        {
            CompoundNBT compoundnbt = new CompoundNBT();

            if (p_mergeToMap_1_ instanceof CompoundNBT)
            {
                CompoundNBT compoundnbt1 = (CompoundNBT)p_mergeToMap_1_;
                compoundnbt1.keySet().forEach((p_240615_2_) ->
                {
                    compoundnbt.put(p_240615_2_, compoundnbt1.get(p_240615_2_));
                });
            }

            List<INBT> list = Lists.newArrayList();
            p_mergeToMap_2_.entries().forEach((p_240605_2_) ->
            {
                INBT inbt = p_240605_2_.getFirst();

                if (!(inbt instanceof StringNBT))
                {
                    list.add(inbt);
                }
                else {
                    compoundnbt.put(inbt.getString(), p_240605_2_.getSecond());
                }
            });
            return !list.isEmpty() ? DataResult.error("some keys are not strings: " + list, compoundnbt) : DataResult.success(compoundnbt);
        }
    }

    public DataResult<Stream<Pair<INBT, INBT>>> getMapValues(INBT p_getMapValues_1_)
    {
        if (!(p_getMapValues_1_ instanceof CompoundNBT))
        {
            return DataResult.error("Not a map: " + p_getMapValues_1_);
        }
        else
        {
            CompoundNBT compoundnbt = (CompoundNBT)p_getMapValues_1_;
            return DataResult.success(compoundnbt.keySet().stream().map((p_240611_2_) ->
            {
                return Pair.of(this.createString(p_240611_2_), compoundnbt.get(p_240611_2_));
            }));
        }
    }

    public DataResult<Consumer<BiConsumer<INBT, INBT>>> getMapEntries(INBT p_getMapEntries_1_)
    {
        if (!(p_getMapEntries_1_ instanceof CompoundNBT))
        {
            return DataResult.error("Not a map: " + p_getMapEntries_1_);
        }
        else
        {
            CompoundNBT compoundnbt = (CompoundNBT)p_getMapEntries_1_;
            return DataResult.success((p_240612_2_) ->
            {
                compoundnbt.keySet().forEach((p_240606_3_) -> {
                    p_240612_2_.accept(this.createString(p_240606_3_), compoundnbt.get(p_240606_3_));
                });
            });
        }
    }

    public DataResult<MapLike<INBT>> getMap(INBT p_getMap_1_)
    {
        if (!(p_getMap_1_ instanceof CompoundNBT))
        {
            return DataResult.error("Not a map: " + p_getMap_1_);
        }
        else
        {
            final CompoundNBT compoundnbt = (CompoundNBT)p_getMap_1_;
            return DataResult.success(new MapLike<INBT>()
            {
                @Nullable
                public INBT get(INBT p_get_1_)
                {
                    return compoundnbt.get(p_get_1_.getString());
                }
                @Nullable
                public INBT get(String p_get_1_)
                {
                    return compoundnbt.get(p_get_1_);
                }
                public Stream<Pair<INBT, INBT>> entries()
                {
                    return compoundnbt.keySet().stream().map((p_240624_2_) ->
                    {
                        return Pair.of(NBTDynamicOps.this.createString(p_240624_2_), compoundnbt.get(p_240624_2_));
                    });
                }
                public String toString()
                {
                    return "MapLike[" + compoundnbt + "]";
                }
            });
        }
    }

    public INBT createMap(Stream<Pair<INBT, INBT>> p_createMap_1_)
    {
        CompoundNBT compoundnbt = new CompoundNBT();
        p_createMap_1_.forEach((p_240610_1_) ->
        {
            compoundnbt.put(p_240610_1_.getFirst().getString(), p_240610_1_.getSecond());
        });
        return compoundnbt;
    }

    public DataResult<Stream<INBT>> getStream(INBT p_getStream_1_)
    {
        return p_getStream_1_ instanceof CollectionNBT ? DataResult.success(((CollectionNBT)p_getStream_1_).stream().map((p_240621_0_) ->
        {
            return p_240621_0_;
        })) : DataResult.error("Not a list");
    }

    public DataResult<Consumer<Consumer<INBT>>> getList(INBT p_getList_1_)
    {
        if (p_getList_1_ instanceof CollectionNBT)
        {
            CollectionNBT<?> collectionnbt = (CollectionNBT)p_getList_1_;
            return DataResult.success(collectionnbt::forEach);
        }
        else
        {
            return DataResult.error("Not a list: " + p_getList_1_);
        }
    }

    public DataResult<ByteBuffer> getByteBuffer(INBT p_getByteBuffer_1_)
    {
        return p_getByteBuffer_1_ instanceof ByteArrayNBT ? DataResult.success(ByteBuffer.wrap(((ByteArrayNBT)p_getByteBuffer_1_).getByteArray())) : DynamicOps.super.getByteBuffer(p_getByteBuffer_1_);
    }

    public INBT createByteList(ByteBuffer p_createByteList_1_)
    {
        return new ByteArrayNBT(DataFixUtils.toArray(p_createByteList_1_));
    }

    public DataResult<IntStream> getIntStream(INBT p_getIntStream_1_)
    {
        return p_getIntStream_1_ instanceof IntArrayNBT ? DataResult.success(Arrays.stream(((IntArrayNBT)p_getIntStream_1_).getIntArray())) : DynamicOps.super.getIntStream(p_getIntStream_1_);
    }

    public INBT createIntList(IntStream p_createIntList_1_)
    {
        return new IntArrayNBT(p_createIntList_1_.toArray());
    }

    public DataResult<LongStream> getLongStream(INBT p_getLongStream_1_)
    {
        return p_getLongStream_1_ instanceof LongArrayNBT ? DataResult.success(Arrays.stream(((LongArrayNBT)p_getLongStream_1_).getAsLongArray())) : DynamicOps.super.getLongStream(p_getLongStream_1_);
    }

    public INBT createLongList(LongStream p_createLongList_1_)
    {
        return new LongArrayNBT(p_createLongList_1_.toArray());
    }

    public INBT createList(Stream<INBT> p_createList_1_)
    {
        PeekingIterator<INBT> peekingiterator = Iterators.peekingIterator(p_createList_1_.iterator());

        if (!peekingiterator.hasNext())
        {
            return new ListNBT();
        }
        else
        {
            INBT inbt = peekingiterator.peek();

            if (inbt instanceof ByteNBT)
            {
                List<Byte> list2 = Lists.newArrayList(Iterators.transform(peekingiterator, (p_210815_0_) ->
                {
                    return ((ByteNBT)p_210815_0_).getByte();
                }));
                return new ByteArrayNBT(list2);
            }
            else if (inbt instanceof IntNBT)
            {
                List<Integer> list1 = Lists.newArrayList(Iterators.transform(peekingiterator, (p_210818_0_) ->
                {
                    return ((IntNBT)p_210818_0_).getInt();
                }));
                return new IntArrayNBT(list1);
            }
            else if (inbt instanceof LongNBT)
            {
                List<Long> list = Lists.newArrayList(Iterators.transform(peekingiterator, (p_210816_0_) ->
                {
                    return ((LongNBT)p_210816_0_).getLong();
                }));
                return new LongArrayNBT(list);
            }
            else
            {
                ListNBT listnbt = new ListNBT();

                while (peekingiterator.hasNext())
                {
                    INBT inbt1 = peekingiterator.next();

                    if (!(inbt1 instanceof EndNBT))
                    {
                        listnbt.add(inbt1);
                    }
                }

                return listnbt;
            }
        }
    }

    public INBT remove(INBT p_remove_1_, String p_remove_2_)
    {
        if (p_remove_1_ instanceof CompoundNBT)
        {
            CompoundNBT compoundnbt = (CompoundNBT)p_remove_1_;
            CompoundNBT compoundnbt1 = new CompoundNBT();
            compoundnbt.keySet().stream().filter((p_212019_1_) ->
            {
                return !Objects.equals(p_212019_1_, p_remove_2_);
            }).forEach((p_212010_2_) ->
            {
                compoundnbt1.put(p_212010_2_, compoundnbt.get(p_212010_2_));
            });
            return compoundnbt1;
        }
        else
        {
            return p_remove_1_;
        }
    }

    public String toString()
    {
        return "NBT";
    }

    public RecordBuilder<INBT> mapBuilder()
    {
        return new NBTDynamicOps.NBTRecordBuilder();
    }

    class NBTRecordBuilder extends AbstractStringBuilder<INBT, CompoundNBT>
    {
        protected NBTRecordBuilder()
        {
            super(NBTDynamicOps.this);
        }

        protected CompoundNBT initBuilder()
        {
            return new CompoundNBT();
        }

        protected CompoundNBT append(String p_append_1_, INBT p_append_2_, CompoundNBT p_append_3_)
        {
            p_append_3_.put(p_append_1_, p_append_2_);
            return p_append_3_;
        }

        protected DataResult<INBT> build(CompoundNBT p_build_1_, INBT p_build_2_)
        {
            if (p_build_2_ != null && p_build_2_ != EndNBT.INSTANCE)
            {
                if (!(p_build_2_ instanceof CompoundNBT))
                {
                    return DataResult.error("mergeToMap called with not a map: " + p_build_2_, p_build_2_);
                }
                else
                {
                    CompoundNBT compoundnbt = new CompoundNBT(Maps.newHashMap(((CompoundNBT)p_build_2_).getTagMap()));

                    for (Entry<String, INBT> entry : p_build_1_.getTagMap().entrySet())
                    {
                        compoundnbt.put(entry.getKey(), entry.getValue());
                    }

                    return DataResult.success(compoundnbt);
                }
            }
            else
            {
                return DataResult.success(p_build_1_);
            }
        }
    }
}
