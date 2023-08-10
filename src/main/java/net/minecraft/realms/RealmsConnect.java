package net.minecraft.realms;

import com.mojang.realmsclient.dto.RealmsServer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.login.ClientLoginNetHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsConnect
{
    private static final Logger field_230719_a_ = LogManager.getLogger();
    private final Screen field_230720_b_;
    private volatile boolean field_230721_c_;
    private NetworkManager field_230722_d_;

    public RealmsConnect(Screen p_i232500_1_)
    {
        this.field_230720_b_ = p_i232500_1_;
    }

    public void func_244798_a(final RealmsServer p_244798_1_, final String p_244798_2_, final int p_244798_3_)
    {
        final Minecraft minecraft = Minecraft.getInstance();
        minecraft.setConnectedToRealms(true);
        RealmsNarratorHelper.func_239550_a_(I18n.format("mco.connect.success"));
        (new Thread("Realms-connect-task")
        {
            public void run()
            {
                InetAddress inetaddress = null;

                try
                {
                    inetaddress = InetAddress.getByName(p_244798_2_);

                    if (RealmsConnect.this.field_230721_c_)
                    {
                        return;
                    }

                    RealmsConnect.this.field_230722_d_ = NetworkManager.createNetworkManagerAndConnect(inetaddress, p_244798_3_, minecraft.gameSettings.isUsingNativeTransport());

                    if (RealmsConnect.this.field_230721_c_)
                    {
                        return;
                    }

                    RealmsConnect.this.field_230722_d_.setNetHandler(new ClientLoginNetHandler(RealmsConnect.this.field_230722_d_, minecraft, RealmsConnect.this.field_230720_b_, (p_209500_0_) ->
                    {
                    }));

                    if (RealmsConnect.this.field_230721_c_)
                    {
                        return;
                    }

                    RealmsConnect.this.field_230722_d_.sendPacket(new CHandshakePacket(p_244798_2_, p_244798_3_, ProtocolType.LOGIN));

                    if (RealmsConnect.this.field_230721_c_)
                    {
                        return;
                    }

                    RealmsConnect.this.field_230722_d_.sendPacket(new CLoginStartPacket(minecraft.getSession().getProfile()));
                    minecraft.setServerData(p_244798_1_.func_244783_d(p_244798_2_));
                }
                catch (UnknownHostException unknownhostexception)
                {
                    minecraft.getPackFinder().clearResourcePack();

                    if (RealmsConnect.this.field_230721_c_)
                    {
                        return;
                    }

                    RealmsConnect.field_230719_a_.error("Couldn't connect to world", (Throwable)unknownhostexception);
                    DisconnectedRealmsScreen disconnectedrealmsscreen = new DisconnectedRealmsScreen(RealmsConnect.this.field_230720_b_, DialogTexts.CONNECTION_FAILED, new TranslationTextComponent("disconnect.genericReason", "Unknown host '" + p_244798_2_ + "'"));
                    minecraft.execute(() ->
                    {
                        minecraft.displayGuiScreen(disconnectedrealmsscreen);
                    });
                }
                catch (Exception exception)
                {
                    minecraft.getPackFinder().clearResourcePack();

                    if (RealmsConnect.this.field_230721_c_)
                    {
                        return;
                    }

                    RealmsConnect.field_230719_a_.error("Couldn't connect to world", (Throwable)exception);
                    String s = exception.toString();

                    if (inetaddress != null)
                    {
                        String s1 = inetaddress + ":" + p_244798_3_;
                        s = s.replaceAll(s1, "");
                    }

                    DisconnectedRealmsScreen disconnectedrealmsscreen1 = new DisconnectedRealmsScreen(RealmsConnect.this.field_230720_b_, DialogTexts.CONNECTION_FAILED, new TranslationTextComponent("disconnect.genericReason", s));
                    minecraft.execute(() ->
                    {
                        minecraft.displayGuiScreen(disconnectedrealmsscreen1);
                    });
                }
            }
        }).start();
    }

    public void func_231396_a_()
    {
        this.field_230721_c_ = true;

        if (this.field_230722_d_ != null && this.field_230722_d_.isChannelOpen())
        {
            this.field_230722_d_.closeChannel(new TranslationTextComponent("disconnect.genericReason"));
            this.field_230722_d_.handleDisconnection();
        }
    }

    public void func_231398_b_()
    {
        if (this.field_230722_d_ != null)
        {
            if (this.field_230722_d_.isChannelOpen())
            {
                this.field_230722_d_.tick();
            }
            else
            {
                this.field_230722_d_.handleDisconnection();
            }
        }
    }
}
