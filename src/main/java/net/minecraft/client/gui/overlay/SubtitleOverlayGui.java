package net.minecraft.client.gui.overlay;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;

public class SubtitleOverlayGui extends AbstractGui implements ISoundEventListener
{
    private final Minecraft client;
    private final List<SubtitleOverlayGui.Subtitle> subtitles = Lists.newArrayList();
    private boolean enabled;

    public SubtitleOverlayGui(Minecraft clientIn)
    {
        this.client = clientIn;
    }

    public void render(MatrixStack p_195620_1_)
    {
        if (!this.enabled && this.client.gameSettings.showSubtitles)
        {
            this.client.getSoundHandler().addListener(this);
            this.enabled = true;
        }
        else if (this.enabled && !this.client.gameSettings.showSubtitles)
        {
            this.client.getSoundHandler().removeListener(this);
            this.enabled = false;
        }

        if (this.enabled && !this.subtitles.isEmpty())
        {
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Vector3d vector3d = new Vector3d(this.client.player.getPosX(), this.client.player.getPosYEye(), this.client.player.getPosZ());
            Vector3d vector3d1 = (new Vector3d(0.0D, 0.0D, -1.0D)).rotatePitch(-this.client.player.rotationPitch * ((float)Math.PI / 180F)).rotateYaw(-this.client.player.rotationYaw * ((float)Math.PI / 180F));
            Vector3d vector3d2 = (new Vector3d(0.0D, 1.0D, 0.0D)).rotatePitch(-this.client.player.rotationPitch * ((float)Math.PI / 180F)).rotateYaw(-this.client.player.rotationYaw * ((float)Math.PI / 180F));
            Vector3d vector3d3 = vector3d1.crossProduct(vector3d2);
            int i = 0;
            int j = 0;
            Iterator<SubtitleOverlayGui.Subtitle> iterator = this.subtitles.iterator();

            while (iterator.hasNext())
            {
                SubtitleOverlayGui.Subtitle subtitleoverlaygui$subtitle = iterator.next();

                if (subtitleoverlaygui$subtitle.getStartTime() + 3000L <= Util.milliTime())
                {
                    iterator.remove();
                }
                else
                {
                    j = Math.max(j, this.client.fontRenderer.getStringPropertyWidth(subtitleoverlaygui$subtitle.func_238526_a_()));
                }
            }

            j = j + this.client.fontRenderer.getStringWidth("<") + this.client.fontRenderer.getStringWidth(" ") + this.client.fontRenderer.getStringWidth(">") + this.client.fontRenderer.getStringWidth(" ");

            for (SubtitleOverlayGui.Subtitle subtitleoverlaygui$subtitle1 : this.subtitles)
            {
                int k = 255;
                ITextComponent itextcomponent = subtitleoverlaygui$subtitle1.func_238526_a_();
                Vector3d vector3d4 = subtitleoverlaygui$subtitle1.getLocation().subtract(vector3d).normalize();
                double d0 = -vector3d3.dotProduct(vector3d4);
                double d1 = -vector3d1.dotProduct(vector3d4);
                boolean flag = d1 > 0.5D;
                int l = j / 2;
                int i1 = 9;
                int j1 = i1 / 2;
                float f = 1.0F;
                int k1 = this.client.fontRenderer.getStringPropertyWidth(itextcomponent);
                int l1 = MathHelper.floor(MathHelper.clampedLerp(255.0D, 75.0D, (double)((float)(Util.milliTime() - subtitleoverlaygui$subtitle1.getStartTime()) / 3000.0F)));
                int i2 = l1 << 16 | l1 << 8 | l1;
                RenderSystem.pushMatrix();
                RenderSystem.translatef((float)this.client.getMainWindow().getScaledWidth() - (float)l * 1.0F - 2.0F, (float)(this.client.getMainWindow().getScaledHeight() - 30) - (float)(i * (i1 + 1)) * 1.0F, 0.0F);
                RenderSystem.scalef(1.0F, 1.0F, 1.0F);
                fill(p_195620_1_, -l - 1, -j1 - 1, l + 1, j1 + 1, this.client.gameSettings.getTextBackgroundColor(0.8F));
                RenderSystem.enableBlend();

                if (!flag)
                {
                    if (d0 > 0.0D)
                    {
                        this.client.fontRenderer.drawString(p_195620_1_, ">", (float)(l - this.client.fontRenderer.getStringWidth(">")), (float)(-j1), i2 + -16777216);
                    }
                    else if (d0 < 0.0D)
                    {
                        this.client.fontRenderer.drawString(p_195620_1_, "<", (float)(-l), (float)(-j1), i2 + -16777216);
                    }
                }

                this.client.fontRenderer.func_243248_b(p_195620_1_, itextcomponent, (float)(-k1 / 2), (float)(-j1), i2 + -16777216);
                RenderSystem.popMatrix();
                ++i;
            }

            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
        }
    }

    public void onPlaySound(ISound soundIn, SoundEventAccessor accessor)
    {
        if (accessor.getSubtitle() != null)
        {
            ITextComponent itextcomponent = accessor.getSubtitle();

            if (!this.subtitles.isEmpty())
            {
                for (SubtitleOverlayGui.Subtitle subtitleoverlaygui$subtitle : this.subtitles)
                {
                    if (subtitleoverlaygui$subtitle.func_238526_a_().equals(itextcomponent))
                    {
                        subtitleoverlaygui$subtitle.refresh(new Vector3d(soundIn.getX(), soundIn.getY(), soundIn.getZ()));
                        return;
                    }
                }
            }

            this.subtitles.add(new SubtitleOverlayGui.Subtitle(itextcomponent, new Vector3d(soundIn.getX(), soundIn.getY(), soundIn.getZ())));
        }
    }

    public class Subtitle
    {
        private final ITextComponent subtitle;
        private long startTime;
        private Vector3d location;

        public Subtitle(ITextComponent p_i232263_2_, Vector3d p_i232263_3_)
        {
            this.subtitle = p_i232263_2_;
            this.location = p_i232263_3_;
            this.startTime = Util.milliTime();
        }

        public ITextComponent func_238526_a_()
        {
            return this.subtitle;
        }

        public long getStartTime()
        {
            return this.startTime;
        }

        public Vector3d getLocation()
        {
            return this.location;
        }

        public void refresh(Vector3d locationIn)
        {
            this.location = locationIn;
            this.startTime = Util.milliTime();
        }
    }
}
