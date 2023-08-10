package net.minecraft.world.chunk.storage;

import javax.annotation.Nullable;

public class SuppressedExceptions<T extends Throwable>
{
    @Nullable
    private T field_233001_a_;

    public void func_233003_a_(T p_233003_1_)
    {
        if (this.field_233001_a_ == null)
        {
            this.field_233001_a_ = p_233003_1_;
        }
        else
        {
            this.field_233001_a_.addSuppressed(p_233003_1_);
        }
    }

    public void func_233002_a_() throws T
    {
        if (this.field_233001_a_ != null)
        {
            throw this.field_233001_a_;
        }
    }
}
