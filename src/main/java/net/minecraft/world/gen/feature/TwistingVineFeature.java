package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;

public class TwistingVineFeature extends Feature<NoFeatureConfig>
{
    public TwistingVineFeature(Codec<NoFeatureConfig> p_i232000_1_)
    {
        super(p_i232000_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_)
    {
        return func_236423_a_(p_241855_1_, p_241855_3_, p_241855_4_, 8, 4, 8);
    }

    public static boolean func_236423_a_(IWorld p_236423_0_, Random p_236423_1_, BlockPos p_236423_2_, int p_236423_3_, int p_236423_4_, int p_236423_5_)
    {
        if (func_236421_a_(p_236423_0_, p_236423_2_))
        {
            return false;
        }
        else
        {
            func_236424_b_(p_236423_0_, p_236423_1_, p_236423_2_, p_236423_3_, p_236423_4_, p_236423_5_);
            return true;
        }
    }

    private static void func_236424_b_(IWorld p_236424_0_, Random p_236424_1_, BlockPos p_236424_2_, int p_236424_3_, int p_236424_4_, int p_236424_5_)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int i = 0; i < p_236424_3_ * p_236424_3_; ++i)
        {
            blockpos$mutable.setPos(p_236424_2_).move(MathHelper.nextInt(p_236424_1_, -p_236424_3_, p_236424_3_), MathHelper.nextInt(p_236424_1_, -p_236424_4_, p_236424_4_), MathHelper.nextInt(p_236424_1_, -p_236424_3_, p_236424_3_));

            if (func_236420_a_(p_236424_0_, blockpos$mutable) && !func_236421_a_(p_236424_0_, blockpos$mutable))
            {
                int j = MathHelper.nextInt(p_236424_1_, 1, p_236424_5_);

                if (p_236424_1_.nextInt(6) == 0)
                {
                    j *= 2;
                }

                if (p_236424_1_.nextInt(5) == 0)
                {
                    j = 1;
                }

                int k = 17;
                int l = 25;
                func_236422_a_(p_236424_0_, p_236424_1_, blockpos$mutable, j, 17, 25);
            }
        }
    }

    private static boolean func_236420_a_(IWorld p_236420_0_, BlockPos.Mutable p_236420_1_)
    {
        do
        {
            p_236420_1_.move(0, -1, 0);

            if (World.isOutsideBuildHeight(p_236420_1_))
            {
                return false;
            }
        }
        while (p_236420_0_.getBlockState(p_236420_1_).isAir());

        p_236420_1_.move(0, 1, 0);
        return true;
    }

    public static void func_236422_a_(IWorld p_236422_0_, Random p_236422_1_, BlockPos.Mutable p_236422_2_, int p_236422_3_, int p_236422_4_, int p_236422_5_)
    {
        for (int i = 1; i <= p_236422_3_; ++i)
        {
            if (p_236422_0_.isAirBlock(p_236422_2_))
            {
                if (i == p_236422_3_ || !p_236422_0_.isAirBlock(p_236422_2_.up()))
                {
                    p_236422_0_.setBlockState(p_236422_2_, Blocks.TWISTING_VINES.getDefaultState().with(AbstractTopPlantBlock.AGE, Integer.valueOf(MathHelper.nextInt(p_236422_1_, p_236422_4_, p_236422_5_))), 2);
                    break;
                }

                p_236422_0_.setBlockState(p_236422_2_, Blocks.TWISTING_VINES_PLANT.getDefaultState(), 2);
            }

            p_236422_2_.move(Direction.UP);
        }
    }

    private static boolean func_236421_a_(IWorld p_236421_0_, BlockPos p_236421_1_)
    {
        if (!p_236421_0_.isAirBlock(p_236421_1_))
        {
            return true;
        }
        else
        {
            BlockState blockstate = p_236421_0_.getBlockState(p_236421_1_.down());
            return !blockstate.isIn(Blocks.NETHERRACK) && !blockstate.isIn(Blocks.WARPED_NYLIUM) && !blockstate.isIn(Blocks.WARPED_WART_BLOCK);
        }
    }
}
