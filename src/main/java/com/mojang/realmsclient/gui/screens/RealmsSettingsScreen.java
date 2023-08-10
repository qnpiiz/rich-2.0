package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RealmsSettingsScreen extends RealmsScreen
{
    private static final ITextComponent field_243169_a = new TranslationTextComponent("mco.configure.world.name");
    private static final ITextComponent field_243170_b = new TranslationTextComponent("mco.configure.world.description");
    private final RealmsConfigureWorldScreen field_224565_a;
    private final RealmsServer field_224566_b;
    private Button field_224568_d;
    private TextFieldWidget field_224569_e;
    private TextFieldWidget field_224570_f;
    private RealmsLabel field_224571_g;

    public RealmsSettingsScreen(RealmsConfigureWorldScreen p_i51751_1_, RealmsServer p_i51751_2_)
    {
        this.field_224565_a = p_i51751_1_;
        this.field_224566_b = p_i51751_2_;
    }

    public void tick()
    {
        this.field_224570_f.tick();
        this.field_224569_e.tick();
        this.field_224568_d.active = !this.field_224570_f.getText().trim().isEmpty();
    }

    public void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        int i = this.width / 2 - 106;
        this.field_224568_d = this.addButton(new Button(i - 2, func_239562_k_(12), 106, 20, new TranslationTextComponent("mco.configure.world.buttons.done"), (p_238033_1_) ->
        {
            this.func_224563_a();
        }));
        this.addButton(new Button(this.width / 2 + 2, func_239562_k_(12), 106, 20, DialogTexts.GUI_CANCEL, (p_238032_1_) ->
        {
            this.mc.displayGuiScreen(this.field_224565_a);
        }));
        String s = this.field_224566_b.field_230586_e_ == RealmsServer.Status.OPEN ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open";
        Button button = new Button(this.width / 2 - 53, func_239562_k_(0), 106, 20, new TranslationTextComponent(s), (p_238031_1_) ->
        {
            if (this.field_224566_b.field_230586_e_ == RealmsServer.Status.OPEN)
            {
                ITextComponent itextcomponent = new TranslationTextComponent("mco.configure.world.close.question.line1");
                ITextComponent itextcomponent1 = new TranslationTextComponent("mco.configure.world.close.question.line2");
                this.mc.displayGuiScreen(new RealmsLongConfirmationScreen((p_238034_1_) ->
                {
                    if (p_238034_1_)
                    {
                        this.field_224565_a.func_237800_a_(this);
                    }
                    else {
                        this.mc.displayGuiScreen(this);
                    }
                }, RealmsLongConfirmationScreen.Type.Info, itextcomponent, itextcomponent1, true));
            }
            else {
                this.field_224565_a.func_237802_a_(false, this);
            }
        });
        this.addButton(button);
        this.field_224570_f = new TextFieldWidget(this.mc.fontRenderer, i, func_239562_k_(4), 212, 20, (TextFieldWidget)null, new TranslationTextComponent("mco.configure.world.name"));
        this.field_224570_f.setMaxStringLength(32);
        this.field_224570_f.setText(this.field_224566_b.func_230775_b_());
        this.addListener(this.field_224570_f);
        this.setListenerDefault(this.field_224570_f);
        this.field_224569_e = new TextFieldWidget(this.mc.fontRenderer, i, func_239562_k_(8), 212, 20, (TextFieldWidget)null, new TranslationTextComponent("mco.configure.world.description"));
        this.field_224569_e.setMaxStringLength(32);
        this.field_224569_e.setText(this.field_224566_b.func_230768_a_());
        this.addListener(this.field_224569_e);
        this.field_224571_g = this.addListener(new RealmsLabel(new TranslationTextComponent("mco.configure.world.settings.title"), this.width / 2, 17, 16777215));
        this.func_231411_u_();
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.mc.displayGuiScreen(this.field_224565_a);
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
        this.field_224571_g.func_239560_a_(this, matrixStack);
        this.font.func_243248_b(matrixStack, field_243169_a, (float)(this.width / 2 - 106), (float)func_239562_k_(3), 10526880);
        this.font.func_243248_b(matrixStack, field_243170_b, (float)(this.width / 2 - 106), (float)func_239562_k_(7), 10526880);
        this.field_224570_f.render(matrixStack, mouseX, mouseY, partialTicks);
        this.field_224569_e.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void func_224563_a()
    {
        this.field_224565_a.func_224410_a(this.field_224570_f.getText(), this.field_224569_e.getText());
    }
}
