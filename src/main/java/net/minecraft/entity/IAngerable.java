package net.minecraft.entity;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public interface IAngerable
{
    int getAngerTime();

    void setAngerTime(int time);

    @Nullable
    UUID getAngerTarget();

    void setAngerTarget(@Nullable UUID target);

    void func_230258_H__();

default void writeAngerNBT(CompoundNBT nbt)
    {
        nbt.putInt("AngerTime", this.getAngerTime());

        if (this.getAngerTarget() != null)
        {
            nbt.putUniqueId("AngryAt", this.getAngerTarget());
        }
    }

default void readAngerNBT(ServerWorld world, CompoundNBT nbt)
    {
        this.setAngerTime(nbt.getInt("AngerTime"));

        if (!nbt.hasUniqueId("AngryAt"))
        {
            this.setAngerTarget((UUID)null);
        }
        else
        {
            UUID uuid = nbt.getUniqueId("AngryAt");
            this.setAngerTarget(uuid);
            Entity entity = world.getEntityByUuid(uuid);

            if (entity != null)
            {
                if (entity instanceof MobEntity)
                {
                    this.setRevengeTarget((MobEntity)entity);
                }

                if (entity.getType() == EntityType.PLAYER)
                {
                    this.func_230246_e_((PlayerEntity)entity);
                }
            }
        }
    }

default void func_241359_a_(ServerWorld p_241359_1_, boolean p_241359_2_)
    {
        LivingEntity livingentity = this.getAttackTarget();
        UUID uuid = this.getAngerTarget();

        if ((livingentity == null || livingentity.getShouldBeDead()) && uuid != null && p_241359_1_.getEntityByUuid(uuid) instanceof MobEntity)
        {
            this.func_241356_K__();
        }
        else
        {
            if (livingentity != null && !Objects.equals(uuid, livingentity.getUniqueID()))
            {
                this.setAngerTarget(livingentity.getUniqueID());
                this.func_230258_H__();
            }

            if (this.getAngerTime() > 0 && (livingentity == null || livingentity.getType() != EntityType.PLAYER || !p_241359_2_))
            {
                this.setAngerTime(this.getAngerTime() - 1);

                if (this.getAngerTime() == 0)
                {
                    this.func_241356_K__();
                }
            }
        }
    }

default boolean func_233680_b_(LivingEntity p_233680_1_)
    {
        if (!EntityPredicates.CAN_HOSTILE_AI_TARGET.test(p_233680_1_))
        {
            return false;
        }
        else
        {
            return p_233680_1_.getType() == EntityType.PLAYER && this.func_241357_a_(p_233680_1_.world) ? true : p_233680_1_.getUniqueID().equals(this.getAngerTarget());
        }
    }

default boolean func_241357_a_(World p_241357_1_)
    {
        return p_241357_1_.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER) && this.func_233678_J__() && this.getAngerTarget() == null;
    }

default boolean func_233678_J__()
    {
        return this.getAngerTime() > 0;
    }

default void func_233681_b_(PlayerEntity p_233681_1_)
    {
        if (p_233681_1_.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS))
        {
            if (p_233681_1_.getUniqueID().equals(this.getAngerTarget()))
            {
                this.func_241356_K__();
            }
        }
    }

default void func_241355_J__()
    {
        this.func_241356_K__();
        this.func_230258_H__();
    }

default void func_241356_K__()
    {
        this.setRevengeTarget((LivingEntity)null);
        this.setAngerTarget((UUID)null);
        this.setAttackTarget((LivingEntity)null);
        this.setAngerTime(0);
    }

    /**
     * Hint to AI tasks that we were attacked by the passed EntityLivingBase and should retaliate. Is not guaranteed to
     * change our actual active target (for example if we are currently busy attacking someone else)
     */
    void setRevengeTarget(@Nullable LivingEntity livingBase);

    void func_230246_e_(@Nullable PlayerEntity p_230246_1_);

    /**
     * Sets the active target the Task system uses for tracking
     */
    void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn);

    @Nullable

    /**
     * Gets the active target the Task system uses for tracking
     */
    LivingEntity getAttackTarget();
}
