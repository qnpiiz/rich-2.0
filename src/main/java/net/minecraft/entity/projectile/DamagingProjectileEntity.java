package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public abstract class DamagingProjectileEntity extends ProjectileEntity
{
    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;

    protected DamagingProjectileEntity(EntityType <? extends DamagingProjectileEntity > p_i50173_1_, World p_i50173_2_)
    {
        super(p_i50173_1_, p_i50173_2_);
    }

    public DamagingProjectileEntity(EntityType <? extends DamagingProjectileEntity > p_i50174_1_, double p_i50174_2_, double p_i50174_4_, double p_i50174_6_, double p_i50174_8_, double p_i50174_10_, double p_i50174_12_, World p_i50174_14_)
    {
        this(p_i50174_1_, p_i50174_14_);
        this.setLocationAndAngles(p_i50174_2_, p_i50174_4_, p_i50174_6_, this.rotationYaw, this.rotationPitch);
        this.recenterBoundingBox();
        double d0 = (double)MathHelper.sqrt(p_i50174_8_ * p_i50174_8_ + p_i50174_10_ * p_i50174_10_ + p_i50174_12_ * p_i50174_12_);

        if (d0 != 0.0D)
        {
            this.accelerationX = p_i50174_8_ / d0 * 0.1D;
            this.accelerationY = p_i50174_10_ / d0 * 0.1D;
            this.accelerationZ = p_i50174_12_ / d0 * 0.1D;
        }
    }

    public DamagingProjectileEntity(EntityType <? extends DamagingProjectileEntity > p_i50175_1_, LivingEntity p_i50175_2_, double p_i50175_3_, double p_i50175_5_, double p_i50175_7_, World p_i50175_9_)
    {
        this(p_i50175_1_, p_i50175_2_.getPosX(), p_i50175_2_.getPosY(), p_i50175_2_.getPosZ(), p_i50175_3_, p_i50175_5_, p_i50175_7_, p_i50175_9_);
        this.setShooter(p_i50175_2_);
        this.setRotation(p_i50175_2_.rotationYaw, p_i50175_2_.rotationPitch);
    }

    protected void registerData()
    {
    }

    /**
     * Checks if the entity is in range to render.
     */
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.getBoundingBox().getAverageEdgeLength() * 4.0D;

        if (Double.isNaN(d0))
        {
            d0 = 4.0D;
        }

        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        Entity entity = this.func_234616_v_();

        if (this.world.isRemote || (entity == null || !entity.removed) && this.world.isBlockLoaded(this.getPosition()))
        {
            super.tick();

            if (this.isFireballFiery())
            {
                this.setFire(1);
            }

            RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);

            if (raytraceresult.getType() != RayTraceResult.Type.MISS)
            {
                this.onImpact(raytraceresult);
            }

            this.doBlockCollisions();
            Vector3d vector3d = this.getMotion();
            double d0 = this.getPosX() + vector3d.x;
            double d1 = this.getPosY() + vector3d.y;
            double d2 = this.getPosZ() + vector3d.z;
            ProjectileHelper.rotateTowardsMovement(this, 0.2F);
            float f = this.getMotionFactor();

            if (this.isInWater())
            {
                for (int i = 0; i < 4; ++i)
                {
                    float f1 = 0.25F;
                    this.world.addParticle(ParticleTypes.BUBBLE, d0 - vector3d.x * 0.25D, d1 - vector3d.y * 0.25D, d2 - vector3d.z * 0.25D, vector3d.x, vector3d.y, vector3d.z);
                }

                f = 0.8F;
            }

            this.setMotion(vector3d.add(this.accelerationX, this.accelerationY, this.accelerationZ).scale((double)f));
            this.world.addParticle(this.getParticle(), d0, d1 + 0.5D, d2, 0.0D, 0.0D, 0.0D);
            this.setPosition(d0, d1, d2);
        }
        else
        {
            this.remove();
        }
    }

    protected boolean func_230298_a_(Entity p_230298_1_)
    {
        return super.func_230298_a_(p_230298_1_) && !p_230298_1_.noClip;
    }

    protected boolean isFireballFiery()
    {
        return true;
    }

    protected IParticleData getParticle()
    {
        return ParticleTypes.SMOKE;
    }

    /**
     * Return the motion factor for this projectile. The factor is multiplied by the original motion.
     */
    protected float getMotionFactor()
    {
        return 0.95F;
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.put("power", this.newDoubleNBTList(new double[] {this.accelerationX, this.accelerationY, this.accelerationZ}));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);

        if (compound.contains("power", 9))
        {
            ListNBT listnbt = compound.getList("power", 6);

            if (listnbt.size() == 3)
            {
                this.accelerationX = listnbt.getDouble(0);
                this.accelerationY = listnbt.getDouble(1);
                this.accelerationZ = listnbt.getDouble(2);
            }
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return true;
    }

    public float getCollisionBorderSize()
    {
        return 1.0F;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isInvulnerableTo(source))
        {
            return false;
        }
        else
        {
            this.markVelocityChanged();
            Entity entity = source.getTrueSource();

            if (entity != null)
            {
                Vector3d vector3d = entity.getLookVec();
                this.setMotion(vector3d);
                this.accelerationX = vector3d.x * 0.1D;
                this.accelerationY = vector3d.y * 0.1D;
                this.accelerationZ = vector3d.z * 0.1D;
                this.setShooter(entity);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness()
    {
        return 1.0F;
    }

    public IPacket<?> createSpawnPacket()
    {
        Entity entity = this.func_234616_v_();
        int i = entity == null ? 0 : entity.getEntityId();
        return new SSpawnObjectPacket(this.getEntityId(), this.getUniqueID(), this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationPitch, this.rotationYaw, this.getType(), i, new Vector3d(this.accelerationX, this.accelerationY, this.accelerationZ));
    }
}
