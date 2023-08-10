package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class ReplaceBlockConfig implements IFeatureConfig
{
    public static final Codec<ReplaceBlockConfig> field_236604_a_ = RecordCodecBuilder.create((p_236606_0_) ->
    {
        return p_236606_0_.group(BlockState.CODEC.fieldOf("target").forGetter((p_236607_0_) -> {
            return p_236607_0_.target;
        }), BlockState.CODEC.fieldOf("state").forGetter((p_236605_0_) -> {
            return p_236605_0_.state;
        })).apply(p_236606_0_, ReplaceBlockConfig::new);
    });
    public final BlockState target;
    public final BlockState state;

    public ReplaceBlockConfig(BlockState target, BlockState state)
    {
        this.target = target;
        this.state = state;
    }
}
