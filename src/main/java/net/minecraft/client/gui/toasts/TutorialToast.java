package net.minecraft.client.gui.toasts;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class TutorialToast implements IToast
{
    private final TutorialToast.Icons icon;
    private final ITextComponent title;
    private final ITextComponent subtitle;
    private IToast.Visibility visibility = IToast.Visibility.SHOW;
    private long lastDelta;
    private float displayedProgress;
    private float currentProgress;
    private final boolean hasProgressBar;

    public TutorialToast(TutorialToast.Icons iconIn, ITextComponent titleComponent, @Nullable ITextComponent subtitleComponent, boolean drawProgressBar)
    {
        this.icon = iconIn;
        this.title = titleComponent;
        this.subtitle = subtitleComponent;
        this.hasProgressBar = drawProgressBar;
    }

    public IToast.Visibility func_230444_a_(MatrixStack p_230444_1_, ToastGui p_230444_2_, long p_230444_3_)
    {
        p_230444_2_.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        p_230444_2_.blit(p_230444_1_, 0, 0, 0, 96, this.func_230445_a_(), this.func_238540_d_());
        this.icon.func_238543_a_(p_230444_1_, p_230444_2_, 6, 6);

        if (this.subtitle == null)
        {
            p_230444_2_.getMinecraft().fontRenderer.func_243248_b(p_230444_1_, this.title, 30.0F, 12.0F, -11534256);
        }
        else
        {
            p_230444_2_.getMinecraft().fontRenderer.func_243248_b(p_230444_1_, this.title, 30.0F, 7.0F, -11534256);
            p_230444_2_.getMinecraft().fontRenderer.func_243248_b(p_230444_1_, this.subtitle, 30.0F, 18.0F, -16777216);
        }

        if (this.hasProgressBar)
        {
            AbstractGui.fill(p_230444_1_, 3, 28, 157, 29, -1);
            float f = (float)MathHelper.clampedLerp((double)this.displayedProgress, (double)this.currentProgress, (double)((float)(p_230444_3_ - this.lastDelta) / 100.0F));
            int i;

            if (this.currentProgress >= this.displayedProgress)
            {
                i = -16755456;
            }
            else
            {
                i = -11206656;
            }

            AbstractGui.fill(p_230444_1_, 3, 28, (int)(3.0F + 154.0F * f), 29, i);
            this.displayedProgress = f;
            this.lastDelta = p_230444_3_;
        }

        return this.visibility;
    }

    public void hide()
    {
        this.visibility = IToast.Visibility.HIDE;
    }

    public void setProgress(float progress)
    {
        this.currentProgress = progress;
    }

    public static enum Icons
    {
        MOVEMENT_KEYS(0, 0),
        MOUSE(1, 0),
        TREE(2, 0),
        RECIPE_BOOK(0, 1),
        WOODEN_PLANKS(1, 1),
        SOCIAL_INTERACTIONS(2, 1);

        private final int column;
        private final int row;

        private Icons(int columnIn, int rowIn)
        {
            this.column = columnIn;
            this.row = rowIn;
        }

        public void func_238543_a_(MatrixStack p_238543_1_, AbstractGui p_238543_2_, int p_238543_3_, int p_238543_4_)
        {
            RenderSystem.enableBlend();
            p_238543_2_.blit(p_238543_1_, p_238543_3_, p_238543_4_, 176 + this.column * 20, this.row * 20, 20, 20);
            RenderSystem.enableBlend();
        }
    }
}
