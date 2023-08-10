package net.minecraft.entity.passive;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class OcelotEntity extends AnimalEntity
{
    private static final Ingredient BREEDING_ITEMS = Ingredient.fromItems(Items.COD, Items.SALMON);
    private static final DataParameter<Boolean> IS_TRUSTING = EntityDataManager.createKey(OcelotEntity.class, DataSerializers.BOOLEAN);
    private OcelotEntity.AvoidEntityGoal<PlayerEntity> field_213531_bB;
    private OcelotEntity.TemptGoal aiTempt;

    public OcelotEntity(EntityType <? extends OcelotEntity > type, World worldIn)
    {
        super(type, worldIn);
        this.func_213529_dV();
    }

    private boolean isTrusting()
    {
        return this.dataManager.get(IS_TRUSTING);
    }

    private void setTrusting(boolean trusting)
    {
        this.dataManager.set(IS_TRUSTING, trusting);
        this.func_213529_dV();
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putBoolean("Trusting", this.isTrusting());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setTrusting(compound.getBoolean("Trusting"));
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(IS_TRUSTING, false);
    }

    protected void registerGoals()
    {
        this.aiTempt = new OcelotEntity.TemptGoal(this, 0.6D, BREEDING_ITEMS, true);
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(3, this.aiTempt);
        this.goalSelector.addGoal(7, new LeapAtTargetGoal(this, 0.3F));
        this.goalSelector.addGoal(8, new OcelotAttackGoal(this));
        this.goalSelector.addGoal(9, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(10, new WaterAvoidingRandomWalkingGoal(this, 0.8D, 1.0000001E-5F));
        this.goalSelector.addGoal(11, new LookAtGoal(this, PlayerEntity.class, 10.0F));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, ChickenEntity.class, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, false, false, TurtleEntity.TARGET_DRY_BABY));
    }

    public void updateAITasks()
    {
        if (this.getMoveHelper().isUpdating())
        {
            double d0 = this.getMoveHelper().getSpeed();

            if (d0 == 0.6D)
            {
                this.setPose(Pose.CROUCHING);
                this.setSprinting(false);
            }
            else if (d0 == 1.33D)
            {
                this.setPose(Pose.STANDING);
                this.setSprinting(true);
            }
            else
            {
                this.setPose(Pose.STANDING);
                this.setSprinting(false);
            }
        }
        else
        {
            this.setPose(Pose.STANDING);
            this.setSprinting(false);
        }
    }

    public boolean canDespawn(double distanceToClosestPlayer)
    {
        return !this.isTrusting() && this.ticksExisted > 2400;
    }

    public static AttributeModifierMap.MutableAttribute func_234201_eI_()
    {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 10.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.3F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    public boolean onLivingFall(float distance, float damageMultiplier)
    {
        return false;
    }

    @Nullable
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_OCELOT_AMBIENT;
    }

    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    public int getTalkInterval()
    {
        return 900;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_OCELOT_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_OCELOT_DEATH;
    }

    private float func_226517_es_()
    {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    public boolean attackEntityAsMob(Entity entityIn)
    {
        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), this.func_226517_es_());
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return this.isInvulnerableTo(source) ? false : super.attackEntityFrom(source, amount);
    }

    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_)
    {
        ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);

        if ((this.aiTempt == null || this.aiTempt.isRunning()) && !this.isTrusting() && this.isBreedingItem(itemstack) && p_230254_1_.getDistanceSq(this) < 9.0D)
        {
            this.consumeItemFromStack(p_230254_1_, itemstack);

            if (!this.world.isRemote)
            {
                if (this.rand.nextInt(3) == 0)
                {
                    this.setTrusting(true);
                    this.func_213527_s(true);
                    this.world.setEntityState(this, (byte)41);
                }
                else
                {
                    this.func_213527_s(false);
                    this.world.setEntityState(this, (byte)40);
                }
            }

            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        else
        {
            return super.func_230254_b_(p_230254_1_, p_230254_2_);
        }
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    public void handleStatusUpdate(byte id)
    {
        if (id == 41)
        {
            this.func_213527_s(true);
        }
        else if (id == 40)
        {
            this.func_213527_s(false);
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    private void func_213527_s(boolean p_213527_1_)
    {
        IParticleData iparticledata = ParticleTypes.HEART;

        if (!p_213527_1_)
        {
            iparticledata = ParticleTypes.SMOKE;
        }

        for (int i = 0; i < 7; ++i)
        {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.addParticle(iparticledata, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), d0, d1, d2);
        }
    }

    protected void func_213529_dV()
    {
        if (this.field_213531_bB == null)
        {
            this.field_213531_bB = new OcelotEntity.AvoidEntityGoal<>(this, PlayerEntity.class, 16.0F, 0.8D, 1.33D);
        }

        this.goalSelector.removeGoal(this.field_213531_bB);

        if (!this.isTrusting())
        {
            this.goalSelector.addGoal(4, this.field_213531_bB);
        }
    }

    public OcelotEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_)
    {
        return EntityType.OCELOT.create(p_241840_1_);
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    public boolean isBreedingItem(ItemStack stack)
    {
        return BREEDING_ITEMS.test(stack);
    }

    public static boolean func_223319_c(EntityType<OcelotEntity> p_223319_0_, IWorld p_223319_1_, SpawnReason p_223319_2_, BlockPos p_223319_3_, Random p_223319_4_)
    {
        return p_223319_4_.nextInt(3) != 0;
    }

    public boolean isNotColliding(IWorldReader worldIn)
    {
        if (worldIn.checkNoEntityCollision(this) && !worldIn.containsAnyLiquid(this.getBoundingBox()))
        {
            BlockPos blockpos = this.getPosition();

            if (blockpos.getY() < worldIn.getSeaLevel())
            {
                return false;
            }

            BlockState blockstate = worldIn.getBlockState(blockpos.down());

            if (blockstate.isIn(Blocks.GRASS_BLOCK) || blockstate.isIn(BlockTags.LEAVES))
            {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        if (spawnDataIn == null)
        {
            spawnDataIn = new AgeableEntity.AgeableData(1.0F);
        }

        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public Vector3d func_241205_ce_()
    {
        return new Vector3d(0.0D, (double)(0.5F * this.getEyeHeight()), (double)(this.getWidth() * 0.4F));
    }

    static class AvoidEntityGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.AvoidEntityGoal<T>
    {
        private final OcelotEntity ocelot;

        public AvoidEntityGoal(OcelotEntity ocelotIn, Class<T> p_i50037_2_, float p_i50037_3_, double p_i50037_4_, double p_i50037_6_)
        {
            super(ocelotIn, p_i50037_2_, p_i50037_3_, p_i50037_4_, p_i50037_6_, EntityPredicates.CAN_AI_TARGET::test);
            this.ocelot = ocelotIn;
        }

        public boolean shouldExecute()
        {
            return !this.ocelot.isTrusting() && super.shouldExecute();
        }

        public boolean shouldContinueExecuting()
        {
            return !this.ocelot.isTrusting() && super.shouldContinueExecuting();
        }
    }

    static class TemptGoal extends net.minecraft.entity.ai.goal.TemptGoal
    {
        private final OcelotEntity ocelot;

        public TemptGoal(OcelotEntity ocelotIn, double speedIn, Ingredient temptItemsIn, boolean p_i50036_5_)
        {
            super(ocelotIn, speedIn, temptItemsIn, p_i50036_5_);
            this.ocelot = ocelotIn;
        }

        protected boolean isScaredByPlayerMovement()
        {
            return super.isScaredByPlayerMovement() && !this.ocelot.isTrusting();
        }
    }
}
