package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class MinMaxBounds<T extends Number>
{
    public static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(new TranslationTextComponent("argument.range.empty"));
    public static final SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.range.swapped"));
    protected final T min;
    protected final T max;

    protected MinMaxBounds(@Nullable T min, @Nullable T max)
    {
        this.min = min;
        this.max = max;
    }

    @Nullable
    public T getMin()
    {
        return this.min;
    }

    @Nullable
    public T getMax()
    {
        return this.max;
    }

    public boolean isUnbounded()
    {
        return this.min == null && this.max == null;
    }

    public JsonElement serialize()
    {
        if (this.isUnbounded())
        {
            return JsonNull.INSTANCE;
        }
        else if (this.min != null && this.min.equals(this.max))
        {
            return new JsonPrimitive(this.min);
        }
        else
        {
            JsonObject jsonobject = new JsonObject();

            if (this.min != null)
            {
                jsonobject.addProperty("min", this.min);
            }

            if (this.max != null)
            {
                jsonobject.addProperty("max", this.max);
            }

            return jsonobject;
        }
    }

    protected static <T extends Number, R extends MinMaxBounds<T>> R fromJson(@Nullable JsonElement element, R defaultIn, BiFunction<JsonElement, String, T> biFunction, MinMaxBounds.IBoundFactory<T, R> boundedFactory)
    {
        if (element != null && !element.isJsonNull())
        {
            if (JSONUtils.isNumber(element))
            {
                T t2 = biFunction.apply(element, "value");
                return boundedFactory.create(t2, t2);
            }
            else
            {
                JsonObject jsonobject = JSONUtils.getJsonObject(element, "value");
                T t = jsonobject.has("min") ? biFunction.apply(jsonobject.get("min"), "min") : null;
                T t1 = jsonobject.has("max") ? biFunction.apply(jsonobject.get("max"), "max") : null;
                return boundedFactory.create(t, t1);
            }
        }
        else
        {
            return defaultIn;
        }
    }

    protected static <T extends Number, R extends MinMaxBounds<T>> R fromReader(StringReader reader, MinMaxBounds.IBoundReader<T, R> minMaxReader, Function<String, T> valueFunction, Supplier<DynamicCommandExceptionType> commandExceptionSupplier, Function<T, T> function) throws CommandSyntaxException
    {
        if (!reader.canRead())
        {
            throw ERROR_EMPTY.createWithContext(reader);
        }
        else
        {
            int i = reader.getCursor();

            try
            {
                T t = optionallyFormat(readNumber(reader, valueFunction, commandExceptionSupplier), function);
                T t1;

                if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.')
                {
                    reader.skip();
                    reader.skip();
                    t1 = optionallyFormat(readNumber(reader, valueFunction, commandExceptionSupplier), function);

                    if (t == null && t1 == null)
                    {
                        throw ERROR_EMPTY.createWithContext(reader);
                    }
                }
                else
                {
                    t1 = t;
                }

                if (t == null && t1 == null)
                {
                    throw ERROR_EMPTY.createWithContext(reader);
                }
                else
                {
                    return minMaxReader.create(reader, t, t1);
                }
            }
            catch (CommandSyntaxException commandsyntaxexception)
            {
                reader.setCursor(i);
                throw new CommandSyntaxException(commandsyntaxexception.getType(), commandsyntaxexception.getRawMessage(), commandsyntaxexception.getInput(), i);
            }
        }
    }

    @Nullable
    private static <T extends Number> T readNumber(StringReader reader, Function<String, T> stringToValueFunction, Supplier<DynamicCommandExceptionType> commandExceptionSupplier) throws CommandSyntaxException
    {
        int i = reader.getCursor();

        while (reader.canRead() && isAllowedInputChat(reader))
        {
            reader.skip();
        }

        String s = reader.getString().substring(i, reader.getCursor());

        if (s.isEmpty())
        {
            return (T)null;
        }
        else
        {
            try
            {
                return stringToValueFunction.apply(s);
            }
            catch (NumberFormatException numberformatexception)
            {
                throw commandExceptionSupplier.get().createWithContext(reader, s);
            }
        }
    }

    private static boolean isAllowedInputChat(StringReader reader)
    {
        char c0 = reader.peek();

        if ((c0 < '0' || c0 > '9') && c0 != '-')
        {
            if (c0 != '.')
            {
                return false;
            }
            else
            {
                return !reader.canRead(2) || reader.peek(1) != '.';
            }
        }
        else
        {
            return true;
        }
    }

    @Nullable
    private static <T> T optionallyFormat(@Nullable T value, Function<T, T> formatterFunction)
    {
        return (T)(value == null ? null : formatterFunction.apply(value));
    }

    public static class FloatBound extends MinMaxBounds<Float>
    {
        public static final MinMaxBounds.FloatBound UNBOUNDED = new MinMaxBounds.FloatBound((Float)null, (Float)null);
        private final Double minSquared;
        private final Double maxSquared;

        private static MinMaxBounds.FloatBound create(StringReader reader, @Nullable Float min, @Nullable Float max) throws CommandSyntaxException
        {
            if (min != null && max != null && min > max)
            {
                throw ERROR_SWAPPED.createWithContext(reader);
            }
            else
            {
                return new MinMaxBounds.FloatBound(min, max);
            }
        }

        @Nullable
        private static Double square(@Nullable Float value)
        {
            return value == null ? null : value.doubleValue() * value.doubleValue();
        }

        private FloatBound(@Nullable Float min, @Nullable Float max)
        {
            super(min, max);
            this.minSquared = square(min);
            this.maxSquared = square(max);
        }

        public static MinMaxBounds.FloatBound atLeast(float value)
        {
            return new MinMaxBounds.FloatBound(value, (Float)null);
        }

        public boolean test(float value)
        {
            if (this.min != null && this.min > value)
            {
                return false;
            }
            else
            {
                return this.max == null || !(this.max < value);
            }
        }

        public boolean testSquared(double value)
        {
            if (this.minSquared != null && this.minSquared > value)
            {
                return false;
            }
            else
            {
                return this.maxSquared == null || !(this.maxSquared < value);
            }
        }

        public static MinMaxBounds.FloatBound fromJson(@Nullable JsonElement element)
        {
            return fromJson(element, UNBOUNDED, JSONUtils::getFloat, MinMaxBounds.FloatBound::new);
        }

        public static MinMaxBounds.FloatBound fromReader(StringReader reader) throws CommandSyntaxException
        {
            return fromReader(reader, (floatValue) ->
            {
                return floatValue;
            });
        }

        public static MinMaxBounds.FloatBound fromReader(StringReader reader, Function<Float, Float> valueFunction) throws CommandSyntaxException
        {
            return fromReader(reader, MinMaxBounds.FloatBound::create, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, valueFunction);
        }
    }

    @FunctionalInterface
    public interface IBoundFactory<T extends Number, R extends MinMaxBounds<T>>
    {
        R create(@Nullable T p_create_1_, @Nullable T p_create_2_);
    }

    @FunctionalInterface
    public interface IBoundReader<T extends Number, R extends MinMaxBounds<T>>
    {
        R create(StringReader p_create_1_, @Nullable T p_create_2_, @Nullable T p_create_3_) throws CommandSyntaxException;
    }

    public static class IntBound extends MinMaxBounds<Integer>
    {
        public static final MinMaxBounds.IntBound UNBOUNDED = new MinMaxBounds.IntBound((Integer)null, (Integer)null);
        private final Long minSquared;
        private final Long maxSquared;

        private static MinMaxBounds.IntBound create(StringReader reader, @Nullable Integer min, @Nullable Integer max) throws CommandSyntaxException
        {
            if (min != null && max != null && min > max)
            {
                throw ERROR_SWAPPED.createWithContext(reader);
            }
            else
            {
                return new MinMaxBounds.IntBound(min, max);
            }
        }

        @Nullable
        private static Long square(@Nullable Integer value)
        {
            return value == null ? null : value.longValue() * value.longValue();
        }

        private IntBound(@Nullable Integer min, @Nullable Integer max)
        {
            super(min, max);
            this.minSquared = square(min);
            this.maxSquared = square(max);
        }

        public static MinMaxBounds.IntBound exactly(int value)
        {
            return new MinMaxBounds.IntBound(value, value);
        }

        public static MinMaxBounds.IntBound atLeast(int value)
        {
            return new MinMaxBounds.IntBound(value, (Integer)null);
        }

        public boolean test(int value)
        {
            if (this.min != null && this.min > value)
            {
                return false;
            }
            else
            {
                return this.max == null || this.max >= value;
            }
        }

        public static MinMaxBounds.IntBound fromJson(@Nullable JsonElement element)
        {
            return fromJson(element, UNBOUNDED, JSONUtils::getInt, MinMaxBounds.IntBound::new);
        }

        public static MinMaxBounds.IntBound fromReader(StringReader reader) throws CommandSyntaxException
        {
            return fromReader(reader, (integer) ->
            {
                return integer;
            });
        }

        public static MinMaxBounds.IntBound fromReader(StringReader reader, Function<Integer, Integer> valueFunction) throws CommandSyntaxException
        {
            return fromReader(reader, MinMaxBounds.IntBound::create, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, valueFunction);
        }
    }
}
