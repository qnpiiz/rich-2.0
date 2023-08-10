package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class TwoLayerFeature extends AbstractFeatureSizeType
{
    public static final Codec<TwoLayerFeature> field_236728_c_ = RecordCodecBuilder.create((p_236732_0_) ->
    {
        return p_236732_0_.group(Codec.intRange(0, 81).fieldOf("limit").orElse(1).forGetter((p_236735_0_) -> {
            return p_236735_0_.field_236729_d_;
        }), Codec.intRange(0, 16).fieldOf("lower_size").orElse(0).forGetter((p_236734_0_) -> {
            return p_236734_0_.field_236730_e_;
        }), Codec.intRange(0, 16).fieldOf("upper_size").orElse(1).forGetter((p_236733_0_) -> {
            return p_236733_0_.field_236731_f_;
        }), func_236706_a_()).apply(p_236732_0_, TwoLayerFeature::new);
    });
    private final int field_236729_d_;
    private final int field_236730_e_;
    private final int field_236731_f_;

    public TwoLayerFeature(int p_i232025_1_, int p_i232025_2_, int p_i232025_3_)
    {
        this(p_i232025_1_, p_i232025_2_, p_i232025_3_, OptionalInt.empty());
    }

    public TwoLayerFeature(int p_i232026_1_, int p_i232026_2_, int p_i232026_3_, OptionalInt p_i232026_4_)
    {
        super(p_i232026_4_);
        this.field_236729_d_ = p_i232026_1_;
        this.field_236730_e_ = p_i232026_2_;
        this.field_236731_f_ = p_i232026_3_;
    }

    protected FeatureSizeType<?> func_230370_b_()
    {
        return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
    }

    public int func_230369_a_(int p_230369_1_, int p_230369_2_)
    {
        return p_230369_2_ < this.field_236729_d_ ? this.field_236730_e_ : this.field_236731_f_;
    }
}
