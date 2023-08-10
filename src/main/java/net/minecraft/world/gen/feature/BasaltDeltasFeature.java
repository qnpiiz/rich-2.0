package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class BasaltDeltasFeature implements IFeatureConfig
{
    public static final Codec<BasaltDeltasFeature> field_236495_a_ = RecordCodecBuilder.create((p_242803_0_) ->
    {
        return p_242803_0_.group(BlockState.CODEC.fieldOf("contents").forGetter((p_236506_0_) -> {
            return p_236506_0_.field_236496_b_;
        }), BlockState.CODEC.fieldOf("rim").forGetter((p_236505_0_) -> {
            return p_236505_0_.field_236497_c_;
        }), FeatureSpread.func_242254_a(0, 8, 8).fieldOf("size").forGetter((p_242805_0_) -> {
            return p_242805_0_.field_242800_d;
        }), FeatureSpread.func_242254_a(0, 8, 8).fieldOf("rim_size").forGetter((p_242802_0_) -> {
            return p_242802_0_.field_242801_e;
        })).apply(p_242803_0_, BasaltDeltasFeature::new);
    });
    private final BlockState field_236496_b_;
    private final BlockState field_236497_c_;
    private final FeatureSpread field_242800_d;
    private final FeatureSpread field_242801_e;

    public BasaltDeltasFeature(BlockState p_i241985_1_, BlockState p_i241985_2_, FeatureSpread p_i241985_3_, FeatureSpread p_i241985_4_)
    {
        this.field_236496_b_ = p_i241985_1_;
        this.field_236497_c_ = p_i241985_2_;
        this.field_242800_d = p_i241985_3_;
        this.field_242801_e = p_i241985_4_;
    }

    public BlockState func_242804_b()
    {
        return this.field_236496_b_;
    }

    public BlockState func_242806_c()
    {
        return this.field_236497_c_;
    }

    public FeatureSpread func_242807_d()
    {
        return this.field_242800_d;
    }

    public FeatureSpread func_242808_e()
    {
        return this.field_242801_e;
    }
}
