package net.minecraft.world.gen.feature.template;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;

public interface IStructureProcessorType<P extends StructureProcessor>
{
    IStructureProcessorType<BlockIgnoreStructureProcessor> BLOCK_IGNORE = func_237139_a_("block_ignore", BlockIgnoreStructureProcessor.field_237073_a_);
    IStructureProcessorType<IntegrityProcessor> BLOCK_ROT = func_237139_a_("block_rot", IntegrityProcessor.field_237077_a_);
    IStructureProcessorType<GravityStructureProcessor> GRAVITY = func_237139_a_("gravity", GravityStructureProcessor.field_237081_a_);
    IStructureProcessorType<JigsawReplacementStructureProcessor> JIGSAW_REPLACEMENT = func_237139_a_("jigsaw_replacement", JigsawReplacementStructureProcessor.field_237085_a_);
    IStructureProcessorType<RuleStructureProcessor> RULE = func_237139_a_("rule", RuleStructureProcessor.field_237125_a_);
    IStructureProcessorType<NopProcessor> NOP = func_237139_a_("nop", NopProcessor.field_237097_a_);
    IStructureProcessorType<BlockMosinessProcessor> field_237135_g_ = func_237139_a_("block_age", BlockMosinessProcessor.field_237062_a_);
    IStructureProcessorType<BlackStoneReplacementProcessor> field_237136_h_ = func_237139_a_("blackstone_replace", BlackStoneReplacementProcessor.field_237057_a_);
    IStructureProcessorType<LavaSubmergingProcessor> field_241534_i_ = func_237139_a_("lava_submerged_block", LavaSubmergingProcessor.field_241531_a_);
    Codec<StructureProcessor> field_237137_i_ = Registry.STRUCTURE_PROCESSOR.dispatch("processor_type", StructureProcessor::getType, IStructureProcessorType::codec);
    Codec<StructureProcessorList> field_242920_k = field_237137_i_.listOf().xmap(StructureProcessorList::new, StructureProcessorList::func_242919_a);
    Codec<StructureProcessorList> field_242921_l = Codec.either(field_242920_k.fieldOf("processors").codec(), field_242920_k).xmap((p_242923_0_) ->
    {
        return p_242923_0_.map((p_242926_0_) -> {
            return p_242926_0_;
        }, (p_242925_0_) -> {
            return p_242925_0_;
        });
    }, Either::left);
    Codec<Supplier<StructureProcessorList>> field_242922_m = RegistryKeyCodec.create(Registry.STRUCTURE_PROCESSOR_LIST_KEY, field_242921_l);

    Codec<P> codec();

    static <P extends StructureProcessor> IStructureProcessorType<P> func_237139_a_(String p_237139_0_, Codec<P> p_237139_1_)
    {
        return Registry.register(Registry.STRUCTURE_PROCESSOR, p_237139_0_, () ->
        {
            return p_237139_1_;
        });
    }
}
