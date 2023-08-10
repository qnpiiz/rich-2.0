package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

public class AngleArgument implements ArgumentType<AngleArgument.Result>
{
    private static final Collection<String> field_242990_b = Arrays.asList("0", "~", "~-5");
    public static final SimpleCommandExceptionType field_242989_a = new SimpleCommandExceptionType(new TranslationTextComponent("argument.angle.incomplete"));

    public static AngleArgument func_242991_a()
    {
        return new AngleArgument();
    }

    public static float func_242992_a(CommandContext<CommandSource> p_242992_0_, String p_242992_1_)
    {
        return p_242992_0_.getArgument(p_242992_1_, AngleArgument.Result.class).func_242995_a(p_242992_0_.getSource());
    }

    public AngleArgument.Result parse(StringReader p_parse_1_) throws CommandSyntaxException
    {
        if (!p_parse_1_.canRead())
        {
            throw field_242989_a.createWithContext(p_parse_1_);
        }
        else
        {
            boolean flag = LocationPart.isRelative(p_parse_1_);
            float f = p_parse_1_.canRead() && p_parse_1_.peek() != ' ' ? p_parse_1_.readFloat() : 0.0F;
            return new AngleArgument.Result(f, flag);
        }
    }

    public Collection<String> getExamples()
    {
        return field_242990_b;
    }

    public static final class Result
    {
        private final float field_242993_a;
        private final boolean field_242994_b;

        private Result(float p_i242044_1_, boolean p_i242044_2_)
        {
            this.field_242993_a = p_i242044_1_;
            this.field_242994_b = p_i242044_2_;
        }

        public float func_242995_a(CommandSource p_242995_1_)
        {
            return MathHelper.wrapDegrees(this.field_242994_b ? this.field_242993_a + p_242995_1_.getRotation().y : this.field_242993_a);
        }
    }
}
