package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;

public class NoExposedOreFeature extends Feature<OreFeatureConfig>
{
    NoExposedOreFeature(Codec<OreFeatureConfig> p_i231974_1_)
    {
        super(p_i231974_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, OreFeatureConfig p_241855_5_)
    {
        int i = p_241855_3_.nextInt(p_241855_5_.size + 1);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int j = 0; j < i; ++j)
        {
            this.func_236327_a_(blockpos$mutable, p_241855_3_, p_241855_4_, Math.min(j, 7));

            if (p_241855_5_.target.test(p_241855_1_.getBlockState(blockpos$mutable), p_241855_3_) && !this.func_236326_a_(p_241855_1_, blockpos$mutable))
            {
                p_241855_1_.setBlockState(blockpos$mutable, p_241855_5_.state, 2);
            }
        }

        return true;
    }

    private void func_236327_a_(BlockPos.Mutable p_236327_1_, Random p_236327_2_, BlockPos p_236327_3_, int p_236327_4_)
    {
        int i = this.func_236328_a_(p_236327_2_, p_236327_4_);
        int j = this.func_236328_a_(p_236327_2_, p_236327_4_);
        int k = this.func_236328_a_(p_236327_2_, p_236327_4_);
        p_236327_1_.setAndOffset(p_236327_3_, i, j, k);
    }

    private int func_236328_a_(Random p_236328_1_, int p_236328_2_)
    {
        return Math.round((p_236328_1_.nextFloat() - p_236328_1_.nextFloat()) * (float)p_236328_2_);
    }

    private boolean func_236326_a_(IWorld p_236326_1_, BlockPos p_236326_2_)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (Direction direction : Direction.values())
        {
            blockpos$mutable.setAndMove(p_236326_2_, direction);

            if (p_236326_1_.getBlockState(blockpos$mutable).isAir())
            {
                return true;
            }
        }

        return false;
    }
}
