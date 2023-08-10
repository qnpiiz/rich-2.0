package net.minecraft.world.gen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.MathHelper;

public class OctavesNoiseGenerator implements INoiseGenerator
{
    private final ImprovedNoiseGenerator[] octaves;
    private final DoubleList field_242931_b;
    private final double field_227460_b_;
    private final double field_227461_c_;

    public OctavesNoiseGenerator(SharedSeedRandom p_i232142_1_, IntStream p_i232142_2_)
    {
        this(p_i232142_1_, p_i232142_2_.boxed().collect(ImmutableList.toImmutableList()));
    }

    public OctavesNoiseGenerator(SharedSeedRandom p_i232141_1_, List<Integer> p_i232141_2_)
    {
        this(p_i232141_1_, new IntRBTreeSet(p_i232141_2_));
    }

    public static OctavesNoiseGenerator func_242932_a(SharedSeedRandom p_242932_0_, int p_242932_1_, DoubleList p_242932_2_)
    {
        return new OctavesNoiseGenerator(p_242932_0_, Pair.of(p_242932_1_, p_242932_2_));
    }

    private static Pair<Integer, DoubleList> func_242933_a(IntSortedSet p_242933_0_)
    {
        if (p_242933_0_.isEmpty())
        {
            throw new IllegalArgumentException("Need some octaves!");
        }
        else
        {
            int i = -p_242933_0_.firstInt();
            int j = p_242933_0_.lastInt();
            int k = i + j + 1;

            if (k < 1)
            {
                throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
            }
            else
            {
                DoubleList doublelist = new DoubleArrayList(new double[k]);
                IntBidirectionalIterator intbidirectionaliterator = p_242933_0_.iterator();

                while (intbidirectionaliterator.hasNext())
                {
                    int l = intbidirectionaliterator.nextInt();
                    doublelist.set(l + i, 1.0D);
                }

                return Pair.of(-i, doublelist);
            }
        }
    }

    private OctavesNoiseGenerator(SharedSeedRandom p_i225879_1_, IntSortedSet p_i225879_2_)
    {
        this(p_i225879_1_, func_242933_a(p_i225879_2_));
    }

    private OctavesNoiseGenerator(SharedSeedRandom p_i242040_1_, Pair<Integer, DoubleList> p_i242040_2_)
    {
        int i = p_i242040_2_.getFirst();
        this.field_242931_b = p_i242040_2_.getSecond();
        ImprovedNoiseGenerator improvednoisegenerator = new ImprovedNoiseGenerator(p_i242040_1_);
        int j = this.field_242931_b.size();
        int k = -i;
        this.octaves = new ImprovedNoiseGenerator[j];

        if (k >= 0 && k < j)
        {
            double d0 = this.field_242931_b.getDouble(k);

            if (d0 != 0.0D)
            {
                this.octaves[k] = improvednoisegenerator;
            }
        }

        for (int i1 = k - 1; i1 >= 0; --i1)
        {
            if (i1 < j)
            {
                double d1 = this.field_242931_b.getDouble(i1);

                if (d1 != 0.0D)
                {
                    this.octaves[i1] = new ImprovedNoiseGenerator(p_i242040_1_);
                }
                else
                {
                    p_i242040_1_.skip(262);
                }
            }
            else
            {
                p_i242040_1_.skip(262);
            }
        }

        if (k < j - 1)
        {
            long j1 = (long)(improvednoisegenerator.func_215456_a(0.0D, 0.0D, 0.0D, 0.0D, 0.0D) * (double)9.223372E18F);
            SharedSeedRandom sharedseedrandom = new SharedSeedRandom(j1);

            for (int l = k + 1; l < j; ++l)
            {
                if (l >= 0)
                {
                    double d2 = this.field_242931_b.getDouble(l);

                    if (d2 != 0.0D)
                    {
                        this.octaves[l] = new ImprovedNoiseGenerator(sharedseedrandom);
                    }
                    else
                    {
                        sharedseedrandom.skip(262);
                    }
                }
                else
                {
                    sharedseedrandom.skip(262);
                }
            }
        }

        this.field_227461_c_ = Math.pow(2.0D, (double)(-k));
        this.field_227460_b_ = Math.pow(2.0D, (double)(j - 1)) / (Math.pow(2.0D, (double)j) - 1.0D);
    }

    public double func_205563_a(double p_205563_1_, double p_205563_3_, double p_205563_5_)
    {
        return this.getValue(p_205563_1_, p_205563_3_, p_205563_5_, 0.0D, 0.0D, false);
    }

    public double getValue(double x, double y, double z, double p_215462_7_, double p_215462_9_, boolean p_215462_11_)
    {
        double d0 = 0.0D;
        double d1 = this.field_227461_c_;
        double d2 = this.field_227460_b_;

        for (int i = 0; i < this.octaves.length; ++i)
        {
            ImprovedNoiseGenerator improvednoisegenerator = this.octaves[i];

            if (improvednoisegenerator != null)
            {
                d0 += this.field_242931_b.getDouble(i) * improvednoisegenerator.func_215456_a(maintainPrecision(x * d1), p_215462_11_ ? -improvednoisegenerator.yCoord : maintainPrecision(y * d1), maintainPrecision(z * d1), p_215462_7_ * d1, p_215462_9_ * d1) * d2;
            }

            d1 *= 2.0D;
            d2 /= 2.0D;
        }

        return d0;
    }

    @Nullable
    public ImprovedNoiseGenerator getOctave(int octaveIndex)
    {
        return this.octaves[this.octaves.length - 1 - octaveIndex];
    }

    public static double maintainPrecision(double p_215461_0_)
    {
        return p_215461_0_ - (double)MathHelper.lfloor(p_215461_0_ / 3.3554432E7D + 0.5D) * 3.3554432E7D;
    }

    public double noiseAt(double x, double y, double z, double p_215460_7_)
    {
        return this.getValue(x, y, 0.0D, z, p_215460_7_, false);
    }
}
