package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.IDecoratable;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public class ConfiguredPlacement<DC extends IPlacementConfig> implements IDecoratable < ConfiguredPlacement<? >>
{
    public static final Codec < ConfiguredPlacement<? >> field_236952_a_ = Registry.DECORATOR.dispatch("type", (p_236954_0_) ->
    {
        return p_236954_0_.decorator;
    }, Placement::getCodec);
    private final Placement<DC> decorator;
    private final DC config;

    public ConfiguredPlacement(Placement<DC> decorator, DC config)
    {
        this.decorator = decorator;
        this.config = config;
    }

    public Stream<BlockPos> func_242876_a(WorldDecoratingHelper p_242876_1_, Random p_242876_2_, BlockPos p_242876_3_)
    {
        return this.decorator.func_241857_a(p_242876_1_, p_242876_2_, this.config, p_242876_3_);
    }

    public String toString()
    {
        return String.format("[%s %s]", Registry.DECORATOR.getKey(this.decorator), this.config);
    }

    public ConfiguredPlacement<?> withPlacement(ConfiguredPlacement<?> p_227228_1_)
    {
        return new ConfiguredPlacement<>(Placement.field_242896_B, new DecoratedPlacementConfig(p_227228_1_, this));
    }

    public DC func_242877_b()
    {
        return this.config;
    }
}
