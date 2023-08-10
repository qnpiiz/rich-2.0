package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class RangeVeryBiasedPlacement extends SimplePlacement<TopSolidRangeConfig>
{
    public RangeVeryBiasedPlacement(Codec<TopSolidRangeConfig> p_i242033_1_)
    {
        super(p_i242033_1_);
    }

    public Stream<BlockPos> getPositions(Random random, TopSolidRangeConfig p_212852_2_, BlockPos pos)
    {
        int i = pos.getX();
        int j = pos.getZ();
        int k = random.nextInt(random.nextInt(random.nextInt(p_212852_2_.field_242815_e - p_212852_2_.field_242814_d) + p_212852_2_.field_242813_c) + p_212852_2_.field_242813_c);
        return Stream.of(new BlockPos(i, k, j));
    }
}
