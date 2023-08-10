package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class ReplaceBlockFeature extends Feature<ReplaceBlockConfig>
{
    public ReplaceBlockFeature(Codec<ReplaceBlockConfig> p_i231983_1_)
    {
        super(p_i231983_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, ReplaceBlockConfig p_241855_5_)
    {
        if (p_241855_1_.getBlockState(p_241855_4_).isIn(p_241855_5_.target.getBlock()))
        {
            p_241855_1_.setBlockState(p_241855_4_, p_241855_5_.state, 2);
        }

        return true;
    }
}
