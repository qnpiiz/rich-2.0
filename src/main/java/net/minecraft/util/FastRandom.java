package net.minecraft.util;

public class FastRandom
{
    public static long mix(long left, long right)
    {
        left = left * (left * 6364136223846793005L + 1442695040888963407L);
        return left + right;
    }
}
