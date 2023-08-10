package net.optifine.shaders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IResourceManager;
import org.apache.commons.io.IOUtils;

public class SimpleShaderTexture extends Texture
{
    private String texturePath;

    public SimpleShaderTexture(String texturePath)
    {
        this.texturePath = texturePath;
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        this.deleteGlTexture();
        InputStream inputstream = Shaders.getShaderPackResourceStream(this.texturePath);

        if (inputstream == null)
        {
            throw new FileNotFoundException("Shader texture not found: " + this.texturePath);
        }
        else
        {
            try
            {
                NativeImage nativeimage = NativeImage.read(inputstream);
                TextureMetadataSection texturemetadatasection = loadTextureMetadataSection(this.texturePath, new TextureMetadataSection(false, false));
                TextureUtil.prepareImage(this.getGlTextureId(), nativeimage.getWidth(), nativeimage.getHeight());
                nativeimage.uploadTextureSub(0, 0, 0, 0, 0, nativeimage.getWidth(), nativeimage.getHeight(), texturemetadatasection.getTextureBlur(), texturemetadatasection.getTextureClamp(), false, true);
            }
            finally
            {
                IOUtils.closeQuietly(inputstream);
            }
        }
    }

    public static TextureMetadataSection loadTextureMetadataSection(String texturePath, TextureMetadataSection def)
    {
        String s = texturePath + ".mcmeta";
        String s1 = "texture";
        InputStream inputstream = Shaders.getShaderPackResourceStream(s);

        if (inputstream != null)
        {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
            TextureMetadataSection texturemetadatasection1;

            try
            {
                JsonObject jsonobject = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
                JsonObject jsonobject1 = jsonobject.getAsJsonObject(s1);

                if (jsonobject1 == null)
                {
                    return def;
                }

                TextureMetadataSection texturemetadatasection = TextureMetadataSection.SERIALIZER.deserialize(jsonobject1);

                if (texturemetadatasection == null)
                {
                    return def;
                }

                texturemetadatasection1 = texturemetadatasection;
            }
            catch (RuntimeException runtimeexception)
            {
                SMCLog.warning("Error reading metadata: " + s);
                SMCLog.warning("" + runtimeexception.getClass().getName() + ": " + runtimeexception.getMessage());
                return def;
            }
            finally
            {
                IOUtils.closeQuietly((Reader)bufferedreader);
                IOUtils.closeQuietly(inputstream);
            }

            return texturemetadatasection1;
        }
        else
        {
            return def;
        }
    }
}
