package net.minecraft.client.gui.spectator;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;

public interface ISpectatorMenuObject
{
    void selectItem(SpectatorMenu menu);

    ITextComponent getSpectatorName();

    void func_230485_a_(MatrixStack p_230485_1_, float p_230485_2_, int p_230485_3_);

    boolean isEnabled();
}
