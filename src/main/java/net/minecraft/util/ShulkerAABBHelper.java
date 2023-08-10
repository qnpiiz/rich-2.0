package net.minecraft.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;

public class ShulkerAABBHelper
{
    public static AxisAlignedBB getOpenedCollisionBox(BlockPos pos, Direction direction)
    {
        return VoxelShapes.fullCube().getBoundingBox().expand((double)(0.5F * (float)direction.getXOffset()), (double)(0.5F * (float)direction.getYOffset()), (double)(0.5F * (float)direction.getZOffset())).contract((double)direction.getXOffset(), (double)direction.getYOffset(), (double)direction.getZOffset()).offset(pos.offset(direction));
    }
}
