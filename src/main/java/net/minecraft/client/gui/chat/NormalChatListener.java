package net.minecraft.client.gui.chat;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;

public class NormalChatListener implements IChatListener
{
    private final Minecraft mc;

    public NormalChatListener(Minecraft minecraft)
    {
        this.mc = minecraft;
    }

    /**
     * Called whenever this listener receives a chat message, if this listener is registered to the given type in {@link
     * net.minecraft.client.gui.GuiIngame#chatListeners chatListeners}
     */
    public void say(ChatType chatTypeIn, ITextComponent message, UUID sender)
    {
        if (chatTypeIn != ChatType.CHAT)
        {
            this.mc.ingameGUI.getChatGUI().printChatMessage(message);
        }
        else
        {
            this.mc.ingameGUI.getChatGUI().func_238495_b_(message);
        }
    }
}
