package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.ITextComponent;

public class RealmsConfirmScreen extends RealmsScreen
{
    protected BooleanConsumer field_237824_a_;
    private final ITextComponent field_224142_b;
    private final ITextComponent field_224146_f;
    private int field_224147_g;

    public RealmsConfirmScreen(BooleanConsumer p_i232202_1_, ITextComponent p_i232202_2_, ITextComponent p_i232202_3_)
    {
        this.field_237824_a_ = p_i232202_1_;
        this.field_224142_b = p_i232202_2_;
        this.field_224146_f = p_i232202_3_;
    }

    public void init()
    {
        this.addButton(new Button(this.width / 2 - 105, func_239562_k_(9), 100, 20, DialogTexts.GUI_YES, (p_237826_1_) ->
        {
            this.field_237824_a_.accept(true);
        }));
        this.addButton(new Button(this.width / 2 + 5, func_239562_k_(9), 100, 20, DialogTexts.GUI_NO, (p_237825_1_) ->
        {
            this.field_237824_a_.accept(false);
        }));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.field_224142_b, this.width / 2, func_239562_k_(3), 16777215);
        drawCenteredString(matrixStack, this.font, this.field_224146_f, this.width / 2, func_239562_k_(5), 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void tick()
    {
        super.tick();

        if (--this.field_224147_g == 0)
        {
            for (Widget widget : this.buttons)
            {
                widget.active = true;
            }
        }
    }
}
