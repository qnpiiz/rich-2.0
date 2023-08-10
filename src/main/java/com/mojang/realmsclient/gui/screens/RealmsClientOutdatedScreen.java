package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RealmsClientOutdatedScreen extends RealmsScreen
{
    private static final ITextComponent field_243104_a = new TranslationTextComponent("mco.client.outdated.title");
    private static final ITextComponent[] field_243105_b = new ITextComponent[] {new TranslationTextComponent("mco.client.outdated.msg.line1"), new TranslationTextComponent("mco.client.outdated.msg.line2")};
    private static final ITextComponent field_243106_c = new TranslationTextComponent("mco.client.incompatible.title");
    private static final ITextComponent[] field_243107_p = new ITextComponent[] {new TranslationTextComponent("mco.client.incompatible.msg.line1"), new TranslationTextComponent("mco.client.incompatible.msg.line2"), new TranslationTextComponent("mco.client.incompatible.msg.line3")};
    private final Screen field_224129_a;
    private final boolean field_224130_b;

    public RealmsClientOutdatedScreen(Screen p_i232201_1_, boolean p_i232201_2_)
    {
        this.field_224129_a = p_i232201_1_;
        this.field_224130_b = p_i232201_2_;
    }

    public void init()
    {
        this.addButton(new Button(this.width / 2 - 100, func_239562_k_(12), 200, 20, DialogTexts.GUI_BACK, (p_237786_1_) ->
        {
            this.mc.displayGuiScreen(this.field_224129_a);
        }));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        ITextComponent itextcomponent;
        ITextComponent[] aitextcomponent;

        if (this.field_224130_b)
        {
            itextcomponent = field_243106_c;
            aitextcomponent = field_243107_p;
        }
        else
        {
            itextcomponent = field_243104_a;
            aitextcomponent = field_243105_b;
        }

        drawCenteredString(matrixStack, this.font, itextcomponent, this.width / 2, func_239562_k_(3), 16711680);

        for (int i = 0; i < aitextcomponent.length; ++i)
        {
            drawCenteredString(matrixStack, this.font, aitextcomponent[i], this.width / 2, func_239562_k_(5) + i * 12, 16777215);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode != 257 && keyCode != 335 && keyCode != 256)
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        else
        {
            this.mc.displayGuiScreen(this.field_224129_a);
            return true;
        }
    }
}
