package net.minecraft.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public abstract class RayTraceResult
{
    protected final Vector3d hitResult;

    protected RayTraceResult(Vector3d hitVec)
    {
        this.hitResult = hitVec;
    }

    public double func_237486_a_(Entity p_237486_1_)
    {
        double d0 = this.hitResult.x - p_237486_1_.getPosX();
        double d1 = this.hitResult.y - p_237486_1_.getPosY();
        double d2 = this.hitResult.z - p_237486_1_.getPosZ();
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public abstract RayTraceResult.Type getType();

    /**
     * Returns the hit position of the raycast, in absolute world coordinates
     */
    public Vector3d getHitVec()
    {
        return this.hitResult;
    }

    public static enum Type
    {
        MISS,
        BLOCK,
        ENTITY;
    }
}
