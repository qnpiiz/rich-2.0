package net.minecraft.world.gen.blockplacer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ColumnBlockPlacer extends BlockPlacer
{
    public static final Codec<ColumnBlockPlacer> CODEC = RecordCodecBuilder.create((builder) ->
    {
        return builder.group(Codec.INT.fieldOf("min_size").forGetter((placer) -> {
            return placer.minSize;
        }), Codec.INT.fieldOf("extra_size").forGetter((placer) -> {
            return placer.extraSize;
        })).apply(builder, ColumnBlockPlacer::new);
    });
    private final int minSize;
    private final int extraSize;

    public ColumnBlockPlacer(int minSize, int extraSize)
    {
        this.minSize = minSize;
        this.extraSize = extraSize;
    }

    protected BlockPlacerType<?> getBlockPlacerType()
    {
        return BlockPlacerType.COLUMN;
    }

    public void place(IWorld world, BlockPos pos, BlockState state, Random random)
    {
        BlockPos.Mutable blockpos$mutable = pos.toMutable();
        int i = this.minSize + random.nextInt(random.nextInt(this.extraSize + 1) + 1);

        for (int j = 0; j < i; ++j)
        {
            world.setBlockState(blockpos$mutable, state, 2);
            blockpos$mutable.move(Direction.UP);
        }
    }
}
