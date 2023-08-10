package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class DecoratedFeature extends Feature<DecoratedFeatureConfig>
{
    public DecoratedFeature(Codec<DecoratedFeatureConfig> p_i231943_1_)
    {
        super(p_i231943_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, DecoratedFeatureConfig p_241855_5_)
    {
        MutableBoolean mutableboolean = new MutableBoolean();
        p_241855_5_.decorator.func_242876_a(new WorldDecoratingHelper(p_241855_1_, p_241855_2_), p_241855_3_, p_241855_4_).forEach((p_242772_5_) ->
        {
            if (p_241855_5_.feature.get().func_242765_a(p_241855_1_, p_241855_2_, p_241855_3_, p_242772_5_))
            {
                mutableboolean.setTrue();
            }
        });
        return mutableboolean.isTrue();
    }

    public String toString()
    {
        return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getKey(this));
    }
}
