package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class MultipleWithChanceRandomFeature extends Feature<MultipleRandomFeatureConfig>
{
    public MultipleWithChanceRandomFeature(Codec<MultipleRandomFeatureConfig> p_i231981_1_)
    {
        super(p_i231981_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, MultipleRandomFeatureConfig p_241855_5_)
    {
        for (ConfiguredRandomFeatureList configuredrandomfeaturelist : p_241855_5_.features)
        {
            if (p_241855_3_.nextFloat() < configuredrandomfeaturelist.chance)
            {
                return configuredrandomfeaturelist.func_242787_a(p_241855_1_, p_241855_2_, p_241855_3_, p_241855_4_);
            }
        }

        return p_241855_5_.defaultFeature.get().func_242765_a(p_241855_1_, p_241855_2_, p_241855_3_, p_241855_4_);
    }
}
