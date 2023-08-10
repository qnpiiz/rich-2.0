package net.minecraft.world.spawner;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public class MobDensityTracker
{
    private final List<MobDensityTracker.DensityEntry> field_234997_a_ = Lists.newArrayList();

    public void func_234998_a_(BlockPos p_234998_1_, double p_234998_2_)
    {
        if (p_234998_2_ != 0.0D)
        {
            this.field_234997_a_.add(new MobDensityTracker.DensityEntry(p_234998_1_, p_234998_2_));
        }
    }

    public double func_234999_b_(BlockPos p_234999_1_, double p_234999_2_)
    {
        if (p_234999_2_ == 0.0D)
        {
            return 0.0D;
        }
        else
        {
            double d0 = 0.0D;

            for (MobDensityTracker.DensityEntry mobdensitytracker$densityentry : this.field_234997_a_)
            {
                d0 += mobdensitytracker$densityentry.func_235002_a_(p_234999_1_);
            }

            return d0 * p_234999_2_;
        }
    }

    static class DensityEntry
    {
        private final BlockPos field_235000_a_;
        private final double field_235001_b_;

        public DensityEntry(BlockPos p_i231624_1_, double p_i231624_2_)
        {
            this.field_235000_a_ = p_i231624_1_;
            this.field_235001_b_ = p_i231624_2_;
        }

        public double func_235002_a_(BlockPos p_235002_1_)
        {
            double d0 = this.field_235000_a_.distanceSq(p_235002_1_);
            return d0 == 0.0D ? Double.POSITIVE_INFINITY : this.field_235001_b_ / Math.sqrt(d0);
        }
    }
}
