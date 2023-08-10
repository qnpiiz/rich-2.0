package net.minecraft.world.gen.trunkplacer;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

public class MegaJungleTrunkPlacer extends GiantTrunkPlacer
{
    public static final Codec<MegaJungleTrunkPlacer> field_236901_b_ = RecordCodecBuilder.create((p_236902_0_) ->
    {
        return func_236915_a_(p_236902_0_).apply(p_236902_0_, MegaJungleTrunkPlacer::new);
    });

    public MegaJungleTrunkPlacer(int p_i232058_1_, int p_i232058_2_, int p_i232058_3_)
    {
        super(p_i232058_1_, p_i232058_2_, p_i232058_3_);
    }

    protected TrunkPlacerType<?> func_230381_a_()
    {
        return TrunkPlacerType.MEGA_TRUNK_PLACER;
    }

    public List<FoliagePlacer.Foliage> func_230382_a_(IWorldGenerationReader p_230382_1_, Random p_230382_2_, int p_230382_3_, BlockPos p_230382_4_, Set<BlockPos> p_230382_5_, MutableBoundingBox p_230382_6_, BaseTreeFeatureConfig p_230382_7_)
    {
        List<FoliagePlacer.Foliage> list = Lists.newArrayList();
        list.addAll(super.func_230382_a_(p_230382_1_, p_230382_2_, p_230382_3_, p_230382_4_, p_230382_5_, p_230382_6_, p_230382_7_));

        for (int i = p_230382_3_ - 2 - p_230382_2_.nextInt(4); i > p_230382_3_ / 2; i -= 2 + p_230382_2_.nextInt(4))
        {
            float f = p_230382_2_.nextFloat() * ((float)Math.PI * 2F);
            int j = 0;
            int k = 0;

            for (int l = 0; l < 5; ++l)
            {
                j = (int)(1.5F + MathHelper.cos(f) * (float)l);
                k = (int)(1.5F + MathHelper.sin(f) * (float)l);
                BlockPos blockpos = p_230382_4_.add(j, i - 3 + l / 2, k);
                func_236911_a_(p_230382_1_, p_230382_2_, blockpos, p_230382_5_, p_230382_6_, p_230382_7_);
            }

            list.add(new FoliagePlacer.Foliage(p_230382_4_.add(j, i, k), -2, false));
        }

        return list;
    }
}
