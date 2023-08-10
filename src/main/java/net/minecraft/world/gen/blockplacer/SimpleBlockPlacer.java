package net.minecraft.world.gen.blockplacer;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class SimpleBlockPlacer extends BlockPlacer
{
    public static final Codec<SimpleBlockPlacer> CODEC;
    public static final SimpleBlockPlacer PLACER = new SimpleBlockPlacer();

    protected BlockPlacerType<?> getBlockPlacerType()
    {
        return BlockPlacerType.SIMPLE_BLOCK;
    }

    public void place(IWorld world, BlockPos pos, BlockState state, Random random)
    {
        world.setBlockState(pos, state, 2);
    }

    static
    {
        CODEC = Codec.unit(() ->
        {
            return PLACER;
        });
    }
}
