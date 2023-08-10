package net.optifine.util;

public class CounterInt
{
    private int startValue;
    private int value;

    public CounterInt(int startValue)
    {
        this.startValue = startValue;
        this.value = startValue;
    }

    public synchronized int nextValue()
    {
        return this.value++;
    }

    public synchronized void reset()
    {
        this.value = this.startValue;
    }

    public int getValue()
    {
        return this.value;
    }
}
