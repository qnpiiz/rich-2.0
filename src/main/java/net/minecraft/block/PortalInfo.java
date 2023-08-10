package net.minecraft.block;

import net.minecraft.util.math.vector.Vector3d;

public class PortalInfo
{
    public final Vector3d pos;
    public final Vector3d motion;
    public final float rotationYaw;
    public final float rotationPitch;

    public PortalInfo(Vector3d pos, Vector3d motion, float rotationYaw, float rotationPitch)
    {
        this.pos = pos;
        this.motion = motion;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }
}
