package net.minecraft.util;

import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class ArbitraryBitLengthIntArray
{
    private final long[] field_233043_a_;
    private final int field_233044_b_;
    private final long field_233045_c_;
    private final int field_233046_d_;

    public ArbitraryBitLengthIntArray(int p_i231442_1_, int p_i231442_2_)
    {
        this(p_i231442_1_, p_i231442_2_, new long[MathHelper.roundUp(p_i231442_2_ * p_i231442_1_, 64) / 64]);
    }

    public ArbitraryBitLengthIntArray(int p_i231443_1_, int p_i231443_2_, long[] p_i231443_3_)
    {
        Validate.inclusiveBetween(1L, 32L, (long)p_i231443_1_);
        this.field_233046_d_ = p_i231443_2_;
        this.field_233044_b_ = p_i231443_1_;
        this.field_233043_a_ = p_i231443_3_;
        this.field_233045_c_ = (1L << p_i231443_1_) - 1L;
        int i = MathHelper.roundUp(p_i231443_2_ * p_i231443_1_, 64) / 64;

        if (p_i231443_3_.length != i)
        {
            throw new IllegalArgumentException("Invalid length given for storage, got: " + p_i231443_3_.length + " but expected: " + i);
        }
    }

    public void func_233049_a_(int p_233049_1_, int p_233049_2_)
    {
        Validate.inclusiveBetween(0L, (long)(this.field_233046_d_ - 1), (long)p_233049_1_);
        Validate.inclusiveBetween(0L, this.field_233045_c_, (long)p_233049_2_);
        int i = p_233049_1_ * this.field_233044_b_;
        int j = i >> 6;
        int k = (p_233049_1_ + 1) * this.field_233044_b_ - 1 >> 6;
        int l = i ^ j << 6;
        this.field_233043_a_[j] = this.field_233043_a_[j] & ~(this.field_233045_c_ << l) | ((long)p_233049_2_ & this.field_233045_c_) << l;

        if (j != k)
        {
            int i1 = 64 - l;
            int j1 = this.field_233044_b_ - i1;
            this.field_233043_a_[k] = this.field_233043_a_[k] >>> j1 << j1 | ((long)p_233049_2_ & this.field_233045_c_) >> i1;
        }
    }

    public int func_233048_a_(int p_233048_1_)
    {
        Validate.inclusiveBetween(0L, (long)(this.field_233046_d_ - 1), (long)p_233048_1_);
        int i = p_233048_1_ * this.field_233044_b_;
        int j = i >> 6;
        int k = (p_233048_1_ + 1) * this.field_233044_b_ - 1 >> 6;
        int l = i ^ j << 6;

        if (j == k)
        {
            return (int)(this.field_233043_a_[j] >>> l & this.field_233045_c_);
        }
        else
        {
            int i1 = 64 - l;
            return (int)((this.field_233043_a_[j] >>> l | this.field_233043_a_[k] << i1) & this.field_233045_c_);
        }
    }

    public long[] func_233047_a_()
    {
        return this.field_233043_a_;
    }

    public int func_233050_b_()
    {
        return this.field_233044_b_;
    }
}
