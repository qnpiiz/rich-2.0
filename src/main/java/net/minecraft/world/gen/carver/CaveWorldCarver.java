package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class CaveWorldCarver extends WorldCarver<ProbabilityConfig>
{
    public CaveWorldCarver(Codec<ProbabilityConfig> p_i231917_1_, int p_i231917_2_)
    {
        super(p_i231917_1_, p_i231917_2_);
    }

    public boolean shouldCarve(Random rand, int chunkX, int chunkZ, ProbabilityConfig config)
    {
        return rand.nextFloat() <= config.probability;
    }

    public boolean carveRegion(IChunk chunk, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig config)
    {
        int i = (this.func_222704_c() * 2 - 1) * 16;
        int j = rand.nextInt(rand.nextInt(rand.nextInt(this.func_230357_a_()) + 1) + 1);

        for (int k = 0; k < j; ++k)
        {
            double d0 = (double)(chunkXOffset * 16 + rand.nextInt(16));
            double d1 = (double)this.func_230361_b_(rand);
            double d2 = (double)(chunkZOffset * 16 + rand.nextInt(16));
            int l = 1;

            if (rand.nextInt(4) == 0)
            {
                double d3 = 0.5D;
                float f1 = 1.0F + rand.nextFloat() * 6.0F;
                this.func_227205_a_(chunk, biomePos, rand.nextLong(), seaLevel, chunkX, chunkZ, d0, d1, d2, f1, 0.5D, carvingMask);
                l += rand.nextInt(4);
            }

            for (int k1 = 0; k1 < l; ++k1)
            {
                float f = rand.nextFloat() * ((float)Math.PI * 2F);
                float f3 = (rand.nextFloat() - 0.5F) / 4.0F;
                float f2 = this.func_230359_a_(rand);
                int i1 = i - rand.nextInt(i / 4);
                int j1 = 0;
                this.func_227206_a_(chunk, biomePos, rand.nextLong(), seaLevel, chunkX, chunkZ, d0, d1, d2, f2, f, f3, 0, i1, this.func_230360_b_(), carvingMask);
            }
        }

        return true;
    }

    protected int func_230357_a_()
    {
        return 15;
    }

    protected float func_230359_a_(Random p_230359_1_)
    {
        float f = p_230359_1_.nextFloat() * 2.0F + p_230359_1_.nextFloat();

        if (p_230359_1_.nextInt(10) == 0)
        {
            f *= p_230359_1_.nextFloat() * p_230359_1_.nextFloat() * 3.0F + 1.0F;
        }

        return f;
    }

    protected double func_230360_b_()
    {
        return 1.0D;
    }

    protected int func_230361_b_(Random p_230361_1_)
    {
        return p_230361_1_.nextInt(p_230361_1_.nextInt(120) + 8);
    }

    protected void func_227205_a_(IChunk p_227205_1_, Function<BlockPos, Biome> p_227205_2_, long p_227205_3_, int seaLevel, int chunkX, int chunkZ, double randOffsetXCoord, double startY, double randOffsetZCoord, float p_227205_14_, double p_227205_15_, BitSet carvingMask)
    {
        double d0 = 1.5D + (double)(MathHelper.sin(((float)Math.PI / 2F)) * p_227205_14_);
        double d1 = d0 * p_227205_15_;
        this.func_227208_a_(p_227205_1_, p_227205_2_, p_227205_3_, seaLevel, chunkX, chunkZ, randOffsetXCoord + 1.0D, startY, randOffsetZCoord, d0, d1, carvingMask);
    }

    protected void func_227206_a_(IChunk chunk, Function<BlockPos, Biome> biomePos, long seed, int seaLevel, int chunkX, int chunkZ, double randOffsetXCoord, double startY, double randOffsetZCoord, float caveRadius, float pitch, float p_227206_16_, int p_227206_17_, int p_227206_18_, double p_227206_19_, BitSet p_227206_21_)
    {
        Random random = new Random(seed);
        int i = random.nextInt(p_227206_18_ / 2) + p_227206_18_ / 4;
        boolean flag = random.nextInt(6) == 0;
        float f = 0.0F;
        float f1 = 0.0F;

        for (int j = p_227206_17_; j < p_227206_18_; ++j)
        {
            double d0 = 1.5D + (double)(MathHelper.sin((float)Math.PI * (float)j / (float)p_227206_18_) * caveRadius);
            double d1 = d0 * p_227206_19_;
            float f2 = MathHelper.cos(p_227206_16_);
            randOffsetXCoord += (double)(MathHelper.cos(pitch) * f2);
            startY += (double)MathHelper.sin(p_227206_16_);
            randOffsetZCoord += (double)(MathHelper.sin(pitch) * f2);
            p_227206_16_ = p_227206_16_ * (flag ? 0.92F : 0.7F);
            p_227206_16_ = p_227206_16_ + f1 * 0.1F;
            pitch += f * 0.1F;
            f1 = f1 * 0.9F;
            f = f * 0.75F;
            f1 = f1 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f = f + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (j == i && caveRadius > 1.0F)
            {
                this.func_227206_a_(chunk, biomePos, random.nextLong(), seaLevel, chunkX, chunkZ, randOffsetXCoord, startY, randOffsetZCoord, random.nextFloat() * 0.5F + 0.5F, pitch - ((float)Math.PI / 2F), p_227206_16_ / 3.0F, j, p_227206_18_, 1.0D, p_227206_21_);
                this.func_227206_a_(chunk, biomePos, random.nextLong(), seaLevel, chunkX, chunkZ, randOffsetXCoord, startY, randOffsetZCoord, random.nextFloat() * 0.5F + 0.5F, pitch + ((float)Math.PI / 2F), p_227206_16_ / 3.0F, j, p_227206_18_, 1.0D, p_227206_21_);
                return;
            }

            if (random.nextInt(4) != 0)
            {
                if (!this.func_222702_a(chunkX, chunkZ, randOffsetXCoord, randOffsetZCoord, j, p_227206_18_, caveRadius))
                {
                    return;
                }

                this.func_227208_a_(chunk, biomePos, seed, seaLevel, chunkX, chunkZ, randOffsetXCoord, startY, randOffsetZCoord, d0, d1, p_227206_21_);
            }
        }
    }

    protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_)
    {
        return p_222708_3_ <= -0.7D || p_222708_1_ * p_222708_1_ + p_222708_3_ * p_222708_3_ + p_222708_5_ * p_222708_5_ >= 1.0D;
    }
}
