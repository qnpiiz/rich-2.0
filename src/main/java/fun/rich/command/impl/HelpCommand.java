package fun.rich.command.impl;

import fun.rich.command.CommandAbstract;
import fun.rich.utils.other.ChatUtils;
import net.minecraft.util.text.TextFormatting;

public class HelpCommand extends CommandAbstract {
    
    public HelpCommand() {
        super("help", "help", ".help", "help");
    }

    @Override
    public void execute(String... args) {
        if (args.length == 1) {
            if (args[0].equals("help")) {
                ChatUtils.addChatMessage(TextFormatting.RED + "All Commands:");
                ChatUtils.addChatMessage(TextFormatting.WHITE + ".bind");
                ChatUtils.addChatMessage(TextFormatting.WHITE + ".macro");
                ChatUtils.addChatMessage(TextFormatting.WHITE + ".gps");
                ChatUtils.addChatMessage(TextFormatting.WHITE + ".parser");
                ChatUtils.addChatMessage(TextFormatting.WHITE + ".vclip | .hclip");
                ChatUtils.addChatMessage(TextFormatting.WHITE + ".fakename");
                ChatUtils.addChatMessage(TextFormatting.WHITE + ".friend");
                ChatUtils.addChatMessage(TextFormatting.WHITE + ".cfg");
            }
        } else
            ChatUtils.addChatMessage(this.getUsage());
    }
}
