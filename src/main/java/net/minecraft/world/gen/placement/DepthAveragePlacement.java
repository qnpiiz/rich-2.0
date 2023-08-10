package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class DepthAveragePlacement extends SimplePlacement<DepthAverageConfig>
{
    public DepthAveragePlacement(Codec<DepthAverageConfig> p_i242023_1_)
    {
        super(p_i242023_1_);
    }

    public Stream<BlockPos> getPositions(Random random, DepthAverageConfig p_212852_2_, BlockPos pos)
    {
        int i = p_212852_2_.baseline;
        int j = p_212852_2_.spread;
        int k = pos.getX();
        int l = pos.getZ();
        int i1 = random.nextInt(j) + random.nextInt(j) - j + i;
        return Stream.of(new BlockPos(k, i1, l));
    }
}
