package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public class DecoratedPlacement extends Placement<DecoratedPlacementConfig>
{
    public DecoratedPlacement(Codec<DecoratedPlacementConfig> p_i242019_1_)
    {
        super(p_i242019_1_);
    }

    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper p_241857_1_, Random p_241857_2_, DecoratedPlacementConfig p_241857_3_, BlockPos p_241857_4_)
    {
        return p_241857_3_.func_242886_a().func_242876_a(p_241857_1_, p_241857_2_, p_241857_4_).flatMap((p_242882_3_) ->
        {
            return p_241857_3_.func_242888_b().func_242876_a(p_241857_1_, p_241857_2_, p_242882_3_);
        });
    }
}
