package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

public abstract class SimplePlacement<DC extends IPlacementConfig> extends Placement<DC>
{
    public SimplePlacement(Codec<DC> p_i232095_1_)
    {
        super(p_i232095_1_);
    }

    public final Stream<BlockPos> func_241857_a(WorldDecoratingHelper p_241857_1_, Random p_241857_2_, DC p_241857_3_, BlockPos p_241857_4_)
    {
        return this.getPositions(p_241857_2_, p_241857_3_, p_241857_4_);
    }

    protected abstract Stream<BlockPos> getPositions(Random random, DC p_212852_2_, BlockPos pos);
}
