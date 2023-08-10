package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;

public class SphereReplaceFeature extends AbstractSphereReplaceConfig
{
    public SphereReplaceFeature(Codec<SphereReplaceConfig> p_i231949_1_)
    {
        super(p_i231949_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, SphereReplaceConfig p_241855_5_)
    {
        return !p_241855_1_.getFluidState(p_241855_4_).isTagged(FluidTags.WATER) ? false : super.func_241855_a(p_241855_1_, p_241855_2_, p_241855_3_, p_241855_4_, p_241855_5_);
    }
}
