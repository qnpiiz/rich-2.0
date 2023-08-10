package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

public abstract class RuleTest
{
    public static final Codec<RuleTest> field_237127_c_ = Registry.RULE_TEST.dispatch("predicate_type", RuleTest::getType, IRuleTestType::codec);

    public abstract boolean test(BlockState p_215181_1_, Random p_215181_2_);

    protected abstract IRuleTestType<?> getType();
}
