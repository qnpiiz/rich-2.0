package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class AtSurfaceWithExtraConfig implements IPlacementConfig
{
    public static final Codec<AtSurfaceWithExtraConfig> field_236973_a_ = RecordCodecBuilder.create((p_236974_0_) ->
    {
        return p_236974_0_.group(Codec.INT.fieldOf("count").forGetter((p_236977_0_) -> {
            return p_236977_0_.count;
        }), Codec.FLOAT.fieldOf("extra_chance").forGetter((p_236976_0_) -> {
            return p_236976_0_.extraChance;
        }), Codec.INT.fieldOf("extra_count").forGetter((p_236975_0_) -> {
            return p_236975_0_.extraCount;
        })).apply(p_236974_0_, AtSurfaceWithExtraConfig::new);
    });
    public final int count;
    public final float extraChance;
    public final int extraCount;

    public AtSurfaceWithExtraConfig(int count, float extraChanceIn, int extraCountIn)
    {
        this.count = count;
        this.extraChance = extraChanceIn;
        this.extraCount = extraCountIn;
    }
}
