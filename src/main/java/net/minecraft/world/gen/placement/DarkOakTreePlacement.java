package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public class DarkOakTreePlacement extends HeightmapBasedPlacement<NoPlacementConfig>
{
    public DarkOakTreePlacement(Codec<NoPlacementConfig> p_i232082_1_)
    {
        super(p_i232082_1_);
    }

    protected Heightmap.Type func_241858_a(NoPlacementConfig p_241858_1_)
    {
        return Heightmap.Type.MOTION_BLOCKING;
    }

    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper p_241857_1_, Random p_241857_2_, NoPlacementConfig p_241857_3_, BlockPos p_241857_4_)
    {
        return IntStream.range(0, 16).mapToObj((p_242881_5_) ->
        {
            int i = p_242881_5_ / 4;
            int j = p_242881_5_ % 4;
            int k = i * 4 + 1 + p_241857_2_.nextInt(3) + p_241857_4_.getX();
            int l = j * 4 + 1 + p_241857_2_.nextInt(3) + p_241857_4_.getZ();
            int i1 = p_241857_1_.func_242893_a(this.func_241858_a(p_241857_3_), k, l);
            return new BlockPos(k, i1, l);
        });
    }
}
