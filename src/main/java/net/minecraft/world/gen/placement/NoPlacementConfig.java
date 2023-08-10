package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;

public class NoPlacementConfig implements IPlacementConfig
{
    public static final Codec<NoPlacementConfig> field_236555_a_;
    public static final NoPlacementConfig field_236556_b_ = new NoPlacementConfig();

    static
    {
        field_236555_a_ = Codec.unit(() ->
        {
            return field_236556_b_;
        });
    }
}
