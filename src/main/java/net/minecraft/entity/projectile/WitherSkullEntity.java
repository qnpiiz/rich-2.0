package net.minecraft.entity.projectile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class WitherSkullEntity extends DamagingProjectileEntity
{
    private static final DataParameter<Boolean> INVULNERABLE = EntityDataManager.createKey(WitherSkullEntity.class, DataSerializers.BOOLEAN);

    public WitherSkullEntity(EntityType <? extends WitherSkullEntity > p_i50147_1_, World p_i50147_2_)
    {
        super(p_i50147_1_, p_i50147_2_);
    }

    public WitherSkullEntity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ)
    {
        super(EntityType.WITHER_SKULL, shooter, accelX, accelY, accelZ, worldIn);
    }

    public WitherSkullEntity(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ)
    {
        super(EntityType.WITHER_SKULL, x, y, z, accelX, accelY, accelZ, worldIn);
    }

    /**
     * Return the motion factor for this projectile. The factor is multiplied by the original motion.
     */
    protected float getMotionFactor()
    {
        return this.isSkullInvulnerable() ? 0.73F : super.getMotionFactor();
    }

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        return false;
    }

    /**
     * Explosion resistance of a block relative to this entity
     */
    public float getExplosionResistance(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, BlockState blockStateIn, FluidState fluidState, float explosionPower)
    {
        return this.isSkullInvulnerable() && WitherEntity.canDestroyBlock(blockStateIn) ? Math.min(0.8F, explosionPower) : explosionPower;
    }

    /**
     * Called when the arrow hits an entity
     */
    protected void onEntityHit(EntityRayTraceResult p_213868_1_)
    {
        super.onEntityHit(p_213868_1_);

        if (!this.world.isRemote)
        {
            Entity entity = p_213868_1_.getEntity();
            Entity entity1 = this.func_234616_v_();
            boolean flag;

            if (entity1 instanceof LivingEntity)
            {
                LivingEntity livingentity = (LivingEntity)entity1;
                flag = entity.attackEntityFrom(DamageSource.func_233549_a_(this, livingentity), 8.0F);

                if (flag)
                {
                    if (entity.isAlive())
                    {
                        this.applyEnchantments(livingentity, entity);
                    }
                    else
                    {
                        livingentity.heal(5.0F);
                    }
                }
            }
            else
            {
                flag = entity.attackEntityFrom(DamageSource.MAGIC, 5.0F);
            }

            if (flag && entity instanceof LivingEntity)
            {
                int i = 0;

                if (this.world.getDifficulty() == Difficulty.NORMAL)
                {
                    i = 10;
                }
                else if (this.world.getDifficulty() == Difficulty.HARD)
                {
                    i = 40;
                }

                if (i > 0)
                {
                    ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.WITHER, 20 * i, 1));
                }
            }
        }
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(RayTraceResult result)
    {
        super.onImpact(result);

        if (!this.world.isRemote)
        {
            Explosion.Mode explosion$mode = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
            this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), 1.0F, false, explosion$mode);
            this.remove();
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return false;
    }

    protected void registerData()
    {
        this.dataManager.register(INVULNERABLE, false);
    }

    /**
     * Return whether this skull comes from an invulnerable (aura) wither boss.
     */
    public boolean isSkullInvulnerable()
    {
        return this.dataManager.get(INVULNERABLE);
    }

    /**
     * Set whether this skull comes from an invulnerable (aura) wither boss.
     */
    public void setSkullInvulnerable(boolean invulnerable)
    {
        this.dataManager.set(INVULNERABLE, invulnerable);
    }

    protected boolean isFireballFiery()
    {
        return false;
    }
}
