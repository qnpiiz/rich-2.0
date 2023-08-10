package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import net.minecraft.util.SharedSeedRandom;

public class MaxMinNoiseMixer
{
    private final double field_237208_a_;
    private final OctavesNoiseGenerator field_237209_b_;
    private final OctavesNoiseGenerator field_237210_c_;

    public static MaxMinNoiseMixer func_242930_a(SharedSeedRandom p_242930_0_, int p_242930_1_, DoubleList p_242930_2_)
    {
        return new MaxMinNoiseMixer(p_242930_0_, p_242930_1_, p_242930_2_);
    }

    private MaxMinNoiseMixer(SharedSeedRandom p_i242039_1_, int p_i242039_2_, DoubleList p_i242039_3_)
    {
        this.field_237209_b_ = OctavesNoiseGenerator.func_242932_a(p_i242039_1_, p_i242039_2_, p_i242039_3_);
        this.field_237210_c_ = OctavesNoiseGenerator.func_242932_a(p_i242039_1_, p_i242039_2_, p_i242039_3_);
        int i = Integer.MAX_VALUE;
        int j = Integer.MIN_VALUE;
        DoubleListIterator doublelistiterator = p_i242039_3_.iterator();

        while (doublelistiterator.hasNext())
        {
            int k = doublelistiterator.nextIndex();
            double d0 = doublelistiterator.nextDouble();

            if (d0 != 0.0D)
            {
                i = Math.min(i, k);
                j = Math.max(j, k);
            }
        }

        this.field_237208_a_ = 0.16666666666666666D / func_237212_a_(j - i);
    }

    private static double func_237212_a_(int p_237212_0_)
    {
        return 0.1D * (1.0D + 1.0D / (double)(p_237212_0_ + 1));
    }

    public double func_237211_a_(double p_237211_1_, double p_237211_3_, double p_237211_5_)
    {
        double d0 = p_237211_1_ * 1.0181268882175227D;
        double d1 = p_237211_3_ * 1.0181268882175227D;
        double d2 = p_237211_5_ * 1.0181268882175227D;
        return (this.field_237209_b_.func_205563_a(p_237211_1_, p_237211_3_, p_237211_5_) + this.field_237210_c_.func_205563_a(d0, d1, d2)) * this.field_237208_a_;
    }
}
