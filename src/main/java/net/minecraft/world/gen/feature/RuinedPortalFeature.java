package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.structure.RuinedPortalStructure;

public class RuinedPortalFeature implements IFeatureConfig
{
    public static final Codec<RuinedPortalFeature> field_236627_a_ = RuinedPortalStructure.Location.field_236342_h_.fieldOf("portal_type").xmap(RuinedPortalFeature::new, (p_236629_0_) ->
    {
        return p_236629_0_.field_236628_b_;
    }).codec();
    public final RuinedPortalStructure.Location field_236628_b_;

    public RuinedPortalFeature(RuinedPortalStructure.Location p_i232016_1_)
    {
        this.field_236628_b_ = p_i232016_1_;
    }
}
