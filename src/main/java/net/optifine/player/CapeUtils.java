package net.optifine.player;

import java.io.File;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.util.TextureUtils;

public class CapeUtils
{
    private static final Pattern PATTERN_USERNAME = Pattern.compile("[a-zA-Z0-9_]+");

    public static void downloadCape(AbstractClientPlayerEntity player)
    {
        String s = player.getNameClear();

        if (s != null && !s.isEmpty() && !s.contains("\u0000") && PATTERN_USERNAME.matcher(s).matches())
        {
            String s1 = "http://s.optifine.net/capes/" + s + ".png";
            ResourceLocation resourcelocation = new ResourceLocation("capeof/" + s);
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            Texture texture = texturemanager.getTexture(resourcelocation);

            if (texture != null && texture instanceof DownloadingTexture)
            {
                DownloadingTexture downloadingtexture = (DownloadingTexture)texture;

                if (downloadingtexture.imageFound != null)
                {
                    if (downloadingtexture.imageFound)
                    {
                        player.setLocationOfCape(resourcelocation);

                        if (downloadingtexture.getProcessTask() instanceof CapeImageBuffer)
                        {
                            CapeImageBuffer capeimagebuffer1 = (CapeImageBuffer)downloadingtexture.getProcessTask();
                            player.setElytraOfCape(capeimagebuffer1.isElytraOfCape());
                        }
                    }

                    return;
                }
            }

            CapeImageBuffer capeimagebuffer = new CapeImageBuffer(player, resourcelocation);
            ResourceLocation resourcelocation1 = TextureUtils.LOCATION_TEXTURE_EMPTY;
            DownloadingTexture downloadingtexture1 = new DownloadingTexture((File)null, s1, resourcelocation1, false, capeimagebuffer);
            downloadingtexture1.pipeline = true;
            texturemanager.loadTexture(resourcelocation, downloadingtexture1);
        }
    }

    public static NativeImage parseCape(NativeImage img)
    {
        int i = 64;
        int j = 32;
        int k = img.getWidth();

        for (int l = img.getHeight(); i < k || j < l; j *= 2)
        {
            i *= 2;
        }

        NativeImage nativeimage = new NativeImage(i, j, true);
        nativeimage.copyImageData(img);
        img.close();
        return nativeimage;
    }

    public static boolean isElytraCape(NativeImage imageRaw, NativeImage imageFixed)
    {
        return imageRaw.getWidth() > imageFixed.getHeight();
    }

    public static void reloadCape(AbstractClientPlayerEntity player)
    {
        String s = player.getNameClear();
        ResourceLocation resourcelocation = new ResourceLocation("capeof/" + s);
        TextureManager texturemanager = Config.getTextureManager();
        Texture texture = texturemanager.getTexture(resourcelocation);

        if (texture instanceof SimpleTexture)
        {
            SimpleTexture simpletexture = (SimpleTexture)texture;
            simpletexture.deleteGlTexture();
            texturemanager.deleteTexture(resourcelocation);
        }

        player.setLocationOfCape((ResourceLocation)null);
        player.setElytraOfCape(false);
        downloadCape(player);
    }
}
