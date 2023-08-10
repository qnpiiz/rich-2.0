package net.minecraft.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.BreakBlockGoal;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

public class ZombieEntity extends MonsterEntity
{
    private static final UUID BABY_SPEED_BOOST_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final AttributeModifier BABY_SPEED_BOOST = new AttributeModifier(BABY_SPEED_BOOST_ID, "Baby speed boost", 0.5D, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final DataParameter<Boolean> IS_CHILD = EntityDataManager.createKey(ZombieEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> VILLAGER_TYPE = EntityDataManager.createKey(ZombieEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> DROWNING = EntityDataManager.createKey(ZombieEntity.class, DataSerializers.BOOLEAN);
    private static final Predicate<Difficulty> HARD_DIFFICULTY_PREDICATE = (p_213697_0_) ->
    {
        return p_213697_0_ == Difficulty.HARD;
    };
    private final BreakDoorGoal breakDoor = new BreakDoorGoal(this, HARD_DIFFICULTY_PREDICATE);
    private boolean isBreakDoorsTaskSet;
    private int inWaterTime;
    private int drownedConversionTime;

    public ZombieEntity(EntityType <? extends ZombieEntity > type, World worldIn)
    {
        super(type, worldIn);
    }

    public ZombieEntity(World worldIn)
    {
        this(EntityType.ZOMBIE, worldIn);
    }

    protected void registerGoals()
    {
        this.goalSelector.addGoal(4, new ZombieEntity.AttackTurtleEggGoal(this, 1.0D, 3));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.applyEntityAI();
    }

    protected void applyEntityAI()
    {
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, true, 4, this::isBreakDoorsTaskSet));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp(ZombifiedPiglinEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.TARGET_DRY_BABY));
    }

