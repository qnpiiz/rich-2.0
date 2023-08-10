package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.optifine.Config;
import net.optifine.http.HttpPipeline;
import net.optifine.http.HttpRequest;
import net.optifine.http.HttpResponse;
import net.optifine.player.CapeImageBuffer;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadingTexture extends SimpleTexture
{
    private static final Logger LOGGER = LogManager.getLogger();
    @Nullable
    private final File cacheFile;
    private final String imageUrl;
    private final boolean legacySkin;
    @Nullable
    private final Runnable processTask;
    @Nullable
    private CompletableFuture<?> future;
    private boolean textureUploaded;
    public Boolean imageFound = null;
    public boolean pipeline = false;
    private boolean uploadPending = false;

    public DownloadingTexture(@Nullable File cacheFileIn, String imageUrlIn, ResourceLocation textureResourceLocation, boolean legacySkinIn, @Nullable Runnable processTaskIn)
    {
        super(textureResourceLocation);
        this.cacheFile = cacheFileIn;
        this.imageUrl = imageUrlIn;
        this.legacySkin = legacySkinIn;
        this.processTask = processTaskIn;
    }

    private void setImage(NativeImage nativeImageIn)
    {
        if (this.processTask instanceof CapeImageBuffer)
        {
            CapeImageBuffer capeimagebuffer = (CapeImageBuffer)this.processTask;
            nativeImageIn = capeimagebuffer.parseUserSkin(nativeImageIn);
            capeimagebuffer.skinAvailable();
        }

        this.setImageImpl(nativeImageIn);
    }

    private void setImageImpl(NativeImage p_setImageImpl_1_)
    {
        if (this.processTask != null)
        {
            this.processTask.run();
        }

        Minecraft.getInstance().execute(() ->
        {
            this.textureUploaded = true;

            if (!RenderSystem.isOnRenderThread())
            {
                RenderSystem.recordRenderCall(() ->
                {
                    this.upload(p_setImageImpl_1_);
                });
            }
            else {
                this.upload(p_setImageImpl_1_);
            }
        });
    }

    private void upload(NativeImage imageIn)
    {
        TextureUtil.prepareImage(this.getGlTextureId(), imageIn.getWidth(), imageIn.getHeight());
        imageIn.uploadTextureSub(0, 0, 0, true);
        this.imageFound = imageIn != null;
    }

    public void loadTexture(IResourceManager manager) throws IOException
    {
        Minecraft.getInstance().execute(() ->
        {
            if (!this.textureUploaded)
            {
                try
                {
                    super.loadTexture(manager);
                }
                catch (IOException ioexception)
                {
                    LOGGER.warn("Failed to load texture: {}", this.textureLocation, ioexception);
                }

                this.textureUploaded = true;
            }
        });

        if (this.future == null)
        {
            NativeImage nativeimage;

            if (this.cacheFile != null && this.cacheFile.isFile())
            {
                LOGGER.debug("Loading http texture from local cache ({})", (Object)this.cacheFile);
                FileInputStream fileinputstream = new FileInputStream(this.cacheFile);
                nativeimage = this.loadTexture(fileinputstream);
            }
            else
            {
                nativeimage = null;
            }

            if (nativeimage != null)
            {
                this.setImage(nativeimage);
                this.loadingFinished();
            }
            else
            {
                this.future = CompletableFuture.runAsync(() ->
                {
                    HttpURLConnection httpurlconnection = null;
                    LOGGER.debug("Downloading http texture from {} to {}", this.imageUrl, this.cacheFile);

                    if (this.shouldPipeline())
                    {
                        this.loadPipelined();
                    }
                    else {
                        try {
                            httpurlconnection = (HttpURLConnection)(new URL(this.imageUrl)).openConnection(Minecraft.getInstance().getProxy());
                            httpurlconnection.setDoInput(true);
                            httpurlconnection.setDoOutput(false);
                            httpurlconnection.connect();

                            if (httpurlconnection.getResponseCode() / 100 != 2)
                            {
                                if (httpurlconnection.getErrorStream() != null)
                                {
                                    Config.readAll(httpurlconnection.getErrorStream());
                                }

                                return;
                            }

                            InputStream inputstream;

                            if (this.cacheFile != null)
                            {
                                FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(), this.cacheFile);
                                inputstream = new FileInputStream(this.cacheFile);
                            }
                            else {
                                inputstream = httpurlconnection.getInputStream();
                            }

                            Minecraft.getInstance().execute(() -> {
                                NativeImage nativeimage1 = this.loadTexture(inputstream);

                                if (nativeimage1 != null)
                                {
                                    this.setImage(nativeimage1);
                                    this.loadingFinished();
                                }
                            });
                            this.uploadPending = true;
                        }
                        catch (Exception exception1)
                        {
                            LOGGER.error("Couldn't download http texture", (Throwable)exception1);
                            return;
                        }
                        finally {
                            if (httpurlconnection != null)
                            {
                                httpurlconnection.disconnect();
                            }

                            this.loadingFinished();
                        }
                    }
                }, this.getExecutor());
            }
        }
    }

    @Nullable
    private NativeImage loadTexture(InputStream inputStreamIn)
    {
        NativeImage nativeimage = null;

        try
        {
            nativeimage = NativeImage.read(inputStreamIn);

            if (this.legacySkin)
            {
                nativeimage = processLegacySkin(nativeimage);
            }
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("Error while loading the skin texture", (Throwable)ioexception);
        }

        return nativeimage;
    }

    private boolean shouldPipeline()
    {
        if (!this.pipeline)
        {
            return false;
        }
        else
        {
            Proxy proxy = Minecraft.getInstance().getProxy();

            if (proxy.type() != Type.DIRECT && proxy.type() != Type.SOCKS)
            {
                return false;
            }
            else
            {
                return this.imageUrl.startsWith("http://");
            }
        }
    }

    private void loadPipelined()
    {
        try
        {
            HttpRequest httprequest = HttpPipeline.makeRequest(this.imageUrl, Minecraft.getInstance().getProxy());
            HttpResponse httpresponse = HttpPipeline.executeRequest(httprequest);

            if (httpresponse.getStatus() / 100 != 2)
            {
                return;
            }

            byte[] abyte = httpresponse.getBody();
            ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte);
            NativeImage nativeimage;

            if (this.cacheFile != null)
            {
                FileUtils.copyInputStreamToFile(bytearrayinputstream, this.cacheFile);
                nativeimage = NativeImage.read(new FileInputStream(this.cacheFile));
            }
            else
            {
                nativeimage = NativeImage.read(bytearrayinputstream);
            }

            this.setImage(nativeimage);
            this.uploadPending = true;
        }
        catch (Exception exception)
        {
            LOGGER.error("Couldn't download http texture: " + exception.getClass().getName() + ": " + exception.getMessage());
            return;
        }
        finally
        {
            this.loadingFinished();
        }
    }

    private void loadingFinished()
    {
        if (!this.uploadPending)
        {
            if (this.processTask instanceof CapeImageBuffer)
            {
                CapeImageBuffer capeimagebuffer = (CapeImageBuffer)this.processTask;
                capeimagebuffer.cleanup();
            }
        }
    }

    public Runnable getProcessTask()
    {
        return this.processTask;
    }

    private Executor getExecutor()
    {
        return (Executor)(this.imageUrl.startsWith("http://s.optifine.net") ? Util.getCapeExecutor() : Util.getServerExecutor());
    }

    private static NativeImage processLegacySkin(NativeImage nativeImageIn)
    {
        boolean flag = nativeImageIn.getHeight() == 32;

        if (flag)
        {
            NativeImage nativeimage = new NativeImage(64, 64, true);
            nativeimage.copyImageData(nativeImageIn);
            nativeImageIn.close();
            nativeImageIn = nativeimage;
            nativeimage.fillAreaRGBA(0, 32, 64, 32, 0);
            nativeimage.copyAreaRGBA(4, 16, 16, 32, 4, 4, true, false);
            nativeimage.copyAreaRGBA(8, 16, 16, 32, 4, 4, true, false);
            nativeimage.copyAreaRGBA(0, 20, 24, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(4, 20, 16, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(8, 20, 8, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(12, 20, 16, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(44, 16, -8, 32, 4, 4, true, false);
            nativeimage.copyAreaRGBA(48, 16, -8, 32, 4, 4, true, false);
            nativeimage.copyAreaRGBA(40, 20, 0, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(44, 20, -8, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(48, 20, -16, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(52, 20, -8, 32, 4, 12, true, false);
        }

        setAreaOpaque(nativeImageIn, 0, 0, 32, 16);

        if (flag)
        {
            setAreaTransparent(nativeImageIn, 32, 0, 64, 32);
        }

        setAreaOpaque(nativeImageIn, 0, 16, 64, 32);
        setAreaOpaque(nativeImageIn, 16, 48, 48, 64);
        return nativeImageIn;
    }

    private static void setAreaTransparent(NativeImage image, int x, int y, int width, int height)
    {
        for (int i = x; i < width; ++i)
        {
            for (int j = y; j < height; ++j)
            {
                int k = image.getPixelRGBA(i, j);

                if ((k >> 24 & 255) < 128)
                {
                    return;
                }
            }
        }

        for (int l = x; l < width; ++l)
        {
            for (int i1 = y; i1 < height; ++i1)
            {
                image.setPixelRGBA(l, i1, image.getPixelRGBA(l, i1) & 16777215);
            }
        }
    }

    private static void setAreaOpaque(NativeImage image, int x, int y, int width, int height)
    {
        for (int i = x; i < width; ++i)
        {
            for (int j = y; j < height; ++j)
            {
                image.setPixelRGBA(i, j, image.getPixelRGBA(i, j) | -16777216);
            }
        }
    }
}
