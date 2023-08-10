package net.minecraft.test;

import java.util.function.Consumer;
import net.minecraft.util.Rotation;

public class TestFunctionInfo
{
    private final String field_229650_a_ = null;
    private final String field_229651_b_ = null;
    private final String field_229652_c_ = null;
    private final boolean field_229653_d_ = false;
    private final Consumer<TestTrackerHolder> field_229654_e_ = null;
    private final int field_229655_f_ = 0;
    private final long field_229656_g_ = 0L;
    private final Rotation field_240589_h_ = null;

    public void func_229658_a_(TestTrackerHolder p_229658_1_)
    {
        this.field_229654_e_.accept(p_229658_1_);
    }

    public String func_229657_a_()
    {
        return this.field_229651_b_;
    }

    public String func_229659_b_()
    {
        return this.field_229652_c_;
    }

    public String toString()
    {
        return this.field_229651_b_;
    }

    public int func_229660_c_()
    {
        return this.field_229655_f_;
    }

    public boolean func_229661_d_()
    {
        return this.field_229653_d_;
    }

    public String func_229662_e_()
    {
        return this.field_229650_a_;
    }

    public long func_229663_f_()
    {
        return this.field_229656_g_;
    }

    public Rotation func_240590_g_()
    {
        return this.field_240589_h_;
    }

    private TestFunctionInfo()
    {
        throw new RuntimeException("Synthetic constructor added by MCP, do not call");
    }
}
