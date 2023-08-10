package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.state.Property;

public final class VariantPropertyBuilder
{
    private static final VariantPropertyBuilder field_240184_a_ = new VariantPropertyBuilder(ImmutableList.of());
    private static final Comparator < Property.ValuePair<? >> field_240185_b_ = Comparator.comparing((p_240192_0_) ->
    {
        return p_240192_0_.func_240181_a_().getName();
    });
    private final List < Property.ValuePair<? >> field_240186_c_;

    public VariantPropertyBuilder func_240188_a_(Property.ValuePair<?> p_240188_1_)
    {
        return new VariantPropertyBuilder(ImmutableList. < Property.ValuePair<? >> builder().addAll(this.field_240186_c_).add(p_240188_1_).build());
    }

    public VariantPropertyBuilder func_240189_a_(VariantPropertyBuilder p_240189_1_)
    {
        return new VariantPropertyBuilder(ImmutableList. < Property.ValuePair<? >> builder().addAll(this.field_240186_c_).addAll(p_240189_1_.field_240186_c_).build());
    }

    private VariantPropertyBuilder(List < Property.ValuePair<? >> p_i232541_1_)
    {
        this.field_240186_c_ = p_i232541_1_;
    }

    public static VariantPropertyBuilder func_240187_a_()
    {
        return field_240184_a_;
    }

    public static VariantPropertyBuilder func_240190_a_(Property.ValuePair<?>... p_240190_0_)
    {
        return new VariantPropertyBuilder(ImmutableList.copyOf(p_240190_0_));
    }

    public boolean equals(Object p_equals_1_)
    {
        return this == p_equals_1_ || p_equals_1_ instanceof VariantPropertyBuilder && this.field_240186_c_.equals(((VariantPropertyBuilder)p_equals_1_).field_240186_c_);
    }

    public int hashCode()
    {
        return this.field_240186_c_.hashCode();
    }

    public String func_240191_b_()
    {
        return this.field_240186_c_.stream().sorted(field_240185_b_).map(Property.ValuePair::toString).collect(Collectors.joining(","));
    }

    public String toString()
    {
        return this.func_240191_b_();
    }
}
