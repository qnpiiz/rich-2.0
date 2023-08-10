package net.minecraft.realms;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class DisconnectedRealmsScreen extends RealmsScreen
{
    private final ITextComponent field_230713_a_;
    private final ITextComponent field_230714_b_;
    private IBidiRenderer field_243509_c = IBidiRenderer.field_243257_a;
    private final Screen field_230716_p_;
    private int field_230717_q_;

    public DisconnectedRealmsScreen(Screen p_i242069_1_, ITextComponent p_i242069_2_, ITextComponent p_i242069_3_)
    {
        this.field_230716_p_ = p_i242069_1_;
        this.field_230713_a_ = p_i242069_2_;
        this.field_230714_b_ = p_i242069_3_;
    }

    public void init()
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setConnectedToRealms(false);
        minecraft.getPackFinder().clearResourcePack();
        RealmsNarratorHelper.func_239550_a_(this.field_230713_a_.getString() + ": " + this.field_230714_b_.getString());
        this.field_243509_c = IBidiRenderer.func_243258_a(this.font, this.field_230714_b_, this.width - 50);
        this.field_230717_q_ = this.field_243509_c.func_241862_a() * 9;
        this.addButton(new Button(this.width / 2 - 100, this.height / 2 + this.field_230717_q_ / 2 + 9, 200, 20, DialogTexts.GUI_BACK, (p_239547_2_) ->
        {
            minecraft.displayGuiScreen(this.field_230716_p_);
        }));
    }

    public void closeScreen()
    {
        Minecraft.getInstance().displayGuiScreen(this.field_230716_p_);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.field_230713_a_, this.width / 2, this.height / 2 - this.field_230717_q_ / 2 - 9 * 2, 11184810);
        this.field_243509_c.func_241863_a(matrixStack, this.width / 2, this.height / 2 - this.field_230717_q_ / 2);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
