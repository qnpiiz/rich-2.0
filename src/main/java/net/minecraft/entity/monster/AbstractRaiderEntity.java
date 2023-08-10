package net.minecraft.entity.monster;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveTowardsRaidGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractRaiderEntity extends PatrollerEntity
{
    protected static final DataParameter<Boolean> CELEBRATING = EntityDataManager.createKey(AbstractRaiderEntity.class, DataSerializers.BOOLEAN);
    private static final Predicate<ItemEntity> bannerPredicate = (banner) ->
    {
        return !banner.cannotPickup() && banner.isAlive() && ItemStack.areItemStacksEqual(banner.getItem(), Raid.createIllagerBanner());
    };
    @Nullable
    protected Raid raid;
    private int wave;
    private boolean canJoinRaid;
    private int joinDelay;

    protected AbstractRaiderEntity(EntityType <? extends AbstractRaiderEntity > type, World worldIn)
    {
        super(type, worldIn);
    }

    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AbstractRaiderEntity.PromoteLeaderGoal<>(this));
        this.goalSelector.addGoal(3, new MoveTowardsRaidGoal<>(this));
        this.goalSelector.addGoal(4, new AbstractRaiderEntity.InvadeHomeGoal(this, (double)1.05F, 1));
        this.goalSelector.addGoal(5, new AbstractRaiderEntity.CelebrateRaidLossGoal(this));
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(CELEBRATING, false);
    }

    public abstract void applyWaveBonus(int wave, boolean p_213660_2_);

    public boolean canJoinRaid()
    {
        return this.canJoinRaid;
    }

    public void setCanJoinRaid(boolean canJoin)
    {
        this.canJoinRaid = canJoin;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void livingTick()
    {
        if (this.world instanceof ServerWorld && this.isAlive())
        {
            Raid raid = this.getRaid();

            if (this.canJoinRaid())
            {
                if (raid == null)
                {
                    if (this.world.getGameTime() % 20L == 0L)
                    {
                        Raid raid1 = ((ServerWorld)this.world).findRaid(this.getPosition());

                        if (raid1 != null && RaidManager.canJoinRaid(this, raid1))
                        {
                            raid1.joinRaid(raid1.getGroupsSpawned(), this, (BlockPos)null, true);
                        }
                    }
                }
                else
                {
                    LivingEntity livingentity = this.getAttackTarget();

                    if (livingentity != null && (livingentity.getType() == EntityType.PLAYER || livingentity.getType() == EntityType.IRON_GOLEM))
                    {
                        this.idleTime = 0;
                    }
                }
            }
        }

        super.livingTick();
    }

    protected void idle()
    {
        this.idleTime += 2;
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource cause)
    {
        if (this.world instanceof ServerWorld)
        {
            Entity entity = cause.getTrueSource();
            Raid raid = this.getRaid();

            if (raid != null)
            {
                if (this.isLeader())
                {
                    raid.removeLeader(this.getWave());
                }

                if (entity != null && entity.getType() == EntityType.PLAYER)
                {
                    raid.addHero(entity);
                }

                raid.leaveRaid(this, false);
            }

            if (this.isLeader() && raid == null && ((ServerWorld)this.world).findRaid(this.getPosition()) == null)
            {
                ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.HEAD);
                PlayerEntity playerentity = null;

                if (entity instanceof PlayerEntity)
                {
                    playerentity = (PlayerEntity)entity;
                }
                else if (entity instanceof WolfEntity)
                {
                    WolfEntity wolfentity = (WolfEntity)entity;
                    LivingEntity livingentity = wolfentity.getOwner();

                    if (wolfentity.isTamed() && livingentity instanceof PlayerEntity)
                    {
                        playerentity = (PlayerEntity)livingentity;
                    }
                }

                if (!itemstack.isEmpty() && ItemStack.areItemStacksEqual(itemstack, Raid.createIllagerBanner()) && playerentity != null)
                {
                    EffectInstance effectinstance1 = playerentity.getActivePotionEffect(Effects.BAD_OMEN);
                    int i = 1;

                    if (effectinstance1 != null)
                    {
                        i += effectinstance1.getAmplifier();
                        playerentity.removeActivePotionEffect(Effects.BAD_OMEN);
                    }
                    else
                    {
                        --i;
                    }

                    i = MathHelper.clamp(i, 0, 4);
                    EffectInstance effectinstance = new EffectInstance(Effects.BAD_OMEN, 120000, i, false, false, true);

                    if (!this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS))
                    {
                        playerentity.addPotionEffect(effectinstance);
                    }
                }
            }
        }

        super.onDeath(cause);
    }

    public boolean notInRaid()
    {
        return !this.isRaidActive();
    }

    public void setRaid(@Nullable Raid raid)
    {
        this.raid = raid;
    }

    @Nullable
    public Raid getRaid()
    {
        return this.raid;
    }

    public boolean isRaidActive()
    {
        return this.getRaid() != null && this.getRaid().isActive();
    }

    public void setWave(int wave)
    {
        this.wave = wave;
    }

    public int getWave()
    {
        return this.wave;
    }

    public boolean getCelebrating()
    {
        return this.dataManager.get(CELEBRATING);
    }

    public void setCelebrating(boolean celebrate)
    {
        this.dataManager.set(CELEBRATING, celebrate);
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putInt("Wave", this.wave);
        compound.putBoolean("CanJoinRaid", this.canJoinRaid);

        if (this.raid != null)
        {
            compound.putInt("RaidId", this.raid.getId());
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.wave = compound.getInt("Wave");
        this.canJoinRaid = compound.getBoolean("CanJoinRaid");

        if (compound.contains("RaidId", 3))
        {
            if (this.world instanceof ServerWorld)
            {
                this.raid = ((ServerWorld)this.world).getRaids().get(compound.getInt("RaidId"));
            }

            if (this.raid != null)
            {
                this.raid.joinRaid(this.wave, this, false);

                if (this.isLeader())
                {
                    this.raid.setLeader(this.wave, this);
                }
            }
        }
    }

    /**
     * Tests if this entity should pickup a weapon or an armor. Entity drops current weapon or armor if the new one is
     * better.
     */
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity)
    {
        ItemStack itemstack = itemEntity.getItem();
        boolean flag = this.isRaidActive() && this.getRaid().getLeader(this.getWave()) != null;

        if (this.isRaidActive() && !flag && ItemStack.areItemStacksEqual(itemstack, Raid.createIllagerBanner()))
        {
            EquipmentSlotType equipmentslottype = EquipmentSlotType.HEAD;
            ItemStack itemstack1 = this.getItemStackFromSlot(equipmentslottype);
            double d0 = (double)this.getDropChance(equipmentslottype);

            if (!itemstack1.isEmpty() && (double)Math.max(this.rand.nextFloat() - 0.1F, 0.0F) < d0)
            {
                this.entityDropItem(itemstack1);
            }

            this.triggerItemPickupTrigger(itemEntity);
            this.setItemStackToSlot(equipmentslottype, itemstack);
            this.onItemPickup(itemEntity, itemstack.getCount());
            itemEntity.remove();
            this.getRaid().setLeader(this.getWave(), this);
            this.setLeader(true);
        }
        else
        {
            super.updateEquipmentIfNeeded(itemEntity);
        }
    }

    public boolean canDespawn(double distanceToClosestPlayer)
    {
        return this.getRaid() == null ? super.canDespawn(distanceToClosestPlayer) : false;
    }

    public boolean preventDespawn()
    {
        return super.preventDespawn() || this.getRaid() != null;
    }

    public int getJoinDelay()
    {
        return this.joinDelay;
    }

    public void setJoinDelay(int delay)
    {
        this.joinDelay = delay;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isRaidActive())
        {
            this.getRaid().updateBarPercentage();
        }

        return super.attackEntityFrom(source, amount);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        this.setCanJoinRaid(this.getType() != EntityType.WITCH || reason != SpawnReason.NATURAL);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public abstract SoundEvent getRaidLossSound();

    public class CelebrateRaidLossGoal extends Goal
    {
        private final AbstractRaiderEntity raiderEntity;

        CelebrateRaidLossGoal(AbstractRaiderEntity raiderEntity)
        {
            this.raiderEntity = raiderEntity;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean shouldExecute()
        {
            Raid raid = this.raiderEntity.getRaid();
            return this.raiderEntity.isAlive() && this.raiderEntity.getAttackTarget() == null && raid != null && raid.isLoss();
        }

        public void startExecuting()
        {
            this.raiderEntity.setCelebrating(true);
            super.startExecuting();
        }

        public void resetTask()
        {
            this.raiderEntity.setCelebrating(false);
            super.resetTask();
        }

        public void tick()
        {
            if (!this.raiderEntity.isSilent() && this.raiderEntity.rand.nextInt(100) == 0)
            {
                AbstractRaiderEntity.this.playSound(AbstractRaiderEntity.this.getRaidLossSound(), AbstractRaiderEntity.this.getSoundVolume(), AbstractRaiderEntity.this.getSoundPitch());
            }

            if (!this.raiderEntity.isPassenger() && this.raiderEntity.rand.nextInt(50) == 0)
            {
                this.raiderEntity.getJumpController().setJumping();
            }

            super.tick();
        }
    }

    public class FindTargetGoal extends Goal
    {
        private final AbstractRaiderEntity raiderEntity;
        private final float findTargetRange;
        public final EntityPredicate findTargetPredicate = (new EntityPredicate()).setDistance(8.0D).setSkipAttackChecks().allowInvulnerable().allowFriendlyFire().setLineOfSiteRequired().setUseInvisibilityCheck();

        public FindTargetGoal(AbstractIllagerEntity raiderEntity, float range)
        {
            this.raiderEntity = raiderEntity;
            this.findTargetRange = range * range;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean shouldExecute()
        {
            LivingEntity livingentity = this.raiderEntity.getRevengeTarget();
            return this.raiderEntity.getRaid() == null && this.raiderEntity.isPatrolling() && this.raiderEntity.getAttackTarget() != null && !this.raiderEntity.isAggressive() && (livingentity == null || livingentity.getType() != EntityType.PLAYER);
        }

        public void startExecuting()
        {
            super.startExecuting();
            this.raiderEntity.getNavigator().clearPath();

            for (AbstractRaiderEntity abstractraiderentity : this.raiderEntity.world.getTargettableEntitiesWithinAABB(AbstractRaiderEntity.class, this.findTargetPredicate, this.raiderEntity, this.raiderEntity.getBoundingBox().grow(8.0D, 8.0D, 8.0D)))
            {
                abstractraiderentity.setAttackTarget(this.raiderEntity.getAttackTarget());
            }
        }

        public void resetTask()
        {
            super.resetTask();
            LivingEntity livingentity = this.raiderEntity.getAttackTarget();

            if (livingentity != null)
            {
                for (AbstractRaiderEntity abstractraiderentity : this.raiderEntity.world.getTargettableEntitiesWithinAABB(AbstractRaiderEntity.class, this.findTargetPredicate, this.raiderEntity, this.raiderEntity.getBoundingBox().grow(8.0D, 8.0D, 8.0D)))
                {
                    abstractraiderentity.setAttackTarget(livingentity);
                    abstractraiderentity.setAggroed(true);
                }

                this.raiderEntity.setAggroed(true);
            }
        }

        public void tick()
        {
            LivingEntity livingentity = this.raiderEntity.getAttackTarget();

            if (livingentity != null)
            {
                if (this.raiderEntity.getDistanceSq(livingentity) > (double)this.findTargetRange)
                {
                    this.raiderEntity.getLookController().setLookPositionWithEntity(livingentity, 30.0F, 30.0F);

                    if (this.raiderEntity.rand.nextInt(50) == 0)
                    {
                        this.raiderEntity.playAmbientSound();
                    }
                }
                else
                {
                    this.raiderEntity.setAggroed(true);
                }

                super.tick();
            }
        }
    }

    static class InvadeHomeGoal extends Goal
    {
        private final AbstractRaiderEntity raiderEntity;
        private final double speed;
        private BlockPos blockPosPOI;
        private final List<BlockPos> cachedPointOfIntresste = Lists.newArrayList();
        private final int distance;
        private boolean idling;

        public InvadeHomeGoal(AbstractRaiderEntity raiderEntity, double speed, int distance)
        {
            this.raiderEntity = raiderEntity;
            this.speed = speed;
            this.distance = distance;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean shouldExecute()
        {
            this.clearLastCachedPoint();
            return this.isActive() && this.findValidHome() && this.raiderEntity.getAttackTarget() == null;
        }

        private boolean isActive()
        {
            return this.raiderEntity.isRaidActive() && !this.raiderEntity.getRaid().isOver();
        }

        private boolean findValidHome()
        {
            ServerWorld serverworld = (ServerWorld)this.raiderEntity.world;
            BlockPos blockpos = this.raiderEntity.getPosition();
            Optional<BlockPos> optional = serverworld.getPointOfInterestManager().getRandom((poiType) ->
            {
                return poiType == PointOfInterestType.HOME;
            }, this::isValidDoorPosition, PointOfInterestManager.Status.ANY, blockpos, 48, this.raiderEntity.rand);

            if (!optional.isPresent())
            {
                return false;
            }
            else
            {
                this.blockPosPOI = optional.get().toImmutable();
                return true;
            }
        }

        public boolean shouldContinueExecuting()
        {
            if (this.raiderEntity.getNavigator().noPath())
            {
                return false;
            }
            else
            {
                return this.raiderEntity.getAttackTarget() == null && !this.blockPosPOI.withinDistance(this.raiderEntity.getPositionVec(), (double)(this.raiderEntity.getWidth() + (float)this.distance)) && !this.idling;
            }
        }

        public void resetTask()
        {
            if (this.blockPosPOI.withinDistance(this.raiderEntity.getPositionVec(), (double)this.distance))
            {
                this.cachedPointOfIntresste.add(this.blockPosPOI);
            }
        }

        public void startExecuting()
        {
            super.startExecuting();
            this.raiderEntity.setIdleTime(0);
            this.raiderEntity.getNavigator().tryMoveToXYZ((double)this.blockPosPOI.getX(), (double)this.blockPosPOI.getY(), (double)this.blockPosPOI.getZ(), this.speed);
            this.idling = false;
        }

        public void tick()
        {
            if (this.raiderEntity.getNavigator().noPath())
            {
                Vector3d vector3d = Vector3d.copyCenteredHorizontally(this.blockPosPOI);
                Vector3d vector3d1 = RandomPositionGenerator.findRandomTargetTowardsScaled(this.raiderEntity, 16, 7, vector3d, (double)((float)Math.PI / 10F));

                if (vector3d1 == null)
                {
                    vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.raiderEntity, 8, 7, vector3d);
                }

                if (vector3d1 == null)
                {
                    this.idling = true;
                    return;
                }

                this.raiderEntity.getNavigator().tryMoveToXYZ(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
            }
        }

        private boolean isValidDoorPosition(BlockPos pos)
        {
            for (BlockPos blockpos : this.cachedPointOfIntresste)
            {
                if (Objects.equals(pos, blockpos))
                {
                    return false;
                }
            }

            return true;
        }

        private void clearLastCachedPoint()
        {
            if (this.cachedPointOfIntresste.size() > 2)
            {
                this.cachedPointOfIntresste.remove(0);
            }
        }
    }

    public class PromoteLeaderGoal<T extends AbstractRaiderEntity> extends Goal
    {
        private final T raiderEntity;

        public PromoteLeaderGoal(T raiderEntity)
        {
            this.raiderEntity = raiderEntity;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean shouldExecute()
        {
            Raid raid = this.raiderEntity.getRaid();

            if (this.raiderEntity.isRaidActive() && !this.raiderEntity.getRaid().isOver() && this.raiderEntity.canBeLeader() && !ItemStack.areItemStacksEqual(this.raiderEntity.getItemStackFromSlot(EquipmentSlotType.HEAD), Raid.createIllagerBanner()))
            {
                AbstractRaiderEntity abstractraiderentity = raid.getLeader(this.raiderEntity.getWave());

                if (abstractraiderentity == null || !abstractraiderentity.isAlive())
                {
                    List<ItemEntity> list = this.raiderEntity.world.getEntitiesWithinAABB(ItemEntity.class, this.raiderEntity.getBoundingBox().grow(16.0D, 8.0D, 16.0D), AbstractRaiderEntity.bannerPredicate);

                    if (!list.isEmpty())
                    {
                        return this.raiderEntity.getNavigator().tryMoveToEntityLiving(list.get(0), (double)1.15F);
                    }
                }

                return false;
            }
            else
            {
                return false;
            }
        }

        public void tick()
        {
            if (this.raiderEntity.getNavigator().getTargetPos().withinDistance(this.raiderEntity.getPositionVec(), 1.414D))
            {
                List<ItemEntity> list = this.raiderEntity.world.getEntitiesWithinAABB(ItemEntity.class, this.raiderEntity.getBoundingBox().grow(4.0D, 4.0D, 4.0D), AbstractRaiderEntity.bannerPredicate);

                if (!list.isEmpty())
                {
                    this.raiderEntity.updateEquipmentIfNeeded(list.get(0));
                }
            }
        }
    }
}
