package net.minecraft.util;

import java.util.Random;

public class RangedInteger
{
    private final int minInclusive;
    private final int max;

    public RangedInteger(int minInclusive, int max)
    {
        if (max < minInclusive)
        {
            throw new IllegalArgumentException("max must be >= minInclusive! Given minInclusive: " + minInclusive + ", Given max: " + max);
        }
        else
        {
            this.minInclusive = minInclusive;
            this.max = max;
        }
    }

    public static RangedInteger createRangedInteger(int minInclusive, int max)
    {
        return new RangedInteger(minInclusive, max);
    }

    public int getRandomWithinRange(Random rand)
    {
        return this.minInclusive == this.max ? this.minInclusive : rand.nextInt(this.max - this.minInclusive + 1) + this.minInclusive;
    }

    public int getMinInclusive()
    {
        return this.minInclusive;
    }

    public int getMax()
    {
        return this.max;
    }

    public String toString()
    {
        return "IntRange[" + this.minInclusive + "-" + this.max + "]";
    }
}
