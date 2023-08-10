package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public class Spread32AbovePlacement extends Placement<NoPlacementConfig>
{
    public Spread32AbovePlacement(Codec<NoPlacementConfig> p_i242031_1_)
    {
        super(p_i242031_1_);
    }

    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper p_241857_1_, Random p_241857_2_, NoPlacementConfig p_241857_3_, BlockPos p_241857_4_)
    {
        int i = p_241857_2_.nextInt(p_241857_4_.getY() + 32);
        return Stream.of(new BlockPos(p_241857_4_.getX(), i, p_241857_4_.getZ()));
    }
}
