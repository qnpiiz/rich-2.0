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

public class BushFoliagePlacer extends BlobFoliagePlacer
{
    public static final Codec<BushFoliagePlacer> field_236743_c_ = RecordCodecBuilder.create((p_236744_0_) ->
    {
        return func_236740_a_(p_236744_0_).apply(p_236744_0_, BushFoliagePlacer::new);
    });

    public BushFoliagePlacer(FeatureSpread p_i241996_1_, FeatureSpread p_i241996_2_, int p_i241996_3_)
    {
        super(p_i241996_1_, p_i241996_2_, p_i241996_3_);
    }

    protected FoliagePlacerType<?> func_230371_a_()
    {
        return FoliagePlacerType.field_236766_e_;
    }

    protected void func_230372_a_(IWorldGenerationReader p_230372_1_, Random p_230372_2_, BaseTreeFeatureConfig p_230372_3_, int p_230372_4_, FoliagePlacer.Foliage p_230372_5_, int p_230372_6_, int p_230372_7_, Set<BlockPos> p_230372_8_, int p_230372_9_, MutableBoundingBox p_230372_10_)
    {
        for (int i = p_230372_9_; i >= p_230372_9_ - p_230372_6_; --i)
        {
            int j = p_230372_7_ + p_230372_5_.func_236764_b_() - 1 - i;
            this.func_236753_a_(p_230372_1_, p_230372_2_, p_230372_3_, p_230372_5_.func_236763_a_(), j, p_230372_8_, i, p_230372_5_.func_236765_c_(), p_230372_10_);
        }
    }

    protected boolean func_230373_a_(Random p_230373_1_, int p_230373_2_, int p_230373_3_, int p_230373_4_, int p_230373_5_, boolean p_230373_6_)
    {
        return p_230373_2_ == p_230373_5_ && p_230373_4_ == p_230373_5_ && p_230373_1_.nextInt(2) == 0;
    }
}
