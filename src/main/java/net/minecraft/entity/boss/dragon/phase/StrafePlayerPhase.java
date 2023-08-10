package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrafePlayerPhase extends Phase
{
    private static final Logger LOGGER = LogManager.getLogger();
    private int fireballCharge;
    private Path currentPath;
    private Vector3d targetLocation;
    private LivingEntity attackTarget;
    private boolean holdingPatternClockwise;

    public StrafePlayerPhase(EnderDragonEntity dragonIn)
    {
        super(dragonIn);
    }

    /**
     * Gives the phase a chance to update its status.
     * Called by dragon's onLivingUpdate. Only used when !worldObj.isRemote.
     */
    public void serverTick()
    {
        if (this.attackTarget == null)
        {
            LOGGER.warn("Skipping player strafe phase because no player was found");
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
        }
        else
        {
            if (this.currentPath != null && this.currentPath.isFinished())
            {
                double d0 = this.attackTarget.getPosX();
                double d1 = this.attackTarget.getPosZ();
                double d2 = d0 - this.dragon.getPosX();
                double d3 = d1 - this.dragon.getPosZ();
                double d4 = (double)MathHelper.sqrt(d2 * d2 + d3 * d3);
                double d5 = Math.min((double)0.4F + d4 / 80.0D - 1.0D, 10.0D);
                this.targetLocation = new Vector3d(d0, this.attackTarget.getPosY() + d5, d1);
            }

            double d12 = this.targetLocation == null ? 0.0D : this.targetLocation.squareDistanceTo(this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ());

            if (d12 < 100.0D || d12 > 22500.0D)
            {
                this.findNewTarget();
            }

            double d13 = 64.0D;

            if (this.attackTarget.getDistanceSq(this.dragon) < 4096.0D)
            {
                if (this.dragon.canEntityBeSeen(this.attackTarget))
                {
                    ++this.fireballCharge;
                    Vector3d vector3d1 = (new Vector3d(this.attackTarget.getPosX() - this.dragon.getPosX(), 0.0D, this.attackTarget.getPosZ() - this.dragon.getPosZ())).normalize();
                    Vector3d vector3d = (new Vector3d((double)MathHelper.sin(this.dragon.rotationYaw * ((float)Math.PI / 180F)), 0.0D, (double)(-MathHelper.cos(this.dragon.rotationYaw * ((float)Math.PI / 180F))))).normalize();
                    float f1 = (float)vector3d.dotProduct(vector3d1);
                    float f = (float)(Math.acos((double)f1) * (double)(180F / (float)Math.PI));
                    f = f + 0.5F;

                    if (this.fireballCharge >= 5 && f >= 0.0F && f < 10.0F)
                    {
                        double d14 = 1.0D;
                        Vector3d vector3d2 = this.dragon.getLook(1.0F);
                        double d6 = this.dragon.dragonPartHead.getPosX() - vector3d2.x * 1.0D;
                        double d7 = this.dragon.dragonPartHead.getPosYHeight(0.5D) + 0.5D;
                        double d8 = this.dragon.dragonPartHead.getPosZ() - vector3d2.z * 1.0D;
                        double d9 = this.attackTarget.getPosX() - d6;
                        double d10 = this.attackTarget.getPosYHeight(0.5D) - d7;
                        double d11 = this.attackTarget.getPosZ() - d8;

                        if (!this.dragon.isSilent())
                        {
                            this.dragon.world.playEvent((PlayerEntity)null, 1017, this.dragon.getPosition(), 0);
                        }

                        DragonFireballEntity dragonfireballentity = new DragonFireballEntity(this.dragon.world, this.dragon, d9, d10, d11);
                        dragonfireballentity.setLocationAndAngles(d6, d7, d8, 0.0F, 0.0F);
                        this.dragon.world.addEntity(dragonfireballentity);
                        this.fireballCharge = 0;

                        if (this.currentPath != null)
                        {
                            while (!this.currentPath.isFinished())
                            {
                                this.currentPath.incrementPathIndex();
                            }
                        }

                        this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
                    }
                }
                else if (this.fireballCharge > 0)
                {
                    --this.fireballCharge;
                }
            }
            else if (this.fireballCharge > 0)
            {
                --this.fireballCharge;
            }
        }
    }

    private void findNewTarget()
    {
        if (this.currentPath == null || this.currentPath.isFinished())
        {
            int i = this.dragon.initPathPoints();
            int j = i;

            if (this.dragon.getRNG().nextInt(8) == 0)
            {
                this.holdingPatternClockwise = !this.holdingPatternClockwise;
                j = i + 6;
            }

            if (this.holdingPatternClockwise)
            {
                ++j;
            }
            else
            {
                --j;
            }

            if (this.dragon.getFightManager() != null && this.dragon.getFightManager().getNumAliveCrystals() > 0)
            {
                j = j % 12;

                if (j < 0)
                {
                    j += 12;
                }
            }
            else
            {
                j = j - 12;
                j = j & 7;
                j = j + 12;
            }

            this.currentPath = this.dragon.findPath(i, j, (PathPoint)null);

            if (this.currentPath != null)
            {
                this.currentPath.incrementPathIndex();
            }
        }

        this.navigateToNextPathNode();
    }

    private void navigateToNextPathNode()
    {
        if (this.currentPath != null && !this.currentPath.isFinished())
        {
            Vector3i vector3i = this.currentPath.func_242948_g();
            this.currentPath.incrementPathIndex();
            double d0 = (double)vector3i.getX();
            double d2 = (double)vector3i.getZ();
            double d1;

            do
            {
                d1 = (double)((float)vector3i.getY() + this.dragon.getRNG().nextFloat() * 20.0F);
            }
            while (d1 < (double)vector3i.getY());

            this.targetLocation = new Vector3d(d0, d1, d2);
        }
    }

    /**
     * Called when this phase is set to active
     */
    public void initPhase()
    {
        this.fireballCharge = 0;
        this.targetLocation = null;
        this.currentPath = null;
        this.attackTarget = null;
    }

    public void setTarget(LivingEntity p_188686_1_)
    {
        this.attackTarget = p_188686_1_;
        int i = this.dragon.initPathPoints();
        int j = this.dragon.getNearestPpIdx(this.attackTarget.getPosX(), this.attackTarget.getPosY(), this.attackTarget.getPosZ());
        int k = MathHelper.floor(this.attackTarget.getPosX());
        int l = MathHelper.floor(this.attackTarget.getPosZ());
        double d0 = (double)k - this.dragon.getPosX();
        double d1 = (double)l - this.dragon.getPosZ();
        double d2 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1);
        double d3 = Math.min((double)0.4F + d2 / 80.0D - 1.0D, 10.0D);
        int i1 = MathHelper.floor(this.attackTarget.getPosY() + d3);
        PathPoint pathpoint = new PathPoint(k, i1, l);
        this.currentPath = this.dragon.findPath(i, j, pathpoint);

        if (this.currentPath != null)
        {
            this.currentPath.incrementPathIndex();
            this.navigateToNextPathNode();
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

    public PhaseType<StrafePlayerPhase> getType()
    {
        return PhaseType.STRAFE_PLAYER;
    }
}
