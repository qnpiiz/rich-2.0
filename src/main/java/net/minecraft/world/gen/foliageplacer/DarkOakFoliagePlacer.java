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

public class DarkOakFoliagePlacer extends FoliagePlacer
{
    public static final Codec<DarkOakFoliagePlacer> field_236745_a_ = RecordCodecBuilder.create((p_236746_0_) ->
    {
        return func_242830_b(p_236746_0_).apply(p_236746_0_, DarkOakFoliagePlacer::new);
    });

    public DarkOakFoliagePlacer(FeatureSpread p_i241997_1_, FeatureSpread p_i241997_2_)
    {
        super(p_i241997_1_, p_i241997_2_);
    }

    protected FoliagePlacerType<?> func_230371_a_()
    {
        return FoliagePlacerType.field_236770_i_;
    }

    protected void func_230372_a_(IWorldGenerationReader p_230372_1_, Random p_230372_2_, BaseTreeFeatureConfig p_230372_3_, int p_230372_4_, FoliagePlacer.Foliage p_230372_5_, int p_230372_6_, int p_230372_7_, Set<BlockPos> p_230372_8_, int p_230372_9_, MutableBoundingBox p_230372_10_)
    {
        BlockPos blockpos = p_230372_5_.func_236763_a_().up(p_230372_9_);
        boolean flag = p_230372_5_.func_236765_c_();

        if (flag)
        {
            this.func_236753_a_(p_230372_1_, p_230372_2_, p_230372_3_, blockpos, p_230372_7_ + 2, p_230372_8_, -1, flag, p_230372_10_);
            this.func_236753_a_(p_230372_1_, p_230372_2_, p_230372_3_, blockpos, p_230372_7_ + 3, p_230372_8_, 0, flag, p_230372_10_);
            this.func_236753_a_(p_230372_1_, p_230372_2_, p_230372_3_, blockpos, p_230372_7_ + 2, p_230372_8_, 1, flag, p_230372_10_);

            if (p_230372_2_.nextBoolean())
            {
                this.func_236753_a_(p_230372_1_, p_230372_2_, p_230372_3_, blockpos, p_230372_7_, p_230372_8_, 2, flag, p_230372_10_);
            }
        }
        else
        {
            this.func_236753_a_(p_230372_1_, p_230372_2_, p_230372_3_, blockpos, p_230372_7_ + 2, p_230372_8_, -1, flag, p_230372_10_);
            this.func_236753_a_(p_230372_1_, p_230372_2_, p_230372_3_, blockpos, p_230372_7_ + 1, p_230372_8_, 0, flag, p_230372_10_);
        }
    }

    public int func_230374_a_(Random p_230374_1_, int p_230374_2_, BaseTreeFeatureConfig p_230374_3_)
    {
        return 4;
    }

    protected boolean func_230375_b_(Random p_230375_1_, int p_230375_2_, int p_230375_3_, int p_230375_4_, int p_230375_5_, boolean p_230375_6_)
    {
        return p_230375_3_ != 0 || !p_230375_6_ || p_230375_2_ != -p_230375_5_ && p_230375_2_ < p_230375_5_ || p_230375_4_ != -p_230375_5_ && p_230375_4_ < p_230375_5_ ? super.func_230375_b_(p_230375_1_, p_230375_2_, p_230375_3_, p_230375_4_, p_230375_5_, p_230375_6_) : true;
    }

    protected boolean func_230373_a_(Random p_230373_1_, int p_230373_2_, int p_230373_3_, int p_230373_4_, int p_230373_5_, boolean p_230373_6_)
    {
        if (p_230373_3_ == -1 && !p_230373_6_)
        {
            return p_230373_2_ == p_230373_5_ && p_230373_4_ == p_230373_5_;
        }
        else if (p_230373_3_ == 1)
        {
            return p_230373_2_ + p_230373_4_ > p_230373_5_ * 2 - 2;
        }
        else
        {
            return false;
        }
    }
}
