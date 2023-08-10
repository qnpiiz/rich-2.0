package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.vector.Vector3d;

public class HoverPhase extends Phase
{
    private Vector3d targetLocation;

    public HoverPhase(EnderDragonEntity dragonIn)
    {
        super(dragonIn);
    }

    /**
     * Gives the phase a chance to update its status.
     * Called by dragon's onLivingUpdate. Only used when !worldObj.isRemote.
     */
    public void serverTick()
    {
        if (this.targetLocation == null)
        {
            this.targetLocation = this.dragon.getPositionVec();
        }
    }

    public boolean getIsStationary()
    {
        return true;
    }

    /**
     * Called when this phase is set to active
     */
    public void initPhase()
    {
        this.targetLocation = null;
    }

    /**
     * Returns the maximum amount dragon may rise or fall during this phase
     */
    public float getMaxRiseOrFall()
    {
        return 1.0F;
    }

    @Nullable

    /**
     * Returns the location the dragon is flying toward
     */
    public Vector3d getTargetLocation()
    {
        return this.targetLocation;
    }

    public PhaseType<HoverPhase> getType()
    {
        return PhaseType.HOVER;
    }
}
