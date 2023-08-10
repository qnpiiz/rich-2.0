package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsInviteScreen extends RealmsScreen
{
    private static final Logger field_224213_a = LogManager.getLogger();
    private static final ITextComponent field_243118_b = new TranslationTextComponent("mco.configure.world.invite.profile.name");
    private static final ITextComponent field_243119_c = new TranslationTextComponent("mco.configure.world.players.error");
    private TextFieldWidget field_224214_b;
    private final RealmsServer field_224215_c;
    private final RealmsConfigureWorldScreen field_224216_d;
    private final Screen field_224217_e;
    @Nullable
    private ITextComponent field_224222_j;

    public RealmsInviteScreen(RealmsConfigureWorldScreen p_i232207_1_, Screen p_i232207_2_, RealmsServer p_i232207_3_)
    {
        this.field_224216_d = p_i232207_1_;
        this.field_224217_e = p_i232207_2_;
        this.field_224215_c = p_i232207_3_;
    }

    public void tick()
    {
        this.field_224214_b.tick();
    }

    public void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.field_224214_b = new TextFieldWidget(this.mc.fontRenderer, this.width / 2 - 100, func_239562_k_(2), 200, 20, (TextFieldWidget)null, new TranslationTextComponent("mco.configure.world.invite.profile.name"));
        this.addListener(this.field_224214_b);
        this.setFocusedDefault(this.field_224214_b);
        this.addButton(new Button(this.width / 2 - 100, func_239562_k_(10), 200, 20, new TranslationTextComponent("mco.configure.world.buttons.invite"), (p_237844_1_) ->
        {
            this.func_224211_a();
        }));
        this.addButton(new Button(this.width / 2 - 100, func_239562_k_(12), 200, 20, DialogTexts.GUI_CANCEL, (p_237843_1_) ->
        {
            this.mc.displayGuiScreen(this.field_224217_e);
        }));
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    private void func_224211_a()
    {
        RealmsClient realmsclient = RealmsClient.func_224911_a();

        if (this.field_224214_b.getText() != null && !this.field_224214_b.getText().isEmpty())
        {
            try
            {
                RealmsServer realmsserver = realmsclient.func_224910_b(this.field_224215_c.field_230582_a_, this.field_224214_b.getText().trim());

                if (realmsserver != null)
                {
                    this.field_224215_c.field_230589_h_ = realmsserver.field_230589_h_;
                    this.mc.displayGuiScreen(new RealmsPlayerScreen(this.field_224216_d, this.field_224215_c));
                }
                else
                {
                    this.func_224209_a(field_243119_c);
                }
            }
            catch (Exception exception)
            {
                field_224213_a.error("Couldn't invite user");
                this.func_224209_a(field_243119_c);
            }
        }
        else
        {
            this.func_224209_a(field_243119_c);
        }
    }

    private void func_224209_a(ITextComponent p_224209_1_)
    {
        this.field_224222_j = p_224209_1_;
        RealmsNarratorHelper.func_239550_a_(p_224209_1_.getString());
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.mc.displayGuiScreen(this.field_224217_e);
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.font.func_243248_b(matrixStack, field_243118_b, (float)(this.width / 2 - 100), (float)func_239562_k_(1), 10526880);

        if (this.field_224222_j != null)
        {
            drawCenteredString(matrixStack, this.font, this.field_224222_j, this.width / 2, func_239562_k_(5), 16711680);
        }

        this.field_224214_b.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
