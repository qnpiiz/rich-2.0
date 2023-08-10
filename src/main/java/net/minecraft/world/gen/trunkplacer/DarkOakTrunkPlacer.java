package net.minecraft.world.gen.trunkplacer;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

public class DarkOakTrunkPlacer extends AbstractTrunkPlacer
{
    public static final Codec<DarkOakTrunkPlacer> field_236882_a_ = RecordCodecBuilder.create((p_236883_0_) ->
    {
        return func_236915_a_(p_236883_0_).apply(p_236883_0_, DarkOakTrunkPlacer::new);
    });

    public DarkOakTrunkPlacer(int p_i232053_1_, int p_i232053_2_, int p_i232053_3_)
    {
        super(p_i232053_1_, p_i232053_2_, p_i232053_3_);
    }

    protected TrunkPlacerType<?> func_230381_a_()
    {
        return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
    }

    public List<FoliagePlacer.Foliage> func_230382_a_(IWorldGenerationReader p_230382_1_, Random p_230382_2_, int p_230382_3_, BlockPos p_230382_4_, Set<BlockPos> p_230382_5_, MutableBoundingBox p_230382_6_, BaseTreeFeatureConfig p_230382_7_)
    {
        List<FoliagePlacer.Foliage> list = Lists.newArrayList();
        BlockPos blockpos = p_230382_4_.down();
        func_236909_a_(p_230382_1_, blockpos);
        func_236909_a_(p_230382_1_, blockpos.east());
        func_236909_a_(p_230382_1_, blockpos.south());
        func_236909_a_(p_230382_1_, blockpos.south().east());
        Direction direction = Direction.Plane.HORIZONTAL.random(p_230382_2_);
        int i = p_230382_3_ - p_230382_2_.nextInt(4);
        int j = 2 - p_230382_2_.nextInt(3);
        int k = p_230382_4_.getX();
        int l = p_230382_4_.getY();
        int i1 = p_230382_4_.getZ();
        int j1 = k;
        int k1 = i1;
        int l1 = l + p_230382_3_ - 1;

        for (int i2 = 0; i2 < p_230382_3_; ++i2)
        {
            if (i2 >= i && j > 0)
            {
                j1 += direction.getXOffset();
                k1 += direction.getZOffset();
                --j;
            }

            int j2 = l + i2;
            BlockPos blockpos1 = new BlockPos(j1, j2, k1);

            if (TreeFeature.isAirOrLeavesAt(p_230382_1_, blockpos1))
            {
                func_236911_a_(p_230382_1_, p_230382_2_, blockpos1, p_230382_5_, p_230382_6_, p_230382_7_);
                func_236911_a_(p_230382_1_, p_230382_2_, blockpos1.east(), p_230382_5_, p_230382_6_, p_230382_7_);
                func_236911_a_(p_230382_1_, p_230382_2_, blockpos1.south(), p_230382_5_, p_230382_6_, p_230382_7_);
                func_236911_a_(p_230382_1_, p_230382_2_, blockpos1.east().south(), p_230382_5_, p_230382_6_, p_230382_7_);
            }
        }

        list.add(new FoliagePlacer.Foliage(new BlockPos(j1, l1, k1), 0, true));

        for (int l2 = -1; l2 <= 2; ++l2)
        {
            for (int i3 = -1; i3 <= 2; ++i3)
            {
                if ((l2 < 0 || l2 > 1 || i3 < 0 || i3 > 1) && p_230382_2_.nextInt(3) <= 0)
                {
                    int j3 = p_230382_2_.nextInt(3) + 2;

                    for (int k2 = 0; k2 < j3; ++k2)
                    {
                        func_236911_a_(p_230382_1_, p_230382_2_, new BlockPos(k + l2, l1 - k2 - 1, i1 + i3), p_230382_5_, p_230382_6_, p_230382_7_);
                    }

                    list.add(new FoliagePlacer.Foliage(new BlockPos(j1 + l2, l1, k1 + i3), 0, false));
                }
            }
        }

        return list;
    }
}
