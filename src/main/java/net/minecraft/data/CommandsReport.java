package net.minecraft.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentTypes;

public class CommandsReport implements IDataProvider
{
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator generator;

    public CommandsReport(DataGenerator generatorIn)
    {
        this.generator = generatorIn;
    }

    /**
     * Performs this provider's action.
     */
    public void act(DirectoryCache cache) throws IOException
    {
        Path path = this.generator.getOutputFolder().resolve("reports/commands.json");
        CommandDispatcher<CommandSource> commanddispatcher = (new Commands(Commands.EnvironmentType.ALL)).getDispatcher();
        IDataProvider.save(GSON, cache, ArgumentTypes.serialize(commanddispatcher, commanddispatcher.getRoot()), path);
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    public String getName()
    {
        return "Command Syntax";
    }
}
