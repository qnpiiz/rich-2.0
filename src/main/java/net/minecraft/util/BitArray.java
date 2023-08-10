package net.minecraft.util;

import java.util.function.IntConsumer;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class BitArray
{
    private static final int[] field_232981_a_ = new int[] { -1, -1, 0, Integer.MIN_VALUE, 0, 0, 1431655765, 1431655765, 0, Integer.MIN_VALUE, 0, 1, 858993459, 858993459, 0, 715827882, 715827882, 0, 613566756, 613566756, 0, Integer.MIN_VALUE, 0, 2, 477218588, 477218588, 0, 429496729, 429496729, 0, 390451572, 390451572, 0, 357913941, 357913941, 0, 330382099, 330382099, 0, 306783378, 306783378, 0, 286331153, 286331153, 0, Integer.MIN_VALUE, 0, 3, 252645135, 252645135, 0, 238609294, 238609294, 0, 226050910, 226050910, 0, 214748364, 214748364, 0, 204522252, 204522252, 0, 195225786, 195225786, 0, 186737708, 186737708, 0, 178956970, 178956970, 0, 171798691, 171798691, 0, 165191049, 165191049, 0, 159072862, 159072862, 0, 153391689, 153391689, 0, 148102320, 148102320, 0, 143165576, 143165576, 0, 138547332, 138547332, 0, Integer.MIN_VALUE, 0, 4, 130150524, 130150524, 0, 126322567, 126322567, 0, 122713351, 122713351, 0, 119304647, 119304647, 0, 116080197, 116080197, 0, 113025455, 113025455, 0, 110127366, 110127366, 0, 107374182, 107374182, 0, 104755299, 104755299, 0, 102261126, 102261126, 0, 99882960, 99882960, 0, 97612893, 97612893, 0, 95443717, 95443717, 0, 93368854, 93368854, 0, 91382282, 91382282, 0, 89478485, 89478485, 0, 87652393, 87652393, 0, 85899345, 85899345, 0, 84215045, 84215045, 0, 82595524, 82595524, 0, 81037118, 81037118, 0, 79536431, 79536431, 0, 78090314, 78090314, 0, 76695844, 76695844, 0, 75350303, 75350303, 0, 74051160, 74051160, 0, 72796055, 72796055, 0, 71582788, 71582788, 0, 70409299, 70409299, 0, 69273666, 69273666, 0, 68174084, 68174084, 0, Integer.MIN_VALUE, 0, 5};
    private final long[] longArray;
    private final int bitsPerEntry;
    private final long maxEntryValue;
    private final int arraySize;
    private final int field_232982_f_;
    private final int field_232983_g_;
    private final int field_232984_h_;
    private final int field_232985_i_;

    public BitArray(int bitsPerEntryIn, int arraySizeIn)
    {
        this(bitsPerEntryIn, arraySizeIn, (long[])null);
    }

    public BitArray(int bitsPerEntryIn, int arraySizeIn, @Nullable long[] data)
    {
        Validate.inclusiveBetween(1L, 32L, (long)bitsPerEntryIn);
        this.arraySize = arraySizeIn;
        this.bitsPerEntry = bitsPerEntryIn;
        this.maxEntryValue = (1L << bitsPerEntryIn) - 1L;
        this.field_232982_f_ = (char)(64 / bitsPerEntryIn);
        int i = 3 * (this.field_232982_f_ - 1);
        this.field_232983_g_ = field_232981_a_[i + 0];
        this.field_232984_h_ = field_232981_a_[i + 1];
        this.field_232985_i_ = field_232981_a_[i + 2];
        int j = (arraySizeIn + this.field_232982_f_ - 1) / this.field_232982_f_;

        if (data != null)
        {
            if (data.length != j)
            {
                throw(RuntimeException)Util.pauseDevMode(new RuntimeException("Invalid length given for storage, got: " + data.length + " but expected: " + j));
            }

            this.longArray = data;
        }
        else
        {
            this.longArray = new long[j];
        }
    }

    private int func_232986_b_(int p_232986_1_)
    {
        long i = Integer.toUnsignedLong(this.field_232983_g_);
        long j = Integer.toUnsignedLong(this.field_232984_h_);
        return (int)((long)p_232986_1_ * i + j >> 32 >> this.field_232985_i_);
    }

    public int swapAt(int index, int value)
    {
        Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)index);
        Validate.inclusiveBetween(0L, this.maxEntryValue, (long)value);
        int i = this.func_232986_b_(index);
        long j = this.longArray[i];
        int k = (index - i * this.field_232982_f_) * this.bitsPerEntry;
        int l = (int)(j >> k & this.maxEntryValue);
        this.longArray[i] = j & ~(this.maxEntryValue << k) | ((long)value & this.maxEntryValue) << k;
        return l;
    }

    /**
     * Sets the entry at the given location to the given value
     */
    public void setAt(int index, int value)
    {
        Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)index);
        Validate.inclusiveBetween(0L, this.maxEntryValue, (long)value);
        int i = this.func_232986_b_(index);
        long j = this.longArray[i];
        int k = (index - i * this.field_232982_f_) * this.bitsPerEntry;
        this.longArray[i] = j & ~(this.maxEntryValue << k) | ((long)value & this.maxEntryValue) << k;
    }

    /**
     * Gets the entry at the given index
     */
    public int getAt(int index)
    {
        Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)index);
        int i = this.func_232986_b_(index);
        long j = this.longArray[i];
        int k = (index - i * this.field_232982_f_) * this.bitsPerEntry;
        return (int)(j >> k & this.maxEntryValue);
    }

    /**
     * Gets the long array that is used to store the data in this BitArray. This is useful for sending packet data.
     */
    public long[] getBackingLongArray()
    {
        return this.longArray;
    }

    public int size()
    {
        return this.arraySize;
    }

    public void getAll(IntConsumer consumer)
    {
        int i = 0;

        for (long j : this.longArray)
        {
            for (int k = 0; k < this.field_232982_f_; ++k)
            {
                consumer.accept((int)(j & this.maxEntryValue));
                j >>= this.bitsPerEntry;
                ++i;

                if (i >= this.arraySize)
                {
                    return;
                }
            }
        }
    }
}
