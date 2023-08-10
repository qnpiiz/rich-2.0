package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class TwoFeatureChoiceFeature extends Feature<TwoFeatureChoiceConfig>
{
    public TwoFeatureChoiceFeature(Codec<TwoFeatureChoiceConfig> p_i231978_1_)
    {
        super(p_i231978_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, TwoFeatureChoiceConfig p_241855_5_)
    {
        boolean flag = p_241855_3_.nextBoolean();
        return flag ? p_241855_5_.field_227285_a_.get().func_242765_a(p_241855_1_, p_241855_2_, p_241855_3_, p_241855_4_) : p_241855_5_.field_227286_b_.get().func_242765_a(p_241855_1_, p_241855_2_, p_241855_3_, p_241855_4_);
    }
}
