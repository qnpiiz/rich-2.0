package net.minecraft.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class SpectatorGui extends AbstractGui implements ISpectatorMenuRecipient
{
    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
    public static final ResourceLocation SPECTATOR_WIDGETS = new ResourceLocation("textures/gui/spectator_widgets.png");
    private final Minecraft mc;
    private long lastSelectionTime;
    private SpectatorMenu menu;

    public SpectatorGui(Minecraft mcIn)
    {
        this.mc = mcIn;
    }

    public void onHotbarSelected(int p_175260_1_)
    {
        this.lastSelectionTime = Util.milliTime();

        if (this.menu != null)
        {
            this.menu.selectSlot(p_175260_1_);
        }
        else
        {
            this.menu = new SpectatorMenu(this);
        }
    }

    private float getHotbarAlpha()
    {
        long i = this.lastSelectionTime - Util.milliTime() + 5000L;
        return MathHelper.clamp((float)i / 2000.0F, 0.0F, 1.0F);
    }

    public void func_238528_a_(MatrixStack p_238528_1_, float p_238528_2_)
    {
        if (this.menu != null)
        {
            float f = this.getHotbarAlpha();

            if (f <= 0.0F)
            {
                this.menu.exit();
            }
            else
            {
                int i = this.mc.getMainWindow().getScaledWidth() / 2;
                int j = this.getBlitOffset();
                this.setBlitOffset(-90);
                int k = MathHelper.floor((float)this.mc.getMainWindow().getScaledHeight() - 22.0F * f);
                SpectatorDetails spectatordetails = this.menu.getCurrentPage();
                this.func_238529_a_(p_238528_1_, f, i, k, spectatordetails);
                this.setBlitOffset(j);
            }
        }
    }

    protected void func_238529_a_(MatrixStack p_238529_1_, float p_238529_2_, int p_238529_3_, int p_238529_4_, SpectatorDetails p_238529_5_)
    {
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, p_238529_2_);
        this.mc.getTextureManager().bindTexture(WIDGETS);
        this.blit(p_238529_1_, p_238529_3_ - 91, p_238529_4_, 0, 0, 182, 22);

        if (p_238529_5_.getSelectedSlot() >= 0)
        {
            this.blit(p_238529_1_, p_238529_3_ - 91 - 1 + p_238529_5_.getSelectedSlot() * 20, p_238529_4_ - 1, 0, 22, 24, 22);
        }

        for (int i = 0; i < 9; ++i)
        {
            this.func_238530_a_(p_238529_1_, i, this.mc.getMainWindow().getScaledWidth() / 2 - 90 + i * 20 + 2, (float)(p_238529_4_ + 3), p_238529_2_, p_238529_5_.getObject(i));
        }

        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
    }

    private void func_238530_a_(MatrixStack p_238530_1_, int p_238530_2_, int p_238530_3_, float p_238530_4_, float p_238530_5_, ISpectatorMenuObject p_238530_6_)
    {
        this.mc.getTextureManager().bindTexture(SPECTATOR_WIDGETS);

        if (p_238530_6_ != SpectatorMenu.EMPTY_SLOT)
        {
            int i = (int)(p_238530_5_ * 255.0F);
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)p_238530_3_, p_238530_4_, 0.0F);
            float f = p_238530_6_.isEnabled() ? 1.0F : 0.25F;
            RenderSystem.color4f(f, f, f, p_238530_5_);
            p_238530_6_.func_230485_a_(p_238530_1_, f, i);
            RenderSystem.popMatrix();

            if (i > 3 && p_238530_6_.isEnabled())
            {
                ITextComponent itextcomponent = this.mc.gameSettings.keyBindsHotbar[p_238530_2_].func_238171_j_();
                this.mc.fontRenderer.func_243246_a(p_238530_1_, itextcomponent, (float)(p_238530_3_ + 19 - 2 - this.mc.fontRenderer.getStringPropertyWidth(itextcomponent)), p_238530_4_ + 6.0F + 3.0F, 16777215 + (i << 24));
            }
        }
    }

    public void func_238527_a_(MatrixStack p_238527_1_)
    {
        int i = (int)(this.getHotbarAlpha() * 255.0F);

        if (i > 3 && this.menu != null)
        {
            ISpectatorMenuObject ispectatormenuobject = this.menu.getSelectedItem();
            ITextComponent itextcomponent = ispectatormenuobject == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt() : ispectatormenuobject.getSpectatorName();

            if (itextcomponent != null)
            {
                int j = (this.mc.getMainWindow().getScaledWidth() - this.mc.fontRenderer.getStringPropertyWidth(itextcomponent)) / 2;
                int k = this.mc.getMainWindow().getScaledHeight() - 35;
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                this.mc.fontRenderer.func_243246_a(p_238527_1_, itextcomponent, (float)j, (float)k, 16777215 + (i << 24));
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            }
        }
    }

    public void onSpectatorMenuClosed(SpectatorMenu menu)
    {
        this.menu = null;
        this.lastSelectionTime = 0L;
    }

    public boolean isMenuActive()
    {
        return this.menu != null;
    }

    public void onMouseScroll(double amount)
    {
        int i;

        for (i = this.menu.getSelectedSlot() + (int)amount; i >= 0 && i <= 8 && (this.menu.getItem(i) == SpectatorMenu.EMPTY_SLOT || !this.menu.getItem(i).isEnabled()); i = (int)((double)i + amount))
        {
        }

        if (i >= 0 && i <= 8)
        {
            this.menu.selectSlot(i);
            this.lastSelectionTime = Util.milliTime();
        }
    }

    public void onMiddleClick()
    {
        this.lastSelectionTime = Util.milliTime();

        if (this.isMenuActive())
        {
            int i = this.menu.getSelectedSlot();

            if (i != -1)
            {
                this.menu.selectSlot(i);
            }
        }
        else
        {
            this.menu = new SpectatorMenu(this);
        }
    }
}
