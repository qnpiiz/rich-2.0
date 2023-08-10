package net.minecraft.realms.action;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

public class OpeningWorldRealmsAction extends LongRunningTask
{
    private final RealmsServer field_238128_c_;
    private final Screen field_238129_d_;
    private final boolean field_238130_e_;
    private final RealmsMainScreen field_238131_f_;

    public OpeningWorldRealmsAction(RealmsServer p_i232232_1_, Screen p_i232232_2_, RealmsMainScreen p_i232232_3_, boolean p_i232232_4_)
    {
        this.field_238128_c_ = p_i232232_1_;
        this.field_238129_d_ = p_i232232_2_;
        this.field_238130_e_ = p_i232232_4_;
        this.field_238131_f_ = p_i232232_3_;
    }

    public void run()
    {
        this.func_224989_b(new TranslationTextComponent("mco.configure.world.opening"));
        RealmsClient realmsclient = RealmsClient.func_224911_a();

        for (int i = 0; i < 25; ++i)
        {
            if (this.func_224988_a())
            {
                return;
            }

            try
            {
                boolean flag = realmsclient.func_224942_e(this.field_238128_c_.field_230582_a_);

                if (flag)
                {
                    if (this.field_238129_d_ instanceof RealmsConfigureWorldScreen)
                    {
                        ((RealmsConfigureWorldScreen)this.field_238129_d_).func_224398_a();
                    }

                    this.field_238128_c_.field_230586_e_ = RealmsServer.Status.OPEN;

                    if (this.field_238130_e_)
                    {
                        this.field_238131_f_.func_223911_a(this.field_238128_c_, this.field_238129_d_);
                    }
                    else
                    {
                        func_238127_a_(this.field_238129_d_);
                    }

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

                field_238124_a_.error("Failed to open server", (Throwable)exception);
                this.func_237703_a_("Failed to open the server");
            }
        }
    }
}
