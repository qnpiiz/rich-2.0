package net.minecraft.util;

public class IntArray implements IIntArray
{
    private final int[] array;

    public IntArray(int size)
    {
        this.array = new int[size];
    }

    public int get(int index)
    {
        return this.array[index];
    }

    public void set(int index, int value)
    {
        this.array[index] = value;
    }

    public int size()
    {
        return this.array.length;
    }
}
