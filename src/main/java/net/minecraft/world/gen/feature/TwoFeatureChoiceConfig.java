package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TwoFeatureChoiceConfig implements IFeatureConfig
{
    public static final Codec<TwoFeatureChoiceConfig> field_236579_a_ = RecordCodecBuilder.create((p_236581_0_) ->
    {
        return p_236581_0_.group(ConfiguredFeature.field_236264_b_.fieldOf("feature_true").forGetter((p_236582_0_) -> {
            return p_236582_0_.field_227285_a_;
        }), ConfiguredFeature.field_236264_b_.fieldOf("feature_false").forGetter((p_236580_0_) -> {
            return p_236580_0_.field_227286_b_;
        })).apply(p_236581_0_, TwoFeatureChoiceConfig::new);
    });
    public final Supplier < ConfiguredFeature <? , ? >> field_227285_a_;
    public final Supplier < ConfiguredFeature <? , ? >> field_227286_b_;

    public TwoFeatureChoiceConfig(Supplier < ConfiguredFeature <? , ? >> p_i241990_1_, Supplier < ConfiguredFeature <? , ? >> p_i241990_2_)
    {
        this.field_227285_a_ = p_i241990_1_;
        this.field_227286_b_ = p_i241990_2_;
    }

    public Stream < ConfiguredFeature <? , ? >> func_241856_an_()
    {
        return Stream.concat(this.field_227285_a_.get().func_242768_d(), this.field_227286_b_.get().func_242768_d());
    }
}
