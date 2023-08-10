package net.minecraft.util.math;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public interface IPosWrapper
{
    Vector3d getPos();

    BlockPos getBlockPos();

    boolean isVisibleTo(LivingEntity entity);
}
