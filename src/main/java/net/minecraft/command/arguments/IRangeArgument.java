package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.CommandSource;

public interface IRangeArgument < T extends MinMaxBounds<? >> extends ArgumentType<T>
{
    /**
     * Note: this class is missing several fields and methods due to them not being used; in particular FloatRange has
     * no way of being created.
     */
    static IRangeArgument.IntRange intRange()
    {
        return new IRangeArgument.IntRange();
    }

    static IRangeArgument.FloatRange func_243493_b()
    {
        return new IRangeArgument.FloatRange();
    }

    public static class FloatRange implements IRangeArgument<MinMaxBounds.FloatBound>
    {
        private static final Collection<String> EXAMPLES = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

        public MinMaxBounds.FloatBound parse(StringReader p_parse_1_) throws CommandSyntaxException
        {
            return MinMaxBounds.FloatBound.fromReader(p_parse_1_);
        }

        public Collection<String> getExamples()
        {
            return EXAMPLES;
        }
    }

    public static class IntRange implements IRangeArgument<MinMaxBounds.IntBound>
    {
        private static final Collection<String> EXAMPLES = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

        public static MinMaxBounds.IntBound getIntRange(CommandContext<CommandSource> context, String name)
        {
            return context.getArgument(name, MinMaxBounds.IntBound.class);
        }

        public MinMaxBounds.IntBound parse(StringReader p_parse_1_) throws CommandSyntaxException
        {
            return MinMaxBounds.IntBound.fromReader(p_parse_1_);
        }

        public Collection<String> getExamples()
        {
            return EXAMPLES;
        }
    }
}
