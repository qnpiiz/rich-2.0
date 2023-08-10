package net.minecraft.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class VexEntity extends MonsterEntity
{
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(VexEntity.class, DataSerializers.BYTE);
    private MobEntity owner;
    @Nullable
    private BlockPos boundOrigin;
    private boolean limitedLifespan;
    private int limitedLifeTicks;

    public VexEntity(EntityType <? extends VexEntity > p_i50190_1_, World p_i50190_2_)
    {
        super(p_i50190_1_, p_i50190_2_);
        this.moveController = new VexEntity.MoveHelperController(this);
        this.experienceValue = 3;
    }

    public void move(MoverType typeIn, Vector3d pos)
    {
        super.move(typeIn, pos);
        this.doBlockCollisions();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        this.noClip = true;
        super.tick();
        this.noClip = false;
        this.setNoGravity(true);

        if (this.limitedLifespan && --this.limitedLifeTicks <= 0)
        {
            this.limitedLifeTicks = 20;
            this.attackEntityFrom(DamageSource.STARVE, 1.0F);
        }
    }

    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(4, new VexEntity.ChargeAttackGoal());
        this.goalSelector.addGoal(8, new VexEntity.MoveRandomGoal());
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
        this.targetSelector.addGoal(2, new VexEntity.CopyOwnerTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static AttributeModifierMap.MutableAttribute func_234321_m_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 14.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(VEX_FLAGS, (byte)0);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);

        if (compound.contains("BoundX"))
        {
            this.boundOrigin = new BlockPos(compound.getInt("BoundX"), compound.getInt("BoundY"), compound.getInt("BoundZ"));
        }

        if (compound.contains("LifeTicks"))
        {
            this.setLimitedLife(compound.getInt("LifeTicks"));
        }
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);

        if (this.boundOrigin != null)
        {
            compound.putInt("BoundX", this.boundOrigin.getX());
            compound.putInt("BoundY", this.boundOrigin.getY());
            compound.putInt("BoundZ", this.boundOrigin.getZ());
        }

        if (this.limitedLifespan)
        {
            compound.putInt("LifeTicks", this.limitedLifeTicks);
        }
    }

    public MobEntity getOwner()
    {
        return this.owner;
    }

    @Nullable
    public BlockPos getBoundOrigin()
    {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos boundOriginIn)
    {
        this.boundOrigin = boundOriginIn;
    }

    private boolean getVexFlag(int mask)
    {
        int i = this.dataManager.get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value)
    {
        int i = this.dataManager.get(VEX_FLAGS);

        if (value)
        {
            i = i | mask;
        }
        else
        {
            i = i & ~mask;
        }

        this.dataManager.set(VEX_FLAGS, (byte)(i & 255));
    }

    public boolean isCharging()
    {
        return this.getVexFlag(1);
    }

    public void setCharging(boolean charging)
    {
        this.setVexFlag(1, charging);
    }

    public void setOwner(MobEntity ownerIn)
    {
        this.owner = ownerIn;
    }

    public void setLimitedLife(int limitedLifeTicksIn)
    {
        this.limitedLifespan = true;
        this.limitedLifeTicks = limitedLifeTicksIn;
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_VEX_AMBIENT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_VEX_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_VEX_HURT;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness()
    {
        return 1.0F;
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        this.setEquipmentBasedOnDifficulty(difficultyIn);
        this.setEnchantmentBasedOnDifficulty(difficultyIn);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
    {
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(EquipmentSlotType.MAINHAND, 0.0F);
    }

    class ChargeAttackGoal extends Goal
    {
        public ChargeAttackGoal()
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean shouldExecute()
        {
            if (VexEntity.this.getAttackTarget() != null && !VexEntity.this.getMoveHelper().isUpdating() && VexEntity.this.rand.nextInt(7) == 0)
            {
                return VexEntity.this.getDistanceSq(VexEntity.this.getAttackTarget()) > 4.0D;
            }
            else
            {
                return false;
            }
        }

        public boolean shouldContinueExecuting()
        {
            return VexEntity.this.getMoveHelper().isUpdating() && VexEntity.this.isCharging() && VexEntity.this.getAttackTarget() != null && VexEntity.this.getAttackTarget().isAlive();
        }

        public void startExecuting()
        {
            LivingEntity livingentity = VexEntity.this.getAttackTarget();
            Vector3d vector3d = livingentity.getEyePosition(1.0F);
            VexEntity.this.moveController.setMoveTo(vector3d.x, vector3d.y, vector3d.z, 1.0D);
            VexEntity.this.setCharging(true);
            VexEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
        }

        public void resetTask()
        {
            VexEntity.this.setCharging(false);
        }

        public void tick()
        {
            LivingEntity livingentity = VexEntity.this.getAttackTarget();

            if (VexEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox()))
            {
                VexEntity.this.attackEntityAsMob(livingentity);
                VexEntity.this.setCharging(false);
            }
            else
            {
                double d0 = VexEntity.this.getDistanceSq(livingentity);

                if (d0 < 9.0D)
                {
                    Vector3d vector3d = livingentity.getEyePosition(1.0F);
                    VexEntity.this.moveController.setMoveTo(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                }
            }
        }
    }

    class CopyOwnerTargetGoal extends TargetGoal
    {
        private final EntityPredicate field_220803_b = (new EntityPredicate()).setLineOfSiteRequired().setUseInvisibilityCheck();

        public CopyOwnerTargetGoal(CreatureEntity creature)
        {
            super(creature, false);
        }

        public boolean shouldExecute()
        {
            return VexEntity.this.owner != null && VexEntity.this.owner.getAttackTarget() != null && this.isSuitableTarget(VexEntity.this.owner.getAttackTarget(), this.field_220803_b);
        }

        public void startExecuting()
        {
            VexEntity.this.setAttackTarget(VexEntity.this.owner.getAttackTarget());
            super.startExecuting();
        }
    }

    class MoveHelperController extends MovementController
    {
        public MoveHelperController(VexEntity vex)
        {
            super(vex);
        }

        public void tick()
        {
            if (this.action == MovementController.Action.MOVE_TO)
            {
                Vector3d vector3d = new Vector3d(this.posX - VexEntity.this.getPosX(), this.posY - VexEntity.this.getPosY(), this.posZ - VexEntity.this.getPosZ());
                double d0 = vector3d.length();

                if (d0 < VexEntity.this.getBoundingBox().getAverageEdgeLength())
                {
                    this.action = MovementController.Action.WAIT;
                    VexEntity.this.setMotion(VexEntity.this.getMotion().scale(0.5D));
                }
                else
                {
                    VexEntity.this.setMotion(VexEntity.this.getMotion().add(vector3d.scale(this.speed * 0.05D / d0)));

                    if (VexEntity.this.getAttackTarget() == null)
                    {
                        Vector3d vector3d1 = VexEntity.this.getMotion();
                        VexEntity.this.rotationYaw = -((float)MathHelper.atan2(vector3d1.x, vector3d1.z)) * (180F / (float)Math.PI);
                        VexEntity.this.renderYawOffset = VexEntity.this.rotationYaw;
                    }
                    else
                    {
                        double d2 = VexEntity.this.getAttackTarget().getPosX() - VexEntity.this.getPosX();
                        double d1 = VexEntity.this.getAttackTarget().getPosZ() - VexEntity.this.getPosZ();
                        VexEntity.this.rotationYaw = -((float)MathHelper.atan2(d2, d1)) * (180F / (float)Math.PI);
                        VexEntity.this.renderYawOffset = VexEntity.this.rotationYaw;
                    }
                }
            }
        }
    }

    class MoveRandomGoal extends Goal
    {
        public MoveRandomGoal()
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean shouldExecute()
        {
            return !VexEntity.this.getMoveHelper().isUpdating() && VexEntity.this.rand.nextInt(7) == 0;
        }

        public boolean shouldContinueExecuting()
        {
            return false;
        }

        public void tick()
        {
            BlockPos blockpos = VexEntity.this.getBoundOrigin();

            if (blockpos == null)
            {
                blockpos = VexEntity.this.getPosition();
            }

            for (int i = 0; i < 3; ++i)
            {
                BlockPos blockpos1 = blockpos.add(VexEntity.this.rand.nextInt(15) - 7, VexEntity.this.rand.nextInt(11) - 5, VexEntity.this.rand.nextInt(15) - 7);

                if (VexEntity.this.world.isAirBlock(blockpos1))
                {
                    VexEntity.this.moveController.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);

                    if (VexEntity.this.getAttackTarget() == null)
                    {
                        VexEntity.this.getLookController().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }

                    break;
                }
            }
        }
    }
}
