package net.optifine.shaders;

import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureUtil;

public class CustomTexture implements ICustomTexture
{
    private int textureUnit = -1;
    private String path = null;
    private Texture texture = null;

    public CustomTexture(int textureUnit, String path, Texture texture)
    {
        this.textureUnit = textureUnit;
        this.path = path;
        this.texture = texture;
    }

    public int getTextureUnit()
    {
        return this.textureUnit;
    }

    public String getPath()
    {
        return this.path;
    }

    public Texture getTexture()
    {
        return this.texture;
    }

    public int getTextureId()
    {
        return this.texture.getGlTextureId();
    }

    public void deleteTexture()
    {
        TextureUtil.releaseTextureId(this.texture.getGlTextureId());
    }

    public String toString()
    {
        return "textureUnit: " + this.textureUnit + ", path: " + this.path + ", glTextureId: " + this.getTextureId();
    }
}
