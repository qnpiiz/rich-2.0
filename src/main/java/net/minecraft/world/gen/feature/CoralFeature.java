package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;

public abstract class CoralFeature extends Feature<NoFeatureConfig>
{
    public CoralFeature(Codec<NoFeatureConfig> p_i231940_1_)
    {
        super(p_i231940_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_)
    {
        BlockState blockstate = BlockTags.CORAL_BLOCKS.getRandomElement(p_241855_3_).getDefaultState();
        return this.func_204623_a(p_241855_1_, p_241855_3_, p_241855_4_, blockstate);
    }

    protected abstract boolean func_204623_a(IWorld p_204623_1_, Random p_204623_2_, BlockPos p_204623_3_, BlockState p_204623_4_);

    protected boolean func_204624_b(IWorld p_204624_1_, Random p_204624_2_, BlockPos p_204624_3_, BlockState p_204624_4_)
    {
        BlockPos blockpos = p_204624_3_.up();
        BlockState blockstate = p_204624_1_.getBlockState(p_204624_3_);

        if ((blockstate.isIn(Blocks.WATER) || blockstate.isIn(BlockTags.CORALS)) && p_204624_1_.getBlockState(blockpos).isIn(Blocks.WATER))
        {
            p_204624_1_.setBlockState(p_204624_3_, p_204624_4_, 3);

            if (p_204624_2_.nextFloat() < 0.25F)
            {
                p_204624_1_.setBlockState(blockpos, BlockTags.CORALS.getRandomElement(p_204624_2_).getDefaultState(), 2);
            }
            else if (p_204624_2_.nextFloat() < 0.05F)
            {
                p_204624_1_.setBlockState(blockpos, Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, Integer.valueOf(p_204624_2_.nextInt(4) + 1)), 2);
            }

            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                if (p_204624_2_.nextFloat() < 0.2F)
                {
                    BlockPos blockpos1 = p_204624_3_.offset(direction);

                    if (p_204624_1_.getBlockState(blockpos1).isIn(Blocks.WATER))
                    {
                        BlockState blockstate1 = BlockTags.WALL_CORALS.getRandomElement(p_204624_2_).getDefaultState().with(DeadCoralWallFanBlock.FACING, direction);
                        p_204624_1_.setBlockState(blockpos1, blockstate1, 2);
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
