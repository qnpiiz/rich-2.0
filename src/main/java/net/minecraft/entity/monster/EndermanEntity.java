package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class EndermanEntity extends MonsterEntity implements IAngerable
{
    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier ATTACKING_SPEED_BOOST = new AttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", (double)0.15F, AttributeModifier.Operation.ADDITION);
    private static final DataParameter<Optional<BlockState>> CARRIED_BLOCK = EntityDataManager.createKey(EndermanEntity.class, DataSerializers.OPTIONAL_BLOCK_STATE);
    private static final DataParameter<Boolean> SCREAMING = EntityDataManager.createKey(EndermanEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> field_226535_bx_ = EntityDataManager.createKey(EndermanEntity.class, DataSerializers.BOOLEAN);
    private static final Predicate<LivingEntity> field_213627_bA = (p_213626_0_) ->
    {
        return p_213626_0_ instanceof EndermiteEntity && ((EndermiteEntity)p_213626_0_).isSpawnedByPlayer();
    };
    private int field_226536_bz_ = Integer.MIN_VALUE;
    private int targetChangeTime;
    private static final RangedInteger field_234286_bz_ = TickRangeConverter.convertRange(20, 39);
    private int field_234284_bA_;
    private UUID field_234285_bB_;

    public EndermanEntity(EntityType <? extends EndermanEntity > type, World worldIn)
    {
        super(type, worldIn);
        this.stepHeight = 1.0F;
        this.setPathPriority(PathNodeType.WATER, -1.0F);
    }

    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new EndermanEntity.StareGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(10, new EndermanEntity.PlaceBlockGoal(this));
        this.goalSelector.addGoal(11, new EndermanEntity.TakeBlockGoal(this));
        this.targetSelector.addGoal(1, new EndermanEntity.FindPlayerGoal(this, this::func_233680_b_));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, EndermiteEntity.class, 10, true, false, field_213627_bA));
        this.targetSelector.addGoal(4, new ResetAngerGoal<>(this, false));
    }

    public static AttributeModifierMap.MutableAttribute func_234287_m_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 40.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.3F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 7.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 64.0D);
    }

    /**
     * Sets the active target the Task system uses for tracking
     */
    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn)
    {
        super.setAttackTarget(entitylivingbaseIn);
        ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);

        if (entitylivingbaseIn == null)
        {
            this.targetChangeTime = 0;
            this.dataManager.set(SCREAMING, false);
            this.dataManager.set(field_226535_bx_, false);
            modifiableattributeinstance.removeModifier(ATTACKING_SPEED_BOOST);
        }
        else
        {
            this.targetChangeTime = this.ticksExisted;
            this.dataManager.set(SCREAMING, true);

            if (!modifiableattributeinstance.hasModifier(ATTACKING_SPEED_BOOST))
            {
                modifiableattributeinstance.applyNonPersistentModifier(ATTACKING_SPEED_BOOST);
            }
        }
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(CARRIED_BLOCK, Optional.empty());
        this.dataManager.register(SCREAMING, false);
        this.dataManager.register(field_226535_bx_, false);
    }

    public void func_230258_H__()
    {
        this.setAngerTime(field_234286_bz_.getRandomWithinRange(this.rand));
    }

    public void setAngerTime(int time)
    {
        this.field_234284_bA_ = time;
    }

    public int getAngerTime()
    {
        return this.field_234284_bA_;
    }

    public void setAngerTarget(@Nullable UUID target)
    {
        this.field_234285_bB_ = target;
    }

    public UUID getAngerTarget()
    {
        return this.field_234285_bB_;
    }

    public void func_226539_l_()
    {
        if (this.ticksExisted >= this.field_226536_bz_ + 400)
        {
            this.field_226536_bz_ = this.ticksExisted;

            if (!this.isSilent())
            {
                this.world.playSound(this.getPosX(), this.getPosYEye(), this.getPosZ(), SoundEvents.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
            }
        }
    }

    public void notifyDataManagerChange(DataParameter<?> key)
    {
        if (SCREAMING.equals(key) && this.func_226537_et_() && this.world.isRemote)
        {
            this.func_226539_l_();
        }

        super.notifyDataManagerChange(key);
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        BlockState blockstate = this.getHeldBlockState();

        if (blockstate != null)
        {
            compound.put("carriedBlockState", NBTUtil.writeBlockState(blockstate));
        }

        this.writeAngerNBT(compound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        BlockState blockstate = null;

        if (compound.contains("carriedBlockState", 10))
        {
            blockstate = NBTUtil.readBlockState(compound.getCompound("carriedBlockState"));

            if (blockstate.isAir())
            {
                blockstate = null;
            }
        }

        this.setHeldBlockState(blockstate);
        this.readAngerNBT((ServerWorld)this.world, compound);
    }

    /**
     * Checks to see if this enderman should be attacking this player
     */
    private boolean shouldAttackPlayer(PlayerEntity player)
    {
        ItemStack itemstack = player.inventory.armorInventory.get(3);

        if (itemstack.getItem() == Blocks.CARVED_PUMPKIN.asItem())
        {
            return false;
        }
        else
        {
            Vector3d vector3d = player.getLook(1.0F).normalize();
            Vector3d vector3d1 = new Vector3d(this.getPosX() - player.getPosX(), this.getPosYEye() - player.getPosYEye(), this.getPosZ() - player.getPosZ());
            double d0 = vector3d1.length();
            vector3d1 = vector3d1.normalize();
            double d1 = vector3d.dotProduct(vector3d1);
            return d1 > 1.0D - 0.025D / d0 ? player.canEntityBeSeen(this) : false;
        }
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return 2.55F;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void livingTick()
    {
        if (this.world.isRemote)
        {
            for (int i = 0; i < 2; ++i)
            {
                this.world.addParticle(ParticleTypes.PORTAL, this.getPosXRandom(0.5D), this.getPosYRandom() - 0.25D, this.getPosZRandom(0.5D), (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
            }
        }

        this.isJumping = false;

        if (!this.world.isRemote)
        {
            this.func_241359_a_((ServerWorld)this.world, true);
        }

        super.livingTick();
    }

    public boolean isWaterSensitive()
    {
        return true;
    }

    protected void updateAITasks()
    {
        if (this.world.isDaytime() && this.ticksExisted >= this.targetChangeTime + 600)
        {
            float f = this.getBrightness();

            if (f > 0.5F && this.world.canSeeSky(this.getPosition()) && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F)
            {
                this.setAttackTarget((LivingEntity)null);
                this.teleportRandomly();
            }
        }

        super.updateAITasks();
    }

    /**
     * Teleport the enderman to a random nearby position
     */
    protected boolean teleportRandomly()
    {
        if (!this.world.isRemote() && this.isAlive())
        {
            double d0 = this.getPosX() + (this.rand.nextDouble() - 0.5D) * 64.0D;
            double d1 = this.getPosY() + (double)(this.rand.nextInt(64) - 32);
            double d2 = this.getPosZ() + (this.rand.nextDouble() - 0.5D) * 64.0D;
            return this.teleportTo(d0, d1, d2);
        }
        else
        {
            return false;
        }
    }

    /**
     * Teleport the enderman to another entity
     */
    private boolean teleportToEntity(Entity p_70816_1_)
    {
        Vector3d vector3d = new Vector3d(this.getPosX() - p_70816_1_.getPosX(), this.getPosYHeight(0.5D) - p_70816_1_.getPosYEye(), this.getPosZ() - p_70816_1_.getPosZ());
        vector3d = vector3d.normalize();
        double d0 = 16.0D;
        double d1 = this.getPosX() + (this.rand.nextDouble() - 0.5D) * 8.0D - vector3d.x * 16.0D;
        double d2 = this.getPosY() + (double)(this.rand.nextInt(16) - 8) - vector3d.y * 16.0D;
        double d3 = this.getPosZ() + (this.rand.nextDouble() - 0.5D) * 8.0D - vector3d.z * 16.0D;
        return this.teleportTo(d1, d2, d3);
    }

    /**
     * Teleport the enderman
     */
    private boolean teleportTo(double x, double y, double z)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(x, y, z);

        while (blockpos$mutable.getY() > 0 && !this.world.getBlockState(blockpos$mutable).getMaterial().blocksMovement())
        {
            blockpos$mutable.move(Direction.DOWN);
        }

        BlockState blockstate = this.world.getBlockState(blockpos$mutable);
        boolean flag = blockstate.getMaterial().blocksMovement();
        boolean flag1 = blockstate.getFluidState().isTagged(FluidTags.WATER);

        if (flag && !flag1)
        {
            boolean flag2 = this.attemptTeleport(x, y, z, true);

            if (flag2 && !this.isSilent())
            {
                this.world.playSound((PlayerEntity)null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return flag2;
        }
        else
        {
            return false;
        }
    }

    protected SoundEvent getAmbientSound()
    {
        return this.isScreaming() ? SoundEvents.ENTITY_ENDERMAN_SCREAM : SoundEvents.ENTITY_ENDERMAN_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_ENDERMAN_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_ENDERMAN_DEATH;
    }

    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn)
    {
        super.dropSpecialItems(source, looting, recentlyHitIn);
        BlockState blockstate = this.getHeldBlockState();

        if (blockstate != null)
        {
            this.entityDropItem(blockstate.getBlock());
        }
    }

    public void setHeldBlockState(@Nullable BlockState state)
    {
        this.dataManager.set(CARRIED_BLOCK, Optional.ofNullable(state));
    }

    @Nullable
    public BlockState getHeldBlockState()
    {
        return this.dataManager.get(CARRIED_BLOCK).orElse((BlockState)null);
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
        else if (source instanceof IndirectEntityDamageSource)
        {
            for (int i = 0; i < 64; ++i)
            {
                if (this.teleportRandomly())
                {
                    return true;
                }
            }

            return false;
        }
        else
        {
            boolean flag = super.attackEntityFrom(source, amount);

            if (!this.world.isRemote() && !(source.getTrueSource() instanceof LivingEntity) && this.rand.nextInt(10) != 0)
            {
                this.teleportRandomly();
            }

            return flag;
        }
    }

    public boolean isScreaming()
    {
        return this.dataManager.get(SCREAMING);
    }

    public boolean func_226537_et_()
    {
        return this.dataManager.get(field_226535_bx_);
    }

    public void func_226538_eu_()
    {
        this.dataManager.set(field_226535_bx_, true);
    }

    public boolean preventDespawn()
    {
        return super.preventDespawn() || this.getHeldBlockState() != null;
    }

    static class FindPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity>
    {
        private final EndermanEntity enderman;
        private PlayerEntity player;
        private int aggroTime;
        private int teleportTime;
        private final EntityPredicate field_220791_m;
        private final EntityPredicate field_220792_n = (new EntityPredicate()).setLineOfSiteRequired();

        public FindPlayerGoal(EndermanEntity p_i241912_1_, @Nullable Predicate<LivingEntity> p_i241912_2_)
        {
            super(p_i241912_1_, PlayerEntity.class, 10, false, false, p_i241912_2_);
            this.enderman = p_i241912_1_;
            this.field_220791_m = (new EntityPredicate()).setDistance(this.getTargetDistance()).setCustomPredicate((p_220790_1_) ->
            {
                return p_i241912_1_.shouldAttackPlayer((PlayerEntity)p_220790_1_);
            });
        }

        public boolean shouldExecute()
        {
            this.player = this.enderman.world.getClosestPlayer(this.field_220791_m, this.enderman);
            return this.player != null;
        }

        public void startExecuting()
        {
            this.aggroTime = 5;
            this.teleportTime = 0;
            this.enderman.func_226538_eu_();
        }

        public void resetTask()
        {
            this.player = null;
            super.resetTask();
        }

        public boolean shouldContinueExecuting()
        {
            if (this.player != null)
            {
                if (!this.enderman.shouldAttackPlayer(this.player))
                {
                    return false;
                }
                else
                {
                    this.enderman.faceEntity(this.player, 10.0F, 10.0F);
                    return true;
                }
            }
            else
            {
                return this.nearestTarget != null && this.field_220792_n.canTarget(this.enderman, this.nearestTarget) ? true : super.shouldContinueExecuting();
            }
        }

        public void tick()
        {
            if (this.enderman.getAttackTarget() == null)
            {
                super.setNearestTarget((LivingEntity)null);
            }

            if (this.player != null)
            {
                if (--this.aggroTime <= 0)
                {
                    this.nearestTarget = this.player;
                    this.player = null;
                    super.startExecuting();
                }
            }
            else
            {
                if (this.nearestTarget != null && !this.enderman.isPassenger())
                {
                    if (this.enderman.shouldAttackPlayer((PlayerEntity)this.nearestTarget))
                    {
                        if (this.nearestTarget.getDistanceSq(this.enderman) < 16.0D)
                        {
                            this.enderman.teleportRandomly();
                        }

                        this.teleportTime = 0;
                    }
                    else if (this.nearestTarget.getDistanceSq(this.enderman) > 256.0D && this.teleportTime++ >= 30 && this.enderman.teleportToEntity(this.nearestTarget))
                    {
                        this.teleportTime = 0;
                    }
                }

                super.tick();
            }
        }
    }

    static class PlaceBlockGoal extends Goal
    {
        private final EndermanEntity enderman;

        public PlaceBlockGoal(EndermanEntity p_i45843_1_)
        {
            this.enderman = p_i45843_1_;
        }

        public boolean shouldExecute()
        {
            if (this.enderman.getHeldBlockState() == null)
            {
                return false;
            }
            else if (!this.enderman.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING))
            {
                return false;
            }
            else
            {
                return this.enderman.getRNG().nextInt(2000) == 0;
            }
        }

        public void tick()
        {
            Random random = this.enderman.getRNG();
            World world = this.enderman.world;
            int i = MathHelper.floor(this.enderman.getPosX() - 1.0D + random.nextDouble() * 2.0D);
            int j = MathHelper.floor(this.enderman.getPosY() + random.nextDouble() * 2.0D);
            int k = MathHelper.floor(this.enderman.getPosZ() - 1.0D + random.nextDouble() * 2.0D);
            BlockPos blockpos = new BlockPos(i, j, k);
            BlockState blockstate = world.getBlockState(blockpos);
            BlockPos blockpos1 = blockpos.down();
            BlockState blockstate1 = world.getBlockState(blockpos1);
            BlockState blockstate2 = this.enderman.getHeldBlockState();

            if (blockstate2 != null)
            {
                blockstate2 = Block.getValidBlockForPosition(blockstate2, this.enderman.world, blockpos);

                if (this.func_220836_a(world, blockpos, blockstate2, blockstate, blockstate1, blockpos1))
                {
                    world.setBlockState(blockpos, blockstate2, 3);
                    this.enderman.setHeldBlockState((BlockState)null);
                }
            }
        }

        private boolean func_220836_a(World p_220836_1_, BlockPos p_220836_2_, BlockState p_220836_3_, BlockState p_220836_4_, BlockState p_220836_5_, BlockPos p_220836_6_)
        {
            return p_220836_4_.isAir() && !p_220836_5_.isAir() && !p_220836_5_.isIn(Blocks.BEDROCK) && p_220836_5_.hasOpaqueCollisionShape(p_220836_1_, p_220836_6_) && p_220836_3_.isValidPosition(p_220836_1_, p_220836_2_) && p_220836_1_.getEntitiesWithinAABBExcludingEntity(this.enderman, AxisAlignedBB.fromVector(Vector3d.copy(p_220836_2_))).isEmpty();
        }
    }

    static class StareGoal extends Goal
    {
        private final EndermanEntity enderman;
        private LivingEntity targetPlayer;

        public StareGoal(EndermanEntity endermanIn)
        {
            this.enderman = endermanIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean shouldExecute()
        {
            this.targetPlayer = this.enderman.getAttackTarget();

            if (!(this.targetPlayer instanceof PlayerEntity))
            {
                return false;
            }
            else
            {
                double d0 = this.targetPlayer.getDistanceSq(this.enderman);
                return d0 > 256.0D ? false : this.enderman.shouldAttackPlayer((PlayerEntity)this.targetPlayer);
            }
        }

        public void startExecuting()
        {
            this.enderman.getNavigator().clearPath();
        }

        public void tick()
        {
            this.enderman.getLookController().setLookPosition(this.targetPlayer.getPosX(), this.targetPlayer.getPosYEye(), this.targetPlayer.getPosZ());
        }
    }

    static class TakeBlockGoal extends Goal
    {
        private final EndermanEntity enderman;

        public TakeBlockGoal(EndermanEntity endermanIn)
        {
            this.enderman = endermanIn;
        }

        public boolean shouldExecute()
        {
            if (this.enderman.getHeldBlockState() != null)
            {
                return false;
            }
            else if (!this.enderman.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING))
            {
                return false;
            }
            else
            {
                return this.enderman.getRNG().nextInt(20) == 0;
            }
        }

        public void tick()
        {
            Random random = this.enderman.getRNG();
            World world = this.enderman.world;
            int i = MathHelper.floor(this.enderman.getPosX() - 2.0D + random.nextDouble() * 4.0D);
            int j = MathHelper.floor(this.enderman.getPosY() + random.nextDouble() * 3.0D);
            int k = MathHelper.floor(this.enderman.getPosZ() - 2.0D + random.nextDouble() * 4.0D);
            BlockPos blockpos = new BlockPos(i, j, k);
            BlockState blockstate = world.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            Vector3d vector3d = new Vector3d((double)MathHelper.floor(this.enderman.getPosX()) + 0.5D, (double)j + 0.5D, (double)MathHelper.floor(this.enderman.getPosZ()) + 0.5D);
            Vector3d vector3d1 = new Vector3d((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D);
            BlockRayTraceResult blockraytraceresult = world.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, this.enderman));
            boolean flag = blockraytraceresult.getPos().equals(blockpos);

            if (block.isIn(BlockTags.ENDERMAN_HOLDABLE) && flag)
            {
                world.removeBlock(blockpos, false);
                this.enderman.setHeldBlockState(blockstate.getBlock().getDefaultState());
            }
        }
    }
}
