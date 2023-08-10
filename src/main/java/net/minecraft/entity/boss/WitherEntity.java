package net.minecraft.entity.boss;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IChargeableMob;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;

public class WitherEntity extends MonsterEntity implements IChargeableMob, IRangedAttackMob
{
    private static final DataParameter<Integer> FIRST_HEAD_TARGET = EntityDataManager.createKey(WitherEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> SECOND_HEAD_TARGET = EntityDataManager.createKey(WitherEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> THIRD_HEAD_TARGET = EntityDataManager.createKey(WitherEntity.class, DataSerializers.VARINT);
    private static final List<DataParameter<Integer>> HEAD_TARGETS = ImmutableList.of(FIRST_HEAD_TARGET, SECOND_HEAD_TARGET, THIRD_HEAD_TARGET);
    private static final DataParameter<Integer> INVULNERABILITY_TIME = EntityDataManager.createKey(WitherEntity.class, DataSerializers.VARINT);
    private final float[] xRotationHeads = new float[2];
    private final float[] yRotationHeads = new float[2];
    private final float[] xRotOHeads = new float[2];
    private final float[] yRotOHeads = new float[2];
    private final int[] nextHeadUpdate = new int[2];
    private final int[] idleHeadUpdates = new int[2];
    private int blockBreakCounter;
    private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS)).setDarkenSky(true);
    private static final Predicate<LivingEntity> NOT_UNDEAD = (p_213797_0_) ->
    {
        return p_213797_0_.getCreatureAttribute() != CreatureAttribute.UNDEAD && p_213797_0_.attackable();
    };
    private static final EntityPredicate ENEMY_CONDITION = (new EntityPredicate()).setDistance(20.0D).setCustomPredicate(NOT_UNDEAD);

