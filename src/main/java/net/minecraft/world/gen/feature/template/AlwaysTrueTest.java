package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;

public class AlwaysTrueTest extends PosRuleTest
{
    public static final Codec<AlwaysTrueTest> field_237099_a_;
    public static final AlwaysTrueTest field_237100_b_ = new AlwaysTrueTest();

    private AlwaysTrueTest()
    {
    }

    public boolean func_230385_a_(BlockPos p_230385_1_, BlockPos p_230385_2_, BlockPos p_230385_3_, Random p_230385_4_)
    {
        return true;
    }

    protected IPosRuleTests<?> func_230384_a_()
    {
        return IPosRuleTests.field_237103_a_;
    }

    static
    {
        field_237099_a_ = Codec.unit(() ->
        {
            return field_237100_b_;
        });
    }
}
