package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.GenerationStage;

public class CaveEdgeConfig implements IPlacementConfig
{
    public static final Codec<CaveEdgeConfig> field_236946_a_ = RecordCodecBuilder.create((p_236947_0_) ->
    {
        return p_236947_0_.group(GenerationStage.Carving.field_236074_c_.fieldOf("step").forGetter((p_236949_0_) -> {
            return p_236949_0_.step;
        }), Codec.FLOAT.fieldOf("probability").forGetter((p_236948_0_) -> {
            return p_236948_0_.probability;
        })).apply(p_236947_0_, CaveEdgeConfig::new);
    });
    protected final GenerationStage.Carving step;
    protected final float probability;

    public CaveEdgeConfig(GenerationStage.Carving step, float probability)
    {
        this.step = step;
        this.probability = probability;
    }
}
