package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class LandingApproachPhase extends Phase
{
    private static final EntityPredicate field_221118_b = (new EntityPredicate()).setDistance(128.0D);
    private Path currentPath;
    private Vector3d targetLocation;

    public LandingApproachPhase(EnderDragonEntity dragonIn)
    {
        super(dragonIn);
    }

    public PhaseType<LandingApproachPhase> getType()
    {
        return PhaseType.LANDING_APPROACH;
    }

    /**
     * Called when this phase is set to active
     */
    public void initPhase()
    {
        this.currentPath = null;
        this.targetLocation = null;
    }

    /**
     * Gives the phase a chance to update its status.
     * Called by dragon's onLivingUpdate. Only used when !worldObj.isRemote.
     */
    public void serverTick()
    {
        double d0 = this.targetLocation == null ? 0.0D : this.targetLocation.squareDistanceTo(this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ());

        if (d0 < 100.0D || d0 > 22500.0D || this.dragon.collidedHorizontally || this.dragon.collidedVertically)
        {
            this.findNewTarget();
        }
    }

    @Nullable

    /**
     * Returns the location the dragon is flying toward
     */
    public Vector3d getTargetLocation()
    {
        return this.targetLocation;
    }

    private void findNewTarget()
    {
        if (this.currentPath == null || this.currentPath.isFinished())
        {
            int i = this.dragon.initPathPoints();
            BlockPos blockpos = this.dragon.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
            PlayerEntity playerentity = this.dragon.world.getClosestPlayer(field_221118_b, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
            int j;

            if (playerentity != null)
            {
                Vector3d vector3d = (new Vector3d(playerentity.getPosX(), 0.0D, playerentity.getPosZ())).normalize();
                j = this.dragon.getNearestPpIdx(-vector3d.x * 40.0D, 105.0D, -vector3d.z * 40.0D);
            }
            else
            {
                j = this.dragon.getNearestPpIdx(40.0D, (double)blockpos.getY(), 0.0D);
            }

            PathPoint pathpoint = new PathPoint(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            this.currentPath = this.dragon.findPath(i, j, pathpoint);

            if (this.currentPath != null)
            {
                this.currentPath.incrementPathIndex();
            }
        }

        this.navigateToNextPathNode();

        if (this.currentPath != null && this.currentPath.isFinished())
        {
            this.dragon.getPhaseManager().setPhase(PhaseType.LANDING);
        }
    }

    private void navigateToNextPathNode()
    {
        if (this.currentPath != null && !this.currentPath.isFinished())
        {
            Vector3i vector3i = this.currentPath.func_242948_g();
            this.currentPath.incrementPathIndex();
            double d0 = (double)vector3i.getX();
            double d1 = (double)vector3i.getZ();
            double d2;

            do
            {
                d2 = (double)((float)vector3i.getY() + this.dragon.getRNG().nextFloat() * 20.0F);
            }
            while (d2 < (double)vector3i.getY());

            this.targetLocation = new Vector3d(d0, d2, d1);
        }
    }
}
