package net.minecraft.realms.action;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.LongRunningTask;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

public class CreateWorldRealmsAction extends LongRunningTask
{
    private final String field_238148_c_;
    private final String field_238149_d_;
    private final long field_238150_e_;
    private final Screen field_238151_f_;

    public CreateWorldRealmsAction(long p_i232237_1_, String p_i232237_3_, String p_i232237_4_, Screen p_i232237_5_)
    {
        this.field_238150_e_ = p_i232237_1_;
        this.field_238148_c_ = p_i232237_3_;
        this.field_238149_d_ = p_i232237_4_;
        this.field_238151_f_ = p_i232237_5_;
    }

    public void run()
    {
        this.func_224989_b(new TranslationTextComponent("mco.create.world.wait"));
        RealmsClient realmsclient = RealmsClient.func_224911_a();

        try
        {
            realmsclient.func_224900_a(this.field_238150_e_, this.field_238148_c_, this.field_238149_d_);
            func_238127_a_(this.field_238151_f_);
        }
        catch (RealmsServiceException realmsserviceexception)
        {
            field_238124_a_.error("Couldn't create world");
            this.func_237703_a_(realmsserviceexception.toString());
        }
        catch (Exception exception)
        {
            field_238124_a_.error("Could not create world");
            this.func_237703_a_(exception.getLocalizedMessage());
        }
    }
}
