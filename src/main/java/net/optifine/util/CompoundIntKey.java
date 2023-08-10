package net.optifine.util;

import net.optifine.Config;

public class CompoundIntKey
{
    private int[] keys;
    private int hashcode = 0;

    public CompoundIntKey(int[] keys)
    {
        this.keys = (int[])keys.clone();
    }

    public CompoundIntKey(int k1, int k2)
    {
        this(new int[] {k1, k2});
    }

    public CompoundIntKey(int k1, int k2, int k3)
    {
        this(new int[] {k1, k2, k3});
    }

    public CompoundIntKey(int k1, int k2, int k3, int k4)
    {
        this(new int[] {k1, k2, k3, k4});
    }

    public int hashCode()
    {
        if (this.hashcode == 0)
        {
            this.hashcode = 7;

            for (int i = 0; i < this.keys.length; ++i)
            {
                int j = this.keys[i];
                this.hashcode = 31 * this.hashcode + j;
            }
        }

        return this.hashcode;
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        else if (obj == this)
        {
            return true;
        }
        else if (!(obj instanceof CompoundIntKey))
        {
            return false;
        }
        else
        {
            CompoundIntKey compoundintkey = (CompoundIntKey)obj;
            int[] aint = compoundintkey.getKeys();

            if (aint.length != this.keys.length)
            {
                return false;
            }
            else
            {
                for (int i = 0; i < this.keys.length; ++i)
                {
                    if (this.keys[i] != aint[i])
                    {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    private int[] getKeys()
    {
        return this.keys;
    }

    public int[] getKeysCopy()
    {
        return (int[])this.keys.clone();
    }

    public String toString()
    {
        return "[" + Config.arrayToString(this.keys) + "]";
    }
}
