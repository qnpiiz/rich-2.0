package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;

public class NBTCompoundTagArgument implements ArgumentType<CompoundNBT>
{
    private static final Collection<String> EXAMPLES = Arrays.asList("{}", "{foo=bar}");

    private NBTCompoundTagArgument()
    {
    }

    public static NBTCompoundTagArgument nbt()
    {
        return new NBTCompoundTagArgument();
    }

    public static <S> CompoundNBT getNbt(CommandContext<S> context, String name)
    {
        return context.getArgument(name, CompoundNBT.class);
    }

    public CompoundNBT parse(StringReader p_parse_1_) throws CommandSyntaxException
    {
        return (new JsonToNBT(p_parse_1_)).readStruct();
    }

    public Collection<String> getExamples()
    {
        return EXAMPLES;
    }
}
