package net.minecraft.entity.passive.horse;

import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DonkeyEntity extends AbstractChestedHorseEntity
{
    public DonkeyEntity(EntityType <? extends DonkeyEntity > p_i50239_1_, World world)
    {
        super(p_i50239_1_, world);
    }

    protected SoundEvent getAmbientSound()
    {
        super.getAmbientSound();
        return SoundEvents.ENTITY_DONKEY_AMBIENT;
    }

    protected SoundEvent getAngrySound()
    {
        super.getAngrySound();
        return SoundEvents.ENTITY_DONKEY_ANGRY;
    }

    protected SoundEvent getDeathSound()
    {
        super.getDeathSound();
        return SoundEvents.ENTITY_DONKEY_DEATH;
    }

    @Nullable
    protected SoundEvent func_230274_fe_()
    {
        return SoundEvents.ENTITY_DONKEY_EAT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_DONKEY_HURT;
    }

    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
    public boolean canMateWith(AnimalEntity otherAnimal)
    {
        if (otherAnimal == this)
        {
            return false;
        }
        else if (!(otherAnimal instanceof DonkeyEntity) && !(otherAnimal instanceof HorseEntity))
        {
            return false;
        }
        else
        {
            return this.canMate() && ((AbstractHorseEntity)otherAnimal).canMate();
        }
    }

    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_)
    {
        EntityType <? extends AbstractHorseEntity > entitytype = p_241840_2_ instanceof HorseEntity ? EntityType.MULE : EntityType.DONKEY;
        AbstractHorseEntity abstracthorseentity = entitytype.create(p_241840_1_);
        this.setOffspringAttributes(p_241840_2_, abstracthorseentity);
        return abstracthorseentity;
    }
}
