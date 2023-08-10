package net.minecraft.realms.action;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.gui.LongRunningTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.realms.RealmsConnect;
import net.minecraft.util.text.TranslationTextComponent;

public class ConnectedToRealmsAction extends LongRunningTask
{
    private final RealmsConnect field_238109_c_;
    private final RealmsServer field_244784_d;
    private final RealmsServerAddress field_238110_d_;

    public ConnectedToRealmsAction(Screen p_i242127_1_, RealmsServer p_i242127_2_, RealmsServerAddress p_i242127_3_)
    {
        this.field_244784_d = p_i242127_2_;
        this.field_238110_d_ = p_i242127_3_;
        this.field_238109_c_ = new RealmsConnect(p_i242127_1_);
    }

    public void run()
    {
        this.func_224989_b(new TranslationTextComponent("mco.connect.connecting"));
        net.minecraft.realms.RealmsServerAddress realmsserveraddress = net.minecraft.realms.RealmsServerAddress.func_231413_a_(this.field_238110_d_.field_230601_a_);
        this.field_238109_c_.func_244798_a(this.field_244784_d, realmsserveraddress.func_231412_a_(), realmsserveraddress.func_231414_b_());
    }

    public void func_224992_d()
    {
        this.field_238109_c_.func_231396_a_();
        Minecraft.getInstance().getPackFinder().clearResourcePack();
    }

    public void func_224990_b()
    {
        this.field_238109_c_.func_231398_b_();
    }
}
