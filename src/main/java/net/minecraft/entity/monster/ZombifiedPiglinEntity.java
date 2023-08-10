package net.minecraft.entity.monster;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ZombifiedPiglinEntity extends ZombieEntity implements IAngerable
{
    private static final UUID field_234344_b_ = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier field_234349_c_ = new AttributeModifier(field_234344_b_, "Attacking speed boost", 0.05D, AttributeModifier.Operation.ADDITION);
    private static final RangedInteger field_234350_d_ = TickRangeConverter.convertRange(0, 1);
    private int field_234345_bu_;
    private static final RangedInteger field_234346_bv_ = TickRangeConverter.convertRange(20, 39);
    private int field_234347_bw_;
    private UUID field_234348_bx_;
    private static final RangedInteger field_241403_bz_ = TickRangeConverter.convertRange(4, 6);
    private int field_241401_bA_;

    public ZombifiedPiglinEntity(EntityType <? extends ZombifiedPiglinEntity > p_i231568_1_, World p_i231568_2_)
    {
        super(p_i231568_1_, p_i231568_2_);
        this.setPathPriority(PathNodeType.LAVA, 8.0F);
    }

    public void setAngerTarget(@Nullable UUID target)
    {
        this.field_234348_bx_ = target;
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return this.isChild() ? -0.05D : -0.45D;
    }

    protected void applyEntityAI()
    {
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
        this.targetSelector.addGoal(3, new ResetAngerGoal<>(this, true));
    }

    public static AttributeModifierMap.MutableAttribute func_234352_eU_()
    {
        return ZombieEntity.func_234342_eQ_().createMutableAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS, 0.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.23F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    protected boolean shouldDrown()
    {
        return false;
    }

    protected void updateAITasks()
    {
        ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);

        if (this.func_233678_J__())
        {
            if (!this.isChild() && !modifiableattributeinstance.hasModifier(field_234349_c_))
            {
                modifiableattributeinstance.applyNonPersistentModifier(field_234349_c_);
            }

            this.func_241409_eY_();
        }
        else if (modifiableattributeinstance.hasModifier(field_234349_c_))
        {
            modifiableattributeinstance.removeModifier(field_234349_c_);
        }

        this.func_241359_a_((ServerWorld)this.world, true);

        if (this.getAttackTarget() != null)
        {
            this.func_241410_eZ_();
        }

        if (this.func_233678_J__())
        {
            this.recentlyHit = this.ticksExisted;
        }

        super.updateAITasks();
    }

    private void func_241409_eY_()
    {
        if (this.field_234345_bu_ > 0)
        {
            --this.field_234345_bu_;

            if (this.field_234345_bu_ == 0)
            {
                this.func_234353_eV_();
            }
        }
    }

    private void func_241410_eZ_()
    {
        if (this.field_241401_bA_ > 0)
        {
            --this.field_241401_bA_;
        }
        else
        {
            if (this.getEntitySenses().canSee(this.getAttackTarget()))
            {
                this.func_241411_fa_();
            }

            this.field_241401_bA_ = field_241403_bz_.getRandomWithinRange(this.rand);
        }
    }

    private void func_241411_fa_()
    {
        double d0 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
        AxisAlignedBB axisalignedbb = AxisAlignedBB.fromVector(this.getPositionVec()).grow(d0, 10.0D, d0);
        this.world.getLoadedEntitiesWithinAABB(ZombifiedPiglinEntity.class, axisalignedbb).stream().filter((p_241408_1_) ->
        {
            return p_241408_1_ != this;
        }).filter((p_241407_0_) ->
        {
            return p_241407_0_.getAttackTarget() == null;
        }).filter((p_241406_1_) ->
        {
            return !p_241406_1_.isOnSameTeam(this.getAttackTarget());
        }).forEach((p_241405_1_) ->
        {
            p_241405_1_.setAttackTarget(this.getAttackTarget());
        });
    }

    private void func_234353_eV_()
    {
        this.playSound(SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0F, this.getSoundPitch() * 1.8F);
    }

    /**
     * Sets the active target the Task system uses for tracking
     */
    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn)
    {
        if (this.getAttackTarget() == null && entitylivingbaseIn != null)
        {
            this.field_234345_bu_ = field_234350_d_.getRandomWithinRange(this.rand);
            this.field_241401_bA_ = field_241403_bz_.getRandomWithinRange(this.rand);
        }

        if (entitylivingbaseIn instanceof PlayerEntity)
        {
            this.func_230246_e_((PlayerEntity)entitylivingbaseIn);
        }

        super.setAttackTarget(entitylivingbaseIn);
    }

    public void func_230258_H__()
    {
        this.setAngerTime(field_234346_bv_.getRandomWithinRange(this.rand));
    }

    public static boolean func_234351_b_(EntityType<ZombifiedPiglinEntity> p_234351_0_, IWorld p_234351_1_, SpawnReason p_234351_2_, BlockPos p_234351_3_, Random p_234351_4_)
    {
        return p_234351_1_.getDifficulty() != Difficulty.PEACEFUL && p_234351_1_.getBlockState(p_234351_3_.down()).getBlock() != Blocks.NETHER_WART_BLOCK;
    }

    public boolean isNotColliding(IWorldReader worldIn)
    {
        return worldIn.checkNoEntityCollision(this) && !worldIn.containsAnyLiquid(this.getBoundingBox());
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        this.writeAngerNBT(compound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.readAngerNBT((ServerWorld)this.world, compound);
    }

    public void setAngerTime(int time)
    {
        this.field_234347_bw_ = time;
    }

    public int getAngerTime()
    {
        return this.field_234347_bw_;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return this.isInvulnerableTo(source) ? false : super.attackEntityFrom(source, amount);
    }

    protected SoundEvent getAmbientSound()
    {
        return this.func_233678_J__() ? SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY : SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_DEATH;
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
    {
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
    }

    protected ItemStack getSkullDrop()
    {
        return ItemStack.EMPTY;
    }

    protected void func_230291_eT_()
    {
        this.getAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0.0D);
    }

    public UUID getAngerTarget()
    {
        return this.field_234348_bx_;
    }

    public boolean func_230292_f_(PlayerEntity p_230292_1_)
    {
        return this.func_233680_b_(p_230292_1_);
    }
}
