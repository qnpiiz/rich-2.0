package net.minecraft.advancements.criterion;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.text.TranslationTextComponent;

public class MinMaxBoundsWrapped
{
    public static final MinMaxBoundsWrapped UNBOUNDED = new MinMaxBoundsWrapped((Float)null, (Float)null);
    public static final SimpleCommandExceptionType ERROR_INTS_ONLY = new SimpleCommandExceptionType(new TranslationTextComponent("argument.range.ints"));
    private final Float min;
    private final Float max;

    public MinMaxBoundsWrapped(@Nullable Float min, @Nullable Float max)
    {
        this.min = min;
        this.max = max;
    }

    @Nullable
    public Float getMin()
    {
        return this.min;
    }

    @Nullable
    public Float getMax()
    {
        return this.max;
    }

    public static MinMaxBoundsWrapped fromReader(StringReader reader, boolean isFloatingPoint, Function<Float, Float> valueFunction) throws CommandSyntaxException
    {
        if (!reader.canRead())
        {
            throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
        }
        else
        {
            int i = reader.getCursor();
            Float f = map(fromReader(reader, isFloatingPoint), valueFunction);
            Float f1;

            if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.')
            {
                reader.skip();
                reader.skip();
                f1 = map(fromReader(reader, isFloatingPoint), valueFunction);

                if (f == null && f1 == null)
                {
                    reader.setCursor(i);
                    throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
                }
            }
            else
            {
                if (!isFloatingPoint && reader.canRead() && reader.peek() == '.')
                {
                    reader.setCursor(i);
                    throw ERROR_INTS_ONLY.createWithContext(reader);
                }

                f1 = f;
            }

            if (f == null && f1 == null)
            {
                reader.setCursor(i);
                throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
            }
            else
            {
                return new MinMaxBoundsWrapped(f, f1);
            }
        }
    }

    @Nullable
    private static Float fromReader(StringReader reader, boolean isFloatingPoint) throws CommandSyntaxException
    {
        int i = reader.getCursor();

        while (reader.canRead() && isValidNumber(reader, isFloatingPoint))
        {
            reader.skip();
        }

        String s = reader.getString().substring(i, reader.getCursor());

        if (s.isEmpty())
        {
            return null;
        }
        else
        {
            try
            {
                return Float.parseFloat(s);
            }
            catch (NumberFormatException numberformatexception)
            {
                if (isFloatingPoint)
                {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(reader, s);
                }
                else
                {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(reader, s);
                }
            }
        }
    }

    private static boolean isValidNumber(StringReader reader, boolean isFloatingPoint)
    {
        char c0 = reader.peek();

        if ((c0 < '0' || c0 > '9') && c0 != '-')
        {
            if (isFloatingPoint && c0 == '.')
            {
                return !reader.canRead(2) || reader.peek(1) != '.';
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    @Nullable
    private static Float map(@Nullable Float value, Function<Float, Float> valueFunction)
    {
        return value == null ? null : valueFunction.apply(value);
    }
}
