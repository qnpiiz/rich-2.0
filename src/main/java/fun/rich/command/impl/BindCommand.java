package fun.rich.command.impl;

import fun.rich.Rich;
import fun.rich.command.CommandAbstract;
import fun.rich.feature.Feature;
import fun.rich.ui.notification.NotificationMode;
import fun.rich.ui.notification.NotificationRenderer;
import fun.rich.utils.other.ChatUtils;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.TextFormatting;

public class BindCommand extends CommandAbstract {

    public BindCommand() {
        super("bind", "bind", "§6.bind" + TextFormatting.RED + " add " + "§7<name> §7<key> " + TextFormatting.RED + "\n" + "[" + TextFormatting.WHITE + "RICHCLIENT" + TextFormatting.GRAY + "] " + "§6.bind " + TextFormatting.RED + "remove " + "§7<name> §7<key> " + "\n" + "[" + TextFormatting.WHITE + "RICHCLIENT" + TextFormatting.GRAY + "] " + "§6.bind " + TextFormatting.RED + "list ", "bind");
    }

    @Override
    public void execute(String... arguments) {
        try {
            if (arguments.length == 4) {
                String moduleName = arguments[2];
                String bind = arguments[3].toUpperCase();
                Feature feature = Rich.instance.featureManager.getFeature(moduleName);
                if (arguments[0].equalsIgnoreCase("bind") && arguments[1].equalsIgnoreCase("add")) {
                    feature.setBind(InputMappings.getInputByName(bind).getKeyCode());

                    ChatUtils.addChatMessage(TextFormatting.GREEN + feature.getLabel() + TextFormatting.WHITE + " was set on key " + TextFormatting.RED + "\"" + bind + "\"");
                    NotificationRenderer.queue("Bind Manager", TextFormatting.GREEN + feature.getLabel() + TextFormatting.WHITE + " was set on key " + TextFormatting.RED + "\"" + bind + "\"", 1, NotificationMode.SUCCESS);
                } else if (arguments[0].equalsIgnoreCase("bind") && arguments[1].equalsIgnoreCase("remove")) {
                    feature.setBind(0);

                    ChatUtils.addChatMessage(TextFormatting.GREEN + feature.getLabel() + TextFormatting.WHITE + " bind was deleted from key " + TextFormatting.RED + "\"" + bind + "\"");
                    NotificationRenderer.queue("Bind Manager", TextFormatting.GREEN + feature.getLabel() + TextFormatting.WHITE + " bind was deleted from key " + TextFormatting.RED + "\"" + bind + "\"", 1, NotificationMode.SUCCESS);
                }
            } else if (arguments.length == 2) {
                if (arguments[0].equalsIgnoreCase("bind") && arguments[1].equalsIgnoreCase("list")) {
                    for (Feature f : Rich.instance.featureManager.getAllFeatures()) {
                        if (f.getBind() != 0)
                            ChatUtils.addChatMessage(f.getLabel() + " : " + InputMappings.getInputByCode(f.getBind(), 0).getTranslationKey());
                    }
                } else
                    ChatUtils.addChatMessage(this.getUsage());
            } else if (arguments[0].equalsIgnoreCase("bind"))
                ChatUtils.addChatMessage(this.getUsage());

        } catch (Throwable ignored) {}
    }
}
