package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SingleRandomFeature implements IFeatureConfig
{
    public static final Codec<SingleRandomFeature> field_236642_a_ = ConfiguredFeature.field_242764_c.fieldOf("features").xmap(SingleRandomFeature::new, (p_236643_0_) ->
    {
        return p_236643_0_.features;
    }).codec();
    public final List < Supplier < ConfiguredFeature <? , ? >>> features;

    public SingleRandomFeature(List < Supplier < ConfiguredFeature <? , ? >>> features)
    {
        this.features = features;
    }

    public Stream < ConfiguredFeature <? , ? >> func_241856_an_()
    {
        return this.features.stream().flatMap((p_242826_0_) ->
        {
            return p_242826_0_.get().func_242768_d();
        });
    }
}
