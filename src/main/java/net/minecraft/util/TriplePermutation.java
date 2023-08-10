package net.minecraft.util;

import java.util.Arrays;
import net.minecraft.util.math.vector.Matrix3f;

public enum TriplePermutation
{
    P123(0, 1, 2),
    P213(1, 0, 2),
    P132(0, 2, 1),
    P231(1, 2, 0),
    P312(2, 0, 1),
    P321(2, 1, 0);

    private final int[] field_239183_g_;
    private final Matrix3f field_239184_h_;
    private static final TriplePermutation[][] field_239185_i_ = Util.make(new TriplePermutation[values().length][values().length], (p_239190_0_) -> {
        for (TriplePermutation triplepermutation : values())
        {
            for (TriplePermutation triplepermutation1 : values())
            {
                int[] aint = new int[3];

                for (int i = 0; i < 3; ++i)
                {
                    aint[i] = triplepermutation.field_239183_g_[triplepermutation1.field_239183_g_[i]];
                }

                TriplePermutation triplepermutation2 = Arrays.stream(values()).filter((p_239189_1_) ->
                {
                    return Arrays.equals(p_239189_1_.field_239183_g_, aint);
                }).findFirst().get();
                p_239190_0_[triplepermutation.ordinal()][triplepermutation1.ordinal()] = triplepermutation2;
            }
        }
    });

    private TriplePermutation(int p_i232416_3_, int p_i232416_4_, int p_i232416_5_)
    {
        this.field_239183_g_ = new int[] {p_i232416_3_, p_i232416_4_, p_i232416_5_};
        this.field_239184_h_ = new Matrix3f();
        this.field_239184_h_.func_232605_a_(0, this.func_239187_a_(0), 1.0F);
        this.field_239184_h_.func_232605_a_(1, this.func_239187_a_(1), 1.0F);
        this.field_239184_h_.func_232605_a_(2, this.func_239187_a_(2), 1.0F);
    }

    public TriplePermutation func_239188_a_(TriplePermutation p_239188_1_)
    {
        return field_239185_i_[this.ordinal()][p_239188_1_.ordinal()];
    }

    public int func_239187_a_(int p_239187_1_)
    {
        return this.field_239183_g_[p_239187_1_];
    }

    public Matrix3f func_239186_a_()
    {
        return this.field_239184_h_;
    }
}
