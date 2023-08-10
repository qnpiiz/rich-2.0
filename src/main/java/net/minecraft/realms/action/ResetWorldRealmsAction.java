package net.minecraft.realms.action;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ResetWorldRealmsAction extends LongRunningTask
{
    private final String field_238132_c_;
    private final WorldTemplate field_238133_d_;
    private final int field_238134_e_;
    private final boolean field_238135_f_;
    private final long field_238136_g_;
    private ITextComponent field_238137_h_ = new TranslationTextComponent("mco.reset.world.resetting.screen.title");
    private final Runnable field_238138_i_;

    public ResetWorldRealmsAction(@Nullable String p_i242048_1_, @Nullable WorldTemplate p_i242048_2_, int p_i242048_3_, boolean p_i242048_4_, long p_i242048_5_, @Nullable ITextComponent p_i242048_7_, Runnable p_i242048_8_)
    {
        this.field_238132_c_ = p_i242048_1_;
        this.field_238133_d_ = p_i242048_2_;
        this.field_238134_e_ = p_i242048_3_;
        this.field_238135_f_ = p_i242048_4_;
        this.field_238136_g_ = p_i242048_5_;

        if (p_i242048_7_ != null)
        {
            this.field_238137_h_ = p_i242048_7_;
        }

        this.field_238138_i_ = p_i242048_8_;
    }

    public void run()
    {
        RealmsClient realmsclient = RealmsClient.func_224911_a();
        this.func_224989_b(this.field_238137_h_);
        int i = 0;

        while (i < 25)
        {
            try
            {
                if (this.func_224988_a())
                {
                    return;
                }

                if (this.field_238133_d_ != null)
                {
                    realmsclient.func_224924_g(this.field_238136_g_, this.field_238133_d_.field_230647_a_);
                }
                else
                {
                    realmsclient.func_224943_a(this.field_238136_g_, this.field_238132_c_, this.field_238134_e_, this.field_238135_f_);
                }

                if (this.func_224988_a())
                {
                    return;
                }

                this.field_238138_i_.run();
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
            catch (Exception exception)
            {
                if (this.func_224988_a())
                {
                    return;
                }

                field_238124_a_.error("Couldn't reset world");
                this.func_237703_a_(exception.toString());
                return;
            }
        }
    }
}
