package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class ChorusPlantFeature extends Feature<NoFeatureConfig>
{
    public ChorusPlantFeature(Codec<NoFeatureConfig> p_i231936_1_)
    {
        super(p_i231936_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_)
    {
        if (p_241855_1_.isAirBlock(p_241855_4_) && p_241855_1_.getBlockState(p_241855_4_.down()).isIn(Blocks.END_STONE))
        {
            ChorusFlowerBlock.generatePlant(p_241855_1_, p_241855_4_, p_241855_3_, 8);
            return true;
        }
        else
        {
            return false;
        }
    }
}
