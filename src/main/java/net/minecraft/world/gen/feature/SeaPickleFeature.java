package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;

public class SeaPickleFeature extends Feature<FeatureSpreadConfig>
{
    public SeaPickleFeature(Codec<FeatureSpreadConfig> p_i231987_1_)
    {
        super(p_i231987_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, FeatureSpreadConfig p_241855_5_)
    {
        int i = 0;
        int j = p_241855_5_.func_242799_a().func_242259_a(p_241855_3_);

        for (int k = 0; k < j; ++k)
        {
            int l = p_241855_3_.nextInt(8) - p_241855_3_.nextInt(8);
            int i1 = p_241855_3_.nextInt(8) - p_241855_3_.nextInt(8);
            int j1 = p_241855_1_.getHeight(Heightmap.Type.OCEAN_FLOOR, p_241855_4_.getX() + l, p_241855_4_.getZ() + i1);
            BlockPos blockpos = new BlockPos(p_241855_4_.getX() + l, j1, p_241855_4_.getZ() + i1);
            BlockState blockstate = Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, Integer.valueOf(p_241855_3_.nextInt(4) + 1));

            if (p_241855_1_.getBlockState(blockpos).isIn(Blocks.WATER) && blockstate.isValidPosition(p_241855_1_, blockpos))
            {
                p_241855_1_.setBlockState(blockpos, blockstate, 2);
                ++i;
            }
        }

        return i > 0;
    }
}
