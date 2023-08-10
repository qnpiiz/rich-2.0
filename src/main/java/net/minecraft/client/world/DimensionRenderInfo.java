package net.minecraft.client.world;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;

public abstract class DimensionRenderInfo
{
    private static final Object2ObjectMap<ResourceLocation, DimensionRenderInfo> field_239208_a_ = Util.make(new Object2ObjectArrayMap<>(), (p_239214_0_) ->
    {
        DimensionRenderInfo.Overworld dimensionrenderinfo$overworld = new DimensionRenderInfo.Overworld();
        p_239214_0_.defaultReturnValue(dimensionrenderinfo$overworld);
        p_239214_0_.put(DimensionType.OVERWORLD_ID, dimensionrenderinfo$overworld);
        p_239214_0_.put(DimensionType.THE_NETHER_ID, new DimensionRenderInfo.Nether());
        p_239214_0_.put(DimensionType.THE_END_ID, new DimensionRenderInfo.End());
    });
    private final float[] field_239209_b_ = new float[4];
    private final float field_239210_c_;
    private final boolean field_239211_d_;
    private final DimensionRenderInfo.FogType field_241680_e_;
    private final boolean field_241681_f_;
    private final boolean field_239212_e_;

    public DimensionRenderInfo(float p_i241259_1_, boolean p_i241259_2_, DimensionRenderInfo.FogType p_i241259_3_, boolean p_i241259_4_, boolean p_i241259_5_)
    {
        this.field_239210_c_ = p_i241259_1_;
        this.field_239211_d_ = p_i241259_2_;
        this.field_241680_e_ = p_i241259_3_;
        this.field_241681_f_ = p_i241259_4_;
        this.field_239212_e_ = p_i241259_5_;
    }

    public static DimensionRenderInfo func_243495_a(DimensionType p_243495_0_)
    {
        return field_239208_a_.get(p_243495_0_.getEffects());
    }

    @Nullable
    public float[] func_230492_a_(float p_230492_1_, float p_230492_2_)
    {
        float f = 0.4F;
        float f1 = MathHelper.cos(p_230492_1_ * ((float)Math.PI * 2F)) - 0.0F;
        float f2 = -0.0F;

        if (f1 >= -0.4F && f1 <= 0.4F)
        {
            float f3 = (f1 - -0.0F) / 0.4F * 0.5F + 0.5F;
            float f4 = 1.0F - (1.0F - MathHelper.sin(f3 * (float)Math.PI)) * 0.99F;
            f4 = f4 * f4;
            this.field_239209_b_[0] = f3 * 0.3F + 0.7F;
            this.field_239209_b_[1] = f3 * f3 * 0.7F + 0.2F;
            this.field_239209_b_[2] = f3 * f3 * 0.0F + 0.2F;
            this.field_239209_b_[3] = f4;
            return this.field_239209_b_;
        }
        else
        {
            return null;
        }
    }

    public float func_239213_a_()
    {
        return this.field_239210_c_;
    }

    public boolean func_239216_b_()
    {
        return this.field_239211_d_;
    }

    public abstract Vector3d func_230494_a_(Vector3d p_230494_1_, float p_230494_2_);

    public abstract boolean func_230493_a_(int p_230493_1_, int p_230493_2_);

    public DimensionRenderInfo.FogType func_241683_c_()
    {
        return this.field_241680_e_;
    }

    public boolean func_241684_d_()
    {
        return this.field_241681_f_;
    }

    public boolean func_239217_c_()
    {
        return this.field_239212_e_;
    }

    public static class End extends DimensionRenderInfo
    {
        public End()
        {
            super(Float.NaN, false, DimensionRenderInfo.FogType.END, true, false);
        }

        public Vector3d func_230494_a_(Vector3d p_230494_1_, float p_230494_2_)
        {
            return p_230494_1_.scale((double)0.15F);
        }

        public boolean func_230493_a_(int p_230493_1_, int p_230493_2_)
        {
            return false;
        }

        @Nullable
        public float[] func_230492_a_(float p_230492_1_, float p_230492_2_)
        {
            return null;
        }
    }

    public static enum FogType
    {
        NONE,
        NORMAL,
        END;
    }

    public static class Nether extends DimensionRenderInfo
    {
        public Nether()
        {
            super(Float.NaN, true, DimensionRenderInfo.FogType.NONE, false, true);
        }

        public Vector3d func_230494_a_(Vector3d p_230494_1_, float p_230494_2_)
        {
            return p_230494_1_;
        }

        public boolean func_230493_a_(int p_230493_1_, int p_230493_2_)
        {
            return true;
        }
    }

    public static class Overworld extends DimensionRenderInfo
    {
        public Overworld()
        {
            super(128.0F, true, DimensionRenderInfo.FogType.NORMAL, false, false);
        }

        public Vector3d func_230494_a_(Vector3d p_230494_1_, float p_230494_2_)
        {
            return p_230494_1_.mul((double)(p_230494_2_ * 0.94F + 0.06F), (double)(p_230494_2_ * 0.94F + 0.06F), (double)(p_230494_2_ * 0.91F + 0.09F));
        }

        public boolean func_230493_a_(int p_230493_1_, int p_230493_2_)
        {
            return false;
        }
    }
}
