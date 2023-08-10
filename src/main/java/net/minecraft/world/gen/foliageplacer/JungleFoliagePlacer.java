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

public class JungleFoliagePlacer extends FoliagePlacer
{
    public static final Codec<JungleFoliagePlacer> field_236774_a_ = RecordCodecBuilder.create((p_236776_0_) ->
    {
        return func_242830_b(p_236776_0_).and(Codec.intRange(0, 16).fieldOf("height").forGetter((p_236777_0_) -> {
            return p_236777_0_.field_236775_b_;
        })).apply(p_236776_0_, JungleFoliagePlacer::new);
    });
    protected final int field_236775_b_;

    public JungleFoliagePlacer(FeatureSpread p_i242000_1_, FeatureSpread p_i242000_2_, int p_i242000_3_)
    {
        super(p_i242000_1_, p_i242000_2_);
        this.field_236775_b_ = p_i242000_3_;
    }

    protected FoliagePlacerType<?> func_230371_a_()
    {
        return FoliagePlacerType.field_236768_g_;
    }

    protected void func_230372_a_(IWorldGenerationReader p_230372_1_, Random p_230372_2_, BaseTreeFeatureConfig p_230372_3_, int p_230372_4_, FoliagePlacer.Foliage p_230372_5_, int p_230372_6_, int p_230372_7_, Set<BlockPos> p_230372_8_, int p_230372_9_, MutableBoundingBox p_230372_10_)
    {
        int i = p_230372_5_.func_236765_c_() ? p_230372_6_ : 1 + p_230372_2_.nextInt(2);

        for (int j = p_230372_9_; j >= p_230372_9_ - i; --j)
        {
            int k = p_230372_7_ + p_230372_5_.func_236764_b_() + 1 - j;
            this.func_236753_a_(p_230372_1_, p_230372_2_, p_230372_3_, p_230372_5_.func_236763_a_(), k, p_230372_8_, j, p_230372_5_.func_236765_c_(), p_230372_10_);
        }
    }

    public int func_230374_a_(Random p_230374_1_, int p_230374_2_, BaseTreeFeatureConfig p_230374_3_)
    {
        return this.field_236775_b_;
    }

    protected boolean func_230373_a_(Random p_230373_1_, int p_230373_2_, int p_230373_3_, int p_230373_4_, int p_230373_5_, boolean p_230373_6_)
    {
        if (p_230373_2_ + p_230373_4_ >= 7)
        {
            return true;
        }
        else
        {
            return p_230373_2_ * p_230373_2_ + p_230373_4_ * p_230373_4_ > p_230373_5_ * p_230373_5_;
        }
    }
}
