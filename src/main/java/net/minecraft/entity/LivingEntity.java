package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.CombatRules;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;

public abstract class LivingEntity extends Entity
{
    private static final UUID SPRINTING_SPEED_BOOST_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final UUID SOUL_SPEED_BOOT_ID = UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038");
    private static final AttributeModifier SPRINTING_SPEED_BOOST = new AttributeModifier(SPRINTING_SPEED_BOOST_ID, "Sprinting speed boost", (double)0.3F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    protected static final DataParameter<Byte> LIVING_FLAGS = EntityDataManager.createKey(LivingEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Float> HEALTH = EntityDataManager.createKey(LivingEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> POTION_EFFECTS = EntityDataManager.createKey(LivingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> HIDE_PARTICLES = EntityDataManager.createKey(LivingEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ARROW_COUNT_IN_ENTITY = EntityDataManager.createKey(LivingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> BEE_STING_COUNT = EntityDataManager.createKey(LivingEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<BlockPos>> BED_POSITION = EntityDataManager.createKey(LivingEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
    protected static final EntitySize SLEEPING_SIZE = EntitySize.fixed(0.2F, 0.2F);
    private final AttributeModifierManager attributes;
    private final CombatTracker combatTracker = new CombatTracker(this);
    private final Map<Effect, EffectInstance> activePotionsMap = Maps.newHashMap();
    private final NonNullList<ItemStack> handInventory = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> armorArray = NonNullList.withSize(4, ItemStack.EMPTY);
    public boolean isSwingInProgress;
    public Hand swingingHand;
    public int swingProgressInt;
    public int arrowHitTimer;
    public int beeStingRemovalCooldown;
    public int hurtTime;
    public int maxHurtTime;
    public float attackedAtYaw;
    public int deathTime;
    public float prevSwingProgress;
    public float swingProgress;
    protected int ticksSinceLastSwing;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    public final int maxHurtResistantTime = 20;
    public final float randomUnused2;
    public final float randomUnused1;
    public float renderYawOffset;
    public float prevRenderYawOffset;
    public float rotationYawHead;
    public float prevRotationYawHead;
    public float jumpMovementFactor = 0.02F;
    @Nullable
    protected PlayerEntity attackingPlayer;
    protected int recentlyHit;
    protected boolean dead;
    protected int idleTime;
    protected float prevOnGroundSpeedFactor;
    protected float onGroundSpeedFactor;
    protected float movedDistance;
    protected float prevMovedDistance;
    protected float unused180;
    protected int scoreValue;

    /**
     * Damage taken in the last hit. Mobs are resistant to damage less than this for a short time after taking damage.
     */
    protected float lastDamage;
    protected boolean isJumping;
    public float moveStrafing;
    public float moveVertical;
    public float moveForward;
    protected int newPosRotationIncrements;
    protected double interpTargetX;
    protected double interpTargetY;
    protected double interpTargetZ;
    protected double interpTargetYaw;
    protected double interpTargetPitch;
    protected double interpTargetHeadYaw;
    protected int interpTicksHead;
    private boolean potionsNeedUpdate = true;
    @Nullable
    private LivingEntity revengeTarget;
    private int revengeTimer;
    private LivingEntity lastAttackedEntity;

    /** Holds the value of ticksExisted when setLastAttacker was last called. */
    private int lastAttackedEntityTime;
    private float landMovementFactor;
    private int jumpTicks;
    private float absorptionAmount;
    protected ItemStack activeItemStack = ItemStack.EMPTY;
    protected int activeItemStackUseCount;
    protected int ticksElytraFlying;
    private BlockPos prevBlockpos;
    private Optional<BlockPos> field_233624_bE_ = Optional.empty();
    private DamageSource lastDamageSource;
    private long lastDamageStamp;
    protected int spinAttackDuration;
    private float swimAnimation;
    private float lastSwimAnimation;
    protected Brain<?> brain;

    protected LivingEntity(EntityType <? extends LivingEntity > type, World worldIn)
    {
        super(type, worldIn);
        this.attributes = new AttributeModifierManager(GlobalEntityTypeAttributes.getAttributesForEntity(type));
        this.setHealth(this.getMaxHealth());
        this.preventEntitySpawning = true;
        this.randomUnused1 = (float)((Math.random() + 1.0D) * (double)0.01F);
        this.recenterBoundingBox();
        this.randomUnused2 = (float)Math.random() * 12398.0F;
        this.rotationYaw = (float)(Math.random() * (double)((float)Math.PI * 2F));
        this.rotationYawHead = this.rotationYaw;
        this.stepHeight = 0.6F;
        NBTDynamicOps nbtdynamicops = NBTDynamicOps.INSTANCE;
        this.brain = this.createBrain(new Dynamic<>(nbtdynamicops, nbtdynamicops.createMap(ImmutableMap.of(nbtdynamicops.createString("memories"), nbtdynamicops.emptyMap()))));
    }

    public Brain<?> getBrain()
    {
        return this.brain;
    }

    protected Brain.BrainCodec<?> getBrainCodec()
    {
        return Brain.createCodec(ImmutableList.of(), ImmutableList.of());
    }

    protected Brain<?> createBrain(Dynamic<?> dynamicIn)
    {
        return this.getBrainCodec().deserialize(dynamicIn);
    }

    /**
     * Called by the /kill command.
     */
    public void onKillCommand()
    {
        this.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    public boolean canAttack(EntityType<?> typeIn)
    {
        return true;
    }

    protected void registerData()
    {
        this.dataManager.register(LIVING_FLAGS, (byte)0);
        this.dataManager.register(POTION_EFFECTS, 0);
        this.dataManager.register(HIDE_PARTICLES, false);
        this.dataManager.register(ARROW_COUNT_IN_ENTITY, 0);
        this.dataManager.register(BEE_STING_COUNT, 0);
        this.dataManager.register(HEALTH, 1.0F);
        this.dataManager.register(BED_POSITION, Optional.empty());
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes()
    {
        return AttributeModifierMap.createMutableAttribute().createMutableAttribute(Attributes.MAX_HEALTH).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE).createMutableAttribute(Attributes.MOVEMENT_SPEED).createMutableAttribute(Attributes.ARMOR).createMutableAttribute(Attributes.ARMOR_TOUGHNESS);
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
        if (!this.isInWater())
        {
            this.func_233567_aH_();
        }

        if (!this.world.isRemote && onGroundIn && this.fallDistance > 0.0F)
        {
            this.func_233641_cN_();
            this.func_233642_cO_();
        }

        if (!this.world.isRemote && this.fallDistance > 3.0F && onGroundIn)
        {
            float f = (float)MathHelper.ceil(this.fallDistance - 3.0F);

            if (!state.isAir())
            {
                double d0 = Math.min((double)(0.2F + f / 15.0F), 2.5D);
                int i = (int)(150.0D * d0);
                ((ServerWorld)this.world).spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, state), this.getPosX(), this.getPosY(), this.getPosZ(), i, 0.0D, 0.0D, 0.0D, (double)0.15F);
            }
        }

        super.updateFallState(y, onGroundIn, state, pos);
    }

    public boolean canBreatheUnderwater()
    {
        return this.getCreatureAttribute() == CreatureAttribute.UNDEAD;
    }

    public float getSwimAnimation(float partialTicks)
    {
        return MathHelper.lerp(partialTicks, this.lastSwimAnimation, this.swimAnimation);
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void baseTick()
    {
        this.prevSwingProgress = this.swingProgress;

        if (this.firstUpdate)
        {
            this.getBedPosition().ifPresent(this::setSleepingPosition);
        }

        if (this.getMovementSpeed())
        {
            this.addSprintingEffect();
        }

        super.baseTick();
        this.world.getProfiler().startSection("livingEntityBaseTick");
        boolean flag = this instanceof PlayerEntity;

        if (this.isAlive())
        {
            if (this.isEntityInsideOpaqueBlock())
            {
                this.attackEntityFrom(DamageSource.IN_WALL, 1.0F);
            }
            else if (flag && !this.world.getWorldBorder().contains(this.getBoundingBox()))
            {
                double d0 = this.world.getWorldBorder().getClosestDistance(this) + this.world.getWorldBorder().getDamageBuffer();

                if (d0 < 0.0D)
                {
                    double d1 = this.world.getWorldBorder().getDamagePerBlock();

                    if (d1 > 0.0D)
                    {
                        this.attackEntityFrom(DamageSource.IN_WALL, (float)Math.max(1, MathHelper.floor(-d0 * d1)));
                    }
                }
            }
        }

        if (this.isImmuneToFire() || this.world.isRemote)
        {
            this.extinguish();
        }

        boolean flag1 = flag && ((PlayerEntity)this).abilities.disableDamage;

        if (this.isAlive())
        {
            if (this.areEyesInFluid(FluidTags.WATER) && !this.world.getBlockState(new BlockPos(this.getPosX(), this.getPosYEye(), this.getPosZ())).isIn(Blocks.BUBBLE_COLUMN))
            {
                if (!this.canBreatheUnderwater() && !EffectUtils.canBreatheUnderwater(this) && !flag1)
                {
                    this.setAir(this.decreaseAirSupply(this.getAir()));

                    if (this.getAir() == -20)
                    {
                        this.setAir(0);
                        Vector3d vector3d = this.getMotion();

                        for (int i = 0; i < 8; ++i)
                        {
                            double d2 = this.rand.nextDouble() - this.rand.nextDouble();
                            double d3 = this.rand.nextDouble() - this.rand.nextDouble();
                            double d4 = this.rand.nextDouble() - this.rand.nextDouble();
                            this.world.addParticle(ParticleTypes.BUBBLE, this.getPosX() + d2, this.getPosY() + d3, this.getPosZ() + d4, vector3d.x, vector3d.y, vector3d.z);
                        }

                        this.attackEntityFrom(DamageSource.DROWN, 2.0F);
                    }
                }

                if (!this.world.isRemote && this.isPassenger() && this.getRidingEntity() != null && !this.getRidingEntity().canBeRiddenInWater())
                {
                    this.stopRiding();
                }
            }
            else if (this.getAir() < this.getMaxAir())
            {
                this.setAir(this.determineNextAir(this.getAir()));
            }

            if (!this.world.isRemote)
            {
                BlockPos blockpos = this.getPosition();

                if (!Objects.equal(this.prevBlockpos, blockpos))
                {
                    this.prevBlockpos = blockpos;
                    this.frostWalk(blockpos);
                }
            }
        }

        if (this.isAlive() && this.isInWaterRainOrBubbleColumn())
        {
            this.extinguish();
        }

        if (this.hurtTime > 0)
        {
            --this.hurtTime;
        }

        if (this.hurtResistantTime > 0 && !(this instanceof ServerPlayerEntity))
        {
            --this.hurtResistantTime;
        }

        if (this.getShouldBeDead())
        {
            this.onDeathUpdate();
        }

        if (this.recentlyHit > 0)
        {
            --this.recentlyHit;
        }
        else
        {
            this.attackingPlayer = null;
        }

        if (this.lastAttackedEntity != null && !this.lastAttackedEntity.isAlive())
        {
            this.lastAttackedEntity = null;
        }

        if (this.revengeTarget != null)
        {
            if (!this.revengeTarget.isAlive())
            {
                this.setRevengeTarget((LivingEntity)null);
            }
            else if (this.ticksExisted - this.revengeTimer > 100)
            {
                this.setRevengeTarget((LivingEntity)null);
            }
        }

        this.updatePotionEffects();
        this.prevMovedDistance = this.movedDistance;
        this.prevRenderYawOffset = this.renderYawOffset;
        this.prevRotationYawHead = this.rotationYawHead;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.world.getProfiler().endSection();
    }

    public boolean getMovementSpeed()
    {
        return this.ticksExisted % 5 == 0 && this.getMotion().x != 0.0D && this.getMotion().z != 0.0D && !this.isSpectator() && EnchantmentHelper.hasSoulSpeed(this) && this.func_230296_cM_();
    }

    protected void addSprintingEffect()
    {
        Vector3d vector3d = this.getMotion();
        this.world.addParticle(ParticleTypes.SOUL, this.getPosX() + (this.rand.nextDouble() - 0.5D) * (double)this.getWidth(), this.getPosY() + 0.1D, this.getPosZ() + (this.rand.nextDouble() - 0.5D) * (double)this.getWidth(), vector3d.x * -0.2D, 0.1D, vector3d.z * -0.2D);
        float f = this.rand.nextFloat() * 0.4F + this.rand.nextFloat() > 0.9F ? 0.6F : 0.0F;
        this.playSound(SoundEvents.PARTICLE_SOUL_ESCAPE, f, 0.6F + this.rand.nextFloat() * 0.4F);
    }

    protected boolean func_230296_cM_()
    {
        return this.world.getBlockState(this.getPositionUnderneath()).isIn(BlockTags.SOUL_SPEED_BLOCKS);
    }

    protected float getSpeedFactor()
    {
        return this.func_230296_cM_() && EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.SOUL_SPEED, this) > 0 ? 1.0F : super.getSpeedFactor();
    }

    protected boolean func_230295_b_(BlockState p_230295_1_)
    {
        return !p_230295_1_.isAir() || this.isElytraFlying();
    }

    protected void func_233641_cN_()
    {
        ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);

        if (modifiableattributeinstance != null)
        {
            if (modifiableattributeinstance.getModifier(SOUL_SPEED_BOOT_ID) != null)
            {
                modifiableattributeinstance.removeModifier(SOUL_SPEED_BOOT_ID);
            }
        }
    }

    protected void func_233642_cO_()
    {
        if (!this.getStateBelow().isAir())
        {
            int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.SOUL_SPEED, this);

            if (i > 0 && this.func_230296_cM_())
            {
                ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);

                if (modifiableattributeinstance == null)
                {
                    return;
                }

                modifiableattributeinstance.applyNonPersistentModifier(new AttributeModifier(SOUL_SPEED_BOOT_ID, "Soul speed boost", (double)(0.03F * (1.0F + (float)i * 0.35F)), AttributeModifier.Operation.ADDITION));

                if (this.getRNG().nextFloat() < 0.04F)
                {
                    ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.FEET);
                    itemstack.damageItem(1, this, (p_233654_0_) ->
                    {
                        p_233654_0_.sendBreakAnimation(EquipmentSlotType.FEET);
                    });
                }
            }
        }
    }

    protected void frostWalk(BlockPos pos)
    {
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FROST_WALKER, this);

        if (i > 0)
        {
            FrostWalkerEnchantment.freezeNearby(this, this.world, pos, i);
        }

        if (this.func_230295_b_(this.getStateBelow()))
        {
            this.func_233641_cN_();
        }

        this.func_233642_cO_();
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild()
    {
        return false;
    }

    public float getRenderScale()
    {
        return this.isChild() ? 0.5F : 1.0F;
    }

    protected boolean func_241208_cS_()
    {
        return true;
    }

    public boolean canBeRiddenInWater()
    {
        return false;
    }

    /**
     * handles entity death timer, experience orb and particle creation
     */
    protected void onDeathUpdate()
    {
        ++this.deathTime;

        if (this.deathTime == 20)
        {
            this.remove();

            for (int i = 0; i < 20; ++i)
            {
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.POOF, this.getPosXRandom(1.0D), this.getPosYRandom(), this.getPosZRandom(1.0D), d0, d1, d2);
            }
        }
    }

    /**
     * Entity won't drop items or experience points if this returns false
     */
    protected boolean canDropLoot()
    {
        return !this.isChild();
    }

    protected boolean func_230282_cS_()
    {
        return !this.isChild();
    }

    /**
     * Decrements the entity's air supply when underwater
     */
    protected int decreaseAirSupply(int air)
    {
        int i = EnchantmentHelper.getRespirationModifier(this);
        return i > 0 && this.rand.nextInt(i + 1) > 0 ? air : air - 1;
    }

    protected int determineNextAir(int currentAir)
    {
        return Math.min(currentAir + 4, this.getMaxAir());
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(PlayerEntity player)
    {
        return 0;
    }

    /**
     * Only use is to identify if class is an instance of player for experience dropping
     */
    protected boolean isPlayer()
    {
        return false;
    }

    public Random getRNG()
    {
        return this.rand;
    }

    @Nullable
    public LivingEntity getRevengeTarget()
    {
        return this.revengeTarget;
    }

    public int getRevengeTimer()
    {
        return this.revengeTimer;
    }

    public void func_230246_e_(@Nullable PlayerEntity p_230246_1_)
    {
        this.attackingPlayer = p_230246_1_;
        this.recentlyHit = this.ticksExisted;
    }

    /**
     * Hint to AI tasks that we were attacked by the passed EntityLivingBase and should retaliate. Is not guaranteed to
     * change our actual active target (for example if we are currently busy attacking someone else)
     */
    public void setRevengeTarget(@Nullable LivingEntity livingBase)
    {
        this.revengeTarget = livingBase;
        this.revengeTimer = this.ticksExisted;
    }

    @Nullable
    public LivingEntity getLastAttackedEntity()
    {
        return this.lastAttackedEntity;
    }

    public int getLastAttackedEntityTime()
    {
        return this.lastAttackedEntityTime;
    }

    public void setLastAttackedEntity(Entity entityIn)
    {
        if (entityIn instanceof LivingEntity)
        {
            this.lastAttackedEntity = (LivingEntity)entityIn;
        }
        else
        {
            this.lastAttackedEntity = null;
        }

        this.lastAttackedEntityTime = this.ticksExisted;
    }

    public int getIdleTime()
    {
        return this.idleTime;
    }

    public void setIdleTime(int idleTimeIn)
    {
        this.idleTime = idleTimeIn;
    }

    protected void playEquipSound(ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            SoundEvent soundevent = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
            Item item = stack.getItem();

            if (item instanceof ArmorItem)
            {
                soundevent = ((ArmorItem)item).getArmorMaterial().getSoundEvent();
            }
            else if (item == Items.ELYTRA)
            {
                soundevent = SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA;
            }

            this.playSound(soundevent, 1.0F, 1.0F);
        }
    }

    public void writeAdditional(CompoundNBT compound)
    {
        compound.putFloat("Health", this.getHealth());
        compound.putShort("HurtTime", (short)this.hurtTime);
        compound.putInt("HurtByTimestamp", this.revengeTimer);
        compound.putShort("DeathTime", (short)this.deathTime);
        compound.putFloat("AbsorptionAmount", this.getAbsorptionAmount());
        compound.put("Attributes", this.getAttributeManager().serialize());

        if (!this.activePotionsMap.isEmpty())
        {
            ListNBT listnbt = new ListNBT();

            for (EffectInstance effectinstance : this.activePotionsMap.values())
            {
                listnbt.add(effectinstance.write(new CompoundNBT()));
            }

            compound.put("ActiveEffects", listnbt);
        }

        compound.putBoolean("FallFlying", this.isElytraFlying());
        this.getBedPosition().ifPresent((p_213338_1_) ->
        {
            compound.putInt("SleepingX", p_213338_1_.getX());
            compound.putInt("SleepingY", p_213338_1_.getY());
            compound.putInt("SleepingZ", p_213338_1_.getZ());
        });
        DataResult<INBT> dataresult = this.brain.encode(NBTDynamicOps.INSTANCE);
        dataresult.resultOrPartial(LOGGER::error).ifPresent((p_233636_1_) ->
        {
            compound.put("Brain", p_233636_1_);
        });
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        this.setAbsorptionAmount(compound.getFloat("AbsorptionAmount"));

        if (compound.contains("Attributes", 9) && this.world != null && !this.world.isRemote)
        {
            this.getAttributeManager().deserialize(compound.getList("Attributes", 10));
        }

        if (compound.contains("ActiveEffects", 9))
        {
            ListNBT listnbt = compound.getList("ActiveEffects", 10);

            for (int i = 0; i < listnbt.size(); ++i)
            {
                CompoundNBT compoundnbt = listnbt.getCompound(i);
                EffectInstance effectinstance = EffectInstance.read(compoundnbt);

                if (effectinstance != null)
                {
                    this.activePotionsMap.put(effectinstance.getPotion(), effectinstance);
                }
            }
        }

        if (compound.contains("Health", 99))
        {
            this.setHealth(compound.getFloat("Health"));
        }

        this.hurtTime = compound.getShort("HurtTime");
        this.deathTime = compound.getShort("DeathTime");
        this.revengeTimer = compound.getInt("HurtByTimestamp");

        if (compound.contains("Team", 8))
        {
            String s = compound.getString("Team");
            ScorePlayerTeam scoreplayerteam = this.world.getScoreboard().getTeam(s);
            boolean flag = scoreplayerteam != null && this.world.getScoreboard().addPlayerToTeam(this.getCachedUniqueIdString(), scoreplayerteam);

            if (!flag)
            {
                LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", (Object)s);
            }
        }

        if (compound.getBoolean("FallFlying"))
        {
            this.setFlag(7, true);
        }

        if (compound.contains("SleepingX", 99) && compound.contains("SleepingY", 99) && compound.contains("SleepingZ", 99))
        {
            BlockPos blockpos = new BlockPos(compound.getInt("SleepingX"), compound.getInt("SleepingY"), compound.getInt("SleepingZ"));
            this.setBedPosition(blockpos);
            this.dataManager.set(POSE, Pose.SLEEPING);

            if (!this.firstUpdate)
            {
                this.setSleepingPosition(blockpos);
            }
        }

        if (compound.contains("Brain", 10))
        {
            this.brain = this.createBrain(new Dynamic<>(NBTDynamicOps.INSTANCE, compound.get("Brain")));
        }
    }

    protected void updatePotionEffects()
    {
        Iterator<Effect> iterator = this.activePotionsMap.keySet().iterator();

        try
        {
            while (iterator.hasNext())
            {
                Effect effect = iterator.next();
                EffectInstance effectinstance = this.activePotionsMap.get(effect);

                if (!effectinstance.tick(this, () ->
            {
                this.onChangedPotionEffect(effectinstance, true);
                }))
                {
                    if (!this.world.isRemote)
                    {
                        iterator.remove();
                        this.onFinishedPotionEffect(effectinstance);
                    }
                }
                else if (effectinstance.getDuration() % 600 == 0)
                {
                    this.onChangedPotionEffect(effectinstance, false);
                }
            }
        }
        catch (ConcurrentModificationException concurrentmodificationexception)
        {
        }

        if (this.potionsNeedUpdate)
        {
            if (!this.world.isRemote)
            {
                this.updatePotionMetadata();
            }

            this.potionsNeedUpdate = false;
        }

        int i = this.dataManager.get(POTION_EFFECTS);
        boolean flag1 = this.dataManager.get(HIDE_PARTICLES);

        if (i > 0)
        {
            boolean flag;

            if (this.isInvisible())
            {
                flag = this.rand.nextInt(15) == 0;
            }
            else
            {
                flag = this.rand.nextBoolean();
            }

            if (flag1)
            {
                flag &= this.rand.nextInt(5) == 0;
            }

            if (flag && i > 0)
            {
                double d0 = (double)(i >> 16 & 255) / 255.0D;
                double d1 = (double)(i >> 8 & 255) / 255.0D;
                double d2 = (double)(i >> 0 & 255) / 255.0D;
                this.world.addParticle(flag1 ? ParticleTypes.AMBIENT_ENTITY_EFFECT : ParticleTypes.ENTITY_EFFECT, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), d0, d1, d2);
            }
        }
    }

    /**
     * Clears potion metadata values if the entity has no potion effects. Otherwise, updates potion effect color,
     * ambience, and invisibility metadata values
     */
    protected void updatePotionMetadata()
    {
        if (this.activePotionsMap.isEmpty())
        {
            this.resetPotionEffectMetadata();
            this.setInvisible(false);
        }
        else
        {
            Collection<EffectInstance> collection = this.activePotionsMap.values();
            this.dataManager.set(HIDE_PARTICLES, areAllPotionsAmbient(collection));
            this.dataManager.set(POTION_EFFECTS, PotionUtils.getPotionColorFromEffectList(collection));
            this.setInvisible(this.isPotionActive(Effects.INVISIBILITY));
        }
    }

    public double getVisibilityMultiplier(@Nullable Entity lookingEntity)
    {
        double d0 = 1.0D;

        if (this.isDiscrete())
        {
            d0 *= 0.8D;
        }

        if (this.isInvisible())
        {
            float f = this.getArmorCoverPercentage();

            if (f < 0.1F)
            {
                f = 0.1F;
            }

            d0 *= 0.7D * (double)f;
        }

        if (lookingEntity != null)
        {
            ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.HEAD);
            Item item = itemstack.getItem();
            EntityType<?> entitytype = lookingEntity.getType();

            if (entitytype == EntityType.SKELETON && item == Items.SKELETON_SKULL || entitytype == EntityType.ZOMBIE && item == Items.ZOMBIE_HEAD || entitytype == EntityType.CREEPER && item == Items.CREEPER_HEAD)
            {
                d0 *= 0.5D;
            }
        }

        return d0;
    }

