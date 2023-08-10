package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class IceSpikeFeature extends Feature<NoFeatureConfig>
{
    public IceSpikeFeature(Codec<NoFeatureConfig> p_i231962_1_)
    {
        super(p_i231962_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_)
    {
        while (p_241855_1_.isAirBlock(p_241855_4_) && p_241855_4_.getY() > 2)
        {
            p_241855_4_ = p_241855_4_.down();
        }

        if (!p_241855_1_.getBlockState(p_241855_4_).isIn(Blocks.SNOW_BLOCK))
        {
            return false;
        }
        else
        {
            p_241855_4_ = p_241855_4_.up(p_241855_3_.nextInt(4));
            int i = p_241855_3_.nextInt(4) + 7;
            int j = i / 4 + p_241855_3_.nextInt(2);

            if (j > 1 && p_241855_3_.nextInt(60) == 0)
            {
                p_241855_4_ = p_241855_4_.up(10 + p_241855_3_.nextInt(30));
            }

            for (int k = 0; k < i; ++k)
            {
                float f = (1.0F - (float)k / (float)i) * (float)j;
                int l = MathHelper.ceil(f);

                for (int i1 = -l; i1 <= l; ++i1)
                {
                    float f1 = (float)MathHelper.abs(i1) - 0.25F;

                    for (int j1 = -l; j1 <= l; ++j1)
                    {
                        float f2 = (float)MathHelper.abs(j1) - 0.25F;

                        if ((i1 == 0 && j1 == 0 || !(f1 * f1 + f2 * f2 > f * f)) && (i1 != -l && i1 != l && j1 != -l && j1 != l || !(p_241855_3_.nextFloat() > 0.75F)))
                        {
                            BlockState blockstate = p_241855_1_.getBlockState(p_241855_4_.add(i1, k, j1));
                            Block block = blockstate.getBlock();

                            if (blockstate.isAir() || isDirt(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE)
                            {
                                this.setBlockState(p_241855_1_, p_241855_4_.add(i1, k, j1), Blocks.PACKED_ICE.getDefaultState());
                            }

                            if (k != 0 && l > 1)
                            {
                                blockstate = p_241855_1_.getBlockState(p_241855_4_.add(i1, -k, j1));
                                block = blockstate.getBlock();

                                if (blockstate.isAir() || isDirt(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE)
                                {
                                    this.setBlockState(p_241855_1_, p_241855_4_.add(i1, -k, j1), Blocks.PACKED_ICE.getDefaultState());
                                }
                            }
                        }
                    }
                }
            }

            int k1 = j - 1;

            if (k1 < 0)
            {
                k1 = 0;
            }
            else if (k1 > 1)
            {
                k1 = 1;
            }

            for (int l1 = -k1; l1 <= k1; ++l1)
            {
                for (int i2 = -k1; i2 <= k1; ++i2)
                {
                    BlockPos blockpos = p_241855_4_.add(l1, -1, i2);
                    int j2 = 50;

                    if (Math.abs(l1) == 1 && Math.abs(i2) == 1)
                    {
                        j2 = p_241855_3_.nextInt(5);
                    }

                    while (blockpos.getY() > 50)
                    {
                        BlockState blockstate1 = p_241855_1_.getBlockState(blockpos);
                        Block block1 = blockstate1.getBlock();

                        if (!blockstate1.isAir() && !isDirt(block1) && block1 != Blocks.SNOW_BLOCK && block1 != Blocks.ICE && block1 != Blocks.PACKED_ICE)
                        {
                            break;
                        }

                        this.setBlockState(p_241855_1_, blockpos, Blocks.PACKED_ICE.getDefaultState());
                        blockpos = blockpos.down();
                        --j2;

                        if (j2 <= 0)
                        {
                            blockpos = blockpos.down(p_241855_3_.nextInt(5) + 1);
                            j2 = p_241855_3_.nextInt(5);
                        }
                    }
                }
            }

            return true;
        }
    }
}
