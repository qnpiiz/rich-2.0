package net.minecraft.world.gen.settings;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.Structure;

public class DimensionStructuresSettings
{
    public static final Codec<DimensionStructuresSettings> field_236190_a_ = RecordCodecBuilder.create((p_236198_0_) ->
    {
        return p_236198_0_.group(StructureSpreadSettings.field_236656_a_.optionalFieldOf("stronghold").forGetter((p_236200_0_) -> {
            return Optional.ofNullable(p_236200_0_.field_236194_e_);
        }), Codec.simpleMap(Registry.STRUCTURE_FEATURE, StructureSeparationSettings.field_236664_a_, Registry.STRUCTURE_FEATURE).fieldOf("structures").forGetter((p_236196_0_) -> {
            return p_236196_0_.field_236193_d_;
        })).apply(p_236198_0_, DimensionStructuresSettings::new);
    });
    public static final ImmutableMap < Structure<?>, StructureSeparationSettings > field_236191_b_ = ImmutableMap. < Structure<?>, StructureSeparationSettings > builder().put(Structure.field_236381_q_, new StructureSeparationSettings(32, 8, 10387312)).put(Structure.field_236370_f_, new StructureSeparationSettings(32, 8, 14357617)).put(Structure.field_236371_g_, new StructureSeparationSettings(32, 8, 14357618)).put(Structure.field_236369_e_, new StructureSeparationSettings(32, 8, 14357619)).put(Structure.field_236374_j_, new StructureSeparationSettings(32, 8, 14357620)).put(Structure.field_236366_b_, new StructureSeparationSettings(32, 8, 165745296)).put(Structure.field_236375_k_, new StructureSeparationSettings(1, 0, 0)).put(Structure.field_236376_l_, new StructureSeparationSettings(32, 5, 10387313)).put(Structure.field_236379_o_, new StructureSeparationSettings(20, 11, 10387313)).put(Structure.field_236368_d_, new StructureSeparationSettings(80, 20, 10387319)).put(Structure.field_236380_p_, new StructureSeparationSettings(1, 0, 0)).put(Structure.field_236367_c_, new StructureSeparationSettings(1, 0, 0)).put(Structure.field_236372_h_, new StructureSeparationSettings(40, 15, 34222645)).put(Structure.field_236373_i_, new StructureSeparationSettings(24, 4, 165745295)).put(Structure.field_236377_m_, new StructureSeparationSettings(20, 8, 14357621)).put(Structure.field_236383_s_, new StructureSeparationSettings(27, 4, 30084232)).put(Structure.field_236378_n_, new StructureSeparationSettings(27, 4, 30084232)).put(Structure.field_236382_r_, new StructureSeparationSettings(2, 1, 14357921)).build();
    public static final StructureSpreadSettings field_236192_c_;
    private final Map < Structure<?>, StructureSeparationSettings > field_236193_d_;
    @Nullable
    private final StructureSpreadSettings field_236194_e_;

    public DimensionStructuresSettings(Optional<StructureSpreadSettings> p_i231912_1_, Map < Structure<?>, StructureSeparationSettings > p_i231912_2_)
    {
        this.field_236194_e_ = p_i231912_1_.orElse((StructureSpreadSettings)null);
        this.field_236193_d_ = p_i231912_2_;
    }

    public DimensionStructuresSettings(boolean p_i231913_1_)
    {
        this.field_236193_d_ = Maps.newHashMap(field_236191_b_);
        this.field_236194_e_ = p_i231913_1_ ? field_236192_c_ : null;
    }

    public Map < Structure<?>, StructureSeparationSettings > func_236195_a_()
    {
        return this.field_236193_d_;
    }

    @Nullable
    public StructureSeparationSettings func_236197_a_(Structure<?> p_236197_1_)
    {
        return this.field_236193_d_.get(p_236197_1_);
    }

    @Nullable
    public StructureSpreadSettings func_236199_b_()
    {
        return this.field_236194_e_;
    }

    static
    {
        for (Structure<?> structure : Registry.STRUCTURE_FEATURE)
        {
            if (!field_236191_b_.containsKey(structure))
            {
                throw new IllegalStateException("Structure feature without default settings: " + Registry.STRUCTURE_FEATURE.getKey(structure));
            }
        }

        field_236192_c_ = new StructureSpreadSettings(32, 3, 128);
    }
}
