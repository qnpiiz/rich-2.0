package net.minecraft.entity.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ExperienceBottleEntity extends ProjectileItemEntity
{
    public ExperienceBottleEntity(EntityType <? extends ExperienceBottleEntity > p_i50152_1_, World world)
    {
        super(p_i50152_1_, world);
    }

    public ExperienceBottleEntity(World worldIn, LivingEntity throwerIn)
    {
        super(EntityType.EXPERIENCE_BOTTLE, throwerIn, worldIn);
    }

    public ExperienceBottleEntity(World worldIn, double x, double y, double z)
    {
        super(EntityType.EXPERIENCE_BOTTLE, x, y, z, worldIn);
    }

    protected Item getDefaultItem()
    {
        return Items.EXPERIENCE_BOTTLE;
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    protected float getGravityVelocity()
    {
        return 0.07F;
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(RayTraceResult result)
    {
        super.onImpact(result);

        if (!this.world.isRemote)
        {
            this.world.playEvent(2002, this.getPosition(), PotionUtils.getPotionColor(Potions.WATER));
            int i = 3 + this.world.rand.nextInt(5) + this.world.rand.nextInt(5);

            while (i > 0)
            {
                int j = ExperienceOrbEntity.getXPSplit(i);
                i -= j;
                this.world.addEntity(new ExperienceOrbEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), j));
            }

            this.remove();
        }
    }
}
