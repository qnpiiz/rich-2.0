package net.minecraft.realms;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

public class RealmsLabel implements IGuiEventListener
{
    private final ITextComponent field_230723_a_;
    private final int field_230724_b_;
    private final int field_230725_c_;
    private final int field_230726_d_;

    public RealmsLabel(ITextComponent p_i232502_1_, int p_i232502_2_, int p_i232502_3_, int p_i232502_4_)
    {
        this.field_230723_a_ = p_i232502_1_;
        this.field_230724_b_ = p_i232502_2_;
        this.field_230725_c_ = p_i232502_3_;
        this.field_230726_d_ = p_i232502_4_;
    }

    public void func_239560_a_(Screen p_239560_1_, MatrixStack p_239560_2_)
    {
        Screen.drawCenteredString(p_239560_2_, Minecraft.getInstance().fontRenderer, this.field_230723_a_, this.field_230724_b_, this.field_230725_c_, this.field_230726_d_);
    }

    public String func_231399_a_()
    {
        return this.field_230723_a_.getString();
    }
}
