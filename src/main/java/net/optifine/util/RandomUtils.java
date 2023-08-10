package net.optifine.util;

import java.util.Random;

public class RandomUtils
{
    private static final Random random = new Random();

    public static Random getRandom()
    {
        return random;
    }

    public static int getRandomInt(int bound)
    {
        return random.nextInt(bound);
    }
}
