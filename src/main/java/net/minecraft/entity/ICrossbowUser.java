package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public interface ICrossbowUser extends IRangedAttackMob
{
    void setCharging(boolean isCharging);

    void func_230284_a_(LivingEntity p_230284_1_, ItemStack p_230284_2_, ProjectileEntity p_230284_3_, float p_230284_4_);

    @Nullable

    /**
     * Gets the active target the Task system uses for tracking
     */
    LivingEntity getAttackTarget();

    void func_230283_U__();

default void func_234281_b_(LivingEntity p_234281_1_, float p_234281_2_)
    {
        Hand hand = ProjectileHelper.getHandWith(p_234281_1_, Items.CROSSBOW);
        ItemStack itemstack = p_234281_1_.getHeldItem(hand);

        if (p_234281_1_.canEquip(Items.CROSSBOW))
        {
            CrossbowItem.fireProjectiles(p_234281_1_.world, p_234281_1_, hand, itemstack, p_234281_2_, (float)(14 - p_234281_1_.world.getDifficulty().getId() * 4));
        }

        this.func_230283_U__();
    }

default void func_234279_a_(LivingEntity p_234279_1_, LivingEntity p_234279_2_, ProjectileEntity p_234279_3_, float p_234279_4_, float p_234279_5_)
    {
        double d0 = p_234279_2_.getPosX() - p_234279_1_.getPosX();
        double d1 = p_234279_2_.getPosZ() - p_234279_1_.getPosZ();
        double d2 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1);
        double d3 = p_234279_2_.getPosYHeight(0.3333333333333333D) - p_234279_3_.getPosY() + d2 * (double)0.2F;
        Vector3f vector3f = this.func_234280_a_(p_234279_1_, new Vector3d(d0, d3, d1), p_234279_4_);
        p_234279_3_.shoot((double)vector3f.getX(), (double)vector3f.getY(), (double)vector3f.getZ(), p_234279_5_, (float)(14 - p_234279_1_.world.getDifficulty().getId() * 4));
        p_234279_1_.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (p_234279_1_.getRNG().nextFloat() * 0.4F + 0.8F));
    }

default Vector3f func_234280_a_(LivingEntity p_234280_1_, Vector3d p_234280_2_, float p_234280_3_)
    {
        Vector3d vector3d = p_234280_2_.normalize();
        Vector3d vector3d1 = vector3d.crossProduct(new Vector3d(0.0D, 1.0D, 0.0D));

        if (vector3d1.lengthSquared() <= 1.0E-7D)
        {
            vector3d1 = vector3d.crossProduct(p_234280_1_.getUpVector(1.0F));
        }

        Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), 90.0F, true);
        Vector3f vector3f = new Vector3f(vector3d);
        vector3f.transform(quaternion);
        Quaternion quaternion1 = new Quaternion(vector3f, p_234280_3_, true);
        Vector3f vector3f1 = new Vector3f(vector3d);
        vector3f1.transform(quaternion1);
        return vector3f1;
    }
}
