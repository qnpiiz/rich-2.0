package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.registry.Registry;

public class LiquidsConfig implements IFeatureConfig
{
    public static final Codec<LiquidsConfig> field_236649_a_ = RecordCodecBuilder.create((p_236650_0_) ->
    {
        return p_236650_0_.group(FluidState.field_237213_a_.fieldOf("state").forGetter((p_236655_0_) -> {
            return p_236655_0_.state;
        }), Codec.BOOL.fieldOf("requires_block_below").orElse(true).forGetter((p_236654_0_) -> {
            return p_236654_0_.needsBlockBelow;
        }), Codec.INT.fieldOf("rock_count").orElse(4).forGetter((p_236653_0_) -> {
            return p_236653_0_.rockAmount;
        }), Codec.INT.fieldOf("hole_count").orElse(1).forGetter((p_236652_0_) -> {
            return p_236652_0_.holeAmount;
        }), Registry.BLOCK.listOf().fieldOf("valid_blocks").<Set<Block>>xmap(ImmutableSet::copyOf, ImmutableList::copyOf).forGetter((p_236651_0_) -> {
            return p_236651_0_.acceptedBlocks;
        })).apply(p_236650_0_, LiquidsConfig::new);
    });
    public final FluidState state;
    public final boolean needsBlockBelow;
    public final int rockAmount;
    public final int holeAmount;
    public final Set<Block> acceptedBlocks;

    public LiquidsConfig(FluidState p_i225841_1_, boolean p_i225841_2_, int p_i225841_3_, int p_i225841_4_, Set<Block> p_i225841_5_)
    {
        this.state = p_i225841_1_;
        this.needsBlockBelow = p_i225841_2_;
        this.rockAmount = p_i225841_3_;
        this.holeAmount = p_i225841_4_;
        this.acceptedBlocks = p_i225841_5_;
    }
}
