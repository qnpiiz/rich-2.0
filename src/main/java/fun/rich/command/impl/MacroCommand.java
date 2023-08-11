package fun.rich.command.impl;

import fun.rich.Rich;
import fun.rich.command.CommandAbstract;
import fun.rich.files.impl.MacroConfig;
import fun.rich.macro.Macro;
import fun.rich.utils.other.ChatUtils;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.TextFormatting;

public class MacroCommand extends CommandAbstract {

    public MacroCommand() {
        super("macros", "macro", TextFormatting.GRAY + ".macro" + TextFormatting.WHITE + " add " + "�3<key> /home_home | �7.macro" + TextFormatting.WHITE + " remove " + "�3<key> |" +  TextFormatting.GRAY + " .macro" + TextFormatting.WHITE + " clear " + "�3| �7.macro" + TextFormatting.WHITE + " list", "�7.macro" + TextFormatting.WHITE + " add " + "�3<key> </home_home> | �7.macro" + TextFormatting.WHITE + " remove " + "�3<key> | �7.macro" + TextFormatting.WHITE + " clear " + "| �7.macro" + TextFormatting.WHITE + " list", "macro");
    }

    @Override
    public void execute(String... arguments) {
        try {
            if (arguments.length > 1) {
                if (arguments[0].equals("macro")) {
                    if (arguments[1].equals("add")) {
                        StringBuilder command = new StringBuilder();
                        for (int i = 3; i < arguments.length; ++i)
                            command.append(arguments[i]).append(" ");

                        Rich.instance.macroManager.addMacro(new Macro(command.toString(), InputMappings.getInputByName(arguments[2].toUpperCase()).getKeyCode()));
                        Rich.instance.fileManager.getFile(MacroConfig.class).saveFile();
                        ChatUtils.addChatMessage(TextFormatting.GREEN + "Added" + " macros for key" + TextFormatting.RED + " \"" + arguments[2].toUpperCase() + TextFormatting.RED + "\" " + TextFormatting.WHITE + "with value " + TextFormatting.RED + command);
                    }
                    if (arguments[1].equals("clear")) {
                        if (Rich.instance.macroManager.getMacros().isEmpty()) {
                            ChatUtils.addChatMessage(TextFormatting.RED + "Your macros list is empty!");
                            return;
                        }

                        Rich.instance.macroManager.getMacros().clear();
                        ChatUtils.addChatMessage(TextFormatting.GREEN + "Your macros list " + TextFormatting.WHITE + " successfully cleared!");
                    }
                    if (arguments[1].equals("remove")) {
                        Rich.instance.macroManager.deleteMacroByKey(InputMappings.getInputByName(arguments[2].toUpperCase()).getKeyCode());
                        ChatUtils.addChatMessage(TextFormatting.GREEN + "Macro " + TextFormatting.WHITE + "was deleted from key " + TextFormatting.RED + "\"" + arguments[2].toUpperCase() + "\"");
                    }
                    if (arguments[1].equals("list")) {
                        if (Rich.instance.macroManager.getMacros().isEmpty()) {
                            ChatUtils.addChatMessage(TextFormatting.RED + "Your macros list is empty!");
                            return;
                        }

                        Rich.instance.macroManager.getMacros().forEach(macro -> ChatUtils.addChatMessage(TextFormatting.GREEN + "Macros list: " + TextFormatting.WHITE + "Macros Name: " + TextFormatting.RED + macro.getValue() + TextFormatting.WHITE + ", Macro Bind: " + TextFormatting.RED + Keyboard.getKeyName(macro.getKey())));
                    }
                }
            } else
                ChatUtils.addChatMessage(getUsage());
        } catch (Throwable ignored) {}
    }
}
