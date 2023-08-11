package fun.rich.command.impl;

import fun.rich.Rich;
import fun.rich.command.CommandAbstract;
import fun.rich.config.Config;
import fun.rich.config.ConfigManager;
import fun.rich.utils.other.ChatUtils;
import net.minecraft.util.text.TextFormatting;

import java.io.File;

public class ConfigCommand extends CommandAbstract {

    public ConfigCommand() {
        super("cfg", "configurations", TextFormatting.RED + ".cfg" + TextFormatting.WHITE + " save <name> | load <name> | delete <name> | list | create <name> | dir" + TextFormatting.RED, "<name>", "cfg");
    }

    @Override
    public void execute(String... args) {
        try {
            if (args.length >= 2) {
                String upperCase = args[1].toUpperCase();
                if (args.length == 3) {
                    switch (upperCase) {
                        case "LOAD":
                            if (Rich.instance.configManager.loadConfig(args[2]))
                                ChatUtils.addChatMessage(TextFormatting.GREEN + "Successfully " + TextFormatting.WHITE + "loaded config: " + TextFormatting.RED + "\"" + args[2] + "\"");
                            else
                                ChatUtils.addChatMessage(TextFormatting.RED + "Failed " + TextFormatting.WHITE + "load config: " + TextFormatting.RED + "\"" + args[2] + "\"");

                            break;
                        case "SAVE":
                            if (Rich.instance.configManager.saveConfig(args[2])) {
                                Rich.instance.fileManager.saveFiles();
                                ChatUtils.addChatMessage(TextFormatting.GREEN + "Successfully " + TextFormatting.WHITE + "saved config: " + TextFormatting.RED + "\"" + args[2] + "\"");
                                ConfigManager.getLoadedConfigs().clear();
                                Rich.instance.configManager.load();
                            } else
                                ChatUtils.addChatMessage(TextFormatting.RED + "Failed " + TextFormatting.WHITE + "to save config: " + TextFormatting.RED + "\"" + args[2] + "\"");

                            break;
                        case "DELETE":
                            if (Rich.instance.configManager.deleteConfig(args[2]))
                                ChatUtils.addChatMessage(TextFormatting.GREEN + "Successfully " + TextFormatting.WHITE + "deleted config: " + TextFormatting.RED + "\"" + args[2] + "\"");
                            else
                                ChatUtils.addChatMessage(TextFormatting.RED + "Failed " + TextFormatting.WHITE + "to delete config: " + TextFormatting.RED + "\"" + args[2] + "\"");

                            break;
                        case "CREATE":
                            Rich.instance.configManager.saveConfig(args[2]);
                            ChatUtils.addChatMessage(TextFormatting.GREEN + "Successfully " + TextFormatting.WHITE + "created config: " + TextFormatting.RED + "\"" + args[2] + "\"");
                            break;
                    }
                } else if (args.length == 2 && upperCase.equalsIgnoreCase("LIST")) {
                    ChatUtils.addChatMessage(TextFormatting.GREEN + "Configs:");
                    for (Config config : Rich.instance.configManager.getContents())
                        ChatUtils.addChatMessage(TextFormatting.RED + config.getName());
                } else if (args.length == 2 && upperCase.equalsIgnoreCase("DIR")) {
                    File file = new File("C:\\RichClient\\game\\configs", "configs");
                    Runtime.getRuntime().exec("explorer.exe /select,".concat(file.getAbsolutePath()));
                }
            } else
                ChatUtils.addChatMessage(this.getUsage());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
