package net.minecraft.entity.merchant.villager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.VillagerTasks;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.IReputationTracking;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.GossipManager;
import net.minecraft.village.GossipType;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class VillagerEntity extends AbstractVillagerEntity implements IReputationTracking, IVillagerDataHolder
{
    private static final DataParameter<VillagerData> VILLAGER_DATA = EntityDataManager.createKey(VillagerEntity.class, DataSerializers.VILLAGER_DATA);
    public static final Map<Item, Integer> FOOD_VALUES = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
    private static final Set<Item> ALLOWED_INVENTORY_ITEMS = ImmutableSet.of(Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, Items.BEETROOT_SEEDS);
    private int timeUntilReset;
    private boolean leveledUp;
    @Nullable
    private PlayerEntity previousCustomer;
    private byte foodLevel;
    private final GossipManager gossip = new GossipManager();
    private long lastGossipTime;
    private long lastGossipDecay;
    private int xp;
    private long lastRestock;
    private int restocksToday;
    private long lastRestockDayTime;
    private boolean assignProfessionWhenSpawned;
    private static final ImmutableList < MemoryModuleType<? >> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.OPENED_DOORS, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY);
    private static final ImmutableList < SensorType <? extends Sensor <? super VillagerEntity >>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_DETECTED);
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<VillagerEntity, PointOfInterestType>> JOB_SITE_PREDICATE_MAP = ImmutableMap.of(MemoryModuleType.HOME, (villager, poiType) ->
    {
        return poiType == PointOfInterestType.HOME;
    }, MemoryModuleType.JOB_SITE, (villager, poiType) ->
    {
        return villager.getVillagerData().getProfession().getPointOfInterest() == poiType;
    }, MemoryModuleType.POTENTIAL_JOB_SITE, (villager, poiType) ->
    {
        return PointOfInterestType.ANY_VILLAGER_WORKSTATION.test(poiType);
    }, MemoryModuleType.MEETING_POINT, (villager, poiType) ->
    {
        return poiType == PointOfInterestType.MEETING;
    });

    public VillagerEntity(EntityType <? extends VillagerEntity > type, World worldIn)
    {
        this(type, worldIn, VillagerType.PLAINS);
    }

    public VillagerEntity(EntityType <? extends VillagerEntity > type, World worldIn, VillagerType villagerType)
    {
        super(type, worldIn);
        ((GroundPathNavigator)this.getNavigator()).setBreakDoors(true);
        this.getNavigator().setCanSwim(true);
        this.setCanPickUpLoot(true);
        this.setVillagerData(this.getVillagerData().withType(villagerType).withProfession(VillagerProfession.NONE));
    }

    public Brain<VillagerEntity> getBrain()
    {
        return (Brain<VillagerEntity>)super.getBrain();
    }

    protected Brain.BrainCodec<VillagerEntity> getBrainCodec()
    {
        return Brain.createCodec(MEMORY_TYPES, SENSOR_TYPES);
    }

    protected Brain<?> createBrain(Dynamic<?> dynamicIn)
    {
        Brain<VillagerEntity> brain = this.getBrainCodec().deserialize(dynamicIn);
        this.initBrain(brain);
        return brain;
    }

    public void resetBrain(ServerWorld serverWorldIn)
    {
        Brain<VillagerEntity> brain = this.getBrain();
        brain.stopAllTasks(serverWorldIn, this);
        this.brain = brain.copy();
        this.initBrain(this.getBrain());
    }

    private void initBrain(Brain<VillagerEntity> villagerBrain)
    {
        VillagerProfession villagerprofession = this.getVillagerData().getProfession();

        if (this.isChild())
        {
            villagerBrain.setSchedule(Schedule.VILLAGER_BABY);
            villagerBrain.registerActivity(Activity.PLAY, VillagerTasks.play(0.5F));
        }
        else
        {
            villagerBrain.setSchedule(Schedule.VILLAGER_DEFAULT);
            villagerBrain.registerActivity(Activity.WORK, VillagerTasks.work(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT)));
        }

        villagerBrain.registerActivity(Activity.CORE, VillagerTasks.core(villagerprofession, 0.5F));
        villagerBrain.registerActivity(Activity.MEET, VillagerTasks.meet(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleStatus.VALUE_PRESENT)));
        villagerBrain.registerActivity(Activity.REST, VillagerTasks.rest(villagerprofession, 0.5F));
        villagerBrain.registerActivity(Activity.IDLE, VillagerTasks.idle(villagerprofession, 0.5F));
        villagerBrain.registerActivity(Activity.PANIC, VillagerTasks.panic(villagerprofession, 0.5F));
        villagerBrain.registerActivity(Activity.PRE_RAID, VillagerTasks.preRaid(villagerprofession, 0.5F));
        villagerBrain.registerActivity(Activity.RAID, VillagerTasks.raid(villagerprofession, 0.5F));
        villagerBrain.registerActivity(Activity.HIDE, VillagerTasks.hide(villagerprofession, 0.5F));
        villagerBrain.setDefaultActivities(ImmutableSet.of(Activity.CORE));
        villagerBrain.setFallbackActivity(Activity.IDLE);
        villagerBrain.switchTo(Activity.IDLE);
        villagerBrain.updateActivity(this.world.getDayTime(), this.world.getGameTime());
    }

    /**
     * This is called when Entity's growing age timer reaches 0 (negative values are considered as a child, positive as
     * an adult)
     */
    protected void onGrowingAdult()
    {
        super.onGrowingAdult();

        if (this.world instanceof ServerWorld)
        {
            this.resetBrain((ServerWorld)this.world);
        }
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes()
    {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.5D).createMutableAttribute(Attributes.FOLLOW_RANGE, 48.0D);
    }

    public boolean shouldAssignProfessionOnSpawn()
    {
        return this.assignProfessionWhenSpawned;
    }

    protected void updateAITasks()
    {
        this.world.getProfiler().startSection("villagerBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().endSection();

        if (this.assignProfessionWhenSpawned)
        {
            this.assignProfessionWhenSpawned = false;
        }

        if (!this.hasCustomer() && this.timeUntilReset > 0)
        {
            --this.timeUntilReset;

            if (this.timeUntilReset <= 0)
            {
                if (this.leveledUp)
                {
                    this.levelUp();
                    this.leveledUp = false;
                }

                this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 200, 0));
            }
        }

        if (this.previousCustomer != null && this.world instanceof ServerWorld)
        {
            ((ServerWorld)this.world).updateReputation(IReputationType.TRADE, this.previousCustomer, this);
            this.world.setEntityState(this, (byte)14);
            this.previousCustomer = null;
        }

        if (!this.isAIDisabled() && this.rand.nextInt(100) == 0)
        {
            Raid raid = ((ServerWorld)this.world).findRaid(this.getPosition());

            if (raid != null && raid.isActive() && !raid.isOver())
            {
                this.world.setEntityState(this, (byte)42);
            }
        }

        if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.hasCustomer())
        {
            this.resetCustomer();
        }

        super.updateAITasks();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        super.tick();

        if (this.getShakeHeadTicks() > 0)
        {
            this.setShakeHeadTicks(this.getShakeHeadTicks() - 1);
        }

        this.tickGossip();
    }

    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_)
    {
        ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);

        if (itemstack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.hasCustomer() && !this.isSleeping())
        {
            if (this.isChild())
            {
                this.shakeHead();
                return ActionResultType.func_233537_a_(this.world.isRemote);
            }
            else
            {
                boolean flag = this.getOffers().isEmpty();

                if (p_230254_2_ == Hand.MAIN_HAND)
                {
                    if (flag && !this.world.isRemote)
                    {
                        this.shakeHead();
                    }

                    p_230254_1_.addStat(Stats.TALKED_TO_VILLAGER);
                }

                if (flag)
                {
                    return ActionResultType.func_233537_a_(this.world.isRemote);
                }
                else
                {
                    if (!this.world.isRemote && !this.offers.isEmpty())
                    {
                        this.displayMerchantGui(p_230254_1_);
                    }

                    return ActionResultType.func_233537_a_(this.world.isRemote);
                }
            }
        }
        else
        {
            return super.func_230254_b_(p_230254_1_, p_230254_2_);
        }
    }

    private void shakeHead()
    {
        this.setShakeHeadTicks(40);

        if (!this.world.isRemote())
        {
            this.playSound(SoundEvents.ENTITY_VILLAGER_NO, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    private void displayMerchantGui(PlayerEntity player)
    {
        this.recalculateSpecialPricesFor(player);
        this.setCustomer(player);
        this.openMerchantContainer(player, this.getDisplayName(), this.getVillagerData().getLevel());
    }

    public void setCustomer(@Nullable PlayerEntity player)
    {
        boolean flag = this.getCustomer() != null && player == null;
        super.setCustomer(player);

        if (flag)
        {
            this.resetCustomer();
        }
    }

    protected void resetCustomer()
    {
        super.resetCustomer();
        this.resetAllSpecialPrices();
    }

    private void resetAllSpecialPrices()
    {
        for (MerchantOffer merchantoffer : this.getOffers())
        {
            merchantoffer.resetSpecialPrice();
        }
    }

    public boolean canRestockTrades()
    {
        return true;
    }

    public void restock()
    {
        this.calculateDemandOfOffers();

        for (MerchantOffer merchantoffer : this.getOffers())
        {
            merchantoffer.resetUses();
        }

        this.lastRestock = this.world.getGameTime();
        ++this.restocksToday;
    }

    private boolean hasUsedOffer()
    {
        for (MerchantOffer merchantoffer : this.getOffers())
        {
            if (merchantoffer.hasBeenUsed())
            {
                return true;
            }
        }

        return false;
    }

    private boolean canRestock()
    {
        return this.restocksToday == 0 || this.restocksToday < 2 && this.world.getGameTime() > this.lastRestock + 2400L;
    }

    public boolean canResetStock()
    {
        long i = this.lastRestock + 12000L;
        long j = this.world.getGameTime();
        boolean flag = j > i;
        long k = this.world.getDayTime();

        if (this.lastRestockDayTime > 0L)
        {
            long l = this.lastRestockDayTime / 24000L;
            long i1 = k / 24000L;
            flag |= i1 > l;
        }

        this.lastRestockDayTime = k;

        if (flag)
        {
            this.lastRestock = j;
            this.func_223718_eH();
        }

        return this.canRestock() && this.hasUsedOffer();
    }

    private void resetOffersAndAdjustForDemand()
    {
        int i = 2 - this.restocksToday;

        if (i > 0)
        {
            for (MerchantOffer merchantoffer : this.getOffers())
            {
                merchantoffer.resetUses();
            }
        }

        for (int j = 0; j < i; ++j)
        {
            this.calculateDemandOfOffers();
        }
    }

    private void calculateDemandOfOffers()
    {
        for (MerchantOffer merchantoffer : this.getOffers())
        {
            merchantoffer.calculateDemand();
        }
    }

    private void recalculateSpecialPricesFor(PlayerEntity playerIn)
    {
        int i = this.getPlayerReputation(playerIn);

        if (i != 0)
        {
            for (MerchantOffer merchantoffer : this.getOffers())
            {
                merchantoffer.increaseSpecialPrice(-MathHelper.floor((float)i * merchantoffer.getPriceMultiplier()));
            }
        }

        if (playerIn.isPotionActive(Effects.HERO_OF_THE_VILLAGE))
        {
            EffectInstance effectinstance = playerIn.getActivePotionEffect(Effects.HERO_OF_THE_VILLAGE);
            int k = effectinstance.getAmplifier();

            for (MerchantOffer merchantoffer1 : this.getOffers())
            {
                double d0 = 0.3D + 0.0625D * (double)k;
                int j = (int)Math.floor(d0 * (double)merchantoffer1.getBuyingStackFirst().getCount());
                merchantoffer1.increaseSpecialPrice(-Math.max(j, 1));
            }
        }
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        VillagerData.CODEC.encodeStart(NBTDynamicOps.INSTANCE, this.getVillagerData()).resultOrPartial(LOGGER::error).ifPresent((data) ->
        {
            compound.put("VillagerData", data);
        });
        compound.putByte("FoodLevel", this.foodLevel);
        compound.put("Gossips", this.gossip.write(NBTDynamicOps.INSTANCE).getValue());
        compound.putInt("Xp", this.xp);
        compound.putLong("LastRestock", this.lastRestock);
        compound.putLong("LastGossipDecay", this.lastGossipDecay);
        compound.putInt("RestocksToday", this.restocksToday);

        if (this.assignProfessionWhenSpawned)
        {
            compound.putBoolean("AssignProfessionWhenSpawned", true);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);

        if (compound.contains("VillagerData", 10))
        {
            DataResult<VillagerData> dataresult = VillagerData.CODEC.parse(new Dynamic<>(NBTDynamicOps.INSTANCE, compound.get("VillagerData")));
            dataresult.resultOrPartial(LOGGER::error).ifPresent(this::setVillagerData);
        }

        if (compound.contains("Offers", 10))
        {
            this.offers = new MerchantOffers(compound.getCompound("Offers"));
        }

        if (compound.contains("FoodLevel", 1))
        {
            this.foodLevel = compound.getByte("FoodLevel");
        }

        ListNBT listnbt = compound.getList("Gossips", 10);
        this.gossip.read(new Dynamic<>(NBTDynamicOps.INSTANCE, listnbt));

        if (compound.contains("Xp", 3))
        {
            this.xp = compound.getInt("Xp");
        }

        this.lastRestock = compound.getLong("LastRestock");
        this.lastGossipDecay = compound.getLong("LastGossipDecay");
        this.setCanPickUpLoot(true);

        if (this.world instanceof ServerWorld)
        {
            this.resetBrain((ServerWorld)this.world);
        }

        this.restocksToday = compound.getInt("RestocksToday");

        if (compound.contains("AssignProfessionWhenSpawned"))
        {
            this.assignProfessionWhenSpawned = compound.getBoolean("AssignProfessionWhenSpawned");
        }
    }

    public boolean canDespawn(double distanceToClosestPlayer)
    {
        return false;
    }

    @Nullable
    protected SoundEvent getAmbientSound()
    {
        if (this.isSleeping())
        {
            return null;
        }
        else
        {
            return this.hasCustomer() ? SoundEvents.ENTITY_VILLAGER_TRADE : SoundEvents.ENTITY_VILLAGER_AMBIENT;
        }
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }

    public void playWorkstationSound()
    {
        SoundEvent soundevent = this.getVillagerData().getProfession().getSound();

        if (soundevent != null)
        {
            this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    public void setVillagerData(VillagerData data)
    {
        VillagerData villagerdata = this.getVillagerData();

        if (villagerdata.getProfession() != data.getProfession())
        {
            this.offers = null;
        }

        this.dataManager.set(VILLAGER_DATA, data);
    }

    public VillagerData getVillagerData()
    {
        return this.dataManager.get(VILLAGER_DATA);
    }

    protected void onVillagerTrade(MerchantOffer offer)
    {
        int i = 3 + this.rand.nextInt(4);
        this.xp += offer.getGivenExp();
        this.previousCustomer = this.getCustomer();

        if (this.canLevelUp())
        {
            this.timeUntilReset = 40;
            this.leveledUp = true;
            i += 5;
        }

        if (offer.getDoesRewardExp())
        {
            this.world.addEntity(new ExperienceOrbEntity(this.world, this.getPosX(), this.getPosY() + 0.5D, this.getPosZ(), i));
        }
    }

    /**
     * Hint to AI tasks that we were attacked by the passed EntityLivingBase and should retaliate. Is not guaranteed to
     * change our actual active target (for example if we are currently busy attacking someone else)
     */
    public void setRevengeTarget(@Nullable LivingEntity livingBase)
    {
        if (livingBase != null && this.world instanceof ServerWorld)
        {
            ((ServerWorld)this.world).updateReputation(IReputationType.VILLAGER_HURT, livingBase, this);

            if (this.isAlive() && livingBase instanceof PlayerEntity)
            {
                this.world.setEntityState(this, (byte)13);
            }
        }

        super.setRevengeTarget(livingBase);
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource cause)
    {
        LOGGER.info("Villager {} died, message: '{}'", this, cause.getDeathMessage(this).getString());
        Entity entity = cause.getTrueSource();

        if (entity != null)
        {
            this.sawMurder(entity);
        }

        this.func_242369_fq();
        super.onDeath(cause);
    }

    private void func_242369_fq()
    {
        this.resetMemoryPoint(MemoryModuleType.HOME);
        this.resetMemoryPoint(MemoryModuleType.JOB_SITE);
        this.resetMemoryPoint(MemoryModuleType.POTENTIAL_JOB_SITE);
        this.resetMemoryPoint(MemoryModuleType.MEETING_POINT);
    }

    private void sawMurder(Entity murderer)
    {
        if (this.world instanceof ServerWorld)
        {
            Optional<List<LivingEntity>> optional = this.brain.getMemory(MemoryModuleType.VISIBLE_MOBS);

            if (optional.isPresent())
            {
                ServerWorld serverworld = (ServerWorld)this.world;
                optional.get().stream().filter((gossipTarget) ->
                {
                    return gossipTarget instanceof IReputationTracking;
                }).forEach((gossipTarget) ->
                {
                    serverworld.updateReputation(IReputationType.VILLAGER_KILLED, murderer, (IReputationTracking)gossipTarget);
                });
            }
        }
    }

    public void resetMemoryPoint(MemoryModuleType<GlobalPos> moduleType)
    {
        if (this.world instanceof ServerWorld)
        {
            MinecraftServer minecraftserver = ((ServerWorld)this.world).getServer();
            this.brain.getMemory(moduleType).ifPresent((jobSitePos) ->
            {
                ServerWorld serverworld = minecraftserver.getWorld(jobSitePos.getDimension());

                if (serverworld != null)
                {
                    PointOfInterestManager pointofinterestmanager = serverworld.getPointOfInterestManager();
                    Optional<PointOfInterestType> optional = pointofinterestmanager.getType(jobSitePos.getPos());
                    BiPredicate<VillagerEntity, PointOfInterestType> bipredicate = JOB_SITE_PREDICATE_MAP.get(moduleType);

                    if (optional.isPresent() && bipredicate.test(this, optional.get()))
                    {
                        pointofinterestmanager.release(jobSitePos.getPos());
                        DebugPacketSender.func_218801_c(serverworld, jobSitePos.getPos());
                    }
                }
            });
        }
    }

    public boolean canBreed()
    {
        return this.foodLevel + this.getFoodValueFromInventory() >= 12 && this.getGrowingAge() == 0;
    }

    private boolean isHungry()
    {
        return this.foodLevel < 12;
    }

    private void eat()
    {
        if (this.isHungry() && this.getFoodValueFromInventory() != 0)
        {
            for (int i = 0; i < this.getVillagerInventory().getSizeInventory(); ++i)
            {
                ItemStack itemstack = this.getVillagerInventory().getStackInSlot(i);

                if (!itemstack.isEmpty())
                {
                    Integer integer = FOOD_VALUES.get(itemstack.getItem());

                    if (integer != null)
                    {
                        int j = itemstack.getCount();

                        for (int k = j; k > 0; --k)
                        {
                            this.foodLevel = (byte)(this.foodLevel + integer);
                            this.getVillagerInventory().decrStackSize(i, 1);

                            if (!this.isHungry())
                            {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public int getPlayerReputation(PlayerEntity player)
    {
        return this.gossip.getReputation(player.getUniqueID(), (gossipType) ->
        {
            return true;
        });
    }

    private void decrFoodLevel(int qty)
    {
        this.foodLevel = (byte)(this.foodLevel - qty);
    }

    public void func_223346_ep()
    {
        this.eat();
        this.decrFoodLevel(12);
    }

    public void setOffers(MerchantOffers offersIn)
    {
        this.offers = offersIn;
    }

    private boolean canLevelUp()
    {
        int i = this.getVillagerData().getLevel();
        return VillagerData.canLevelUp(i) && this.xp >= VillagerData.getExperienceNext(i);
    }

    private void levelUp()
    {
        this.setVillagerData(this.getVillagerData().withLevel(this.getVillagerData().getLevel() + 1));
        this.populateTradeData();
    }

    protected ITextComponent getProfessionName()
    {
        return new TranslationTextComponent(this.getType().getTranslationKey() + '.' + Registry.VILLAGER_PROFESSION.getKey(this.getVillagerData().getProfession()).getPath());
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    public void handleStatusUpdate(byte id)
    {
        if (id == 12)
        {
            this.spawnParticles(ParticleTypes.HEART);
        }
        else if (id == 13)
        {
            this.spawnParticles(ParticleTypes.ANGRY_VILLAGER);
        }
        else if (id == 14)
        {
            this.spawnParticles(ParticleTypes.HAPPY_VILLAGER);
        }
        else if (id == 42)
        {
            this.spawnParticles(ParticleTypes.SPLASH);
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        if (reason == SpawnReason.BREEDING)
        {
            this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.NONE));
        }

        if (reason == SpawnReason.COMMAND || reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.SPAWNER || reason == SpawnReason.DISPENSER)
        {
            this.setVillagerData(this.getVillagerData().withType(VillagerType.func_242371_a(worldIn.func_242406_i(this.getPosition()))));
        }

        if (reason == SpawnReason.STRUCTURE)
        {
            this.assignProfessionWhenSpawned = true;
        }

        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public VillagerEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_)
    {
        double d0 = this.rand.nextDouble();
        VillagerType villagertype;

        if (d0 < 0.5D)
        {
            villagertype = VillagerType.func_242371_a(p_241840_1_.func_242406_i(this.getPosition()));
        }
        else if (d0 < 0.75D)
        {
            villagertype = this.getVillagerData().getType();
        }
        else
        {
            villagertype = ((VillagerEntity)p_241840_2_).getVillagerData().getType();
        }

        VillagerEntity villagerentity = new VillagerEntity(EntityType.VILLAGER, p_241840_1_, villagertype);
        villagerentity.onInitialSpawn(p_241840_1_, p_241840_1_.getDifficultyForLocation(villagerentity.getPosition()), SpawnReason.BREEDING, (ILivingEntityData)null, (CompoundNBT)null);
        return villagerentity;
    }

    public void func_241841_a(ServerWorld p_241841_1_, LightningBoltEntity p_241841_2_)
    {
        if (p_241841_1_.getDifficulty() != Difficulty.PEACEFUL)
        {
            LOGGER.info("Villager {} was struck by lightning {}.", this, p_241841_2_);
            WitchEntity witchentity = EntityType.WITCH.create(p_241841_1_);
            witchentity.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
            witchentity.onInitialSpawn(p_241841_1_, p_241841_1_.getDifficultyForLocation(witchentity.getPosition()), SpawnReason.CONVERSION, (ILivingEntityData)null, (CompoundNBT)null);
            witchentity.setNoAI(this.isAIDisabled());

            if (this.hasCustomName())
            {
                witchentity.setCustomName(this.getCustomName());
                witchentity.setCustomNameVisible(this.isCustomNameVisible());
            }

            witchentity.enablePersistence();
            p_241841_1_.func_242417_l(witchentity);
            this.func_242369_fq();
            this.remove();
        }
        else
        {
            super.func_241841_a(p_241841_1_, p_241841_2_);
        }
    }

    /**
     * Tests if this entity should pickup a weapon or an armor. Entity drops current weapon or armor if the new one is
     * better.
     */
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity)
    {
        ItemStack itemstack = itemEntity.getItem();

        if (this.func_230293_i_(itemstack))
        {
            Inventory inventory = this.getVillagerInventory();
            boolean flag = inventory.func_233541_b_(itemstack);

            if (!flag)
            {
                return;
            }

            this.triggerItemPickupTrigger(itemEntity);
            this.onItemPickup(itemEntity, itemstack.getCount());
            ItemStack itemstack1 = inventory.addItem(itemstack);

            if (itemstack1.isEmpty())
            {
                itemEntity.remove();
            }
            else
            {
                itemstack.setCount(itemstack1.getCount());
            }
        }
    }

    public boolean func_230293_i_(ItemStack p_230293_1_)
    {
        Item item = p_230293_1_.getItem();
        return (ALLOWED_INVENTORY_ITEMS.contains(item) || this.getVillagerData().getProfession().getSpecificItems().contains(item)) && this.getVillagerInventory().func_233541_b_(p_230293_1_);
    }

    /**
     * Used by {@link net.minecraft.entity.ai.EntityAIVillagerInteract EntityAIVillagerInteract} to check if the
     * villager can give some items from an inventory to another villager.
     */
    public boolean canAbondonItems()
    {
        return this.getFoodValueFromInventory() >= 24;
    }

    public boolean wantsMoreFood()
    {
        return this.getFoodValueFromInventory() < 12;
    }

    /**
     * @return calculated food value from item stacks in this villager's inventory
     */
    private int getFoodValueFromInventory()
    {
        Inventory inventory = this.getVillagerInventory();
        return FOOD_VALUES.entrySet().stream().mapToInt((foodValueEntry) ->
        {
            return inventory.count(foodValueEntry.getKey()) * foodValueEntry.getValue();
        }).sum();
    }

    /**
     * Returns true if villager has seeds, potatoes or carrots in inventory
     */
    public boolean isFarmItemInInventory()
    {
        return this.getVillagerInventory().hasAny(ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
    }

    protected void populateTradeData()
    {
        VillagerData villagerdata = this.getVillagerData();
        Int2ObjectMap<VillagerTrades.ITrade[]> int2objectmap = VillagerTrades.VILLAGER_DEFAULT_TRADES.get(villagerdata.getProfession());

        if (int2objectmap != null && !int2objectmap.isEmpty())
        {
            VillagerTrades.ITrade[] avillagertrades$itrade = int2objectmap.get(villagerdata.getLevel());

            if (avillagertrades$itrade != null)
            {
                MerchantOffers merchantoffers = this.getOffers();
                this.addTrades(merchantoffers, avillagertrades$itrade, 2);
            }
        }
    }

    public void func_242368_a(ServerWorld p_242368_1_, VillagerEntity p_242368_2_, long p_242368_3_)
    {
        if ((p_242368_3_ < this.lastGossipTime || p_242368_3_ >= this.lastGossipTime + 1200L) && (p_242368_3_ < p_242368_2_.lastGossipTime || p_242368_3_ >= p_242368_2_.lastGossipTime + 1200L))
        {
            this.gossip.transferFrom(p_242368_2_.gossip, this.rand, 10);
            this.lastGossipTime = p_242368_3_;
            p_242368_2_.lastGossipTime = p_242368_3_;
            this.func_242367_a(p_242368_1_, p_242368_3_, 5);
        }
    }

    private void tickGossip()
    {
        long i = this.world.getGameTime();

        if (this.lastGossipDecay == 0L)
        {
            this.lastGossipDecay = i;
        }
        else if (i >= this.lastGossipDecay + 24000L)
        {
            this.gossip.tick();
            this.lastGossipDecay = i;
        }
    }

    public void func_242367_a(ServerWorld p_242367_1_, long p_242367_2_, int p_242367_4_)
    {
        if (this.canSpawnGolems(p_242367_2_))
        {
            AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(10.0D, 10.0D, 10.0D);
            List<VillagerEntity> list = p_242367_1_.getEntitiesWithinAABB(VillagerEntity.class, axisalignedbb);
            List<VillagerEntity> list1 = list.stream().filter((villager) ->
            {
                return villager.canSpawnGolems(p_242367_2_);
            }).limit(5L).collect(Collectors.toList());

            if (list1.size() >= p_242367_4_)
            {
                IronGolemEntity irongolementity = this.trySpawnGolem(p_242367_1_);

                if (irongolementity != null)
                {
                    list.forEach(GolemLastSeenSensor::reset);
                }
            }
        }
    }

    public boolean canSpawnGolems(long gameTime)
    {
        if (!this.hasSleptAndWorkedRecently(this.world.getGameTime()))
        {
            return false;
        }
        else
        {
            return !this.brain.hasMemory(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
        }
    }

    @Nullable
    private IronGolemEntity trySpawnGolem(ServerWorld p_213759_1_)
    {
        BlockPos blockpos = this.getPosition();

        for (int i = 0; i < 10; ++i)
        {
            double d0 = (double)(p_213759_1_.rand.nextInt(16) - 8);
            double d1 = (double)(p_213759_1_.rand.nextInt(16) - 8);
            BlockPos blockpos1 = this.getValidGolemSpawnPosition(blockpos, d0, d1);

            if (blockpos1 != null)
            {
                IronGolemEntity irongolementity = EntityType.IRON_GOLEM.create(p_213759_1_, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, blockpos1, SpawnReason.MOB_SUMMONED, false, false);

                if (irongolementity != null)
                {
                    if (irongolementity.canSpawn(p_213759_1_, SpawnReason.MOB_SUMMONED) && irongolementity.isNotColliding(p_213759_1_))
                    {
                        p_213759_1_.func_242417_l(irongolementity);
                        return irongolementity;
                    }

                    irongolementity.remove();
                }
            }
        }

        return null;
    }

    @Nullable
    private BlockPos getValidGolemSpawnPosition(BlockPos pos, double x, double z)
    {
        int i = 6;
        BlockPos blockpos = pos.add(x, 6.0D, z);
        BlockState blockstate = this.world.getBlockState(blockpos);

        for (int j = 6; j >= -6; --j)
        {
            BlockPos blockpos1 = blockpos;
            BlockState blockstate1 = blockstate;
            blockpos = blockpos.down();
            blockstate = this.world.getBlockState(blockpos);

            if ((blockstate1.isAir() || blockstate1.getMaterial().isLiquid()) && blockstate.getMaterial().isOpaque())
            {
                return blockpos1;
            }
        }

        return null;
    }

    public void updateReputation(IReputationType type, Entity target)
    {
        if (type == IReputationType.ZOMBIE_VILLAGER_CURED)
        {
            this.gossip.add(target.getUniqueID(), GossipType.MAJOR_POSITIVE, 20);
            this.gossip.add(target.getUniqueID(), GossipType.MINOR_POSITIVE, 25);
        }
        else if (type == IReputationType.TRADE)
        {
            this.gossip.add(target.getUniqueID(), GossipType.TRADING, 2);
        }
        else if (type == IReputationType.VILLAGER_HURT)
        {
            this.gossip.add(target.getUniqueID(), GossipType.MINOR_NEGATIVE, 25);
        }
        else if (type == IReputationType.VILLAGER_KILLED)
        {
            this.gossip.add(target.getUniqueID(), GossipType.MAJOR_NEGATIVE, 25);
        }
    }

    public int getXp()
    {
        return this.xp;
    }

    public void setXp(int xpIn)
    {
        this.xp = xpIn;
    }

    private void func_223718_eH()
    {
        this.resetOffersAndAdjustForDemand();
        this.restocksToday = 0;
    }

    public GossipManager getGossip()
    {
        return this.gossip;
    }

    public void setGossips(INBT gossip)
    {
        this.gossip.read(new Dynamic<>(NBTDynamicOps.INSTANCE, gossip));
    }

    protected void sendDebugPackets()
    {
        super.sendDebugPackets();
        DebugPacketSender.sendLivingEntity(this);
    }

    public void startSleeping(BlockPos pos)
    {
        super.startSleeping(pos);
        this.brain.setMemory(MemoryModuleType.LAST_SLEPT, this.world.getGameTime());
        this.brain.removeMemory(MemoryModuleType.WALK_TARGET);
        this.brain.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    public void wakeUp()
    {
        super.wakeUp();
        this.brain.setMemory(MemoryModuleType.LAST_WOKEN, this.world.getGameTime());
    }

    private boolean hasSleptAndWorkedRecently(long gameTime)
    {
        Optional<Long> optional = this.brain.getMemory(MemoryModuleType.LAST_SLEPT);

        if (optional.isPresent())
        {
            return gameTime - optional.get() < 24000L;
        }
        else
        {
            return false;
        }
    }
}
