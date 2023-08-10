package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public class NetherMagma extends Placement<NoPlacementConfig>
{
    public NetherMagma(Codec<NoPlacementConfig> p_i232103_1_)
    {
        super(p_i232103_1_);
    }

    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper p_241857_1_, Random p_241857_2_, NoPlacementConfig p_241857_3_, BlockPos p_241857_4_)
    {
        int i = p_241857_1_.func_242895_b();
        int j = i - 5 + p_241857_2_.nextInt(10);
        return Stream.of(new BlockPos(p_241857_4_.getX(), j, p_241857_4_.getZ()));
    }
}
