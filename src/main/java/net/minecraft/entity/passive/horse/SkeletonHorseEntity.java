package net.minecraft.entity.passive.horse;

import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.TriggerSkeletonTrapGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SkeletonHorseEntity extends AbstractHorseEntity
{
    private final TriggerSkeletonTrapGoal skeletonTrapAI = new TriggerSkeletonTrapGoal(this);
    private boolean skeletonTrap;
    private int skeletonTrapTime;

    public SkeletonHorseEntity(EntityType <? extends SkeletonHorseEntity > p_i50235_1_, World p_i50235_2_)
    {
        super(p_i50235_1_, p_i50235_2_);
    }

    public static AttributeModifierMap.MutableAttribute func_234250_eJ_()
    {
        return func_234237_fg_().createMutableAttribute(Attributes.MAX_HEALTH, 15.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.2F);
    }

    protected void func_230273_eI_()
    {
        this.getAttribute(Attributes.HORSE_JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
    }

    protected void initExtraAI()
    {
    }

    protected SoundEvent getAmbientSound()
    {
        super.getAmbientSound();
        return this.areEyesInFluid(FluidTags.WATER) ? SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT_WATER : SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT;
    }

    protected SoundEvent getDeathSound()
    {
        super.getDeathSound();
        return SoundEvents.ENTITY_SKELETON_HORSE_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_SKELETON_HORSE_HURT;
    }

    protected SoundEvent getSwimSound()
    {
        if (this.onGround)
        {
            if (!this.isBeingRidden())
            {
                return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
            }

            ++this.gallopTime;

            if (this.gallopTime > 5 && this.gallopTime % 3 == 0)
            {
                return SoundEvents.ENTITY_SKELETON_HORSE_GALLOP_WATER;
            }

            if (this.gallopTime <= 5)
            {
                return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
            }
        }

        return SoundEvents.ENTITY_SKELETON_HORSE_SWIM;
    }

    protected void playSwimSound(float volume)
    {
        if (this.onGround)
        {
            super.playSwimSound(0.3F);
        }
        else
        {
            super.playSwimSound(Math.min(0.1F, volume * 25.0F));
        }
    }

    protected void playJumpSound()
    {
        if (this.isInWater())
        {
            this.playSound(SoundEvents.ENTITY_SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
        }
        else
        {
            super.playJumpSound();
        }
    }

    public CreatureAttribute getCreatureAttribute()
    {
        return CreatureAttribute.UNDEAD;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return super.getMountedYOffset() - 0.1875D;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void livingTick()
    {
        super.livingTick();

        if (this.isTrap() && this.skeletonTrapTime++ >= 18000)
        {
            this.remove();
        }
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putBoolean("SkeletonTrap", this.isTrap());
        compound.putInt("SkeletonTrapTime", this.skeletonTrapTime);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setTrap(compound.getBoolean("SkeletonTrap"));
        this.skeletonTrapTime = compound.getInt("SkeletonTrapTime");
    }

    public boolean canBeRiddenInWater()
    {
        return true;
    }

    protected float getWaterSlowDown()
    {
        return 0.96F;
    }

    public boolean isTrap()
    {
        return this.skeletonTrap;
    }

    public void setTrap(boolean trap)
    {
        if (trap != this.skeletonTrap)
        {
            this.skeletonTrap = trap;

            if (trap)
            {
                this.goalSelector.addGoal(1, this.skeletonTrapAI);
            }
            else
            {
                this.goalSelector.removeGoal(this.skeletonTrapAI);
            }
        }
    }

    @Nullable
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_)
    {
        return EntityType.SKELETON_HORSE.create(p_241840_1_);
    }

    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_)
    {
        ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);

        if (!this.isTame())
        {
            return ActionResultType.PASS;
        }
        else if (this.isChild())
        {
            return super.func_230254_b_(p_230254_1_, p_230254_2_);
        }
        else if (p_230254_1_.isSecondaryUseActive())
        {
            this.openGUI(p_230254_1_);
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        else if (this.isBeingRidden())
        {
            return super.func_230254_b_(p_230254_1_, p_230254_2_);
        }
        else
        {
            if (!itemstack.isEmpty())
            {
                if (itemstack.getItem() == Items.SADDLE && !this.isHorseSaddled())
                {
                    this.openGUI(p_230254_1_);
                    return ActionResultType.func_233537_a_(this.world.isRemote);
                }

                ActionResultType actionresulttype = itemstack.interactWithEntity(p_230254_1_, this, p_230254_2_);

                if (actionresulttype.isSuccessOrConsume())
                {
                    return actionresulttype;
                }
            }

            this.mountTo(p_230254_1_);
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
    }
}
