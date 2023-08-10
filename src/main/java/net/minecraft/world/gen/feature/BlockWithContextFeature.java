package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class BlockWithContextFeature extends Feature<BlockWithContextConfig>
{
    public BlockWithContextFeature(Codec<BlockWithContextConfig> p_i231991_1_)
    {
        super(p_i231991_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, BlockWithContextConfig p_241855_5_)
    {
        if (p_241855_5_.placeOn.contains(p_241855_1_.getBlockState(p_241855_4_.down())) && p_241855_5_.placeIn.contains(p_241855_1_.getBlockState(p_241855_4_)) && p_241855_5_.placeUnder.contains(p_241855_1_.getBlockState(p_241855_4_.up())))
        {
            p_241855_1_.setBlockState(p_241855_4_, p_241855_5_.toPlace, 2);
            return true;
        }
        else
        {
            return false;
        }
    }
}
