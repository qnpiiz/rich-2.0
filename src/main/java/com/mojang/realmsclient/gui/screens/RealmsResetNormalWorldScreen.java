package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RealmsResetNormalWorldScreen extends RealmsScreen
{
    private static final ITextComponent field_243144_a = new TranslationTextComponent("mco.reset.world.seed");
    private static final ITextComponent[] field_243145_b = new ITextComponent[] {new TranslationTextComponent("generator.default"), new TranslationTextComponent("generator.flat"), new TranslationTextComponent("generator.large_biomes"), new TranslationTextComponent("generator.amplified")};
    private final RealmsResetWorldScreen field_224354_b;
    private RealmsLabel field_224355_c;
    private TextFieldWidget field_224356_d;
    private Boolean field_224357_e = true;
    private Integer field_224358_f = 0;
    private ITextComponent field_224365_m;

    public RealmsResetNormalWorldScreen(RealmsResetWorldScreen p_i232214_1_, ITextComponent p_i232214_2_)
    {
        this.field_224354_b = p_i232214_1_;
        this.field_224365_m = p_i232214_2_;
    }

    public void tick()
    {
        this.field_224356_d.tick();
        super.tick();
    }

    public void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.field_224355_c = new RealmsLabel(new TranslationTextComponent("mco.reset.world.generate"), this.width / 2, 17, 16777215);
        this.addListener(this.field_224355_c);
        this.field_224356_d = new TextFieldWidget(this.mc.fontRenderer, this.width / 2 - 100, func_239562_k_(2), 200, 20, (TextFieldWidget)null, new TranslationTextComponent("mco.reset.world.seed"));
        this.field_224356_d.setMaxStringLength(32);
        this.addListener(this.field_224356_d);
        this.setFocusedDefault(this.field_224356_d);
        this.addButton(new Button(this.width / 2 - 102, func_239562_k_(4), 205, 20, this.func_237937_g_(), (p_237936_1_) ->
        {
            this.field_224358_f = (this.field_224358_f + 1) % field_243145_b.length;
            p_237936_1_.setMessage(this.func_237937_g_());
        }));
        this.addButton(new Button(this.width / 2 - 102, func_239562_k_(6) - 2, 205, 20, this.func_237938_j_(), (p_237935_1_) ->
        {
            this.field_224357_e = !this.field_224357_e;
            p_237935_1_.setMessage(this.func_237938_j_());
        }));
        this.addButton(new Button(this.width / 2 - 102, func_239562_k_(12), 97, 20, this.field_224365_m, (p_237934_1_) ->
        {
            this.field_224354_b.func_224438_a(new RealmsResetWorldScreen.ResetWorldInfo(this.field_224356_d.getText(), this.field_224358_f, this.field_224357_e));
        }));
        this.addButton(new Button(this.width / 2 + 8, func_239562_k_(12), 97, 20, DialogTexts.GUI_BACK, (p_237933_1_) ->
        {
            this.mc.displayGuiScreen(this.field_224354_b);
        }));
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
            this.mc.displayGuiScreen(this.field_224354_b);
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
        this.field_224355_c.func_239560_a_(this, matrixStack);
        this.font.func_243248_b(matrixStack, field_243144_a, (float)(this.width / 2 - 100), (float)func_239562_k_(1), 10526880);
        this.field_224356_d.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private ITextComponent func_237937_g_()
    {
        return (new TranslationTextComponent("selectWorld.mapType")).appendString(" ").append(field_243145_b[this.field_224358_f]);
    }

    private ITextComponent func_237938_j_()
    {
        return DialogTexts.getComposedOptionMessage(new TranslationTextComponent("selectWorld.mapFeatures"), this.field_224357_e);
    }
}
