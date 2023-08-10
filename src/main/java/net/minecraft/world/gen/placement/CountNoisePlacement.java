package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public class CountNoisePlacement extends Placement<NoiseDependant>
{
    public CountNoisePlacement(Codec<NoiseDependant> p_i242017_1_)
    {
        super(p_i242017_1_);
    }

    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper p_241857_1_, Random p_241857_2_, NoiseDependant p_241857_3_, BlockPos p_241857_4_)
    {
        double d0 = Biome.INFO_NOISE.noiseAt((double)p_241857_4_.getX() / 200.0D, (double)p_241857_4_.getZ() / 200.0D, false);
        int i = d0 < p_241857_3_.noiseLevel ? p_241857_3_.belowNoise : p_241857_3_.aboveNoise;
        return IntStream.range(0, i).mapToObj((p_242879_1_) ->
        {
            return p_241857_4_;
        });
    }
}
