package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public class EndGateway extends Placement<NoPlacementConfig>
{
    public EndGateway(Codec<NoPlacementConfig> p_i232084_1_)
    {
        super(p_i232084_1_);
    }

    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper p_241857_1_, Random p_241857_2_, NoPlacementConfig p_241857_3_, BlockPos p_241857_4_)
    {
        if (p_241857_2_.nextInt(700) == 0)
        {
            int i = p_241857_2_.nextInt(16) + p_241857_4_.getX();
            int j = p_241857_2_.nextInt(16) + p_241857_4_.getZ();
            int k = p_241857_1_.func_242893_a(Heightmap.Type.MOTION_BLOCKING, i, j);

            if (k > 0)
            {
                int l = k + 3 + p_241857_2_.nextInt(7);
                return Stream.of(new BlockPos(i, l, j));
            }
        }

        return Stream.empty();
    }
}