    public boolean canAttack(LivingEntity target)
    {
        return true;
    }

    public boolean canAttack(LivingEntity livingentityIn, EntityPredicate predicateIn)
    {
        return predicateIn.canTarget(this, livingentityIn);
    }

    /**
     * Returns true if all of the potion effects in the specified collection are ambient.
     */
    public static boolean areAllPotionsAmbient(Collection<EffectInstance> potionEffects)
    {
        for (EffectInstance effectinstance : potionEffects)
        {
            if (!effectinstance.isAmbient())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Resets the potion effect color and ambience metadata values
     */
    protected void resetPotionEffectMetadata()
    {
        this.dataManager.set(HIDE_PARTICLES, false);
        this.dataManager.set(POTION_EFFECTS, 0);
    }

    public boolean clearActivePotions()
    {
        if (this.world.isRemote)
        {
            return false;
        }
        else
        {
            Iterator<EffectInstance> iterator = this.activePotionsMap.values().iterator();
            boolean flag;

            for (flag = false; iterator.hasNext(); flag = true)
            {
                this.onFinishedPotionEffect(iterator.next());
                iterator.remove();
            }

            return flag;
        }
    }

    public Collection<EffectInstance> getActivePotionEffects()
    {
        return this.activePotionsMap.values();
    }

    public Map<Effect, EffectInstance> getActivePotionMap()
    {
        return this.activePotionsMap;
    }

    public boolean isPotionActive(Effect potionIn)
    {
        return this.activePotionsMap.containsKey(potionIn);
    }

    @Nullable

    /**
     * returns the PotionEffect for the supplied Potion if it is active, null otherwise.
     */
    public EffectInstance getActivePotionEffect(Effect potionIn)
    {
        return this.activePotionsMap.get(potionIn);
    }

    public boolean addPotionEffect(EffectInstance effectInstanceIn)
    {
        if (!this.isPotionApplicable(effectInstanceIn))
        {
            return false;
        }
        else
        {
            EffectInstance effectinstance = this.activePotionsMap.get(effectInstanceIn.getPotion());

            if (effectinstance == null)
            {
                this.activePotionsMap.put(effectInstanceIn.getPotion(), effectInstanceIn);
                this.onNewPotionEffect(effectInstanceIn);
                return true;
            }
            else if (effectinstance.combine(effectInstanceIn))
            {
                this.onChangedPotionEffect(effectinstance, true);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public boolean isPotionApplicable(EffectInstance potioneffectIn)
    {
        if (this.getCreatureAttribute() == CreatureAttribute.UNDEAD)
        {
            Effect effect = potioneffectIn.getPotion();

            if (effect == Effects.REGENERATION || effect == Effects.POISON)
            {
                return false;
            }
        }

        return true;
    }

    public void func_233646_e_(EffectInstance p_233646_1_)
    {
        if (this.isPotionApplicable(p_233646_1_))
        {
            EffectInstance effectinstance = this.activePotionsMap.put(p_233646_1_.getPotion(), p_233646_1_);

            if (effectinstance == null)
            {
                this.onNewPotionEffect(p_233646_1_);
            }
            else
            {
                this.onChangedPotionEffect(p_233646_1_, true);
            }
        }
    }

    /**
     * Returns true if this entity is undead.
     */
    public boolean isEntityUndead()
    {
        return this.getCreatureAttribute() == CreatureAttribute.UNDEAD;
    }

    @Nullable

    /**
     * Removes the given potion effect from the active potion map and returns it. Does not call cleanup callbacks for
     * the end of the potion effect.
     */
    public EffectInstance removeActivePotionEffect(@Nullable Effect potioneffectin)
    {
        return this.activePotionsMap.remove(potioneffectin);
    }

    public boolean removePotionEffect(Effect effectIn)
    {
        EffectInstance effectinstance = this.removeActivePotionEffect(effectIn);

        if (effectinstance != null)
        {
            this.onFinishedPotionEffect(effectinstance);
            return true;
        }
        else
        {
            return false;
        }
    }

    protected void onNewPotionEffect(EffectInstance id)
    {
        this.potionsNeedUpdate = true;

        if (!this.world.isRemote)
        {
            id.getPotion().applyAttributesModifiersToEntity(this, this.getAttributeManager(), id.getAmplifier());
        }
    }

    protected void onChangedPotionEffect(EffectInstance id, boolean reapply)
    {
        this.potionsNeedUpdate = true;

        if (reapply && !this.world.isRemote)
        {
            Effect effect = id.getPotion();
            effect.removeAttributesModifiersFromEntity(this, this.getAttributeManager(), id.getAmplifier());
            effect.applyAttributesModifiersToEntity(this, this.getAttributeManager(), id.getAmplifier());
        }
    }

    protected void onFinishedPotionEffect(EffectInstance effect)
    {
        this.potionsNeedUpdate = true;

        if (!this.world.isRemote)
        {
            effect.getPotion().removeAttributesModifiersFromEntity(this, this.getAttributeManager(), effect.getAmplifier());
        }
    }

    /**
     * Heal living entity (param: amount of half-hearts)
     */
    public void heal(float healAmount)
    {
        float f = this.getHealth();

        if (f > 0.0F)
        {
            this.setHealth(f + healAmount);
        }
    }

    public float getHealth()
    {
        return this.dataManager.get(HEALTH);
    }

    public void setHealth(float health)
    {
        this.dataManager.set(HEALTH, MathHelper.clamp(health, 0.0F, this.getMaxHealth()));
    }

    public boolean getShouldBeDead()
    {
        return this.getHealth() <= 0.0F;
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
        else if (this.world.isRemote)
        {
            return false;
        }
        else if (this.getShouldBeDead())
        {
            return false;
        }
        else if (source.isFireDamage() && this.isPotionActive(Effects.FIRE_RESISTANCE))
        {
            return false;
        }
        else
        {
            if (this.isSleeping() && !this.world.isRemote)
            {
                this.wakeUp();
            }

            this.idleTime = 0;
            float f = amount;

            if ((source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK) && !this.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty())
            {
                this.getItemStackFromSlot(EquipmentSlotType.HEAD).damageItem((int)(amount * 4.0F + this.rand.nextFloat() * amount * 2.0F), this, (p_233653_0_) ->
                {
                    p_233653_0_.sendBreakAnimation(EquipmentSlotType.HEAD);
                });
                amount *= 0.75F;
            }

            boolean flag = false;
            float f1 = 0.0F;

            if (amount > 0.0F && this.canBlockDamageSource(source))
            {
                this.damageShield(amount);
                f1 = amount;
                amount = 0.0F;

                if (!source.isProjectile())
                {
                    Entity entity = source.getImmediateSource();

                    if (entity instanceof LivingEntity)
                    {
                        this.blockUsingShield((LivingEntity)entity);
                    }
                }

                flag = true;
            }

            this.limbSwingAmount = 1.5F;
            boolean flag1 = true;

            if ((float)this.hurtResistantTime > 10.0F)
            {
                if (amount <= this.lastDamage)
                {
                    return false;
                }

                this.damageEntity(source, amount - this.lastDamage);
                this.lastDamage = amount;
                flag1 = false;
            }
            else
            {
                this.lastDamage = amount;
                this.hurtResistantTime = 20;
                this.damageEntity(source, amount);
                this.maxHurtTime = 10;
                this.hurtTime = this.maxHurtTime;
            }

            this.attackedAtYaw = 0.0F;
            Entity entity1 = source.getTrueSource();

            if (entity1 != null)
            {
                if (entity1 instanceof LivingEntity)
                {
                    this.setRevengeTarget((LivingEntity)entity1);
                }

                if (entity1 instanceof PlayerEntity)
                {
                    this.recentlyHit = 100;
                    this.attackingPlayer = (PlayerEntity)entity1;
                }
                else if (entity1 instanceof WolfEntity)
                {
                    WolfEntity wolfentity = (WolfEntity)entity1;

                    if (wolfentity.isTamed())
                    {
                        this.recentlyHit = 100;
                        LivingEntity livingentity = wolfentity.getOwner();

                        if (livingentity != null && livingentity.getType() == EntityType.PLAYER)
                        {
                            this.attackingPlayer = (PlayerEntity)livingentity;
                        }
                        else
                        {
                            this.attackingPlayer = null;
                        }
                    }
                }
            }

            if (flag1)
            {
                if (flag)
                {
                    this.world.setEntityState(this, (byte)29);
                }
                else if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage())
                {
                    this.world.setEntityState(this, (byte)33);
                }
                else
                {
                    byte b0;

                    if (source == DamageSource.DROWN)
                    {
                        b0 = 36;
                    }
                    else if (source.isFireDamage())
                    {
                        b0 = 37;
                    }
                    else if (source == DamageSource.SWEET_BERRY_BUSH)
                    {
                        b0 = 44;
                    }
                    else
                    {
                        b0 = 2;
                    }

                    this.world.setEntityState(this, b0);
                }

                if (source != DamageSource.DROWN && (!flag || amount > 0.0F))
                {
                    this.markVelocityChanged();
                }

                if (entity1 != null)
                {
                    double d1 = entity1.getPosX() - this.getPosX();
                    double d0;

                    for (d0 = entity1.getPosZ() - this.getPosZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
                    {
                        d1 = (Math.random() - Math.random()) * 0.01D;
                    }

                    this.attackedAtYaw = (float)(MathHelper.atan2(d0, d1) * (double)(180F / (float)Math.PI) - (double)this.rotationYaw);
                    this.applyKnockback(0.4F, d1, d0);
                }
                else
                {
                    this.attackedAtYaw = (float)((int)(Math.random() * 2.0D) * 180);
                }
            }

            if (this.getShouldBeDead())
            {
                if (!this.checkTotemDeathProtection(source))
                {
                    SoundEvent soundevent = this.getDeathSound();

                    if (flag1 && soundevent != null)
                    {
                        this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
                    }

                    this.onDeath(source);
                }
            }
            else if (flag1)
            {
                this.playHurtSound(source);
            }

            boolean flag2 = !flag || amount > 0.0F;

            if (flag2)
            {
                this.lastDamageSource = source;
                this.lastDamageStamp = this.world.getGameTime();
            }

            if (this instanceof ServerPlayerEntity)
            {
                CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((ServerPlayerEntity)this, source, f, amount, flag);

                if (f1 > 0.0F && f1 < 3.4028235E37F)
                {
                    ((ServerPlayerEntity)this).addStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(f1 * 10.0F));
                }
            }

            if (entity1 instanceof ServerPlayerEntity)
            {
                CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayerEntity)entity1, this, source, f, amount, flag);
            }

            return flag2;
        }
    }

    protected void blockUsingShield(LivingEntity entityIn)
    {
        entityIn.constructKnockBackVector(this);
    }

    protected void constructKnockBackVector(LivingEntity entityIn)
    {
        entityIn.applyKnockback(0.5F, entityIn.getPosX() - this.getPosX(), entityIn.getPosZ() - this.getPosZ());
    }

    private boolean checkTotemDeathProtection(DamageSource damageSourceIn)
    {
        if (damageSourceIn.canHarmInCreative())
        {
            return false;
        }
        else
        {
            ItemStack itemstack = null;

            for (Hand hand : Hand.values())
            {
                ItemStack itemstack1 = this.getHeldItem(hand);

                if (itemstack1.getItem() == Items.TOTEM_OF_UNDYING)
                {
                    itemstack = itemstack1.copy();
                    itemstack1.shrink(1);
                    break;
                }
            }

            if (itemstack != null)
            {
                if (this instanceof ServerPlayerEntity)
                {
                    ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)this;
                    serverplayerentity.addStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                    CriteriaTriggers.USED_TOTEM.trigger(serverplayerentity, itemstack);
                }

                this.setHealth(1.0F);
                this.clearActivePotions();
                this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 900, 1));
                this.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
                this.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 800, 0));
                this.world.setEntityState(this, (byte)35);
            }

            return itemstack != null;
        }
    }

    @Nullable
    public DamageSource getLastDamageSource()
    {
        if (this.world.getGameTime() - this.lastDamageStamp > 40L)
        {
            this.lastDamageSource = null;
        }

        return this.lastDamageSource;
    }

    protected void playHurtSound(DamageSource source)
    {
        SoundEvent soundevent = this.getHurtSound(source);

        if (soundevent != null)
        {
            this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    /**
     * Determines whether the entity can block the damage source based on the damage source's location, whether the
     * damage source is blockable, and whether the entity is blocking.
     */
    private boolean canBlockDamageSource(DamageSource damageSourceIn)
    {
        Entity entity = damageSourceIn.getImmediateSource();
        boolean flag = false;

        if (entity instanceof AbstractArrowEntity)
        {
            AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity)entity;

            if (abstractarrowentity.getPierceLevel() > 0)
            {
                flag = true;
            }
        }

        if (!damageSourceIn.isUnblockable() && this.isActiveItemStackBlocking() && !flag)
        {
            Vector3d vector3d2 = damageSourceIn.getDamageLocation();

            if (vector3d2 != null)
            {
                Vector3d vector3d = this.getLook(1.0F);
                Vector3d vector3d1 = vector3d2.subtractReverse(this.getPositionVec()).normalize();
                vector3d1 = new Vector3d(vector3d1.x, 0.0D, vector3d1.z);

                if (vector3d1.dotProduct(vector3d) < 0.0D)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Renders broken item particles using the given ItemStack
     */
    private void renderBrokenItemStack(ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            if (!this.isSilent())
            {
                this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ITEM_BREAK, this.getSoundCategory(), 0.8F, 0.8F + this.world.rand.nextFloat() * 0.4F, false);
            }

            this.addItemParticles(stack, 5);
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource cause)
    {
        if (!this.removed && !this.dead)
        {
            Entity entity = cause.getTrueSource();
            LivingEntity livingentity = this.getAttackingEntity();

            if (this.scoreValue >= 0 && livingentity != null)
            {
                livingentity.awardKillScore(this, this.scoreValue, cause);
            }

            if (this.isSleeping())
            {
                this.wakeUp();
            }

            this.dead = true;
            this.getCombatTracker().reset();

            if (this.world instanceof ServerWorld)
            {
                if (entity != null)
                {
                    entity.func_241847_a((ServerWorld)this.world, this);
                }

                this.spawnDrops(cause);
                this.createWitherRose(livingentity);
            }

            this.world.setEntityState(this, (byte)3);
            this.setPose(Pose.DYING);
        }
    }

    protected void createWitherRose(@Nullable LivingEntity entitySource)
    {
        if (!this.world.isRemote)
        {
            boolean flag = false;

            if (entitySource instanceof WitherEntity)
            {
                if (this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING))
                {
                    BlockPos blockpos = this.getPosition();
                    BlockState blockstate = Blocks.WITHER_ROSE.getDefaultState();

                    if (this.world.getBlockState(blockpos).isAir() && blockstate.isValidPosition(this.world, blockpos))
                    {
                        this.world.setBlockState(blockpos, blockstate, 3);
                        flag = true;
                    }
                }

                if (!flag)
                {
                    ItemEntity itementity = new ItemEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), new ItemStack(Items.WITHER_ROSE));
                    this.world.addEntity(itementity);
                }
            }
        }
    }

    protected void spawnDrops(DamageSource damageSourceIn)
    {
        Entity entity = damageSourceIn.getTrueSource();
        int i;

        if (entity instanceof PlayerEntity)
        {
            i = EnchantmentHelper.getLootingModifier((LivingEntity)entity);
        }
        else
        {
            i = 0;
        }

        boolean flag = this.recentlyHit > 0;

        if (this.func_230282_cS_() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))
        {
            this.dropLoot(damageSourceIn, flag);
            this.dropSpecialItems(damageSourceIn, i, flag);
        }

        this.dropInventory();
        this.dropExperience();
    }

    protected void dropInventory()
    {
    }

    protected void dropExperience()
    {
        if (!this.world.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)))
        {
            int i = this.getExperiencePoints(this.attackingPlayer);

            while (i > 0)
            {
                int j = ExperienceOrbEntity.getXPSplit(i);
                i -= j;
                this.world.addEntity(new ExperienceOrbEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), j));
            }
        }
    }

    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn)
    {
    }

    public ResourceLocation getLootTableResourceLocation()
    {
        return this.getType().getLootTable();
    }

    protected void dropLoot(DamageSource damageSourceIn, boolean attackedRecently)
    {
        ResourceLocation resourcelocation = this.getLootTableResourceLocation();
        LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(resourcelocation);
        LootContext.Builder lootcontext$builder = this.getLootContextBuilder(attackedRecently, damageSourceIn);
        loottable.generate(lootcontext$builder.build(LootParameterSets.ENTITY), this::entityDropItem);
    }

    protected LootContext.Builder getLootContextBuilder(boolean attackedRecently, DamageSource damageSourceIn)
    {
        LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.world)).withRandom(this.rand).withParameter(LootParameters.THIS_ENTITY, this).withParameter(LootParameters.field_237457_g_, this.getPositionVec()).withParameter(LootParameters.DAMAGE_SOURCE, damageSourceIn).withNullableParameter(LootParameters.KILLER_ENTITY, damageSourceIn.getTrueSource()).withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, damageSourceIn.getImmediateSource());

        if (attackedRecently && this.attackingPlayer != null)
        {
            lootcontext$builder = lootcontext$builder.withParameter(LootParameters.LAST_DAMAGE_PLAYER, this.attackingPlayer).withLuck(this.attackingPlayer.getLuck());
        }

        return lootcontext$builder;
    }

    public void applyKnockback(float strength, double ratioX, double ratioZ)
    {
        strength = (float)((double)strength * (1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));

        if (!(strength <= 0.0F))
        {
            this.isAirBorne = true;
            Vector3d vector3d = this.getMotion();
            Vector3d vector3d1 = (new Vector3d(ratioX, 0.0D, ratioZ)).normalize().scale((double)strength);
            this.setMotion(vector3d.x / 2.0D - vector3d1.x, this.onGround ? Math.min(0.4D, vector3d.y / 2.0D + (double)strength) : vector3d.y, vector3d.z / 2.0D - vector3d1.z);
        }
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_GENERIC_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_GENERIC_DEATH;
    }

    protected SoundEvent getFallSound(int heightIn)
    {
        return heightIn > 4 ? SoundEvents.ENTITY_GENERIC_BIG_FALL : SoundEvents.ENTITY_GENERIC_SMALL_FALL;
    }

    protected SoundEvent getDrinkSound(ItemStack stack)
    {
        return stack.getDrinkSound();
    }

    public SoundEvent getEatSound(ItemStack itemStackIn)
    {
        return itemStackIn.getEatSound();
    }

    public void setOnGround(boolean grounded)
    {
        super.setOnGround(grounded);

        if (grounded)
        {
            this.field_233624_bE_ = Optional.empty();
        }
    }

    public Optional<BlockPos> func_233644_dn_()
    {
        return this.field_233624_bE_;
    }

    /**
     * Returns true if this entity should move as if it were on a ladder (either because it's actually on a ladder, or
     * for AI reasons)
     */
    public boolean isOnLadder()
    {
        if (this.isSpectator())
        {
            return false;
        }
        else
        {
            BlockPos blockpos = this.getPosition();
            BlockState blockstate = this.getBlockState();
            Block block = blockstate.getBlock();

            if (block.isIn(BlockTags.CLIMBABLE))
            {
                this.field_233624_bE_ = Optional.of(blockpos);
                return true;
            }
            else if (block instanceof TrapDoorBlock && this.canGoThroughtTrapDoorOnLadder(blockpos, blockstate))
            {
                this.field_233624_bE_ = Optional.of(blockpos);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public BlockState getBlockState()
    {
        return this.world.getBlockState(this.getPosition());
    }

    private boolean canGoThroughtTrapDoorOnLadder(BlockPos pos, BlockState state)
    {
        if (state.get(TrapDoorBlock.OPEN))
        {
            BlockState blockstate = this.world.getBlockState(pos.down());

            if (blockstate.isIn(Blocks.LADDER) && blockstate.get(LadderBlock.FACING) == state.get(TrapDoorBlock.HORIZONTAL_FACING))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the entity has not been {@link #removed}.
     */
    public boolean isAlive()
    {
        return !this.removed && this.getHealth() > 0.0F;
    }

    public boolean onLivingFall(float distance, float damageMultiplier)
    {
        boolean flag = super.onLivingFall(distance, damageMultiplier);
        int i = this.calculateFallDamage(distance, damageMultiplier);

        if (i > 0)
        {
            this.playSound(this.getFallSound(i), 1.0F, 1.0F);
            this.playFallSound();
            this.attackEntityFrom(DamageSource.FALL, (float)i);
            return true;
        }
        else
        {
            return flag;
        }
    }

    protected int calculateFallDamage(float distance, float damageMultiplier)
    {
        EffectInstance effectinstance = this.getActivePotionEffect(Effects.JUMP_BOOST);
        float f = effectinstance == null ? 0.0F : (float)(effectinstance.getAmplifier() + 1);
        return MathHelper.ceil((distance - 3.0F - f) * damageMultiplier);
    }

    /**
     * Plays the fall sound for the block landed on
     */
    protected void playFallSound()
    {
        if (!this.isSilent())
        {
            int i = MathHelper.floor(this.getPosX());
            int j = MathHelper.floor(this.getPosY() - (double)0.2F);
            int k = MathHelper.floor(this.getPosZ());
            BlockState blockstate = this.world.getBlockState(new BlockPos(i, j, k));

            if (!blockstate.isAir())
            {
                SoundType soundtype = blockstate.getSoundType();
                this.playSound(soundtype.getFallSound(), soundtype.getVolume() * 0.5F, soundtype.getPitch() * 0.75F);
            }
        }
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation()
    {
        this.maxHurtTime = 10;
        this.hurtTime = this.maxHurtTime;
        this.attackedAtYaw = 0.0F;
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        return MathHelper.floor(this.getAttributeValue(Attributes.ARMOR));
    }

    protected void damageArmor(DamageSource damageSource, float damage)
    {
    }

    protected void damageShield(float damage)
    {
    }

    /**
     * Reduces damage, depending on armor
     */
    protected float applyArmorCalculations(DamageSource source, float damage)
    {
        if (!source.isUnblockable())
        {
            this.damageArmor(source, damage);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float)this.getTotalArmorValue(), (float)this.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        }

        return damage;
    }

    /**
     * Reduces damage, depending on potions
     */
    protected float applyPotionDamageCalculations(DamageSource source, float damage)
    {
        if (source.isDamageAbsolute())
        {
            return damage;
        }
        else
        {
            if (this.isPotionActive(Effects.RESISTANCE) && source != DamageSource.OUT_OF_WORLD)
            {
                int i = (this.getActivePotionEffect(Effects.RESISTANCE).getAmplifier() + 1) * 5;
                int j = 25 - i;
                float f = damage * (float)j;
                float f1 = damage;
                damage = Math.max(f / 25.0F, 0.0F);
                float f2 = f1 - damage;

                if (f2 > 0.0F && f2 < 3.4028235E37F)
                {
                    if (this instanceof ServerPlayerEntity)
                    {
                        ((ServerPlayerEntity)this).addStat(Stats.DAMAGE_RESISTED, Math.round(f2 * 10.0F));
                    }
                    else if (source.getTrueSource() instanceof ServerPlayerEntity)
                    {
                        ((ServerPlayerEntity)source.getTrueSource()).addStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(f2 * 10.0F));
                    }
                }
            }

            if (damage <= 0.0F)
            {
                return 0.0F;
            }
            else
            {
                int k = EnchantmentHelper.getEnchantmentModifierDamage(this.getArmorInventoryList(), source);

                if (k > 0)
                {
                    damage = CombatRules.getDamageAfterMagicAbsorb(damage, (float)k);
                }

                return damage;
            }
        }
    }

    /**
     * Deals damage to the entity. This will take the armor of the entity into consideration before damaging the health
     * bar.
     */
    protected void damageEntity(DamageSource damageSrc, float damageAmount)
    {
        if (!this.isInvulnerableTo(damageSrc))
        {
            damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
            damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
            float f2 = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (damageAmount - f2));
            float f = damageAmount - f2;

            if (f > 0.0F && f < 3.4028235E37F && damageSrc.getTrueSource() instanceof ServerPlayerEntity)
            {
                ((ServerPlayerEntity)damageSrc.getTrueSource()).addStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(f * 10.0F));
            }

            if (f2 != 0.0F)
            {
                float f1 = this.getHealth();
                this.setHealth(f1 - f2);
                this.getCombatTracker().trackDamage(damageSrc, f1, f2);
                this.setAbsorptionAmount(this.getAbsorptionAmount() - f2);
            }
        }
    }

    /**
     * 1.8.9
     */
    public CombatTracker getCombatTracker()
    {
        return this.combatTracker;
    }

    @Nullable
    public LivingEntity getAttackingEntity()
    {
        if (this.combatTracker.getBestAttacker() != null)
        {
            return this.combatTracker.getBestAttacker();
        }
        else if (this.attackingPlayer != null)
        {
            return this.attackingPlayer;
        }
        else
        {
            return this.revengeTarget != null ? this.revengeTarget : null;
        }
    }

    /**
     * Returns the maximum health of the entity (what it is able to regenerate up to, what it spawned with, etc)
     */
    public final float getMaxHealth()
    {
        return (float)this.getAttributeValue(Attributes.MAX_HEALTH);
    }

    /**
     * counts the amount of arrows stuck in the entity. getting hit by arrows increases this, used in rendering
     */
    public final int getArrowCountInEntity()
    {
        return this.dataManager.get(ARROW_COUNT_IN_ENTITY);
    }

    /**
     * sets the amount of arrows stuck in the entity. used for rendering those
     */
    public final void setArrowCountInEntity(int count)
    {
        this.dataManager.set(ARROW_COUNT_IN_ENTITY, count);
    }

    public final int getBeeStingCount()
    {
        return this.dataManager.get(BEE_STING_COUNT);
    }

    public final void setBeeStingCount(int p_226300_1_)
    {
        this.dataManager.set(BEE_STING_COUNT, p_226300_1_);
    }

    /**
     * Returns an integer indicating the end point of the swing animation, used by {@link #swingProgress} to provide a
     * progress indicator. Takes dig speed enchantments into account.
     */
    private int getArmSwingAnimationEnd()
    {
        if (EffectUtils.hasMiningSpeedup(this))
        {
            return 6 - (1 + EffectUtils.getMiningSpeedup(this));
        }
        else
        {
            return this.isPotionActive(Effects.MINING_FATIGUE) ? 6 + (1 + this.getActivePotionEffect(Effects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
        }
    }

    public void swingArm(Hand hand)
    {
        this.swing(hand, false);
    }

    public void swing(Hand handIn, boolean updateSelf)
    {
        if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0)
        {
            this.swingProgressInt = -1;
            this.isSwingInProgress = true;
            this.swingingHand = handIn;

            if (this.world instanceof ServerWorld)
            {
                SAnimateHandPacket sanimatehandpacket = new SAnimateHandPacket(this, handIn == Hand.MAIN_HAND ? 0 : 3);
                ServerChunkProvider serverchunkprovider = ((ServerWorld)this.world).getChunkProvider();

                if (updateSelf)
                {
                    serverchunkprovider.sendToTrackingAndSelf(this, sanimatehandpacket);
                }
                else
                {
                    serverchunkprovider.sendToAllTracking(this, sanimatehandpacket);
                }
            }
        }
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    public void handleStatusUpdate(byte id)
    {
        switch (id)
        {
            case 2:
            case 33:
            case 36:
            case 37:
            case 44:
                boolean flag1 = id == 33;
                boolean flag2 = id == 36;
                boolean flag3 = id == 37;
                boolean flag = id == 44;
                this.limbSwingAmount = 1.5F;
                this.hurtResistantTime = 20;
                this.maxHurtTime = 10;
                this.hurtTime = this.maxHurtTime;
                this.attackedAtYaw = 0.0F;

                if (flag1)
                {
                    this.playSound(SoundEvents.ENCHANT_THORNS_HIT, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                }

                DamageSource damagesource;

                if (flag3)
                {
                    damagesource = DamageSource.ON_FIRE;
                }
                else if (flag2)
                {
                    damagesource = DamageSource.DROWN;
                }
                else if (flag)
                {
                    damagesource = DamageSource.SWEET_BERRY_BUSH;
                }
                else
                {
                    damagesource = DamageSource.GENERIC;
                }

                SoundEvent soundevent1 = this.getHurtSound(damagesource);

                if (soundevent1 != null)
                {
                    this.playSound(soundevent1, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                }

                this.attackEntityFrom(DamageSource.GENERIC, 0.0F);
                break;

            case 3:
                SoundEvent soundevent = this.getDeathSound();

                if (soundevent != null)
                {
                    this.playSound(soundevent, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                }

                if (!(this instanceof PlayerEntity))
                {
                    this.setHealth(0.0F);
                    this.onDeath(DamageSource.GENERIC);
                }

                break;

            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 31:
            case 32:
            case 34:
            case 35:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 45:
            case 53:
            default:
                super.handleStatusUpdate(id);
                break;

            case 29:
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + this.world.rand.nextFloat() * 0.4F);
                break;

            case 30:
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.rand.nextFloat() * 0.4F);
                break;

            case 46:
                int i = 128;

                for (int j = 0; j < 128; ++j)
                {
                    double d0 = (double)j / 127.0D;
                    float f = (this.rand.nextFloat() - 0.5F) * 0.2F;
                    float f1 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                    float f2 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                    double d1 = MathHelper.lerp(d0, this.prevPosX, this.getPosX()) + (this.rand.nextDouble() - 0.5D) * (double)this.getWidth() * 2.0D;
                    double d2 = MathHelper.lerp(d0, this.prevPosY, this.getPosY()) + this.rand.nextDouble() * (double)this.getHeight();
                    double d3 = MathHelper.lerp(d0, this.prevPosZ, this.getPosZ()) + (this.rand.nextDouble() - 0.5D) * (double)this.getWidth() * 2.0D;
                    this.world.addParticle(ParticleTypes.PORTAL, d1, d2, d3, (double)f, (double)f1, (double)f2);
                }

                break;

            case 47:
                this.renderBrokenItemStack(this.getItemStackFromSlot(EquipmentSlotType.MAINHAND));
                break;

            case 48:
                this.renderBrokenItemStack(this.getItemStackFromSlot(EquipmentSlotType.OFFHAND));
                break;

            case 49:
                this.renderBrokenItemStack(this.getItemStackFromSlot(EquipmentSlotType.HEAD));
                break;

            case 50:
                this.renderBrokenItemStack(this.getItemStackFromSlot(EquipmentSlotType.CHEST));
                break;

            case 51:
                this.renderBrokenItemStack(this.getItemStackFromSlot(EquipmentSlotType.LEGS));
                break;

            case 52:
                this.renderBrokenItemStack(this.getItemStackFromSlot(EquipmentSlotType.FEET));
                break;

            case 54:
                HoneyBlock.livingSlideParticles(this);
                break;

            case 55:
                this.swapHands();
        }
    }

    private void swapHands()
    {
        ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
        this.setItemStackToSlot(EquipmentSlotType.OFFHAND, this.getItemStackFromSlot(EquipmentSlotType.MAINHAND));
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack);
    }

    /**
     * sets the dead flag. Used when you fall off the bottom of the world.
     */
    protected void outOfWorld()
    {
        this.attackEntityFrom(DamageSource.OUT_OF_WORLD, 4.0F);
    }

    /**
     * Updates the arm swing progress counters and animation progress
     */
    protected void updateArmSwingProgress()
    {
        int i = this.getArmSwingAnimationEnd();

        if (this.isSwingInProgress)
        {
            ++this.swingProgressInt;

            if (this.swingProgressInt >= i)
            {
                this.swingProgressInt = 0;
                this.isSwingInProgress = false;
            }
        }
        else
        {
            this.swingProgressInt = 0;
        }

        this.swingProgress = (float)this.swingProgressInt / (float)i;
    }

    @Nullable
    public ModifiableAttributeInstance getAttribute(Attribute attribute)
    {
        return this.getAttributeManager().createInstanceIfAbsent(attribute);
    }

    public double getAttributeValue(Attribute attribute)
    {
        return this.getAttributeManager().getAttributeValue(attribute);
    }

    public double getBaseAttributeValue(Attribute attribute)
    {
        return this.getAttributeManager().getAttributeBaseValue(attribute);
    }

    public AttributeModifierManager getAttributeManager()
    {
        return this.attributes;
    }

    public CreatureAttribute getCreatureAttribute()
    {
        return CreatureAttribute.UNDEFINED;
    }

    public ItemStack getHeldItemMainhand()
    {
        return this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
    }

    public ItemStack getHeldItemOffhand()
    {
        return this.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
    }

    public boolean canEquip(Item item)
    {
        return this.func_233634_a_((p_233632_1_) ->
        {
            return p_233632_1_ == item;
        });
    }

    public boolean func_233634_a_(Predicate<Item> p_233634_1_)
    {
        return p_233634_1_.test(this.getHeldItemMainhand().getItem()) || p_233634_1_.test(this.getHeldItemOffhand().getItem());
    }

    public ItemStack getHeldItem(Hand hand)
    {
        if (hand == Hand.MAIN_HAND)
        {
            return this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        }
        else if (hand == Hand.OFF_HAND)
        {
            return this.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
        }
        else
        {
            throw new IllegalArgumentException("Invalid hand " + hand);
        }
    }

    public void setHeldItem(Hand hand, ItemStack stack)
    {
        if (hand == Hand.MAIN_HAND)
        {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
        }
        else
        {
            if (hand != Hand.OFF_HAND)
            {
                throw new IllegalArgumentException("Invalid hand " + hand);
            }

            this.setItemStackToSlot(EquipmentSlotType.OFFHAND, stack);
        }
    }

    public boolean hasItemInSlot(EquipmentSlotType slotIn)
    {
        return !this.getItemStackFromSlot(slotIn).isEmpty();
    }

    public abstract Iterable<ItemStack> getArmorInventoryList();

    public abstract ItemStack getItemStackFromSlot(EquipmentSlotType slotIn);

    public abstract void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack);

    public float getArmorCoverPercentage()
    {
        Iterable<ItemStack> iterable = this.getArmorInventoryList();
        int i = 0;
        int j = 0;

        for (ItemStack itemstack : iterable)
        {
            if (!itemstack.isEmpty())
            {
                ++j;
            }

            ++i;
        }

        return i > 0 ? (float)j / (float)i : 0.0F;
    }

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting(boolean sprinting)
    {
        super.setSprinting(sprinting);
        ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);

        if (modifiableattributeinstance.getModifier(SPRINTING_SPEED_BOOST_ID) != null)
        {
            modifiableattributeinstance.removeModifier(SPRINTING_SPEED_BOOST);
        }

        if (sprinting)
        {
            modifiableattributeinstance.applyNonPersistentModifier(SPRINTING_SPEED_BOOST);
        }
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 1.0F;
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    protected float getSoundPitch()
    {
        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
    }

    /**
     * Dead and sleeping entities cannot move
     */
    protected boolean isMovementBlocked()
    {
        return this.getShouldBeDead();
    }

    /**
     * Applies a velocity to the entities, to push them away from eachother.
     */
    public void applyEntityCollision(Entity entityIn)
    {
        if (!this.isSleeping())
        {
            super.applyEntityCollision(entityIn);
        }
    }

    private void func_233628_a_(Entity p_233628_1_)
    {
        Vector3d vector3d;

        if (!p_233628_1_.removed && !this.world.getBlockState(p_233628_1_.getPosition()).getBlock().isIn(BlockTags.PORTALS))
        {
            vector3d = p_233628_1_.func_230268_c_(this);
        }
        else
        {
            vector3d = new Vector3d(p_233628_1_.getPosX(), p_233628_1_.getPosY() + (double)p_233628_1_.getHeight(), p_233628_1_.getPosZ());
        }

        this.setPositionAndUpdate(vector3d.x, vector3d.y, vector3d.z);
    }

    public boolean getAlwaysRenderNameTagForRender()
    {
        return this.isCustomNameVisible();
    }

    protected float getJumpUpwardsMotion()
    {
        return 0.42F * this.getJumpFactor();
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void jump()
    {
        float f = this.getJumpUpwardsMotion();

        if (this.isPotionActive(Effects.JUMP_BOOST))
        {
            f += 0.1F * (float)(this.getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier() + 1);
        }

        Vector3d vector3d = this.getMotion();
        this.setMotion(vector3d.x, (double)f, vector3d.z);

        if (this.isSprinting())
        {
            float f1 = this.rotationYaw * ((float)Math.PI / 180F);
            this.setMotion(this.getMotion().add((double)(-MathHelper.sin(f1) * 0.2F), 0.0D, (double)(MathHelper.cos(f1) * 0.2F)));
        }

        this.isAirBorne = true;
    }

    protected void handleFluidSneak()
    {
        this.setMotion(this.getMotion().add(0.0D, (double) - 0.04F, 0.0D));
    }

    protected void handleFluidJump(ITag<Fluid> fluidTag)
    {
        this.setMotion(this.getMotion().add(0.0D, (double)0.04F, 0.0D));
    }

    protected float getWaterSlowDown()
    {
        return 0.8F;
    }

    public boolean func_230285_a_(Fluid p_230285_1_)
    {
        return false;
    }

    public void travel(Vector3d travelVector)
    {
        if (this.isServerWorld() || this.canPassengerSteer())
        {
            double d0 = 0.08D;
            boolean flag = this.getMotion().y <= 0.0D;

            if (flag && this.isPotionActive(Effects.SLOW_FALLING))
            {
                d0 = 0.01D;
                this.fallDistance = 0.0F;
            }

            FluidState fluidstate = this.world.getFluidState(this.getPosition());

            if (this.isInWater() && this.func_241208_cS_() && !this.func_230285_a_(fluidstate.getFluid()))
            {
                double d8 = this.getPosY();
                float f5 = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
                float f6 = 0.02F;
                float f7 = (float)EnchantmentHelper.getDepthStriderModifier(this);

                if (f7 > 3.0F)
                {
                    f7 = 3.0F;
                }

                if (!this.onGround)
                {
                    f7 *= 0.5F;
                }

                if (f7 > 0.0F)
                {
                    f5 += (0.54600006F - f5) * f7 / 3.0F;
                    f6 += (this.getAIMoveSpeed() - f6) * f7 / 3.0F;
                }

                if (this.isPotionActive(Effects.DOLPHINS_GRACE))
                {
                    f5 = 0.96F;
                }

                this.moveRelative(f6, travelVector);
                this.move(MoverType.SELF, this.getMotion());
                Vector3d vector3d6 = this.getMotion();

                if (this.collidedHorizontally && this.isOnLadder())
                {
                    vector3d6 = new Vector3d(vector3d6.x, 0.2D, vector3d6.z);
                }

                this.setMotion(vector3d6.mul((double)f5, (double)0.8F, (double)f5));
                Vector3d vector3d2 = this.func_233626_a_(d0, flag, this.getMotion());
                this.setMotion(vector3d2);

                if (this.collidedHorizontally && this.isOffsetPositionInLiquid(vector3d2.x, vector3d2.y + (double)0.6F - this.getPosY() + d8, vector3d2.z))
                {
                    this.setMotion(vector3d2.x, (double)0.3F, vector3d2.z);
                }
            }
            else if (this.isInLava() && this.func_241208_cS_() && !this.func_230285_a_(fluidstate.getFluid()))
            {
                double d7 = this.getPosY();
                this.moveRelative(0.02F, travelVector);
                this.move(MoverType.SELF, this.getMotion());

                if (this.func_233571_b_(FluidTags.LAVA) <= this.func_233579_cu_())
                {
                    this.setMotion(this.getMotion().mul(0.5D, (double)0.8F, 0.5D));
                    Vector3d vector3d3 = this.func_233626_a_(d0, flag, this.getMotion());
                    this.setMotion(vector3d3);
                }
                else
                {
                    this.setMotion(this.getMotion().scale(0.5D));
                }

                if (!this.hasNoGravity())
                {
                    this.setMotion(this.getMotion().add(0.0D, -d0 / 4.0D, 0.0D));
                }

                Vector3d vector3d4 = this.getMotion();

                if (this.collidedHorizontally && this.isOffsetPositionInLiquid(vector3d4.x, vector3d4.y + (double)0.6F - this.getPosY() + d7, vector3d4.z))
                {
                    this.setMotion(vector3d4.x, (double)0.3F, vector3d4.z);
                }
            }
            else if (this.isElytraFlying())
            {
                Vector3d vector3d = this.getMotion();

                if (vector3d.y > -0.5D)
                {
                    this.fallDistance = 1.0F;
                }

                Vector3d vector3d1 = this.getLookVec();
                float f = this.rotationPitch * ((float)Math.PI / 180F);
                double d1 = Math.sqrt(vector3d1.x * vector3d1.x + vector3d1.z * vector3d1.z);
                double d3 = Math.sqrt(horizontalMag(vector3d));
                double d4 = vector3d1.length();
                float f1 = MathHelper.cos(f);
                f1 = (float)((double)f1 * (double)f1 * Math.min(1.0D, d4 / 0.4D));
                vector3d = this.getMotion().add(0.0D, d0 * (-1.0D + (double)f1 * 0.75D), 0.0D);

                if (vector3d.y < 0.0D && d1 > 0.0D)
                {
                    double d5 = vector3d.y * -0.1D * (double)f1;
                    vector3d = vector3d.add(vector3d1.x * d5 / d1, d5, vector3d1.z * d5 / d1);
                }

                if (f < 0.0F && d1 > 0.0D)
                {
                    double d9 = d3 * (double)(-MathHelper.sin(f)) * 0.04D;
                    vector3d = vector3d.add(-vector3d1.x * d9 / d1, d9 * 3.2D, -vector3d1.z * d9 / d1);
                }

                if (d1 > 0.0D)
                {
                    vector3d = vector3d.add((vector3d1.x / d1 * d3 - vector3d.x) * 0.1D, 0.0D, (vector3d1.z / d1 * d3 - vector3d.z) * 0.1D);
                }

                this.setMotion(vector3d.mul((double)0.99F, (double)0.98F, (double)0.99F));
                this.move(MoverType.SELF, this.getMotion());

                if (this.collidedHorizontally && !this.world.isRemote)
                {
                    double d10 = Math.sqrt(horizontalMag(this.getMotion()));
                    double d6 = d3 - d10;
                    float f2 = (float)(d6 * 10.0D - 3.0D);

                    if (f2 > 0.0F)
                    {
                        this.playSound(this.getFallSound((int)f2), 1.0F, 1.0F);
                        this.attackEntityFrom(DamageSource.FLY_INTO_WALL, f2);
                    }
                }

                if (this.onGround && !this.world.isRemote)
                {
                    this.setFlag(7, false);
                }
            }
            else
            {
                BlockPos blockpos = this.getPositionUnderneath();
                float f3 = this.world.getBlockState(blockpos).getBlock().getSlipperiness();
                float f4 = this.onGround ? f3 * 0.91F : 0.91F;
                Vector3d vector3d5 = this.func_233633_a_(travelVector, f3);
                double d2 = vector3d5.y;

                if (this.isPotionActive(Effects.LEVITATION))
                {
                    d2 += (0.05D * (double)(this.getActivePotionEffect(Effects.LEVITATION).getAmplifier() + 1) - vector3d5.y) * 0.2D;
                    this.fallDistance = 0.0F;
                }
                else if (this.world.isRemote && !this.world.isBlockLoaded(blockpos))
                {
                    if (this.getPosY() > 0.0D)
                    {
                        d2 = -0.1D;
                    }
                    else
                    {
                        d2 = 0.0D;
                    }
                }
                else if (!this.hasNoGravity())
                {
                    d2 -= d0;
                }

                this.setMotion(vector3d5.x * (double)f4, d2 * (double)0.98F, vector3d5.z * (double)f4);
            }
        }

        this.func_233629_a_(this, this instanceof IFlyingAnimal);
    }

    public void func_233629_a_(LivingEntity p_233629_1_, boolean p_233629_2_)
    {
        p_233629_1_.prevLimbSwingAmount = p_233629_1_.limbSwingAmount;
        double d0 = p_233629_1_.getPosX() - p_233629_1_.prevPosX;
        double d1 = p_233629_2_ ? p_233629_1_.getPosY() - p_233629_1_.prevPosY : 0.0D;
        double d2 = p_233629_1_.getPosZ() - p_233629_1_.prevPosZ;
        float f = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 4.0F;

        if (f > 1.0F)
        {
            f = 1.0F;
        }

        p_233629_1_.limbSwingAmount += (f - p_233629_1_.limbSwingAmount) * 0.4F;
        p_233629_1_.limbSwing += p_233629_1_.limbSwingAmount;
    }

    public Vector3d func_233633_a_(Vector3d p_233633_1_, float p_233633_2_)
    {
        this.moveRelative(this.getRelevantMoveFactor(p_233633_2_), p_233633_1_);
        this.setMotion(this.handleOnClimbable(this.getMotion()));
        this.move(MoverType.SELF, this.getMotion());
        Vector3d vector3d = this.getMotion();

        if ((this.collidedHorizontally || this.isJumping) && this.isOnLadder())
        {
            vector3d = new Vector3d(vector3d.x, 0.2D, vector3d.z);
        }

        return vector3d;
    }

    public Vector3d func_233626_a_(double p_233626_1_, boolean p_233626_3_, Vector3d p_233626_4_)
    {
        if (!this.hasNoGravity() && !this.isSprinting())
        {
            double d0;

            if (p_233626_3_ && Math.abs(p_233626_4_.y - 0.005D) >= 0.003D && Math.abs(p_233626_4_.y - p_233626_1_ / 16.0D) < 0.003D)
            {
                d0 = -0.003D;
            }
            else
            {
                d0 = p_233626_4_.y - p_233626_1_ / 16.0D;
            }

            return new Vector3d(p_233626_4_.x, d0, p_233626_4_.z);
        }
        else
        {
            return p_233626_4_;
        }
    }

    private Vector3d handleOnClimbable(Vector3d p_213362_1_)
    {
        if (this.isOnLadder())
        {
            this.fallDistance = 0.0F;
            float f = 0.15F;
            double d0 = MathHelper.clamp(p_213362_1_.x, (double) - 0.15F, (double)0.15F);
            double d1 = MathHelper.clamp(p_213362_1_.z, (double) - 0.15F, (double)0.15F);
            double d2 = Math.max(p_213362_1_.y, (double) - 0.15F);

            if (d2 < 0.0D && !this.getBlockState().isIn(Blocks.SCAFFOLDING) && this.hasStoppedClimbing() && this instanceof PlayerEntity)
            {
                d2 = 0.0D;
            }

            p_213362_1_ = new Vector3d(d0, d2, d1);
        }

        return p_213362_1_;
    }

    private float getRelevantMoveFactor(float p_213335_1_)
    {
        return this.onGround ? this.getAIMoveSpeed() * (0.21600002F / (p_213335_1_ * p_213335_1_ * p_213335_1_)) : this.jumpMovementFactor;
    }

    /**
     * the movespeed used for the new AI system
     */
    public float getAIMoveSpeed()
    {
        return this.landMovementFactor;
    }

    /**
     * set the movespeed used for the new AI system
     */
    public void setAIMoveSpeed(float speedIn)
    {
        this.landMovementFactor = speedIn;
    }

    public boolean attackEntityAsMob(Entity entityIn)
    {
        this.setLastAttackedEntity(entityIn);
        return false;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        super.tick();
        this.updateActiveHand();
        this.updateSwimAnimation();

        if (!this.world.isRemote)
        {
            int i = this.getArrowCountInEntity();

            if (i > 0)
            {
                if (this.arrowHitTimer <= 0)
                {
                    this.arrowHitTimer = 20 * (30 - i);
                }

                --this.arrowHitTimer;

                if (this.arrowHitTimer <= 0)
                {
                    this.setArrowCountInEntity(i - 1);
                }
            }

            int j = this.getBeeStingCount();

            if (j > 0)
            {
                if (this.beeStingRemovalCooldown <= 0)
                {
                    this.beeStingRemovalCooldown = 20 * (30 - j);
                }

                --this.beeStingRemovalCooldown;

                if (this.beeStingRemovalCooldown <= 0)
                {
                    this.setBeeStingCount(j - 1);
                }
            }

            this.func_241353_q_();

            if (this.ticksExisted % 20 == 0)
            {
                this.getCombatTracker().reset();
            }

            if (!this.glowing)
            {
                boolean flag = this.isPotionActive(Effects.GLOWING);

                if (this.getFlag(6) != flag)
                {
                    this.setFlag(6, flag);
                }
            }

            if (this.isSleeping() && !this.isInValidBed())
            {
                this.wakeUp();
            }
        }

        this.livingTick();
        double d0 = this.getPosX() - this.prevPosX;
        double d1 = this.getPosZ() - this.prevPosZ;
        float f = (float)(d0 * d0 + d1 * d1);
        float f1 = this.renderYawOffset;
        float f2 = 0.0F;
        this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
        float f3 = 0.0F;

        if (f > 0.0025000002F)
        {
            f3 = 1.0F;
            f2 = (float)Math.sqrt((double)f) * 3.0F;
            float f4 = (float)MathHelper.atan2(d1, d0) * (180F / (float)Math.PI) - 90.0F;
            float f5 = MathHelper.abs(MathHelper.wrapDegrees(this.rotationYaw) - f4);

            if (95.0F < f5 && f5 < 265.0F)
            {
                f1 = f4 - 180.0F;
            }
            else
            {
                f1 = f4;
            }
        }

        if (this.swingProgress > 0.0F)
        {
            f1 = this.rotationYaw;
        }

        if (!this.onGround)
        {
            f3 = 0.0F;
        }

        this.onGroundSpeedFactor += (f3 - this.onGroundSpeedFactor) * 0.3F;
        this.world.getProfiler().startSection("headTurn");
        f2 = this.updateDistance(f1, f2);
        this.world.getProfiler().endSection();
        this.world.getProfiler().startSection("rangeChecks");

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F)
        {
            this.prevRenderYawOffset -= 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F)
        {
            this.prevRenderYawOffset += 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch < -180.0F)
        {
            this.prevRotationPitch -= 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead < -180.0F)
        {
            this.prevRotationYawHead -= 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead >= 180.0F)
        {
            this.prevRotationYawHead += 360.0F;
        }

        this.world.getProfiler().endSection();
        this.movedDistance += f2;

        if (this.isElytraFlying())
        {
            ++this.ticksElytraFlying;
        }
        else
        {
            this.ticksElytraFlying = 0;
        }

        if (this.isSleeping())
        {
            this.rotationPitch = 0.0F;
        }
    }

    private void func_241353_q_()
    {
        Map<EquipmentSlotType, ItemStack> map = this.func_241354_r_();

        if (map != null)
        {
            this.func_241342_a_(map);

            if (!map.isEmpty())
            {
                this.func_241344_b_(map);
            }
        }
    }

    @Nullable
    private Map<EquipmentSlotType, ItemStack> func_241354_r_()
    {
        Map<EquipmentSlotType, ItemStack> map = null;

        for (EquipmentSlotType equipmentslottype : EquipmentSlotType.values())
        {
            ItemStack itemstack;

            switch (equipmentslottype.getSlotType())
            {
                case HAND:
                    itemstack = this.getItemInHand(equipmentslottype);
                    break;

                case ARMOR:
                    itemstack = this.getArmorInSlot(equipmentslottype);
                    break;

                default:
                    continue;
            }

            ItemStack itemstack1 = this.getItemStackFromSlot(equipmentslottype);

            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack))
            {
                if (map == null)
                {
                    map = Maps.newEnumMap(EquipmentSlotType.class);
                }

                map.put(equipmentslottype, itemstack1);

                if (!itemstack.isEmpty())
                {
                    this.getAttributeManager().removeModifiers(itemstack.getAttributeModifiers(equipmentslottype));
                }

                if (!itemstack1.isEmpty())
                {
                    this.getAttributeManager().reapplyModifiers(itemstack1.getAttributeModifiers(equipmentslottype));
                }
            }
        }

        return map;
    }

    private void func_241342_a_(Map<EquipmentSlotType, ItemStack> p_241342_1_)
    {
        ItemStack itemstack = p_241342_1_.get(EquipmentSlotType.MAINHAND);
        ItemStack itemstack1 = p_241342_1_.get(EquipmentSlotType.OFFHAND);

        if (itemstack != null && itemstack1 != null && ItemStack.areItemStacksEqual(itemstack, this.getItemInHand(EquipmentSlotType.OFFHAND)) && ItemStack.areItemStacksEqual(itemstack1, this.getItemInHand(EquipmentSlotType.MAINHAND)))
        {
            ((ServerWorld)this.world).getChunkProvider().sendToAllTracking(this, new SEntityStatusPacket(this, (byte)55));
            p_241342_1_.remove(EquipmentSlotType.MAINHAND);
            p_241342_1_.remove(EquipmentSlotType.OFFHAND);
            this.setItemInHand(EquipmentSlotType.MAINHAND, itemstack.copy());
            this.setItemInHand(EquipmentSlotType.OFFHAND, itemstack1.copy());
        }
    }

    private void func_241344_b_(Map<EquipmentSlotType, ItemStack> p_241344_1_)
    {
        List<Pair<EquipmentSlotType, ItemStack>> list = Lists.newArrayListWithCapacity(p_241344_1_.size());
        p_241344_1_.forEach((p_241341_2_, p_241341_3_) ->
        {
            ItemStack itemstack = p_241341_3_.copy();
            list.add(Pair.of(p_241341_2_, itemstack));

            switch (p_241341_2_.getSlotType())
            {
                case HAND:
                    this.setItemInHand(p_241341_2_, itemstack);
                    break;

                case ARMOR:
                    this.setArmorInSlot(p_241341_2_, itemstack);
            }
        });
        ((ServerWorld)this.world).getChunkProvider().sendToAllTracking(this, new SEntityEquipmentPacket(this.getEntityId(), list));
    }

    private ItemStack getArmorInSlot(EquipmentSlotType slot)
    {
        return this.armorArray.get(slot.getIndex());
    }

    private void setArmorInSlot(EquipmentSlotType slot, ItemStack stack)
    {
        this.armorArray.set(slot.getIndex(), stack);
    }

    private ItemStack getItemInHand(EquipmentSlotType slot)
    {
        return this.handInventory.get(slot.getIndex());
    }

    private void setItemInHand(EquipmentSlotType slot, ItemStack stack)
    {
        this.handInventory.set(slot.getIndex(), stack);
    }

    protected float updateDistance(float p_110146_1_, float p_110146_2_)
    {
        float f = MathHelper.wrapDegrees(p_110146_1_ - this.renderYawOffset);
        this.renderYawOffset += f * 0.3F;
        float f1 = MathHelper.wrapDegrees(this.rotationYaw - this.renderYawOffset);
        boolean flag = f1 < -90.0F || f1 >= 90.0F;

        if (f1 < -75.0F)
        {
            f1 = -75.0F;
        }

        if (f1 >= 75.0F)
        {
            f1 = 75.0F;
        }

        this.renderYawOffset = this.rotationYaw - f1;

        if (f1 * f1 > 2500.0F)
        {
            this.renderYawOffset += f1 * 0.2F;
        }

        if (flag)
        {
            p_110146_2_ *= -1.0F;
        }

        return p_110146_2_;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void livingTick()
    {
        if (this.jumpTicks > 0)
        {
            --this.jumpTicks;
        }

        if (this.canPassengerSteer())
        {
            this.newPosRotationIncrements = 0;
            this.setPacketCoordinates(this.getPosX(), this.getPosY(), this.getPosZ());
        }

        if (this.newPosRotationIncrements > 0)
        {
            double d0 = this.getPosX() + (this.interpTargetX - this.getPosX()) / (double)this.newPosRotationIncrements;
            double d2 = this.getPosY() + (this.interpTargetY - this.getPosY()) / (double)this.newPosRotationIncrements;
            double d4 = this.getPosZ() + (this.interpTargetZ - this.getPosZ()) / (double)this.newPosRotationIncrements;
            double d6 = MathHelper.wrapDegrees(this.interpTargetYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d6 / (double)this.newPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.interpTargetPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(d0, d2, d4);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        else if (!this.isServerWorld())
        {
            this.setMotion(this.getMotion().scale(0.98D));
        }

        if (this.interpTicksHead > 0)
        {
            this.rotationYawHead = (float)((double)this.rotationYawHead + MathHelper.wrapDegrees(this.interpTargetHeadYaw - (double)this.rotationYawHead) / (double)this.interpTicksHead);
            --this.interpTicksHead;
        }

        Vector3d vector3d = this.getMotion();
        double d1 = vector3d.x;
        double d3 = vector3d.y;
        double d5 = vector3d.z;

        if (Math.abs(vector3d.x) < 0.003D)
        {
            d1 = 0.0D;
        }

        if (Math.abs(vector3d.y) < 0.003D)
        {
            d3 = 0.0D;
        }

        if (Math.abs(vector3d.z) < 0.003D)
        {
            d5 = 0.0D;
        }

        this.setMotion(d1, d3, d5);
        this.world.getProfiler().startSection("ai");

        if (this.isMovementBlocked())
        {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
        }
        else if (this.isServerWorld())
        {
            this.world.getProfiler().startSection("newAi");
            this.updateEntityActionState();
            this.world.getProfiler().endSection();
        }

        this.world.getProfiler().endSection();
        this.world.getProfiler().startSection("jump");

        if (this.isJumping && this.func_241208_cS_())
        {
            double d7;

            if (this.isInLava())
            {
                d7 = this.func_233571_b_(FluidTags.LAVA);
            }
            else
            {
                d7 = this.func_233571_b_(FluidTags.WATER);
            }

            boolean flag = this.isInWater() && d7 > 0.0D;
            double d8 = this.func_233579_cu_();

            if (!flag || this.onGround && !(d7 > d8))
            {
                if (!this.isInLava() || this.onGround && !(d7 > d8))
                {
                    if ((this.onGround || flag && d7 <= d8) && this.jumpTicks == 0)
                    {
                        this.jump();
                        this.jumpTicks = 10;
                    }
                }
                else
                {
                    this.handleFluidJump(FluidTags.LAVA);
                }
            }
            else
            {
                this.handleFluidJump(FluidTags.WATER);
            }
        }
        else
        {
            this.jumpTicks = 0;
        }

        this.world.getProfiler().endSection();
        this.world.getProfiler().startSection("travel");
        this.moveStrafing *= 0.98F;
        this.moveForward *= 0.98F;
        this.updateElytra();
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        this.travel(new Vector3d((double)this.moveStrafing, (double)this.moveVertical, (double)this.moveForward));
        this.world.getProfiler().endSection();
        this.world.getProfiler().startSection("push");

        if (this.spinAttackDuration > 0)
        {
            --this.spinAttackDuration;
            this.updateSpinAttack(axisalignedbb, this.getBoundingBox());
        }

        this.collideWithNearbyEntities();
        this.world.getProfiler().endSection();

        if (!this.world.isRemote && this.isWaterSensitive() && this.isInWaterRainOrBubbleColumn())
        {
            this.attackEntityFrom(DamageSource.DROWN, 1.0F);
        }
    }

    public boolean isWaterSensitive()
    {
        return false;
    }

    /**
     * Called each tick. Updates state for the elytra.
     */
    private void updateElytra()
    {
        boolean flag = this.getFlag(7);

        if (flag && !this.onGround && !this.isPassenger() && !this.isPotionActive(Effects.LEVITATION))
        {
            ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.CHEST);

            if (itemstack.getItem() == Items.ELYTRA && ElytraItem.isUsable(itemstack))
            {
                flag = true;

                if (!this.world.isRemote && (this.ticksElytraFlying + 1) % 20 == 0)
                {
                    itemstack.damageItem(1, this, (p_233652_0_) ->
                    {
                        p_233652_0_.sendBreakAnimation(EquipmentSlotType.CHEST);
                    });
                }
            }
            else
            {
                flag = false;
            }
        }
        else
        {
            flag = false;
        }

        if (!this.world.isRemote)
        {
            this.setFlag(7, flag);
        }
    }

    protected void updateEntityActionState()
    {
    }

    protected void collideWithNearbyEntities()
    {
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), EntityPredicates.pushableBy(this));

        if (!list.isEmpty())
        {
            int i = this.world.getGameRules().getInt(GameRules.MAX_ENTITY_CRAMMING);

            if (i > 0 && list.size() > i - 1 && this.rand.nextInt(4) == 0)
            {
                int j = 0;

                for (int k = 0; k < list.size(); ++k)
                {
                    if (!list.get(k).isPassenger())
                    {
                        ++j;
                    }
                }

                if (j > i - 1)
                {
                    this.attackEntityFrom(DamageSource.CRAMMING, 6.0F);
                }
            }

            for (int l = 0; l < list.size(); ++l)
            {
                Entity entity = list.get(l);
                this.collideWithEntity(entity);
            }
        }
    }

    protected void updateSpinAttack(AxisAlignedBB p_204801_1_, AxisAlignedBB p_204801_2_)
    {
        AxisAlignedBB axisalignedbb = p_204801_1_.union(p_204801_2_);
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, axisalignedbb);

        if (!list.isEmpty())
        {
            for (int i = 0; i < list.size(); ++i)
            {
                Entity entity = list.get(i);

                if (entity instanceof LivingEntity)
                {
                    this.spinAttack((LivingEntity)entity);
                    this.spinAttackDuration = 0;
                    this.setMotion(this.getMotion().scale(-0.2D));
                    break;
                }
            }
        }
        else if (this.collidedHorizontally)
        {
            this.spinAttackDuration = 0;
        }

        if (!this.world.isRemote && this.spinAttackDuration <= 0)
        {
            this.setLivingFlag(4, false);
        }
    }

    protected void collideWithEntity(Entity entityIn)
    {
        entityIn.applyEntityCollision(this);
    }

    protected void spinAttack(LivingEntity p_204804_1_)
    {
    }

    public void startSpinAttack(int p_204803_1_)
    {
        this.spinAttackDuration = p_204803_1_;

        if (!this.world.isRemote)
        {
            this.setLivingFlag(4, true);
        }
    }

    public boolean isSpinAttacking()
    {
        return (this.dataManager.get(LIVING_FLAGS) & 4) != 0;
    }

    /**
     * Dismounts this entity from the entity it is riding.
     */
    public void stopRiding()
    {
        Entity entity = this.getRidingEntity();
        super.stopRiding();

        if (entity != null && entity != this.getRidingEntity() && !this.world.isRemote)
        {
            this.func_233628_a_(entity);
        }
    }

    /**
     * Handles updating while riding another entity
     */
    public void updateRidden()
    {
        super.updateRidden();
        this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
        this.onGroundSpeedFactor = 0.0F;
        this.fallDistance = 0.0F;
    }

    /**
     * Sets a target for the client to interpolate towards over the next few ticks
     */
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.interpTargetX = x;
        this.interpTargetY = y;
        this.interpTargetZ = z;
        this.interpTargetYaw = (double)yaw;
        this.interpTargetPitch = (double)pitch;
        this.newPosRotationIncrements = posRotationIncrements;
    }

    public void setHeadRotation(float yaw, int pitch)
    {
        this.interpTargetHeadYaw = (double)yaw;
        this.interpTicksHead = pitch;
    }

    public void setJumping(boolean jumping)
    {
        this.isJumping = jumping;
    }

    public void triggerItemPickupTrigger(ItemEntity item)
    {
        PlayerEntity playerentity = item.getThrowerId() != null ? this.world.getPlayerByUuid(item.getThrowerId()) : null;

        if (playerentity instanceof ServerPlayerEntity)
        {
            CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_ENTITY.test((ServerPlayerEntity)playerentity, item.getItem(), this);
        }
    }

    /**
     * Called when the entity picks up an item.
     */
    public void onItemPickup(Entity entityIn, int quantity)
    {
        if (!entityIn.removed && !this.world.isRemote && (entityIn instanceof ItemEntity || entityIn instanceof AbstractArrowEntity || entityIn instanceof ExperienceOrbEntity))
        {
            ((ServerWorld)this.world).getChunkProvider().sendToAllTracking(entityIn, new SCollectItemPacket(entityIn.getEntityId(), this.getEntityId(), quantity));
        }
    }

    /**
     * returns true if the entity provided in the argument can be seen. (Raytrace)
     */
    public boolean canEntityBeSeen(Entity entityIn)
    {
        Vector3d vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());
        Vector3d vector3d1 = new Vector3d(entityIn.getPosX(), entityIn.getPosYEye(), entityIn.getPosZ());
        return this.world.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() == RayTraceResult.Type.MISS;
    }

    /**
     * Gets the current yaw of the entity
     */
    public float getYaw(float partialTicks)
    {
        return partialTicks == 1.0F ? this.rotationYawHead : MathHelper.lerp(partialTicks, this.prevRotationYawHead, this.rotationYawHead);
    }

    /**
     * Gets the progression of the swing animation, ranges from 0.0 to 1.0.
     */
    public float getSwingProgress(float partialTickTime)
    {
        float f = this.swingProgress - this.prevSwingProgress;

        if (f < 0.0F)
        {
            ++f;
        }

        return this.prevSwingProgress + f * partialTickTime;
    }

    /**
     * Returns whether the entity is in a server world
     */
    public boolean isServerWorld()
    {
        return !this.world.isRemote;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.removed;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return this.isAlive() && !this.isSpectator() && !this.isOnLadder();
    }

    /**
     * Marks this entity's velocity as changed, so that it can be re-synced with the client later
     */
    protected void markVelocityChanged()
    {
        this.velocityChanged = this.rand.nextDouble() >= this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
    }

    public float getRotationYawHead()
    {
        return this.rotationYawHead;
    }

    /**
     * Sets the head's yaw rotation of the entity.
     */
    public void setRotationYawHead(float rotation)
    {
        this.rotationYawHead = rotation;
    }

    /**
     * Set the render yaw offset
     */
    public void setRenderYawOffset(float offset)
    {
        this.renderYawOffset = offset;
    }

    protected Vector3d func_241839_a(Direction.Axis axis, TeleportationRepositioner.Result result)
    {
        return func_242288_h(super.func_241839_a(axis, result));
    }

    public static Vector3d func_242288_h(Vector3d p_242288_0_)
    {
        return new Vector3d(p_242288_0_.x, p_242288_0_.y, 0.0D);
    }

    /**
     * Returns the amount of health added by the Absorption effect.
     */
    public float getAbsorptionAmount()
    {
        return this.absorptionAmount;
    }

    public void setAbsorptionAmount(float amount)
    {
        if (amount < 0.0F)
        {
            amount = 0.0F;
        }

        this.absorptionAmount = amount;
    }

    /**
     * Sends an ENTER_COMBAT packet to the client
     */
    public void sendEnterCombat()
    {
    }

    /**
     * Sends an END_COMBAT packet to the client
     */
    public void sendEndCombat()
    {
    }

    protected void markPotionsDirty()
    {
        this.potionsNeedUpdate = true;
    }

    public abstract HandSide getPrimaryHand();

    public boolean isHandActive()
    {
        return (this.dataManager.get(LIVING_FLAGS) & 1) > 0;
    }

    public Hand getActiveHand()
    {
        return (this.dataManager.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    private void updateActiveHand()
    {
        if (this.isHandActive())
        {
            if (ItemStack.areItemsEqualIgnoreDurability(this.getHeldItem(this.getActiveHand()), this.activeItemStack))
            {
                this.activeItemStack = this.getHeldItem(this.getActiveHand());
                this.activeItemStack.onItemUsed(this.world, this, this.getItemInUseCount());

                if (this.shouldTriggerItemUseEffects())
                {
                    this.triggerItemUseEffects(this.activeItemStack, 5);
                }

                if (--this.activeItemStackUseCount == 0 && !this.world.isRemote && !this.activeItemStack.isCrossbowStack())
                {
                    this.onItemUseFinish();
                }
            }
            else
            {
                this.resetActiveHand();
            }
        }
    }

    private boolean shouldTriggerItemUseEffects()
    {
        int i = this.getItemInUseCount();
        Food food = this.activeItemStack.getItem().getFood();
        boolean flag = food != null && food.isFastEating();
        flag = flag | i <= this.activeItemStack.getUseDuration() - 7;
        return flag && i % 4 == 0;
    }

    private void updateSwimAnimation()
    {
        this.lastSwimAnimation = this.swimAnimation;

        if (this.isActualySwimming())
        {
            this.swimAnimation = Math.min(1.0F, this.swimAnimation + 0.09F);
        }
        else
        {
            this.swimAnimation = Math.max(0.0F, this.swimAnimation - 0.09F);
        }
    }

    protected void setLivingFlag(int key, boolean value)
    {
        int i = this.dataManager.get(LIVING_FLAGS);

        if (value)
        {
            i = i | key;
        }
        else
        {
            i = i & ~key;
        }

        this.dataManager.set(LIVING_FLAGS, (byte)i);
    }

    public void setActiveHand(Hand hand)
    {
        ItemStack itemstack = this.getHeldItem(hand);

        if (!itemstack.isEmpty() && !this.isHandActive())
        {
            this.activeItemStack = itemstack;
            this.activeItemStackUseCount = itemstack.getUseDuration();

            if (!this.world.isRemote)
            {
                this.setLivingFlag(1, true);
                this.setLivingFlag(2, hand == Hand.OFF_HAND);
            }
        }
    }

    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);

        if (BED_POSITION.equals(key))
        {
            if (this.world.isRemote)
            {
                this.getBedPosition().ifPresent(this::setSleepingPosition);
            }
        }
        else if (LIVING_FLAGS.equals(key) && this.world.isRemote)
        {
            if (this.isHandActive() && this.activeItemStack.isEmpty())
            {
                this.activeItemStack = this.getHeldItem(this.getActiveHand());

                if (!this.activeItemStack.isEmpty())
                {
                    this.activeItemStackUseCount = this.activeItemStack.getUseDuration();
                }
            }
            else if (!this.isHandActive() && !this.activeItemStack.isEmpty())
            {
                this.activeItemStack = ItemStack.EMPTY;
                this.activeItemStackUseCount = 0;
            }
        }
    }

    public void lookAt(EntityAnchorArgument.Type anchor, Vector3d target)
    {
        super.lookAt(anchor, target);
        this.prevRotationYawHead = this.rotationYawHead;
        this.renderYawOffset = this.rotationYawHead;
        this.prevRenderYawOffset = this.renderYawOffset;
    }

    protected void triggerItemUseEffects(ItemStack stack, int count)
    {
        if (!stack.isEmpty() && this.isHandActive())
        {
            if (stack.getUseAction() == UseAction.DRINK)
            {
                this.playSound(this.getDrinkSound(stack), 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (stack.getUseAction() == UseAction.EAT)
            {
                this.addItemParticles(stack, count);
                this.playSound(this.getEatSound(stack), 0.5F + 0.5F * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }
        }
    }

    private void addItemParticles(ItemStack stack, int count)
    {
        for (int i = 0; i < count; ++i)
        {
            Vector3d vector3d = new Vector3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            vector3d = vector3d.rotatePitch(-this.rotationPitch * ((float)Math.PI / 180F));
            vector3d = vector3d.rotateYaw(-this.rotationYaw * ((float)Math.PI / 180F));
            double d0 = (double)(-this.rand.nextFloat()) * 0.6D - 0.3D;
            Vector3d vector3d1 = new Vector3d(((double)this.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
            vector3d1 = vector3d1.rotatePitch(-this.rotationPitch * ((float)Math.PI / 180F));
            vector3d1 = vector3d1.rotateYaw(-this.rotationYaw * ((float)Math.PI / 180F));
            vector3d1 = vector3d1.add(this.getPosX(), this.getPosYEye(), this.getPosZ());
            this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, stack), vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y + 0.05D, vector3d.z);
        }
    }

    /**
     * Used for when item use count runs out, ie: eating completed
     */
    protected void onItemUseFinish()
    {
        Hand hand = this.getActiveHand();

        if (!this.activeItemStack.equals(this.getHeldItem(hand)))
        {
            this.stopActiveHand();
        }
        else
        {
            if (!this.activeItemStack.isEmpty() && this.isHandActive())
            {
                this.triggerItemUseEffects(this.activeItemStack, 16);
                ItemStack itemstack = this.activeItemStack.onItemUseFinish(this.world, this);

                if (itemstack != this.activeItemStack)
                {
                    this.setHeldItem(hand, itemstack);
                }

                this.resetActiveHand();
            }
        }
    }

    public ItemStack getActiveItemStack()
    {
        return this.activeItemStack;
    }

    public int getItemInUseCount()
    {
        return this.activeItemStackUseCount;
    }

    public int getItemInUseMaxCount()
    {
        return this.isHandActive() ? this.activeItemStack.getUseDuration() - this.getItemInUseCount() : 0;
    }

    public void stopActiveHand()
    {
        if (!this.activeItemStack.isEmpty())
        {
            this.activeItemStack.onPlayerStoppedUsing(this.world, this, this.getItemInUseCount());

            if (this.activeItemStack.isCrossbowStack())
            {
                this.updateActiveHand();
            }
        }

        this.resetActiveHand();
    }

    public void resetActiveHand()
    {
        if (!this.world.isRemote)
        {
            this.setLivingFlag(1, false);
        }

        this.activeItemStack = ItemStack.EMPTY;
        this.activeItemStackUseCount = 0;
    }

    public boolean isActiveItemStackBlocking()
    {
        if (this.isHandActive() && !this.activeItemStack.isEmpty())
        {
            Item item = this.activeItemStack.getItem();

            if (item.getUseAction(this.activeItemStack) != UseAction.BLOCK)
            {
                return false;
            }
            else
            {
                return item.getUseDuration(this.activeItemStack) - this.activeItemStackUseCount >= 5;
            }
        }
        else
        {
            return false;
        }
    }

    public boolean hasStoppedClimbing()
    {
        return this.isSneaking();
    }

    public boolean isElytraFlying()
    {
        return this.getFlag(7);
    }

    public boolean isActualySwimming()
    {
        return super.isActualySwimming() || !this.isElytraFlying() && this.getPose() == Pose.FALL_FLYING;
    }

    public int getTicksElytraFlying()
    {
        return this.ticksElytraFlying;
    }

    public boolean attemptTeleport(double x, double y, double z, boolean p_213373_7_)
    {
        double d0 = this.getPosX();
        double d1 = this.getPosY();
        double d2 = this.getPosZ();
        double d3 = y;
        boolean flag = false;
        BlockPos blockpos = new BlockPos(x, y, z);
        World world = this.world;

        if (world.isBlockLoaded(blockpos))
        {
            boolean flag1 = false;

            while (!flag1 && blockpos.getY() > 0)
            {
                BlockPos blockpos1 = blockpos.down();
                BlockState blockstate = world.getBlockState(blockpos1);

                if (blockstate.getMaterial().blocksMovement())
                {
                    flag1 = true;
                }
                else
                {
                    --d3;
                    blockpos = blockpos1;
                }
            }

            if (flag1)
            {
                this.setPositionAndUpdate(x, d3, z);

                if (world.hasNoCollisions(this) && !world.containsAnyLiquid(this.getBoundingBox()))
                {
                    flag = true;
                }
            }
        }

        if (!flag)
        {
            this.setPositionAndUpdate(d0, d1, d2);
            return false;
        }
        else
        {
            if (p_213373_7_)
            {
                world.setEntityState(this, (byte)46);
            }

            if (this instanceof CreatureEntity)
            {
                ((CreatureEntity)this).getNavigator().clearPath();
            }

            return true;
        }
    }

    /**
     * Returns false if the entity is an armor stand. Returns true for all other entity living bases.
     */
    public boolean canBeHitWithPotion()
    {
        return true;
    }

    public boolean attackable()
    {
        return true;
    }

    /**
     * Called when a record starts or stops playing. Used to make parrots start or stop partying.
     */
    public void setPartying(BlockPos pos, boolean isPartying)
    {
    }

    public boolean canPickUpItem(ItemStack itemstackIn)
    {
        return false;
    }

    public IPacket<?> createSpawnPacket()
    {
        return new SSpawnMobPacket(this);
    }

    public EntitySize getSize(Pose poseIn)
    {
        return poseIn == Pose.SLEEPING ? SLEEPING_SIZE : super.getSize(poseIn).scale(this.getRenderScale());
    }

    public ImmutableList<Pose> getAvailablePoses()
    {
        return ImmutableList.of(Pose.STANDING);
    }

    public AxisAlignedBB getPoseAABB(Pose pose)
    {
        EntitySize entitysize = this.getSize(pose);
        return new AxisAlignedBB((double)(-entitysize.width / 2.0F), 0.0D, (double)(-entitysize.width / 2.0F), (double)(entitysize.width / 2.0F), (double)entitysize.height, (double)(entitysize.width / 2.0F));
    }

    public Optional<BlockPos> getBedPosition()
    {
        return this.dataManager.get(BED_POSITION);
    }

    public void setBedPosition(BlockPos p_213369_1_)
    {
        this.dataManager.set(BED_POSITION, Optional.of(p_213369_1_));
    }

    public void clearBedPosition()
    {
        this.dataManager.set(BED_POSITION, Optional.empty());
    }

    /**
     * Returns whether player is sleeping or not
     */
    public boolean isSleeping()
    {
        return this.getBedPosition().isPresent();
    }

    public void startSleeping(BlockPos pos)
    {
        if (this.isPassenger())
        {
            this.stopRiding();
        }

        BlockState blockstate = this.world.getBlockState(pos);

        if (blockstate.getBlock() instanceof BedBlock)
        {
            this.world.setBlockState(pos, blockstate.with(BedBlock.OCCUPIED, Boolean.valueOf(true)), 3);
        }

        this.setPose(Pose.SLEEPING);
        this.setSleepingPosition(pos);
        this.setBedPosition(pos);
        this.setMotion(Vector3d.ZERO);
        this.isAirBorne = true;
    }

    /**
     * Sets entity position to a supplied BlockPos plus a little offset
     */
    private void setSleepingPosition(BlockPos p_213370_1_)
    {
        this.setPosition((double)p_213370_1_.getX() + 0.5D, (double)p_213370_1_.getY() + 0.6875D, (double)p_213370_1_.getZ() + 0.5D);
    }

    private boolean isInValidBed()
    {
        return this.getBedPosition().map((p_241350_1_) ->
        {
            return this.world.getBlockState(p_241350_1_).getBlock() instanceof BedBlock;
        }).orElse(false);
    }

    public void wakeUp()
    {
        this.getBedPosition().filter(this.world::isBlockLoaded).ifPresent((p_241348_1_) ->
        {
            BlockState blockstate = this.world.getBlockState(p_241348_1_);

            if (blockstate.getBlock() instanceof BedBlock)
            {
                this.world.setBlockState(p_241348_1_, blockstate.with(BedBlock.OCCUPIED, Boolean.valueOf(false)), 3);
                Vector3d vector3d1 = BedBlock.func_242652_a(this.getType(), this.world, p_241348_1_, this.rotationYaw).orElseGet(() ->
                {
                    BlockPos blockpos = p_241348_1_.up();
                    return new Vector3d((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.1D, (double)blockpos.getZ() + 0.5D);
                });
                Vector3d vector3d2 = Vector3d.copyCenteredHorizontally(p_241348_1_).subtract(vector3d1).normalize();
                float f = (float)MathHelper.wrapDegrees(MathHelper.atan2(vector3d2.z, vector3d2.x) * (double)(180F / (float)Math.PI) - 90.0D);
                this.setPosition(vector3d1.x, vector3d1.y, vector3d1.z);
                this.rotationYaw = f;
                this.rotationPitch = 0.0F;
            }
        });
        Vector3d vector3d = this.getPositionVec();
        this.setPose(Pose.STANDING);
        this.setPosition(vector3d.x, vector3d.y, vector3d.z);
        this.clearBedPosition();
    }

    @Nullable

    /**
     * gets the Direction for the camera if this entity is sleeping
     */
    public Direction getBedDirection()
    {
        BlockPos blockpos = this.getBedPosition().orElse((BlockPos)null);
        return blockpos != null ? BedBlock.getBedDirection(this.world, blockpos) : null;
    }

    /**
     * Checks if this entity is inside of an opaque block
     */
    public boolean isEntityInsideOpaqueBlock()
    {
        return !this.isSleeping() && super.isEntityInsideOpaqueBlock();
    }

    protected final float getEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return poseIn == Pose.SLEEPING ? 0.2F : this.getStandingEyeHeight(poseIn, sizeIn);
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return super.getEyeHeight(poseIn, sizeIn);
    }

    public ItemStack findAmmo(ItemStack shootable)
    {
        return ItemStack.EMPTY;
    }

    public ItemStack onFoodEaten(World p_213357_1_, ItemStack p_213357_2_)
    {
        if (p_213357_2_.isFood())
        {
            p_213357_1_.playSound((PlayerEntity)null, this.getPosX(), this.getPosY(), this.getPosZ(), this.getEatSound(p_213357_2_), SoundCategory.NEUTRAL, 1.0F, 1.0F + (p_213357_1_.rand.nextFloat() - p_213357_1_.rand.nextFloat()) * 0.4F);
            this.applyFoodEffects(p_213357_2_, p_213357_1_, this);

            if (!(this instanceof PlayerEntity) || !((PlayerEntity)this).abilities.isCreativeMode)
            {
                p_213357_2_.shrink(1);
            }
        }

        return p_213357_2_;
    }

    private void applyFoodEffects(ItemStack p_213349_1_, World p_213349_2_, LivingEntity p_213349_3_)
    {
        Item item = p_213349_1_.getItem();

        if (item.isFood())
        {
            for (Pair<EffectInstance, Float> pair : item.getFood().getEffects())
            {
                if (!p_213349_2_.isRemote && pair.getFirst() != null && p_213349_2_.rand.nextFloat() < pair.getSecond())
                {
                    p_213349_3_.addPotionEffect(new EffectInstance(pair.getFirst()));
                }
            }
        }
    }

    private static byte equipmentSlotToEntityState(EquipmentSlotType p_213350_0_)
    {
        switch (p_213350_0_)
        {
            case MAINHAND:
                return 47;

            case OFFHAND:
                return 48;

            case HEAD:
                return 49;

            case CHEST:
                return 50;

            case FEET:
                return 52;

            case LEGS:
                return 51;

            default:
                return 47;
        }
    }

    public void sendBreakAnimation(EquipmentSlotType p_213361_1_)
    {
        this.world.setEntityState(this, equipmentSlotToEntityState(p_213361_1_));
    }

    public void sendBreakAnimation(Hand p_213334_1_)
    {
        this.sendBreakAnimation(p_213334_1_ == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND);
    }

    /**
     * Gets the bounding box of this Entity, adjusted to take auxiliary entities into account (e.g. the tile contained
     * by a minecart, such as a command block).
     */
    public AxisAlignedBB getRenderBoundingBox()
    {
        if (this.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == Items.DRAGON_HEAD)
        {
            float f = 0.5F;
            return this.getBoundingBox().grow(0.5D, 0.5D, 0.5D);
        }
        else
        {
            return super.getRenderBoundingBox();
        }
    }
}
