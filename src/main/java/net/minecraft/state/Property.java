package net.minecraft.state;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class Property<T extends Comparable<T>>
{
    private final Class<T> field_235913_a_;
    private final String field_235914_b_;
    private Integer field_235915_c_;
    private final Codec<T> field_235916_d_ = Codec.STRING.comapFlatMap((p_lambda$new$1_1_) ->
    {
        return this.parseValue(p_lambda$new$1_1_).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("Unable to read property: " + this + " with value: " + p_lambda$new$1_1_);
        });
    }, this::getName);
    private final Codec<Property.ValuePair<T>> field_241488_e_ = this.field_235916_d_.xmap(this::func_241490_b_, Property.ValuePair::func_241493_b_);

    protected Property(String name, Class<T> valueClass)
    {
        this.field_235913_a_ = valueClass;
        this.field_235914_b_ = name;
    }

    public Property.ValuePair<T> func_241490_b_(T p_241490_1_)
    {
        return new Property.ValuePair<>(this, p_241490_1_);
    }

    public Property.ValuePair<T> func_241489_a_(StateHolder <? , ? > p_241489_1_)
    {
        return new Property.ValuePair<>(this, p_241489_1_.get(this));
    }

    public Stream<Property.ValuePair<T>> func_241491_c_()
    {
        return this.getAllowedValues().stream().map(this::func_241490_b_);
    }

    public Codec<Property.ValuePair<T>> func_241492_e_()
    {
        return this.field_241488_e_;
    }

    public String getName()
    {
        return this.field_235914_b_;
    }

    public Class<T> getValueClass()
    {
        return this.field_235913_a_;
    }

    public abstract Collection<T> getAllowedValues();

    /**
     * Get the name for the given value.
     */
    public abstract String getName(T value);

    public abstract Optional<T> parseValue(String value);

    public String toString()
    {
        return MoreObjects.toStringHelper(this).add("name", this.field_235914_b_).add("clazz", this.field_235913_a_).add("values", this.getAllowedValues()).toString();
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof Property))
        {
            return false;
        }
        else
        {
            Property<?> property = (Property)p_equals_1_;
            return this.field_235913_a_.equals(property.field_235913_a_) && this.field_235914_b_.equals(property.field_235914_b_);
        }
    }

    public final int hashCode()
    {
        if (this.field_235915_c_ == null)
        {
            this.field_235915_c_ = this.computeHashCode();
        }

        return this.field_235915_c_;
    }

    public int computeHashCode()
    {
        return 31 * this.field_235913_a_.hashCode() + this.field_235914_b_.hashCode();
    }

    public static final class ValuePair<T extends Comparable<T>>
    {
        private final Property<T> field_240179_a_;
        private final T field_240180_b_;

        private ValuePair(Property<T> p_i232540_1_, T p_i232540_2_)
        {
            if (!p_i232540_1_.getAllowedValues().contains(p_i232540_2_))
            {
                throw new IllegalArgumentException("Value " + p_i232540_2_ + " does not belong to property " + p_i232540_1_);
            }
            else
            {
                this.field_240179_a_ = p_i232540_1_;
                this.field_240180_b_ = p_i232540_2_;
            }
        }

        public Property<T> func_240181_a_()
        {
            return this.field_240179_a_;
        }

        public T func_241493_b_()
        {
            return this.field_240180_b_;
        }

        public String toString()
        {
            return this.field_240179_a_.getName() + "=" + this.field_240179_a_.getName(this.field_240180_b_);
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else if (!(p_equals_1_ instanceof Property.ValuePair))
            {
                return false;
            }
            else
            {
                Property.ValuePair<?> valuepair = (Property.ValuePair)p_equals_1_;
                return this.field_240179_a_ == valuepair.field_240179_a_ && this.field_240180_b_.equals(valuepair.field_240180_b_);
            }
        }

        public int hashCode()
        {
            int i = this.field_240179_a_.hashCode();
            return 31 * i + this.field_240180_b_.hashCode();
        }
    }
}
