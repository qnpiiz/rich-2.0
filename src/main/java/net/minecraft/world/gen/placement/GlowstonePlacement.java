package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;

public class GlowstonePlacement extends SimplePlacement<FeatureSpreadConfig>
{
    public GlowstonePlacement(Codec<FeatureSpreadConfig> p_i242035_1_)
    {
        super(p_i242035_1_);
    }

    public Stream<BlockPos> getPositions(Random random, FeatureSpreadConfig p_212852_2_, BlockPos pos)
    {
        return IntStream.range(0, random.nextInt(random.nextInt(p_212852_2_.func_242799_a().func_242259_a(random)) + 1)).mapToObj((p_242916_2_) ->
        {
            int i = random.nextInt(16) + pos.getX();
            int j = random.nextInt(16) + pos.getZ();
            int k = random.nextInt(120) + 4;
            return new BlockPos(i, k, j);
        });
    }
}
