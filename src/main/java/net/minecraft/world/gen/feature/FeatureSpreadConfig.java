package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class FeatureSpreadConfig implements IPlacementConfig, IFeatureConfig
{
    public static final Codec<FeatureSpreadConfig> field_242797_a = FeatureSpread.func_242254_a(-10, 128, 128).fieldOf("count").xmap(FeatureSpreadConfig::new, FeatureSpreadConfig::func_242799_a).codec();
    private final FeatureSpread field_242798_c;

    public FeatureSpreadConfig(int p_i241982_1_)
    {
        this.field_242798_c = FeatureSpread.func_242252_a(p_i241982_1_);
    }

    public FeatureSpreadConfig(FeatureSpread p_i241983_1_)
    {
        this.field_242798_c = p_i241983_1_;
    }

    public FeatureSpread func_242799_a()
    {
        return this.field_242798_c;
    }
}
