package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;

public class UUIDArgument implements ArgumentType<UUID>
{
    public static final SimpleCommandExceptionType field_239191_a_ = new SimpleCommandExceptionType(new TranslationTextComponent("argument.uuid.invalid"));
    private static final Collection<String> field_239192_b_ = Arrays.asList("dd12be42-52a9-4a91-a8a1-11c01849e498");
    private static final Pattern field_239193_c_ = Pattern.compile("^([-A-Fa-f0-9]+)");

    public static UUID func_239195_a_(CommandContext<CommandSource> p_239195_0_, String p_239195_1_)
    {
        return p_239195_0_.getArgument(p_239195_1_, UUID.class);
    }

    public static UUIDArgument func_239194_a_()
    {
        return new UUIDArgument();
    }

    public UUID parse(StringReader p_parse_1_) throws CommandSyntaxException
    {
        String s = p_parse_1_.getRemaining();
        Matcher matcher = field_239193_c_.matcher(s);

        if (matcher.find())
        {
            String s1 = matcher.group(1);

            try
            {
                UUID uuid = UUID.fromString(s1);
                p_parse_1_.setCursor(p_parse_1_.getCursor() + s1.length());
                return uuid;
            }
            catch (IllegalArgumentException illegalargumentexception)
            {
            }
        }

        throw field_239191_a_.create();
    }

    public Collection<String> getExamples()
    {
        return field_239192_b_;
    }
}
