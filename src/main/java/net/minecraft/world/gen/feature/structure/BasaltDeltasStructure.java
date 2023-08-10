package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BasaltDeltasFeature;
import net.minecraft.world.gen.feature.Feature;

public class BasaltDeltasStructure extends Feature<BasaltDeltasFeature>
{
    private static final ImmutableList<Block> field_236274_a_ = ImmutableList.of(Blocks.BEDROCK, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER);
    private static final Direction[] field_236275_ac_ = Direction.values();

    public BasaltDeltasStructure(Codec<BasaltDeltasFeature> p_i231946_1_)
    {
        super(p_i231946_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, BasaltDeltasFeature p_241855_5_)
    {
        boolean flag = false;
        boolean flag1 = p_241855_3_.nextDouble() < 0.9D;
        int i = flag1 ? p_241855_5_.func_242808_e().func_242259_a(p_241855_3_) : 0;
        int j = flag1 ? p_241855_5_.func_242808_e().func_242259_a(p_241855_3_) : 0;
        boolean flag2 = flag1 && i != 0 && j != 0;
        int k = p_241855_5_.func_242807_d().func_242259_a(p_241855_3_);
        int l = p_241855_5_.func_242807_d().func_242259_a(p_241855_3_);
        int i1 = Math.max(k, l);

        for (BlockPos blockpos : BlockPos.getProximitySortedBoxPositionsIterator(p_241855_4_, k, 0, l))
        {
            if (blockpos.manhattanDistance(p_241855_4_) > i1)
            {
                break;
            }

            if (func_236277_a_(p_241855_1_, blockpos, p_241855_5_))
            {
                if (flag2)
                {
                    flag = true;
                    this.setBlockState(p_241855_1_, blockpos, p_241855_5_.func_242806_c());
                }

                BlockPos blockpos1 = blockpos.add(i, 0, j);

                if (func_236277_a_(p_241855_1_, blockpos1, p_241855_5_))
                {
                    flag = true;
                    this.setBlockState(p_241855_1_, blockpos1, p_241855_5_.func_242804_b());
                }
            }
        }

        return flag;
    }

    private static boolean func_236277_a_(IWorld p_236277_0_, BlockPos p_236277_1_, BasaltDeltasFeature p_236277_2_)
    {
        BlockState blockstate = p_236277_0_.getBlockState(p_236277_1_);

        if (blockstate.isIn(p_236277_2_.func_242804_b().getBlock()))
        {
            return false;
        }
        else if (field_236274_a_.contains(blockstate.getBlock()))
        {
            return false;
        }
        else
        {
            for (Direction direction : field_236275_ac_)
            {
                boolean flag = p_236277_0_.getBlockState(p_236277_1_.offset(direction)).isAir();

                if (flag && direction != Direction.UP || !flag && direction == Direction.UP)
                {
                    return false;
                }
            }

            return true;
        }
    }
}
