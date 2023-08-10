package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public class LakeWater extends Placement<ChanceConfig>
{
    public LakeWater(Codec<ChanceConfig> p_i232090_1_)
    {
        super(p_i232090_1_);
    }

    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper p_241857_1_, Random p_241857_2_, ChanceConfig p_241857_3_, BlockPos p_241857_4_)
    {
        if (p_241857_2_.nextInt(p_241857_3_.chance) == 0)
        {
            int i = p_241857_2_.nextInt(16) + p_241857_4_.getX();
            int j = p_241857_2_.nextInt(16) + p_241857_4_.getZ();
            int k = p_241857_2_.nextInt(p_241857_1_.func_242891_a());
            return Stream.of(new BlockPos(i, k, j));
        }
        else
        {
            return Stream.empty();
        }
    }
}
