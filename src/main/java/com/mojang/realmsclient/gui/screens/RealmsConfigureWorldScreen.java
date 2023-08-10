package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsServerSlotButton;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.action.CloseRealmsAction;
import net.minecraft.realms.action.OpeningWorldRealmsAction;
import net.minecraft.realms.action.StartMinigameRealmsAction;
import net.minecraft.realms.action.SwitchMinigameRealmsAction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsConfigureWorldScreen extends NotifableRealmsScreen
{
    private static final Logger field_224413_a = LogManager.getLogger();
    private static final ResourceLocation field_237787_b_ = new ResourceLocation("realms", "textures/gui/realms/on_icon.png");
    private static final ResourceLocation field_237788_c_ = new ResourceLocation("realms", "textures/gui/realms/off_icon.png");
    private static final ResourceLocation field_237789_p_ = new ResourceLocation("realms", "textures/gui/realms/expired_icon.png");
    private static final ResourceLocation field_237790_q_ = new ResourceLocation("realms", "textures/gui/realms/expires_soon_icon.png");
    private static final ITextComponent field_243108_r = new TranslationTextComponent("mco.configure.worlds.title");
    private static final ITextComponent field_243109_s = new TranslationTextComponent("mco.configure.world.title");
    private static final ITextComponent field_243110_t = (new TranslationTextComponent("mco.configure.current.minigame")).appendString(": ");
    private static final ITextComponent field_243111_u = new TranslationTextComponent("mco.selectServer.expired");
    private static final ITextComponent field_243112_v = new TranslationTextComponent("mco.selectServer.expires.soon");
    private static final ITextComponent field_243113_w = new TranslationTextComponent("mco.selectServer.expires.day");
    private static final ITextComponent field_243114_x = new TranslationTextComponent("mco.selectServer.open");
    private static final ITextComponent field_243115_y = new TranslationTextComponent("mco.selectServer.closed");
    @Nullable
    private ITextComponent field_224414_b;
    private final RealmsMainScreen field_224415_c;
    @Nullable
    private RealmsServer field_224416_d;
    private final long field_224417_e;
    private int field_224418_f;
    private int field_224419_g;
    private Button field_224422_j;
    private Button field_224423_k;
    private Button field_224424_l;
    private Button field_224425_m;
    private Button field_224426_n;
    private Button field_224427_o;
    private Button field_224428_p;
    private boolean field_224429_q;
    private int field_224430_r;
    private int field_224431_s;

    public RealmsConfigureWorldScreen(RealmsMainScreen p_i51774_1_, long p_i51774_2_)
    {
        this.field_224415_c = p_i51774_1_;
        this.field_224417_e = p_i51774_2_;
    }

    public void init()
    {
        if (this.field_224416_d == null)
        {
            this.func_224387_a(this.field_224417_e);
        }

        this.field_224418_f = this.width / 2 - 187;
        this.field_224419_g = this.width / 2 + 190;
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.field_224422_j = this.addButton(new Button(this.func_224374_a(0, 3), func_239562_k_(0), 100, 20, new TranslationTextComponent("mco.configure.world.buttons.players"), (p_237818_1_) ->
        {
            this.mc.displayGuiScreen(new RealmsPlayerScreen(this, this.field_224416_d));
        }));
        this.field_224423_k = this.addButton(new Button(this.func_224374_a(1, 3), func_239562_k_(0), 100, 20, new TranslationTextComponent("mco.configure.world.buttons.settings"), (p_237817_1_) ->
        {
            this.mc.displayGuiScreen(new RealmsSettingsScreen(this, this.field_224416_d.clone()));
        }));
        this.field_224424_l = this.addButton(new Button(this.func_224374_a(2, 3), func_239562_k_(0), 100, 20, new TranslationTextComponent("mco.configure.world.buttons.subscription"), (p_237816_1_) ->
        {
            this.mc.displayGuiScreen(new RealmsSubscriptionInfoScreen(this, this.field_224416_d.clone(), this.field_224415_c));
        }));

        for (int i = 1; i < 5; ++i)
        {
            this.func_224402_a(i);
        }

        this.field_224428_p = this.addButton(new Button(this.func_224411_b(0), func_239562_k_(13) - 5, 100, 20, new TranslationTextComponent("mco.configure.world.buttons.switchminigame"), (p_237815_1_) ->
        {
            RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.MINIGAME);
            realmsselectworldtemplatescreen.func_238001_a_(new TranslationTextComponent("mco.template.title.minigame"));
            this.mc.displayGuiScreen(realmsselectworldtemplatescreen);
        }));
        this.field_224425_m = this.addButton(new Button(this.func_224411_b(0), func_239562_k_(13) - 5, 90, 20, new TranslationTextComponent("mco.configure.world.buttons.options"), (p_237814_1_) ->
        {
            this.mc.displayGuiScreen(new RealmsSlotOptionsScreen(this, this.field_224416_d.field_230590_i_.get(this.field_224416_d.field_230595_n_).clone(), this.field_224416_d.field_230594_m_, this.field_224416_d.field_230595_n_));
        }));
        this.field_224426_n = this.addButton(new Button(this.func_224411_b(1), func_239562_k_(13) - 5, 90, 20, new TranslationTextComponent("mco.configure.world.backup"), (p_237812_1_) ->
        {
            this.mc.displayGuiScreen(new RealmsBackupScreen(this, this.field_224416_d.clone(), this.field_224416_d.field_230595_n_));
        }));
        this.field_224427_o = this.addButton(new Button(this.func_224411_b(2), func_239562_k_(13) - 5, 90, 20, new TranslationTextComponent("mco.configure.world.buttons.resetworld"), (p_237810_1_) ->
        {
            this.mc.displayGuiScreen(new RealmsResetWorldScreen(this, this.field_224416_d.clone(), () -> {
                this.mc.displayGuiScreen(this.func_224407_b());
            }, () -> {
                this.mc.displayGuiScreen(this.func_224407_b());
            }));
        }));
        this.addButton(new Button(this.field_224419_g - 80 + 8, func_239562_k_(13) - 5, 70, 20, DialogTexts.GUI_BACK, (p_237808_1_) ->
        {
            this.func_224390_d();
        }));
        this.field_224426_n.active = true;

        if (this.field_224416_d == null)
        {
            this.func_224412_j();
            this.func_224377_h();
            this.field_224422_j.active = false;
            this.field_224423_k.active = false;
            this.field_224424_l.active = false;
        }
        else
        {
            this.func_224400_e();

            if (this.func_224376_g())
            {
                this.func_224377_h();
            }
            else
            {
                this.func_224412_j();
            }
        }
    }

    private void func_224402_a(int p_224402_1_)
    {
        int i = this.func_224368_c(p_224402_1_);
        int j = func_239562_k_(5) + 5;
        RealmsServerSlotButton realmsserverslotbutton = new RealmsServerSlotButton(i, j, 80, 80, () ->
        {
            return this.field_224416_d;
        }, (p_237801_1_) ->
        {
            this.field_224414_b = p_237801_1_;
        }, p_224402_1_, (p_237795_2_) ->
        {
            RealmsServerSlotButton.ServerData realmsserverslotbutton$serverdata = ((RealmsServerSlotButton)p_237795_2_).func_237717_a_();

            if (realmsserverslotbutton$serverdata != null)
            {
                switch (realmsserverslotbutton$serverdata.field_225116_g)
                {
                    case NOTHING:
                        break;

                    case JOIN:
                        this.func_224385_a(this.field_224416_d);
                        break;

                    case SWITCH_SLOT:
                        if (realmsserverslotbutton$serverdata.field_225115_f)
                        {
                            this.func_224401_f();
                        }
                        else if (realmsserverslotbutton$serverdata.field_225114_e)
                        {
                            this.func_224388_b(p_224402_1_, this.field_224416_d);
                        }
                        else
                        {
                            this.func_224403_a(p_224402_1_, this.field_224416_d);
                        }

                        break;

                    default:
                        throw new IllegalStateException("Unknown action " + realmsserverslotbutton$serverdata.field_225116_g);
                }
            }
        });
        this.addButton(realmsserverslotbutton);
    }

    private int func_224411_b(int p_224411_1_)
    {
        return this.field_224418_f + p_224411_1_ * 95;
    }

    private int func_224374_a(int p_224374_1_, int p_224374_2_)
    {
        return this.width / 2 - (p_224374_2_ * 105 - 5) / 2 + p_224374_1_ * 105;
    }

    public void tick()
    {
        super.tick();
        ++this.field_224430_r;
        --this.field_224431_s;

        if (this.field_224431_s < 0)
        {
            this.field_224431_s = 0;
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.field_224414_b = null;
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, field_243108_r, this.width / 2, func_239562_k_(4), 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (this.field_224416_d == null)
        {
            drawCenteredString(matrixStack, this.font, field_243109_s, this.width / 2, 17, 16777215);
        }
        else
        {
            String s = this.field_224416_d.func_230775_b_();
            int i = this.font.getStringWidth(s);
            int j = this.field_224416_d.field_230586_e_ == RealmsServer.Status.CLOSED ? 10526880 : 8388479;
            int k = this.font.getStringPropertyWidth(field_243109_s);
            drawCenteredString(matrixStack, this.font, field_243109_s, this.width / 2, 12, 16777215);
            drawCenteredString(matrixStack, this.font, s, this.width / 2, 24, j);
            int l = Math.min(this.func_224374_a(2, 3) + 80 - 11, this.width / 2 + i / 2 + k / 2 + 10);
            this.func_237807_c_(matrixStack, l, 7, mouseX, mouseY);

            if (this.func_224376_g())
            {
                this.font.func_243248_b(matrixStack, field_243110_t.deepCopy().appendString(this.field_224416_d.func_230778_c_()), (float)(this.field_224418_f + 80 + 20 + 10), (float)func_239562_k_(13), 16777215);
            }

            if (this.field_224414_b != null)
            {
                this.func_237796_a_(matrixStack, this.field_224414_b, mouseX, mouseY);
            }
        }
    }

    private int func_224368_c(int p_224368_1_)
    {
        return this.field_224418_f + (p_224368_1_ - 1) * 98;
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.func_224390_d();
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private void func_224390_d()
    {
        if (this.field_224429_q)
        {
            this.field_224415_c.func_223978_e();
        }

        this.mc.displayGuiScreen(this.field_224415_c);
    }

    private void func_224387_a(long p_224387_1_)
    {
        (new Thread(() ->
        {
            RealmsClient realmsclient = RealmsClient.func_224911_a();

            try {
                this.field_224416_d = realmsclient.func_224935_a(p_224387_1_);
                this.func_224400_e();

                if (this.func_224376_g())
                {
                    this.func_237806_b_(this.field_224428_p);
                }
                else {
                    this.func_237806_b_(this.field_224425_m);
                    this.func_237806_b_(this.field_224426_n);
                    this.func_237806_b_(this.field_224427_o);
                }
            }
            catch (RealmsServiceException realmsserviceexception)
            {
                field_224413_a.error("Couldn't get own world");
                this.mc.execute(() ->
                {
                    this.mc.displayGuiScreen(new RealmsGenericErrorScreen(ITextComponent.getTextComponentOrEmpty(realmsserviceexception.getMessage()), this.field_224415_c));
                });
            }
        })).start();
    }

    private void func_224400_e()
    {
        this.field_224422_j.active = !this.field_224416_d.field_230591_j_;
        this.field_224423_k.active = !this.field_224416_d.field_230591_j_;
        this.field_224424_l.active = true;
        this.field_224428_p.active = !this.field_224416_d.field_230591_j_;
        this.field_224425_m.active = !this.field_224416_d.field_230591_j_;
        this.field_224427_o.active = !this.field_224416_d.field_230591_j_;
    }

    private void func_224385_a(RealmsServer p_224385_1_)
    {
        if (this.field_224416_d.field_230586_e_ == RealmsServer.Status.OPEN)
        {
            this.field_224415_c.func_223911_a(p_224385_1_, new RealmsConfigureWorldScreen(this.field_224415_c.func_223942_f(), this.field_224417_e));
        }
        else
        {
            this.func_237802_a_(true, new RealmsConfigureWorldScreen(this.field_224415_c.func_223942_f(), this.field_224417_e));
        }
    }

    private void func_224401_f()
    {
        RealmsSelectWorldTemplateScreen realmsselectworldtemplatescreen = new RealmsSelectWorldTemplateScreen(this, RealmsServer.ServerType.MINIGAME);
        realmsselectworldtemplatescreen.func_238001_a_(new TranslationTextComponent("mco.template.title.minigame"));
        realmsselectworldtemplatescreen.func_238002_a_(new TranslationTextComponent("mco.minigame.world.info.line1"), new TranslationTextComponent("mco.minigame.world.info.line2"));
        this.mc.displayGuiScreen(realmsselectworldtemplatescreen);
    }

    private void func_224403_a(int p_224403_1_, RealmsServer p_224403_2_)
    {
        ITextComponent itextcomponent = new TranslationTextComponent("mco.configure.world.slot.switch.question.line1");
        ITextComponent itextcomponent1 = new TranslationTextComponent("mco.configure.world.slot.switch.question.line2");
        this.mc.displayGuiScreen(new RealmsLongConfirmationScreen((p_237805_3_) ->
        {
            if (p_237805_3_)
            {
                this.mc.displayGuiScreen(new RealmsLongRunningMcoTaskScreen(this.field_224415_c, new SwitchMinigameRealmsAction(p_224403_2_.field_230582_a_, p_224403_1_, () ->
                {
                    this.mc.displayGuiScreen(this.func_224407_b());
                })));
            }
            else {
                this.mc.displayGuiScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, itextcomponent, itextcomponent1, true));
    }

    private void func_224388_b(int p_224388_1_, RealmsServer p_224388_2_)
    {
        ITextComponent itextcomponent = new TranslationTextComponent("mco.configure.world.slot.switch.question.line1");
        ITextComponent itextcomponent1 = new TranslationTextComponent("mco.configure.world.slot.switch.question.line2");
        this.mc.displayGuiScreen(new RealmsLongConfirmationScreen((p_237797_3_) ->
        {
            if (p_237797_3_)
            {
                RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this, p_224388_2_, new TranslationTextComponent("mco.configure.world.switch.slot"), new TranslationTextComponent("mco.configure.world.switch.slot.subtitle"), 10526880, DialogTexts.GUI_CANCEL, () ->
                {
                    this.mc.displayGuiScreen(this.func_224407_b());
                }, () ->
                {
                    this.mc.displayGuiScreen(this.func_224407_b());
                });
                realmsresetworldscreen.func_224445_b(p_224388_1_);
                realmsresetworldscreen.func_224432_a(new TranslationTextComponent("mco.create.world.reset.title"));
                this.mc.displayGuiScreen(realmsresetworldscreen);
            }
            else {
                this.mc.displayGuiScreen(this);
            }
        }, RealmsLongConfirmationScreen.Type.Info, itextcomponent, itextcomponent1, true));
    }

    protected void func_237796_a_(MatrixStack p_237796_1_, @Nullable ITextComponent p_237796_2_, int p_237796_3_, int p_237796_4_)
    {
        int i = p_237796_3_ + 12;
        int j = p_237796_4_ - 12;
        int k = this.font.getStringPropertyWidth(p_237796_2_);

        if (i + k + 3 > this.field_224419_g)
        {
            i = i - k - 20;
        }

        this.fillGradient(p_237796_1_, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
        this.font.func_243246_a(p_237796_1_, p_237796_2_, (float)i, (float)j, 16777215);
    }

    private void func_237807_c_(MatrixStack p_237807_1_, int p_237807_2_, int p_237807_3_, int p_237807_4_, int p_237807_5_)
    {
        if (this.field_224416_d.field_230591_j_)
        {
            this.func_237809_d_(p_237807_1_, p_237807_2_, p_237807_3_, p_237807_4_, p_237807_5_);
        }
        else if (this.field_224416_d.field_230586_e_ == RealmsServer.Status.CLOSED)
        {
            this.func_237813_f_(p_237807_1_, p_237807_2_, p_237807_3_, p_237807_4_, p_237807_5_);
        }
        else if (this.field_224416_d.field_230586_e_ == RealmsServer.Status.OPEN)
        {
            if (this.field_224416_d.field_230593_l_ < 7)
            {
                this.func_237804_b_(p_237807_1_, p_237807_2_, p_237807_3_, p_237807_4_, p_237807_5_, this.field_224416_d.field_230593_l_);
            }
            else
            {
                this.func_237811_e_(p_237807_1_, p_237807_2_, p_237807_3_, p_237807_4_, p_237807_5_);
            }
        }
    }

    private void func_237809_d_(MatrixStack p_237809_1_, int p_237809_2_, int p_237809_3_, int p_237809_4_, int p_237809_5_)
    {
        this.mc.getTextureManager().bindTexture(field_237789_p_);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        AbstractGui.blit(p_237809_1_, p_237809_2_, p_237809_3_, 0.0F, 0.0F, 10, 28, 10, 28);

        if (p_237809_4_ >= p_237809_2_ && p_237809_4_ <= p_237809_2_ + 9 && p_237809_5_ >= p_237809_3_ && p_237809_5_ <= p_237809_3_ + 27)
        {
            this.field_224414_b = field_243111_u;
        }
    }

    private void func_237804_b_(MatrixStack p_237804_1_, int p_237804_2_, int p_237804_3_, int p_237804_4_, int p_237804_5_, int p_237804_6_)
    {
        this.mc.getTextureManager().bindTexture(field_237790_q_);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.field_224430_r % 20 < 10)
        {
            AbstractGui.blit(p_237804_1_, p_237804_2_, p_237804_3_, 0.0F, 0.0F, 10, 28, 20, 28);
        }
        else
        {
            AbstractGui.blit(p_237804_1_, p_237804_2_, p_237804_3_, 10.0F, 0.0F, 10, 28, 20, 28);
        }

        if (p_237804_4_ >= p_237804_2_ && p_237804_4_ <= p_237804_2_ + 9 && p_237804_5_ >= p_237804_3_ && p_237804_5_ <= p_237804_3_ + 27)
        {
            if (p_237804_6_ <= 0)
            {
                this.field_224414_b = field_243112_v;
            }
            else if (p_237804_6_ == 1)
            {
                this.field_224414_b = field_243113_w;
            }
            else
            {
                this.field_224414_b = new TranslationTextComponent("mco.selectServer.expires.days", p_237804_6_);
            }
        }
    }

    private void func_237811_e_(MatrixStack p_237811_1_, int p_237811_2_, int p_237811_3_, int p_237811_4_, int p_237811_5_)
    {
        this.mc.getTextureManager().bindTexture(field_237787_b_);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        AbstractGui.blit(p_237811_1_, p_237811_2_, p_237811_3_, 0.0F, 0.0F, 10, 28, 10, 28);

        if (p_237811_4_ >= p_237811_2_ && p_237811_4_ <= p_237811_2_ + 9 && p_237811_5_ >= p_237811_3_ && p_237811_5_ <= p_237811_3_ + 27)
        {
            this.field_224414_b = field_243114_x;
        }
    }

    private void func_237813_f_(MatrixStack p_237813_1_, int p_237813_2_, int p_237813_3_, int p_237813_4_, int p_237813_5_)
    {
        this.mc.getTextureManager().bindTexture(field_237788_c_);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        AbstractGui.blit(p_237813_1_, p_237813_2_, p_237813_3_, 0.0F, 0.0F, 10, 28, 10, 28);

        if (p_237813_4_ >= p_237813_2_ && p_237813_4_ <= p_237813_2_ + 9 && p_237813_5_ >= p_237813_3_ && p_237813_5_ <= p_237813_3_ + 27)
        {
            this.field_224414_b = field_243115_y;
        }
    }

    private boolean func_224376_g()
    {
        return this.field_224416_d != null && this.field_224416_d.field_230594_m_ == RealmsServer.ServerType.MINIGAME;
    }

    private void func_224377_h()
    {
        this.func_237799_a_(this.field_224425_m);
        this.func_237799_a_(this.field_224426_n);
        this.func_237799_a_(this.field_224427_o);
    }

    private void func_237799_a_(Button p_237799_1_)
    {
        p_237799_1_.visible = false;
        this.children.remove(p_237799_1_);
        this.buttons.remove(p_237799_1_);
    }

    private void func_237806_b_(Button p_237806_1_)
    {
        p_237806_1_.visible = true;
        this.addButton(p_237806_1_);
    }

    private void func_224412_j()
    {
        this.func_237799_a_(this.field_224428_p);
    }

    public void func_224386_a(RealmsWorldOptions p_224386_1_)
    {
        RealmsWorldOptions realmsworldoptions = this.field_224416_d.field_230590_i_.get(this.field_224416_d.field_230595_n_);
        p_224386_1_.field_230624_k_ = realmsworldoptions.field_230624_k_;
        p_224386_1_.field_230625_l_ = realmsworldoptions.field_230625_l_;
        RealmsClient realmsclient = RealmsClient.func_224911_a();

        try
        {
            realmsclient.func_224925_a(this.field_224416_d.field_230582_a_, this.field_224416_d.field_230595_n_, p_224386_1_);
            this.field_224416_d.field_230590_i_.put(this.field_224416_d.field_230595_n_, p_224386_1_);
        }
        catch (RealmsServiceException realmsserviceexception)
        {
            field_224413_a.error("Couldn't save slot settings");
            this.mc.displayGuiScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
            return;
        }

        this.mc.displayGuiScreen(this);
    }

    public void func_224410_a(String p_224410_1_, String p_224410_2_)
    {
        String s = p_224410_2_.trim().isEmpty() ? null : p_224410_2_;
        RealmsClient realmsclient = RealmsClient.func_224911_a();

        try
        {
            realmsclient.func_224922_b(this.field_224416_d.field_230582_a_, p_224410_1_, s);
            this.field_224416_d.func_230773_a_(p_224410_1_);
            this.field_224416_d.func_230777_b_(s);
        }
        catch (RealmsServiceException realmsserviceexception)
        {
            field_224413_a.error("Couldn't save settings");
            this.mc.displayGuiScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
            return;
        }

        this.mc.displayGuiScreen(this);
    }

    public void func_237802_a_(boolean p_237802_1_, Screen p_237802_2_)
    {
        this.mc.displayGuiScreen(new RealmsLongRunningMcoTaskScreen(p_237802_2_, new OpeningWorldRealmsAction(this.field_224416_d, this, this.field_224415_c, p_237802_1_)));
    }

    public void func_237800_a_(Screen p_237800_1_)
    {
        this.mc.displayGuiScreen(new RealmsLongRunningMcoTaskScreen(p_237800_1_, new CloseRealmsAction(this.field_224416_d, this)));
    }

    public void func_224398_a()
    {
        this.field_224429_q = true;
    }

    protected void func_223627_a_(@Nullable WorldTemplate p_223627_1_)
    {
        if (p_223627_1_ != null)
        {
            if (WorldTemplate.Type.MINIGAME == p_223627_1_.field_230655_i_)
            {
                this.mc.displayGuiScreen(new RealmsLongRunningMcoTaskScreen(this.field_224415_c, new StartMinigameRealmsAction(this.field_224416_d.field_230582_a_, p_223627_1_, this.func_224407_b())));
            }
        }
    }

    public RealmsConfigureWorldScreen func_224407_b()
    {
        return new RealmsConfigureWorldScreen(this.field_224415_c, this.field_224417_e);
    }
}
