package net.minecraft.entity.monster;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.AttackTargetTask;
import net.minecraft.entity.ai.brain.task.DummyTask;
import net.minecraft.entity.ai.brain.task.FindNewAttackTargetTask;
import net.minecraft.entity.ai.brain.task.FirstShuffledTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.LookAtEntityTask;
import net.minecraft.entity.ai.brain.task.LookTask;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.ai.brain.task.RunSometimesTask;
import net.minecraft.entity.ai.brain.task.SupplementedTask;
import net.minecraft.entity.ai.brain.task.WalkRandomlyTask;
import net.minecraft.entity.ai.brain.task.WalkToTargetTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsLookTargetTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ZoglinEntity extends MonsterEntity implements IMob, IFlinging
{
    private static final DataParameter<Boolean> field_234327_d_ = EntityDataManager.createKey(ZoglinEntity.class, DataSerializers.BOOLEAN);
    private int field_234325_bu_;
    protected static final ImmutableList <? extends SensorType <? extends Sensor <? super ZoglinEntity >>> field_234324_b_ = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS);
    protected static final ImmutableList <? extends MemoryModuleType<? >> field_234326_c_ = ImmutableList.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN);

    public ZoglinEntity(EntityType <? extends ZoglinEntity > p_i231566_1_, World p_i231566_2_)
    {
        super(p_i231566_1_, p_i231566_2_);
        this.experienceValue = 5;
    }

    protected Brain.BrainCodec<ZoglinEntity> getBrainCodec()
    {
        return Brain.createCodec(field_234326_c_, field_234324_b_);
    }

    protected Brain<?> createBrain(Dynamic<?> dynamicIn)
    {
        Brain<ZoglinEntity> brain = this.getBrainCodec().deserialize(dynamicIn);
        func_234328_a_(brain);
        func_234329_b_(brain);
        func_234330_c_(brain);
        brain.setDefaultActivities(ImmutableSet.of(Activity.CORE));
        brain.setFallbackActivity(Activity.IDLE);
        brain.switchToFallbackActivity();
        return brain;
    }

    private static void func_234328_a_(Brain<ZoglinEntity> p_234328_0_)
    {
        p_234328_0_.registerActivity(Activity.CORE, 0, ImmutableList.of(new LookTask(45, 90), new WalkToTargetTask()));
    }

    private static void func_234329_b_(Brain<ZoglinEntity> p_234329_0_)
    {
        p_234329_0_.registerActivity(Activity.IDLE, 10, ImmutableList. < net.minecraft.entity.ai.brain.task.Task <? super ZoglinEntity >> of(new ForgetAttackTargetTask<>(ZoglinEntity::func_234335_eQ_), new RunSometimesTask<>(new LookAtEntityTask(8.0F), RangedInteger.createRangedInteger(30, 60)), new FirstShuffledTask<>(ImmutableList.of(Pair.of(new WalkRandomlyTask(0.4F), 2), Pair.of(new WalkTowardsLookTargetTask(0.4F, 3), 2), Pair.of(new DummyTask(30, 60), 1)))));
    }

    private static void func_234330_c_(Brain<ZoglinEntity> p_234330_0_)
    {
        p_234330_0_.registerActivity(Activity.FIGHT, 10, ImmutableList. < net.minecraft.entity.ai.brain.task.Task <? super ZoglinEntity >> of(new MoveToTargetTask(1.0F), new SupplementedTask<>(ZoglinEntity::func_234331_eI_, new AttackTargetTask(40)), new SupplementedTask<>(ZoglinEntity::isChild, new AttackTargetTask(15)), new FindNewAttackTargetTask<>()), MemoryModuleType.ATTACK_TARGET);
    }

    private Optional <? extends LivingEntity > func_234335_eQ_()
    {
        return this.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS).orElse(ImmutableList.of()).stream().filter(ZoglinEntity::func_234337_j_).findFirst();
    }

    private static boolean func_234337_j_(LivingEntity p_234337_0_)
    {
        EntityType<?> entitytype = p_234337_0_.getType();
        return entitytype != EntityType.ZOGLIN && entitytype != EntityType.CREEPER && EntityPredicates.CAN_HOSTILE_AI_TARGET.test(p_234337_0_);
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(field_234327_d_, false);
    }

    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);

        if (field_234327_d_.equals(key))
        {
            this.recalculateSize();
        }
    }

    public static AttributeModifierMap.MutableAttribute func_234339_m_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 40.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.3F).createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, (double)0.6F).createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 6.0D);
    }

    public boolean func_234331_eI_()
    {
        return !this.isChild();
    }

    public boolean attackEntityAsMob(Entity entityIn)
    {
        if (!(entityIn instanceof LivingEntity))
        {
            return false;
        }
        else
        {
            this.field_234325_bu_ = 10;
            this.world.setEntityState(this, (byte)4);
            this.playSound(SoundEvents.ENTITY_ZOGLIN_ATTACK, 1.0F, this.getSoundPitch());
            return IFlinging.func_234403_a_(this, (LivingEntity)entityIn);
        }
    }

    public boolean canBeLeashedTo(PlayerEntity player)
    {
        return !this.getLeashed();
    }

    protected void constructKnockBackVector(LivingEntity entityIn)
    {
        if (!this.isChild())
        {
            IFlinging.func_234404_b_(this, entityIn);
        }
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.getHeight() - (this.isChild() ? 0.2D : 0.15D);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        boolean flag = super.attackEntityFrom(source, amount);

        if (this.world.isRemote)
        {
            return false;
        }
        else if (flag && source.getTrueSource() instanceof LivingEntity)
        {
            LivingEntity livingentity = (LivingEntity)source.getTrueSource();

            if (EntityPredicates.CAN_HOSTILE_AI_TARGET.test(livingentity) && !BrainUtil.isTargetWithinDistance(this, livingentity, 4.0D))
            {
                this.func_234338_k_(livingentity);
            }

            return flag;
        }
        else
        {
            return flag;
        }
    }

    private void func_234338_k_(LivingEntity p_234338_1_)
    {
        this.brain.removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        this.brain.replaceMemory(MemoryModuleType.ATTACK_TARGET, p_234338_1_, 200L);
    }

    public Brain<ZoglinEntity> getBrain()
    {
        return (Brain<ZoglinEntity>)super.getBrain();
    }

    protected void func_234332_eJ_()
    {
        Activity activity = this.brain.getTemporaryActivity().orElse((Activity)null);
        this.brain.switchActivities(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
        Activity activity1 = this.brain.getTemporaryActivity().orElse((Activity)null);

        if (activity1 == Activity.FIGHT && activity != Activity.FIGHT)
        {
            this.func_234334_eN_();
        }

        this.setAggroed(this.brain.hasMemory(MemoryModuleType.ATTACK_TARGET));
    }

    protected void updateAITasks()
    {
        this.world.getProfiler().startSection("zoglinBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().endSection();
        this.func_234332_eJ_();
    }

    /**
     * Set whether this zombie is a child.
     */
    public void setChild(boolean childZombie)
    {
        this.getDataManager().set(field_234327_d_, childZombie);

        if (!this.world.isRemote && childZombie)
        {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.5D);
        }
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild()
    {
        return this.getDataManager().get(field_234327_d_);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void livingTick()
    {
        if (this.field_234325_bu_ > 0)
        {
            --this.field_234325_bu_;
        }

        super.livingTick();
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    public void handleStatusUpdate(byte id)
    {
        if (id == 4)
        {
            this.field_234325_bu_ = 10;
            this.playSound(SoundEvents.ENTITY_ZOGLIN_ATTACK, 1.0F, this.getSoundPitch());
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    public int func_230290_eL_()
    {
        return this.field_234325_bu_;
    }

    protected SoundEvent getAmbientSound()
    {
        if (this.world.isRemote)
        {
            return null;
        }
        else
        {
            return this.brain.hasMemory(MemoryModuleType.ATTACK_TARGET) ? SoundEvents.ENTITY_ZOGLIN_ANGRY : SoundEvents.ENTITY_ZOGLIN_AMBIENT;
        }
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_ZOGLIN_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_ZOGLIN_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        this.playSound(SoundEvents.ENTITY_ZOGLIN_STEP, 0.15F, 1.0F);
    }

    protected void func_234334_eN_()
    {
        this.playSound(SoundEvents.ENTITY_ZOGLIN_ANGRY, 1.0F, this.getSoundPitch());
    }

    protected void sendDebugPackets()
    {
        super.sendDebugPackets();
        DebugPacketSender.sendLivingEntity(this);
    }

    public CreatureAttribute getCreatureAttribute()
    {
        return CreatureAttribute.UNDEAD;
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);

        if (this.isChild())
        {
            compound.putBoolean("IsBaby", true);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);

        if (compound.getBoolean("IsBaby"))
        {
            this.setChild(true);
        }
    }
}
