package net.minecraft.client.gui.toasts;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class AdvancementToast implements IToast
{
    private final Advancement advancement;
    private boolean hasPlayedSound;

    public AdvancementToast(Advancement advancementIn)
    {
        this.advancement = advancementIn;
    }

    public IToast.Visibility func_230444_a_(MatrixStack p_230444_1_, ToastGui p_230444_2_, long p_230444_3_)
    {
        p_230444_2_.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        DisplayInfo displayinfo = this.advancement.getDisplay();
        p_230444_2_.blit(p_230444_1_, 0, 0, 0, 0, this.func_230445_a_(), this.func_238540_d_());

        if (displayinfo != null)
        {
            List<IReorderingProcessor> list = p_230444_2_.getMinecraft().fontRenderer.trimStringToWidth(displayinfo.getTitle(), 125);
            int i = displayinfo.getFrame() == FrameType.CHALLENGE ? 16746751 : 16776960;

            if (list.size() == 1)
            {
                p_230444_2_.getMinecraft().fontRenderer.func_243248_b(p_230444_1_, displayinfo.getFrame().getTranslatedToast(), 30.0F, 7.0F, i | -16777216);
                p_230444_2_.getMinecraft().fontRenderer.func_238422_b_(p_230444_1_, list.get(0), 30.0F, 18.0F, -1);
            }
            else
            {
                int j = 1500;
                float f = 300.0F;

                if (p_230444_3_ < 1500L)
                {
                    int k = MathHelper.floor(MathHelper.clamp((float)(1500L - p_230444_3_) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                    p_230444_2_.getMinecraft().fontRenderer.func_243248_b(p_230444_1_, displayinfo.getFrame().getTranslatedToast(), 30.0F, 11.0F, i | k);
                }
                else
                {
                    int i1 = MathHelper.floor(MathHelper.clamp((float)(p_230444_3_ - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                    int l = this.func_238540_d_() / 2 - list.size() * 9 / 2;

                    for (IReorderingProcessor ireorderingprocessor : list)
                    {
                        p_230444_2_.getMinecraft().fontRenderer.func_238422_b_(p_230444_1_, ireorderingprocessor, 30.0F, (float)l, 16777215 | i1);
                        l += 9;
                    }
                }
            }

            if (!this.hasPlayedSound && p_230444_3_ > 0L)
            {
                this.hasPlayedSound = true;

                if (displayinfo.getFrame() == FrameType.CHALLENGE)
                {
                    p_230444_2_.getMinecraft().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
                }
            }

            p_230444_2_.getMinecraft().getItemRenderer().renderItemAndEffectIntoGuiWithoutEntity(displayinfo.getIcon(), 8, 8);
            return p_230444_3_ >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
        }
        else
        {
            return IToast.Visibility.HIDE;
        }
    }
}
