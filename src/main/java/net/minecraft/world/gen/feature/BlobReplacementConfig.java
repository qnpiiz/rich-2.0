package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class BlobReplacementConfig implements IFeatureConfig
{
    public static final Codec<BlobReplacementConfig> field_242817_a = RecordCodecBuilder.create((p_242822_0_) ->
    {
        return p_242822_0_.group(BlockState.CODEC.fieldOf("target").forGetter((p_242825_0_) -> {
            return p_242825_0_.field_242818_b;
        }), BlockState.CODEC.fieldOf("state").forGetter((p_242824_0_) -> {
            return p_242824_0_.field_242819_c;
        }), FeatureSpread.field_242249_a.fieldOf("radius").forGetter((p_242821_0_) -> {
            return p_242821_0_.field_242820_d;
        })).apply(p_242822_0_, BlobReplacementConfig::new);
    });
    public final BlockState field_242818_b;
    public final BlockState field_242819_c;
    private final FeatureSpread field_242820_d;

    public BlobReplacementConfig(BlockState p_i241993_1_, BlockState p_i241993_2_, FeatureSpread p_i241993_3_)
    {
        this.field_242818_b = p_i241993_1_;
        this.field_242819_c = p_i241993_2_;
        this.field_242820_d = p_i241993_3_;
    }

    public FeatureSpread func_242823_b()
    {
        return this.field_242820_d;
    }
}
