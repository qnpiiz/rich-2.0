package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;

public interface IMultiPartPredicateBuilder extends Supplier<JsonElement>
{
    void func_230523_a_(StateContainer <? , ? > p_230523_1_);

    static IMultiPartPredicateBuilder.Properties func_240089_a_()
    {
        return new IMultiPartPredicateBuilder.Properties();
    }

    static IMultiPartPredicateBuilder func_240090_b_(IMultiPartPredicateBuilder... p_240090_0_)
    {
        return new IMultiPartPredicateBuilder.Serializer(IMultiPartPredicateBuilder.Operator.OR, Arrays.asList(p_240090_0_));
    }

    public static enum Operator
    {
        AND("AND"),
        OR("OR");

        private final String field_240094_c_;

        private Operator(String p_i232523_3_)
        {
            this.field_240094_c_ = p_i232523_3_;
        }
    }

    public static class Properties implements IMultiPartPredicateBuilder
    {
        private final Map < Property<?>, String > field_240096_a_ = Maps.newHashMap();

        private static <T extends Comparable<T>> String func_240101_a_(Property<T> p_240101_0_, Stream<T> p_240101_1_)
        {
            return p_240101_1_.map(p_240101_0_::getName).collect(Collectors.joining("|"));
        }

        private static <T extends Comparable<T>> String func_240103_c_(Property<T> p_240103_0_, T p_240103_1_, T[] p_240103_2_)
        {
            return func_240101_a_(p_240103_0_, Stream.concat(Stream.of(p_240103_1_), Stream.of(p_240103_2_)));
        }

        private <T extends Comparable<T>> void func_240100_a_(Property<T> p_240100_1_, String p_240100_2_)
        {
            String s = this.field_240096_a_.put(p_240100_1_, p_240100_2_);

            if (s != null)
            {
                throw new IllegalStateException("Tried to replace " + p_240100_1_ + " value from " + s + " to " + p_240100_2_);
            }
        }

        public final <T extends Comparable<T>> IMultiPartPredicateBuilder.Properties func_240098_a_(Property<T> p_240098_1_, T p_240098_2_)
        {
            this.func_240100_a_(p_240098_1_, p_240098_1_.getName(p_240098_2_));
            return this;
        }

        @SafeVarargs
        public final <T extends Comparable<T>> IMultiPartPredicateBuilder.Properties func_240099_a_(Property<T> p_240099_1_, T p_240099_2_, T... p_240099_3_)
        {
            this.func_240100_a_(p_240099_1_, func_240103_c_(p_240099_1_, p_240099_2_, p_240099_3_));
            return this;
        }

        public JsonElement get()
        {
            JsonObject jsonobject = new JsonObject();
            this.field_240096_a_.forEach((p_240102_1_, p_240102_2_) ->
            {
                jsonobject.addProperty(p_240102_1_.getName(), p_240102_2_);
            });
            return jsonobject;
        }

        public void func_230523_a_(StateContainer <? , ? > p_230523_1_)
        {
            List < Property<? >> list = this.field_240096_a_.keySet().stream().filter((p_240097_1_) ->
            {
                return p_230523_1_.getProperty(p_240097_1_.getName()) != p_240097_1_;
            }).collect(Collectors.toList());

            if (!list.isEmpty())
            {
                throw new IllegalStateException("Properties " + list + " are missing from " + p_230523_1_);
            }
        }
    }

    public static class Serializer implements IMultiPartPredicateBuilder
    {
        private final IMultiPartPredicateBuilder.Operator field_240091_a_;
        private final List<IMultiPartPredicateBuilder> field_240092_b_;

        private Serializer(IMultiPartPredicateBuilder.Operator p_i232521_1_, List<IMultiPartPredicateBuilder> p_i232521_2_)
        {
            this.field_240091_a_ = p_i232521_1_;
            this.field_240092_b_ = p_i232521_2_;
        }

        public void func_230523_a_(StateContainer <? , ? > p_230523_1_)
        {
            this.field_240092_b_.forEach((p_240093_1_) ->
            {
                p_240093_1_.func_230523_a_(p_230523_1_);
            });
        }

        public JsonElement get()
        {
            JsonArray jsonarray = new JsonArray();
            this.field_240092_b_.stream().map(Supplier::get).forEach(jsonarray::add);
            JsonObject jsonobject = new JsonObject();
            jsonobject.add(this.field_240091_a_.field_240094_c_, jsonarray);
            return jsonobject;
        }
    }
}
