package net.minecraft.world.gen.feature.template;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public abstract class StructureProcessor
{
    @Nullable
    public abstract Template.BlockInfo func_230386_a_(IWorldReader p_230386_1_, BlockPos p_230386_2_, BlockPos p_230386_3_, Template.BlockInfo p_230386_4_, Template.BlockInfo p_230386_5_, PlacementSettings p_230386_6_);

    protected abstract IStructureProcessorType<?> getType();
}
