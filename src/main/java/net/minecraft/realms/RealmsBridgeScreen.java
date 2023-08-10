package net.minecraft.realms;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

public class RealmsBridgeScreen extends RealmsScreen
{
    private Screen field_230718_a_;

    public void func_231394_a_(Screen p_231394_1_)
    {
        this.field_230718_a_ = p_231394_1_;
        Minecraft.getInstance().displayGuiScreen(new RealmsMainScreen(this));
    }

    @Nullable
    public RealmsScreen func_239555_b_(Screen p_239555_1_)
    {
        this.field_230718_a_ = p_239555_1_;
        return new RealmsNotificationsScreen();
    }

    public void init()
    {
        Minecraft.getInstance().displayGuiScreen(this.field_230718_a_);
    }
}
