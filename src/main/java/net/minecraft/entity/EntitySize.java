package net.minecraft.entity;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class EntitySize
{
    public final float width;
    public final float height;
    public final boolean fixed;

    public EntitySize(float widthIn, float heightIn, boolean fixedIn)
    {
        this.width = widthIn;
        this.height = heightIn;
        this.fixed = fixedIn;
    }

    public AxisAlignedBB func_242286_a(Vector3d p_242286_1_)
    {
        return this.func_242285_a(p_242286_1_.x, p_242286_1_.y, p_242286_1_.z);
    }

    public AxisAlignedBB func_242285_a(double p_242285_1_, double p_242285_3_, double p_242285_5_)
    {
        float f = this.width / 2.0F;
        float f1 = this.height;
        return new AxisAlignedBB(p_242285_1_ - (double)f, p_242285_3_, p_242285_5_ - (double)f, p_242285_1_ + (double)f, p_242285_3_ + (double)f1, p_242285_5_ + (double)f);
    }

    public EntitySize scale(float factor)
    {
        return this.scale(factor, factor);
    }

    public EntitySize scale(float widthFactor, float heightFactor)
    {
        return !this.fixed && (widthFactor != 1.0F || heightFactor != 1.0F) ? flexible(this.width * widthFactor, this.height * heightFactor) : this;
    }

    public static EntitySize flexible(float widthIn, float heightIn)
    {
        return new EntitySize(widthIn, heightIn, false);
    }

    public static EntitySize fixed(float widthIn, float heightIn)
    {
        return new EntitySize(widthIn, heightIn, true);
    }

    public String toString()
    {
        return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.fixed;
    }
}
