package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public abstract class SimpleHeightmapBasedPlacement<DC extends IPlacementConfig> extends HeightmapBasedPlacement<DC>
{
    public SimpleHeightmapBasedPlacement(Codec<DC> p_i242013_1_)
    {
        super(p_i242013_1_);
    }

    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper p_241857_1_, Random p_241857_2_, DC p_241857_3_, BlockPos p_241857_4_)
    {
        int i = p_241857_4_.getX();
        int j = p_241857_4_.getZ();
        int k = p_241857_1_.func_242893_a(this.func_241858_a(p_241857_3_), i, j);
        return k > 0 ? Stream.of(new BlockPos(i, k, j)) : Stream.of();
    }
}
