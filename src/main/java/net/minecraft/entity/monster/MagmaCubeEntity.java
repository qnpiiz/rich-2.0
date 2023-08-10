package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.fluid.Fluid;
import net.minecraft.loot.LootTables;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class MagmaCubeEntity extends SlimeEntity
{
    public MagmaCubeEntity(EntityType <? extends MagmaCubeEntity > type, World worldIn)
    {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute func_234294_m_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.2F);
    }

    public static boolean func_223367_b(EntityType<MagmaCubeEntity> p_223367_0_, IWorld p_223367_1_, SpawnReason p_223367_2_, BlockPos p_223367_3_, Random p_223367_4_)
    {
        return p_223367_1_.getDifficulty() != Difficulty.PEACEFUL;
    }

    public boolean isNotColliding(IWorldReader worldIn)
    {
        return worldIn.checkNoEntityCollision(this) && !worldIn.containsAnyLiquid(this.getBoundingBox());
    }

    protected void setSlimeSize(int size, boolean resetHealth)
    {
        super.setSlimeSize(size, resetHealth);
        this.getAttribute(Attributes.ARMOR).setBaseValue((double)(size * 3));
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness()
    {
        return 1.0F;
    }

    protected IParticleData getSquishParticle()
    {
        return ParticleTypes.FLAME;
    }

    protected ResourceLocation getLootTable()
    {
        return this.isSmallSlime() ? LootTables.EMPTY : this.getType().getLootTable();
    }

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        return false;
    }

    /**
     * Gets the amount of time the slime needs to wait between jumps.
     */
    protected int getJumpDelay()
    {
        return super.getJumpDelay() * 4;
    }

    protected void alterSquishAmount()
    {
        this.squishAmount *= 0.9F;
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void jump()
    {
        Vector3d vector3d = this.getMotion();
        this.setMotion(vector3d.x, (double)(this.getJumpUpwardsMotion() + (float)this.getSlimeSize() * 0.1F), vector3d.z);
        this.isAirBorne = true;
    }

    protected void handleFluidJump(ITag<Fluid> fluidTag)
    {
        if (fluidTag == FluidTags.LAVA)
        {
            Vector3d vector3d = this.getMotion();
            this.setMotion(vector3d.x, (double)(0.22F + (float)this.getSlimeSize() * 0.05F), vector3d.z);
            this.isAirBorne = true;
        }
        else
        {
            super.handleFluidJump(fluidTag);
        }
    }

    public boolean onLivingFall(float distance, float damageMultiplier)
    {
        return false;
    }

    /**
     * Indicates weather the slime is able to damage the player (based upon the slime's size)
     */
    protected boolean canDamagePlayer()
    {
        return this.isServerWorld();
    }

    protected float func_225512_er_()
    {
        return super.func_225512_er_() + 2.0F;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return this.isSmallSlime() ? SoundEvents.ENTITY_MAGMA_CUBE_HURT_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return this.isSmallSlime() ? SoundEvents.ENTITY_MAGMA_CUBE_DEATH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_DEATH;
    }

    protected SoundEvent getSquishSound()
    {
        return this.isSmallSlime() ? SoundEvents.ENTITY_MAGMA_CUBE_SQUISH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_SQUISH;
    }

    protected SoundEvent getJumpSound()
    {
        return SoundEvents.ENTITY_MAGMA_CUBE_JUMP;
    }
}
