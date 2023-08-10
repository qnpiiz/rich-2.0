package net.minecraft.profiler;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public class TimeTracker
{
    private final LongSupplier field_233502_a_;
    private final IntSupplier field_233503_b_;
    private IResultableProfiler field_233504_c_ = EmptyProfiler.INSTANCE;

    public TimeTracker(LongSupplier p_i231483_1_, IntSupplier p_i231483_2_)
    {
        this.field_233502_a_ = p_i231483_1_;
        this.field_233503_b_ = p_i231483_2_;
    }

    public boolean func_233505_a_()
    {
        return this.field_233504_c_ != EmptyProfiler.INSTANCE;
    }

    public void func_233506_b_()
    {
        this.field_233504_c_ = EmptyProfiler.INSTANCE;
    }

    public void func_233507_c_()
    {
        this.field_233504_c_ = new Profiler(this.field_233502_a_, this.field_233503_b_, true);
    }

    public IProfiler func_233508_d_()
    {
        return this.field_233504_c_;
    }

    public IProfileResult func_233509_e_()
    {
        return this.field_233504_c_.getResults();
    }
}
