package net.minecraft.realms.action;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import net.minecraft.util.text.TranslationTextComponent;

public class SwitchMinigameRealmsAction extends LongRunningTask
{
    private final long field_238145_c_;
    private final int field_238146_d_;
    private final Runnable field_238147_e_;

    public SwitchMinigameRealmsAction(long p_i232236_1_, int p_i232236_3_, Runnable p_i232236_4_)
    {
        this.field_238145_c_ = p_i232236_1_;
        this.field_238146_d_ = p_i232236_3_;
        this.field_238147_e_ = p_i232236_4_;
    }

    public void run()
    {
        RealmsClient realmsclient = RealmsClient.func_224911_a();
        this.func_224989_b(new TranslationTextComponent("mco.minigame.world.slot.screen.title"));

        for (int i = 0; i < 25; ++i)
        {
            try
            {
                if (this.func_224988_a())
                {
                    return;
                }

                if (realmsclient.func_224927_a(this.field_238145_c_, this.field_238146_d_))
                {
                    this.field_238147_e_.run();
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

                field_238124_a_.error("Couldn't switch world!");
                this.func_237703_a_(exception.toString());
            }
        }
    }
}
