package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface IPosRuleTests<P extends PosRuleTest>
{
    IPosRuleTests<AlwaysTrueTest> field_237103_a_ = func_237107_a_("always_true", AlwaysTrueTest.field_237099_a_);
    IPosRuleTests<LinearPosTest> field_237104_b_ = func_237107_a_("linear_pos", LinearPosTest.field_237087_a_);
    IPosRuleTests<AxisAlignedLinearPosTest> field_237105_c_ = func_237107_a_("axis_aligned_linear_pos", AxisAlignedLinearPosTest.field_237045_a_);

    Codec<P> codec();

    static <P extends PosRuleTest> IPosRuleTests<P> func_237107_a_(String p_237107_0_, Codec<P> p_237107_1_)
    {
        return Registry.register(Registry.POS_RULE_TEST, p_237107_0_, () ->
        {
            return p_237107_1_;
        });
    }
}
