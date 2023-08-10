package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.util.registry.Registry;

public abstract class AbstractFeatureSizeType
{
    public static final Codec<AbstractFeatureSizeType> field_236704_a_ = Registry.FEATURE_SIZE_TYPE.dispatch(AbstractFeatureSizeType::func_230370_b_, FeatureSizeType::getCodec);
    protected final OptionalInt field_236705_b_;

    protected static <S extends AbstractFeatureSizeType> RecordCodecBuilder<S, OptionalInt> func_236706_a_()
    {
        return Codec.intRange(0, 80).optionalFieldOf("min_clipped_height").xmap((p_236708_0_) ->
        {
            return p_236708_0_.map(OptionalInt::of).orElse(OptionalInt.empty());
        }, (p_236709_0_) ->
        {
            return p_236709_0_.isPresent() ? Optional.of(p_236709_0_.getAsInt()) : Optional.empty();
        }).forGetter((p_236707_0_) ->
        {
            return p_236707_0_.field_236705_b_;
        });
    }

    public AbstractFeatureSizeType(OptionalInt p_i232022_1_)
    {
        this.field_236705_b_ = p_i232022_1_;
    }

    protected abstract FeatureSizeType<?> func_230370_b_();

    public abstract int func_230369_a_(int p_230369_1_, int p_230369_2_);

    public OptionalInt func_236710_c_()
    {
        return this.field_236705_b_;
    }
}
