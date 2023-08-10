package net.optifine.util;

public class CompareUtils
{
    public static int hash(int v1, float v2)
    {
        return 31 * hash(v1) + hash(v2);
    }

    public static int hash(float v1, float v2)
    {
        return 31 * hash(v1) + hash(v2);
    }

    public static int hash(boolean v1, boolean v2)
    {
        return 31 * hash(v1) + hash(v2);
    }

    public static int hash(int v1, Object v2)
    {
        return 31 * hash(v1) + hash(v2);
    }

    public static int hash(Object v1, boolean v2)
    {
        return 31 * hash(v1) + hash(v2);
    }

    public static int hash(Object o1, Object o2)
    {
        return 31 * hash(o1) + hash(o2);
    }

    public static int hash(int i)
    {
        return i;
    }

    public static int hash(float f)
    {
        return Float.hashCode(f);
    }

    public static int hash(boolean b)
    {
        return Boolean.hashCode(b);
    }

    public static int hash(Object o)
    {
        return o == null ? 0 : o.hashCode();
    }
}
