package net.minecraft.util.math.shapes;

import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

public interface ISelectionContext
{
    static ISelectionContext dummy()
    {
        return EntitySelectionContext.DUMMY;
    }

    static ISelectionContext forEntity(Entity entityIn)
    {
        return new EntitySelectionContext(entityIn);
    }

    boolean getPosY();

    boolean func_216378_a(VoxelShape shape, BlockPos pos, boolean p_216378_3_);

    boolean hasItem(Item itemIn);

    boolean func_230426_a_(FluidState p_230426_1_, FlowingFluid p_230426_2_);
}
