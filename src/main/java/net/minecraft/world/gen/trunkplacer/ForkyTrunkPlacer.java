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
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;

public class ForkyTrunkPlacer extends AbstractTrunkPlacer
{
    public static final Codec<ForkyTrunkPlacer> field_236896_a_ = RecordCodecBuilder.create((p_236897_0_) ->
    {
        return func_236915_a_(p_236897_0_).apply(p_236897_0_, ForkyTrunkPlacer::new);
    });

    public ForkyTrunkPlacer(int p_i232056_1_, int p_i232056_2_, int p_i232056_3_)
    {
        super(p_i232056_1_, p_i232056_2_, p_i232056_3_);
    }

    protected TrunkPlacerType<?> func_230381_a_()
    {
        return TrunkPlacerType.FORKING_TRUNK_PLACER;
    }

    public List<FoliagePlacer.Foliage> func_230382_a_(IWorldGenerationReader p_230382_1_, Random p_230382_2_, int p_230382_3_, BlockPos p_230382_4_, Set<BlockPos> p_230382_5_, MutableBoundingBox p_230382_6_, BaseTreeFeatureConfig p_230382_7_)
    {
        func_236909_a_(p_230382_1_, p_230382_4_.down());
        List<FoliagePlacer.Foliage> list = Lists.newArrayList();
        Direction direction = Direction.Plane.HORIZONTAL.random(p_230382_2_);
        int i = p_230382_3_ - p_230382_2_.nextInt(4) - 1;
        int j = 3 - p_230382_2_.nextInt(3);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int k = p_230382_4_.getX();
        int l = p_230382_4_.getZ();
        int i1 = 0;

        for (int j1 = 0; j1 < p_230382_3_; ++j1)
        {
            int k1 = p_230382_4_.getY() + j1;

            if (j1 >= i && j > 0)
            {
                k += direction.getXOffset();
                l += direction.getZOffset();
                --j;
            }

            if (func_236911_a_(p_230382_1_, p_230382_2_, blockpos$mutable.setPos(k, k1, l), p_230382_5_, p_230382_6_, p_230382_7_))
            {
                i1 = k1 + 1;
            }
        }

        list.add(new FoliagePlacer.Foliage(new BlockPos(k, i1, l), 1, false));
        k = p_230382_4_.getX();
        l = p_230382_4_.getZ();
        Direction direction1 = Direction.Plane.HORIZONTAL.random(p_230382_2_);

        if (direction1 != direction)
        {
            int k2 = i - p_230382_2_.nextInt(2) - 1;
            int l1 = 1 + p_230382_2_.nextInt(3);
            i1 = 0;

            for (int i2 = k2; i2 < p_230382_3_ && l1 > 0; --l1)
            {
                if (i2 >= 1)
                {
                    int j2 = p_230382_4_.getY() + i2;
                    k += direction1.getXOffset();
                    l += direction1.getZOffset();

                    if (func_236911_a_(p_230382_1_, p_230382_2_, blockpos$mutable.setPos(k, j2, l), p_230382_5_, p_230382_6_, p_230382_7_))
                    {
                        i1 = j2 + 1;
                    }
                }

                ++i2;
            }

            if (i1 > 1)
            {
                list.add(new FoliagePlacer.Foliage(new BlockPos(k, i1, l), 0, false));
            }
        }

        return list;
    }
}
