package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;

public class BlockWithContextConfig implements IFeatureConfig
{
    public static final Codec<BlockWithContextConfig> field_236636_a_ = RecordCodecBuilder.create((p_236638_0_) ->
    {
        return p_236638_0_.group(BlockState.CODEC.fieldOf("to_place").forGetter((p_236641_0_) -> {
            return p_236641_0_.toPlace;
        }), BlockState.CODEC.listOf().fieldOf("place_on").forGetter((p_236640_0_) -> {
            return p_236640_0_.placeOn;
        }), BlockState.CODEC.listOf().fieldOf("place_in").forGetter((p_236639_0_) -> {
            return p_236639_0_.placeIn;
        }), BlockState.CODEC.listOf().fieldOf("place_under").forGetter((p_236637_0_) -> {
            return p_236637_0_.placeUnder;
        })).apply(p_236638_0_, BlockWithContextConfig::new);
    });
    public final BlockState toPlace;
    public final List<BlockState> placeOn;
    public final List<BlockState> placeIn;
    public final List<BlockState> placeUnder;

    public BlockWithContextConfig(BlockState toPlace, List<BlockState> placeOn, List<BlockState> placeIn, List<BlockState> placeUnder)
    {
        this.toPlace = toPlace;
        this.placeOn = placeOn;
        this.placeIn = placeIn;
        this.placeUnder = placeUnder;
    }
}
