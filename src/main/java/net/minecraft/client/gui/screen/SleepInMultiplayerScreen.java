package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.text.TranslationTextComponent;

public class SleepInMultiplayerScreen extends ChatScreen
{
    public SleepInMultiplayerScreen()
    {
        super("");
    }

    protected void init()
    {
        super.init();
        this.addButton(new Button(this.width / 2 - 100, this.height - 40, 200, 20, new TranslationTextComponent("multiplayer.stopSleeping"), (p_212998_1_) ->
        {
            this.wakeFromSleep();
        }));
    }

    public void closeScreen()
    {
        this.wakeFromSleep();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.wakeFromSleep();
        }
        else if (keyCode == 257 || keyCode == 335)
        {
            String s = this.inputField.getText().trim();

            if (!s.isEmpty())
            {
                this.sendMessage(s);
            }

            this.inputField.setText("");
            this.mc.ingameGUI.getChatGUI().resetScroll();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void wakeFromSleep()
    {
        ClientPlayNetHandler clientplaynethandler = this.mc.player.connection;
        clientplaynethandler.sendPacket(new CEntityActionPacket(this.mc.player, CEntityActionPacket.Action.STOP_SLEEPING));
    }
}
