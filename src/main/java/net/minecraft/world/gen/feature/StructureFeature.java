package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.StructureSeparationSettings;

public class StructureFeature<FC extends IFeatureConfig, F extends Structure<FC>>
{
    public static final Codec < StructureFeature <? , ? >> field_236267_a_ = Registry.STRUCTURE_FEATURE.dispatch((p_236271_0_) ->
    {
        return p_236271_0_.field_236268_b_;
    }, Structure::func_236398_h_);
    public static final Codec < Supplier < StructureFeature <? , ? >>> field_244391_b_ = RegistryKeyCodec.create(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, field_236267_a_);
    public static final Codec < List < Supplier < StructureFeature <? , ? >>> > field_242770_c = RegistryKeyCodec.getValueCodecs(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, field_236267_a_);
    public final F field_236268_b_;
    public final FC field_236269_c_;

    public StructureFeature(F p_i231937_1_, FC p_i231937_2_)
    {
        this.field_236268_b_ = p_i231937_1_;
        this.field_236269_c_ = p_i231937_2_;
    }

    public StructureStart<?> func_242771_a(DynamicRegistries p_242771_1_, ChunkGenerator p_242771_2_, BiomeProvider p_242771_3_, TemplateManager p_242771_4_, long p_242771_5_, ChunkPos p_242771_7_, Biome p_242771_8_, int p_242771_9_, StructureSeparationSettings p_242771_10_)
    {
        return this.field_236268_b_.func_242785_a(p_242771_1_, p_242771_2_, p_242771_3_, p_242771_4_, p_242771_5_, p_242771_7_, p_242771_8_, p_242771_9_, new SharedSeedRandom(), p_242771_10_, this.field_236269_c_);
    }
}