    public static AttributeModifierMap.MutableAttribute func_234342_eQ_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.FOLLOW_RANGE, 35.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.23F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D).createMutableAttribute(Attributes.ARMOR, 2.0D).createMutableAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS);
    }

    protected void registerData()
    {
        super.registerData();
        this.getDataManager().register(IS_CHILD, false);
        this.getDataManager().register(VILLAGER_TYPE, 0);
        this.getDataManager().register(DROWNING, false);
    }

    public boolean isDrowning()
    {
        return this.getDataManager().get(DROWNING);
    }

    public boolean isBreakDoorsTaskSet()
    {
        return this.isBreakDoorsTaskSet;
    }

    /**
     * Sets or removes EntityAIBreakDoor task
     */
    public void setBreakDoorsAItask(boolean enabled)
    {
        if (this.canBreakDoors() && GroundPathHelper.isGroundNavigator(this))
        {
            if (this.isBreakDoorsTaskSet != enabled)
            {
                this.isBreakDoorsTaskSet = enabled;
                ((GroundPathNavigator)this.getNavigator()).setBreakDoors(enabled);

                if (enabled)
                {
                    this.goalSelector.addGoal(1, this.breakDoor);
                }
                else
                {
                    this.goalSelector.removeGoal(this.breakDoor);
                }
            }
        }
        else if (this.isBreakDoorsTaskSet)
        {
            this.goalSelector.removeGoal(this.breakDoor);
            this.isBreakDoorsTaskSet = false;
        }
    }

    protected boolean canBreakDoors()
    {
        return true;
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild()
    {
        return this.getDataManager().get(IS_CHILD);
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(PlayerEntity player)
    {
        if (this.isChild())
        {
            this.experienceValue = (int)((float)this.experienceValue * 2.5F);
        }

        return super.getExperiencePoints(player);
    }

    /**
     * Set whether this zombie is a child.
     */
    public void setChild(boolean childZombie)
    {
        this.getDataManager().set(IS_CHILD, childZombie);

        if (this.world != null && !this.world.isRemote)
        {
            ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            modifiableattributeinstance.removeModifier(BABY_SPEED_BOOST);

            if (childZombie)
            {
                modifiableattributeinstance.applyNonPersistentModifier(BABY_SPEED_BOOST);
            }
        }
    }

    public void notifyDataManagerChange(DataParameter<?> key)
    {
        if (IS_CHILD.equals(key))
        {
            this.recalculateSize();
        }

        super.notifyDataManagerChange(key);
    }

    protected boolean shouldDrown()
    {
        return true;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        if (!this.world.isRemote && this.isAlive() && !this.isAIDisabled())
        {
            if (this.isDrowning())
            {
                --this.drownedConversionTime;

                if (this.drownedConversionTime < 0)
                {
                    this.onDrowned();
                }
            }
            else if (this.shouldDrown())
            {
                if (this.areEyesInFluid(FluidTags.WATER))
                {
                    ++this.inWaterTime;

                    if (this.inWaterTime >= 600)
                    {
                        this.startDrowning(300);
                    }
                }
                else
                {
                    this.inWaterTime = -1;
                }
            }
        }

        super.tick();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void livingTick()
    {
        if (this.isAlive())
        {
            boolean flag = this.shouldBurnInDay() && this.isInDaylight();

            if (flag)
            {
                ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.HEAD);

                if (!itemstack.isEmpty())
                {
                    if (itemstack.isDamageable())
                    {
                        itemstack.setDamage(itemstack.getDamage() + this.rand.nextInt(2));

                        if (itemstack.getDamage() >= itemstack.getMaxDamage())
                        {
                            this.sendBreakAnimation(EquipmentSlotType.HEAD);
                            this.setItemStackToSlot(EquipmentSlotType.HEAD, ItemStack.EMPTY);
                        }
                    }

                    flag = false;
                }

                if (flag)
                {
                    this.setFire(8);
                }
            }
        }

        super.livingTick();
    }

    private void startDrowning(int p_204704_1_)
    {
        this.drownedConversionTime = p_204704_1_;
        this.getDataManager().set(DROWNING, true);
    }

    protected void onDrowned()
    {
        this.func_234341_c_(EntityType.DROWNED);

        if (!this.isSilent())
        {
            this.world.playEvent((PlayerEntity)null, 1040, this.getPosition(), 0);
        }
    }

    protected void func_234341_c_(EntityType <? extends ZombieEntity > p_234341_1_)
    {
        ZombieEntity zombieentity = this.func_233656_b_(p_234341_1_, true);

        if (zombieentity != null)
        {
            zombieentity.applyAttributeBonuses(zombieentity.world.getDifficultyForLocation(zombieentity.getPosition()).getClampedAdditionalDifficulty());
            zombieentity.setBreakDoorsAItask(zombieentity.canBreakDoors() && this.isBreakDoorsTaskSet());
        }
    }

    protected boolean shouldBurnInDay()
    {
        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!super.attackEntityFrom(source, amount))
        {
            return false;
        }
        else if (!(this.world instanceof ServerWorld))
        {
            return false;
        }
        else
        {
            ServerWorld serverworld = (ServerWorld)this.world;
            LivingEntity livingentity = this.getAttackTarget();

            if (livingentity == null && source.getTrueSource() instanceof LivingEntity)
            {
                livingentity = (LivingEntity)source.getTrueSource();
            }

            if (livingentity != null && this.world.getDifficulty() == Difficulty.HARD && (double)this.rand.nextFloat() < this.getAttributeValue(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS) && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING))
            {
                int i = MathHelper.floor(this.getPosX());
                int j = MathHelper.floor(this.getPosY());
                int k = MathHelper.floor(this.getPosZ());
                ZombieEntity zombieentity = new ZombieEntity(this.world);

                for (int l = 0; l < 50; ++l)
                {
                    int i1 = i + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
                    int j1 = j + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
                    int k1 = k + MathHelper.nextInt(this.rand, 7, 40) * MathHelper.nextInt(this.rand, -1, 1);
                    BlockPos blockpos = new BlockPos(i1, j1, k1);
                    EntityType<?> entitytype = zombieentity.getType();
                    EntitySpawnPlacementRegistry.PlacementType entityspawnplacementregistry$placementtype = EntitySpawnPlacementRegistry.getPlacementType(entitytype);

                    if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(entityspawnplacementregistry$placementtype, this.world, blockpos, entitytype) && EntitySpawnPlacementRegistry.canSpawnEntity(entitytype, serverworld, SpawnReason.REINFORCEMENT, blockpos, this.world.rand))
                    {
                        zombieentity.setPosition((double)i1, (double)j1, (double)k1);

                        if (!this.world.isPlayerWithin((double)i1, (double)j1, (double)k1, 7.0D) && this.world.checkNoEntityCollision(zombieentity) && this.world.hasNoCollisions(zombieentity) && !this.world.containsAnyLiquid(zombieentity.getBoundingBox()))
                        {
                            zombieentity.setAttackTarget(livingentity);
                            zombieentity.onInitialSpawn(serverworld, this.world.getDifficultyForLocation(zombieentity.getPosition()), SpawnReason.REINFORCEMENT, (ILivingEntityData)null, (CompoundNBT)null);
                            serverworld.func_242417_l(zombieentity);
                            this.getAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS).applyPersistentModifier(new AttributeModifier("Zombie reinforcement caller charge", (double) - 0.05F, AttributeModifier.Operation.ADDITION));
                            zombieentity.getAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS).applyPersistentModifier(new AttributeModifier("Zombie reinforcement callee charge", (double) - 0.05F, AttributeModifier.Operation.ADDITION));
                            break;
                        }
                    }
                }
            }

            return true;
        }
    }

    public boolean attackEntityAsMob(Entity entityIn)
    {
        boolean flag = super.attackEntityAsMob(entityIn);

        if (flag)
        {
            float f = this.world.getDifficultyForLocation(this.getPosition()).getAdditionalDifficulty();

            if (this.getHeldItemMainhand().isEmpty() && this.isBurning() && this.rand.nextFloat() < f * 0.3F)
            {
                entityIn.setFire(2 * (int)f);
            }
        }

        return flag;
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    protected SoundEvent getStepSound()
    {
        return SoundEvents.ENTITY_ZOMBIE_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    public CreatureAttribute getCreatureAttribute()
    {
        return CreatureAttribute.UNDEAD;
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
    {
        super.setEquipmentBasedOnDifficulty(difficulty);

        if (this.rand.nextFloat() < (this.world.getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F))
        {
            int i = this.rand.nextInt(3);

            if (i == 0)
            {
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));
            }
            else
            {
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putBoolean("IsBaby", this.isChild());
        compound.putBoolean("CanBreakDoors", this.isBreakDoorsTaskSet());
        compound.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
        compound.putInt("DrownedConversionTime", this.isDrowning() ? this.drownedConversionTime : -1);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setChild(compound.getBoolean("IsBaby"));
        this.setBreakDoorsAItask(compound.getBoolean("CanBreakDoors"));
        this.inWaterTime = compound.getInt("InWaterTime");

        if (compound.contains("DrownedConversionTime", 99) && compound.getInt("DrownedConversionTime") > -1)
        {
            this.startDrowning(compound.getInt("DrownedConversionTime"));
        }
    }

    public void func_241847_a(ServerWorld p_241847_1_, LivingEntity p_241847_2_)
    {
        super.func_241847_a(p_241847_1_, p_241847_2_);

        if ((p_241847_1_.getDifficulty() == Difficulty.NORMAL || p_241847_1_.getDifficulty() == Difficulty.HARD) && p_241847_2_ instanceof VillagerEntity)
        {
            if (p_241847_1_.getDifficulty() != Difficulty.HARD && this.rand.nextBoolean())
            {
                return;
            }

            VillagerEntity villagerentity = (VillagerEntity)p_241847_2_;
            ZombieVillagerEntity zombievillagerentity = villagerentity.func_233656_b_(EntityType.ZOMBIE_VILLAGER, false);
            zombievillagerentity.onInitialSpawn(p_241847_1_, p_241847_1_.getDifficultyForLocation(zombievillagerentity.getPosition()), SpawnReason.CONVERSION, new ZombieEntity.GroupData(false, true), (CompoundNBT)null);
            zombievillagerentity.setVillagerData(villagerentity.getVillagerData());
            zombievillagerentity.setGossips(villagerentity.getGossip().write(NBTDynamicOps.INSTANCE).getValue());
            zombievillagerentity.setOffers(villagerentity.getOffers().write());
            zombievillagerentity.setEXP(villagerentity.getXp());

            if (!this.isSilent())
            {
                p_241847_1_.playEvent((PlayerEntity)null, 1026, this.getPosition(), 0);
            }
        }
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return this.isChild() ? 0.93F : 1.74F;
    }

    public boolean canEquipItem(ItemStack stack)
    {
        return stack.getItem() == Items.EGG && this.isChild() && this.isPassenger() ? false : super.canEquipItem(stack);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        float f = difficultyIn.getClampedAdditionalDifficulty();
        this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * f);

        if (spawnDataIn == null)
        {
            spawnDataIn = new ZombieEntity.GroupData(func_241399_a_(worldIn.getRandom()), true);
        }

        if (spawnDataIn instanceof ZombieEntity.GroupData)
        {
            ZombieEntity.GroupData zombieentity$groupdata = (ZombieEntity.GroupData)spawnDataIn;

            if (zombieentity$groupdata.isChild)
            {
                this.setChild(true);

                if (zombieentity$groupdata.field_241400_b_)
                {
                    if ((double)worldIn.getRandom().nextFloat() < 0.05D)
                    {
                        List<ChickenEntity> list = worldIn.getEntitiesWithinAABB(ChickenEntity.class, this.getBoundingBox().grow(5.0D, 3.0D, 5.0D), EntityPredicates.IS_STANDALONE);

                        if (!list.isEmpty())
                        {
                            ChickenEntity chickenentity = list.get(0);
                            chickenentity.setChickenJockey(true);
                            this.startRiding(chickenentity);
                        }
                    }
                    else if ((double)worldIn.getRandom().nextFloat() < 0.05D)
                    {
                        ChickenEntity chickenentity1 = EntityType.CHICKEN.create(this.world);
                        chickenentity1.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, 0.0F);
                        chickenentity1.onInitialSpawn(worldIn, difficultyIn, SpawnReason.JOCKEY, (ILivingEntityData)null, (CompoundNBT)null);
                        chickenentity1.setChickenJockey(true);
                        this.startRiding(chickenentity1);
                        worldIn.addEntity(chickenentity1);
                    }
                }
            }

            this.setBreakDoorsAItask(this.canBreakDoors() && this.rand.nextFloat() < f * 0.1F);
            this.setEquipmentBasedOnDifficulty(difficultyIn);
            this.setEnchantmentBasedOnDifficulty(difficultyIn);
        }

        if (this.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty())
        {
            LocalDate localdate = LocalDate.now();
            int i = localdate.get(ChronoField.DAY_OF_MONTH);
            int j = localdate.get(ChronoField.MONTH_OF_YEAR);

            if (j == 10 && i == 31 && this.rand.nextFloat() < 0.25F)
            {
                this.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.inventoryArmorDropChances[EquipmentSlotType.HEAD.getIndex()] = 0.0F;
            }
        }

        this.applyAttributeBonuses(f);
        return spawnDataIn;
    }

    public static boolean func_241399_a_(Random p_241399_0_)
    {
        return p_241399_0_.nextFloat() < 0.05F;
    }

    protected void applyAttributeBonuses(float difficulty)
    {
        this.func_230291_eT_();
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).applyPersistentModifier(new AttributeModifier("Random spawn bonus", this.rand.nextDouble() * (double)0.05F, AttributeModifier.Operation.ADDITION));
        double d0 = this.rand.nextDouble() * 1.5D * (double)difficulty;

        if (d0 > 1.0D)
        {
            this.getAttribute(Attributes.FOLLOW_RANGE).applyPersistentModifier(new AttributeModifier("Random zombie-spawn bonus", d0, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

        if (this.rand.nextFloat() < difficulty * 0.05F)
        {
            this.getAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS).applyPersistentModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 0.25D + 0.5D, AttributeModifier.Operation.ADDITION));
            this.getAttribute(Attributes.MAX_HEALTH).applyPersistentModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 3.0D + 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
            this.setBreakDoorsAItask(this.canBreakDoors());
        }
    }

    protected void func_230291_eT_()
    {
        this.getAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(this.rand.nextDouble() * (double)0.1F);
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return this.isChild() ? 0.0D : -0.45D;
    }

    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn)
    {
        super.dropSpecialItems(source, looting, recentlyHitIn);
        Entity entity = source.getTrueSource();

        if (entity instanceof CreeperEntity)
        {
            CreeperEntity creeperentity = (CreeperEntity)entity;

            if (creeperentity.ableToCauseSkullDrop())
            {
                ItemStack itemstack = this.getSkullDrop();

                if (!itemstack.isEmpty())
                {
                    creeperentity.incrementDroppedSkulls();
                    this.entityDropItem(itemstack);
                }
            }
        }
    }

    protected ItemStack getSkullDrop()
    {
        return new ItemStack(Items.ZOMBIE_HEAD);
    }

    class AttackTurtleEggGoal extends BreakBlockGoal
    {
        AttackTurtleEggGoal(CreatureEntity creatureIn, double speed, int yMax)
        {
            super(Blocks.TURTLE_EGG, creatureIn, speed, yMax);
        }

        public void playBreakingSound(IWorld worldIn, BlockPos pos)
        {
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + ZombieEntity.this.rand.nextFloat() * 0.2F);
        }

        public void playBrokenSound(World worldIn, BlockPos pos)
        {
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + worldIn.rand.nextFloat() * 0.2F);
        }

        public double getTargetDistanceSq()
        {
            return 1.14D;
        }
    }

    public static class GroupData implements ILivingEntityData
    {
        public final boolean isChild;
        public final boolean field_241400_b_;

        public GroupData(boolean p_i231567_1_, boolean p_i231567_2_)
        {
            this.isChild = p_i231567_1_;
            this.field_241400_b_ = p_i231567_2_;
        }
    }
}
