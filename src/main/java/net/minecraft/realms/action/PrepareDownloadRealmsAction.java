package net.minecraft.realms.action;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

public class PrepareDownloadRealmsAction extends LongRunningTask
{
    private final long field_238111_c_;
    private final int field_238112_d_;
    private final Screen field_238113_e_;
    private final String field_238114_f_;

    public PrepareDownloadRealmsAction(long p_i232230_1_, int p_i232230_3_, String p_i232230_4_, Screen p_i232230_5_)
    {
        this.field_238111_c_ = p_i232230_1_;
        this.field_238112_d_ = p_i232230_3_;
        this.field_238113_e_ = p_i232230_5_;
        this.field_238114_f_ = p_i232230_4_;
    }

    public void run()
    {
        this.func_224989_b(new TranslationTextComponent("mco.download.preparing"));
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

                WorldDownload worlddownload = realmsclient.func_224917_b(this.field_238111_c_, this.field_238112_d_);
                func_238125_a_(1);

                if (this.func_224988_a())
                {
                    return;
                }

                func_238127_a_(new RealmsDownloadLatestWorldScreen(this.field_238113_e_, worlddownload, this.field_238114_f_, (p_238115_0_) ->
                {
                }));
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

                field_238124_a_.error("Couldn't download world data");
                func_238127_a_(new RealmsGenericErrorScreen(realmsserviceexception, this.field_238113_e_));
                return;
            }
            catch (Exception exception)
            {
                if (this.func_224988_a())
                {
                    return;
                }

                field_238124_a_.error("Couldn't download world data", (Throwable)exception);
                this.func_237703_a_(exception.getLocalizedMessage());
                return;
            }
        }
    }
}
