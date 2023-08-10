package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RealmsParentalConsentScreen extends RealmsScreen
{
    private static final ITextComponent field_243122_a = new TranslationTextComponent("mco.account.privacyinfo");
    private final Screen field_224260_a;
    private IBidiRenderer field_243123_c = IBidiRenderer.field_243257_a;

    public RealmsParentalConsentScreen(Screen p_i232210_1_)
    {
        this.field_224260_a = p_i232210_1_;
    }

    public void init()
    {
        RealmsNarratorHelper.func_239550_a_(field_243122_a.getString());
        ITextComponent itextcomponent = new TranslationTextComponent("mco.account.update");
        ITextComponent itextcomponent1 = DialogTexts.GUI_BACK;
        int i = Math.max(this.font.getStringPropertyWidth(itextcomponent), this.font.getStringPropertyWidth(itextcomponent1)) + 30;
        ITextComponent itextcomponent2 = new TranslationTextComponent("mco.account.privacy.info");
        int j = (int)((double)this.font.getStringPropertyWidth(itextcomponent2) * 1.2D);
        this.addButton(new Button(this.width / 2 - j / 2, func_239562_k_(11), j, 20, itextcomponent2, (p_237862_0_) ->
        {
            Util.getOSType().openURI("https://aka.ms/MinecraftGDPR");
        }));
        this.addButton(new Button(this.width / 2 - (i + 5), func_239562_k_(13), i, 20, itextcomponent, (p_237861_0_) ->
        {
            Util.getOSType().openURI("https://aka.ms/UpdateMojangAccount");
        }));
        this.addButton(new Button(this.width / 2 + 5, func_239562_k_(13), i, 20, itextcomponent1, (p_237860_1_) ->
        {
            this.mc.displayGuiScreen(this.field_224260_a);
        }));
        this.field_243123_c = IBidiRenderer.func_243258_a(this.font, field_243122_a, (int)Math.round((double)this.width * 0.9D));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        this.field_243123_c.func_241864_a(matrixStack, this.width / 2, 15, 15, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
