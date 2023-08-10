package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;

public abstract class AbstractBigMushroomFeature extends Feature<BigMushroomFeatureConfig>
{
    public AbstractBigMushroomFeature(Codec<BigMushroomFeatureConfig> p_i231923_1_)
    {
        super(p_i231923_1_);
    }

    protected void func_227210_a_(IWorld p_227210_1_, Random p_227210_2_, BlockPos p_227210_3_, BigMushroomFeatureConfig p_227210_4_, int p_227210_5_, BlockPos.Mutable p_227210_6_)
    {
        for (int i = 0; i < p_227210_5_; ++i)
        {
            p_227210_6_.setPos(p_227210_3_).move(Direction.UP, i);

            if (!p_227210_1_.getBlockState(p_227210_6_).isOpaqueCube(p_227210_1_, p_227210_6_))
            {
                this.setBlockState(p_227210_1_, p_227210_6_, p_227210_4_.field_227273_b_.getBlockState(p_227210_2_, p_227210_3_));
            }
        }
    }

    protected int func_227211_a_(Random p_227211_1_)
    {
        int i = p_227211_1_.nextInt(3) + 4;

        if (p_227211_1_.nextInt(12) == 0)
        {
            i *= 2;
        }

        return i;
    }

    protected boolean func_227209_a_(IWorld p_227209_1_, BlockPos p_227209_2_, int p_227209_3_, BlockPos.Mutable p_227209_4_, BigMushroomFeatureConfig p_227209_5_)
    {
        int i = p_227209_2_.getY();

        if (i >= 1 && i + p_227209_3_ + 1 < 256)
        {
            Block block = p_227209_1_.getBlockState(p_227209_2_.down()).getBlock();

            if (!isDirt(block) && !block.isIn(BlockTags.MUSHROOM_GROW_BLOCK))
            {
                return false;
            }
            else
            {
                for (int j = 0; j <= p_227209_3_; ++j)
                {
                    int k = this.func_225563_a_(-1, -1, p_227209_5_.field_227274_c_, j);

                    for (int l = -k; l <= k; ++l)
                    {
                        for (int i1 = -k; i1 <= k; ++i1)
                        {
                            BlockState blockstate = p_227209_1_.getBlockState(p_227209_4_.setAndOffset(p_227209_2_, l, j, i1));

                            if (!blockstate.isAir() && !blockstate.isIn(BlockTags.LEAVES))
                            {
                                return false;
                            }
                        }
                    }
                }

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, BigMushroomFeatureConfig p_241855_5_)
    {
        int i = this.func_227211_a_(p_241855_3_);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        if (!this.func_227209_a_(p_241855_1_, p_241855_4_, i, blockpos$mutable, p_241855_5_))
        {
            return false;
        }
        else
        {
            this.func_225564_a_(p_241855_1_, p_241855_3_, p_241855_4_, i, blockpos$mutable, p_241855_5_);
            this.func_227210_a_(p_241855_1_, p_241855_3_, p_241855_4_, p_241855_5_, i, blockpos$mutable);
            return true;
        }
    }

    protected abstract int func_225563_a_(int p_225563_1_, int p_225563_2_, int p_225563_3_, int p_225563_4_);

    protected abstract void func_225564_a_(IWorld p_225564_1_, Random p_225564_2_, BlockPos p_225564_3_, int p_225564_4_, BlockPos.Mutable p_225564_5_, BigMushroomFeatureConfig p_225564_6_);
}
