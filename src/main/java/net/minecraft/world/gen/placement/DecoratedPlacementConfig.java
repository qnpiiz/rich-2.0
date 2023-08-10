package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DecoratedPlacementConfig implements IPlacementConfig
{
    public static final Codec<DecoratedPlacementConfig> field_242883_a = RecordCodecBuilder.create((p_242887_0_) ->
    {
        return p_242887_0_.group(ConfiguredPlacement.field_236952_a_.fieldOf("outer").forGetter(DecoratedPlacementConfig::func_242886_a), ConfiguredPlacement.field_236952_a_.fieldOf("inner").forGetter(DecoratedPlacementConfig::func_242888_b)).apply(p_242887_0_, DecoratedPlacementConfig::new);
    });
    private final ConfiguredPlacement<?> field_242884_c;
    private final ConfiguredPlacement<?> field_242885_d;

    public DecoratedPlacementConfig(ConfiguredPlacement<?> p_i242020_1_, ConfiguredPlacement<?> p_i242020_2_)
    {
        this.field_242884_c = p_i242020_1_;
        this.field_242885_d = p_i242020_2_;
    }

    public ConfiguredPlacement<?> func_242886_a()
    {
        return this.field_242884_c;
    }

    public ConfiguredPlacement<?> func_242888_b()
    {
        return this.field_242885_d;
    }
}
