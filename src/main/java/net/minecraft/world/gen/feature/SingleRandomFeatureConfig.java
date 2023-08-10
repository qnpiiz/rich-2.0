package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class SingleRandomFeatureConfig extends Feature<SingleRandomFeature>
{
    public SingleRandomFeatureConfig(Codec<SingleRandomFeature> p_i231992_1_)
    {
        super(p_i231992_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, SingleRandomFeature p_241855_5_)
    {
        int i = p_241855_3_.nextInt(p_241855_5_.features.size());
        ConfiguredFeature <? , ? > configuredfeature = p_241855_5_.features.get(i).get();
        return configuredfeature.func_242765_a(p_241855_1_, p_241855_2_, p_241855_3_, p_241855_4_);
    }
}
