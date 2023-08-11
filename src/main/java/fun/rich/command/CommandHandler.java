package fun.rich.command;

import fun.rich.event.EventTarget;
import fun.rich.event.events.impl.player.EventMessage;

public class CommandHandler {

    public CommandManager commandManager;

    public CommandHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @EventTarget
    public void onMessage(EventMessage event) {
        String msg = event.getMessage();
        if (msg.startsWith("."))
            event.setCancelled(commandManager.execute(msg));
    }
}
