package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class FillLayerFeature extends Feature<FillLayerConfig>
{
    public FillLayerFeature(Codec<FillLayerConfig> p_i231954_1_)
    {
        super(p_i231954_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, FillLayerConfig p_241855_5_)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
                int k = p_241855_4_.getX() + i;
                int l = p_241855_4_.getZ() + j;
                int i1 = p_241855_5_.height;
                blockpos$mutable.setPos(k, i1, l);

                if (p_241855_1_.getBlockState(blockpos$mutable).isAir())
                {
                    p_241855_1_.setBlockState(blockpos$mutable, p_241855_5_.state, 2);
                }
            }
        }

        return true;
    }
}
