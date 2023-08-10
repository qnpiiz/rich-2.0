package net.minecraft.realms.action;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import net.minecraft.util.text.TranslationTextComponent;

public class RestoringBackupRealmsAction extends LongRunningTask
{
    private final Backup field_238139_c_;
    private final long field_238140_d_;
    private final RealmsConfigureWorldScreen field_238141_e_;

    public RestoringBackupRealmsAction(Backup p_i232234_1_, long p_i232234_2_, RealmsConfigureWorldScreen p_i232234_4_)
    {
        this.field_238139_c_ = p_i232234_1_;
        this.field_238140_d_ = p_i232234_2_;
        this.field_238141_e_ = p_i232234_4_;
    }

    public void run()
    {
        this.func_224989_b(new TranslationTextComponent("mco.backup.restoring"));
        RealmsClient realmsclient = RealmsClient.func_224911_a();
        int i = 0;

        while (i < 25)
        {
            try
            {
                if (this.func_224988_a())
                {
                    return;
                }

                realmsclient.func_224928_c(this.field_238140_d_, this.field_238139_c_.field_230553_a_);
                func_238125_a_(1);

                if (this.func_224988_a())
                {
                    return;
                }

                func_238127_a_(this.field_238141_e_.func_224407_b());
                return;
            }
            catch (RetryCallException retrycallexception)
            {
                if (this.func_224988_a())
                {
                    return;
                }

                func_238125_a_(retrycallexception.field_224985_e);
                ++i;
            }
            catch (RealmsServiceException realmsserviceexception)
            {
                if (this.func_224988_a())
                {
                    return;
                }

                field_238124_a_.error("Couldn't restore backup", (Throwable)realmsserviceexception);
                func_238127_a_(new RealmsGenericErrorScreen(realmsserviceexception, this.field_238141_e_));
                return;
            }
            catch (Exception exception)
            {
                if (this.func_224988_a())
                {
                    return;
                }

                field_238124_a_.error("Couldn't restore backup", (Throwable)exception);
                this.func_237703_a_(exception.getLocalizedMessage());
                return;
            }
        }
    }
}
