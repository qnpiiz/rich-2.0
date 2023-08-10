package net.minecraft.world.gen.foliageplacer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;

public class PineFoliagePlacer extends FoliagePlacer
{
    public static final Codec<PineFoliagePlacer> field_236784_a_ = RecordCodecBuilder.create((p_242834_0_) ->
    {
        return func_242830_b(p_242834_0_).and(FeatureSpread.func_242254_a(0, 16, 8).fieldOf("height").forGetter((p_242833_0_) -> {
            return p_242833_0_.field_236785_b_;
        })).apply(p_242834_0_, PineFoliagePlacer::new);
    });
    private final FeatureSpread field_236785_b_;

    public PineFoliagePlacer(FeatureSpread p_i242002_1_, FeatureSpread p_i242002_2_, FeatureSpread p_i242002_3_)
    {
        super(p_i242002_1_, p_i242002_2_);
        this.field_236785_b_ = p_i242002_3_;
    }

    protected FoliagePlacerType<?> func_230371_a_()
    {
        return FoliagePlacerType.PINE;
    }

    protected void func_230372_a_(IWorldGenerationReader p_230372_1_, Random p_230372_2_, BaseTreeFeatureConfig p_230372_3_, int p_230372_4_, FoliagePlacer.Foliage p_230372_5_, int p_230372_6_, int p_230372_7_, Set<BlockPos> p_230372_8_, int p_230372_9_, MutableBoundingBox p_230372_10_)
    {
        int i = 0;

        for (int j = p_230372_9_; j >= p_230372_9_ - p_230372_6_; --j)
        {
            this.func_236753_a_(p_230372_1_, p_230372_2_, p_230372_3_, p_230372_5_.func_236763_a_(), i, p_230372_8_, j, p_230372_5_.func_236765_c_(), p_230372_10_);

            if (i >= 1 && j == p_230372_9_ - p_230372_6_ + 1)
            {
                --i;
            }
            else if (i < p_230372_7_ + p_230372_5_.func_236764_b_())
            {
                ++i;
            }
        }
    }

    public int func_230376_a_(Random p_230376_1_, int p_230376_2_)
    {
        return super.func_230376_a_(p_230376_1_, p_230376_2_) + p_230376_1_.nextInt(p_230376_2_ + 1);
    }

    public int func_230374_a_(Random p_230374_1_, int p_230374_2_, BaseTreeFeatureConfig p_230374_3_)
    {
        return this.field_236785_b_.func_242259_a(p_230374_1_);
    }

    protected boolean func_230373_a_(Random p_230373_1_, int p_230373_2_, int p_230373_3_, int p_230373_4_, int p_230373_5_, boolean p_230373_6_)
    {
        return p_230373_2_ == p_230373_5_ && p_230373_4_ == p_230373_5_ && p_230373_5_ > 0;
    }
}
