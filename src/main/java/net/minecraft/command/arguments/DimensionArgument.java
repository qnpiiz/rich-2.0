package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DimensionArgument implements ArgumentType<ResourceLocation>
{
    private static final Collection<String> EXAMPLES = Stream.of(World.OVERWORLD, World.THE_NETHER).map((worldKey) ->
    {
        return worldKey.getLocation().toString();
    }).collect(Collectors.toList());
    private static final DynamicCommandExceptionType INVALID_DIMENSION_EXCEPTION = new DynamicCommandExceptionType((worldKey) ->
    {
        return new TranslationTextComponent("argument.dimension.invalid", worldKey);
    });

    public ResourceLocation parse(StringReader p_parse_1_) throws CommandSyntaxException
    {
        return ResourceLocation.read(p_parse_1_);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_)
    {
        return p_listSuggestions_1_.getSource() instanceof ISuggestionProvider ? ISuggestionProvider.func_212476_a(((ISuggestionProvider)p_listSuggestions_1_.getSource()).func_230390_p_().stream().map(RegistryKey::getLocation), p_listSuggestions_2_) : Suggestions.empty();
    }

    public Collection<String> getExamples()
    {
        return EXAMPLES;
    }

    public static DimensionArgument getDimension()
    {
        return new DimensionArgument();
    }

    public static ServerWorld getDimensionArgument(CommandContext<CommandSource> context, String name) throws CommandSyntaxException
    {
        ResourceLocation resourcelocation = context.getArgument(name, ResourceLocation.class);
        RegistryKey<World> registrykey = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, resourcelocation);
        ServerWorld serverworld = context.getSource().getServer().getWorld(registrykey);

        if (serverworld == null)
        {
            throw INVALID_DIMENSION_EXCEPTION.create(resourcelocation);
        }
        else
        {
            return serverworld;
        }
    }
}
