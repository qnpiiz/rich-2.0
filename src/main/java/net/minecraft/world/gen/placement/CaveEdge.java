package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public class CaveEdge extends Placement<CaveEdgeConfig>
{
    public CaveEdge(Codec<CaveEdgeConfig> p_i232065_1_)
    {
        super(p_i232065_1_);
    }

    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper p_241857_1_, Random p_241857_2_, CaveEdgeConfig p_241857_3_, BlockPos p_241857_4_)
    {
        ChunkPos chunkpos = new ChunkPos(p_241857_4_);
        BitSet bitset = p_241857_1_.func_242892_a(chunkpos, p_241857_3_.step);
        return IntStream.range(0, bitset.length()).filter((p_215067_3_) ->
        {
            return bitset.get(p_215067_3_) && p_241857_2_.nextFloat() < p_241857_3_.probability;
        }).mapToObj((p_215068_1_) ->
        {
            int i = p_215068_1_ & 15;
            int j = p_215068_1_ >> 4 & 15;
            int k = p_215068_1_ >> 8;
            return new BlockPos(chunkpos.getXStart() + i, k, chunkpos.getZStart() + j);
        });
    }
}
