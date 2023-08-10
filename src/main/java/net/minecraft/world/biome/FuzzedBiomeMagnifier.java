package net.minecraft.world.biome;

import net.minecraft.util.FastRandom;

public enum FuzzedBiomeMagnifier implements IBiomeMagnifier
{
    INSTANCE;

    ThreadLocal<double[]> DOUBLE8 = ThreadLocal.withInitial(() -> {
        return new double[8];
    });

    public Biome getBiome(long seed, int x, int y, int z, BiomeManager.IBiomeReader biomeReader)
    {
        int i = x - 2;
        int j = y - 2;
        int k = z - 2;
        int l = i >> 2;
        int i1 = j >> 2;
        int j1 = k >> 2;
        double d0 = (double)(i & 3) / 4.0D;
        double d1 = (double)(j & 3) / 4.0D;
        double d2 = (double)(k & 3) / 4.0D;
        double[] adouble = this.DOUBLE8.get();

        for (int k1 = 0; k1 < 8; ++k1)
        {
            boolean flag = (k1 & 4) == 0;
            boolean flag1 = (k1 & 2) == 0;
            boolean flag2 = (k1 & 1) == 0;
            int l1 = flag ? l : l + 1;
            int i2 = flag1 ? i1 : i1 + 1;
            int j2 = flag2 ? j1 : j1 + 1;
            double d3 = flag ? d0 : d0 - 1.0D;
            double d4 = flag1 ? d1 : d1 - 1.0D;
            double d5 = flag2 ? d2 : d2 - 1.0D;
            adouble[k1] = distanceToCorner(seed, l1, i2, j2, d3, d4, d5);
        }

        int k2 = 0;
        double d6 = adouble[0];

        for (int l2 = 1; l2 < 8; ++l2)
        {
            if (d6 > adouble[l2])
            {
                k2 = l2;
                d6 = adouble[l2];
            }
        }

        int i3 = (k2 & 4) == 0 ? l : l + 1;
        int j3 = (k2 & 2) == 0 ? i1 : i1 + 1;
        int k3 = (k2 & 1) == 0 ? j1 : j1 + 1;
        return biomeReader.getNoiseBiome(i3, j3, k3);
    }

    private static double distanceToCorner(long seed, int x, int y, int z, double scaleX, double scaleY, double scaleZ)
    {
        long i = FastRandom.mix(seed, (long)x);
        i = FastRandom.mix(i, (long)y);
        i = FastRandom.mix(i, (long)z);
        i = FastRandom.mix(i, (long)x);
        i = FastRandom.mix(i, (long)y);
        i = FastRandom.mix(i, (long)z);
        double d0 = randomDouble(i);
        i = FastRandom.mix(i, seed);
        double d1 = randomDouble(i);
        i = FastRandom.mix(i, seed);
        double d2 = randomDouble(i);
        return square(scaleZ + d2) + square(scaleY + d1) + square(scaleX + d0);
    }

    private static double randomDouble(long seed)
    {
        double d0 = (double)((int)Math.floorMod(seed >> 24, 1024L)) / 1024.0D;
        return (d0 - 0.5D) * 0.9D;
    }

    private static double square(double x)
    {
        return x * x;
    }
}
