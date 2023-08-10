package net.minecraft.entity.passive.horse;

import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ZombieHorseEntity extends AbstractHorseEntity
{
    public ZombieHorseEntity(EntityType <? extends ZombieHorseEntity > p_i50233_1_, World p_i50233_2_)
    {
        super(p_i50233_1_, p_i50233_2_);
    }

    public static AttributeModifierMap.MutableAttribute func_234256_eJ_()
    {
        return func_234237_fg_().createMutableAttribute(Attributes.MAX_HEALTH, 15.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.2F);
    }

    protected void func_230273_eI_()
    {
        this.getAttribute(Attributes.HORSE_JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
    }

    public CreatureAttribute getCreatureAttribute()
    {
        return CreatureAttribute.UNDEAD;
    }

    protected SoundEvent getAmbientSound()
    {
        super.getAmbientSound();
        return SoundEvents.ENTITY_ZOMBIE_HORSE_AMBIENT;
    }

    protected SoundEvent getDeathSound()
    {
        super.getDeathSound();
        return SoundEvents.ENTITY_ZOMBIE_HORSE_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_ZOMBIE_HORSE_HURT;
    }

    @Nullable
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_)
    {
        return EntityType.ZOMBIE_HORSE.create(p_241840_1_);
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

    protected void initExtraAI()
    {
    }
}
