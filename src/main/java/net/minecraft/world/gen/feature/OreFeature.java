package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;

public class OreFeature extends Feature<OreFeatureConfig>
{
    public OreFeature(Codec<OreFeatureConfig> p_i231976_1_)
    {
        super(p_i231976_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, OreFeatureConfig p_241855_5_)
    {
        float f = p_241855_3_.nextFloat() * (float)Math.PI;
        float f1 = (float)p_241855_5_.size / 8.0F;
        int i = MathHelper.ceil(((float)p_241855_5_.size / 16.0F * 2.0F + 1.0F) / 2.0F);
        double d0 = (double)p_241855_4_.getX() + Math.sin((double)f) * (double)f1;
        double d1 = (double)p_241855_4_.getX() - Math.sin((double)f) * (double)f1;
        double d2 = (double)p_241855_4_.getZ() + Math.cos((double)f) * (double)f1;
        double d3 = (double)p_241855_4_.getZ() - Math.cos((double)f) * (double)f1;
        int j = 2;
        double d4 = (double)(p_241855_4_.getY() + p_241855_3_.nextInt(3) - 2);
        double d5 = (double)(p_241855_4_.getY() + p_241855_3_.nextInt(3) - 2);
        int k = p_241855_4_.getX() - MathHelper.ceil(f1) - i;
        int l = p_241855_4_.getY() - 2 - i;
        int i1 = p_241855_4_.getZ() - MathHelper.ceil(f1) - i;
        int j1 = 2 * (MathHelper.ceil(f1) + i);
        int k1 = 2 * (2 + i);

        for (int l1 = k; l1 <= k + j1; ++l1)
        {
            for (int i2 = i1; i2 <= i1 + j1; ++i2)
            {
                if (l <= p_241855_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, l1, i2))
                {
                    return this.func_207803_a(p_241855_1_, p_241855_3_, p_241855_5_, d0, d1, d2, d3, d4, d5, k, l, i1, j1, k1);
                }
            }
        }

        return false;
    }

    protected boolean func_207803_a(IWorld worldIn, Random random, OreFeatureConfig config, double p_207803_4_, double p_207803_6_, double p_207803_8_, double p_207803_10_, double p_207803_12_, double p_207803_14_, int p_207803_16_, int p_207803_17_, int p_207803_18_, int p_207803_19_, int p_207803_20_)
    {
        int i = 0;
        BitSet bitset = new BitSet(p_207803_19_ * p_207803_20_ * p_207803_19_);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int j = config.size;
        double[] adouble = new double[j * 4];

        for (int k = 0; k < j; ++k)
        {
            float f = (float)k / (float)j;
            double d0 = MathHelper.lerp((double)f, p_207803_4_, p_207803_6_);
            double d2 = MathHelper.lerp((double)f, p_207803_12_, p_207803_14_);
            double d4 = MathHelper.lerp((double)f, p_207803_8_, p_207803_10_);
            double d6 = random.nextDouble() * (double)j / 16.0D;
            double d7 = ((double)(MathHelper.sin((float)Math.PI * f) + 1.0F) * d6 + 1.0D) / 2.0D;
            adouble[k * 4 + 0] = d0;
            adouble[k * 4 + 1] = d2;
            adouble[k * 4 + 2] = d4;
            adouble[k * 4 + 3] = d7;
        }

        for (int i3 = 0; i3 < j - 1; ++i3)
        {
            if (!(adouble[i3 * 4 + 3] <= 0.0D))
            {
                for (int k3 = i3 + 1; k3 < j; ++k3)
                {
                    if (!(adouble[k3 * 4 + 3] <= 0.0D))
                    {
                        double d12 = adouble[i3 * 4 + 0] - adouble[k3 * 4 + 0];
                        double d13 = adouble[i3 * 4 + 1] - adouble[k3 * 4 + 1];
                        double d14 = adouble[i3 * 4 + 2] - adouble[k3 * 4 + 2];
                        double d15 = adouble[i3 * 4 + 3] - adouble[k3 * 4 + 3];

                        if (d15 * d15 > d12 * d12 + d13 * d13 + d14 * d14)
                        {
                            if (d15 > 0.0D)
                            {
                                adouble[k3 * 4 + 3] = -1.0D;
                            }
                            else
                            {
                                adouble[i3 * 4 + 3] = -1.0D;
                            }
                        }
                    }
                }
            }
        }

        for (int j3 = 0; j3 < j; ++j3)
        {
            double d11 = adouble[j3 * 4 + 3];

            if (!(d11 < 0.0D))
            {
                double d1 = adouble[j3 * 4 + 0];
                double d3 = adouble[j3 * 4 + 1];
                double d5 = adouble[j3 * 4 + 2];
                int l = Math.max(MathHelper.floor(d1 - d11), p_207803_16_);
                int l3 = Math.max(MathHelper.floor(d3 - d11), p_207803_17_);
                int i1 = Math.max(MathHelper.floor(d5 - d11), p_207803_18_);
                int j1 = Math.max(MathHelper.floor(d1 + d11), l);
                int k1 = Math.max(MathHelper.floor(d3 + d11), l3);
                int l1 = Math.max(MathHelper.floor(d5 + d11), i1);

                for (int i2 = l; i2 <= j1; ++i2)
                {
                    double d8 = ((double)i2 + 0.5D - d1) / d11;

                    if (d8 * d8 < 1.0D)
                    {
                        for (int j2 = l3; j2 <= k1; ++j2)
                        {
                            double d9 = ((double)j2 + 0.5D - d3) / d11;

                            if (d8 * d8 + d9 * d9 < 1.0D)
                            {
                                for (int k2 = i1; k2 <= l1; ++k2)
                                {
                                    double d10 = ((double)k2 + 0.5D - d5) / d11;

                                    if (d8 * d8 + d9 * d9 + d10 * d10 < 1.0D)
                                    {
                                        int l2 = i2 - p_207803_16_ + (j2 - p_207803_17_) * p_207803_19_ + (k2 - p_207803_18_) * p_207803_19_ * p_207803_20_;

                                        if (!bitset.get(l2))
                                        {
                                            bitset.set(l2);
                                            blockpos$mutable.setPos(i2, j2, k2);

                                            if (config.target.test(worldIn.getBlockState(blockpos$mutable), random))
                                            {
                                                worldIn.setBlockState(blockpos$mutable, config.state, 2);
                                                ++i;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return i > 0;
    }
}
