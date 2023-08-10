package net.minecraft.realms.action;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ConnectingToRealmsAction extends LongRunningTask
{
    private final RealmsServer field_238116_c_;
    private final Screen field_238117_d_;
    private final RealmsMainScreen field_238118_e_;
    private final ReentrantLock field_238119_f_;

    public ConnectingToRealmsAction(RealmsMainScreen p_i232231_1_, Screen p_i232231_2_, RealmsServer p_i232231_3_, ReentrantLock p_i232231_4_)
    {
        this.field_238117_d_ = p_i232231_2_;
        this.field_238118_e_ = p_i232231_1_;
        this.field_238116_c_ = p_i232231_3_;
        this.field_238119_f_ = p_i232231_4_;
    }

    public void run()
    {
        this.func_224989_b(new TranslationTextComponent("mco.connect.connecting"));
        RealmsClient realmsclient = RealmsClient.func_224911_a();
        boolean flag = false;
        boolean flag1 = false;
        int i = 5;
        RealmsServerAddress realmsserveraddress = null;
        boolean flag2 = false;
        boolean flag3 = false;

        for (int j = 0; j < 40 && !this.func_224988_a(); ++j)
        {
            try
            {
                realmsserveraddress = realmsclient.func_224904_b(this.field_238116_c_.field_230582_a_);
                flag = true;
            }
            catch (RetryCallException retrycallexception)
            {
                i = retrycallexception.field_224985_e;
            }
            catch (RealmsServiceException realmsserviceexception)
            {
                if (realmsserviceexception.field_224983_c == 6002)
                {
                    flag2 = true;
                }
                else if (realmsserviceexception.field_224983_c == 6006)
                {
                    flag3 = true;
                }
                else
                {
                    flag1 = true;
                    this.func_237703_a_(realmsserviceexception.toString());
                    field_238124_a_.error("Couldn't connect to world", (Throwable)realmsserviceexception);
                }

                break;
            }
            catch (Exception exception)
            {
                flag1 = true;
                field_238124_a_.error("Couldn't connect to world", (Throwable)exception);
                this.func_237703_a_(exception.getLocalizedMessage());
                break;
            }

            if (flag)
            {
                break;
            }

            this.func_238123_b_(i);
        }

        if (flag2)
        {
            func_238127_a_(new RealmsTermsScreen(this.field_238117_d_, this.field_238118_e_, this.field_238116_c_));
        }
        else if (flag3)
        {
            if (this.field_238116_c_.field_230588_g_.equals(Minecraft.getInstance().getSession().getPlayerID()))
            {
                func_238127_a_(new RealmsBrokenWorldScreen(this.field_238117_d_, this.field_238118_e_, this.field_238116_c_.field_230582_a_, this.field_238116_c_.field_230594_m_ == RealmsServer.ServerType.MINIGAME));
            }
            else
            {
                func_238127_a_(new RealmsGenericErrorScreen(new TranslationTextComponent("mco.brokenworld.nonowner.title"), new TranslationTextComponent("mco.brokenworld.nonowner.error"), this.field_238117_d_));
            }
        }
        else if (!this.func_224988_a() && !flag1)
        {
            if (flag)
            {
                RealmsServerAddress realmsserveraddress1 = realmsserveraddress;

                if (realmsserveraddress1.field_230602_b_ != null && realmsserveraddress1.field_230603_c_ != null)
                {
                    ITextComponent itextcomponent = new TranslationTextComponent("mco.configure.world.resourcepack.question.line1");
                    ITextComponent itextcomponent1 = new TranslationTextComponent("mco.configure.world.resourcepack.question.line2");
                    func_238127_a_(new RealmsLongConfirmationScreen((p_238121_2_) ->
                    {
                        try {
                            if (p_238121_2_)
                            {
                                Function<Throwable, Void> function = (p_238122_1_) ->
                                {
                                    Minecraft.getInstance().getPackFinder().clearResourcePack();
                                    field_238124_a_.error(p_238122_1_);
                                    func_238127_a_(new RealmsGenericErrorScreen(new StringTextComponent("Failed to download resource pack!"), this.field_238117_d_));
                                    return null;
                                };

                                try
                                {
                                    Minecraft.getInstance().getPackFinder().downloadResourcePack(realmsserveraddress1.field_230602_b_, realmsserveraddress1.field_230603_c_).thenRun(() ->
                                    {
                                        this.func_224987_a(new RealmsLongRunningMcoTaskScreen(this.field_238117_d_, new ConnectedToRealmsAction(this.field_238117_d_, this.field_238116_c_, realmsserveraddress1)));
                                    }).exceptionally(function);
                                }
                                catch (Exception exception1)
                                {
                                    function.apply(exception1);
                                }
                            }
                            else {
                                func_238127_a_(this.field_238117_d_);
                            }
                        }
                        finally {
                            if (this.field_238119_f_ != null && this.field_238119_f_.isHeldByCurrentThread())
                            {
                                this.field_238119_f_.unlock();
                            }
                        }
                    }, RealmsLongConfirmationScreen.Type.Info, itextcomponent, itextcomponent1, true));
                }
                else
                {
                    this.func_224987_a(new RealmsLongRunningMcoTaskScreen(this.field_238117_d_, new ConnectedToRealmsAction(this.field_238117_d_, this.field_238116_c_, realmsserveraddress1)));
                }
            }
            else
            {
                this.func_230434_a_(new TranslationTextComponent("mco.errorMessage.connectionFailure"));
            }
        }
    }

    private void func_238123_b_(int p_238123_1_)
    {
        try
        {
            Thread.sleep((long)(p_238123_1_ * 1000));
        }
        catch (InterruptedException interruptedexception)
        {
            field_238124_a_.warn(interruptedexception.getLocalizedMessage());
        }
    }
}
