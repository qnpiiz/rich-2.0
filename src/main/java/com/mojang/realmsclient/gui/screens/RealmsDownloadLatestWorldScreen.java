package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.FileDownload;
import com.mojang.realmsclient.dto.WorldDownload;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.UploadSpeed;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsDownloadLatestWorldScreen extends RealmsScreen
{
    private static final Logger field_224175_a = LogManager.getLogger();
    private static final ReentrantLock field_237832_b_ = new ReentrantLock();
    private final Screen field_224176_b;
    private final WorldDownload field_224177_c;
    private final ITextComponent field_224178_d;
    private final RateLimiter field_224179_e;
    private Button field_224180_f;
    private final String field_224181_g;
    private final RealmsDownloadLatestWorldScreen.DownloadStatus field_224182_h;
    private volatile ITextComponent field_224183_i;
    private volatile ITextComponent field_224184_j = new TranslationTextComponent("mco.download.preparing");
    private volatile String field_224185_k;
    private volatile boolean field_224186_l;
    private volatile boolean field_224187_m = true;
    private volatile boolean field_224188_n;
    private volatile boolean field_224189_o;
    private Long field_224190_p;
    private Long field_224191_q;
    private long field_224192_r;
    private int field_224193_s;
    private static final String[] field_224194_t = new String[] {"", ".", ". .", ". . ."};
    private int field_224195_u;
    private boolean field_224198_x;
    private final BooleanConsumer field_237831_J_;

    public RealmsDownloadLatestWorldScreen(Screen p_i232203_1_, WorldDownload p_i232203_2_, String p_i232203_3_, BooleanConsumer p_i232203_4_)
    {
        this.field_237831_J_ = p_i232203_4_;
        this.field_224176_b = p_i232203_1_;
        this.field_224181_g = p_i232203_3_;
        this.field_224177_c = p_i232203_2_;
        this.field_224182_h = new RealmsDownloadLatestWorldScreen.DownloadStatus();
        this.field_224178_d = new TranslationTextComponent("mco.download.title");
        this.field_224179_e = RateLimiter.create((double)0.1F);
    }

    public void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.field_224180_f = this.addButton(new Button(this.width / 2 - 100, this.height - 42, 200, 20, DialogTexts.GUI_CANCEL, (p_237834_1_) ->
        {
            this.field_224186_l = true;
            this.func_224174_d();
        }));
        this.func_224162_c();
    }

    private void func_224162_c()
    {
        if (!this.field_224188_n)
        {
            if (!this.field_224198_x && this.func_224152_a(this.field_224177_c.field_230643_a_) >= 5368709120L)
            {
                ITextComponent itextcomponent = new TranslationTextComponent("mco.download.confirmation.line1", UploadSpeed.func_237684_b_(5368709120L));
                ITextComponent itextcomponent1 = new TranslationTextComponent("mco.download.confirmation.line2");
                this.mc.displayGuiScreen(new RealmsLongConfirmationScreen((p_237837_1_) ->
                {
                    this.field_224198_x = true;
                    this.mc.displayGuiScreen(this);
                    this.func_224165_h();
                }, RealmsLongConfirmationScreen.Type.Warning, itextcomponent, itextcomponent1, false));
            }
            else
            {
                this.func_224165_h();
            }
        }
    }

    private long func_224152_a(String p_224152_1_)
    {
        FileDownload filedownload = new FileDownload();
        return filedownload.func_224827_a(p_224152_1_);
    }

    public void tick()
    {
        super.tick();
        ++this.field_224193_s;

        if (this.field_224184_j != null && this.field_224179_e.tryAcquire(1))
        {
            List<ITextComponent> list = Lists.newArrayList();
            list.add(this.field_224178_d);
            list.add(this.field_224184_j);

            if (this.field_224185_k != null)
            {
                list.add(new StringTextComponent(this.field_224185_k + "%"));
                list.add(new StringTextComponent(UploadSpeed.func_237684_b_(this.field_224192_r) + "/s"));
            }

            if (this.field_224183_i != null)
            {
                list.add(this.field_224183_i);
            }

            String s = list.stream().map(ITextComponent::getString).collect(Collectors.joining("\n"));
            RealmsNarratorHelper.func_239550_a_(s);
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            this.field_224186_l = true;
            this.func_224174_d();
            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private void func_224174_d()
    {
        if (this.field_224188_n && this.field_237831_J_ != null && this.field_224183_i == null)
        {
            this.field_237831_J_.accept(true);
        }

        this.mc.displayGuiScreen(this.field_224176_b);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.field_224178_d, this.width / 2, 20, 16777215);
        drawCenteredString(matrixStack, this.font, this.field_224184_j, this.width / 2, 50, 16777215);

        if (this.field_224187_m)
        {
            this.func_237835_b_(matrixStack);
        }

        if (this.field_224182_h.field_225139_a != 0L && !this.field_224186_l)
        {
            this.func_237836_c_(matrixStack);
            this.func_237838_d_(matrixStack);
        }

        if (this.field_224183_i != null)
        {
            drawCenteredString(matrixStack, this.font, this.field_224183_i, this.width / 2, 110, 16711680);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void func_237835_b_(MatrixStack p_237835_1_)
    {
        int i = this.font.getStringPropertyWidth(this.field_224184_j);

        if (this.field_224193_s % 10 == 0)
        {
            ++this.field_224195_u;
        }

        this.font.drawString(p_237835_1_, field_224194_t[this.field_224195_u % field_224194_t.length], (float)(this.width / 2 + i / 2 + 5), 50.0F, 16777215);
    }

    private void func_237836_c_(MatrixStack p_237836_1_)
    {
        double d0 = Math.min((double)this.field_224182_h.field_225139_a / (double)this.field_224182_h.field_225140_b, 1.0D);
        this.field_224185_k = String.format(Locale.ROOT, "%.1f", d0 * 100.0D);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        double d1 = (double)(this.width / 2 - 100);
        double d2 = 0.5D;
        bufferbuilder.pos(d1 - 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
        bufferbuilder.pos(d1 + 200.0D * d0 + 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
        bufferbuilder.pos(d1 + 200.0D * d0 + 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
        bufferbuilder.pos(d1 - 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
        bufferbuilder.pos(d1, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
        bufferbuilder.pos(d1 + 200.0D * d0, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
        bufferbuilder.pos(d1 + 200.0D * d0, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
        bufferbuilder.pos(d1, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
        tessellator.draw();
        RenderSystem.enableTexture();
        drawCenteredString(p_237836_1_, this.font, this.field_224185_k + " %", this.width / 2, 84, 16777215);
    }

    private void func_237838_d_(MatrixStack p_237838_1_)
    {
        if (this.field_224193_s % 20 == 0)
        {
            if (this.field_224190_p != null)
            {
                long i = Util.milliTime() - this.field_224191_q;

                if (i == 0L)
                {
                    i = 1L;
                }

                this.field_224192_r = 1000L * (this.field_224182_h.field_225139_a - this.field_224190_p) / i;
                this.func_237833_a_(p_237838_1_, this.field_224192_r);
            }

            this.field_224190_p = this.field_224182_h.field_225139_a;
            this.field_224191_q = Util.milliTime();
        }
        else
        {
            this.func_237833_a_(p_237838_1_, this.field_224192_r);
        }
    }

    private void func_237833_a_(MatrixStack p_237833_1_, long p_237833_2_)
    {
        if (p_237833_2_ > 0L)
        {
            int i = this.font.getStringWidth(this.field_224185_k);
            String s = "(" + UploadSpeed.func_237684_b_(p_237833_2_) + "/s)";
            this.font.drawString(p_237833_1_, s, (float)(this.width / 2 + i / 2 + 15), 84.0F, 16777215);
        }
    }

    private void func_224165_h()
    {
        (new Thread(() ->
        {
            try {
                if (field_237832_b_.tryLock(1L, TimeUnit.SECONDS))
                {
                    if (this.field_224186_l)
                    {
                        this.func_224159_i();
                        return;
                    }

                    this.field_224184_j = new TranslationTextComponent("mco.download.downloading", this.field_224181_g);
                    FileDownload filedownload = new FileDownload();
                    filedownload.func_224827_a(this.field_224177_c.field_230643_a_);
                    filedownload.func_237688_a_(this.field_224177_c, this.field_224181_g, this.field_224182_h, this.mc.getSaveLoader());

                    while (!filedownload.func_224835_b())
                    {
                        if (filedownload.func_224836_c())
                        {
                            filedownload.func_224834_a();
                            this.field_224183_i = new TranslationTextComponent("mco.download.failed");
                            this.field_224180_f.setMessage(DialogTexts.GUI_DONE);
                            return;
                        }

                        if (filedownload.func_224837_d())
                        {
                            if (!this.field_224189_o)
                            {
                                this.field_224184_j = new TranslationTextComponent("mco.download.extracting");
                            }

                            this.field_224189_o = true;
                        }

                        if (this.field_224186_l)
                        {
                            filedownload.func_224834_a();
                            this.func_224159_i();
                            return;
                        }

                        try
                        {
                            Thread.sleep(500L);
                        }
                        catch (InterruptedException interruptedexception)
                        {
                            field_224175_a.error("Failed to check Realms backup download status");
                        }
                    }

                    this.field_224188_n = true;
                    this.field_224184_j = new TranslationTextComponent("mco.download.done");
                    this.field_224180_f.setMessage(DialogTexts.GUI_DONE);
                    return;
                }

                this.field_224184_j = new TranslationTextComponent("mco.download.failed");
            }
            catch (InterruptedException interruptedexception1)
            {
                field_224175_a.error("Could not acquire upload lock");
                return;
            }
            catch (Exception exception)
            {
                this.field_224183_i = new TranslationTextComponent("mco.download.failed");
                exception.printStackTrace();
                return;
            }
            finally {
                if (!field_237832_b_.isHeldByCurrentThread())
                {
                    return;
                }

                field_237832_b_.unlock();
                this.field_224187_m = false;
                this.field_224188_n = true;
            }
        })).start();
    }

    private void func_224159_i()
    {
        this.field_224184_j = new TranslationTextComponent("mco.download.cancelled");
    }

    public class DownloadStatus
    {
        public volatile long field_225139_a;
        public volatile long field_225140_b;
    }
}
