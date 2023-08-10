package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RealmsGenericErrorScreen extends RealmsScreen
{
    private final Screen field_224228_a;
    private ITextComponent field_224229_b;
    private ITextComponent field_224230_c;

    public RealmsGenericErrorScreen(RealmsServiceException p_i232204_1_, Screen p_i232204_2_)
    {
        this.field_224228_a = p_i232204_2_;
        this.func_224224_a(p_i232204_1_);
    }

    public RealmsGenericErrorScreen(ITextComponent p_i232205_1_, Screen p_i232205_2_)
    {
        this.field_224228_a = p_i232205_2_;
        this.func_237841_a_(p_i232205_1_);
    }

    public RealmsGenericErrorScreen(ITextComponent p_i232206_1_, ITextComponent p_i232206_2_, Screen p_i232206_3_)
    {
        this.field_224228_a = p_i232206_3_;
        this.func_237842_a_(p_i232206_1_, p_i232206_2_);
    }

    private void func_224224_a(RealmsServiceException p_224224_1_)
    {
        if (p_224224_1_.field_224983_c == -1)
        {
            this.field_224229_b = new StringTextComponent("An error occurred (" + p_224224_1_.field_224981_a + "):");
            this.field_224230_c = new StringTextComponent(p_224224_1_.field_224982_b);
        }
        else
        {
            this.field_224229_b = new StringTextComponent("Realms (" + p_224224_1_.field_224983_c + "):");
            String s = "mco.errorMessage." + p_224224_1_.field_224983_c;
            this.field_224230_c = (ITextComponent)(I18n.hasKey(s) ? new TranslationTextComponent(s) : ITextComponent.getTextComponentOrEmpty(p_224224_1_.field_224984_d));
        }
    }

    private void func_237841_a_(ITextComponent p_237841_1_)
    {
        this.field_224229_b = new StringTextComponent("An error occurred: ");
        this.field_224230_c = p_237841_1_;
    }

    private void func_237842_a_(ITextComponent p_237842_1_, ITextComponent p_237842_2_)
    {
        this.field_224229_b = p_237842_1_;
        this.field_224230_c = p_237842_2_;
    }

    public void init()
    {
        RealmsNarratorHelper.func_239550_a_(this.field_224229_b.getString() + ": " + this.field_224230_c.getString());
        this.addButton(new Button(this.width / 2 - 100, this.height - 52, 200, 20, new StringTextComponent("Ok"), (p_237840_1_) ->
        {
            this.mc.displayGuiScreen(this.field_224228_a);
        }));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.field_224229_b, this.width / 2, 80, 16777215);
        drawCenteredString(matrixStack, this.font, this.field_224230_c, this.width / 2, 100, 16711680);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
