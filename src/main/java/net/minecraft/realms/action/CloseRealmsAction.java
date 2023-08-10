package net.minecraft.realms.action;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.minecraft.util.text.TranslationTextComponent;

public class CloseRealmsAction extends LongRunningTask
{
    private final RealmsServer field_238107_c_;
    private final RealmsConfigureWorldScreen field_238108_d_;

    public CloseRealmsAction(RealmsServer p_i232228_1_, RealmsConfigureWorldScreen p_i232228_2_)
    {
        this.field_238107_c_ = p_i232228_1_;
        this.field_238108_d_ = p_i232228_2_;
    }

    public void run()
    {
        this.func_224989_b(new TranslationTextComponent("mco.configure.world.closing"));
        RealmsClient realmsclient = RealmsClient.func_224911_a();

        for (int i = 0; i < 25; ++i)
        {
            if (this.func_224988_a())
            {
                return;
            }

            try
            {
                boolean flag = realmsclient.func_224932_f(this.field_238107_c_.field_230582_a_);

                if (flag)
                {
                    this.field_238108_d_.func_224398_a();
                    this.field_238107_c_.field_230586_e_ = RealmsServer.Status.CLOSED;
                    func_238127_a_(this.field_238108_d_);
                    break;
                }
            }
            catch (RetryCallException retrycallexception)
            {
                if (this.func_224988_a())
                {
                    return;
                }

                func_238125_a_(retrycallexception.field_224985_e);
            }
            catch (Exception exception)
            {
                if (this.func_224988_a())
                {
                    return;
                }

                field_238124_a_.error("Failed to close server", (Throwable)exception);
                this.func_237703_a_("Failed to close the server");
            }
        }
    }
}
