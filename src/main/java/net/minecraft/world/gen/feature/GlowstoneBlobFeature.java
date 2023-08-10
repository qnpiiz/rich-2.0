package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class GlowstoneBlobFeature extends Feature<NoFeatureConfig>
{
    public GlowstoneBlobFeature(Codec<NoFeatureConfig> p_i231956_1_)
    {
        super(p_i231956_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_)
    {
        if (!p_241855_1_.isAirBlock(p_241855_4_))
        {
            return false;
        }
        else
        {
            BlockState blockstate = p_241855_1_.getBlockState(p_241855_4_.up());

            if (!blockstate.isIn(Blocks.NETHERRACK) && !blockstate.isIn(Blocks.BASALT) && !blockstate.isIn(Blocks.BLACKSTONE))
            {
                return false;
            }
            else
            {
                p_241855_1_.setBlockState(p_241855_4_, Blocks.GLOWSTONE.getDefaultState(), 2);

                for (int i = 0; i < 1500; ++i)
                {
                    BlockPos blockpos = p_241855_4_.add(p_241855_3_.nextInt(8) - p_241855_3_.nextInt(8), -p_241855_3_.nextInt(12), p_241855_3_.nextInt(8) - p_241855_3_.nextInt(8));

                    if (p_241855_1_.getBlockState(blockpos).isAir())
                    {
                        int j = 0;

                        for (Direction direction : Direction.values())
                        {
                            if (p_241855_1_.getBlockState(blockpos.offset(direction)).isIn(Blocks.GLOWSTONE))
                            {
                                ++j;
                            }

                            if (j > 1)
                            {
                                break;
                            }
                        }

                        if (j == 1)
                        {
                            p_241855_1_.setBlockState(blockpos, Blocks.GLOWSTONE.getDefaultState(), 2);
                        }
                    }
                }

                return true;
            }
        }
    }
}
