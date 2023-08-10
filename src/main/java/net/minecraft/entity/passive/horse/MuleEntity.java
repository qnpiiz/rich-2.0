package net.minecraft.entity.passive.horse;

import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class MuleEntity extends AbstractChestedHorseEntity
{
    public MuleEntity(EntityType <? extends MuleEntity > p_i50236_1_, World p_i50236_2_)
    {
        super(p_i50236_1_, p_i50236_2_);
    }

    protected SoundEvent getAmbientSound()
    {
        super.getAmbientSound();
        return SoundEvents.ENTITY_MULE_AMBIENT;
    }

    protected SoundEvent getAngrySound()
    {
        super.getAngrySound();
        return SoundEvents.ENTITY_MULE_ANGRY;
    }

    protected SoundEvent getDeathSound()
    {
        super.getDeathSound();
        return SoundEvents.ENTITY_MULE_DEATH;
    }

    @Nullable
    protected SoundEvent func_230274_fe_()
    {
        return SoundEvents.ENTITY_MULE_EAT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_MULE_HURT;
    }

    protected void playChestEquipSound()
    {
        this.playSound(SoundEvents.ENTITY_MULE_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
    }

    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_)
    {
        return EntityType.MULE.create(p_241840_1_);
    }
}
