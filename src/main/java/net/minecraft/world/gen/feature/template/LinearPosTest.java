package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LinearPosTest extends PosRuleTest
{
    public static final Codec<LinearPosTest> field_237087_a_ = RecordCodecBuilder.create((p_237092_0_) ->
    {
        return p_237092_0_.group(Codec.FLOAT.fieldOf("min_chance").orElse(0.0F).forGetter((p_237096_0_) -> {
            return p_237096_0_.field_237088_b_;
        }), Codec.FLOAT.fieldOf("max_chance").orElse(0.0F).forGetter((p_237095_0_) -> {
            return p_237095_0_.field_237089_d_;
        }), Codec.INT.fieldOf("min_dist").orElse(0).forGetter((p_237094_0_) -> {
            return p_237094_0_.field_237090_e_;
        }), Codec.INT.fieldOf("max_dist").orElse(0).forGetter((p_237093_0_) -> {
            return p_237093_0_.field_237091_f_;
        })).apply(p_237092_0_, LinearPosTest::new);
    });
    private final float field_237088_b_;
    private final float field_237089_d_;
    private final int field_237090_e_;
    private final int field_237091_f_;

    public LinearPosTest(float p_i232116_1_, float p_i232116_2_, int p_i232116_3_, int p_i232116_4_)
    {
        if (p_i232116_3_ >= p_i232116_4_)
        {
            throw new IllegalArgumentException("Invalid range: [" + p_i232116_3_ + "," + p_i232116_4_ + "]");
        }
        else
        {
            this.field_237088_b_ = p_i232116_1_;
            this.field_237089_d_ = p_i232116_2_;
            this.field_237090_e_ = p_i232116_3_;
            this.field_237091_f_ = p_i232116_4_;
        }
    }

    public boolean func_230385_a_(BlockPos p_230385_1_, BlockPos p_230385_2_, BlockPos p_230385_3_, Random p_230385_4_)
    {
        int i = p_230385_2_.manhattanDistance(p_230385_3_);
        float f = p_230385_4_.nextFloat();
        return (double)f <= MathHelper.clampedLerp((double)this.field_237088_b_, (double)this.field_237089_d_, MathHelper.func_233020_c_((double)i, (double)this.field_237090_e_, (double)this.field_237091_f_));
    }

    protected IPosRuleTests<?> func_230384_a_()
    {
        return IPosRuleTests.field_237104_b_;
    }
}