    public WitherEntity(EntityType <? extends WitherEntity > wither, World world)
    {
        super(wither, world);
        this.setHealth(this.getMaxHealth());
        this.getNavigator().setCanSwim(true);
        this.experienceValue = 50;
    }

    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new WitherEntity.DoNothingGoal());
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 40, 20.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MobEntity.class, 0, false, false, NOT_UNDEAD));
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(FIRST_HEAD_TARGET, 0);
        this.dataManager.register(SECOND_HEAD_TARGET, 0);
        this.dataManager.register(THIRD_HEAD_TARGET, 0);
        this.dataManager.register(INVULNERABILITY_TIME, 0);
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putInt("Invul", this.getInvulTime());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setInvulTime(compound.getInt("Invul"));

        if (this.hasCustomName())
        {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    public void setCustomName(@Nullable ITextComponent name)
    {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_WITHER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_WITHER_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_WITHER_DEATH;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void livingTick()
    {
        Vector3d vector3d = this.getMotion().mul(1.0D, 0.6D, 1.0D);

        if (!this.world.isRemote && this.getWatchedTargetId(0) > 0)
        {
            Entity entity = this.world.getEntityByID(this.getWatchedTargetId(0));

            if (entity != null)
            {
                double d0 = vector3d.y;

                if (this.getPosY() < entity.getPosY() || !this.isCharged() && this.getPosY() < entity.getPosY() + 5.0D)
                {
                    d0 = Math.max(0.0D, d0);
                    d0 = d0 + (0.3D - d0 * (double)0.6F);
                }

                vector3d = new Vector3d(vector3d.x, d0, vector3d.z);
                Vector3d vector3d1 = new Vector3d(entity.getPosX() - this.getPosX(), 0.0D, entity.getPosZ() - this.getPosZ());

                if (horizontalMag(vector3d1) > 9.0D)
                {
                    Vector3d vector3d2 = vector3d1.normalize();
                    vector3d = vector3d.add(vector3d2.x * 0.3D - vector3d.x * 0.6D, 0.0D, vector3d2.z * 0.3D - vector3d.z * 0.6D);
                }
            }
        }

        this.setMotion(vector3d);

        if (horizontalMag(vector3d) > 0.05D)
        {
            this.rotationYaw = (float)MathHelper.atan2(vector3d.z, vector3d.x) * (180F / (float)Math.PI) - 90.0F;
        }

        super.livingTick();

        for (int i = 0; i < 2; ++i)
        {
            this.yRotOHeads[i] = this.yRotationHeads[i];
            this.xRotOHeads[i] = this.xRotationHeads[i];
        }

        for (int j = 0; j < 2; ++j)
        {
            int k = this.getWatchedTargetId(j + 1);
            Entity entity1 = null;

            if (k > 0)
            {
                entity1 = this.world.getEntityByID(k);
            }

            if (entity1 != null)
            {
                double d9 = this.getHeadX(j + 1);
                double d1 = this.getHeadY(j + 1);
                double d3 = this.getHeadZ(j + 1);
                double d4 = entity1.getPosX() - d9;
                double d5 = entity1.getPosYEye() - d1;
                double d6 = entity1.getPosZ() - d3;
                double d7 = (double)MathHelper.sqrt(d4 * d4 + d6 * d6);
                float f = (float)(MathHelper.atan2(d6, d4) * (double)(180F / (float)Math.PI)) - 90.0F;
                float f1 = (float)(-(MathHelper.atan2(d5, d7) * (double)(180F / (float)Math.PI)));
                this.xRotationHeads[j] = this.rotlerp(this.xRotationHeads[j], f1, 40.0F);
                this.yRotationHeads[j] = this.rotlerp(this.yRotationHeads[j], f, 10.0F);
            }
            else
            {
                this.yRotationHeads[j] = this.rotlerp(this.yRotationHeads[j], this.renderYawOffset, 10.0F);
            }
        }

        boolean flag = this.isCharged();

        for (int l = 0; l < 3; ++l)
        {
            double d8 = this.getHeadX(l);
            double d10 = this.getHeadY(l);
            double d2 = this.getHeadZ(l);
            this.world.addParticle(ParticleTypes.SMOKE, d8 + this.rand.nextGaussian() * (double)0.3F, d10 + this.rand.nextGaussian() * (double)0.3F, d2 + this.rand.nextGaussian() * (double)0.3F, 0.0D, 0.0D, 0.0D);

            if (flag && this.world.rand.nextInt(4) == 0)
            {
                this.world.addParticle(ParticleTypes.ENTITY_EFFECT, d8 + this.rand.nextGaussian() * (double)0.3F, d10 + this.rand.nextGaussian() * (double)0.3F, d2 + this.rand.nextGaussian() * (double)0.3F, (double)0.7F, (double)0.7F, 0.5D);
            }
        }

        if (this.getInvulTime() > 0)
        {
            for (int i1 = 0; i1 < 3; ++i1)
            {
                this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.getPosX() + this.rand.nextGaussian(), this.getPosY() + (double)(this.rand.nextFloat() * 3.3F), this.getPosZ() + this.rand.nextGaussian(), (double)0.7F, (double)0.7F, (double)0.9F);
            }
        }
    }

    protected void updateAITasks()
    {
        if (this.getInvulTime() > 0)
        {
            int j1 = this.getInvulTime() - 1;

            if (j1 <= 0)
            {
                Explosion.Mode explosion$mode = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
                this.world.createExplosion(this, this.getPosX(), this.getPosYEye(), this.getPosZ(), 7.0F, false, explosion$mode);

                if (!this.isSilent())
                {
                    this.world.playBroadcastSound(1023, this.getPosition(), 0);
                }
            }

            this.setInvulTime(j1);

            if (this.ticksExisted % 10 == 0)
            {
                this.heal(10.0F);
            }
        }
        else
        {
            super.updateAITasks();

            for (int i = 1; i < 3; ++i)
            {
                if (this.ticksExisted >= this.nextHeadUpdate[i - 1])
                {
                    this.nextHeadUpdate[i - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);

                    if (this.world.getDifficulty() == Difficulty.NORMAL || this.world.getDifficulty() == Difficulty.HARD)
                    {
                        int j3 = i - 1;
                        int k3 = this.idleHeadUpdates[i - 1];
                        this.idleHeadUpdates[j3] = this.idleHeadUpdates[i - 1] + 1;

                        if (k3 > 15)
                        {
                            float f = 10.0F;
                            float f1 = 5.0F;
                            double d0 = MathHelper.nextDouble(this.rand, this.getPosX() - 10.0D, this.getPosX() + 10.0D);
                            double d1 = MathHelper.nextDouble(this.rand, this.getPosY() - 5.0D, this.getPosY() + 5.0D);
                            double d2 = MathHelper.nextDouble(this.rand, this.getPosZ() - 10.0D, this.getPosZ() + 10.0D);
                            this.launchWitherSkullToCoords(i + 1, d0, d1, d2, true);
                            this.idleHeadUpdates[i - 1] = 0;
                        }
                    }

                    int k1 = this.getWatchedTargetId(i);

                    if (k1 > 0)
                    {
                        Entity entity = this.world.getEntityByID(k1);

                        if (entity != null && entity.isAlive() && !(this.getDistanceSq(entity) > 900.0D) && this.canEntityBeSeen(entity))
                        {
                            if (entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.disableDamage)
                            {
                                this.updateWatchedTargetId(i, 0);
                            }
                            else
                            {
                                this.launchWitherSkullToEntity(i + 1, (LivingEntity)entity);
                                this.nextHeadUpdate[i - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                                this.idleHeadUpdates[i - 1] = 0;
                            }
                        }
                        else
                        {
                            this.updateWatchedTargetId(i, 0);
                        }
                    }
                    else
                    {
                        List<LivingEntity> list = this.world.getTargettableEntitiesWithinAABB(LivingEntity.class, ENEMY_CONDITION, this, this.getBoundingBox().grow(20.0D, 8.0D, 20.0D));

                        for (int j2 = 0; j2 < 10 && !list.isEmpty(); ++j2)
                        {
                            LivingEntity livingentity = list.get(this.rand.nextInt(list.size()));

                            if (livingentity != this && livingentity.isAlive() && this.canEntityBeSeen(livingentity))
                            {
                                if (livingentity instanceof PlayerEntity)
                                {
                                    if (!((PlayerEntity)livingentity).abilities.disableDamage)
                                    {
                                        this.updateWatchedTargetId(i, livingentity.getEntityId());
                                    }
                                }
                                else
                                {
                                    this.updateWatchedTargetId(i, livingentity.getEntityId());
                                }

                                break;
                            }

                            list.remove(livingentity);
                        }
                    }
                }
            }

            if (this.getAttackTarget() != null)
            {
                this.updateWatchedTargetId(0, this.getAttackTarget().getEntityId());
            }
            else
            {
                this.updateWatchedTargetId(0, 0);
            }

            if (this.blockBreakCounter > 0)
            {
                --this.blockBreakCounter;

                if (this.blockBreakCounter == 0 && this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING))
                {
                    int i1 = MathHelper.floor(this.getPosY());
                    int l1 = MathHelper.floor(this.getPosX());
                    int i2 = MathHelper.floor(this.getPosZ());
                    boolean flag = false;

                    for (int k2 = -1; k2 <= 1; ++k2)
                    {
                        for (int l2 = -1; l2 <= 1; ++l2)
                        {
                            for (int j = 0; j <= 3; ++j)
                            {
                                int i3 = l1 + k2;
                                int k = i1 + j;
                                int l = i2 + l2;
                                BlockPos blockpos = new BlockPos(i3, k, l);
                                BlockState blockstate = this.world.getBlockState(blockpos);

                                if (canDestroyBlock(blockstate))
                                {
                                    flag = this.world.destroyBlock(blockpos, true, this) || flag;
                                }
                            }
                        }
                    }

                    if (flag)
                    {
                        this.world.playEvent((PlayerEntity)null, 1022, this.getPosition(), 0);
                    }
                }
            }

            if (this.ticksExisted % 20 == 0)
            {
                this.heal(1.0F);
            }

            this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
        }
    }

    public static boolean canDestroyBlock(BlockState blockIn)
    {
        return !blockIn.isAir() && !BlockTags.WITHER_IMMUNE.contains(blockIn.getBlock());
    }

    /**
     * Initializes this Wither's explosion sequence and makes it invulnerable. Called immediately after spawning.
     */
    public void ignite()
    {
        this.setInvulTime(220);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    public void setMotionMultiplier(BlockState state, Vector3d motionMultiplierIn)
    {
    }

    /**
     * Add the given player to the list of players tracking this entity. For instance, a player may track a boss in
     * order to view its associated boss bar.
     */
    public void addTrackingPlayer(ServerPlayerEntity player)
    {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    /**
     * Removes the given player from the list of players tracking this entity. See {@link Entity#addTrackingPlayer} for
     * more information on tracking.
     */
    public void removeTrackingPlayer(ServerPlayerEntity player)
    {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    private double getHeadX(int head)
    {
        if (head <= 0)
        {
            return this.getPosX();
        }
        else
        {
            float f = (this.renderYawOffset + (float)(180 * (head - 1))) * ((float)Math.PI / 180F);
            float f1 = MathHelper.cos(f);
            return this.getPosX() + (double)f1 * 1.3D;
        }
    }

    private double getHeadY(int head)
    {
        return head <= 0 ? this.getPosY() + 3.0D : this.getPosY() + 2.2D;
    }

    private double getHeadZ(int head)
    {
        if (head <= 0)
        {
            return this.getPosZ();
        }
        else
        {
            float f = (this.renderYawOffset + (float)(180 * (head - 1))) * ((float)Math.PI / 180F);
            float f1 = MathHelper.sin(f);
            return this.getPosZ() + (double)f1 * 1.3D;
        }
    }

    private float rotlerp(float p_82204_1_, float p_82204_2_, float p_82204_3_)
    {
        float f = MathHelper.wrapDegrees(p_82204_2_ - p_82204_1_);

        if (f > p_82204_3_)
        {
            f = p_82204_3_;
        }

        if (f < -p_82204_3_)
        {
            f = -p_82204_3_;
        }

        return p_82204_1_ + f;
    }

    private void launchWitherSkullToEntity(int head, LivingEntity target)
    {
        this.launchWitherSkullToCoords(head, target.getPosX(), target.getPosY() + (double)target.getEyeHeight() * 0.5D, target.getPosZ(), head == 0 && this.rand.nextFloat() < 0.001F);
    }

    /**
     * Launches a Wither skull toward (par2, par4, par6)
     */
    private void launchWitherSkullToCoords(int head, double x, double y, double z, boolean invulnerable)
    {
        if (!this.isSilent())
        {
            this.world.playEvent((PlayerEntity)null, 1024, this.getPosition(), 0);
        }

        double d0 = this.getHeadX(head);
        double d1 = this.getHeadY(head);
        double d2 = this.getHeadZ(head);
        double d3 = x - d0;
        double d4 = y - d1;
        double d5 = z - d2;
        WitherSkullEntity witherskullentity = new WitherSkullEntity(this.world, this, d3, d4, d5);
        witherskullentity.setShooter(this);

        if (invulnerable)
        {
            witherskullentity.setSkullInvulnerable(true);
        }

        witherskullentity.setRawPosition(d0, d1, d2);
        this.world.addEntity(witherskullentity);
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor)
    {
        this.launchWitherSkullToEntity(0, target);
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
        else if (source != DamageSource.DROWN && !(source.getTrueSource() instanceof WitherEntity))
        {
            if (this.getInvulTime() > 0 && source != DamageSource.OUT_OF_WORLD)
            {
                return false;
            }
            else
            {
                if (this.isCharged())
                {
                    Entity entity = source.getImmediateSource();

                    if (entity instanceof AbstractArrowEntity)
                    {
                        return false;
                    }
                }

                Entity entity1 = source.getTrueSource();

                if (entity1 != null && !(entity1 instanceof PlayerEntity) && entity1 instanceof LivingEntity && ((LivingEntity)entity1).getCreatureAttribute() == this.getCreatureAttribute())
                {
                    return false;
                }
                else
                {
                    if (this.blockBreakCounter <= 0)
                    {
                        this.blockBreakCounter = 20;
                    }

                    for (int i = 0; i < this.idleHeadUpdates.length; ++i)
                    {
                        this.idleHeadUpdates[i] += 3;
                    }

                    return super.attackEntityFrom(source, amount);
                }
            }
        }
        else
        {
            return false;
        }
    }

    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn)
    {
        super.dropSpecialItems(source, looting, recentlyHitIn);
        ItemEntity itementity = this.entityDropItem(Items.NETHER_STAR);

        if (itementity != null)
        {
            itementity.setNoDespawn();
        }
    }

    /**
     * Makes the entity despawn if requirements are reached
     */
    public void checkDespawn()
    {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.isDespawnPeaceful())
        {
            this.remove();
        }
        else
        {
            this.idleTime = 0;
        }
    }

    public boolean onLivingFall(float distance, float damageMultiplier)
    {
        return false;
    }

    public boolean addPotionEffect(EffectInstance effectInstanceIn)
    {
        return false;
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 300.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.6F).createMutableAttribute(Attributes.FOLLOW_RANGE, 40.0D).createMutableAttribute(Attributes.ARMOR, 4.0D);
    }

    public float getHeadYRotation(int head)
    {
        return this.yRotationHeads[head];
    }

    public float getHeadXRotation(int head)
    {
        return this.xRotationHeads[head];
    }

    public int getInvulTime()
    {
        return this.dataManager.get(INVULNERABILITY_TIME);
    }

    public void setInvulTime(int time)
    {
        this.dataManager.set(INVULNERABILITY_TIME, time);
    }

    /**
     * Returns the target entity ID if present, or -1 if not @param par1 The target offset, should be from 0-2
     */
    public int getWatchedTargetId(int head)
    {
        return this.dataManager.get(HEAD_TARGETS.get(head));
    }

    /**
     * Updates the target entity ID
     */
    public void updateWatchedTargetId(int targetOffset, int newId)
    {
        this.dataManager.set(HEAD_TARGETS.get(targetOffset), newId);
    }

    public boolean isCharged()
    {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    public CreatureAttribute getCreatureAttribute()
    {
        return CreatureAttribute.UNDEAD;
    }

    protected boolean canBeRidden(Entity entityIn)
    {
        return false;
    }

    /**
     * Returns false if this Entity is a boss, true otherwise.
     */
    public boolean isNonBoss()
    {
        return false;
    }

    public boolean isPotionApplicable(EffectInstance potioneffectIn)
    {
        return potioneffectIn.getPotion() == Effects.WITHER ? false : super.isPotionApplicable(potioneffectIn);
    }

    class DoNothingGoal extends Goal
    {
        public DoNothingGoal()
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        public boolean shouldExecute()
        {
            return WitherEntity.this.getInvulTime() > 0;
        }
    }
}
