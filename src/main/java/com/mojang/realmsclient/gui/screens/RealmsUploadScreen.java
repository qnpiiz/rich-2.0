package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.UploadSpeed;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.WorldSummary;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsUploadScreen extends RealmsScreen
{
    private static final Logger field_224696_a = LogManager.getLogger();
    private static final ReentrantLock field_238081_b_ = new ReentrantLock();
    private static final String[] field_224713_r = new String[] {"", ".", ". .", ". . ."};
    private static final ITextComponent field_243187_p = new TranslationTextComponent("mco.upload.verifying");
    private final RealmsResetWorldScreen field_224697_b;
    private final WorldSummary field_224698_c;
    private final long field_224699_d;
    private final int field_224700_e;
    private final UploadStatus field_224701_f;
    private final RateLimiter field_224702_g;
    private volatile ITextComponent[] field_224703_h;
    private volatile ITextComponent field_224704_i = new TranslationTextComponent("mco.upload.preparing");
    private volatile String field_224705_j;
    private volatile boolean field_224706_k;
    private volatile boolean field_224707_l;
    private volatile boolean field_224708_m = true;
    private volatile boolean field_224709_n;
    private Button field_224710_o;
    private Button field_224711_p;
    private int field_238079_E_;
    private Long field_224715_t;
    private Long field_224716_u;
    private long field_224717_v;
    private final Runnable field_238080_I_;

    public RealmsUploadScreen(long p_i232226_1_, int p_i232226_3_, RealmsResetWorldScreen p_i232226_4_, WorldSummary p_i232226_5_, Runnable p_i232226_6_)
    {
        this.field_224699_d = p_i232226_1_;
        this.field_224700_e = p_i232226_3_;
        this.field_224697_b = p_i232226_4_;
        this.field_224698_c = p_i232226_5_;
        this.field_224701_f = new UploadStatus();
        this.field_224702_g = RateLimiter.create((double)0.1F);
        this.field_238080_I_ = p_i232226_6_;
    }

    public void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.field_224710_o = this.addButton(new Button(this.width / 2 - 100, this.height - 42, 200, 20, DialogTexts.GUI_BACK, (p_238087_1_) ->
        {
            this.func_224679_c();
        }));
        this.field_224710_o.visible = false;
        this.field_224711_p = this.addButton(new Button(this.width / 2 - 100, this.height - 42, 200, 20, DialogTexts.GUI_CANCEL, (p_238084_1_) ->
        {
            this.func_224695_d();
        }));

        if (!this.field_224709_n)
        {
            if (this.field_224697_b.field_224455_a == -1)
            {
                this.func_224682_h();
            }
            else
            {
                this.field_224697_b.func_237952_a_(() ->
                {
                    if (!this.field_224709_n)
                    {
                        this.field_224709_n = true;
                        this.mc.displayGuiScreen(this);
                        this.func_224682_h();
                    }
                });
            }
        }
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    private void func_224679_c()
    {
        this.field_238080_I_.run();
    }

    private void func_224695_d()
    {
        this.field_224706_k = true;
        this.mc.displayGuiScreen(this.field_224697_b);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == 256)
        {
            if (this.field_224708_m)
            {
                this.func_224695_d();
            }
            else
            {
                this.func_224679_c();
            }

            return true;
        }
        else
        {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);

        if (!this.field_224707_l && this.field_224701_f.field_224978_a != 0L && this.field_224701_f.field_224978_a == this.field_224701_f.field_224979_b)
        {
            this.field_224704_i = field_243187_p;
            this.field_224711_p.active = false;
        }

        drawCenteredString(matrixStack, this.font, this.field_224704_i, this.width / 2, 50, 16777215);

        if (this.field_224708_m)
        {
            this.func_238086_b_(matrixStack);
        }

        if (this.field_224701_f.field_224978_a != 0L && !this.field_224706_k)
        {
            this.func_238088_c_(matrixStack);
            this.func_238089_d_(matrixStack);
        }

        if (this.field_224703_h != null)
        {
            for (int i = 0; i < this.field_224703_h.length; ++i)
            {
                drawCenteredString(matrixStack, this.font, this.field_224703_h[i], this.width / 2, 110 + 12 * i, 16711680);
            }
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void func_238086_b_(MatrixStack p_238086_1_)
    {
        int i = this.font.getStringPropertyWidth(this.field_224704_i);
        this.font.drawString(p_238086_1_, field_224713_r[this.field_238079_E_ / 10 % field_224713_r.length], (float)(this.width / 2 + i / 2 + 5), 50.0F, 16777215);
    }

    private void func_238088_c_(MatrixStack p_238088_1_)
    {
        double d0 = Math.min((double)this.field_224701_f.field_224978_a / (double)this.field_224701_f.field_224979_b, 1.0D);
        this.field_224705_j = String.format(Locale.ROOT, "%.1f", d0 * 100.0D);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        double d1 = (double)(this.width / 2 - 100);
        double d2 = 0.5D;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
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
        drawCenteredString(p_238088_1_, this.font, this.field_224705_j + " %", this.width / 2, 84, 16777215);
    }

    private void func_238089_d_(MatrixStack p_238089_1_)
    {
        if (this.field_238079_E_ % 20 == 0)
        {
            if (this.field_224715_t != null)
            {
                long i = Util.milliTime() - this.field_224716_u;

                if (i == 0L)
                {
                    i = 1L;
                }

                this.field_224717_v = 1000L * (this.field_224701_f.field_224978_a - this.field_224715_t) / i;
                this.func_238083_a_(p_238089_1_, this.field_224717_v);
            }

            this.field_224715_t = this.field_224701_f.field_224978_a;
            this.field_224716_u = Util.milliTime();
        }
        else
        {
            this.func_238083_a_(p_238089_1_, this.field_224717_v);
        }
    }

    private void func_238083_a_(MatrixStack p_238083_1_, long p_238083_2_)
    {
        if (p_238083_2_ > 0L)
        {
            int i = this.font.getStringWidth(this.field_224705_j);
            String s = "(" + UploadSpeed.func_237684_b_(p_238083_2_) + "/s)";
            this.font.drawString(p_238083_1_, s, (float)(this.width / 2 + i / 2 + 15), 84.0F, 16777215);
        }
    }

    public void tick()
    {
        super.tick();
        ++this.field_238079_E_;

        if (this.field_224704_i != null && this.field_224702_g.tryAcquire(1))
        {
            List<String> list = Lists.newArrayList();
            list.add(this.field_224704_i.getString());

            if (this.field_224705_j != null)
            {
                list.add(this.field_224705_j + "%");
            }

            if (this.field_224703_h != null)
            {
                Stream.of(this.field_224703_h).map(ITextComponent::getString).forEach(list::add);
            }

            RealmsNarratorHelper.func_239550_a_(String.join(System.lineSeparator(), list));
        }
    }

    private void func_224682_h()
    {
        this.field_224709_n = true;
        (new Thread(() ->
        {
            File file1 = null;
            RealmsClient realmsclient = RealmsClient.func_224911_a();
            long i = this.field_224699_d;

            try {
                if (field_238081_b_.tryLock(1L, TimeUnit.SECONDS))
                {
                    UploadInfo uploadinfo = null;

                    for (int j = 0; j < 20; ++j)
                    {
                        try
                        {
                            if (this.field_224706_k)
                            {
                                this.func_224676_i();
                                return;
                            }

                            uploadinfo = realmsclient.func_224934_h(i, UploadTokenCache.func_225235_a(i));

                            if (uploadinfo != null)
                            {
                                break;
                            }
                        }
                        catch (RetryCallException retrycallexception)
                        {
                            Thread.sleep((long)(retrycallexception.field_224985_e * 1000));
                        }
                    }

                    if (uploadinfo == null)
                    {
                        this.field_224704_i = new TranslationTextComponent("mco.upload.close.failure");
                        return;
                    }

                    UploadTokenCache.func_225234_a(i, uploadinfo.func_230795_a_());

                    if (!uploadinfo.func_230799_c_())
                    {
                        this.field_224704_i = new TranslationTextComponent("mco.upload.close.failure");
                        return;
                    }

                    if (this.field_224706_k)
                    {
                        this.func_224676_i();
                        return;
                    }

                    File file2 = new File(this.mc.gameDir.getAbsolutePath(), "saves");
                    file1 = this.func_224675_b(new File(file2, this.field_224698_c.getFileName()));

                    if (this.field_224706_k)
                    {
                        this.func_224676_i();
                        return;
                    }

                    if (this.func_224692_a(file1))
                    {
                        this.field_224704_i = new TranslationTextComponent("mco.upload.uploading", this.field_224698_c.getDisplayName());
                        FileUpload fileupload = new FileUpload(file1, this.field_224699_d, this.field_224700_e, uploadinfo, this.mc.getSession(), SharedConstants.getVersion().getName(), this.field_224701_f);
                        fileupload.func_224874_a((p_238082_3_) ->
                        {
                            if (p_238082_3_.field_225179_a >= 200 && p_238082_3_.field_225179_a < 300)
                            {
                                this.field_224707_l = true;
                                this.field_224704_i = new TranslationTextComponent("mco.upload.done");
                                this.field_224710_o.setMessage(DialogTexts.GUI_DONE);
                                UploadTokenCache.func_225233_b(i);
                            }
                            else if (p_238082_3_.field_225179_a == 400 && p_238082_3_.field_225180_b != null)
                            {
                                this.func_238085_a_(new TranslationTextComponent("mco.upload.failed", p_238082_3_.field_225180_b));
                            }
                            else {
                                this.func_238085_a_(new TranslationTextComponent("mco.upload.failed", p_238082_3_.field_225179_a));
                            }
                        });

                        while (!fileupload.func_224881_b())
                        {
                            if (this.field_224706_k)
                            {
                                fileupload.func_224878_a();
                                this.func_224676_i();
                                return;
                            }

                            try
                            {
                                Thread.sleep(500L);
                            }
                            catch (InterruptedException interruptedexception)
                            {
                                field_224696_a.error("Failed to check Realms file upload status");
                            }
                        }

                        return;
                    }

                    long k = file1.length();
                    UploadSpeed uploadspeed = UploadSpeed.func_237682_a_(k);
                    UploadSpeed uploadspeed1 = UploadSpeed.func_237682_a_(5368709120L);

                    if (UploadSpeed.func_237685_b_(k, uploadspeed).equals(UploadSpeed.func_237685_b_(5368709120L, uploadspeed1)) && uploadspeed != UploadSpeed.B)
                    {
                        UploadSpeed uploadspeed2 = UploadSpeed.values()[uploadspeed.ordinal() - 1];
                        this.func_238085_a_(new TranslationTextComponent("mco.upload.size.failure.line1", this.field_224698_c.getDisplayName()), new TranslationTextComponent("mco.upload.size.failure.line2", UploadSpeed.func_237685_b_(k, uploadspeed2), UploadSpeed.func_237685_b_(5368709120L, uploadspeed2)));
                        return;
                    }

                    this.func_238085_a_(new TranslationTextComponent("mco.upload.size.failure.line1", this.field_224698_c.getDisplayName()), new TranslationTextComponent("mco.upload.size.failure.line2", UploadSpeed.func_237685_b_(k, uploadspeed), UploadSpeed.func_237685_b_(5368709120L, uploadspeed1)));
                    return;
                }

                this.field_224704_i = new TranslationTextComponent("mco.upload.close.failure");
            }
            catch (IOException ioexception)
            {
                this.func_238085_a_(new TranslationTextComponent("mco.upload.failed", ioexception.getMessage()));
                return;
            }
            catch (RealmsServiceException realmsserviceexception)
            {
                this.func_238085_a_(new TranslationTextComponent("mco.upload.failed", realmsserviceexception.toString()));
                return;
            }
            catch (InterruptedException interruptedexception1)
            {
                field_224696_a.error("Could not acquire upload lock");
                return;
            }
            finally {
                this.field_224707_l = true;

                if (field_238081_b_.isHeldByCurrentThread())
                {
                    field_238081_b_.unlock();
                    this.field_224708_m = false;
                    this.field_224710_o.visible = true;
                    this.field_224711_p.visible = false;

                    if (file1 != null)
                    {
                        field_224696_a.debug("Deleting file " + file1.getAbsolutePath());
                        file1.delete();
                    }
                }

                return;
            }
        })).start();
    }

    private void func_238085_a_(ITextComponent... p_238085_1_)
    {
        this.field_224703_h = p_238085_1_;
    }

    private void func_224676_i()
    {
        this.field_224704_i = new TranslationTextComponent("mco.upload.cancelled");
        field_224696_a.debug("Upload was cancelled");
    }

    private boolean func_224692_a(File p_224692_1_)
    {
        return p_224692_1_.length() < 5368709120L;
    }

    private File func_224675_b(File p_224675_1_) throws IOException
    {
        TarArchiveOutputStream tararchiveoutputstream = null;
        File file2;

        try
        {
            File file1 = File.createTempFile("realms-upload-file", ".tar.gz");
            tararchiveoutputstream = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(file1)));
            tararchiveoutputstream.setLongFileMode(3);
            this.func_224669_a(tararchiveoutputstream, p_224675_1_.getAbsolutePath(), "world", true);
            tararchiveoutputstream.finish();
            file2 = file1;
        }
        finally
        {
            if (tararchiveoutputstream != null)
            {
                tararchiveoutputstream.close();
            }
        }

        return file2;
    }

    private void func_224669_a(TarArchiveOutputStream p_224669_1_, String p_224669_2_, String p_224669_3_, boolean p_224669_4_) throws IOException
    {
        if (!this.field_224706_k)
        {
            File file1 = new File(p_224669_2_);
            String s = p_224669_4_ ? p_224669_3_ : p_224669_3_ + file1.getName();
            TarArchiveEntry tararchiveentry = new TarArchiveEntry(file1, s);
            p_224669_1_.putArchiveEntry(tararchiveentry);

            if (file1.isFile())
            {
                IOUtils.copy(new FileInputStream(file1), p_224669_1_);
                p_224669_1_.closeArchiveEntry();
            }
            else
            {
                p_224669_1_.closeArchiveEntry();
                File[] afile = file1.listFiles();

                if (afile != null)
                {
                    for (File file2 : afile)
                    {
                        this.func_224669_a(p_224669_1_, file2.getAbsolutePath(), s + "/", false);
                    }
                }
            }
        }
    }
}
