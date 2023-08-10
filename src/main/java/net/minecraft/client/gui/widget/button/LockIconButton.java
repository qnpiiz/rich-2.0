package net.minecraft.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class LockIconButton extends Button
{
    private boolean locked;

    public LockIconButton(int x, int y, Button.IPressable p_i51133_3_)
    {
        super(x, y, 20, 20, new TranslationTextComponent("narrator.button.difficulty_lock"), p_i51133_3_);
    }

    protected IFormattableTextComponent getNarrationMessage()
    {
        return super.getNarrationMessage().appendString(". ").append(this.isLocked() ? new TranslationTextComponent("narrator.button.difficulty_lock.locked") : new TranslationTextComponent("narrator.button.difficulty_lock.unlocked"));
    }

    public boolean isLocked()
    {
        return this.locked;
    }

    public void setLocked(boolean lockedIn)
    {
        this.locked = lockedIn;
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(Button.WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        LockIconButton.Icon lockiconbutton$icon;

        if (!this.active)
        {
            lockiconbutton$icon = this.locked ? LockIconButton.Icon.LOCKED_DISABLED : LockIconButton.Icon.UNLOCKED_DISABLED;
        }
        else if (this.isHovered())
        {
            lockiconbutton$icon = this.locked ? LockIconButton.Icon.LOCKED_HOVER : LockIconButton.Icon.UNLOCKED_HOVER;
        }
        else
        {
            lockiconbutton$icon = this.locked ? LockIconButton.Icon.LOCKED : LockIconButton.Icon.UNLOCKED;
        }

        this.blit(matrixStack, this.x, this.y, lockiconbutton$icon.getX(), lockiconbutton$icon.getY(), this.width, this.height);
    }

    static enum Icon
    {
        LOCKED(0, 146),
        LOCKED_HOVER(0, 166),
        LOCKED_DISABLED(0, 186),
        UNLOCKED(20, 146),
        UNLOCKED_HOVER(20, 166),
        UNLOCKED_DISABLED(20, 186);

        private final int x;
        private final int y;

        private Icon(int xIn, int yIn)
        {
            this.x = xIn;
            this.y = yIn;
        }

        public int getX()
        {
            return this.x;
        }

        public int getY()
        {
            return this.y;
        }
    }
}
