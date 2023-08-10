package net.minecraft.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;

public class ChangePageButton extends Button
{
    private final boolean isForward;
    private final boolean playTurnSound;

    public ChangePageButton(int x, int y, boolean isForward, Button.IPressable onPress, boolean playTurnSound)
    {
        super(x, y, 23, 13, StringTextComponent.EMPTY, onPress);
        this.isForward = isForward;
        this.playTurnSound = playTurnSound;
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(ReadBookScreen.BOOK_TEXTURES);
        int i = 0;
        int j = 192;

        if (this.isHovered())
        {
            i += 23;
        }

        if (!this.isForward)
        {
            j += 13;
        }

        this.blit(matrixStack, this.x, this.y, i, j, 23, 13);
    }

    public void playDownSound(SoundHandler handler)
    {
        if (this.playTurnSound)
        {
            handler.play(SimpleSound.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
        }
    }
}
