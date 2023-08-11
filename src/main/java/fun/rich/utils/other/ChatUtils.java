package fun.rich.utils.other;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

public class ChatUtils {

    public static String chatPrefix = "[Rich] ";

    public static void addChatMessage(String message) {
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(chatPrefix.concat(message)));
    }
}
