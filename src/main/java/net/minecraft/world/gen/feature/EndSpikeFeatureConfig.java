package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class EndSpikeFeatureConfig implements IFeatureConfig
{
    public static final Codec<EndSpikeFeatureConfig> field_236644_a_ = RecordCodecBuilder.create((p_236645_0_) ->
    {
        return p_236645_0_.group(Codec.BOOL.fieldOf("crystal_invulnerable").orElse(false).forGetter((p_236648_0_) -> {
            return p_236648_0_.crystalInvulnerable;
        }), EndSpikeFeature.EndSpike.field_236357_a_.listOf().fieldOf("spikes").forGetter((p_236647_0_) -> {
            return p_236647_0_.spikes;
        }), BlockPos.CODEC.optionalFieldOf("crystal_beam_target").forGetter((p_236646_0_) -> {
            return Optional.ofNullable(p_236646_0_.crystalBeamTarget);
        })).apply(p_236645_0_, EndSpikeFeatureConfig::new);
    });
    private final boolean crystalInvulnerable;
    private final List<EndSpikeFeature.EndSpike> spikes;
    @Nullable
    private final BlockPos crystalBeamTarget;

    public EndSpikeFeatureConfig(boolean crystalInvulnerable, List<EndSpikeFeature.EndSpike> spikes, @Nullable BlockPos crystalBeamTarget)
    {
        this(crystalInvulnerable, spikes, Optional.ofNullable(crystalBeamTarget));
    }

    private EndSpikeFeatureConfig(boolean p_i232017_1_, List<EndSpikeFeature.EndSpike> p_i232017_2_, Optional<BlockPos> p_i232017_3_)
    {
        this.crystalInvulnerable = p_i232017_1_;
        this.spikes = p_i232017_2_;
        this.crystalBeamTarget = p_i232017_3_.orElse((BlockPos)null);
    }

    public boolean isCrystalInvulnerable()
    {
        return this.crystalInvulnerable;
    }

    public List<EndSpikeFeature.EndSpike> getSpikes()
    {
        return this.spikes;
    }

    @Nullable
    public BlockPos getCrystalBeamTarget()
    {
        return this.crystalBeamTarget;
    }
}
