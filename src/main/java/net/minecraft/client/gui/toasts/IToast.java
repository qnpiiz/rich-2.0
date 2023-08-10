package net.minecraft.client.gui.toasts;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public interface IToast
{
    ResourceLocation TEXTURE_TOASTS = new ResourceLocation("textures/gui/toasts.png");
    Object NO_TOKEN = new Object();

    IToast.Visibility func_230444_a_(MatrixStack p_230444_1_, ToastGui p_230444_2_, long p_230444_3_);

default Object getType()
    {
        return NO_TOKEN;
    }

default int func_230445_a_()
    {
        return 160;
    }

default int func_238540_d_()
    {
        return 32;
    }

    public static enum Visibility
    {
        SHOW(SoundEvents.UI_TOAST_IN),
        HIDE(SoundEvents.UI_TOAST_OUT);

        private final SoundEvent sound;

        private Visibility(SoundEvent soundIn)
        {
            this.sound = soundIn;
        }

        public void playSound(SoundHandler handler)
        {
            handler.play(SimpleSound.master(this.sound, 1.0F, 1.0F));
        }
    }
}
