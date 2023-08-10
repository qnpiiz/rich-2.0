package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class IcebergPlacement extends SimplePlacement<NoPlacementConfig>
{
    public IcebergPlacement(Codec<NoPlacementConfig> p_i232088_1_)
    {
        super(p_i232088_1_);
    }

    public Stream<BlockPos> getPositions(Random random, NoPlacementConfig p_212852_2_, BlockPos pos)
    {
        int i = random.nextInt(8) + 4 + pos.getX();
        int j = random.nextInt(8) + 4 + pos.getZ();
        return Stream.of(new BlockPos(i, pos.getY(), j));
    }
}
