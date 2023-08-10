package net.minecraft.advancements.criterion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateHolder;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.JSONUtils;

public class StatePropertiesPredicate
{
    public static final StatePropertiesPredicate EMPTY = new StatePropertiesPredicate(ImmutableList.of());
    private final List<StatePropertiesPredicate.Matcher> matchers;

    private static StatePropertiesPredicate.Matcher deserializeProperty(String name, JsonElement element)
    {
        if (element.isJsonPrimitive())
        {
            String s2 = element.getAsString();
            return new StatePropertiesPredicate.ExactMatcher(name, s2);
        }
        else
        {
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "value");
            String s = jsonobject.has("min") ? getNullableString(jsonobject.get("min")) : null;
            String s1 = jsonobject.has("max") ? getNullableString(jsonobject.get("max")) : null;
            return (StatePropertiesPredicate.Matcher)(s != null && s.equals(s1) ? new StatePropertiesPredicate.ExactMatcher(name, s) : new StatePropertiesPredicate.RangedMacher(name, s, s1));
        }
    }

    @Nullable
    private static String getNullableString(JsonElement element)
    {
        return element.isJsonNull() ? null : element.getAsString();
    }

    private StatePropertiesPredicate(List<StatePropertiesPredicate.Matcher> matchers)
    {
        this.matchers = ImmutableList.copyOf(matchers);
    }

    public < S extends StateHolder <? , S >> boolean matchesAll(StateContainer <? , S > properties, S targetProperty)
    {
        for (StatePropertiesPredicate.Matcher statepropertiespredicate$matcher : this.matchers)
        {
            if (!statepropertiespredicate$matcher.test(properties, targetProperty))
            {
                return false;
            }
        }

        return true;
    }

    public boolean matches(BlockState state)
    {
        return this.matchesAll(state.getBlock().getStateContainer(), state);
    }

    public boolean matches(FluidState state)
    {
        return this.matchesAll(state.getFluid().getStateContainer(), state);
    }

    public void forEachNotPresent(StateContainer <? , ? > properties, Consumer<String> stringConsumer)
    {
        this.matchers.forEach((m) ->
        {
            m.runIfNotPresent(properties, stringConsumer);
        });
    }

    public static StatePropertiesPredicate deserializeProperties(@Nullable JsonElement element)
    {
        if (element != null && !element.isJsonNull())
        {
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "properties");
            List<StatePropertiesPredicate.Matcher> list = Lists.newArrayList();

            for (Entry<String, JsonElement> entry : jsonobject.entrySet())
            {
                list.add(deserializeProperty(entry.getKey(), entry.getValue()));
            }

            return new StatePropertiesPredicate(list);
        }
        else
        {
            return EMPTY;
        }
    }

    public JsonElement toJsonElement()
    {
        if (this == EMPTY)
        {
            return JsonNull.INSTANCE;
        }
        else
        {
            JsonObject jsonobject = new JsonObject();

            if (!this.matchers.isEmpty())
            {
                this.matchers.forEach((matcher) ->
                {
                    jsonobject.add(matcher.getPropertyName(), matcher.toJsonElement());
                });
            }

            return jsonobject;
        }
    }

    public static class Builder
    {
        private final List<StatePropertiesPredicate.Matcher> matchers = Lists.newArrayList();

        private Builder()
        {
        }

        public static StatePropertiesPredicate.Builder newBuilder()
        {
            return new StatePropertiesPredicate.Builder();
        }

        public StatePropertiesPredicate.Builder withStringProp(Property<?> property, String value)
        {
            this.matchers.add(new StatePropertiesPredicate.ExactMatcher(property.getName(), value));
            return this;
        }

        public StatePropertiesPredicate.Builder withIntProp(Property<Integer> intProp, int value)
        {
            return this.withStringProp(intProp, Integer.toString(value));
        }

        public StatePropertiesPredicate.Builder withBoolProp(Property<Boolean> boolProp, boolean value)
        {
            return this.withStringProp(boolProp, Boolean.toString(value));
        }

        public <T extends Comparable<T> & IStringSerializable> StatePropertiesPredicate.Builder withProp(Property<T> prop, T value)
        {
            return this.withStringProp(prop, value.getString());
        }

        public StatePropertiesPredicate build()
        {
            return new StatePropertiesPredicate(this.matchers);
        }
    }

    static class ExactMatcher extends StatePropertiesPredicate.Matcher
    {
        private final String valueToMatch;

        public ExactMatcher(String propertyName, String valueToMatch)
        {
            super(propertyName);
            this.valueToMatch = valueToMatch;
        }

        protected <T extends Comparable<T>> boolean matchesExact(StateHolder <? , ? > properties, Property<T> propertyTarget)
        {
            T t = properties.get(propertyTarget);
            Optional<T> optional = propertyTarget.parseValue(this.valueToMatch);
            return optional.isPresent() && t.compareTo(optional.get()) == 0;
        }

        public JsonElement toJsonElement()
        {
            return new JsonPrimitive(this.valueToMatch);
        }
    }

    abstract static class Matcher
    {
        private final String propertyName;

        public Matcher(String propertyName)
        {
            this.propertyName = propertyName;
        }

        public < S extends StateHolder <? , S >> boolean test(StateContainer <? , S > properties, S propertyToMatch)
        {
            Property<?> property = properties.getProperty(this.propertyName);
            return property == null ? false : this.matchesExact(propertyToMatch, property);
        }

        protected abstract <T extends Comparable<T>> boolean matchesExact(StateHolder <? , ? > properties, Property<T> propertyTarget);

        public abstract JsonElement toJsonElement();

        public String getPropertyName()
        {
            return this.propertyName;
        }

        public void runIfNotPresent(StateContainer <? , ? > properties, Consumer<String> propertyConsumer)
        {
            Property<?> property = properties.getProperty(this.propertyName);

            if (property == null)
            {
                propertyConsumer.accept(this.propertyName);
            }
        }
    }

    static class RangedMacher extends StatePropertiesPredicate.Matcher
    {
        @Nullable
        private final String minimum;
        @Nullable
        private final String maximum;

        public RangedMacher(String propertyName, @Nullable String minimum, @Nullable String maximum)
        {
            super(propertyName);
            this.minimum = minimum;
            this.maximum = maximum;
        }

        protected <T extends Comparable<T>> boolean matchesExact(StateHolder <? , ? > properties, Property<T> propertyTarget)
        {
            T t = properties.get(propertyTarget);

            if (this.minimum != null)
            {
                Optional<T> optional = propertyTarget.parseValue(this.minimum);

                if (!optional.isPresent() || t.compareTo(optional.get()) < 0)
                {
                    return false;
                }
            }

            if (this.maximum != null)
            {
                Optional<T> optional1 = propertyTarget.parseValue(this.maximum);

                if (!optional1.isPresent() || t.compareTo(optional1.get()) > 0)
                {
                    return false;
                }
            }

            return true;
        }

        public JsonElement toJsonElement()
        {
            JsonObject jsonobject = new JsonObject();

            if (this.minimum != null)
            {
                jsonobject.addProperty("min", this.minimum);
            }

            if (this.maximum != null)
            {
                jsonobject.addProperty("max", this.maximum);
            }

            return jsonobject;
        }
    }
}
