package fun.rich.utils.other;

import net.minecraft.client.Minecraft;

public class ChatUtils {

    public static String chatPrefix = "[Rich] ";

    public static void addChatMessage(String message) {
        Minecraft.getInstance().ingameGUI.getChatGUI().addToSentMessages(chatPrefix.concat(message));
    }
}
