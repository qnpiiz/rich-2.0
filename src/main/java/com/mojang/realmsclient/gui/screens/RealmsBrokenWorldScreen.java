package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsServerSlotButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.action.OpeningWorldRealmsAction;
import net.minecraft.realms.action.SwitchMinigameRealmsAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBrokenWorldScreen extends RealmsScreen
{
    private static final Logger field_224071_a = LogManager.getLogger();
    private final Screen field_224072_b;
    private final RealmsMainScreen field_224073_c;
    private RealmsServer field_224074_d;
    private final long field_224075_e;
    private final ITextComponent field_237769_r_;
    private final ITextComponent[] field_224077_g = new ITextComponent[] {new TranslationTextComponent("mco.brokenworld.message.line1"), new TranslationTextComponent("mco.brokenworld.message.line2")};
    private int field_224078_h;
    private int field_224079_i;
    private final List<Integer> field_224086_p = Lists.newArrayList();
    private int field_224087_q;

    public RealmsBrokenWorldScreen(Screen p_i232200_1_, RealmsMainScreen p_i232200_2_, long p_i232200_3_, boolean p_i232200_5_)
    {
        this.field_224072_b = p_i232200_1_;
        this.field_224073_c = p_i232200_2_;
        this.field_224075_e = p_i232200_3_;
        this.field_237769_r_ = p_i232200_5_ ? new TranslationTextComponent("mco.brokenworld.minigame.title") : new TranslationTextComponent("mco.brokenworld.title");
    }

    public void init()
    {
        this.field_224078_h = this.width / 2 - 150;
        this.field_224079_i = this.width / 2 + 190;
        this.addButton(new Button(this.field_224079_i - 80 + 8, func_239562_k_(13) - 5, 70, 20, DialogTexts.GUI_BACK, (p_237776_1_) ->
        {
            this.func_224060_e();
        }));

        if (this.field_224074_d == null)
        {
            this.func_224068_a(this.field_224075_e);
        }
        else
        {
            this.func_224058_a();
        }

        this.mc.keyboardListener.enableRepeatEvents(true);
        RealmsNarratorHelper.func_239550_a_(Stream.concat(Stream.of(this.field_237769_r_), Stream.of(this.field_224077_g)).map(ITextComponent::getString).collect(Collectors.joining(" ")));
    }

    private void func_224058_a()
    {
        for (Entry<Integer, RealmsWorldOptions> entry : this.field_224074_d.field_230590_i_.entrySet())
        {
            int i = entry.getKey();
            boolean flag = i != this.field_224074_d.field_230595_n_ || this.field_224074_d.field_230594_m_ == RealmsServer.ServerType.MINIGAME;
            Button button;

            if (flag)
            {
                button = new Button(this.func_224065_a(i), func_239562_k_(8), 80, 20, new TranslationTextComponent("mco.brokenworld.play"), (p_237780_2_) ->
                {
                    if ((this.field_224074_d.field_230590_i_.get(i)).field_230627_n_)
                    {
                        RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this, this.field_224074_d, new TranslationTextComponent("mco.configure.world.switch.slot"), new TranslationTextComponent("mco.configure.world.switch.slot.subtitle"), 10526880, DialogTexts.GUI_CANCEL, this::func_237772_a_, () ->
                        {
                            this.mc.displayGuiScreen(this);
                            this.func_237772_a_();
                        });
                        realmsresetworldscreen.func_224445_b(i);
                        realmsresetworldscreen.func_224432_a(new TranslationTextComponent("mco.create.world.reset.title"));
                        this.mc.displayGuiScreen(realmsresetworldscreen);
                    }
                    else {
                        this.mc.displayGuiScreen(new RealmsLongRunningMcoTaskScreen(this.field_224072_b, new SwitchMinigameRealmsAction(this.field_224074_d.field_230582_a_, i, this::func_237772_a_)));
                    }
                });
            }
            else
            {
                button = new Button(this.func_224065_a(i), func_239562_k_(8), 80, 20, new TranslationTextComponent("mco.brokenworld.download"), (p_237777_2_) ->
                {
                    ITextComponent itextcomponent = new TranslationTextComponent("mco.configure.world.restore.download.question.line1");
                    ITextComponent itextcomponent1 = new TranslationTextComponent("mco.configure.world.restore.download.question.line2");
                    this.mc.displayGuiScreen(new RealmsLongConfirmationScreen((p_237778_2_) -> {
                        if (p_237778_2_)
                        {
                            this.func_224066_b(i);
                        }
                        else {
                            this.mc.displayGuiScreen(this);
                        }
                    }, RealmsLongConfirmationScreen.Type.Info, itextcomponent, itextcomponent1, true));
                });
            }

            if (this.field_224086_p.contains(i))
            {
                button.active = false;
                button.setMessage(new TranslationTextComponent("mco.brokenworld.downloaded"));
            }

            this.addButton(button);
            this.addButton(new Button(this.func_224065_a(i), func_239562_k_(10), 80, 20, new TranslationTextComponent("mco.brokenworld.reset"), (p_237773_2_) ->
            {
                RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this, this.field_224074_d, this::func_237772_a_, () -> {
                    this.mc.displayGuiScreen(this);
                    this.func_237772_a_();
                });

                if (i != this.field_224074_d.field_230595_n_ || this.field_224074_d.field_230594_m_ == RealmsServer.ServerType.MINIGAME)
                {
                    realmsresetworldscreen.func_224445_b(i);
                }

                this.mc.displayGuiScreen(realmsresetworldscreen);
            }));
        }
    }

    public void tick()
    {
        ++this.field_224087_q;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, this.field_237769_r_, this.width / 2, 17, 16777215);

        for (int i = 0; i < this.field_224077_g.length; ++i)
        {
            drawCenteredString(matrixStack, this.font, this.field_224077_g[i], this.width / 2, func_239562_k_(-1) + 3 + i * 12, 10526880);
        }

        if (this.field_224074_d != null)
        {
            for (Entry<Integer, RealmsWorldOptions> entry : this.field_224074_d.field_230590_i_.entrySet())
            {
                if ((entry.getValue()).field_230625_l_ != null && (entry.getValue()).field_230624_k_ != -1L)
                {
                    this.func_237775_a_(matrixStack, this.func_224065_a(entry.getKey()), func_239562_k_(1) + 5, mouseX, mouseY, this.field_224074_d.field_230595_n_ == entry.getKey() && !this.func_224069_f(), entry.getValue().func_230787_a_(entry.getKey()), entry.getKey(), (entry.getValue()).field_230624_k_, (entry.getValue()).field_230625_l_, (entry.getValue()).field_230627_n_);
                }
                else
                {
                    this.func_237775_a_(matrixStack, this.func_224065_a(entry.getKey()), func_239562_k_(1) + 5, mouseX, mouseY, this.field_224074_d.field_230595_n_ == entry.getKey() && !this.func_224069_f(), entry.getValue().func_230787_a_(entry.getKey()), entry.getKey(), -1L, (String)null, (entry.getValue()).field_230627_n_);
                }
            }
        }
    }

    private int func_224065_a(int p_224065_1_)
    {
        return this.field_224078_h + (p_224065_1_ - 1) * 110;
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.func_224060_e();
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private void func_224060_e()
    {
        this.mc.displayGuiScreen(this.field_224072_b);
    }

    private void func_224068_a(long p_224068_1_)
    {
        (new Thread(() ->
        {
            RealmsClient realmsclient = RealmsClient.func_224911_a();

            try {
                this.field_224074_d = realmsclient.func_224935_a(p_224068_1_);
                this.func_224058_a();
            }
            catch (RealmsServiceException realmsserviceexception)
            {
                field_224071_a.error("Couldn't get own world");
                this.mc.displayGuiScreen(new RealmsGenericErrorScreen(ITextComponent.getTextComponentOrEmpty(realmsserviceexception.getMessage()), this.field_224072_b));
            }
        })).start();
    }

    public void func_237772_a_()
    {
        (new Thread(() ->
        {
            RealmsClient realmsclient = RealmsClient.func_224911_a();

            if (this.field_224074_d.field_230586_e_ == RealmsServer.Status.CLOSED)
            {
                this.mc.execute(() ->
                {
                    this.mc.displayGuiScreen(new RealmsLongRunningMcoTaskScreen(this, new OpeningWorldRealmsAction(this.field_224074_d, this, this.field_224073_c, true)));
                });
            }
            else {
                try {
                    this.field_224073_c.func_223942_f().func_223911_a(realmsclient.func_224935_a(this.field_224075_e), this);
                }
                catch (RealmsServiceException realmsserviceexception)
                {
                    field_224071_a.error("Couldn't get own world");
                    this.mc.execute(() ->
                    {
                        this.mc.displayGuiScreen(this.field_224072_b);
                    });
                }
            }
        })).start();
    }

    private void func_224066_b(int p_224066_1_)
    {
        RealmsClient realmsclient = RealmsClient.func_224911_a();

        try
        {
            WorldDownload worlddownload = realmsclient.func_224917_b(this.field_224074_d.field_230582_a_, p_224066_1_);
            RealmsDownloadLatestWorldScreen realmsdownloadlatestworldscreen = new RealmsDownloadLatestWorldScreen(this, worlddownload, this.field_224074_d.func_237696_a_(p_224066_1_), (p_237774_2_) ->
            {
                if (p_237774_2_)
                {
                    this.field_224086_p.add(p_224066_1_);
                    this.children.clear();
                    this.func_224058_a();
                }
                else {
                    this.mc.displayGuiScreen(this);
                }
            });
            this.mc.displayGuiScreen(realmsdownloadlatestworldscreen);
        }
        catch (RealmsServiceException realmsserviceexception)
        {
            field_224071_a.error("Couldn't download world data");
            this.mc.displayGuiScreen(new RealmsGenericErrorScreen(realmsserviceexception, this));
        }
    }

    private boolean func_224069_f()
    {
        return this.field_224074_d != null && this.field_224074_d.field_230594_m_ == RealmsServer.ServerType.MINIGAME;
    }

    private void func_237775_a_(MatrixStack p_237775_1_, int p_237775_2_, int p_237775_3_, int p_237775_4_, int p_237775_5_, boolean p_237775_6_, String p_237775_7_, int p_237775_8_, long p_237775_9_, String p_237775_11_, boolean p_237775_12_)
    {
        if (p_237775_12_)
        {
            this.mc.getTextureManager().bindTexture(RealmsServerSlotButton.field_237713_b_);
        }
        else if (p_237775_11_ != null && p_237775_9_ != -1L)
        {
            RealmsTextureManager.func_225202_a(String.valueOf(p_237775_9_), p_237775_11_);
        }
        else if (p_237775_8_ == 1)
        {
            this.mc.getTextureManager().bindTexture(RealmsServerSlotButton.field_237714_c_);
        }
        else if (p_237775_8_ == 2)
        {
            this.mc.getTextureManager().bindTexture(RealmsServerSlotButton.field_237715_d_);
        }
        else if (p_237775_8_ == 3)
        {
            this.mc.getTextureManager().bindTexture(RealmsServerSlotButton.field_237716_e_);
        }
        else
        {
            RealmsTextureManager.func_225202_a(String.valueOf(this.field_224074_d.field_230597_p_), this.field_224074_d.field_230598_q_);
        }

        if (!p_237775_6_)
        {
            RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
        }
        else if (p_237775_6_)
        {
            float f = 0.9F + 0.1F * MathHelper.cos((float)this.field_224087_q * 0.2F);
            RenderSystem.color4f(f, f, f, 1.0F);
        }

        AbstractGui.blit(p_237775_1_, p_237775_2_ + 3, p_237775_3_ + 3, 0.0F, 0.0F, 74, 74, 74, 74);
        this.mc.getTextureManager().bindTexture(RealmsServerSlotButton.field_237712_a_);

        if (p_237775_6_)
        {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
        }

        AbstractGui.blit(p_237775_1_, p_237775_2_, p_237775_3_, 0.0F, 0.0F, 80, 80, 80, 80);
        drawCenteredString(p_237775_1_, this.font, p_237775_7_, p_237775_2_ + 40, p_237775_3_ + 66, 16777215);
    }
}
