package net.optifine.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StaticMap
{
    private static final Map<String, Object> MAP = Collections.synchronizedMap(new HashMap<>());

    public static boolean contains(String key)
    {
        return MAP.containsKey(key);
    }

    public static Object get(String key)
    {
        return MAP.get(key);
    }

    public static void put(String key, Object val)
    {
        MAP.put(key, val);
    }

    public static int getInt(String key, int def)
    {
        Object object = MAP.get(key);

        if (!(object instanceof Integer))
        {
            return def;
        }
        else
        {
            Integer integer = (Integer)object;
            return integer;
        }
    }

    public static int putInt(String key, int val)
    {
        int i = getInt(key, 0);
        Integer integer = val;
        MAP.put(key, integer);
        return i;
    }

    public static long getLong(String key, long def)
    {
        Object object = MAP.get(key);

        if (!(object instanceof Long))
        {
            return def;
        }
        else
        {
            Long olong = (Long)object;
            return olong;
        }
    }

    public static void putLong(String key, long val)
    {
        Long olong = val;
        MAP.put(key, olong);
    }

    public static long putLong(String key, long val, long def)
    {
        long i = getLong(key, def);
        Long olong = val;
        MAP.put(key, olong);
        return i;
    }

    public static long addLong(String key, long val, long def)
    {
        long i = getLong(key, def);
        i = i + val;
        putLong(key, i);
        return i;
    }
}
