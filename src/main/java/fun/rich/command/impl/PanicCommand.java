package fun.rich.command.impl;

import fun.rich.Rich;
import fun.rich.command.CommandAbstract;
import fun.rich.feature.Feature;
import fun.rich.utils.other.ChatUtils;
import net.minecraft.util.text.TextFormatting;

public class PanicCommand extends CommandAbstract {

    public PanicCommand() {
        super("panic", "Disabled all modules", ".panic", "panic");
    }

    @Override
    public void execute(String... args) {
        if (args[0].equalsIgnoreCase("panic")) {
            for (Feature feature : Rich.instance.featureManager.getAllFeatures()) {
                if (feature.isEnabled())
                    feature.toggle();
            }
            
            ChatUtils.addChatMessage(TextFormatting.GREEN + "успешно " + TextFormatting.RED + "выключены " + TextFormatting.WHITE + "все модули");
        }
    }
}
