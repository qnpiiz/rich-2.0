package net.minecraft.util.text;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class TextPropertiesManager
{
    private final List<ITextProperties> field_238153_a_ = Lists.newArrayList();

    public void func_238155_a_(ITextProperties p_238155_1_)
    {
        this.field_238153_a_.add(p_238155_1_);
    }

    @Nullable
    public ITextProperties func_238154_a_()
    {
        if (this.field_238153_a_.isEmpty())
        {
            return null;
        }
        else
        {
            return this.field_238153_a_.size() == 1 ? this.field_238153_a_.get(0) : ITextProperties.func_240654_a_(this.field_238153_a_);
        }
    }

    public ITextProperties func_238156_b_()
    {
        ITextProperties itextproperties = this.func_238154_a_();
        return itextproperties != null ? itextproperties : ITextProperties.field_240651_c_;
    }
}
