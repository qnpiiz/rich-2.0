package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class ConfiguredRandomFeatureList
{
    public static final Codec<ConfiguredRandomFeatureList> field_236430_a_ = RecordCodecBuilder.create((p_236433_0_) ->
    {
        return p_236433_0_.group(ConfiguredFeature.field_236264_b_.fieldOf("feature").forGetter((p_242789_0_) -> {
            return p_242789_0_.feature;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter((p_236432_0_) -> {
            return p_236432_0_.chance;
        })).apply(p_236433_0_, ConfiguredRandomFeatureList::new);
    });
    public final Supplier < ConfiguredFeature <? , ? >> feature;
    public final float chance;

    public ConfiguredRandomFeatureList(ConfiguredFeature <? , ? > p_i225822_1_, float p_i225822_2_)
    {
        this(() ->
        {
            return p_i225822_1_;
        }, p_i225822_2_);
    }

    private ConfiguredRandomFeatureList(Supplier < ConfiguredFeature <? , ? >> p_i241980_1_, float p_i241980_2_)
    {
        this.feature = p_i241980_1_;
        this.chance = p_i241980_2_;
    }

    public boolean func_242787_a(ISeedReader p_242787_1_, ChunkGenerator p_242787_2_, Random p_242787_3_, BlockPos p_242787_4_)
    {
        return this.feature.get().func_242765_a(p_242787_1_, p_242787_2_, p_242787_3_, p_242787_4_);
    }
}
