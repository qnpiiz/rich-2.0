package net.optifine.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;

public class MathUtils
{
    public static final float PI = (float)Math.PI;
    public static final float PI2 = ((float)Math.PI * 2F);
    public static final float PId2 = ((float)Math.PI / 2F);
    private static final float[] ASIN_TABLE = new float[65536];

    public static float asin(float value)
    {
        return ASIN_TABLE[(int)((double)(value + 1.0F) * 32767.5D) & 65535];
    }

    public static float acos(float value)
    {
        return ((float)Math.PI / 2F) - ASIN_TABLE[(int)((double)(value + 1.0F) * 32767.5D) & 65535];
    }

    public static int getAverage(int[] vals)
    {
        if (vals.length <= 0)
        {
            return 0;
        }
        else
        {
            int i = getSum(vals);
            return i / vals.length;
        }
    }

    public static int getSum(int[] vals)
    {
        if (vals.length <= 0)
        {
            return 0;
        }
        else
        {
            int i = 0;

            for (int j = 0; j < vals.length; ++j)
            {
                int k = vals[j];
                i += k;
            }

            return i;
        }
    }

    public static int roundDownToPowerOfTwo(int val)
    {
        int i = MathHelper.smallestEncompassingPowerOfTwo(val);
        return val == i ? i : i / 2;
    }

    public static boolean equalsDelta(float f1, float f2, float delta)
    {
        return Math.abs(f1 - f2) <= delta;
    }

    public static float toDeg(float angle)
    {
        return angle * 180.0F / MathHelper.PI;
    }

    public static float toRad(float angle)
    {
        return angle / 180.0F * MathHelper.PI;
    }

    public static float roundToFloat(double d)
    {
        return (float)((double)Math.round(d * 1.0E8D) / 1.0E8D);
    }

    public static double distanceSq(BlockPos pos, double x, double y, double z)
    {
        return distanceSq((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), x, y, z);
    }

    public static float distanceSq(BlockPos pos, float x, float y, float z)
    {
        return distanceSq((float)pos.getX(), (float)pos.getY(), (float)pos.getZ(), x, y, z);
    }

    public static double distanceSq(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double d0 = x1 - x2;
        double d1 = y1 - y2;
        double d2 = z1 - z2;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public static float distanceSq(float x1, float y1, float z1, float x2, float y2, float z2)
    {
        float f = x1 - x2;
        float f1 = y1 - y2;
        float f2 = z1 - z2;
        return f * f + f1 * f1 + f2 * f2;
    }

    public static Matrix4f makeMatrixIdentity()
    {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        return matrix4f;
    }

    static
    {
        for (int i = 0; i < 65536; ++i)
        {
            ASIN_TABLE[i] = (float)Math.asin((double)i / 32767.5D - 1.0D);
        }

        for (int j = -1; j < 2; ++j)
        {
            ASIN_TABLE[(int)(((double)j + 1.0D) * 32767.5D) & 65535] = (float)Math.asin((double)j);
        }
    }
}
