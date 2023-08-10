package net.minecraft.util.math;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public class BlockPosWrapper implements IPosWrapper
{
    private final BlockPos pos;
    private final Vector3d centerPos;

    public BlockPosWrapper(BlockPos pos)
    {
        this.pos = pos;
        this.centerPos = Vector3d.copyCentered(pos);
    }

    public Vector3d getPos()
    {
        return this.centerPos;
    }

    public BlockPos getBlockPos()
    {
        return this.pos;
    }

    public boolean isVisibleTo(LivingEntity entity)
    {
        return true;
    }

    public String toString()
    {
        return "BlockPosTracker{blockPos=" + this.pos + ", centerPosition=" + this.centerPos + '}';
    }
}
