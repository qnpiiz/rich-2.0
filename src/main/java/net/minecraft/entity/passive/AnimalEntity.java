package net.minecraft.entity.passive;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AnimalEntity extends AgeableEntity
{
    private int inLove;
    private UUID playerInLove;

    protected AnimalEntity(EntityType <? extends AnimalEntity > type, World worldIn)
    {
        super(type, worldIn);
        this.setPathPriority(PathNodeType.DANGER_FIRE, 16.0F);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, -1.0F);
    }

    protected void updateAITasks()
    {
        if (this.getGrowingAge() != 0)
        {
            this.inLove = 0;
        }

        super.updateAITasks();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void livingTick()
    {
        super.livingTick();

        if (this.getGrowingAge() != 0)
        {
            this.inLove = 0;
        }

        if (this.inLove > 0)
        {
            --this.inLove;

            if (this.inLove % 10 == 0)
            {
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.HEART, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), d0, d1, d2);
            }
        }
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
        else
        {
            this.inLove = 0;
            return super.attackEntityFrom(source, amount);
        }
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn)
    {
        return worldIn.getBlockState(pos.down()).isIn(Blocks.GRASS_BLOCK) ? 10.0F : worldIn.getBrightness(pos) - 0.5F;
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putInt("InLove", this.inLove);

        if (this.playerInLove != null)
        {
            compound.putUniqueId("LoveCause", this.playerInLove);
        }
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return 0.14D;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.inLove = compound.getInt("InLove");
        this.playerInLove = compound.hasUniqueId("LoveCause") ? compound.getUniqueId("LoveCause") : null;
    }

    /**
     * Static predicate for determining whether or not an animal can spawn at the provided location.
     *  
     * @param animal The animal entity to be spawned
     */
    public static boolean canAnimalSpawn(EntityType <? extends AnimalEntity > animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random)
    {
        return worldIn.getBlockState(pos.down()).isIn(Blocks.GRASS_BLOCK) && worldIn.getLightSubtracted(pos, 0) > 8;
    }

    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    public int getTalkInterval()
    {
        return 120;
    }

    public boolean canDespawn(double distanceToClosestPlayer)
    {
        return false;
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(PlayerEntity player)
    {
        return 1 + this.world.rand.nextInt(3);
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    public boolean isBreedingItem(ItemStack stack)
    {
        return stack.getItem() == Items.WHEAT;
    }

    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_)
    {
        ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);

        if (this.isBreedingItem(itemstack))
        {
            int i = this.getGrowingAge();

            if (!this.world.isRemote && i == 0 && this.canFallInLove())
            {
                this.consumeItemFromStack(p_230254_1_, itemstack);
                this.setInLove(p_230254_1_);
                return ActionResultType.SUCCESS;
            }

            if (this.isChild())
            {
                this.consumeItemFromStack(p_230254_1_, itemstack);
                this.ageUp((int)((float)(-i / 20) * 0.1F), true);
                return ActionResultType.func_233537_a_(this.world.isRemote);
            }

            if (this.world.isRemote)
            {
                return ActionResultType.CONSUME;
            }
        }

        return super.func_230254_b_(p_230254_1_, p_230254_2_);
    }

    /**
     * Decreases ItemStack size by one
     */
    protected void consumeItemFromStack(PlayerEntity player, ItemStack stack)
    {
        if (!player.abilities.isCreativeMode)
        {
            stack.shrink(1);
        }
    }

    public boolean canFallInLove()
    {
        return this.inLove <= 0;
    }

    public void setInLove(@Nullable PlayerEntity player)
    {
        this.inLove = 600;

        if (player != null)
        {
            this.playerInLove = player.getUniqueID();
        }

        this.world.setEntityState(this, (byte)18);
    }

    public void setInLove(int ticks)
    {
        this.inLove = ticks;
    }

    public int func_234178_eO_()
    {
        return this.inLove;
    }

    @Nullable
    public ServerPlayerEntity getLoveCause()
    {
        if (this.playerInLove == null)
        {
            return null;
        }
        else
        {
            PlayerEntity playerentity = this.world.getPlayerByUuid(this.playerInLove);
            return playerentity instanceof ServerPlayerEntity ? (ServerPlayerEntity)playerentity : null;
        }
    }

    /**
     * Returns if the entity is currently in 'love mode'.
     */
    public boolean isInLove()
    {
        return this.inLove > 0;
    }

    public void resetInLove()
    {
        this.inLove = 0;
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
        else if (otherAnimal.getClass() != this.getClass())
        {
            return false;
        }
        else
        {
            return this.isInLove() && otherAnimal.isInLove();
        }
    }

    public void func_234177_a_(ServerWorld p_234177_1_, AnimalEntity p_234177_2_)
    {
        AgeableEntity ageableentity = this.func_241840_a(p_234177_1_, p_234177_2_);

        if (ageableentity != null)
        {
            ServerPlayerEntity serverplayerentity = this.getLoveCause();

            if (serverplayerentity == null && p_234177_2_.getLoveCause() != null)
            {
                serverplayerentity = p_234177_2_.getLoveCause();
            }

            if (serverplayerentity != null)
            {
                serverplayerentity.addStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this, p_234177_2_, ageableentity);
            }

            this.setGrowingAge(6000);
            p_234177_2_.setGrowingAge(6000);
            this.resetInLove();
            p_234177_2_.resetInLove();
            ageableentity.setChild(true);
            ageableentity.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), 0.0F, 0.0F);
            p_234177_1_.func_242417_l(ageableentity);
            p_234177_1_.setEntityState(this, (byte)18);

            if (p_234177_1_.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))
            {
                p_234177_1_.addEntity(new ExperienceOrbEntity(p_234177_1_, this.getPosX(), this.getPosY(), this.getPosZ(), this.getRNG().nextInt(7) + 1));
            }
        }
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    public void handleStatusUpdate(byte id)
    {
        if (id == 18)
        {
            for (int i = 0; i < 7; ++i)
            {
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.HEART, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), d0, d1, d2);
            }
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }
}
