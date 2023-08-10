package net.minecraft.world.gen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BasaltDeltasSurfaceBuilder extends ValleySurfaceBuilder
{
    private static final BlockState field_237163_a_ = Blocks.BASALT.getDefaultState();
    private static final BlockState field_237164_b_ = Blocks.BLACKSTONE.getDefaultState();
    private static final BlockState field_237165_c_ = Blocks.GRAVEL.getDefaultState();
    private static final ImmutableList<BlockState> field_237166_d_ = ImmutableList.of(field_237163_a_, field_237164_b_);
    private static final ImmutableList<BlockState> field_237167_e_ = ImmutableList.of(field_237163_a_);

    public BasaltDeltasSurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232123_1_)
    {
        super(p_i232123_1_);
    }

    protected ImmutableList<BlockState> func_230387_a_()
    {
        return field_237166_d_;
    }

    protected ImmutableList<BlockState> func_230388_b_()
    {
        return field_237167_e_;
    }

    protected BlockState func_230389_c_()
    {
        return field_237165_c_;
    }
}
