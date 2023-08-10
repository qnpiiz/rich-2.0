package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.Feature;

public class LeaveVineTreeDecorator extends TreeDecorator
{
    public static final Codec<LeaveVineTreeDecorator> field_236870_a_;
    public static final LeaveVineTreeDecorator field_236871_b_ = new LeaveVineTreeDecorator();

    protected TreeDecoratorType<?> func_230380_a_()
    {
        return TreeDecoratorType.LEAVE_VINE;
    }

    public void func_225576_a_(ISeedReader p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_)
    {
        p_225576_4_.forEach((p_242866_5_) ->
        {
            if (p_225576_2_.nextInt(4) == 0)
            {
                BlockPos blockpos = p_242866_5_.west();

                if (Feature.isAirAt(p_225576_1_, blockpos))
                {
                    this.func_227420_a_(p_225576_1_, blockpos, VineBlock.EAST, p_225576_5_, p_225576_6_);
                }
            }

            if (p_225576_2_.nextInt(4) == 0)
            {
                BlockPos blockpos1 = p_242866_5_.east();

                if (Feature.isAirAt(p_225576_1_, blockpos1))
                {
                    this.func_227420_a_(p_225576_1_, blockpos1, VineBlock.WEST, p_225576_5_, p_225576_6_);
                }
            }

            if (p_225576_2_.nextInt(4) == 0)
            {
                BlockPos blockpos2 = p_242866_5_.north();

                if (Feature.isAirAt(p_225576_1_, blockpos2))
                {
                    this.func_227420_a_(p_225576_1_, blockpos2, VineBlock.SOUTH, p_225576_5_, p_225576_6_);
                }
            }

            if (p_225576_2_.nextInt(4) == 0)
            {
                BlockPos blockpos3 = p_242866_5_.south();

                if (Feature.isAirAt(p_225576_1_, blockpos3))
                {
                    this.func_227420_a_(p_225576_1_, blockpos3, VineBlock.NORTH, p_225576_5_, p_225576_6_);
                }
            }
        });
    }

    private void func_227420_a_(IWorldGenerationReader p_227420_1_, BlockPos p_227420_2_, BooleanProperty p_227420_3_, Set<BlockPos> p_227420_4_, MutableBoundingBox p_227420_5_)
    {
        this.func_227424_a_(p_227420_1_, p_227420_2_, p_227420_3_, p_227420_4_, p_227420_5_);
        int i = 4;

        for (BlockPos blockpos = p_227420_2_.down(); Feature.isAirAt(p_227420_1_, blockpos) && i > 0; --i)
        {
            this.func_227424_a_(p_227420_1_, blockpos, p_227420_3_, p_227420_4_, p_227420_5_);
            blockpos = blockpos.down();
        }
    }

    static
    {
        field_236870_a_ = Codec.unit(() ->
        {
            return field_236871_b_;
        });
    }
}
