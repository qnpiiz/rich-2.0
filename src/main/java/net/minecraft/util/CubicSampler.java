package net.minecraft.util;

import javax.annotation.Nonnull;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class CubicSampler
{
    private static final double[] field_240806_a_ = new double[] {0.0D, 1.0D, 4.0D, 6.0D, 4.0D, 1.0D, 0.0D};

    @Nonnull
    public static Vector3d func_240807_a_(Vector3d p_240807_0_, CubicSampler.Vec3Fetcher p_240807_1_)
    {
        int i = MathHelper.floor(p_240807_0_.getX());
        int j = MathHelper.floor(p_240807_0_.getY());
        int k = MathHelper.floor(p_240807_0_.getZ());
        double d0 = p_240807_0_.getX() - (double)i;
        double d1 = p_240807_0_.getY() - (double)j;
        double d2 = p_240807_0_.getZ() - (double)k;
        double d3 = 0.0D;
        Vector3d vector3d = Vector3d.ZERO;

        for (int l = 0; l < 6; ++l)
        {
            double d4 = MathHelper.lerp(d0, field_240806_a_[l + 1], field_240806_a_[l]);
            int i1 = i - 2 + l;

            for (int j1 = 0; j1 < 6; ++j1)
            {
                double d5 = MathHelper.lerp(d1, field_240806_a_[j1 + 1], field_240806_a_[j1]);
                int k1 = j - 2 + j1;

                for (int l1 = 0; l1 < 6; ++l1)
                {
                    double d6 = MathHelper.lerp(d2, field_240806_a_[l1 + 1], field_240806_a_[l1]);
                    int i2 = k - 2 + l1;
                    double d7 = d4 * d5 * d6;
                    d3 += d7;
                    vector3d = vector3d.add(p_240807_1_.fetch(i1, k1, i2).scale(d7));
                }
            }
        }

        return vector3d.scale(1.0D / d3);
    }

    public interface Vec3Fetcher
    {
        Vector3d fetch(int p_fetch_1_, int p_fetch_2_, int p_fetch_3_);
    }
}
