package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class IcePathFeature extends AbstractSphereReplaceConfig
{
    public IcePathFeature(Codec<SphereReplaceConfig> p_i231961_1_)
    {
        super(p_i231961_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, SphereReplaceConfig p_241855_5_)
    {
        while (p_241855_1_.isAirBlock(p_241855_4_) && p_241855_4_.getY() > 2)
        {
            p_241855_4_ = p_241855_4_.down();
        }

        return !p_241855_1_.getBlockState(p_241855_4_).isIn(Blocks.SNOW_BLOCK) ? false : super.func_241855_a(p_241855_1_, p_241855_2_, p_241855_3_, p_241855_4_, p_241855_5_);
    }
}
