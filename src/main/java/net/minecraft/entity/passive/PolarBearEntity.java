package net.minecraft.entity.passive;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;

public class PolarBearEntity extends AnimalEntity implements IAngerable
{
    private static final DataParameter<Boolean> IS_STANDING = EntityDataManager.createKey(PolarBearEntity.class, DataSerializers.BOOLEAN);
    private float clientSideStandAnimation0;
    private float clientSideStandAnimation;
    private int warningSoundTicks;
    private static final RangedInteger field_234217_by_ = TickRangeConverter.convertRange(20, 39);
    private int field_234218_bz_;
    private UUID field_234216_bA_;

    public PolarBearEntity(EntityType <? extends PolarBearEntity > type, World worldIn)
    {
        super(type, worldIn);
    }

    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_)
    {
        return EntityType.POLAR_BEAR.create(p_241840_1_);
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    public boolean isBreedingItem(ItemStack stack)
    {
        return false;
    }

    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new PolarBearEntity.MeleeAttackGoal());
        this.goalSelector.addGoal(1, new PolarBearEntity.PanicGoal());
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new RandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new PolarBearEntity.HurtByTargetGoal());
        this.targetSelector.addGoal(2, new PolarBearEntity.AttackPlayerGoal());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, FoxEntity.class, 10, true, true, (Predicate<LivingEntity>)null));
        this.targetSelector.addGoal(5, new ResetAngerGoal<>(this, false));
    }

    public static AttributeModifierMap.MutableAttribute func_234219_eI_()
    {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 30.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 20.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 6.0D);
    }

    public static boolean func_223320_c(EntityType<PolarBearEntity> p_223320_0_, IWorld p_223320_1_, SpawnReason reason, BlockPos p_223320_3_, Random p_223320_4_)
    {
        Optional<RegistryKey<Biome>> optional = p_223320_1_.func_242406_i(p_223320_3_);

        if (!Objects.equals(optional, Optional.of(Biomes.FROZEN_OCEAN)) && !Objects.equals(optional, Optional.of(Biomes.DEEP_FROZEN_OCEAN)))
        {
            return canAnimalSpawn(p_223320_0_, p_223320_1_, reason, p_223320_3_, p_223320_4_);
        }
        else
        {
            return p_223320_1_.getLightSubtracted(p_223320_3_, 0) > 8 && p_223320_1_.getBlockState(p_223320_3_.down()).isIn(Blocks.ICE);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.readAngerNBT((ServerWorld)this.world, compound);
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        this.writeAngerNBT(compound);
    }

    public void func_230258_H__()
    {
        this.setAngerTime(field_234217_by_.getRandomWithinRange(this.rand));
    }

    public void setAngerTime(int time)
    {
        this.field_234218_bz_ = time;
    }

    public int getAngerTime()
    {
        return this.field_234218_bz_;
    }

    public void setAngerTarget(@Nullable UUID target)
    {
        this.field_234216_bA_ = target;
    }

    public UUID getAngerTarget()
    {
        return this.field_234216_bA_;
    }

    protected SoundEvent getAmbientSound()
    {
        return this.isChild() ? SoundEvents.ENTITY_POLAR_BEAR_AMBIENT_BABY : SoundEvents.ENTITY_POLAR_BEAR_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_POLAR_BEAR_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_POLAR_BEAR_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        this.playSound(SoundEvents.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void playWarningSound()
    {
        if (this.warningSoundTicks <= 0)
        {
            this.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, 1.0F, this.getSoundPitch());
            this.warningSoundTicks = 40;
        }
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(IS_STANDING, false);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        super.tick();

        if (this.world.isRemote)
        {
            if (this.clientSideStandAnimation != this.clientSideStandAnimation0)
            {
                this.recalculateSize();
            }

            this.clientSideStandAnimation0 = this.clientSideStandAnimation;

            if (this.isStanding())
            {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
            }
            else
            {
                this.clientSideStandAnimation = MathHelper.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.warningSoundTicks > 0)
        {
            --this.warningSoundTicks;
        }

        if (!this.world.isRemote)
        {
            this.func_241359_a_((ServerWorld)this.world, true);
        }
    }

    public EntitySize getSize(Pose poseIn)
    {
        if (this.clientSideStandAnimation > 0.0F)
        {
            float f = this.clientSideStandAnimation / 6.0F;
            float f1 = 1.0F + f;
            return super.getSize(poseIn).scale(1.0F, f1);
        }
        else
        {
            return super.getSize(poseIn);
        }
    }

    public boolean attackEntityAsMob(Entity entityIn)
    {
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));

        if (flag)
        {
            this.applyEnchantments(this, entityIn);
        }

        return flag;
    }

    public boolean isStanding()
    {
        return this.dataManager.get(IS_STANDING);
    }

    public void setStanding(boolean standing)
    {
        this.dataManager.set(IS_STANDING, standing);
    }

    public float getStandingAnimationScale(float p_189795_1_)
    {
        return MathHelper.lerp(p_189795_1_, this.clientSideStandAnimation0, this.clientSideStandAnimation) / 6.0F;
    }

    protected float getWaterSlowDown()
    {
        return 0.98F;
    }

    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        if (spawnDataIn == null)
        {
            spawnDataIn = new AgeableEntity.AgeableData(1.0F);
        }

        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    class AttackPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity>
    {
        public AttackPlayerGoal()
        {
            super(PolarBearEntity.this, PlayerEntity.class, 20, true, true, (Predicate<LivingEntity>)null);
        }

        public boolean shouldExecute()
        {
            if (PolarBearEntity.this.isChild())
            {
                return false;
            }
            else
            {
                if (super.shouldExecute())
                {
                    for (PolarBearEntity polarbearentity : PolarBearEntity.this.world.getEntitiesWithinAABB(PolarBearEntity.class, PolarBearEntity.this.getBoundingBox().grow(8.0D, 4.0D, 8.0D)))
                    {
                        if (polarbearentity.isChild())
                        {
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        protected double getTargetDistance()
        {
            return super.getTargetDistance() * 0.5D;
        }
    }

    class HurtByTargetGoal extends net.minecraft.entity.ai.goal.HurtByTargetGoal
    {
        public HurtByTargetGoal()
        {
            super(PolarBearEntity.this);
        }

        public void startExecuting()
        {
            super.startExecuting();

            if (PolarBearEntity.this.isChild())
            {
                this.alertOthers();
                this.resetTask();
            }
        }

        protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn)
        {
            if (mobIn instanceof PolarBearEntity && !mobIn.isChild())
            {
                super.setAttackTarget(mobIn, targetIn);
            }
        }
    }

    class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal
    {
        public MeleeAttackGoal()
        {
            super(PolarBearEntity.this, 1.25D, true);
        }

        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr)
        {
            double d0 = this.getAttackReachSqr(enemy);

            if (distToEnemySqr <= d0 && this.func_234040_h_())
            {
                this.func_234039_g_();
                this.attacker.attackEntityAsMob(enemy);
                PolarBearEntity.this.setStanding(false);
            }
            else if (distToEnemySqr <= d0 * 2.0D)
            {
                if (this.func_234040_h_())
                {
                    PolarBearEntity.this.setStanding(false);
                    this.func_234039_g_();
                }

                if (this.func_234041_j_() <= 10)
                {
                    PolarBearEntity.this.setStanding(true);
                    PolarBearEntity.this.playWarningSound();
                }
            }
            else
            {
                this.func_234039_g_();
                PolarBearEntity.this.setStanding(false);
            }
        }

        public void resetTask()
        {
            PolarBearEntity.this.setStanding(false);
            super.resetTask();
        }

        protected double getAttackReachSqr(LivingEntity attackTarget)
        {
            return (double)(4.0F + attackTarget.getWidth());
        }
    }

    class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal
    {
        public PanicGoal()
        {
            super(PolarBearEntity.this, 2.0D);
        }

        public boolean shouldExecute()
        {
            return !PolarBearEntity.this.isChild() && !PolarBearEntity.this.isBurning() ? false : super.shouldExecute();
        }
    }
}
