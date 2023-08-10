package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class AbstractSphereReplaceConfig extends Feature<SphereReplaceConfig>
{
    public AbstractSphereReplaceConfig(Codec<SphereReplaceConfig> p_i241977_1_)
    {
        super(p_i241977_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, SphereReplaceConfig p_241855_5_)
    {
        boolean flag = false;
        int i = p_241855_5_.radius.func_242259_a(p_241855_3_);

        for (int j = p_241855_4_.getX() - i; j <= p_241855_4_.getX() + i; ++j)
        {
            for (int k = p_241855_4_.getZ() - i; k <= p_241855_4_.getZ() + i; ++k)
            {
                int l = j - p_241855_4_.getX();
                int i1 = k - p_241855_4_.getZ();

                if (l * l + i1 * i1 <= i * i)
                {
                    for (int j1 = p_241855_4_.getY() - p_241855_5_.field_242809_d; j1 <= p_241855_4_.getY() + p_241855_5_.field_242809_d; ++j1)
                    {
                        BlockPos blockpos = new BlockPos(j, j1, k);
                        Block block = p_241855_1_.getBlockState(blockpos).getBlock();

                        for (BlockState blockstate : p_241855_5_.targets)
                        {
                            if (blockstate.isIn(block))
                            {
                                p_241855_1_.setBlockState(blockpos, p_241855_5_.state, 2);
                                flag = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return flag;
    }
}
