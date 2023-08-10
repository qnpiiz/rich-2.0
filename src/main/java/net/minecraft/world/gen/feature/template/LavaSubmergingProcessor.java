package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class LavaSubmergingProcessor extends StructureProcessor
{
    public static final Codec<LavaSubmergingProcessor> field_241531_a_;
    public static final LavaSubmergingProcessor field_241532_b_ = new LavaSubmergingProcessor();

    @Nullable
    public Template.BlockInfo func_230386_a_(IWorldReader p_230386_1_, BlockPos p_230386_2_, BlockPos p_230386_3_, Template.BlockInfo p_230386_4_, Template.BlockInfo p_230386_5_, PlacementSettings p_230386_6_)
    {
        BlockPos blockpos = p_230386_5_.pos;
        boolean flag = p_230386_1_.getBlockState(blockpos).isIn(Blocks.LAVA);
        return flag && !Block.isOpaque(p_230386_5_.state.getShape(p_230386_1_, blockpos)) ? new Template.BlockInfo(blockpos, Blocks.LAVA.getDefaultState(), p_230386_5_.nbt) : p_230386_5_;
    }

    protected IStructureProcessorType<?> getType()
    {
        return IStructureProcessorType.field_241534_i_;
    }

    static
    {
        field_241531_a_ = Codec.unit(() ->
        {
            return field_241532_b_;
        });
    }
}
