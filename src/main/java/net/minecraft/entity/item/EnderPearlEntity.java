package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class EnderPearlEntity extends ProjectileItemEntity
{
    public EnderPearlEntity(EntityType <? extends EnderPearlEntity > p_i50153_1_, World world)
    {
        super(p_i50153_1_, world);
    }

    public EnderPearlEntity(World worldIn, LivingEntity throwerIn)
    {
        super(EntityType.ENDER_PEARL, throwerIn, worldIn);
    }

    public EnderPearlEntity(World worldIn, double x, double y, double z)
    {
        super(EntityType.ENDER_PEARL, x, y, z, worldIn);
    }

    protected Item getDefaultItem()
    {
        return Items.ENDER_PEARL;
    }

    /**
     * Called when the arrow hits an entity
     */
    protected void onEntityHit(EntityRayTraceResult p_213868_1_)
    {
        super.onEntityHit(p_213868_1_);
        p_213868_1_.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()), 0.0F);
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(RayTraceResult result)
    {
        super.onImpact(result);
        Entity entity = this.func_234616_v_();

        for (int i = 0; i < 32; ++i)
        {
            this.world.addParticle(ParticleTypes.PORTAL, this.getPosX(), this.getPosY() + this.rand.nextDouble() * 2.0D, this.getPosZ(), this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
        }

        if (!this.world.isRemote && !this.removed)
        {
            if (entity instanceof ServerPlayerEntity)
            {
                ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity;

                if (serverplayerentity.connection.getNetworkManager().isChannelOpen() && serverplayerentity.world == this.world && !serverplayerentity.isSleeping())
                {
                    if (this.rand.nextFloat() < 0.05F && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING))
                    {
                        EndermiteEntity endermiteentity = EntityType.ENDERMITE.create(this.world);
                        endermiteentity.setSpawnedByPlayer(true);
                        endermiteentity.setLocationAndAngles(entity.getPosX(), entity.getPosY(), entity.getPosZ(), entity.rotationYaw, entity.rotationPitch);
                        this.world.addEntity(endermiteentity);
                    }

                    if (entity.isPassenger())
                    {
                        entity.stopRiding();
                    }

                    entity.setPositionAndUpdate(this.getPosX(), this.getPosY(), this.getPosZ());
                    entity.fallDistance = 0.0F;
                    entity.attackEntityFrom(DamageSource.FALL, 5.0F);
                }
            }
            else if (entity != null)
            {
                entity.setPositionAndUpdate(this.getPosX(), this.getPosY(), this.getPosZ());
                entity.fallDistance = 0.0F;
            }

            this.remove();
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        Entity entity = this.func_234616_v_();

        if (entity instanceof PlayerEntity && !entity.isAlive())
        {
            this.remove();
        }
        else
        {
            super.tick();
        }
    }

    @Nullable
    public Entity changeDimension(ServerWorld server)
    {
        Entity entity = this.func_234616_v_();

        if (entity != null && entity.world.getDimensionKey() != server.getDimensionKey())
        {
            this.setShooter((Entity)null);
        }

        return super.changeDimension(server);
    }
}
