package net.minecraft.world.gen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.OctavesNoiseGenerator;

public class NetherForestsSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig>
{
    private static final BlockState field_237178_b_ = Blocks.CAVE_AIR.getDefaultState();
    protected long field_237177_a_;
    private OctavesNoiseGenerator field_237179_c_;

    public NetherForestsSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232131_1_)
    {
        super(p_i232131_1_);
    }

    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        int i = seaLevel;
        int j = x & 15;
        int k = z & 15;
        double d0 = this.field_237179_c_.func_205563_a((double)x * 0.1D, (double)seaLevel, (double)z * 0.1D);
        boolean flag = d0 > 0.15D + random.nextDouble() * 0.35D;
        double d1 = this.field_237179_c_.func_205563_a((double)x * 0.1D, 109.0D, (double)z * 0.1D);
        boolean flag1 = d1 > 0.25D + random.nextDouble() * 0.9D;
        int l = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int i1 = -1;
        BlockState blockstate = config.getUnder();

        for (int j1 = 127; j1 >= 0; --j1)
        {
            blockpos$mutable.setPos(j, j1, k);
            BlockState blockstate1 = config.getTop();
            BlockState blockstate2 = chunkIn.getBlockState(blockpos$mutable);

            if (blockstate2.isAir())
            {
                i1 = -1;
            }
            else if (blockstate2.isIn(defaultBlock.getBlock()))
            {
                if (i1 == -1)
                {
                    boolean flag2 = false;

                    if (l <= 0)
                    {
                        flag2 = true;
                        blockstate = config.getUnder();
                    }

                    if (flag)
                    {
                        blockstate1 = config.getUnder();
                    }
                    else if (flag1)
                    {
                        blockstate1 = config.getUnderWaterMaterial();
                    }

                    if (j1 < i && flag2)
                    {
                        blockstate1 = defaultFluid;
                    }

                    i1 = l;

                    if (j1 >= i - 1)
                    {
                        chunkIn.setBlockState(blockpos$mutable, blockstate1, false);
                    }
                    else
                    {
                        chunkIn.setBlockState(blockpos$mutable, blockstate, false);
                    }
                }
                else if (i1 > 0)
                {
                    --i1;
                    chunkIn.setBlockState(blockpos$mutable, blockstate, false);
                }
            }
        }
    }

    public void setSeed(long seed)
    {
        if (this.field_237177_a_ != seed || this.field_237179_c_ == null)
        {
            this.field_237179_c_ = new OctavesNoiseGenerator(new SharedSeedRandom(seed), ImmutableList.of(0));
        }

        this.field_237177_a_ = seed;
    }
}
