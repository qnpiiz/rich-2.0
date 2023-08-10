package net.minecraft.world.gen.foliageplacer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;

public class MegaPineFoliagePlacer extends FoliagePlacer
{
    public static final Codec<MegaPineFoliagePlacer> field_236778_a_ = RecordCodecBuilder.create((p_242832_0_) ->
    {
        return func_242830_b(p_242832_0_).and(FeatureSpread.func_242254_a(0, 16, 8).fieldOf("crown_height").forGetter((p_242831_0_) -> {
            return p_242831_0_.field_236780_c_;
        })).apply(p_242832_0_, MegaPineFoliagePlacer::new);
    });
    private final FeatureSpread field_236780_c_;

    public MegaPineFoliagePlacer(FeatureSpread p_i242001_1_, FeatureSpread p_i242001_2_, FeatureSpread p_i242001_3_)
    {
        super(p_i242001_1_, p_i242001_2_);
        this.field_236780_c_ = p_i242001_3_;
    }

    protected FoliagePlacerType<?> func_230371_a_()
    {
        return FoliagePlacerType.field_236769_h_;
    }

    protected void func_230372_a_(IWorldGenerationReader p_230372_1_, Random p_230372_2_, BaseTreeFeatureConfig p_230372_3_, int p_230372_4_, FoliagePlacer.Foliage p_230372_5_, int p_230372_6_, int p_230372_7_, Set<BlockPos> p_230372_8_, int p_230372_9_, MutableBoundingBox p_230372_10_)
    {
        BlockPos blockpos = p_230372_5_.func_236763_a_();
        int i = 0;

        for (int j = blockpos.getY() - p_230372_6_ + p_230372_9_; j <= blockpos.getY() + p_230372_9_; ++j)
        {
            int k = blockpos.getY() - j;
            int l = p_230372_7_ + p_230372_5_.func_236764_b_() + MathHelper.floor((float)k / (float)p_230372_6_ * 3.5F);
            int i1;

            if (k > 0 && l == i && (j & 1) == 0)
            {
                i1 = l + 1;
            }
            else
            {
                i1 = l;
            }

            this.func_236753_a_(p_230372_1_, p_230372_2_, p_230372_3_, new BlockPos(blockpos.getX(), j, blockpos.getZ()), i1, p_230372_8_, 0, p_230372_5_.func_236765_c_(), p_230372_10_);
            i = l;
        }
    }

    public int func_230374_a_(Random p_230374_1_, int p_230374_2_, BaseTreeFeatureConfig p_230374_3_)
    {
        return this.field_236780_c_.func_242259_a(p_230374_1_);
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
