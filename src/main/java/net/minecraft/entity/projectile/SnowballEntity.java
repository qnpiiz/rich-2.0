package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class SnowballEntity extends ProjectileItemEntity
{
    public SnowballEntity(EntityType <? extends SnowballEntity > p_i50159_1_, World p_i50159_2_)
    {
        super(p_i50159_1_, p_i50159_2_);
    }

    public SnowballEntity(World worldIn, LivingEntity throwerIn)
    {
        super(EntityType.SNOWBALL, throwerIn, worldIn);
    }

    public SnowballEntity(World worldIn, double x, double y, double z)
    {
        super(EntityType.SNOWBALL, x, y, z, worldIn);
    }

    protected Item getDefaultItem()
    {
        return Items.SNOWBALL;
    }

    private IParticleData makeParticle()
    {
        ItemStack itemstack = this.func_213882_k();
        return (IParticleData)(itemstack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleData(ParticleTypes.ITEM, itemstack));
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    public void handleStatusUpdate(byte id)
    {
        if (id == 3)
        {
            IParticleData iparticledata = this.makeParticle();

            for (int i = 0; i < 8; ++i)
            {
                this.world.addParticle(iparticledata, this.getPosX(), this.getPosY(), this.getPosZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /**
     * Called when the arrow hits an entity
     */
    protected void onEntityHit(EntityRayTraceResult p_213868_1_)
    {
        super.onEntityHit(p_213868_1_);
        Entity entity = p_213868_1_.getEntity();
        int i = entity instanceof BlazeEntity ? 3 : 0;
        entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()), (float)i);
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(RayTraceResult result)
    {
        super.onImpact(result);

        if (!this.world.isRemote)
        {
            this.world.setEntityState(this, (byte)3);
            this.remove();
        }
    }
}
