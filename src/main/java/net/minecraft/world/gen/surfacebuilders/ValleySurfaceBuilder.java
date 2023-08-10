package net.minecraft.world.gen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import java.util.Comparator;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.OctavesNoiseGenerator;

public abstract class ValleySurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig>
{
    private long field_237170_a_;
    private ImmutableMap<BlockState, OctavesNoiseGenerator> field_237171_b_ = ImmutableMap.of();
    private ImmutableMap<BlockState, OctavesNoiseGenerator> field_237172_c_ = ImmutableMap.of();
    private OctavesNoiseGenerator field_237173_d_;

    public ValleySurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232130_1_)
    {
        super(p_i232130_1_);
    }

    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        int i = seaLevel + 1;
        int j = x & 15;
        int k = z & 15;
        int l = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int i1 = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        double d0 = 0.03125D;
        boolean flag = this.field_237173_d_.func_205563_a((double)x * 0.03125D, 109.0D, (double)z * 0.03125D) * 75.0D + random.nextDouble() > 0.0D;
        BlockState blockstate = this.field_237172_c_.entrySet().stream().max(Comparator.comparing((p_237176_3_) ->
        {
            return p_237176_3_.getValue().func_205563_a((double)x, (double)seaLevel, (double)z);
        })).get().getKey();
        BlockState blockstate1 = this.field_237171_b_.entrySet().stream().max(Comparator.comparing((p_237174_3_) ->
        {
            return p_237174_3_.getValue().func_205563_a((double)x, (double)seaLevel, (double)z);
        })).get().getKey();
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        BlockState blockstate2 = chunkIn.getBlockState(blockpos$mutable.setPos(j, 128, k));

        for (int j1 = 127; j1 >= 0; --j1)
        {
            blockpos$mutable.setPos(j, j1, k);
            BlockState blockstate3 = chunkIn.getBlockState(blockpos$mutable);

            if (blockstate2.isIn(defaultBlock.getBlock()) && (blockstate3.isAir() || blockstate3 == defaultFluid))
            {
                for (int k1 = 0; k1 < l; ++k1)
                {
                    blockpos$mutable.move(Direction.UP);

                    if (!chunkIn.getBlockState(blockpos$mutable).isIn(defaultBlock.getBlock()))
                    {
                        break;
                    }

                    chunkIn.setBlockState(blockpos$mutable, blockstate, false);
                }

                blockpos$mutable.setPos(j, j1, k);
            }

            if ((blockstate2.isAir() || blockstate2 == defaultFluid) && blockstate3.isIn(defaultBlock.getBlock()))
            {
                for (int l1 = 0; l1 < i1 && chunkIn.getBlockState(blockpos$mutable).isIn(defaultBlock.getBlock()); ++l1)
                {
                    if (flag && j1 >= i - 4 && j1 <= i + 1)
                    {
                        chunkIn.setBlockState(blockpos$mutable, this.func_230389_c_(), false);
                    }
                    else
                    {
                        chunkIn.setBlockState(blockpos$mutable, blockstate1, false);
                    }

                    blockpos$mutable.move(Direction.DOWN);
                }
            }

            blockstate2 = blockstate3;
        }
    }

    public void setSeed(long seed)
    {
        if (this.field_237170_a_ != seed || this.field_237173_d_ == null || this.field_237171_b_.isEmpty() || this.field_237172_c_.isEmpty())
        {
            this.field_237171_b_ = func_237175_a_(this.func_230387_a_(), seed);
            this.field_237172_c_ = func_237175_a_(this.func_230388_b_(), seed + (long)this.field_237171_b_.size());
            this.field_237173_d_ = new OctavesNoiseGenerator(new SharedSeedRandom(seed + (long)this.field_237171_b_.size() + (long)this.field_237172_c_.size()), ImmutableList.of(0));
        }

        this.field_237170_a_ = seed;
    }

    private static ImmutableMap<BlockState, OctavesNoiseGenerator> func_237175_a_(ImmutableList<BlockState> p_237175_0_, long p_237175_1_)
    {
        Builder<BlockState, OctavesNoiseGenerator> builder = new Builder<>();

        for (BlockState blockstate : p_237175_0_)
        {
            builder.put(blockstate, new OctavesNoiseGenerator(new SharedSeedRandom(p_237175_1_), ImmutableList.of(-4)));
            ++p_237175_1_;
        }

        return builder.build();
    }

    protected abstract ImmutableList<BlockState> func_230387_a_();

    protected abstract ImmutableList<BlockState> func_230388_b_();

    protected abstract BlockState func_230389_c_();
}
