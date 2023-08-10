package net.minecraft.world.gen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SoulSandValleySurfaceBuilder extends ValleySurfaceBuilder
{
    private static final BlockState field_237180_a_ = Blocks.SOUL_SAND.getDefaultState();
    private static final BlockState field_237181_b_ = Blocks.SOUL_SOIL.getDefaultState();
    private static final BlockState field_237182_c_ = Blocks.GRAVEL.getDefaultState();
    private static final ImmutableList<BlockState> field_237183_d_ = ImmutableList.of(field_237180_a_, field_237181_b_);

    public SoulSandValleySurfaceBuilder(Codec<SurfaceBuilderConfig> p_i232135_1_)
    {
        super(p_i232135_1_);
    }

    protected ImmutableList<BlockState> func_230387_a_()
    {
        return field_237183_d_;
    }

    protected ImmutableList<BlockState> func_230388_b_()
    {
        return field_237183_d_;
    }

    protected BlockState func_230389_c_()
    {
        return field_237182_c_;
    }
}
