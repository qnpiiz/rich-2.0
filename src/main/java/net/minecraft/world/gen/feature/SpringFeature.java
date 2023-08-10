package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class SpringFeature extends Feature<LiquidsConfig>
{
    public SpringFeature(Codec<LiquidsConfig> p_i231995_1_)
    {
        super(p_i231995_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, LiquidsConfig p_241855_5_)
    {
        if (!p_241855_5_.acceptedBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.up()).getBlock()))
        {
            return false;
        }
        else if (p_241855_5_.needsBlockBelow && !p_241855_5_.acceptedBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.down()).getBlock()))
        {
            return false;
        }
        else
        {
            BlockState blockstate = p_241855_1_.getBlockState(p_241855_4_);

            if (!blockstate.isAir() && !p_241855_5_.acceptedBlocks.contains(blockstate.getBlock()))
            {
                return false;
            }
            else
            {
                int i = 0;
                int j = 0;

                if (p_241855_5_.acceptedBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.west()).getBlock()))
                {
                    ++j;
                }

                if (p_241855_5_.acceptedBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.east()).getBlock()))
                {
                    ++j;
                }

                if (p_241855_5_.acceptedBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.north()).getBlock()))
                {
                    ++j;
                }

                if (p_241855_5_.acceptedBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.south()).getBlock()))
                {
                    ++j;
                }

                if (p_241855_5_.acceptedBlocks.contains(p_241855_1_.getBlockState(p_241855_4_.down()).getBlock()))
                {
                    ++j;
                }

                int k = 0;

                if (p_241855_1_.isAirBlock(p_241855_4_.west()))
                {
                    ++k;
                }

                if (p_241855_1_.isAirBlock(p_241855_4_.east()))
                {
                    ++k;
                }

                if (p_241855_1_.isAirBlock(p_241855_4_.north()))
                {
                    ++k;
                }

                if (p_241855_1_.isAirBlock(p_241855_4_.south()))
                {
                    ++k;
                }

                if (p_241855_1_.isAirBlock(p_241855_4_.down()))
                {
                    ++k;
                }

                if (j == p_241855_5_.rockAmount && k == p_241855_5_.holeAmount)
                {
                    p_241855_1_.setBlockState(p_241855_4_, p_241855_5_.state.getBlockState(), 2);
                    p_241855_1_.getPendingFluidTicks().scheduleTick(p_241855_4_, p_241855_5_.state.getFluid(), 0);
                    ++i;
                }

                return i > 0;
            }
        }
    }
}
