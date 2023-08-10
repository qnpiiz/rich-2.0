package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.action.CreateWorldRealmsAction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RealmsCreateRealmScreen extends RealmsScreen
{
    private static final ITextComponent field_243116_a = new TranslationTextComponent("mco.configure.world.name");
    private static final ITextComponent field_243117_b = new TranslationTextComponent("mco.configure.world.description");
    private final RealmsServer field_224135_a;
    private final RealmsMainScreen field_224136_b;
    private TextFieldWidget field_224137_c;
    private TextFieldWidget field_224138_d;
    private Button field_224139_e;
    private RealmsLabel field_224140_f;

    public RealmsCreateRealmScreen(RealmsServer p_i51772_1_, RealmsMainScreen p_i51772_2_)
    {
        this.field_224135_a = p_i51772_1_;
        this.field_224136_b = p_i51772_2_;
    }

    public void tick()
    {
        if (this.field_224137_c != null)
        {
            this.field_224137_c.tick();
        }

        if (this.field_224138_d != null)
        {
            this.field_224138_d.tick();
        }
    }

    public void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.field_224139_e = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 17, 97, 20, new TranslationTextComponent("mco.create.world"), (p_237828_1_) ->
        {
            this.func_224132_a();
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height / 4 + 120 + 17, 95, 20, DialogTexts.GUI_CANCEL, (p_237827_1_) ->
        {
            this.mc.displayGuiScreen(this.field_224136_b);
        }));
        this.field_224139_e.active = false;
        this.field_224137_c = new TextFieldWidget(this.mc.fontRenderer, this.width / 2 - 100, 65, 200, 20, (TextFieldWidget)null, new TranslationTextComponent("mco.configure.world.name"));
        this.addListener(this.field_224137_c);
        this.setFocusedDefault(this.field_224137_c);
        this.field_224138_d = new TextFieldWidget(this.mc.fontRenderer, this.width / 2 - 100, 115, 200, 20, (TextFieldWidget)null, new TranslationTextComponent("mco.configure.world.description"));
        this.addListener(this.field_224138_d);
        this.field_224140_f = new RealmsLabel(new TranslationTextComponent("mco.selectServer.create"), this.width / 2, 11, 16777215);
        this.addListener(this.field_224140_f);
        this.func_231411_u_();
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public boolean charTyped(char codePoint, int modifiers)
    {
        boolean flag = super.charTyped(codePoint, modifiers);
        this.field_224139_e.active = this.func_224133_b();
        return flag;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.mc.displayGuiScreen(this.field_224136_b);
            return true;
        }
        else
        {
            boolean flag = super.keyPressed(keyCode, scanCode, modifiers);
            this.field_224139_e.active = this.func_224133_b();
            return flag;
        }
    }

    private void func_224132_a()
    {
        if (this.func_224133_b())
        {
            RealmsResetWorldScreen realmsresetworldscreen = new RealmsResetWorldScreen(this.field_224136_b, this.field_224135_a, new TranslationTextComponent("mco.selectServer.create"), new TranslationTextComponent("mco.create.world.subtitle"), 10526880, new TranslationTextComponent("mco.create.world.skip"), () ->
            {
                this.mc.displayGuiScreen(this.field_224136_b.func_223942_f());
            }, () ->
            {
                this.mc.displayGuiScreen(this.field_224136_b.func_223942_f());
            });
            realmsresetworldscreen.func_224432_a(new TranslationTextComponent("mco.create.world.reset.title"));
            this.mc.displayGuiScreen(new RealmsLongRunningMcoTaskScreen(this.field_224136_b, new CreateWorldRealmsAction(this.field_224135_a.field_230582_a_, this.field_224137_c.getText(), this.field_224138_d.getText(), realmsresetworldscreen)));
        }
    }

    private boolean func_224133_b()
    {
        return !this.field_224137_c.getText().trim().isEmpty();
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.field_224140_f.func_239560_a_(this, matrixStack);
        this.font.func_243248_b(matrixStack, field_243116_a, (float)(this.width / 2 - 100), 52.0F, 10526880);
        this.font.func_243248_b(matrixStack, field_243117_b, (float)(this.width / 2 - 100), 102.0F, 10526880);

        if (this.field_224137_c != null)
        {
            this.field_224137_c.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        if (this.field_224138_d != null)
        {
            this.field_224138_d.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
