package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface IRuleTestType<P extends RuleTest>
{
    IRuleTestType<AlwaysTrueRuleTest> ALWAYS_TRUE = func_237129_a_("always_true", AlwaysTrueRuleTest.field_237043_a_);
    IRuleTestType<BlockMatchRuleTest> BLOCK_MATCH = func_237129_a_("block_match", BlockMatchRuleTest.field_237075_a_);
    IRuleTestType<BlockStateMatchRuleTest> BLOCKSTATE_MATCH = func_237129_a_("blockstate_match", BlockStateMatchRuleTest.field_237079_a_);
    IRuleTestType<TagMatchRuleTest> TAG_MATCH = func_237129_a_("tag_match", TagMatchRuleTest.field_237161_a_);
    IRuleTestType<RandomBlockMatchRuleTest> RANDOM_BLOCK_MATCH = func_237129_a_("random_block_match", RandomBlockMatchRuleTest.field_237117_a_);
    IRuleTestType<RandomBlockStateMatchRuleTest> RANDOM_BLOCKSTATE_MATCH = func_237129_a_("random_blockstate_match", RandomBlockStateMatchRuleTest.field_237121_a_);

    Codec<P> codec();

    static <P extends RuleTest> IRuleTestType<P> func_237129_a_(String p_237129_0_, Codec<P> p_237129_1_)
    {
        return Registry.register(Registry.RULE_TEST, p_237129_0_, () ->
        {
            return p_237129_1_;
        });
    }
}
