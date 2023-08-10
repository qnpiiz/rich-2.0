package net.optifine.util;

import net.optifine.Config;

public class CompoundKey
{
    private Object[] keys;
    private int hashcode = 0;

    public CompoundKey(Object[] keys)
    {
        this.keys = keys.clone();
    }

    public CompoundKey(Object k1, Object k2)
    {
        this(new Object[] {k1, k2});
    }

    public CompoundKey(Object k1, Object k2, Object k3)
    {
        this(new Object[] {k1, k2, k3});
    }

    public CompoundKey(Object k1, Object k2, Object k3, Object k4)
    {
        this(new Object[] {k1, k2, k3, k4});
    }

    public int hashCode()
    {
        if (this.hashcode == 0)
        {
            this.hashcode = 7;

            for (int i = 0; i < this.keys.length; ++i)
            {
                Object object = this.keys[i];

                if (object != null)
                {
                    this.hashcode = 31 * this.hashcode + object.hashCode();
                }
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
        else if (!(obj instanceof CompoundKey))
        {
            return false;
        }
        else
        {
            CompoundKey compoundkey = (CompoundKey)obj;
            Object[] aobject = compoundkey.getKeys();

            if (aobject.length != this.keys.length)
            {
                return false;
            }
            else
            {
                for (int i = 0; i < this.keys.length; ++i)
                {
                    if (!compareKeys(this.keys[i], aobject[i]))
                    {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    private static boolean compareKeys(Object key1, Object key2)
    {
        if (key1 == key2)
        {
            return true;
        }
        else if (key1 == null)
        {
            return false;
        }
        else
        {
            return key2 == null ? false : key1.equals(key2);
        }
    }

    private Object[] getKeys()
    {
        return this.keys;
    }

    public Object[] getKeysCopy()
    {
        return this.keys.clone();
    }

    public String toString()
    {
        return "[" + Config.arrayToString(this.keys) + "]";
    }
}
