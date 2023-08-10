package net.optifine.config;

public class RangeInt
{
    private int min;
    private int max;

    public RangeInt(int min, int max)
    {
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
    }

    public boolean isInRange(int val)
    {
        if (val < this.min)
        {
            return false;
        }
        else
        {
            return val <= this.max;
        }
    }

    public int getMin()
    {
        return this.min;
    }

    public int getMax()
    {
        return this.max;
    }

    public String toString()
    {
        return "min: " + this.min + ", max: " + this.max;
    }
}
