package net.minecraft.world.gen.placement;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public class CountMultilayerPlacement extends Placement<FeatureSpreadConfig>
{
    public CountMultilayerPlacement(Codec<FeatureSpreadConfig> p_i242034_1_)
    {
        super(p_i242034_1_);
    }

    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper p_241857_1_, Random p_241857_2_, FeatureSpreadConfig p_241857_3_, BlockPos p_241857_4_)
    {
        List<BlockPos> list = Lists.newArrayList();
        int i = 0;
        boolean flag;

        do
        {
            flag = false;

            for (int j = 0; j < p_241857_3_.func_242799_a().func_242259_a(p_241857_2_); ++j)
            {
                int k = p_241857_2_.nextInt(16) + p_241857_4_.getX();
                int l = p_241857_2_.nextInt(16) + p_241857_4_.getZ();
                int i1 = p_241857_1_.func_242893_a(Heightmap.Type.MOTION_BLOCKING, k, l);
                int j1 = func_242915_a(p_241857_1_, k, i1, l, i);

                if (j1 != Integer.MAX_VALUE)
                {
                    list.add(new BlockPos(k, j1, l));
                    flag = true;
                }
            }

            ++i;
        }
        while (flag);

        return list.stream();
    }

    private static int func_242915_a(WorldDecoratingHelper p_242915_0_, int p_242915_1_, int p_242915_2_, int p_242915_3_, int p_242915_4_)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_242915_1_, p_242915_2_, p_242915_3_);
        int i = 0;
        BlockState blockstate = p_242915_0_.func_242894_a(blockpos$mutable);

        for (int j = p_242915_2_; j >= 1; --j)
        {
            blockpos$mutable.setY(j - 1);
            BlockState blockstate1 = p_242915_0_.func_242894_a(blockpos$mutable);

            if (!func_242914_a(blockstate1) && func_242914_a(blockstate) && !blockstate1.isIn(Blocks.BEDROCK))
            {
                if (i == p_242915_4_)
                {
                    return blockpos$mutable.getY() + 1;
                }

                ++i;
            }

            blockstate = blockstate1;
        }

        return Integer.MAX_VALUE;
    }

    private static boolean func_242914_a(BlockState p_242914_0_)
    {
        return p_242914_0_.isAir() || p_242914_0_.isIn(Blocks.WATER) || p_242914_0_.isIn(Blocks.LAVA);
    }
}
