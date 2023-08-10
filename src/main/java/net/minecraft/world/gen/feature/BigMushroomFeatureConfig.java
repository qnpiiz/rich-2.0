package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;

public class BigMushroomFeatureConfig implements IFeatureConfig
{
    public static final Codec<BigMushroomFeatureConfig> field_236528_a_ = RecordCodecBuilder.create((p_236530_0_) ->
    {
        return p_236530_0_.group(BlockStateProvider.CODEC.fieldOf("cap_provider").forGetter((p_236532_0_) -> {
            return p_236532_0_.field_227272_a_;
        }), BlockStateProvider.CODEC.fieldOf("stem_provider").forGetter((p_236531_0_) -> {
            return p_236531_0_.field_227273_b_;
        }), Codec.INT.fieldOf("foliage_radius").orElse(2).forGetter((p_236529_0_) -> {
            return p_236529_0_.field_227274_c_;
        })).apply(p_236530_0_, BigMushroomFeatureConfig::new);
    });
    public final BlockStateProvider field_227272_a_;
    public final BlockStateProvider field_227273_b_;
    public final int field_227274_c_;

    public BigMushroomFeatureConfig(BlockStateProvider p_i225832_1_, BlockStateProvider p_i225832_2_, int p_i225832_3_)
    {
        this.field_227272_a_ = p_i225832_1_;
        this.field_227273_b_ = p_i225832_2_;
        this.field_227274_c_ = p_i225832_3_;
    }
}
