package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileDownload
{
    private static final Logger LOGGER = LogManager.getLogger();
    private volatile boolean field_224844_b;
    private volatile boolean field_224845_c;
    private volatile boolean field_224846_d;
    private volatile boolean field_224847_e;
    private volatile File field_224848_f;
    private volatile File field_224849_g;
    private volatile HttpGet field_224850_h;
    private Thread field_224851_i;
    private final RequestConfig field_224852_j = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
    private static final String[] field_224853_k = new String[] {"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    public long func_224827_a(String p_224827_1_)
    {
        CloseableHttpClient closeablehttpclient = null;
        HttpGet httpget = null;
        long i;

        try
        {
            httpget = new HttpGet(p_224827_1_);
            closeablehttpclient = HttpClientBuilder.create().setDefaultRequestConfig(this.field_224852_j).build();
            CloseableHttpResponse closeablehttpresponse = closeablehttpclient.execute(httpget);
            return Long.parseLong(closeablehttpresponse.getFirstHeader("Content-Length").getValue());
        }
        catch (Throwable throwable)
        {
            LOGGER.error("Unable to get content length for download");
            i = 0L;
        }
        finally
        {
            if (httpget != null)
            {
                httpget.releaseConnection();
            }

            if (closeablehttpclient != null)
            {
                try
                {
                    closeablehttpclient.close();
                }
                catch (IOException ioexception)
                {
                    LOGGER.error("Could not close http client", (Throwable)ioexception);
                }
            }
        }

        return i;
    }

    public void func_237688_a_(WorldDownload p_237688_1_, String p_237688_2_, RealmsDownloadLatestWorldScreen.DownloadStatus p_237688_3_, SaveFormat p_237688_4_)
    {
        if (this.field_224851_i == null)
        {
            this.field_224851_i = new Thread(() ->
            {
                CloseableHttpClient closeablehttpclient = null;

                try {
                    this.field_224848_f = File.createTempFile("backup", ".tar.gz");
                    this.field_224850_h = new HttpGet(p_237688_1_.field_230643_a_);
                    closeablehttpclient = HttpClientBuilder.create().setDefaultRequestConfig(this.field_224852_j).build();
                    HttpResponse httpresponse = closeablehttpclient.execute(this.field_224850_h);
                    p_237688_3_.field_225140_b = Long.parseLong(httpresponse.getFirstHeader("Content-Length").getValue());

                    if (httpresponse.getStatusLine().getStatusCode() == 200)
                    {
                        OutputStream outputstream = new FileOutputStream(this.field_224848_f);
                        FileDownload.ProgressListener filedownload$progresslistener = new FileDownload.ProgressListener(p_237688_2_.trim(), this.field_224848_f, p_237688_4_, p_237688_3_);
                        FileDownload.DownloadCountingOutputStream filedownload$downloadcountingoutputstream = new FileDownload.DownloadCountingOutputStream(outputstream);
                        filedownload$downloadcountingoutputstream.func_224804_a(filedownload$progresslistener);
                        IOUtils.copy(httpresponse.getEntity().getContent(), filedownload$downloadcountingoutputstream);
                        return;
                    }

                    this.field_224846_d = true;
                    this.field_224850_h.abort();
                }
                catch (Exception exception1)
                {
                    LOGGER.error("Caught exception while downloading: " + exception1.getMessage());
                    this.field_224846_d = true;
                    return;
                }
                finally {
                    this.field_224850_h.releaseConnection();

                    if (this.field_224848_f != null)
                    {
                        this.field_224848_f.delete();
                    }

                    if (!this.field_224846_d)
                    {
                        if (!p_237688_1_.field_230644_b_.isEmpty() && !p_237688_1_.field_230645_c_.isEmpty())
                        {
                            try
                            {
                                this.field_224848_f = File.createTempFile("resources", ".tar.gz");
                                this.field_224850_h = new HttpGet(p_237688_1_.field_230644_b_);
                                HttpResponse httpresponse1 = closeablehttpclient.execute(this.field_224850_h);
                                p_237688_3_.field_225140_b = Long.parseLong(httpresponse1.getFirstHeader("Content-Length").getValue());

                                if (httpresponse1.getStatusLine().getStatusCode() != 200)
                                {
                                    this.field_224846_d = true;
                                    this.field_224850_h.abort();
                                    return;
                                }

                                OutputStream outputstream1 = new FileOutputStream(this.field_224848_f);
                                FileDownload.ResourcePackProgressListener filedownload$resourcepackprogresslistener = new FileDownload.ResourcePackProgressListener(this.field_224848_f, p_237688_3_, p_237688_1_);
                                FileDownload.DownloadCountingOutputStream filedownload$downloadcountingoutputstream1 = new FileDownload.DownloadCountingOutputStream(outputstream1);
                                filedownload$downloadcountingoutputstream1.func_224804_a(filedownload$resourcepackprogresslistener);
                                IOUtils.copy(httpresponse1.getEntity().getContent(), filedownload$downloadcountingoutputstream1);
                            }
                            catch (Exception exception)
                            {
                                LOGGER.error("Caught exception while downloading: " + exception.getMessage());
                                this.field_224846_d = true;
                            }
                            finally
                            {
                                this.field_224850_h.releaseConnection();

                                if (this.field_224848_f != null)
                                {
                                    this.field_224848_f.delete();
                                }
                            }
                        }
                        else
                        {
                            this.field_224845_c = true;
                        }
                    }

                    if (closeablehttpclient != null)
                    {
                        try
                        {
                            closeablehttpclient.close();
                        }
                        catch (IOException ioexception)
                        {
                            LOGGER.error("Failed to close Realms download client");
                        }
                    }
                }
            });
            this.field_224851_i.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
            this.field_224851_i.start();
        }
    }

    public void func_224834_a()
    {
        if (this.field_224850_h != null)
        {
            this.field_224850_h.abort();
        }

        if (this.field_224848_f != null)
        {
            this.field_224848_f.delete();
        }

        this.field_224844_b = true;
    }

    public boolean func_224835_b()
    {
        return this.field_224845_c;
    }

    public boolean func_224836_c()
    {
        return this.field_224846_d;
    }

    public boolean func_224837_d()
    {
        return this.field_224847_e;
    }

    public static String func_224828_b(String p_224828_0_)
    {
        p_224828_0_ = p_224828_0_.replaceAll("[\\./\"]", "_");

        for (String s : field_224853_k)
        {
            if (p_224828_0_.equalsIgnoreCase(s))
            {
                p_224828_0_ = "_" + p_224828_0_ + "_";
            }
        }

        return p_224828_0_;
    }

    private void func_237690_a_(String p_237690_1_, File p_237690_2_, SaveFormat p_237690_3_) throws IOException
    {
        Pattern pattern = Pattern.compile(".*-([0-9]+)$");
        int i = 1;

        for (char c0 : SharedConstants.ILLEGAL_FILE_CHARACTERS)
        {
            p_237690_1_ = p_237690_1_.replace(c0, '_');
        }

        if (StringUtils.isEmpty(p_237690_1_))
        {
            p_237690_1_ = "Realm";
        }

        p_237690_1_ = func_224828_b(p_237690_1_);

        try
        {
            for (WorldSummary worldsummary : p_237690_3_.getSaveList())
            {
                if (worldsummary.getFileName().toLowerCase(Locale.ROOT).startsWith(p_237690_1_.toLowerCase(Locale.ROOT)))
                {
                    Matcher matcher = pattern.matcher(worldsummary.getFileName());

                    if (matcher.matches())
                    {
                        if (Integer.valueOf(matcher.group(1)) > i)
                        {
                            i = Integer.valueOf(matcher.group(1));
                        }
                    }
                    else
                    {
                        ++i;
                    }
                }
            }
        }
        catch (Exception exception1)
        {
            LOGGER.error("Error getting level list", (Throwable)exception1);
            this.field_224846_d = true;
            return;
        }

        String s;

        if (p_237690_3_.isNewLevelIdAcceptable(p_237690_1_) && i <= 1)
        {
            s = p_237690_1_;
        }
        else
        {
            s = p_237690_1_ + (i == 1 ? "" : "-" + i);

            if (!p_237690_3_.isNewLevelIdAcceptable(s))
            {
                boolean flag = false;

                while (!flag)
                {
                    ++i;
                    s = p_237690_1_ + (i == 1 ? "" : "-" + i);

                    if (p_237690_3_.isNewLevelIdAcceptable(s))
                    {
                        flag = true;
                    }
                }
            }
        }

        TarArchiveInputStream tararchiveinputstream = null;
        File file1 = new File(Minecraft.getInstance().gameDir.getAbsolutePath(), "saves");

        try
        {
            file1.mkdir();
            tararchiveinputstream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(p_237690_2_))));

            for (TarArchiveEntry tararchiveentry = tararchiveinputstream.getNextTarEntry(); tararchiveentry != null; tararchiveentry = tararchiveinputstream.getNextTarEntry())
            {
                File file2 = new File(file1, tararchiveentry.getName().replace("world", s));

                if (tararchiveentry.isDirectory())
                {
                    file2.mkdirs();
                }
                else
                {
                    file2.createNewFile();

                    try (FileOutputStream fileoutputstream = new FileOutputStream(file2))
                    {
                        IOUtils.copy(tararchiveinputstream, fileoutputstream);
                    }
                }
            }
        }
        catch (Exception exception)
        {
            LOGGER.error("Error extracting world", (Throwable)exception);
            this.field_224846_d = true;
        }
        finally
        {
            if (tararchiveinputstream != null)
            {
                tararchiveinputstream.close();
            }

            if (p_237690_2_ != null)
            {
                p_237690_2_.delete();
            }

            try (SaveFormat.LevelSave saveformat$levelsave = p_237690_3_.getLevelSave(s))
            {
                saveformat$levelsave.updateSaveName(s.trim());
                Path path = saveformat$levelsave.resolveFilePath(FolderName.LEVEL_DAT);
                func_237689_a_(path.toFile());
            }
            catch (IOException ioexception)
            {
                LOGGER.error("Failed to rename unpacked realms level {}", s, ioexception);
            }

            this.field_224849_g = new File(file1, s + File.separator + "resources.zip");
        }
    }

    private static void func_237689_a_(File p_237689_0_)
    {
        if (p_237689_0_.exists())
        {
            try
            {
                CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(p_237689_0_);
                CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
                compoundnbt1.remove("Player");
                CompressedStreamTools.writeCompressed(compoundnbt, p_237689_0_);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }

    class DownloadCountingOutputStream extends CountingOutputStream
    {
        private ActionListener field_224806_b;

        public DownloadCountingOutputStream(OutputStream p_i51649_2_)
        {
            super(p_i51649_2_);
        }

        public void func_224804_a(ActionListener p_224804_1_)
        {
            this.field_224806_b = p_224804_1_;
        }

        protected void afterWrite(int p_afterWrite_1_) throws IOException
        {
            super.afterWrite(p_afterWrite_1_);

            if (this.field_224806_b != null)
            {
                this.field_224806_b.actionPerformed(new ActionEvent(this, 0, (String)null));
            }
        }
    }

    class ProgressListener implements ActionListener
    {
        private final String field_224813_b;
        private final File field_224814_c;
        private final SaveFormat field_224815_d;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus field_224816_e;

        private ProgressListener(String p_i232192_2_, File p_i232192_3_, SaveFormat p_i232192_4_, RealmsDownloadLatestWorldScreen.DownloadStatus p_i232192_5_)
        {
            this.field_224813_b = p_i232192_2_;
            this.field_224814_c = p_i232192_3_;
            this.field_224815_d = p_i232192_4_;
            this.field_224816_e = p_i232192_5_;
        }

        public void actionPerformed(ActionEvent p_actionPerformed_1_)
        {
            this.field_224816_e.field_225139_a = ((FileDownload.DownloadCountingOutputStream)p_actionPerformed_1_.getSource()).getByteCount();

            if (this.field_224816_e.field_225139_a >= this.field_224816_e.field_225140_b && !FileDownload.this.field_224844_b && !FileDownload.this.field_224846_d)
            {
                try
                {
                    FileDownload.this.field_224847_e = true;
                    FileDownload.this.func_237690_a_(this.field_224813_b, this.field_224814_c, this.field_224815_d);
                }
                catch (IOException ioexception)
                {
                    FileDownload.LOGGER.error("Error extracting archive", (Throwable)ioexception);
                    FileDownload.this.field_224846_d = true;
                }
            }
        }
    }

    class ResourcePackProgressListener implements ActionListener
    {
        private final File field_224819_b;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus field_224820_c;
        private final WorldDownload field_224821_d;

        private ResourcePackProgressListener(File p_i51645_2_, RealmsDownloadLatestWorldScreen.DownloadStatus p_i51645_3_, WorldDownload p_i51645_4_)
        {
            this.field_224819_b = p_i51645_2_;
            this.field_224820_c = p_i51645_3_;
            this.field_224821_d = p_i51645_4_;
        }

        public void actionPerformed(ActionEvent p_actionPerformed_1_)
        {
            this.field_224820_c.field_225139_a = ((FileDownload.DownloadCountingOutputStream)p_actionPerformed_1_.getSource()).getByteCount();

            if (this.field_224820_c.field_225139_a >= this.field_224820_c.field_225140_b && !FileDownload.this.field_224844_b)
            {
                try
                {
                    String s = Hashing.sha1().hashBytes(Files.toByteArray(this.field_224819_b)).toString();

                    if (s.equals(this.field_224821_d.field_230645_c_))
                    {
                        FileUtils.copyFile(this.field_224819_b, FileDownload.this.field_224849_g);
                        FileDownload.this.field_224845_c = true;
                    }
                    else
                    {
                        FileDownload.LOGGER.error("Resourcepack had wrong hash (expected " + this.field_224821_d.field_230645_c_ + ", found " + s + "). Deleting it.");
                        FileUtils.deleteQuietly(this.field_224819_b);
                        FileDownload.this.field_224846_d = true;
                    }
                }
                catch (IOException ioexception)
                {
                    FileDownload.LOGGER.error("Error copying resourcepack file", (Object)ioexception.getMessage());
                    FileDownload.this.field_224846_d = true;
                }
            }
        }
    }
}
