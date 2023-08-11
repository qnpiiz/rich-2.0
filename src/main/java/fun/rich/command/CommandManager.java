package fun.rich.command;

import com.google.common.collect.Lists;
import fun.rich.command.impl.*;
import fun.rich.event.EventManager;
import lombok.Getter;

import java.util.List;

@Getter
public class CommandManager {

    private final List<Command> commands;

    public CommandManager() {
        commands = Lists.newLinkedList();
        EventManager.register(new CommandHandler(this));
        commands.add(new ConfigCommand());
        commands.add(new MacroCommand());
        commands.add(new FriendCommand());
        commands.add(new HelpCommand());
        commands.add(new PanicCommand());
        commands.add(new BindCommand());
    }

    public boolean execute(String args) {
        String noPrefix = args.substring(1);
        String[] split = noPrefix.split(" ");
        if (split.length > 0) {
            for (Command command : commands) {
                CommandAbstract abstractCommand = (CommandAbstract) command;
                String[] commandAliases = abstractCommand.getAliases();
                for (String alias : commandAliases) {
                    if (split[0].equalsIgnoreCase(alias)) {
                        abstractCommand.execute(split);
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
