package net.minecraft.util;

public abstract class IntReferenceHolder
{
    private int lastKnownValue;

    public static IntReferenceHolder create(final IIntArray data, final int idx)
    {
        return new IntReferenceHolder()
        {
            public int get()
            {
                return data.get(idx);
            }
            public void set(int value)
            {
                data.set(idx, value);
            }
        };
    }

    public static IntReferenceHolder create(final int[] data, final int idx)
    {
        return new IntReferenceHolder()
        {
            public int get()
            {
                return data[idx];
            }
            public void set(int value)
            {
                data[idx] = value;
            }
        };
    }

    public static IntReferenceHolder single()
    {
        return new IntReferenceHolder()
        {
            private int value;
            public int get()
            {
                return this.value;
            }
            public void set(int value)
            {
                this.value = value;
            }
        };
    }

    public abstract int get();

    public abstract void set(int value);

    public boolean isDirty()
    {
        int i = this.get();
        boolean flag = i != this.lastKnownValue;
        this.lastKnownValue = i;
        return flag;
    }
}
