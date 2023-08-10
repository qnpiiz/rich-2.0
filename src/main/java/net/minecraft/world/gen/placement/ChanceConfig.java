package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;

public class ChanceConfig implements IPlacementConfig
{
    public static final Codec<ChanceConfig> field_236950_a_ = Codec.INT.fieldOf("chance").xmap(ChanceConfig::new, (p_236951_0_) ->
    {
        return p_236951_0_.chance;
    }).codec();
    public final int chance;

    public ChanceConfig(int chance)
    {
        this.chance = chance;
    }
}
