package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class VinesFeature extends Feature<NoFeatureConfig>
{
    private static final Direction[] DIRECTIONS = Direction.values();

    public VinesFeature(Codec<NoFeatureConfig> p_i232002_1_)
    {
        super(p_i232002_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_)
    {
        BlockPos.Mutable blockpos$mutable = p_241855_4_.toMutable();

        for (int i = 64; i < 256; ++i)
        {
            blockpos$mutable.setPos(p_241855_4_);
            blockpos$mutable.move(p_241855_3_.nextInt(4) - p_241855_3_.nextInt(4), 0, p_241855_3_.nextInt(4) - p_241855_3_.nextInt(4));
            blockpos$mutable.setY(i);

            if (p_241855_1_.isAirBlock(blockpos$mutable))
            {
                for (Direction direction : DIRECTIONS)
                {
                    if (direction != Direction.DOWN && VineBlock.canAttachTo(p_241855_1_, blockpos$mutable, direction))
                    {
                        p_241855_1_.setBlockState(blockpos$mutable, Blocks.VINE.getDefaultState().with(VineBlock.getPropertyFor(direction), Boolean.valueOf(true)), 2);
                        break;
                    }
                }
            }
        }

        return true;
    }
}
